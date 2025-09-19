package com.binarybrains.blogpersonal.article;

import static com.binarybrains.blogpersonal.article.ArticleTestUtils.createMockArticleDto;
import static com.binarybrains.blogpersonal.article.ArticleTestUtils.createTitlelessArticleDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(ArticleControllerV1.class)
class ArticleControllerV1Test {

  private static final String ARTICLE_URI = "/v1/articles";
  private static final String ARTICLE_ID = ARTICLE_URI + "/{id}";
  private static final String CREATE_ARTICLE_URI = ARTICLE_URI + "/create";

  @MockitoBean private ArticleService articleService;
  @MockitoBean private ArticleRepository articleRepository;

  @Autowired private MockMvc mockMvc;

  ObjectMapper objectMapper = new ObjectMapper();
  ArticleDto articleDto;
  String articleJson;

  @BeforeEach
  void setUp() throws JsonProcessingException {
    openMocks(this);
    articleDto = createMockArticleDto();
    articleJson = objectMapper.writeValueAsString(articleDto);
  }

  @Test
  void getAllArticles_shouldReturnListOfArticles_whenArticlesFound() throws Exception {
    when(articleService.getAllArticles()).thenReturn(List.of(articleDto));

    MvcResult result = mockMvc.perform(get(ARTICLE_URI)).andExpect(status().isOk()).andReturn();

    List<ArticleDto> articles =
        objectMapper.readValue(
            result.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, ArticleDto.class));

    assertThat(articles).isNotNull().hasOnlyElementsOfType(ArticleDto.class);
  }

  @Test
  void getAllArticles_shouldReturnEmptyList_whenNoArticlesFound() throws Exception {
    when(articleService.getAllArticles()).thenReturn(Collections.emptyList());

    MvcResult result = mockMvc.perform(get(ARTICLE_URI)).andExpect(status().isOk()).andReturn();

    List<ArticleDto> articles =
        objectMapper.readValue(
            result.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, ArticleDto.class));

    assertThat(articles).isNotNull().isEmpty();
  }

  @Test
  void getArticleById_shouldReturnArticle_whenArticleIsFoundById() throws Exception {
    when(articleService.getArticleById(anyString())).thenReturn(Optional.of(articleDto));

    Assertions.assertNotNull(articleDto.id());
    MvcResult result =
        mockMvc
            .perform(get(ARTICLE_ID.replace("{id}", articleDto.id().toString())))
            .andExpect(status().isOk())
            .andReturn();

    ArticleDto articleFound =
        objectMapper.readValue(result.getResponse().getContentAsString(), ArticleDto.class);

    assertThat(articleFound).isNotNull().matches(article -> articleDto.id().equals(article.id()));
  }

  @Test
  void getArticleById_shouldReturnNotFound_whenArticleIsNotFoundById() throws Exception {
    String articleId = UUID.randomUUID().toString();
    when(articleService.getArticleById(anyString())).thenReturn(Optional.empty());

    MockHttpServletRequestBuilder request = get(ARTICLE_ID.replace("{id}", articleId));

    mockMvc
        .perform(request)
        .andExpect(status().isNotFound())
        .andExpect(
            result ->
                assertThat(result.getResponse().getContentAsString())
                    .contains("Article not found with id: " + articleId));
  }

  @Test
  void createArticle_shouldReturnArticle_whenNewArticleWasCreated() throws Exception {

    when(articleService.createArticle(any(ArticleDto.class))).thenReturn(articleDto);

    MvcResult result =
        mockMvc
            .perform(post(CREATE_ARTICLE_URI).contentType("application/json").content(articleJson))
            .andExpect(status().isCreated())
            .andReturn();

    ArticleDto createdArticle =
        objectMapper.readValue(result.getResponse().getContentAsString(), ArticleDto.class);

    assertThat(createdArticle).isNotNull().isEqualTo(articleDto);
  }

  @Test
  void createArticle_shouldReturnBadRequest_whenTitleIsMissing() throws Exception {
    ArticleDto titlelessArticleDto = createTitlelessArticleDto();

    mockMvc
        .perform(
            post(CREATE_ARTICLE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(titlelessArticleDto)))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertThat(result.getResponse().getContentAsString())
                    .contains("Title is required"));
  }

  @Test
  void updateArticle_shouldReturnArticle_whenArticleIsUpdated() throws Exception {
    when(articleService.updateArticle(anyString(), any(ArticleDto.class)))
        .thenReturn(Optional.of(articleDto));
    mockMvc
        .perform(
            put(ARTICLE_ID.replace("{id}", articleDto.id().toString()))
                .content(articleJson)
                .contentType("application/json"))
        .andExpect(status().isNoContent());
  }

  @Test
  void updateArticle_shouldResponseNotFound_whenArticleToUpdateDoesNotExist() throws Exception {
    when(articleRepository.findById(any())).thenReturn(Optional.empty());
    String articleId = UUID.randomUUID().toString();
    String expectedMessage = String.format("Article not found with id: %s", articleId);

    mockMvc
        .perform(
            put(ARTICLE_ID.replace("{id}", articleId))
                .content(articleJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(
            result ->
                assertThat(result.getResponse().getContentAsString()).contains(expectedMessage));
  }

  @Test
  void deleteArticle_shouldDeleteArticle_whenArticleExists() throws Exception {
    String articleId = UUID.randomUUID().toString();

    mockMvc
        .perform(delete(ARTICLE_ID.replace("{id}", articleId)))
        .andExpect(status().isNoContent());

    verify(articleService).deleteArticle(anyString());
  }
}
