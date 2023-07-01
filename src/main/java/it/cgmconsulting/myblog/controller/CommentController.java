package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.model.data.payload.request.CommentRequest;
import it.cgmconsulting.myblog.model.service.CommentService;
import it.cgmconsulting.myblog.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "myBlogSecurityScheme")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_READER') or hasRole('ROLE_EDITORIAL_STAFF')")
    public ResponseEntity<?> createComment(@RequestBody @Valid CommentRequest request,
                                           @AuthenticationPrincipal UserPrincipal prinipal){
        return commentService.saveComment(request, prinipal);
    }

    @GetMapping("public/{postId}")
    public ResponseEntity<?> getCommentByPost(@PathVariable long postId){
        return commentService.getCommentByPost(postId);
    }
}
