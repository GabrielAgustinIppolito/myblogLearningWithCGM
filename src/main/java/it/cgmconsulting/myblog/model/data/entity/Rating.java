package it.cgmconsulting.myblog.model.data.entity;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.RatingId;
import it.cgmconsulting.myblog.model.data.common.CreationUpdate;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
/*@Check(constraints = "rate > 0 AND rate < 6")
Funziona con versioni antecedenti ad hirante 6.2.2
Per farlo funzionare conq uesta versione bisogna mettere il Check a livello di attributo:
@Column(columnDefinition = "TINYINT check(rate > 0 AND rate < 6)")
ma si è obbligati a scrivere TINYINT che è un tipo di Mysql e MariaDb e non dia ltri database.
Quindi direi di eliminare il @Check*/
public class Rating extends CreationUpdate {
    @EmbeddedId
    private RatingId ratingId;

    private byte rate; // voti da 1 a 5

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return Objects.equals(getRatingId(), rating.getRatingId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRatingId());
    }
}
