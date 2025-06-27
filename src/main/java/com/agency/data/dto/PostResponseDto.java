package com.agency.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private String content;
}
