package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.Post;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, long postId);

    Optional<Post> findByIdAndPublishedAtNotNullAndPublishedAtBefore(long id, LocalDateTime now);
}
