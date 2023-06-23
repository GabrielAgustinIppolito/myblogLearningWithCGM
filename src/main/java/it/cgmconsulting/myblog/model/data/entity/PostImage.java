package it.cgmconsulting.myblog.model.data.entity;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.PostImageId;
import it.cgmconsulting.myblog.model.data.common.ImagePosition;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @ToString @AllArgsConstructor @EqualsAndHashCode
public class PostImage {
    @EmbeddedId
    private PostImageId postImageId;

    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private ImagePosition imagePosition;

    // Esempio cotruttore
    // PostImage pi = new PostImage(new PostImageId(oggettoPost, strinaFilenameName, ImagePosition.Value))

}
