package com.github.maulazaro.service;

import com.github.maulazaro.dto.UserDto;
import com.github.maulazaro.exception.CommentException;
import com.github.maulazaro.exception.PostException;
import com.github.maulazaro.exception.UserException;
import com.github.maulazaro.modal.Comment;
import com.github.maulazaro.modal.Post;
import com.github.maulazaro.modal.User;
import com.github.maulazaro.repository.CommentRepository;
import com.github.maulazaro.repository.PostRepository;
import com.github.maulazaro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    public Comment createComment(Comment comment, Integer postId, Integer userId) throws UserException, PostException {
        Optional<User> user = userRepository.findById(userId);
        Optional<Post> post = postRepository.findById(postId);
        if (user.isEmpty()) {
            throw new UserException("user not exist with id " + userId);
        }
        if (post.isEmpty()) {
            throw new PostException("Post not found with id " + postId);
        }
        UserDto dto = new UserDto();
        dto.setEmail(user.get().getEmail());
        dto.setId(user.get().getId());
        dto.setName(user.get().getName());
        dto.setUserImage(user.get().getImage());
        dto.setUsername(user.get().getUsername());

        comment.setUser(dto);
        comment.setCreatedAt(LocalDateTime.now());

        Comment createdComment = commentRepository.save(comment);
        post.get().getComments().add(createdComment);
        postRepository.save(post.get());
        return createdComment;
    }

    public Comment findCommentById(Integer commentId) throws CommentException {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            return comment.get();
        }
        throw new CommentException("Could not find comment with id " + commentId);
    }

    public Comment likeComment(Integer commentId, Integer userId) throws UserException, CommentException {
        Optional<User> user = userRepository.findById(userId);
        Comment comment = findCommentById(commentId);
        if (user.isEmpty()) {
            throw new UserException("user not exist with id " + userId);
        }
        UserDto dto = new UserDto();
        dto.setEmail(user.get().getEmail());
        dto.setId(user.get().getId());
        dto.setName(user.get().getName());
        dto.setUserImage(user.get().getImage());
        dto.setUsername(user.get().getUsername());

        comment.getLikedByUsers().add(dto);

        return commentRepository.save(comment);
    }

    public Comment unlikeComment(Integer commentId, Integer userId) throws UserException, CommentException {
        Optional<User> user = userRepository.findById(userId);
        Comment comment = findCommentById(commentId);
        if (user.isEmpty()) {
            throw new UserException("user not exist with id " + userId);
        }
        UserDto dto = new UserDto();
        dto.setEmail(user.get().getEmail());
        dto.setId(user.get().getId());
        dto.setName(user.get().getName());
        dto.setUserImage(user.get().getImage());
        dto.setUsername(user.get().getUsername());

        comment.getLikedByUsers().remove(dto);

        return commentRepository.save(comment);
    }

}
