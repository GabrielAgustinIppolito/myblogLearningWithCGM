package it.cgmconsulting.myblog.model.data.payload.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter @AllArgsConstructor
public class ReasonRequest {

    @NotBlank @Size(max = 50, min = 5)
    private String reason;
    @NotNull @FutureOrPresent
    private LocalDate startDate;
    @Min(1)
    private int severity;

}
