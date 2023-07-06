package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.ReportAuthorRating;
import it.cgmconsulting.myblog.model.data.payload.response.ReportAuthorRatingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

//this.author = author;
//        this.average = average;
//        this.postWritten = postWritten;
//        this.actually = actually;

public interface ReportAuthorRatingRepository extends JpaRepository<ReportAuthorRating, Long> {
//select sui post del mese precedente
//    SELECT
//    r.user_id,
//    ROUND(AVG(r.rate), 2) as media,
//    COUNT(p.author) as "post scritti"
//    FROM Rating r
//    INNER JOIN post p ON r.post_id = p.id
//    WHERE p.created_at BETWEEN '2023-06-01 00:00:00.00' AND '2023-06-30 00:00:00.00'
//    AND p.created_at IS NOT NULL
//    AND p.created_at < '2023-08-22 09:56:44.49'
//    GROUP BY p.author;        <-- count sbagliata
    @Query(value = """
                   SELECT new it.cgmconsulting.myblog.model.data.payload.response.ReportAuthorRatingResponse(
                   p.author.id,
                   p.author.username,
                   COALESCE(ROUND(AVG(r.rate),2), 0.0),
                    (
                        SELECT COUNT(p.id)
                        FROM Post p
                        WHERE p.author.id = r.ratingId.post.author.id
                        GROUP BY p.author.id
                    )
                   ) FROM Post p
                   LEFT JOIN Rating r ON p.id = r.ratingId.post.id
                   WHERE r.updatedAt BETWEEN :start AND :end
                   AND p.publishedAt IS NOT NULL AND p.publishedAt < :now
                   GROUP BY p.author.id
                   """)
//                   COUNT(r.ratingId.post.author.id)
//    INNER JOIN Post p ON r.ratingId.post.id = p.id
    List<ReportAuthorRatingResponse> getReportAuthorRating(@Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end,
                                                           @Param("now") LocalDateTime now);
//    List<ReportAuthorRatingResponse> getReportAuthorRating(@Param("start") LocalDateTime start,
//                                                           @Param("end") Y end,
//                                                           @Param("now") LocalDateTime now);
}
