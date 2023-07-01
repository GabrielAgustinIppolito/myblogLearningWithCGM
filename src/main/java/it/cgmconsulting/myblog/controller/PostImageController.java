package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.model.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("post-image")
@RequiredArgsConstructor
@SecurityRequirement(name = "myBlogSecurityScheme")
public class PostImageController {

    private final PostImageService postImageService;

    @PostMapping(value = "{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> addImagesToPost(
            @PathVariable long postId,
            @RequestPart MultipartFile[] filesP,
            @RequestPart MultipartFile[] filesH,
            @RequestPart MultipartFile[] filesC) throws IOException {
        return new ResponseEntity<>(
                postImageService.callGlobalCheckImages(postId, filesP, filesH, filesC),
                HttpStatus.OK
        );
    }

    @DeleteMapping("{postId}")
    @PreAuthorize("hasRole('ROLE_WRITER')")
    public ResponseEntity<?> deleteImagesFromPost(@PathVariable long postId,
                                                  @RequestBody Set<String> filesToDelete){
        return postImageService.delete(postId, filesToDelete);
    }

//    @PostMapping("update/{postId}")
//    @PreAuthorize("hasRole('ROLE_WRITER')")
//    public ResponseEntity<?> updateImagesFromPost(@PathVariable long postId,
//                                                  @RequestBody Set <String> filesToUpdate,
//                                                  @RequestParam MultipartFile[] files){
//        postImageService.delete(postId, filesToUpdate);
//        return postImageService.addImages(postId, files);
//    }
}
