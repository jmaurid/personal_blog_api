package com.binarybrains.blogpersonal.article;

import java.util.List;
import java.util.Optional;

public interface ArticleService {

  List<ArticleDto> getAllArticles();

  ArticleDto createArticle(ArticleDto article);

  Optional<ArticleDto> getArticleById(String id);

  Optional<ArticleDto> updateArticle(String id, ArticleDto article);

  void deleteArticle(String id);
}
