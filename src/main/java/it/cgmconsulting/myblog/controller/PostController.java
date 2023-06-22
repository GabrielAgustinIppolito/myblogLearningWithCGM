package it.cgmconsulting.myblog.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.cgmconsulting.myblog.model.data.payload.request.PostRequest;
import it.cgmconsulting.myblog.model.service.PostService;
import it.cgmconsulting.myblog.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@RestController
@RequestMapping("post")
@RequiredArgsConstructor
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

    @PutMapping("publish/{postId}")
    @PreAuthorize("hasRole('ROLE_CHIEF_EDITOR')")
    public ResponseEntity<?> publishPost(@PathVariable long postId,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String publishedAt){ // Un reqParam è sempre obbligatorio

        return postService.publishPost(postId, publishedAt);
    }

    @PutMapping("add-categories/{postId}") // aggiunge e/o modifica categorie associate ad un post
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> addCategories(@PathVariable long postId,
                                           @RequestBody @NotEmpty Set<String> categories) {
        return postService.addCategories(postId, categories);
    }

    @PutMapping("remove-all-categories/{postId}") // elimina tutte le categorie associate ad un post
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> removeAllCategories(@PathVariable long postId) {
        return postService.removeAllCategories(postId);
    }
}
