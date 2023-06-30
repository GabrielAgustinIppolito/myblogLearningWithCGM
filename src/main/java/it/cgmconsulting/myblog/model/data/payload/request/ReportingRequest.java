package it.cgmconsulting.myblog.model.data.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ReportingRequest {

    private long commentId;
    @NotBlank @Size(min = 5, max = 50)
    private String reason;
    private String note;

}
