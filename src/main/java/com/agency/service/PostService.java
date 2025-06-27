package com.agency.service;


import com.agency.data.dto.PostRequestDto;
import com.agency.data.dto.PostResponseDto;
import java.util.List;
import java.util.Optional;

public interface PostService {
    PostResponseDto createPost(Long userId, PostRequestDto postRequestDto);
    List<PostResponseDto> getAllPosts();
    Optional<PostResponseDto> getPostById(Long id);
    List<PostResponseDto> getPostsByUserId(Long userId);
    Optional<PostResponseDto> updatePost(Long id, PostRequestDto postRequestDto);
    boolean deletePost(Long id);
}
