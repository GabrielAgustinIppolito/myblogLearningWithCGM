package it.cgmconsulting.myblog.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.cgmconsulting.myblog.model.data.payload.request.ChangePwdRequest;
import it.cgmconsulting.myblog.model.data.payload.request.ChangeRoleRequest;
import it.cgmconsulting.myblog.model.data.payload.request.UpdateMeRequest;
import it.cgmconsulting.myblog.model.service.AuthService;
import it.cgmconsulting.myblog.model.service.AvatarService;
import it.cgmconsulting.myblog.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Validated
@SecurityRequirement(name = "myBlogSecurityScheme")
public class UserController {

    private final AuthService authService;
    private final AvatarService avatarService;

    @PutMapping("change-role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> changeRole(@RequestBody @Valid ChangeRoleRequest request,
                                        @AuthenticationPrincipal UserPrincipal principal){
        // verifivare che l'utente loggato non stia cambiando il proprio ruolo (lato service)
        return authService.changeRole(request, principal);
    }

    @PutMapping("change-pwd")
    public ResponseEntity<?> changePwd(@RequestBody @Valid ChangePwdRequest request,
                                       @AuthenticationPrincipal UserPrincipal principal){
        return authService.changePwd(request, principal);
    }

    @PutMapping("update-me")
    public ResponseEntity<?> updateMe(@AuthenticationPrincipal UserPrincipal principal,
                                      @RequestBody @Valid UpdateMeRequest updateMeRequest){
        return authService.updateMe(updateMeRequest, principal);
    }

    @PostMapping(value = "avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> avatar(@AuthenticationPrincipal UserPrincipal principal,
                                      @RequestParam MultipartFile file) throws IOException { //per il file singolo, per array RequestPart
        return avatarService.avatar(file, principal);
    }

    @GetMapping("get-me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserPrincipal principal){
        return authService.getMe(principal);
    }

    @GetMapping("public/get-authors")
    public ResponseEntity<?> getAllAuthors(@RequestParam(defaultValue = "ROLE_WRITER") String authorityName){
        return authService.getAllAuthors(authorityName);
    }


}
