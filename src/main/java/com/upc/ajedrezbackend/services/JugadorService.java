package com.upc.ajedrezbackend.services;

import com.upc.ajedrezbackend.dtos.JugadorDTO;
import com.upc.ajedrezbackend.entities.Jugador;
import com.upc.ajedrezbackend.interfaces.IJugadorService;
import com.upc.ajedrezbackend.repositories.JugadorRepository;
import com.upc.ajedrezbackend.security.entities.Role;
import com.upc.ajedrezbackend.security.entities.User;
import com.upc.ajedrezbackend.security.repository.RoleRepository;
import com.upc.ajedrezbackend.security.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JugadorService implements IJugadorService {
    @Autowired
    private JugadorRepository jugadorRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public JugadorDTO crearJugador(JugadorDTO jugadorDTO) {
        if (jugadorDTO.getId() == null) {
            Jugador jugador = modelMapper.map(jugadorDTO, Jugador.class);
            User user = new User();
            user.setUsername(jugadorDTO.getCorreoElectronico());
            user.setPassword(passwordEncoder.encode(jugadorDTO.getContrasena()));
            Role role = roleRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            user.getRoles().add(role);
            userRepository.save(user);
            jugador.setElo(0.0);
            jugador.setNroVictorias(0L);
            jugador.setNroEmpates(0L);
            jugador.setNroDerrotas(0L);
            jugador.setEstado("Activo");
            Role rol = new Role();
            rol.setId(1L);
            rol.setName("ROLE_JUGADOR");
            jugador.setRole(rol);
            jugadorRepository.save(jugador);
            return modelMapper.map(jugador, JugadorDTO.class);
        }
        return null;
    }

    @Override
    public JugadorDTO actualizarJugador(JugadorDTO jugadorDTO) {
        if (jugadorDTO.getId() != null) {
            Jugador jugador = modelMapper.map(jugadorDTO, Jugador.class);
            jugador.setElo(jugadorDTO.getElo());
            jugador.setNroVictorias(jugadorDTO.getNroVictorias());
            jugador.setNroEmpates(jugadorDTO.getNroEmpates());
            jugador.setNroDerrotas(jugadorDTO.getNroDerrotas());
            jugador.setEstado("Activo");
            jugadorRepository.save(jugador);
            return modelMapper.map(jugador, JugadorDTO.class);
        }
        return null;
    }

    @Override
    public void eliminarJugador(Long idJugador) {
        Jugador jugador = jugadorRepository.findById(idJugador).orElse(null);
        if (jugador != null) {
            jugador.setEstado("Eliminado");
            jugadorRepository.save(jugador);
        }
    }

    @Override
    public List<JugadorDTO> buscarJugadores() {
        List<Jugador> jugadores = jugadorRepository.findAll();
        return jugadores.stream().map(jugador -> modelMapper.map(jugador, JugadorDTO.class)).toList();
    }

    @Override
    public JugadorDTO buscarJugadoresPorId(Long idJugador) {
        Jugador jugador = jugadorRepository.findById(idJugador).orElse(null);
        return modelMapper.map(jugador, JugadorDTO.class);
    }

    @Override
    public JugadorDTO buscarJugadoresPorCorreo(String correo) {
        Jugador jugador = jugadorRepository.findByCorreoElectronico(correo);
        if (jugador == null) {
            throw new RuntimeException("No se encontró un perfil de jugador para el correo: " + correo);
        }
        return modelMapper.map(jugador, JugadorDTO.class);
    }
}
