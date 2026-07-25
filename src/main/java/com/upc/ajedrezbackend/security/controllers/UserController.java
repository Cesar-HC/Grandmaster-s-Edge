package com.upc.ajedrezbackend.security.controllers;

import com.upc.ajedrezbackend.security.entities.Role;
import com.upc.ajedrezbackend.security.entities.User;
import com.upc.ajedrezbackend.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", exposedHeaders = "Authorization")
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder bcrypt;

    @PostMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
    public void createUser(@RequestBody User user) {
        String bcryptPassword = bcrypt.encode(user.getPassword());
        user.setPassword(bcryptPassword);
        userService.save(user);
    }

    @PostMapping("/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public void createRol(@RequestBody Role rol) {
        userService.grabar(rol);
    }


    @PostMapping("/save/{user_id}/{rol_id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> saveUseRol(@PathVariable("user_id") Long user_id,
                                              @PathVariable("rol_id") Long rol_id){
        return new ResponseEntity<Integer>(userService.insertUserRol(user_id, rol_id), HttpStatus.OK);
    }
}
