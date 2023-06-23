package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.common.ImagePosition;
import it.cgmconsulting.myblog.model.data.entity.PostImage;
import it.cgmconsulting.myblog.model.data.EmbeddablesId.PostImageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PostImageRepository  extends JpaRepository<PostImage, PostImageId> {

    long countByPostImageIdPostIdAndImagePosition(long postId, ImagePosition imagePosition);

//    Set<PostImage> findByPostImageId(Set<PostImageId> filenames);
    void deleteAllByPostImageIdPostIdAndPostImageIdFilenameIn(long postId, Set<String> filenames);

}
