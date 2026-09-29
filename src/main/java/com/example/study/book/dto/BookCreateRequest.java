package com.example.study.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookCreateRequest(
    @NotBlank(message = "책 제목은 필수") @Size(max = 50, message = "책 제목 50자 이하") String title,
    @Size(max = 20, message = "저자는 20자 이하") String author,
    @NotBlank(message = "책 사진은 필수") String coverImageUrl,
    @Size(max = 1000, message = "설명 1000자 이하") String description,
    boolean acceptsPrice,
    boolean acceptsSwap,
    boolean acceptsGiveaway) {}
