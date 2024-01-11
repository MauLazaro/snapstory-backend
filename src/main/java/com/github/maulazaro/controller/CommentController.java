package com.github.maulazaro.controller;

import com.github.maulazaro.exception.CommentException;
import com.github.maulazaro.exception.PostException;
import com.github.maulazaro.exception.UserException;
import com.github.maulazaro.modal.Comment;
import com.github.maulazaro.modal.User;
import com.github.maulazaro.service.CommentService;
import com.github.maulazaro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @PostMapping("/create/{postId}")
    public ResponseEntity<Comment> createCommentHandler(@RequestBody Comment comment, @PathVariable Integer postId,@RequestHeader("Authorization") String token) throws UserException, PostException {
        User user = userService.findUserProfile(token);
        Comment createdComment = commentService.createComment(comment,postId, user.getId());
        return new ResponseEntity<Comment>(createdComment, HttpStatus.OK);
    }

    @PutMapping("/like/{commentId}")
    public ResponseEntity<Comment> likeCommentHandler(@RequestHeader("Authorization") String token, @PathVariable Integer commentId) throws UserException, CommentException {
        User user = userService.findUserProfile(token);
        Comment comment = commentService.likeComment(commentId, user.getId());
        return new ResponseEntity<Comment>(comment, HttpStatus.OK);
    }

    @PutMapping("/unlike/{commentId}")
    public ResponseEntity<Comment> unlikeCommentHandler(@RequestHeader("Authorization") String token, @PathVariable Integer commentId) throws UserException, CommentException {
        User user = userService.findUserProfile(token);
        Comment comment = commentService.unlikeComment(commentId, user.getId());
        return new ResponseEntity<Comment>(comment, HttpStatus.OK);
    }
}
