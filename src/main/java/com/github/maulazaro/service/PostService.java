package com.github.maulazaro.service;

import com.github.maulazaro.dto.UserDto;
import com.github.maulazaro.exception.PostException;
import com.github.maulazaro.exception.UserException;
import com.github.maulazaro.modal.Post;
import com.github.maulazaro.modal.User;
import com.github.maulazaro.repository.PostRepository;
import com.github.maulazaro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    public Post createPost(Post post, Integer userId) throws UserException {
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

        post.setUser(dto);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @Transactional
    public String deletePost(Integer postId, Integer userId) throws UserException, PostException {
        Post post = findPostById(postId);

        if (post == null) {
            throw new PostException("Post not exist with id " + postId);
        }

        List<User> usersWithSavedPost = userRepository.findBySavedPost(post);

        for (User user : usersWithSavedPost) {
            user.getSavedPost().removeIf(savedPost -> savedPost.getId().equals(postId));
            userRepository.save(user);
        }

        postRepository.deleteById(postId);

        return "Post deleted successfully";
    }



    public List<Post> findPostByUserId(Integer userId) throws UserException {
        List<Post> posts = postRepository.findByUserId(userId);
        if (posts.isEmpty()) {
            throw new UserException("this user doesn't have any posts");
        }
        return posts;
    }

    public Post findPostById(Integer postId) throws PostException {
        Optional<Post> post = postRepository.findById(postId);

        if (post.isPresent()) {
            return post.get();
        }
        throw new PostException("Post not found with id " + postId);
    }

    public List<Post> findAllPostByUserIds(List<Integer> userIds) throws PostException, UserException {
        List<Post> posts = postRepository.findAllPostByUserIds(userIds);

        if (posts.isEmpty()) {
            throw new PostException("No post available");
        }

        return posts;
    }

    public String savedPost(Integer postId, Integer userId) throws PostException, UserException {
        Post post = findPostById(postId);
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new UserException("user not exist with id " + userId);
        }

        if (!user.get().getSavedPost().contains(post)) {
            user.get().getSavedPost().add(post);
            userRepository.save(user.get());
        }
        return "Post saved successfully";

    }

    public String unSavedPost(Integer postId, Integer userId) throws PostException, UserException {
        Post post = findPostById(postId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new UserException("user not exist with id " + userId);
        }
        User user = userOptional.get();

        if (user.getSavedPost().contains(post)) {
            user.getSavedPost().remove(post);
            userRepository.save(user);
            return "Post removed successfully";
        } else {
            return "Post is not in the saved list";
        }
    }


    public Post likePost(Integer postId, Integer userId) throws UserException, PostException {
        Post post = findPostById(postId);
        Optional<User> user = userRepository.findById(userId);
        UserDto dto = null;

        if (user.isPresent()) {
            dto = new UserDto();
            dto.setEmail(user.get().getEmail());
            dto.setId(user.get().getId());
            dto.setName(user.get().getName());
            dto.setUserImage(user.get().getImage());
            dto.setUsername(user.get().getUsername());
        }
        post.getLikedByUsers().add(dto);

        return postRepository.save(post);
    }

    public Post unLikePost(Integer postId, Integer userId) throws UserException, PostException {
        Post post = findPostById(postId);
        Optional<User> user = userRepository.findById(userId);
        UserDto dto = null;

        if (user.isPresent()) {
            dto = new UserDto();
            dto.setEmail(user.get().getEmail());
            dto.setId(user.get().getId());
            dto.setName(user.get().getName());
            dto.setUserImage(user.get().getImage());
            dto.setUsername(user.get().getUsername());
        }
        post.getLikedByUsers().remove(dto);

        return postRepository.save(post);
    }

    public List<Post> findAllPost() throws PostException {
        List<Post> posts = postRepository.findAll();
        if (posts.isEmpty()) {
            throw new PostException("No post found");
        }
        return posts;
    }
}
