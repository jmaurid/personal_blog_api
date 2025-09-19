package com.binarybrains.blogpersonal.article.impl;

import static com.binarybrains.blogpersonal.article.ArticleTestUtils.createMockArticle;
import static com.binarybrains.blogpersonal.article.ArticleTestUtils.createMockArticleDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

import com.binarybrains.blogpersonal.article.Article;
import com.binarybrains.blogpersonal.article.ArticleDto;
import com.binarybrains.blogpersonal.article.ArticleRepository;
import com.binarybrains.blogpersonal.article.exception.custom.ArticleNotFoundException;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ArticleServiceImplTest {

  @Mock private ArticleRepository articleRepository;

  @InjectMocks private ArticleServiceImpl articleService;

  @BeforeEach
  void setUp() {
    openMocks(this);
  }

  @Test
  void getAllArticles_shouldReturnListOfArticles_whenArticlesFound() {
    Article article = mock(Article.class);
    List<Article> articlesMocked = List.of(article);
    when(articleRepository.findAll()).thenReturn(articlesMocked);

    List<ArticleDto> articles = articleService.getAllArticles();

    assertThat(articles).isNotNull().isNotEmpty().hasOnlyElementsOfType(ArticleDto.class);
  }

  @Test
  void getAllArticles_shouldReturnEmptyList_whenNoArticlesFound() {
    when(articleRepository.findAll()).thenReturn(Collections.emptyList());

    List<ArticleDto> articles = articleService.getAllArticles();

    assertThat(articles).isNotNull().isEmpty();
  }

  @Test
  void getArticleById_shouldReturnOptionalWithArticle_whenArticleIsFoundById() {
    Article article = createMockArticle();
    when(articleRepository.findById(article.getId())).thenReturn(Optional.of(article));

    Optional<ArticleDto> articleOptional =
        articleService.getArticleById(article.getId().toString());

    assertThat(articleOptional).isPresent();
    assertThat(articleOptional.get().title()).isEqualTo(article.getTitle());
    assertThat(articleOptional.get().content()).isEqualTo(article.getContent());
  }

  @Test
  void getArticleById_shouldReturnEmptyOptional_whenArticleIsNotFoundById() {
    when(articleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Optional<ArticleDto> articleOptional =
        articleService.getArticleById(UUID.randomUUID().toString());

    assertThat(articleOptional).isNotPresent();
  }

  @Test
  void createArticle_shouldReturnArticle_whenArticleIsCreated() {
    Article article = mock(Article.class);
    ArticleDto articleDto = mock(ArticleDto.class);
    when(articleRepository.save(any(Article.class))).thenReturn(article);

    ArticleDto createdArticle = articleService.createArticle(articleDto);

    assertThat(createdArticle).isNotNull();
  }

  @Test
  void updateArticle_shouldUpdateOnlyTheTitle_whenOnlyTitleIsUpdated() {
    Article existingArticle = createMockArticle();
    ArticleDto articleDtoToUpdate =
        createMockArticleDto(
            Article.builder().id(existingArticle.getId()).title("Updated Title").build());

    when(articleRepository.findById(existingArticle.getId()))
        .thenReturn(Optional.of(existingArticle));
    when(articleRepository.save(any(Article.class))).thenReturn(existingArticle);

    Optional<ArticleDto> articleUpdated =
        articleService.updateArticle(articleDtoToUpdate.id().toString(), articleDtoToUpdate);

    ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);
    verify(articleRepository).save(articleCaptor.capture());
    Article articleCaptured = articleCaptor.getValue();

    assertThat(articleUpdated).isPresent();
    assertThat(articleCaptured.getTitle())
        .isNotEqualTo(existingArticle.getTitle())
        .isEqualTo(articleDtoToUpdate.title());
    assertThat(articleCaptured.getContent()).isEqualTo(existingArticle.getContent());
  }

  @Test
  void updateArticle_shouldUpdateOnlyTheContent_whenOnlyContentIsUpdated() {
    Article existingArticle = createMockArticle();
    ArticleDto articleDtoToUpdate =
        createMockArticleDto(
            Article.builder().id(existingArticle.getId()).content("Updated content").build());

    when(articleRepository.findById(existingArticle.getId()))
        .thenReturn(Optional.of(existingArticle));
    when(articleRepository.save(any(Article.class))).thenReturn(existingArticle);

    Optional<ArticleDto> articleUpdated =
        articleService.updateArticle(articleDtoToUpdate.id().toString(), articleDtoToUpdate);

    ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);
    verify(articleRepository).save(articleCaptor.capture());
    Article articleCaptured = articleCaptor.getValue();

    assertThat(articleUpdated).isPresent();
    assertThat(articleCaptured.getTitle()).isEqualTo(existingArticle.getTitle());
    assertThat(articleCaptured.getContent())
        .isNotEqualTo(existingArticle.getContent())
        .isEqualTo(articleDtoToUpdate.content());
  }

  @Test
  void updateArticle_shouldUpdateAllFields_whenAllFieldsAreUpdated() {
    Article existingArticle = createMockArticle();
    ArticleDto articleDtoToUpdate =
        createMockArticleDto(
            Article.builder()
                .id(existingArticle.getId())
                .title("Updated title")
                .content("Updated content")
                .build());

    when(articleRepository.findById(existingArticle.getId()))
        .thenReturn(Optional.of(existingArticle));
    when(articleRepository.save(any(Article.class))).thenReturn(existingArticle);

    Optional<ArticleDto> articleUpdated =
        articleService.updateArticle(articleDtoToUpdate.id().toString(), articleDtoToUpdate);

    ArgumentCaptor<Article> articleCaptor = ArgumentCaptor.forClass(Article.class);
    verify(articleRepository).save(articleCaptor.capture());
    Article articleCaptured = articleCaptor.getValue();

    assertThat(articleUpdated).isPresent();
    assertThat(articleCaptured.getTitle())
        .isEqualTo(articleDtoToUpdate.title())
        .isNotEqualTo(existingArticle.getTitle());
    assertThat(articleCaptured.getContent())
        .isEqualTo(articleDtoToUpdate.content())
        .isNotEqualTo(existingArticle.getContent());
  }

  @Test
  void updateArticle_shouldReturnOptionalWithArticle_whenArticleWasUpdated() {
    UUID articleId = UUID.randomUUID();
    LocalDate createdDate = LocalDate.now();

    Article existingArticle =
        Article.builder()
            .id(articleId)
            .title("Original Title")
            .content("Original Content")
            .created(createdDate)
            .build();

    ArticleDto articleDto =
        ArticleDto.builder()
            .title("Updated Title")
            .content("Updated Content")
            .created(createdDate)
            .build();

    Article updatedArticle =
        existingArticle.toBuilder()
            .title(articleDto.title())
            .content(articleDto.content())
            .created(articleDto.created())
            .build();

    ArticleDto expectedDto =
        ArticleDto.builder()
            .title("Updated Title")
            .content("Updated Content")
            .created(createdDate)
            .build();

    when(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle));
    when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);

    Optional<ArticleDto> result = articleService.updateArticle(articleId.toString(), articleDto);

    assertThat(result).isPresent();
    assertThat(result.get().title()).isEqualTo(expectedDto.title());
    assertThat(result.get().content()).isEqualTo(expectedDto.content());
    assertThat(result.get().created()).isEqualTo(expectedDto.created());

    verify(articleRepository).findById(articleId);
    verify(articleRepository).save(updatedArticle);
  }

  @Test
  void updateArticle_shouldReturnEmptyOptional_whenArticleWasNotUpdatedAsItWasNotFound() {
    String articleId = UUID.randomUUID().toString();
    ArticleDto article = mock(ArticleDto.class);

    when(articleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Optional<ArticleDto> articleUpdated = articleService.updateArticle(articleId, article);

    assertThat(articleUpdated).isEmpty();
  }

  @Test
  void deleteArticle_shouldDeleteArticle_whenArticleIsFound() {
    String articleId = UUID.randomUUID().toString();
    Article article = mock(Article.class);
    when(articleRepository.findById(any(UUID.class))).thenReturn(Optional.of(article));

    articleService.deleteArticle(articleId);

    verify(articleRepository, times(1)).delete(any(Article.class));
  }

  @Test
  void deleteArticle_shouldNotDeleteArticle_whenArticleIsNotFound() {
    String articleId = UUID.randomUUID().toString();
    when(articleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    assertThrows(ArticleNotFoundException.class, () -> articleService.deleteArticle(articleId));

    verify(articleRepository, times(0)).delete(any(Article.class));
  }
}
