package com.zack.peojects.chatapp.userservice.controller;

import com.zack.peojects.chatapp.userservice.template.UserRequestTemplate;
import com.zack.peojects.chatapp.userservice.template.UserResponseTemplate;
import com.zack.peojects.chatapp.userservice.exception.UserNameExistsException;
import com.zack.peojects.chatapp.userservice.exception.UserNameNotFoundException;
import com.zack.peojects.chatapp.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserResponseTemplate registerUser(@RequestBody UserRequestTemplate userRequestTemplate) throws UserNameExistsException {
        return userService.registerUser(userRequestTemplate);
    }

    @GetMapping("/all")
    public List<UserResponseTemplate> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}")
    public UserResponseTemplate getUser(@PathVariable String username) throws UserNameNotFoundException {
        return userService.getUser(username);
    }

    @PutMapping("/{username}/online")
    public boolean setUserOnline(@PathVariable String username)
            throws UserNameNotFoundException {
        return userService.setUserOnline(username);
    }

    @PutMapping("/{username}/offline")
    public boolean setUserOffline(@PathVariable String username)
            throws UserNameNotFoundException {
        return userService.setUserOffline(username);
    }

    @PutMapping("/{username}/{availability}")
    public boolean updateUserAvailability(@PathVariable String username, @PathVariable String availability)
            throws UserNameNotFoundException {
        return userService.setUserAvailability(username, availability);
    }

}
