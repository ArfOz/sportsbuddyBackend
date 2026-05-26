package com.sportbuddy.service;

import com.sportbuddy.model.User;
import com.sportbuddy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    //Hashcode will be made for password for store pass
    @Transactional
    public User registerUser(User user) {
        try {
            boolean existUser = userRepository.existsByEmail(user.getEmail());

            if (existUser) {
                throw new RuntimeException("User already exists");
            }
            userRepository.save(user);
            return user;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


    public void loginUser() {

    }

    public void getUserById() {

    }

    public void getUserByEmail() {

    }

    public void updateUser() {

    }

    public void updateLocation() {

    }

    public void updateSports() {

    }

    public void updateAvailability() {

    }

    public void findUsersBySport() {

    }

    public void findNearbyUsers() {

    }

    public void findUsersBySportAndLocation() {

    }

    public void getJoinedEvents() {

    }

    public void getCreatedEvents() {

    }

    private boolean existsByEmail() {

        return true;
    }

    private void validateUser() {

    }


}
