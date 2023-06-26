package it.cgmconsulting.myblog.model.data.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class PostSearchResponse {
    private long postId;
    private String title;
    private String overview;
    private LocalDateTime publishedAt;
    private String author;
    @JsonIgnore
    private String content;
}
