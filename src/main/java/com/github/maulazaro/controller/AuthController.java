package com.github.maulazaro.controller;

import com.github.maulazaro.exception.UserException;
import com.github.maulazaro.modal.User;
import com.github.maulazaro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> registerHandler(@RequestBody User user) throws UserException {
        User newUser = userService.register(user);
        return new ResponseEntity<User>(newUser, HttpStatus.OK);
    }

    @GetMapping("/signing")
    public ResponseEntity<User> signinHandler(Authentication auth) throws BadCredentialsException, UserException {
        Optional<User> user = Optional.ofNullable(userService.findUserByEmail(auth.getName()));

        if (user.isPresent()) {
            return new ResponseEntity<User>(user.get(), HttpStatus.ACCEPTED);
        }

        throw new BadCredentialsException("invalid username or password");
    }

}
