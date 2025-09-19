package com.binarybrains.blogpersonal.article;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class ArticleTestUtils {

  public static Article createMockArticle() {
    return Article.builder()
        .title("Test Title")
        .content("Test Content")
        .id(UUID.randomUUID())
        .created(LocalDate.now())
        .build();
  }

  public static ArticleDto createMockArticleDto() {
    return createMockArticleDto(null);
  }

  public static ArticleDto createMockArticleDto(Article article) {
    Article articleToDto = Objects.nonNull(article) ? article : createMockArticle();
    return ArticleDto.builder()
        .title(articleToDto.getTitle())
        .content(articleToDto.getContent())
        .id(articleToDto.getId())
        .build();
  }

  public static ArticleDto createTitlelessArticleDto() {
    return ArticleDto.builder().content("Test Content").id(UUID.randomUUID()).build();
  }
}
