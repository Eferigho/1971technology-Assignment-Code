package com.agency;

import com.agency.data.dto.PostRequestDto;
import com.agency.data.dto.PostResponseDto;
import com.agency.data.dto.UserRequestDto;
import com.agency.data.dto.UserResponseDto;
import com.agency.data.entity.Post;
import com.agency.repository.PostRepository;
import com.agency.repository.UserRepository;
import com.agency.service.PostService;
import com.agency.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostServiceImplIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private UserResponseDto testUser;
    private PostRequestDto postRequestDto;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setName("Test User");
        userRequestDto.setEmail("test@example.com");
        testUser = userService.createUser(userRequestDto);

        // Create test post request
        postRequestDto = new PostRequestDto();
        postRequestDto.setTitle("Integration Test Post");
        postRequestDto.setContent("Integration test content");
    }

    @Test
    void createPost_ShouldPersistPostInDatabase() {
        // When
        PostResponseDto result = postService.createPost(testUser.getId(), postRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Integration Test Post");
        assertThat(result.getContent()).isEqualTo("Integration test content");
        assertThat(result.getUserId()).isEqualTo(testUser.getId());

        // Verify in database
        Optional<Post> savedPost = postRepository.findById(result.getId());
        assertThat(savedPost).isPresent();
        assertThat(savedPost.get().getTitle()).isEqualTo("Integration Test Post");
    }

    @Test
    void createPost_ShouldThrowException_WhenUserNotExists() {
        // When & Then
        assertThatThrownBy(() -> postService.createPost(999L, postRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with ID: 999");
    }

    @Test
    void getAllPosts_ShouldReturnAllPersistedPosts() {
        // Given
        PostRequestDto post1 = new PostRequestDto();
        post1.setTitle("Post 1");
        post1.setContent("Content 1");

        PostRequestDto post2 = new PostRequestDto();
        post2.setTitle("Post 2");
        post2.setContent("Content 2");

        postService.createPost(testUser.getId(), post1);
        postService.createPost(testUser.getId(), post2);

        // When
        List<PostResponseDto> result = postService.getAllPosts();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(PostResponseDto::getTitle)
                .containsExactlyInAnyOrder("Post 1", "Post 2");
    }

    @Test
    void getPostsByUserId_ShouldReturnUserPosts() {
        // Given
        PostRequestDto post1 = new PostRequestDto();
        post1.setTitle("User Post 1");
        post1.setContent("User Content 1");

        PostRequestDto post2 = new PostRequestDto();
        post2.setTitle("User Post 2");
        post2.setContent("User Content 2");

        postService.createPost(testUser.getId(), post1);
        postService.createPost(testUser.getId(), post2);

        // When
        List<PostResponseDto> result = postService.getPostsByUserId(testUser.getId());

        // Then
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(PostResponseDto::getTitle)
                .containsExactlyInAnyOrder("User Post 1", "User Post 2");
        assertThat(result)
                .extracting(PostResponseDto::getUserId)
                .containsOnly(testUser.getId());
    }

    @Test
    void updatePost_ShouldPersistChangesInDatabase() {
        // Given
        PostResponseDto createdPost = postService.createPost(testUser.getId(), postRequestDto);

        PostRequestDto updateRequest = new PostRequestDto();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");

        // When
        Optional<PostResponseDto> result = postService.updatePost(createdPost.getId(), updateRequest);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Updated Title");
        assertThat(result.get().getContent()).isEqualTo("Updated Content");

        // Verify in database
        Optional<Post> updatedPost = postRepository.findById(createdPost.getId());
        assertThat(updatedPost).isPresent();
        assertThat(updatedPost.get().getTitle()).isEqualTo("Updated Title");
        assertThat(updatedPost.get().getContent()).isEqualTo("Updated Content");
    }

    @Test
    void deletePost_ShouldRemoveFromDatabase() {
        // Given
        PostResponseDto createdPost = postService.createPost(testUser.getId(), postRequestDto);

        // When
        boolean result = postService.deletePost(createdPost.getId());

        // Then
        assertThat(result).isTrue();

        // Verify removal from database
        Optional<Post> deletedPost = postRepository.findById(createdPost.getId());
        assertThat(deletedPost).isEmpty();
    }

    @Test
    void fullPostLifecycle_ShouldWorkEndToEnd() {
        // Create
        PostResponseDto created = postService.createPost(testUser.getId(), postRequestDto);
        assertThat(created.getId()).isNotNull();

        // Read
        Optional<PostResponseDto> found = postService.getPostById(created.getId());
        assertThat(found).isPresent();

        // Update
        PostRequestDto updateRequest = new PostRequestDto();
        updateRequest.setTitle("Updated Integration Post");
        updateRequest.setContent("Updated integration content");

        Optional<PostResponseDto> updated = postService.updatePost(created.getId(), updateRequest);
        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("Updated Integration Post");

        // Delete
        boolean deleted = postService.deletePost(created.getId());
        assertThat(deleted).isTrue();

        // Verify deletion
        Optional<PostResponseDto> notFound = postService.getPostById(created.getId());
        assertThat(notFound).isEmpty();
    }
}
