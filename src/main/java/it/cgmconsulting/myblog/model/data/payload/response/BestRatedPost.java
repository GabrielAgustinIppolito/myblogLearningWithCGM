package it.cgmconsulting.myblog.model.data.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class BestRatedPost {
    private long id;
    private String title;
    private double average;
}
