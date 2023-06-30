package it.cgmconsulting.myblog.model.data.payload.response;

import it.cgmconsulting.myblog.model.data.entity.Avatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthorResponse {
    private long id;
    private String username;
    private String filename;
    private String filetype;
    private byte[] data;
    private String bio;
    private long writtenPosts; // numero di post scritti e pubblicati
}
