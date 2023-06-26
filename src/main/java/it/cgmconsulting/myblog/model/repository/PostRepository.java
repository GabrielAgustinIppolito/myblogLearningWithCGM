package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.common.ImagePosition;
import it.cgmconsulting.myblog.model.data.entity.Post;
import it.cgmconsulting.myblog.model.data.payload.response.PostBoxesResponse;
import it.cgmconsulting.myblog.model.data.payload.response.PostDetailResponse;
import it.cgmconsulting.myblog.model.data.payload.response.PostSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, long postId);

    Optional<Post> findByIdAndPublishedAtNotNullAndPublishedAtBefore(long id, LocalDateTime now);

    @Query(value = "SELECT new it.cgmconsulting.myblog.model.data.payload.response.PostBoxesResponse(" +
            "p.id, " +
            "(SELECT pi.postImageId.filename FROM PostImage pi " +
            "WHERE pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition) as image, " +
            "p.author.username, " +
            "p.publishedAt, " +
            "p.title, " +
            "p.overview) " +
            "FROM Post p " +
            "WHERE p.publishedAt IS NOT NULL AND p.publishedAt < :now " +
            "ORDER BY p.publishedAt DESC",
            countQuery = "SELECT COUNT(pi.postImageId.filename) FROM PostImage pi LEFT JOIN Post p " +
                    "ON pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition AND :now = :now "
    )
    Page<PostBoxesResponse> getPostBoxes(Pageable pageable,
                                         @Param("now") LocalDateTime now,
                                         @Param("imagePosition") ImagePosition imagePosition);

    @Query(value = """
                    SELECT cat.categoryName 
                    FROM Post p
                    INNER JOIN p.categories cat 
                    WHERE p.id = :postId AND cat.visible = true        
                    """)
    Set<String> getCategoriesByPost(long postId);

    @Query(value = "SELECT new it.cgmconsulting.myblog.model.data.payload.response.PostDetailResponse(" +
            " p.id," +
            "(SELECT pi.postImageId.filename FROM PostImage pi " +
            " WHERE pi.postImageId.post.id = p.id AND pi.imagePosition = :imagePosition) AS image," +
            "p.title," +
            "p.content," +
            "p.author.username," +
            " p.publishedAt," +
            "(SELECT COALESCE(ROUND(AVG(r.rate),2), 0d) FROM Rating r " +
            " WHERE r.ratingId.post.id = p.id ) AS average " +
            ") FROM Post p " +
            "WHERE p.id = :postId " +
            "AND p.publishedAt IS NOT NULL AND p.publishedAt < :now")
    PostDetailResponse getPostDetail(@Param("postId") long postId,
                                     @Param("imagePosition") ImagePosition imagePosition,
                                     @Param("now") LocalDateTime now);

    @Query(value = """
                SELECT new it.cgmconsulting.myblog.model.data.payload.response.PostBoxesResponse
                (
                p.id, 
                (SELECT pi.postImageId.filename FROM PostImage pi 
                WHERE pi.postImageId.post.id = p.id AND 
                pi.imagePosition = :imagePosition) as image, 
                p.author.username, 
                p.publishedAt, 
                p.title, 
                p.overview) 
                FROM Post p
                INNER JOIN p.categories cat 
                WHERE cat.visible = true AND cat.categoryName = :categoryName
                AND p.publishedAt IS NOT NULL AND p.publishedAt < :now
                ORDER BY p.publishedAt DESC
                """)
    List<PostBoxesResponse> getPostByCategory(@Param("categoryName") String categoryName,
                                              @Param("now") LocalDateTime now,
                                              @Param("imagePosition") ImagePosition imagePosition);

    @Query(value = """
                   SELECT new it.cgmconsulting.myblog.model.data.payload.response.PostSearchResponse 
                   (
                   p.id, 
                   p.title, 
                   p.overview, 
                   p.publishedAt, 
                   p.author.username, 
                   p.content
                   ) 
                   FROM Post p
                   WHERE (p.title LIKE %:keyword%
                   OR p.content LIKE %:keyword%
                   OR p.overview LIKE %:keyword%)
                   AND p.publishedAt IS NOT NULL
                   AND p.publishedAt < :now
                   ORDER BY p.publishedAt DESC
                   """)
    List<PostSearchResponse> getPostContainsAnywhere(@Param("keyword")String keyword,
                                                     @Param("now") LocalDateTime now);
}