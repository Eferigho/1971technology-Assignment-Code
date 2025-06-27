package com.agency.controller;

import com.agency.data.dto.PostRequestDto;
import com.agency.data.dto.PostResponseDto;
import com.agency.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Post Management", description = "Operations related to post management")
public class PostController {

    private final PostService postService;

    @PostMapping("/users/{userId}")
    @Operation(summary = "Create a new post", description = "Creates a new post for the specified user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or user ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PostResponseDto> createPost(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @RequestBody PostRequestDto postRequestDto) {
        try {
            log.info("Request to create post for user ID: {} with title: {}", userId, postRequestDto.getTitle());
            PostResponseDto createdPost = postService.createPost(userId, postRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (EntityNotFoundException e) {
            log.error("User not found while creating post: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while creating post: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error while creating post: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all posts", description = "Retrieves a list of all posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        try {
            log.info("Request to get all posts");
            List<PostResponseDto> posts = postService.getAllPosts();
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            log.error("Error while fetching all posts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get post by ID", description = "Retrieves a post by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post found"),
            @ApiResponse(responseCode = "400", description = "Invalid post ID"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PostResponseDto> getPostById(
            @Parameter(description = "Post ID", required = true)
            @PathVariable Long id) {
        try {
            log.info("Request to get post by ID: {}", id);
            Optional<PostResponseDto> post = postService.getPostById(id);

            return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while fetching post by ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error while fetching post by ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get posts by user ID", description = "Retrieves all posts for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PostResponseDto>> getPostsByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        try {
            log.info("Request to get posts for user ID: {}", userId);
            List<PostResponseDto> posts = postService.getPostsByUserId(userId);
            return ResponseEntity.ok(posts);
        } catch (EntityNotFoundException e) {
            log.error("User not found while fetching posts: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while fetching posts for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error while fetching posts for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update post", description = "Updates an existing post with the provided information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or post ID"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PostResponseDto> updatePost(
            @Parameter(description = "Post ID", required = true)
            @PathVariable Long id,
            @RequestBody PostRequestDto postRequestDto) {
        try {
            log.info("Request to update post with ID: {}", id);
            Optional<PostResponseDto> updatedPost = postService.updatePost(id, postRequestDto);

            return updatedPost.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while updating post with ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error while updating post with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post", description = "Deletes a post by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid post ID"),
            @ApiResponse(responseCode = "404", description = "Post not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "Post ID", required = true)
            @PathVariable Long id) {
        try {
            log.info("Request to delete post with ID: {}", id);
            boolean deleted = postService.deletePost(id);
            
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid argument while deleting post with ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error while deleting post with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}