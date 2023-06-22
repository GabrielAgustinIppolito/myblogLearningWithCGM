package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.model.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("post-image")
@RequiredArgsConstructor
public class PostImageController {

    private final PostImageService postImageService;

    @PostMapping("{postId}")
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> addImagesToPost(@PathVariable long postId,
                                             @RequestParam MultipartFile[] files ){
        return postImageService.addImages(postId, files);
    }

    @DeleteMapping("{postId}")
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> deleteImagesFromPost(@PathVariable long postId,
                                                  @RequestBody Set <String> filesToDelete){
        return postImageService.deleteImagesFromPost(postId, filesToDelete);
    }

    @PostMapping("update/{postId}")
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> updateImagesFromPost(@PathVariable long postId,
                                                  @RequestBody Set <String> filesToUpdate,
                                                  @RequestParam MultipartFile[] files){
         postImageService.deleteImagesFromPost(postId, filesToUpdate);
        return postImageService.addImages(postId, files);
    }
}
