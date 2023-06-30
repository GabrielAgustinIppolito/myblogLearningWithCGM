package it.cgmconsulting.myblog.model.data.entity;

import it.cgmconsulting.myblog.model.data.common.CreationUpdate;
import it.cgmconsulting.myblog.model.data.EmbeddablesId.ReportingId;
import it.cgmconsulting.myblog.model.data.common.ReportingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Reporting extends CreationUpdate {
    @EmbeddedId
    private ReportingId reportingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumns( {
            @JoinColumn(name="reason", referencedColumnName="reason"),
            @JoinColumn(name="start_date", referencedColumnName="startDate")
    } )
    private Reason reason;

    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false)
    private ReportingStatus status = ReportingStatus.OPEN;

    @Column(nullable = false)
    private String note;

    public Reporting(ReportingId reportingId, User user, Reason reason) {
        this.reportingId = reportingId;
        this.user = user;
        this.reason = reason;
    }

    public Reporting(ReportingId reportingId) {
        this.reportingId = reportingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reporting reporting = (Reporting) o;
        return Objects.equals(getReportingId(), reporting.getReportingId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReportingId());
    }
}
