package com.binarybrains.blogpersonal.article.exception.custom;

public class ArticleNotFoundException extends RuntimeException {
  public ArticleNotFoundException(String id) {
    super("Article not found with id: " + id);
  }
}
