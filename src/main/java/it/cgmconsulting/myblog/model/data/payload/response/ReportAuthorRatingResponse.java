package it.cgmconsulting.myblog.model.data.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.time.YearMonth;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ReportAuthorRatingResponse {

    private long id;
    private long authorId;
    private String authorUsername;
    private double average;
    private long postWritten;
    private LocalDate actually;
//    private YearMonth yearMonth;

    public ReportAuthorRatingResponse(long authorId, String authorUsername, double average, long postWritten) {
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.average = average;
        this.postWritten = postWritten;
        this.actually = LocalDate.now().minusMonths(1);
    }

//    public ReportAuthorRatingResponse(long authorId, double average, long postWritten) {
//        this.authorId = authorId;
//        this.average = average;
//        this.postWritten = postWritten;
//        this.yearMonth = YearMonth.now().minusMonths(1);
//    }
}
