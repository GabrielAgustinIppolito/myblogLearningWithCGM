package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.Reporting;
import it.cgmconsulting.myblog.model.data.EmbeddablesId.ReportingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportingRepository  extends JpaRepository<Reporting, ReportingId> {
}
