package it.cgmconsulting.myblog.model.data.EmbeddablesId;

import it.cgmconsulting.myblog.model.data.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString @EqualsAndHashCode
public class PostImageId implements Serializable {
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @Column
    private String filename;
}
