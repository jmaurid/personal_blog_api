package com.binarybrains.blogpersonal.article;

import com.binarybrains.blogpersonal.article.exception.custom.ArticleNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/v1/articles")
@AllArgsConstructor
public class ArticleControllerV1 {

  private static final String ARTICLE_URI = "/api/v1/articles/";
  private final ArticleService articleService;

  @GetMapping
  public ResponseEntity<List<ArticleDto>> getAllArticles() {
    List<ArticleDto> articles = articleService.getAllArticles();
    return ResponseEntity.ok(articles);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ArticleDto> getArticleById(@PathVariable String id) {
    ArticleDto articleDto =
        articleService.getArticleById(id).orElseThrow(() -> new ArticleNotFoundException(id));

    return ResponseEntity.ok(articleDto);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createArticle(@Valid @RequestBody ArticleDto articleDto) {
    ArticleDto articleCreated = articleService.createArticle(articleDto);
    URI location = URI.create(String.format("%s%s", ARTICLE_URI, articleCreated.id()));
    return ResponseEntity.created(location).body(articleCreated);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ArticleDto> updateArticle(
      @PathVariable String id, @RequestBody ArticleDto articleDto) {
    Optional<ArticleDto> updatedArticle = articleService.updateArticle(id, articleDto);
    if (updatedArticle.isEmpty()) {
      throw new ArticleNotFoundException(id);
    }
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ArticleDto> deleteArticle(@PathVariable String id) {
    articleService.deleteArticle(id);
    return ResponseEntity.noContent().build();
  }
}
