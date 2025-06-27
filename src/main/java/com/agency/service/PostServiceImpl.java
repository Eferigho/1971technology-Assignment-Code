package com.agency.service;

import com.agency.data.dto.PostRequestDto;
import com.agency.data.dto.PostResponseDto;
import com.agency.data.entity.Post;
import com.agency.data.entity.User;
import com.agency.repository.PostRepository;
import com.agency.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public PostResponseDto createPost(Long userId, PostRequestDto postRequestDto) {
        try {
            log.info("Creating post for user ID: {} with title: {}", userId, postRequestDto.getTitle());
            
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            // Verify user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            
            Post post = mapToEntity(postRequestDto, user);
            Post savedPost = postRepository.save(post);
            
            log.info("Post created successfully with ID: {} for user ID: {}", savedPost.getId(), userId);
            return mapToResponseDto(savedPost);
            
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("Error while creating post for user ID {}: {}", userId, e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while creating post for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to create post due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while creating post for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to create post", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        try {
            log.info("Fetching all posts");
            List<Post> posts = postRepository.findAll();
            return posts.stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Database error while fetching all posts: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch posts due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching all posts: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch posts", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PostResponseDto> getPostById(Long id) {
        try {
            log.info("Fetching post with ID: {}", id);
            
            if (id == null) {
                throw new IllegalArgumentException("Post ID cannot be null");
            }
            
            return postRepository.findById(id)
                    .map(this::mapToResponseDto);
                    
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while fetching post: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while fetching post with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to fetch post due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching post with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to fetch post", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByUserId(Long userId) {
        try {
            log.info("Fetching posts for user ID: {}", userId);
            
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            // Verify user exists
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("User not found with ID: " + userId);
            }
            
            List<Post> posts = postRepository.findAllByUserId(userId);
            return posts.stream()
                    .map(this::mapToResponseDto)
                    .collect(Collectors.toList());
                    
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            log.error("Error while fetching posts for user ID {}: {}", userId, e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while fetching posts for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch posts due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching posts for user ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch posts", e);
        }
    }

    @Override
    public Optional<PostResponseDto> updatePost(Long id, PostRequestDto postRequestDto) {
        try {
            log.info("Updating post with ID: {}", id);
            
            if (id == null) {
                throw new IllegalArgumentException("Post ID cannot be null");
            }
            
            Optional<Post> existingPostOpt = postRepository.findById(id);
            if (existingPostOpt.isEmpty()) {
                log.warn("Post with ID {} not found for update", id);
                return Optional.empty();
            }
            
            Post existingPost = existingPostOpt.get();
            existingPost.setTitle(postRequestDto.getTitle());
            existingPost.setContent(postRequestDto.getContent());
            
            Post updatedPost = postRepository.save(existingPost);
            log.info("Post updated successfully with ID: {}", updatedPost.getId());
            
            return Optional.of(mapToResponseDto(updatedPost));
            
        } catch (IllegalArgumentException e) {
            log.error("Error while updating post with ID {}: {}", id, e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while updating post with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update post due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while updating post with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to update post", e);
        }
    }

    @Override
    public boolean deletePost(Long id) {
        try {
            log.info("Deleting post with ID: {}", id);
            
            if (id == null) {
                throw new IllegalArgumentException("Post ID cannot be null");
            }
            
            if (!postRepository.existsById(id)) {
                log.warn("Post with ID {} not found for deletion", id);
                return false;
            }
            
            postRepository.deleteById(id);
            log.info("Post deleted successfully with ID: {}", id);
            return true;
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while deleting post: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while deleting post with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete post due to database error", e);
        } catch (Exception e) {
            log.error("Unexpected error while deleting post with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete post", e);
        }
    }

    private Post mapToEntity(PostRequestDto postRequestDto, User user) {
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setUser(user);
        return post;
    }

    private PostResponseDto mapToResponseDto(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        return dto;
    }
}