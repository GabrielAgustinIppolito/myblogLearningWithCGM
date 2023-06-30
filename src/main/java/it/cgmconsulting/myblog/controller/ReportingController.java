package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.model.data.payload.request.ReportingRequest;
import it.cgmconsulting.myblog.model.service.ReportingService;
import it.cgmconsulting.myblog.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("reporting")
@RequiredArgsConstructor
@Validated
public class ReportingController {

    private final ReportingService reportingService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_READER')")
    public ResponseEntity<?> createReporting(@RequestBody @Valid ReportingRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        return reportingService.createReporting(request, principal.getId());
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ResponseEntity<?> manageReporting(@PathVariable long commentId,
                                             @RequestParam(required = false) String reason,
                                             @RequestParam String status){
        return reportingService.manageReporting(commentId,reason, status);
    }

}
