package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.RatingId;
import it.cgmconsulting.myblog.model.data.entity.Post;
import it.cgmconsulting.myblog.model.data.entity.Rating;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.data.payload.response.ReportAuthorRatingResponse;
import it.cgmconsulting.myblog.model.repository.RatingRepository;
import it.cgmconsulting.myblog.model.repository.ReportAuthorRatingRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final PostService postService;
    private final RatingRepository repo;
    private final ReportAuthorRatingRepository reportRepository;

    public ResponseEntity<?> addRate(long principalId, long postId, byte rate) {

        Post p = postService.findVisiblePost(postId);

        Rating r = new Rating(new RatingId(new User(principalId), p),
                              rate);
        repo.save(r);
        return new ResponseEntity<>("Your rate has been registered", HttpStatus.CREATED);
    }

    public ResponseEntity<?> getBestMonthlyAuthors() {
        List<ReportAuthorRatingResponse> r = reportRepository.findAll().stream().
                map(x -> new ReportAuthorRatingResponse(x.getId(), x.getAuthor().getId(),
                        x.getAuthor().getUsername(), x.getAverage(), x.getPostWritten(),x.getActually())).toList();
//        private long id;
//        private long authorId;
//        private double average;
//        private long postWritten;
//        private YearMonth yearMonth;
        return new ResponseEntity<>(r, HttpStatus.CREATED);
    }
}
