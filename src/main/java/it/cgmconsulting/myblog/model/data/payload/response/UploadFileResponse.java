package it.cgmconsulting.myblog.model.data.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UploadFileResponse {

    private String imageDestination; // pre, hdr,con
    private String fileName;
    private String message;
    private String error;

}
