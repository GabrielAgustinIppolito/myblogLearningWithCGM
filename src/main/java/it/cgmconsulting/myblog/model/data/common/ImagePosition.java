package it.cgmconsulting.myblog.model.data.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ImagePosition {
    /*
    * PREview = immagine che serve per l'anteprima
    * HeaDeR = immagine per l'header del dettaglio del post
    * CONtent = immagine per il contenuto nel post
    * */
    PRE(200,200,10240, 1, "Preview"),
    HDR(600,300, 102400,1, "Header"),
    CON(400,200,20480, 5, "Content");

    public final int height;
    public final int width;
    public final long size;
    public final int maxImages;
    public final String description;

}
