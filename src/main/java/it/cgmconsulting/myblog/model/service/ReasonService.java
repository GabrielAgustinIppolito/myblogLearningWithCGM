package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.ReasonId;
import it.cgmconsulting.myblog.model.data.entity.Reason;
import it.cgmconsulting.myblog.model.data.payload.request.ReasonRequest;
import it.cgmconsulting.myblog.model.repository.ReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReasonService {

    private final ReasonRepository repo;

    public ResponseEntity<Reason> createReason(ReasonRequest request){
        ReasonId reasonId = new ReasonId(request.getReason(), request.getStartDate());
        if(repo.existsById(reasonId)){
            return new ResponseEntity("Reason alredy present", HttpStatus.BAD_REQUEST);
        }
        Reason reason = new Reason(reasonId, request.getSeverity());
        return new ResponseEntity<>(repo.save(reason), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Reason> updateReason(ReasonRequest request) {
        /* 1. trovare la reason attualmlente valida (ovvero quella con endDate a null)
           1 bis. confrontare le severity, se sono uguali non fare niente
           2. agggiornare le endDate con il valore di startDate - 1 day
           3. creare nuovo record
         */
        Optional<Reason> reasonIniziale = repo.findByReasonIdReasonAndEndDateIsNull(request.getReason());

        if(!reasonIniziale.isPresent())
            return new ResponseEntity("Reason not found", HttpStatus.NOT_FOUND);
        if (reasonIniziale.isPresent() && reasonIniziale.get().getSeverity() == request.getSeverity())
            return new ResponseEntity("Reason props equals, nothing changed", HttpStatus.BAD_REQUEST);
        ReasonId reasonId = new ReasonId(request.getReason(), request.getStartDate());
        if(repo.existsById(reasonId)){
            return new ResponseEntity("Reason alredy present", HttpStatus.BAD_REQUEST);
        }
        reasonIniziale.get().setEndDate(request.getStartDate().minusDays(1));
        Reason reason = new Reason(reasonId, request.getSeverity());
        return new ResponseEntity<>(repo.save(reason), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> removeReason(String reason, LocalDate endDate) {
        Optional<Reason> reasonIniziale = repo.findByReasonIdReasonAndEndDateIsNull(reason);

        if(!reasonIniziale.isPresent())
            return new ResponseEntity("Reason not found", HttpStatus.NOT_FOUND);

        reasonIniziale.get().setEndDate(endDate);
        return new ResponseEntity<>("Reason " + reasonIniziale + " no more valid from " + endDate,
                HttpStatus.OK);
    }

    public ResponseEntity<?> getReasons() {
        /* Elenco di reason in corso di validità per ordine alfabetico*/
        return new ResponseEntity<>(repo.getReasons(LocalDate.now()), HttpStatus.OK);
    }
    public Optional<Reason> getValidReason(String reason) {
        /* Elenco di reason in corso di validità per ordine alfabetico*/
        return repo.getValidReason(reason, LocalDate.now());
    }
}
