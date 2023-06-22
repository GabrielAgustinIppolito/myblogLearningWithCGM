package it.cgmconsulting.myblog.model.data.EmbeddablesId;

import it.cgmconsulting.myblog.model.data.entity.User;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString @EqualsAndHashCode
public class AvatarId implements Serializable {

    @OneToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;
}
