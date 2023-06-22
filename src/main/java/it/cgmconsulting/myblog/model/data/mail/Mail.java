package it.cgmconsulting.myblog.model.data.mail;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Mail {

    private String mailFrom;
    private String mailTo;
    private String mailSubject;
    private String mailContent;

}
