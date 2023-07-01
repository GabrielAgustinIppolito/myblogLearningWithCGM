package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.common.ReportingStatus;
import it.cgmconsulting.myblog.model.data.entity.Reporting;
import it.cgmconsulting.myblog.model.data.EmbeddablesId.ReportingId;
import it.cgmconsulting.myblog.model.data.payload.request.ReportingRequest;
import it.cgmconsulting.myblog.model.data.payload.response.ReportingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportingRepository  extends JpaRepository<Reporting, ReportingId> {
    boolean existsById(ReportingId reporingId);

    @Query(value = """
                   SELECT new it.cgmconsulting.myblog.model.data.payload.response.ReportingResponse(
                   rep.reportingId.comment.id,
                   rep.reportingId.comment.comment,
                   rep.reason.reasonId.reason,
                   rep.note,
                   rep.user.username,
                   rep.updatedAt
                   ) FROM Reporting rep
                   WHERE rep.status = :status
                   ORDER BY rep.updatedAt
                   """)
    List<ReportingResponse> getReportingsByStatus(@Param("status")ReportingStatus status);

}
