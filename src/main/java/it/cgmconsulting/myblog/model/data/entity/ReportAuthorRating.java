package it.cgmconsulting.myblog.model.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ReportAuthorRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "author", nullable = false)
    private User author;
    private double average;
    private byte postWritten;
    private LocalDate actually;

//    private YearMonth annoMese; //Sul db si salva come binary, va bene  o da salvare come string?

    public ReportAuthorRating(User author, double average, byte postWritten, LocalDate actually) {
        this.author = author;
        this.average = average;
        this.postWritten = postWritten;
        this.actually = actually;
    }
//
//    public ReportAuthorRating(User author, double average, byte postWritten, YearMonth annoMese) {
//        this.author = author;
//        this.average = average;
//        this.postWritten = postWritten;
//        this.annoMese = annoMese;
//    }
}
