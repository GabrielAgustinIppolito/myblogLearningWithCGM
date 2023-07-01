package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.entity.ReportAuthorRating;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.data.payload.response.ReportAuthorRatingResponse;
import it.cgmconsulting.myblog.model.repository.ReportAuthorRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final ReportAuthorRatingRepository repo;

//    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.DAYS) // intervalli regolari, fissati, di default in millisecondi
    @Scheduled(cron = "0 0 1 1 * *") // crontab di linux 0 0 1 1 * *
    public void generateReportAuthorRating(){
        System.out.println("            STO SCHEDULANDO             ");
        /*  La schedulazione parte ogni primo del mese
            report: deve restituirre gli author che hanno scritto qualcosa nel mese precedente
            e scrivere sul db questa classifica
        */  // ETL --> Extract: recupero dei dati per creare una lista di ReportAuthorRating,
            // Transform: elaborare questi dati (qualora servisse),
            // Load: "caricarli" fa qualche parte (la tabella report_author_raitng nel nostro caso)
        List<ReportAuthorRatingResponse> list =  repo.getReportAuthorRating(
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
        System.out.println(list.size());
        if(!list.isEmpty()){
            list.forEach( r -> {
                System.out.println(r);
                repo.save(new ReportAuthorRating(new User(r.getAuthorId()),
                            r.getAverage(), (byte) r.getPostWritten(),r.getActually())
            );}
        );
        }


    }

}
