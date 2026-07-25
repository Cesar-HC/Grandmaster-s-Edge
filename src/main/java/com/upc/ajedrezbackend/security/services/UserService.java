package com.upc.ajedrezbackend.security.services;

import com.upc.ajedrezbackend.security.entities.Role;
import com.upc.ajedrezbackend.security.entities.User;
import com.upc.ajedrezbackend.security.repository.RoleRepository;
import com.upc.ajedrezbackend.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void grabar(Role role) {
        roleRepository.save(role);
    }
    public Integer insertUserRol(Long user_id, Long rol_id) {
        Integer result = 0;
        userRepository.insertUserRol(user_id, rol_id);
        return 1;
    }
}
