package it.cgmconsulting.myblog.model.data.payload.response;

import it.cgmconsulting.myblog.model.data.entity.Avatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class GetMeResponse {

    private long id;
    private String username;
    private String email;
    private String bio;
    private LocalDate createdAt;    // da riportare: yyyy-mm-dd
    private String filename;
    private String filetype;
    private byte[] data;
/*Facendolo fare al db evito di farlo qua --> CAST(u.createdAt as LocalDate) as createdAt,*/
//    public GetMeResponse(long id, String username, String email, String bio, LocalDateTime createdAt, String filename,
//                         String filetype, byte[] data) {
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.bio = bio;
//        this.createdAt = createdAt.toLocalDate();
//        this.filename = filename;
//        this.filetype = filetype;
//        this.data = data;
//    }

}
