package com.github.maulazaro.service;

import com.github.maulazaro.dto.UserDto;
import com.github.maulazaro.exception.StoryException;
import com.github.maulazaro.exception.UserException;
import com.github.maulazaro.modal.Story;
import com.github.maulazaro.modal.User;
import com.github.maulazaro.repository.StoryRepository;
import com.github.maulazaro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StoryService {
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private UserRepository userRepository;

    public Story createStory(Story story, Integer userId) throws UserException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserException("user not exist with id " + userId);
        }
        UserDto dto = new UserDto();
        dto.setEmail(user.get().getEmail());
        dto.setId(user.get().getId());
        dto.setName(user.get().getName());
        dto.setUserImage(user.get().getImage());
        dto.setUsername(user.get().getUsername());

        story.setUser(dto);
        story.setTimestamp(LocalDateTime.now());
        user.get().getStories().add(story);

        return storyRepository.save(story);
    }

    public List<Story> findStoriesByUserId(Integer userId) throws UserException, StoryException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserException("user not exist with id " + userId);
        }

        List<Story> stories = user.get().getStories();
        if (stories.isEmpty()) {
            throw new StoryException("this user doesn't have any story");
        }

        return stories;
    }

}
