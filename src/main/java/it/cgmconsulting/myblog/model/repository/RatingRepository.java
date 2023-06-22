package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.Rating;
import it.cgmconsulting.myblog.model.data.EmbeddablesId.RatingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository  extends JpaRepository<Rating, RatingId> {
}
