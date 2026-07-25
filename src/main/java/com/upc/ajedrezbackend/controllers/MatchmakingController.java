package com.upc.ajedrezbackend.controllers;

import com.upc.ajedrezbackend.dtos.JugadorMatchDTO;
import com.upc.ajedrezbackend.repositories.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class MatchmakingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private final List<JugadorMatchDTO> colaDeEspera = new CopyOnWriteArrayList<>();
    private final Map<Long, PartidaPendiente> partidasPendientes = new ConcurrentHashMap<>();
    private final Map<String, List<Long>> listosPorSala = new ConcurrentHashMap<>();
    private final Map<String, SalaEnMemoria> salasDisponibles = new ConcurrentHashMap<>();
    @Autowired
    private JugadorRepository jugadorRepository;

    class PartidaPendiente {
        Long jugador1Id;
        Long jugador2Id;
        boolean jugador1Acepto = false;
        boolean jugador2Acepto = false;
        boolean jugador1Listo = false;
        boolean jugador2Listo = false;

        public PartidaPendiente(Long j1, Long j2) {
            this.jugador1Id = j1;
            this.jugador2Id = j2;
        }
    }

    @MessageMapping("/buscarPartida")
    public void buscarPartida(JugadorMatchDTO nuevoJugador) {
        Optional<JugadorMatchDTO> posibleOponente = colaDeEspera.stream()
                .filter(j -> Math.abs(j.getElo() - nuevoJugador.getElo()) <= 35
                        && !j.getIdJugador().equals(nuevoJugador.getIdJugador()))
                .findFirst();

        if (posibleOponente.isPresent()) {
            JugadorMatchDTO oponente = posibleOponente.get();
            colaDeEspera.remove(oponente);
            PartidaPendiente partida = new PartidaPendiente(nuevoJugador.getIdJugador(), oponente.getIdJugador());
            partidasPendientes.put(nuevoJugador.getIdJugador(), partida);
            partidasPendientes.put(oponente.getIdJugador(), partida);

            messagingTemplate.convertAndSend("/queue/match/" + nuevoJugador.getIdJugador(),
                    "{\"tipo\": \"MATCH_FOUND\", \"rivalElo\": " + oponente.getElo() + "}");

            messagingTemplate.convertAndSend("/queue/match/" + oponente.getIdJugador(),
                    "{\"tipo\": \"MATCH_FOUND\", \"rivalElo\": " + nuevoJugador.getElo() + "}");

        } else {
            colaDeEspera.add(nuevoJugador);
        }
    }

    @MessageMapping("/cancelarBusqueda")
    public void cancelarBusqueda(JugadorMatchDTO jugador) {
        colaDeEspera.removeIf(j -> j.getIdJugador().equals(jugador.getIdJugador()));
    }

    @MessageMapping("/aceptarEnfrentamiento")
    public void aceptarEnfrentamiento(JugadorMatchDTO jugador) {
        PartidaPendiente partida = partidasPendientes.get(jugador.getIdJugador());

        if (partida != null) {
            if (partida.jugador1Id.equals(jugador.getIdJugador())) partida.jugador1Acepto = true;
            if (partida.jugador2Id.equals(jugador.getIdJugador())) partida.jugador2Acepto = true;

            if (partida.jugador1Acepto && partida.jugador2Acepto) {
                String salaId = UUID.randomUUID().toString();
                messagingTemplate.convertAndSend("/queue/match/" + partida.jugador1Id,
                        "{\"tipo\": \"MATCH_START\", \"salaId\": \"" + salaId + "\", \"rivalId\": " + partida.jugador2Id + "}");
                messagingTemplate.convertAndSend("/queue/match/" + partida.jugador2Id,
                        "{\"tipo\": \"MATCH_START\", \"salaId\": \"" + salaId + "\", \"rivalId\": " + partida.jugador1Id + "}");

                partidasPendientes.remove(partida.jugador1Id);
                partidasPendientes.remove(partida.jugador2Id);
            }
        }
    }

    @MessageMapping("/rechazarEnfrentamiento")
    public void rechazarEnfrentamiento(JugadorMatchDTO jugador) {
        PartidaPendiente partida = partidasPendientes.get(jugador.getIdJugador());
        if (partida != null) {
            Long elOtroJugadorId = partida.jugador1Id.equals(jugador.getIdJugador())
                    ? partida.jugador2Id : partida.jugador1Id;
            messagingTemplate.convertAndSend("/queue/match/" + elOtroJugadorId,
                    "{\"tipo\": \"MATCH_CANCELLED\"}");
            partidasPendientes.remove(partida.jugador1Id);
            partidasPendientes.remove(partida.jugador2Id);
        }
    }

    @MessageMapping("/proponerTiempo")
    public void proponerTiempo(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        String tiempo = (String) payload.get("tiempo");
        Long emisorId = Long.valueOf(payload.get("emisorId").toString());
        Long receptorId = Long.valueOf(payload.get("receptorId").toString());
        messagingTemplate.convertAndSend("/queue/match/" + receptorId,
                "{\"tipo\": \"TIEMPO_PROPUESTO\", \"tiempo\": \"" + tiempo + "\", \"emisorId\": " + emisorId + "}");
    }

    @MessageMapping("/responderTiempo")
    public void responderTiempo(Map<String, Object> payload) {
        String respuesta = (String) payload.get("respuesta");
        String tiempo = (String) payload.get("tiempo");
        Long receptorId = Long.valueOf(payload.get("receptorId").toString());
        String salaId = (String) payload.get("salaId");
        SalaEnMemoria sala = (salaId != null) ? salasDisponibles.get(salaId) : null;
        if (sala != null && "ACEPTADO".equals(respuesta)) {
            for (JugadorSalaDTO j : sala.jugadores) {
                messagingTemplate.convertAndSend("/queue/match/" + j.id,
                        "{\"tipo\": \"RESPUESTA_TIEMPO\", \"respuesta\": \"ACEPTADO\", \"tiempo\": \"" + tiempo + "\"}");
            }
        } else {
            messagingTemplate.convertAndSend("/queue/match/" + receptorId,
                    "{\"tipo\": \"RESPUESTA_TIEMPO\", \"respuesta\": \"" + respuesta + "\", \"tiempo\": \"" + tiempo + "\"}");
        }
    }

    @MessageMapping("/jugadorListo")
    public void jugadorListo(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        Long jugadorId = Long.valueOf(payload.get("jugadorId").toString());
        listosPorSala.putIfAbsent(salaId, new CopyOnWriteArrayList<>());
        List<Long> jugadoresListos = listosPorSala.get(salaId);
        if (!jugadoresListos.contains(jugadorId)) {
            jugadoresListos.add(jugadorId);
        }
        if (jugadoresListos.size() == 2) {
            Long jugador1Id = jugadoresListos.get(0);
            Long jugador2Id = jugadoresListos.get(1);
            boolean j1EsBlancas = Math.random() > 0.5;
            SalaEnMemoria sala = (salaId != null) ? salasDisponibles.get(salaId) : null;
            if (sala != null) {
                for (JugadorSalaDTO j : sala.jugadores) {
                    if (j.id.equals(jugador1Id)) {
                        messagingTemplate.convertAndSend("/queue/match/" + j.id,
                                "{\"tipo\": \"JUEGO_INICIADO\", \"color\": \"" + (j1EsBlancas ? "w" : "b") + "\"}");
                    } else if (j.id.equals(jugador2Id)) {
                        messagingTemplate.convertAndSend("/queue/match/" + j.id,
                                "{\"tipo\": \"JUEGO_INICIADO\", \"color\": \"" + (j1EsBlancas ? "b" : "w") + "\"}");
                    } else {
                        messagingTemplate.convertAndSend("/queue/match/" + j.id,
                                "{\"tipo\": \"JUEGO_INICIADO\", \"color\": \"espectador\"}");
                    }
                }
            } else {
                messagingTemplate.convertAndSend("/queue/match/" + jugador1Id,
                        "{\"tipo\": \"JUEGO_INICIADO\", \"color\": \"" + (j1EsBlancas ? "w" : "b") + "\"}");
                messagingTemplate.convertAndSend("/queue/match/" + jugador2Id,
                        "{\"tipo\": \"JUEGO_INICIADO\", \"color\": \"" + (j1EsBlancas ? "b" : "w") + "\"}");
            }
            listosPorSala.remove(salaId);
        }
    }

    @MessageMapping("/proponerInicio")
    public void proponerInicio(Map<String, Object> payload) {
        Long receptorId = Long.valueOf(payload.get("receptorId").toString());
        messagingTemplate.convertAndSend("/queue/match/" + receptorId,
                "{\"tipo\": \"INICIO_PROPUESTO\"}");
    }

    @MessageMapping("/responderInicio")
    public void responderInicio(Map<String, Object> payload) {
        String respuesta = (String) payload.get("respuesta");
        Long receptorId = Long.valueOf(payload.get("receptorId").toString());

        if ("RECHAZADO".equals(respuesta)) {
            messagingTemplate.convertAndSend("/queue/match/" + receptorId,
                    "{\"tipo\": \"INICIO_RECHAZADO\"}");
        }
    }
    @MessageMapping("/moverPieza")
    public void moverPieza(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        Long emisorId = payload.get("emisorId") != null ? Long.valueOf(payload.get("emisorId").toString()) : null;
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        String promotion = (String) payload.get("promotion");
        String msg = "{\"tipo\": \"MOVIMIENTO\", \"from\": \"" + from + "\", \"to\": \"" + to + "\", \"promotion\": \"" + promotion + "\"}";
        SalaEnMemoria sala = (salaId != null) ? salasDisponibles.get(salaId) : null;
        if (sala != null && emisorId != null) {
            for (JugadorSalaDTO j : sala.jugadores) {
                if (!j.id.equals(emisorId)) {
                    messagingTemplate.convertAndSend("/queue/match/" + j.id, msg);
                }
            }
        } else {
            Long receptorId = Long.valueOf(payload.get("receptorId").toString());
            messagingTemplate.convertAndSend("/queue/match/" + receptorId, msg);
        }
    }

    @MessageMapping("/rendirse")
    public void rendirse(Map<String, Object> payload) {
        Long receptorId = Long.valueOf(payload.get("receptorId").toString());
        messagingTemplate.convertAndSend("/queue/match/" + receptorId,
                "{\"tipo\": \"RENDICION\"}");
    }

    @MessageMapping("/finPartida")
    public void finPartida(Map<String, Object> payload) {
        Long ganadorId = payload.get("ganadorId") != null ? Long.valueOf(payload.get("ganadorId").toString()) : null;
        Long perdedorId = payload.get("perdedorId") != null ? Long.valueOf(payload.get("perdedorId").toString()) : null;
        boolean empate = payload.get("empate") != null && (boolean) payload.get("empate");
        String salaIdStr = payload.get("salaId") != null ? (String) payload.get("salaId") : null;
        if (salaIdStr != null && salasDisponibles.containsKey(salaIdStr)) {
            SalaEnMemoria sala = salasDisponibles.get(salaIdStr);
            for(JugadorSalaDTO j : sala.jugadores) {
                messagingTemplate.convertAndSend("/queue/match/" + j.id,
                        "{\"tipo\": \"PARTIDA_PRIVADA_TERMINADA\", \"ganadorId\": " + ganadorId + ", \"empate\": " + empate + "}");
            }
            return;
        }
        double eloGanado = 0;
        double eloPerdido = 0;

        if (empate) {
            if (ganadorId != null) {
                jugadorRepository.findById(ganadorId).ifPresent(j1 -> {
                    j1.setNroEmpates(j1.getNroEmpates() + 1);
                    jugadorRepository.save(j1);
                });
            }
            if (perdedorId != null) {
                jugadorRepository.findById(perdedorId).ifPresent(j2 -> {
                    j2.setNroEmpates(j2.getNroEmpates() + 1);
                    jugadorRepository.save(j2);
                });
            }
        } else {
            eloGanado = (int) (Math.random() * 11) + 10;
            eloPerdido = eloGanado / 2.0;
            final double finalEloGanado = eloGanado;
            final double finalEloPerdido = eloPerdido;
            if (ganadorId != null) {
                jugadorRepository.findById(ganadorId).ifPresent(ganador -> {
                    ganador.setElo(ganador.getElo() + finalEloGanado);
                    ganador.setNroVictorias(ganador.getNroVictorias() + 1);
                    jugadorRepository.save(ganador);
                });
            }
            if (perdedorId != null) {
                jugadorRepository.findById(perdedorId).ifPresent(perdedor -> {
                    double nuevoElo = perdedor.getElo() - finalEloPerdido;
                    perdedor.setElo(Math.max(nuevoElo, 0.0));
                    perdedor.setNroDerrotas(perdedor.getNroDerrotas() + 1);
                    jugadorRepository.save(perdedor);
                });
            }
        }
        if (ganadorId != null) {
            double delta = empate ? 0 : eloGanado;
            messagingTemplate.convertAndSend("/queue/match/" + ganadorId,
                    "{\"tipo\": \"STATS_ACTUALIZADOS\", \"eloDelta\": " + delta + "}");
        }
        if (perdedorId != null) {
            double delta = empate ? 0 : -eloPerdido;
            messagingTemplate.convertAndSend("/queue/match/" + perdedorId,
                    "{\"tipo\": \"STATS_ACTUALIZADOS\", \"eloDelta\": " + delta + "}");
        }
    }
    @MessageMapping("/abandonarSala")
    public void abandonarSala(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        Long emisorId = payload.get("emisorId") != null ? Long.valueOf(payload.get("emisorId").toString()) : null;
        Long receptorId = payload.get("receptorId") != null ? Long.valueOf(payload.get("receptorId").toString()) : null;
        if (salaId != null && salasDisponibles.containsKey(salaId)) {
            SalaEnMemoria sala = salasDisponibles.get(salaId);
            if (emisorId != null) {
                boolean eraJugadorActivo = sala.jugadores.stream()
                        .anyMatch(j -> j.id.equals(emisorId) && j.jugando);
                sala.jugadores.removeIf(j -> j.id.equals(emisorId));
                sala.jugadoresActuales = sala.jugadores.size();
                if (sala.jugadores.isEmpty()) {
                    salasDisponibles.remove(salaId);
                    listosPorSala.remove(salaId);
                } else {
                    if (eraJugadorActivo) {
                        List<JugadorSalaDTO> espectadores = sala.jugadores.stream()
                                .filter(j -> !j.jugando)
                                .collect(java.util.stream.Collectors.toList());
                        if (!espectadores.isEmpty()) {
                            int randomIdx = (int) (Math.random() * espectadores.size());
                            espectadores.get(randomIdx).jugando = true;
                        }
                    }
                    if (sala.jugadores.stream().noneMatch(j -> j.esLider)) {
                        int randomIdx = (int) (Math.random() * sala.jugadores.size());
                        sala.jugadores.get(randomIdx).esLider = true;
                    }
                    emitirEstadoSala(sala);
                }
            }
        } else {
            if (receptorId != null) {
                messagingTemplate.convertAndSend("/queue/match/" + receptorId,
                        "{\"tipo\": \"SALA_ABANDONADA\"}");
            }
        }
    }
    @MessageMapping("/crearSala")
    public void crearSala(Map<String, Object> payload) {
        String codigoSala = (String) payload.get("codigoSala");
        String nombreSala = (String) payload.get("nombreSala");
        boolean esPrivada = (boolean) payload.get("esPrivada");
        String contrasena = (String) payload.get("contrasena");
        int maxJugadores = (int) payload.get("maxJugadores");
        Long creadorId = Long.valueOf(payload.get("creadorId").toString());
        String nombreJugador = (String) payload.get("nombre");
        double elo = Double.parseDouble(payload.get("elo").toString());
        SalaEnMemoria nuevaSala = new SalaEnMemoria(codigoSala, nombreSala, esPrivada, contrasena, maxJugadores, creadorId);
        nuevaSala.jugadores.add(new JugadorSalaDTO(creadorId, nombreJugador, elo, true, true));
        salasDisponibles.put(codigoSala, nuevaSala);
    }

    @MessageMapping("/unirseSala")
    public void unirseSala(Map<String, Object> payload) {
        String codigoSala = (String) payload.get("codigoSala");
        Long jugadorId = Long.valueOf(payload.get("jugadorId").toString());
        SalaEnMemoria sala = salasDisponibles.get(codigoSala);
        if (sala != null && sala.jugadores.size() < sala.maxJugadores) {
            boolean jugando = sala.jugadores.size() < 2;
            sala.jugadores.add(new JugadorSalaDTO(jugadorId, (String) payload.get("nombre"),
                    Double.parseDouble(payload.get("elo").toString()), false, jugando));
            sala.jugadoresActuales = sala.jugadores.size();
            messagingTemplate.convertAndSend("/queue/match/" + jugadorId, "{\"tipo\": \"UNIDO_EXITO\", \"salaId\": \"" + codigoSala + "\"}");
            emitirEstadoSala(sala);
        }
    }

    @MessageMapping("/solicitarSalas")
    public void solicitarSalas(Map<String, Object> payload) {
        Long jugadorId = Long.valueOf(payload.get("jugadorId").toString());
        List<SalaEnMemoria> listaSalas = new java.util.ArrayList<>(salasDisponibles.values());
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("tipo", "LISTA_SALAS");
        response.put("salas", listaSalas);
        messagingTemplate.convertAndSend("/queue/match/" + jugadorId, response);
    }

    @MessageMapping("/solicitarEstadoSala")
    public void solicitarEstadoSala(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        SalaEnMemoria sala = salasDisponibles.get(salaId);
        if (sala != null) {
            emitirEstadoSala(sala);
        }
    }
    @MessageMapping("/transferirLider")
    public void transferirLider(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        Long nuevoLiderId = Long.valueOf(payload.get("nuevoLiderId").toString());
        SalaEnMemoria sala = salasDisponibles.get(salaId);
        if(sala != null) {
            for(JugadorSalaDTO j : sala.jugadores) j.esLider = j.id.equals(nuevoLiderId);
            emitirEstadoSala(sala);
        }
    }

    @MessageMapping("/iniciarVotacion")
    public void iniciarVotacion(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        SalaEnMemoria sala = salasDisponibles.get(salaId);
        if(sala != null) {
            sala.enVotacion = true;
            for(JugadorSalaDTO j : sala.jugadores) j.votos = 0;
            emitirEstadoSala(sala);
            for(JugadorSalaDTO j : sala.jugadores) {
                messagingTemplate.convertAndSend("/queue/match/" + j.id, "{\"tipo\": \"VOTACION_INICIADA\"}");
            }
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                try { Thread.sleep(20000); } catch (Exception e) {}
                SalaEnMemoria s = salasDisponibles.get(salaId);
                if(s != null) {
                    s.enVotacion = false;
                    s.jugadores.sort((a, b) -> Integer.compare(b.votos, a.votos));
                    for(int i=0; i<s.jugadores.size(); i++) s.jugadores.get(i).jugando = (i < 2);
                    emitirEstadoSala(s);
                    for(JugadorSalaDTO j : s.jugadores) {
                        messagingTemplate.convertAndSend("/queue/match/" + j.id, "{\"tipo\": \"VOTACION_TERMINADA\"}");
                    }
                }
            });
        }
    }

    @MessageMapping("/emitirVoto")
    public void emitirVoto(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        Long candidatoId = Long.valueOf(payload.get("candidatoId").toString());
        SalaEnMemoria sala = salasDisponibles.get(salaId);
        if(sala != null && sala.enVotacion) {
            for(JugadorSalaDTO j : sala.jugadores) {
                if(j.id.equals(candidatoId)) { j.votos++; break; }
            }
            emitirEstadoSala(sala);
        }
    }

    @MessageMapping("/enviarEmote")
    public void enviarEmote(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        if (salaId != null) {
            messagingTemplate.convertAndSend("/topic/sala/" + salaId + "/emotes", payload);
        }
    }

    @MessageMapping("/enviarChat")
    public void enviarChat(Map<String, Object> payload) {
        String salaId = (String) payload.get("salaId");
        if (salaId != null) {
            messagingTemplate.convertAndSend("/topic/sala/" + salaId + "/chat", payload);
        }
    }

    public static class JugadorSalaDTO {
        public Long id;
        public String nombreCompleto;
        public double elo;
        public boolean esLider;
        public boolean jugando;
        public int votos;

        public JugadorSalaDTO(Long id, String n, double elo, boolean esLider, boolean jugando) {
            this.id = id; this.nombreCompleto = n; this.elo = elo; this.esLider = esLider; this.jugando = jugando; this.votos = 0;
        }
    }

    public class SalaEnMemoria {
        public String codigoSala; public String nombreSala; public boolean esPrivada;
        public String contrasena; public int maxJugadores; public int jugadoresActuales;
        public boolean enCurso; public Long creadorId; public boolean enVotacion;
        public List<JugadorSalaDTO> jugadores = new CopyOnWriteArrayList<>();

        public SalaEnMemoria(String cod, String nom, boolean priv, String pass, int max, Long creador) {
            this.codigoSala = cod; this.nombreSala = nom; this.esPrivada = priv; this.contrasena = pass;
            this.maxJugadores = max; this.jugadoresActuales = 1; this.creadorId = creador; this.enVotacion = false;
        }
    }
    private void emitirEstadoSala(SalaEnMemoria sala) {
        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("tipo", "ESTADO_SALA");
        payload.put("jugadores", sala.jugadores);
        for(JugadorSalaDTO j : sala.jugadores) {
            messagingTemplate.convertAndSend("/queue/match/" + j.id, payload);
        }
    }
}