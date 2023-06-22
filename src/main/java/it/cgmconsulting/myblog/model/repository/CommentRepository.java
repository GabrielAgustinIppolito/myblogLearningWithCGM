package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.Comment;
import it.cgmconsulting.myblog.model.data.payload.response.CommentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndCensoredFalse(Long id);

    @Query(value = """
                   SELECT new it.cgmconsulting.myblog.model.data.payload.response.CommentResponse(
                   c.id, 
                   CASE WHEN (c.censored = true) THEN  '***********' ELSE c.comment END, 
                   c.author.username, 
                   c.createdAt, 
                   c.parent.id ) 
                   FROM Comment c 
                   WHERE c.post.id = :postId 
                   ORDER BY c.createdAt DESC, c.parent.id DESC
                   """

    )
    List<CommentResponse> getComments(@Param("postId") long postId);

}
