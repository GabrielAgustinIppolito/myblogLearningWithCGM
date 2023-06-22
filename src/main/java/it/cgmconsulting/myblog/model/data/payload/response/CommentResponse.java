package it.cgmconsulting.myblog.model.data.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommentResponse {

    private long id;
    private String comment;
    private String author; //Solo username
    private LocalDateTime createdAt;
    private Long parentId;

}
