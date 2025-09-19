package com.binarybrains.blogpersonal.article.impl;

import static com.binarybrains.blogpersonal.utils.MappersUtil.mapArticleDtoToArticle;
import static com.binarybrains.blogpersonal.utils.MappersUtil.mapArticleToDto;

import com.binarybrains.blogpersonal.article.Article;
import com.binarybrains.blogpersonal.article.ArticleDto;
import com.binarybrains.blogpersonal.article.ArticleRepository;
import com.binarybrains.blogpersonal.article.ArticleService;
import com.binarybrains.blogpersonal.article.exception.custom.ArticleNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArticleServiceImpl implements ArticleService {

  private final ArticleRepository articleRepository;

  @Override
  public List<ArticleDto> getAllArticles() {
    return articleRepository.findAll().stream().map(mapArticleToDto::map).toList();
  }

  @Override
  public ArticleDto createArticle(ArticleDto articleDto) {
    Article article = mapArticleDtoToArticle.map(articleDto);
    Article articleSaved = articleRepository.save(article);
    return mapArticleToDto.map(articleSaved);
  }

  @Override
  public Optional<ArticleDto> getArticleById(String id) {
    return articleRepository.findById(UUID.fromString(id)).map(mapArticleToDto::map);
  }

  @Override
  public Optional<ArticleDto> updateArticle(String id, ArticleDto articleDto) {
    return articleRepository
        .findById(UUID.fromString(id))
        .map(
            existingArticle -> {
              Article.ArticleBuilder articleBuilder = existingArticle.toBuilder();
              if (Objects.nonNull(articleDto.title())) {
                articleBuilder.title(articleDto.title());
              }
              if (Objects.nonNull(articleDto.content())) {
                articleBuilder.content(articleDto.content());
              }
              Article articleSaved = articleRepository.save(articleBuilder.build());
              return mapArticleToDto.map(articleSaved);
            });
  }

  @Override
  public void deleteArticle(String id) {
    articleRepository
        .findById(UUID.fromString(id))
        .ifPresentOrElse(
            articleRepository::delete,
            () -> {
              throw new ArticleNotFoundException(id);
            });
  }
}
