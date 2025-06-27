package com.agency;

import com.agency.data.dto.PostRequestDto;
import com.agency.data.dto.PostResponseDto;
import com.agency.data.entity.Post;
import com.agency.data.entity.User;
import com.agency.repository.PostRepository;
import com.agency.repository.UserRepository;
import com.agency.service.PostServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private PostRequestDto postRequestDto;
    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        postRequestDto = new PostRequestDto();
        postRequestDto.setTitle("Test Post");
        postRequestDto.setContent("Test Content");

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test Content");
        post.setUser(user);
    }

    @Test
    void createPost_ShouldReturnPostResponseDto_WhenValidInput() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // When
        PostResponseDto result = postService.createPost(1L, postRequestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Post");
        assertThat(result.getContent()).isEqualTo("Test Content");
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(userRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPost_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> postService.createPost(1L, postRequestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with ID: 1");

        verify(userRepository).findById(1L);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void createPost_ShouldThrowException_WhenUserIdIsNull() {
        // When & Then
        assertThatThrownBy(() -> postService.createPost(null, postRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID cannot be null");

        verify(userRepository, never()).findById(any());
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void getAllPosts_ShouldReturnListOfPosts() {
        // Given
        Post post2 = new Post();
        post2.setId(2L);
        post2.setTitle("Second Post");
        post2.setContent("Second Content");
        post2.setUser(user);

        when(postRepository.findAll()).thenReturn(Arrays.asList(post, post2));

        // When
        List<PostResponseDto> result = postService.getAllPosts();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Post");
        assertThat(result.get(1).getTitle()).isEqualTo("Second Post");

        verify(postRepository).findAll();
    }

    @Test
    void getPostById_ShouldReturnPost_WhenPostExists() {
        // Given
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // When
        Optional<PostResponseDto> result = postService.getPostById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTitle()).isEqualTo("Test Post");

        verify(postRepository).findById(1L);
    }

    @Test
    void getPostsByUserId_ShouldReturnUserPosts_WhenUserExists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(postRepository.findAllByUserId(1L)).thenReturn(Arrays.asList(post));

        // When
        List<PostResponseDto> result = postService.getPostsByUserId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Post");
        assertThat(result.get(0).getUserId()).isEqualTo(1L);

        verify(userRepository).existsById(1L);
        verify(postRepository).findAllByUserId(1L);
    }

    @Test
    void getPostsByUserId_ShouldThrowException_WhenUserNotExists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> postService.getPostsByUserId(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with ID: 1");

        verify(userRepository).existsById(1L);
        verify(postRepository, never()).findAllByUserId(any());
    }

    @Test
    void updatePost_ShouldReturnUpdatedPost_WhenValidInput() {
        // Given
        PostRequestDto updateRequest = new PostRequestDto();
        updateRequest.setTitle("Updated Title");
        updateRequest.setContent("Updated Content");

        Post updatedPost = new Post();
        updatedPost.setId(1L);
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");
        updatedPost.setUser(user);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(updatedPost);

        // When
        Optional<PostResponseDto> result = postService.updatePost(1L, updateRequest);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Updated Title");
        assertThat(result.get().getContent()).isEqualTo("Updated Content");

        verify(postRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void deletePost_ShouldReturnTrue_WhenPostExists() {
        // Given
        when(postRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = postService.deletePost(1L);

        // Then
        assertThat(result).isTrue();

        verify(postRepository).existsById(1L);
        verify(postRepository).deleteById(1L);
    }

    @Test
    void deletePost_ShouldReturnFalse_WhenPostNotExists() {
        // Given
        when(postRepository.existsById(1L)).thenReturn(false);

        // When
        boolean result = postService.deletePost(1L);

        // Then
        assertThat(result).isFalse();

        verify(postRepository).existsById(1L);
        verify(postRepository, never()).deleteById(any());
    }
}

