package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.Reason;
import it.cgmconsulting.myblog.model.data.EmbeddablesId.ReasonId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReasonRepository  extends JpaRepository<Reason, ReasonId> {
}
