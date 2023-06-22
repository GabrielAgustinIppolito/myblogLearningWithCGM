package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.RatingId;
import it.cgmconsulting.myblog.model.data.entity.Post;
import it.cgmconsulting.myblog.model.data.entity.Rating;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.repository.RatingRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final PostService postService;
    private final RatingRepository repo;

    public ResponseEntity<?> addRate(long principalId, long postId, byte rate) {

        Post p = postService.findVisiblePost(postId);

        Rating r = new Rating(new RatingId(new User(principalId), p),
                              rate);
        repo.save(r);
        return new ResponseEntity<>("Your rate has been registered", HttpStatus.CREATED);
    }
}
