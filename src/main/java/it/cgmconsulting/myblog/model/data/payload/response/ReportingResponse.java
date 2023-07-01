package it.cgmconsulting.myblog.model.data.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ReportingResponse {

    private long commentId; // Che coincide con il ReportingId
    private String comment;
    private String reason;
    private String note;
    private String username; // Segnalante
    private LocalDateTime updatedAt;

}
