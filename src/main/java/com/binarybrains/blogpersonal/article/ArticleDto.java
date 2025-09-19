package com.binarybrains.blogpersonal.article;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ArticleDto(
    @Nullable UUID id,
    @NotBlank(message = "Title is required.") String title,
    @NotBlank(message = "Content is required.") String content,
    @Nullable LocalDate created) {}
