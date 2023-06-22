package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.PostImage;
import it.cgmconsulting.myblog.model.data.EmbeddablesId.PostImageId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository  extends JpaRepository<PostImage, PostImageId> {
}
