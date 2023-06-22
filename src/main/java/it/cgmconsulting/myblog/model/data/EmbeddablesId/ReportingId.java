package it.cgmconsulting.myblog.model.data.EmbeddablesId;

import it.cgmconsulting.myblog.model.data.entity.Comment;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import java.io.Serializable;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString @EqualsAndHashCode
public class ReportingId implements Serializable {
        @OneToOne
        @JoinColumn(name = "comment_id", nullable = false)
        private Comment comment;
}
