package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.Avatar;
import it.cgmconsulting.myblog.model.data.EmbeddablesId.AvatarId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarRepository extends JpaRepository<Avatar, AvatarId> {
}
