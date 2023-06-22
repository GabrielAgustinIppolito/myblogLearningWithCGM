package it.cgmconsulting.myblog.model.data.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
public class Creation implements Serializable {

    @CreationTimestamp //in automatico si definisce un unica volta quando viene caricata per la prima volta sul db l'entità
    @Column(updatable = false)  // mi assicuro che non venga mai modificata
    private LocalDateTime createdAt;


}
