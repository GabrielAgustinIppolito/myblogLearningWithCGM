package it.cgmconsulting.myblog.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.model.data.payload.request.PostRequest;
import it.cgmconsulting.myblog.model.service.PostService;
import it.cgmconsulting.myblog.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


@RestController
@RequestMapping("post")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "myBlogSecurityScheme")
public class PostController {

    private final PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> createPost(@RequestBody @Valid PostRequest request,
                                        @AuthenticationPrincipal UserPrincipal principal){
        return postService.createPost(request, principal);
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> updatePost(@PathVariable long postId,
                                        @RequestBody @Valid PostRequest request,
                                        @AuthenticationPrincipal UserPrincipal principal){
        return postService.updatePost(postId, request, principal);
    }

    @CacheEvict(value = "postByCategories", allEntries = true)
    @PutMapping("publish/{postId}")
    @PreAuthorize("hasRole('ROLE_CHIEF_EDITOR')")
    public ResponseEntity<?> publishPost(@PathVariable long postId,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String publishedAt){ // Un reqParam è sempre obbligatorio

        return postService.publishPost(postId, publishedAt);
    }

    @CacheEvict(value = "postByCategories", allEntries = true)
    @PutMapping("add-categories/{postId}") // aggiunge e/o modifica categorie associate ad un post
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> addCategories(@PathVariable long postId,
                                           @RequestBody @NotEmpty Set<String> categories) {
        return postService.addCategories(postId, categories);
    }

    @CacheEvict(value = "postByCategories", allEntries = true)
    @PutMapping("remove-all-categories/{postId}") // elimina tutte le categorie associate ad un post
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> removeAllCategories(@PathVariable long postId) {
        return postService.removeAllCategories(postId);
    }

    @GetMapping("public/get-boxes")
    public ResponseEntity<?> getBoxes(@RequestParam(defaultValue = "0") int pageNumber,
                                      @RequestParam(defaultValue = "3")  int pageSize,
                                      @RequestParam(defaultValue = "DESC")  String direction,
                                      @RequestParam(defaultValue = "publishedAt")  String sortBy,
                                      @RequestParam(defaultValue = "PRE") String imagePosition) {
        return postService.getPostBoxes(pageNumber, pageSize, direction, sortBy, imagePosition);
    }

    @GetMapping("public/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable long postId,
                                           @RequestParam(defaultValue = "HDR") String imagePosition) {
        return postService.getPostDetail(postId, imagePosition);
    }

    @Cacheable("postByCategories") // Da usare con attenzione perché con un altra stessa chiamata
    @GetMapping("public/get-posts-by-category")
    public ResponseEntity<?> getPostByCategory(@RequestParam String categoryName,
                                               @RequestParam(defaultValue = "HDR") String imagePosition) {
        log.info("\n\n---------------------- POST BY CATEGORIES ----------------------\n\n");
        return postService.getPostByCategory(categoryName, imagePosition);
    }

    @GetMapping("public/get-posts-by-keyword")
    public ResponseEntity<?> getPostByKeyword(@RequestParam String keyword,
                                              @RequestParam(defaultValue = "false") boolean isCaseSensitive,
                                              @RequestParam(defaultValue = "false") boolean isExactMatch
                                              ) {
        return postService.getPostByKeyword(keyword, isCaseSensitive, isExactMatch);
    }

    @GetMapping("public/get-posts-by-author")
    public ResponseEntity<?> getPostByAuthor(@RequestParam String author){
        return postService.getPostByAuthor(author);
    }

    @GetMapping("public/get-most-rated-in-period")
    public ResponseEntity<?> getMostRatedInPeriod(@RequestParam(required = false) LocalDate start,
                                                  @RequestParam(required = false) LocalDate end){
        return postService.getMostRatedInPeriod(start, end);
    }

}
