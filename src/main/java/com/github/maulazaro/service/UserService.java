package com.github.maulazaro.service;

import com.github.maulazaro.dto.UserDto;
import com.github.maulazaro.exception.UserException;
import com.github.maulazaro.modal.User;
import com.github.maulazaro.repository.UserRepository;
import com.github.maulazaro.security.JwtTokenClaims;
import com.github.maulazaro.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public User register(User user) throws UserException {
        Optional<User> isEmailExist = userRepository.findByEmail(user.getEmail());
        if (isEmailExist.isPresent()) {
            throw new UserException("Email is already exist");
        }
        Optional<User> isUsernameExist = userRepository.findByUsername(user.getUsername());
        if (isUsernameExist.isPresent()) {
            throw new UserException("Username is already taken");
        }
        if (user.getEmail() == null || user.getPassword() == null || user.getUsername() == null || user.getName() == null) {
            throw new UserException("All fields are required");
        }

        User newUser = new User();

        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setName(user.getName());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(newUser);
    }

    public User findUserById(Integer userId) throws UserException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("user not exist with id " + userId);
    }

    public User findUserProfile(String token) throws UserException {
        token = token.substring(7);
        JwtTokenClaims jwtTokenClaims = jwtTokenProvider.getClaimsFromToken(token);
        String email = jwtTokenClaims.getUsername();
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            return user.get();
        }

        throw new UserException("Invalid token...");
    }

    public User findUserByUsername(String username) throws UserException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("user not exist with username " + username);
    }

    public String followUser(Integer reqUserId, Integer followUserId) throws UserException {
        User reqUser = findUserById(reqUserId);
        User followUser = findUserById(followUserId);

        UserDto follower = new UserDto();

        follower.setEmail(reqUser.getEmail());
        follower.setId(reqUser.getId());
        follower.setName(reqUser.getName());
        follower.setUserImage(reqUser.getUsername());
        follower.setUsername(reqUser.getUsername());

        UserDto following = new UserDto();
        following.setEmail(followUser.getEmail());
        following.setId(followUser.getId());
        following.setUserImage(followUser.getImage());
        following.setName(followUser.getName());
        following.setUsername(followUser.getUsername());

        reqUser.getFollowing().add(following);
        followUser.getFollower().add(follower);

        userRepository.save(followUser);
        userRepository.save(reqUser);

        return "You are following "+ followUser.getUsername();
    }

    public String unFollowUser(Integer reqUserId, Integer followUserId) throws UserException {
        User reqUser = findUserById(reqUserId);
        User followUser = findUserById(followUserId);

        UserDto follower = new UserDto();

        follower.setEmail(reqUser.getEmail());
        follower.setId(reqUser.getId());
        follower.setName(reqUser.getName());
        follower.setUserImage(reqUser.getUsername());
        follower.setUsername(reqUser.getUsername());

        UserDto following = new UserDto();

        following.setEmail(followUser.getEmail());
        following.setId(followUser.getId());
        following.setUserImage(followUser.getImage());
        following.setName(followUser.getName());
        following.setUsername(followUser.getUsername());

        reqUser.getFollowing().remove(following);
        followUser.getFollower().remove(follower);

        userRepository.save(followUser);
        userRepository.save(reqUser);

        return "You are unfollowed "+ followUser.getUsername();
    }

    public List<User> findUserByIds(List<Integer> userIds) throws UserException {
        return userRepository.findAllUsersByUserIds(userIds);
    }

    public List<User> searchUser(String query) throws UserException {
        List<User> users = userRepository.findByQuery(query);
        if (users.isEmpty()) {
            throw new UserException("User not found");
        }
        return users;
    }

    public User updateUserDetails(User updatedUser, User existingUser) throws UserException {
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        } if (updatedUser.getBio() != null) {
            existingUser.setBio(updatedUser.getBio());
        } if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        } if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        } if (updatedUser.getMobile() != null) {
            existingUser.setMobile(updatedUser.getMobile());
        } if (updatedUser.getGender() != null) {
            existingUser.setGender(updatedUser.getGender());
        } if (updatedUser.getWebsite() != null) {
            existingUser.setWebsite(updatedUser.getWebsite());
        } if (updatedUser.getImage() != null) {
            existingUser.setImage(updatedUser.getImage());
        } if (updatedUser.getId().equals(existingUser.getId())) {
            return userRepository.save(existingUser);
        }
        throw new UserException("You can't update this user");
    }


    public User findUserByEmail(String email) throws UserException{
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("user not exist with email " + email);
    }
}
