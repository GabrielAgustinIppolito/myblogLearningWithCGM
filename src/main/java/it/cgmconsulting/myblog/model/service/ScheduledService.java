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
import java.util.ArrayList;
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
//        if(!list.isEmpty()){
//            list.forEach( r -> {
//                System.out.println(r);
//                repo.save(new ReportAuthorRating(new User(r.getAuthorId()),
//                            r.getAverage(), (byte) r.getPostWritten(),r.getActually())
//            );}
//        );
//        }
        List<ReportAuthorRating> lr = new ArrayList<>();

        list.forEach( r -> {
            lr.add(new ReportAuthorRating(new User(r.getAuthorId()),
                            r.getAverage(), (byte) r.getPostWritten(),r.getActually())
            );}
        );
        repo.saveAll(lr); // PIù OTTIMALE PER LA RESA DEL DB


    }

}
/*
* **********************Codice Metodo*************************************
System.out.println(" #### SCHEDULAZIONE MENSILE PER AUTHOR RATING #### ");
        LocalDate dataOdierna = LocalDate.now();
        // Ottieni il mese corrente
        int meseCorrente = dataOdierna.getMonthValue() - 1;
        int annoCorrente = dataOdierna.getYear();

        LocalDate primoGiorno = LocalDate.of(annoCorrente, meseCorrente, 1);
        LocalDate ultimoGiorno = primoGiorno.withDayOfMonth(primoGiorno.lengthOfMonth());

        List<ReportAuthorRatingResponse> listAuthorRating = reportAuthorRatingRepository.checkRatingAuthorWriterPost(primoGiorno,ultimoGiorno);
        List<ReportAuthorRating> listAuthorToSave =new ArrayList<>();

        if(!listAuthorRating.isEmpty()){
            for (ReportAuthorRatingResponse rarr : listAuthorRating){
                listAuthorToSave.add(new ReportAuthorRating(new User(rarr.getAuthor_id()), rarr.getAverage(), rarr.getPostWritten(), LocalDate.now()));
            }
        }else {
            log.info("Non ci sono informazioni valide");
        }

        if(!listAuthorToSave.isEmpty()){
            reportAuthorRatingRepository.saveAll(listAuthorToSave);
        }else {
            log.info("Non ci sono informazioni valide da salvare");
        }


        // la schedulazione parte ogni primo del mese
        // report: deve restituire tutti gli author che hanno scritto qualcosa nel mese precedente
        // e scrivere sul db questa 'classifica'
        // AuthorWrittenPost
        // ETL -> Extract, Transform, Load (Tipico dei processi batch)
        // Extract: Recupero dei dati da cercare
        // Transform: creo una lista di ReportAuthorRating
        // Load: 'caricarli' da qua qualche parle (la tabella report_author_rating nel nostro caso)
****************************************************************************************************************

 @Query(value=
            "SELECT new DA INSERIRE IL PATH DEL VOSTRO PACKAGE.payload.response.ReportAuthorRatingResponse( " +
                    "u.id, " +
                    "(SELECT COALESCE(ROUND(AVG(r.rate),2), 0.0) FROM Rating r WHERE r.ratingId.post.id = p.id) as average, " +
                    "(SELECT COUNT(pCount.id) FROM Post pCount WHERE pCount.author_id.id = u.id AND pCount.publishedAt IS NOT NULL AND pCount.publishedAt BETWEEN :dateStart AND :dateEnd) as postWritten " +
                    ")FROM User u  " +
                    "LEFT JOIN Post p ON (p.author_id.id =  u.id) " +
                    "WHERE p.publishedAt IS NOT NULL AND p.publishedAt <> '' " +
                    "AND p.publishedAt BETWEEN :dateStart AND :dateEnd " +
                    "GROUP BY u.id"
    )
 List<ReportAuthorRatingResponse> checkRatingAuthorWriterPost(@Param("dateStart")LocalDate dateStart,@Param("dateEnd")LocalDate dateEnd);
 *
 *
 *
 * //@Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS) // unità di misura di default millisecondi
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    //@Scheduled(cron = "0 58 12 30 * *" )  // 0   30   9   7   *   * every month on 7th day at 9:30 AM
    //@Scheduled(cron = "0 0 1 1 * *" ) // All'una di notte di ogni primo del mese
    public void generateReportAuthorRating(){
        System.out.println(" #### SCHEDULAZIONE MENSILE PER AUTHOR RATING #### ");
        // la schedulazione parte ogni primo del mese
        // report: deve restituire tutti gli author che hanno scritto qualcosa nel mese precedente
        // e scrivere sul db questa 'classifica'

        // ETL -> Extract, Transform, Load (Tipico dei processi batch)
        // Extract: Recupero dei dati da cercare
        // Transform: creo una lista di ReportAuthorRating
        // Load: 'caricarli' da qua qualche parle (la tabella report_author_rating nel nostro caso)

        LocalDateTime dataOdierna = LocalDateTime.now();
        LocalDateTime primoGiorno = dataOdierna.minusMonths(1);
        LocalDateTime ultimoGiorno = dataOdierna.minusDays(1);

        List<ReportAuthorRatingResponse> listAuthorRating = userRepository.checkRatingAuthorWriterPost(primoGiorno, ultimoGiorno);
        List<ReportAuthorRating> rar = new ArrayList<>();
        for(ReportAuthorRatingResponse r : listAuthorRating){
            rar.add(new ReportAuthorRating(new User(r.getId()), r.getAverage(), r.getWittenPosts().byteValue(), LocalDate.now()));
            log.info(r.toString());
        }
        reportAuthorRatingRepository.saveAll(rar);

    }
*
*
*
*
* @Query(value = "SELECT new it.cgmconsulting.myblog.payload.response.ReportAuthorRatingResponse(" +
            "p.author.id, p.author.username, " +
            "COALESCE(AVG(r.rate),0.0), " +
            "(SELECT COUNT(a.id) FROM Post a WHERE (p.author.id = a.author.id) AND (p.publishedAt IS NOT NULL AND a.publishedAt BETWEEN :dateStart AND :dateEnd) GROUP BY a.author.id) " +
            ") FROM Post p " +
            "LEFT JOIN Rating r ON p.id = r.ratingId.post.id " +
            "WHERE r.updatedAt BETWEEN :dateStart AND :dateEnd " +
            "GROUP BY p.author.id, p.author.username")
//     funzionanate ma meno efficiente in virtù di un sub-query in più
//             @Query(value=
//                "SELECT new it.cgmconsulting.myblog.payload.response.ReportAuthorRatingResponse(" +
//                "u.id, u.username, " +
//                "(SELECT COALESCE(ROUND(AVG(r.rate),2), 0.0) FROM Rating r WHERE r.ratingId.post.author.id = p.author.id AND r.updatedAt BETWEEN :dateStart AND :dateEnd) as average, " +
//                "(SELECT COUNT(pCount.id) FROM Post pCount WHERE pCount.author.id = u.id AND pCount.publishedAt IS NOT NULL AND pCount.publishedAt BETWEEN :dateStart AND :dateEnd) as postWritten " +
//                ") FROM User u  " +
//                "LEFT JOIN Post p ON (p.author.id =  u.id) " +
//                "WHERE p.publishedAt IS NOT NULL  " +
//                "AND (p.publishedAt BETWEEN :dateStart AND :dateEnd) " +
//                "GROUP BY u.id,u.username"
//    )

List<ReportAuthorRatingResponse> checkRatingAuthorWriterPost(@Param("dateStart")LocalDateTime dateStart, @Param("dateEnd")LocalDateTime dateEnd);*/

