package com.blog.web.api;

import com.blog.api.model.Article;
import com.blog.api.service.ArticleService;
import com.blog.web.api.dto.ApiResponse;
import com.blog.web.security.annotation.RequirePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*") // 允许跨域请求
public class ArticleRestController {

    private final ArticleService articleService;

    @Autowired
    public ArticleRestController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 获取所有文章
     * GET /api/articles
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Article>>> getAllArticles() {
        try {
            List<Article> articles = articleService.getAllArticles();
            return ResponseEntity.ok(ApiResponse.success("获取文章列表成功", articles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取文章列表失败", e.getMessage()));
        }
    }

    /**
     * 根据ID获取文章
     * GET /api/articles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Article>> getArticleById(@PathVariable Long id) {
        try {
            Optional<Article> article = articleService.getArticleById(id);
            if (article.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("获取文章成功", article.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("文章不存在", "未找到ID为 " + id + " 的文章"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取文章失败", e.getMessage()));
        }
    }

    /**
     * 创建新文章
     * POST /api/articles
     */
    @PostMapping
    @RequirePermission(resource = "article", action = "write", description = "创建文章")
    public ResponseEntity<ApiResponse<Article>> createArticle(@RequestBody Article article) {
        try {
            Article createdArticle = articleService.createArticle(article);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("文章创建成功", createdArticle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("文章创建失败", e.getMessage()));
        }
    }

    /**
     * 更新文章
     * PUT /api/articles/{id}
     */
    @PutMapping("/{id}")
    @RequirePermission(resource = "article", action = "write", description = "更新文章")
    public ResponseEntity<ApiResponse<Article>> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        try {
            Article updatedArticle = articleService.updateArticle(id, article);
            return ResponseEntity.ok(ApiResponse.success("文章更新成功", updatedArticle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("文章更新失败", e.getMessage()));
        }
    }

    /**
     * 删除文章
     * DELETE /api/articles/{id}
     */
    @DeleteMapping("/{id}")
    @RequirePermission(resource = "article", action = "delete", description = "删除文章")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.ok(ApiResponse.success("文章删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("文章删除失败", e.getMessage()));
        }
    }
}