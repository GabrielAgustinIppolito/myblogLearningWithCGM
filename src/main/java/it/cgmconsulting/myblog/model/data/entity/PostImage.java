package it.cgmconsulting.myblog.model.data.entity;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.PostImageId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
public class PostImage {
    @EmbeddedId
    private PostImageId postImageId;

    // Esempio cotruttore
    // PostImage pi = new PostImage(new PostImageId(oggettoPost, strinaFilenameName))

}
