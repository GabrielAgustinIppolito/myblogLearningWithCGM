package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.model.data.entity.Comment;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.data.payload.request.CommentRequest;
import it.cgmconsulting.myblog.model.repository.CommentRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repo;
    private final PostService postService;

    public ResponseEntity<?> saveComment(CommentRequest request, UserPrincipal principal){
        Comment c = new Comment(
                postService.findVisiblePost(request.getPostId()),
                new User(principal.getId()),
                request.getComment(),
                request.getParentId() == null ? null : findCommentNotCensored(request.getParentId())
        );
        repo.save(c);
        return new ResponseEntity<>("New comment has been added to post", HttpStatus.CREATED);
    }

    public ResponseEntity<?> getCommentByPost(long postId) {
        return new ResponseEntity<>(repo.getComments(postId), HttpStatus.OK);
    }



    protected  Comment findComment(long commentId){
        return repo.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", commentId)
        );
    }
    protected  Comment findCommentNotCensored(long commentId){
        return repo.findByIdAndCensoredFalse(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", commentId)
        );
    }
}
