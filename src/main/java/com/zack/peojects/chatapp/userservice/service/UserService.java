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
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

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
        boolean availabilityChanged = userRepository.updateUserAvailability(username, availability) == 1;
        if(availabilityChanged) {
            restTemplate.put(
                    "http://NOTIFICATION-SERVICE/notifications/notifyUsers/" + username,
                    null,
                    Boolean.class);
        }

        return availabilityChanged;
    }

    public boolean userIsRegistered(String username) {
        log.info(String.format("Checking if user [%s] is registered", username));
        return userRepository.findUserByUsername(username) != null;
    }

    public List<UserResponseTemplate> searchUsersStartWith(String username) {

        log.info((String.format("Searching username [%s]", username)));
        return userRepository.findByUsernameStartingWith(username);

    }

}
