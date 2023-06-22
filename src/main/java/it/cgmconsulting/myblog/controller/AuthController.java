package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.model.data.payload.request.SignInRequest;
import it.cgmconsulting.myblog.model.data.payload.request.SignUpRequest;
import it.cgmconsulting.myblog.model.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("signup") // localhost:8081/auth/signup
    public ResponseEntity<?> signup(@RequestBody @Valid SignUpRequest request){
        return authService.signup(request);
    }

    @PutMapping("confirm/{confirmCode}")
    public ResponseEntity<?> confirmRegistration(@PathVariable @NotBlank String confirmCode){
        return authService.confirmRegistration(confirmCode);
    }

    @PostMapping("signin") // localhost:8081/auth/signup
    public ResponseEntity<?> signin(@RequestBody @Valid SignInRequest request){
        return authService.signin(request);
    }

}
