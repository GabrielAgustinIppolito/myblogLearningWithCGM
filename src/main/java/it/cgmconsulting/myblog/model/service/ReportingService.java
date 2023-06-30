package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.ReportingId;
import it.cgmconsulting.myblog.model.data.common.ReportingStatus;
import it.cgmconsulting.myblog.model.data.entity.Comment;
import it.cgmconsulting.myblog.model.data.entity.Reason;
import it.cgmconsulting.myblog.model.data.entity.Reporting;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.data.payload.request.ReportingRequest;
import it.cgmconsulting.myblog.model.repository.ReportingRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportingService {

    private final ReportingRepository repo;
    private final ReasonService reasonService;
    private final CommentService commentService;

    public ResponseEntity<?> createReporting(ReportingRequest request, Long userId) {
        // verigicare che il commento non sia già presente nella reporting
        if(repo.existsById(new ReportingId(new Comment(request.getCommentId())))){
            return new ResponseEntity<>("This comment is already reported", HttpStatus.BAD_REQUEST);
        }
        Comment c = commentService.findCommentNotCensored(request.getCommentId());

        // L'utente segnalante non può essere l'utente segnalato
        if (userId == c.getAuthor().getId()){
            return new ResponseEntity<>("You cannot report yourself", HttpStatus.BAD_REQUEST);
        }
        User u = new User(userId);

        Optional<Reason> r = reasonService.getValidReason(request.getReason());
        if(r.isEmpty()){
            return new ResponseEntity<>("Invalid reason", HttpStatus.NOT_FOUND);
        }
        Reporting rep = new Reporting(new ReportingId(c),u, r.get());
        rep.setNote(request.getNote());
        repo.save(rep);
        return new ResponseEntity<>("New reporing about comment " + rep.getReportingId().getComment().getId() +
                " has been created", HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> manageReporting(long commentId, String reason, String status) {
        /*
            Cambi di status
                OPEN -> IN_PROGRESS, CLOESD_WITH_BAN, CLOSED_WITHOUT_BAN, PERMABAN
                IN_PROGRESS -> CLOESD_WITH_BAN, CLOSED_WITHOUT_BAN, PERMABAN
            Una volta che la segnalazione viene chiusa (con o senza ban) non é più modificabile
        */

        Optional<Reporting> reporting = repo.findById(new ReportingId(commentService.findCommentNotCensored(commentId)));
        Reason r;
        if(!reporting.isPresent())
            return new ResponseEntity<>("Reporting not found", HttpStatus.NOT_FOUND);
        if(reason != null){
            Optional<Reason> rea = reasonService.getValidReason(reason);
            if(reporting.get().getReason().getReasonId().getReason().equals(reason))
                r = reporting.get().getReason();
            else
                r = rea.get();
        } else
            r = reporting.get().getReason();
        if(reporting.get().getStatus().equals(ReportingStatus.CLOSED_WITH_BAN) ||
                reporting.get().getStatus().equals(ReportingStatus.CLOSED_WITHOUT_BAN) ||
                reporting.get().getStatus().equals(ReportingStatus.PERMABAN))
            return new ResponseEntity<>("This reporting is already closed", HttpStatus.FORBIDDEN);
        if (ReportingStatus.valueOf(status).equals(reporting.get().getStatus()))
            return new ResponseEntity<>("Please change status", HttpStatus.FORBIDDEN);
        if(ReportingStatus.valueOf(status).equals(ReportingStatus.OPEN) && reporting.get().getStatus().
                equals(ReportingStatus.IN_PROGRESS))
            return new ResponseEntity<>("Cannot revert status to OPEN", HttpStatus.FORBIDDEN);

            if(ReportingStatus.valueOf(status).equals(ReportingStatus.CLOSED_WITH_BAN) ||
                            ReportingStatus.valueOf(status).equals(ReportingStatus.PERMABAN)){
                /* Censuro il commento e disabilito lo user */
                reporting.get().getReportingId().getComment().setCensored(true);
                reporting.get().getReportingId().getComment().getAuthor().setEnabled(false);
                reporting.get().getReportingId().getComment().getAuthor().
                        setBannedUntil(LocalDateTime.now().plusDays(r.getSeverity()));
            }
            reporting.get().setStatus(ReportingStatus.valueOf(status));
            reporting.get().setReason(r);

        return new ResponseEntity<>("Reporting Updated!", HttpStatus.OK);
    }
}
