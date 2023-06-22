package it.cgmconsulting.myblog.model.data.entity;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.ReasonId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
@Check(constraints = "severity > 0")
public class Reason {
    @EmbeddedId
    private ReasonId reasonId;

    private LocalDate endDate;

    private int severity;

    public Reason(ReasonId reasonId) {
        this.reasonId = reasonId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reason reason = (Reason) o;
        return Objects.equals(getReasonId(), reason.getReasonId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReasonId());
    }
}
