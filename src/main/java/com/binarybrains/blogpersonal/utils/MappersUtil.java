package com.binarybrains.blogpersonal.utils;

import com.binarybrains.blogpersonal.article.Article;
import com.binarybrains.blogpersonal.article.ArticleDto;

public class MappersUtil {
  public static final Mapper<Article, ArticleDto> mapArticleToDto =
      article ->
          ArticleDto.builder()
              .id(article.getId())
              .title(article.getTitle())
              .content(article.getContent())
              .created(article.getCreated())
              .build();

  public static final Mapper<ArticleDto, Article> mapArticleDtoToArticle =
      articleDto ->
          Article.builder()
              .title(articleDto.title())
              .content(articleDto.content())
              .created(articleDto.created())
              .build();
}
