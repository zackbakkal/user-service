package com.zack.peojects.chatapp.userservice.service;

import com.zack.peojects.chatapp.userservice.template.UserRequestTemplate;
import com.zack.peojects.chatapp.userservice.template.UserResponseTemplate;
import com.zack.peojects.chatapp.userservice.entity.User;
import com.zack.peojects.chatapp.userservice.exception.UserNameExistsException;
import com.zack.peojects.chatapp.userservice.exception.UserNameNotFoundException;
import com.zack.peojects.chatapp.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponseTemplate registerUser(UserRequestTemplate userRequestTemplate) throws UserNameExistsException {
        log.info(String.format("Checking if username [%s] already exists", userRequestTemplate.getUsername()));
        boolean userNameExists = userRepository.existsById(userRequestTemplate.getUsername());

        if(userNameExists) {
            log.info(String.format("username [%s] is taken", userRequestTemplate.getUsername()));
            throw new UserNameExistsException(
                    String.format("Username [%s] is taken, please try a different username", userRequestTemplate.getUsername()));
        }

        log.info(String.format("username [%s] is available: ", userRequestTemplate.getUsername()));
        User user = new User();

        log.info("Encoding password");
        user.setPassword(userRequestTemplate.getPassword());

        log.info("Activating user account");
        activateUserAccount(user);

        log.info("Initializing user availability to available");
        initializeUserAvailability(user);

        log.info(String.format("Registering user: [%s]", user));
        userRepository.save(user);

        log.info(String.format("Creating response"));
        UserResponseTemplate userResponseTemplate = new UserResponseTemplate(user);

        return userResponseTemplate;
    }

    private void activateUserAccount(User user) {
        log.info(String.format("Activating User [%s] account", user));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
    }

    private void initializeUserAvailability(User user) {
        user.setAvailability("available");
    }

    public List<UserResponseTemplate> getAllUsers() {
        List<UserResponseTemplate> userResponseTemplates = new ArrayList<>();

        log.info((String.format("Retrieving all users")));
        userRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .forEach(user ->
                        userResponseTemplates.add(new UserResponseTemplate(user)));

        return userResponseTemplates;
    }

    public UserResponseTemplate getUser(String username) throws UserNameNotFoundException {

        log.info(String.format("Retrieving user [%s]", username));
        UserResponseTemplate userResponseTemplate = userRepository.findUserByUsername(username);

        if(userResponseTemplate != null) {
            return userResponseTemplate;
        }

        throw new UserNameNotFoundException(String.format("Username [%s] does not exist", username));
    }


    public boolean setUserOnline(String username) {
        log.info(String.format("Updating user [%s] online status to online", username));
        return userRepository.updateUserOnlineStatus(username, true) == 1;
    }

    public boolean setUserOffline(String username) {
        log.info(String.format("Updating user [%s] online status to offline", username));
        return userRepository.updateUserOnlineStatus(username, false) == 1;
    }

    public boolean setUserAvailability(String username, String availability) {
        log.info(String.format("Updating user [%s] availability to [%s]", username, availability));
        return userRepository.updateUserAvailability(username, availability) == 1;
    }

    public boolean userIsRegistered(String username) {
        log.info(String.format("Checking if user [%s] is registered", username));
        return userRepository.findUserByUsername(username) != null;
    }
}
