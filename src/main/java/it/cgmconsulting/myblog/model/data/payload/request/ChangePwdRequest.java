package it.cgmconsulting.myblog.model.data.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class ChangePwdRequest {

    @NotBlank
    private String oldPassword;
    @Pattern(regexp = "^[a-zA-Z0-9]{6,10}",
    message = "La password può contenerere solo numeri, caratteri maiuyìscili e minuscoli. " +
            "La lungheza deve essere compresa tra 6 e 10")
    private String newPassword1;
    private String newPassword2;
}
