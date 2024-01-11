package com.github.maulazaro.controller;

import com.github.maulazaro.exception.StoryException;
import com.github.maulazaro.exception.UserException;
import com.github.maulazaro.modal.Story;
import com.github.maulazaro.modal.User;
import com.github.maulazaro.service.StoryService;
import com.github.maulazaro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
public class StoryController {
    @Autowired
    private StoryService storyService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<Story> createStoryHandler(@RequestBody Story story, @RequestHeader("Authorization") String token) throws UserException {
        User user = userService.findUserProfile(token);
        Story createdStory = storyService.createStory(story, user.getId());
        return new ResponseEntity<Story>(createdStory, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Story>> findAllStoryByUserIdHandler(@PathVariable Integer userId) throws UserException, StoryException {
        User user = userService.findUserById(userId);
        List<Story> stories = storyService.findStoriesByUserId(user.getId());
        return new ResponseEntity<List<Story>>(stories, HttpStatus.OK);
    }
}
