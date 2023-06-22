package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.model.data.entity.Authority;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.data.mail.Mail;
import it.cgmconsulting.myblog.model.data.mail.MailService;
import it.cgmconsulting.myblog.model.data.payload.request.*;
import it.cgmconsulting.myblog.model.data.payload.response.JwtAuthenticationResponse;
import it.cgmconsulting.myblog.model.repository.UserRepository;
import it.cgmconsulting.myblog.security.JwtTokenProvider;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value(("${app.mail.sender}"))
    private String from;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;

    protected boolean existsByUsernameOrEmail(String username, String email) {
        return userRepository.existsByUsernameOrEmail(username, email);
    }

    protected User fromRequestToEntity(SignUpRequest request) {
        return new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
    }

    protected User save(User user) {
        userRepository.save(user);
        return user;
    }

    // Utente abilitato all'atto della registrazione
    public ResponseEntity<?> signup(SignUpRequest request) {
        if (existsByUsernameOrEmail(request.getUsername(), request.getEmail()))
            return new ResponseEntity<>("Username or email already in use", HttpStatus.BAD_REQUEST);
        User u = fromRequestToEntity(request);
        Optional<Authority> a = authorityService.findByAuthorityName("ROLE_READER"); //commentare se scommento sopra
        if (!a.isPresent()) return new ResponseEntity<>("Something went wrong during registration",
                HttpStatus.UNPROCESSABLE_ENTITY);
        u.getAuthorities().add(a.get());
        u.setEnabled(true); //da commentare quando si scommenta quello sotto
        save(u);
        return new ResponseEntity<>("Signup successfully completed", HttpStatus.OK);
    }

    // Utente registrato da abilitare
//    public ResponseEntity<?> signup(SignUpRequest request){
//        if(existsByUsernameOrEmail(request.getUsername(), request.getEmail()))
//            return new ResponseEntity<>("Username or email already in use", HttpStatus.BAD_REQUEST);
//        User u = fromRequestToEntity(request);
//        Optional<Authority> a = authorityService.findByAuthorityName("ROLE_GUEST"); scommentare per reg con conferma
//        Optional<Authority> a = authorityService.findByAuthorityName("ROLE_READER"); //commentare se scommento sopra
//        if(!a.isPresent()) return new ResponseEntity<>("Something went wrong during registration",
//                                                              HttpStatus.UNPROCESSABLE_ENTITY);
//        u.getAuthorities().add(a.get());
//        u.setConfirmCode(UUID.randomUUID().toString());
//        save(u);
//        mailService.sendMail(new Mail(from, request.getEmail(),
//                "MyBlog: Please cofirm yuur registration",
//                "http://localhost:8081/auth/confirm/" + u.getConfirmCode()));
//        return new ResponseEntity<>("Signup successfully completed", HttpStatus.OK);
//    }

    /*  con questa notazione in automatico poi fa la save   *
     *  imposta tutto ciò che ha lo user in to many a eager */
    @Transactional
    public ResponseEntity<?> confirmRegistration(String confirmCode) {
        /* query sul db per confirmCode*/
        User u = userRepository.findByConfirmCode(confirmCode).orElseThrow(
                () -> new ResourceNotFoundException("User", "confirmCode", confirmCode));
        /*se lo trovo abilito user, cambio il ruolo in ROLE_READER, annullo confirmCode e salvo*/
        u.setEnabled(true);
        u.setConfirmCode(null);
        Optional<Authority> a = authorityService.findByAuthorityName("ROLE_READER");
        if (!a.isPresent()) return new ResponseEntity<>("Something went wrong during registration",
                HttpStatus.UNPROCESSABLE_ENTITY);
        /*vado a eliminare il role guest e metto role reader, per farlo sovrascrivo il set
         * con un altro set che contiene un solo elemento*/
        u.setAuthorities(Collections.singleton(a.get()));
        /*altrimenti avviso lo user */
        mailService.sendMail(new Mail(from, u.getEmail(),
                "MyBlog: TANK you for registration",
                "west YfeC19K"));

        return new ResponseEntity<>("Thank you for registration", HttpStatus.OK);
    }

    public ResponseEntity<?> signin(SignInRequest request) {
        /* Verificare esistenza user e correttezza password */
        User u = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User", "confirmCode", request.getUsernameOrEmail())
                );
        if (!passwordEncoder.matches(request.getPassword(), u.getPassword())) {
            return new ResponseEntity<>("Wrong username or password", HttpStatus.FORBIDDEN);
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        /* Generare response con JWT                        */
        String jwt = JwtTokenProvider.generateToken(authentication);
        JwtAuthenticationResponse currentUser = UserPrincipal.
                createJwtAuthenticationResponseFromUserPrincipal((UserPrincipal) authentication.getPrincipal(), jwt);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    public ResponseEntity<?> changeRole(ChangeRoleRequest request, UserPrincipal principal) {
        Set<Authority> authorities = authorityService.findByAuthorityNameIn(request.getNewAuthorities());
        if (principal.getId() == request.getId()) {
            return new ResponseEntity<>("Admin cannot change his own roles", HttpStatus.FORBIDDEN);
        }
        if (authorities.isEmpty()) {
            return new ResponseEntity<>("No valid authority selected", HttpStatus.BAD_REQUEST);
        }
        Optional<User> u = userRepository.findById(request.getId());
        if (u.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        u.get().setAuthorities(authorities);
        userRepository.save(u.get());
        return new ResponseEntity<>("Roles updated succesfuly", HttpStatus.OK);
    }

    public ResponseEntity<?> changePwd(ChangePwdRequest request, UserPrincipal principal) {
        if (!request.getNewPassword1().equals(request.getNewPassword2())) {
            return new ResponseEntity<>("Password mismatch", HttpStatus.BAD_REQUEST);
        }
        if (!passwordEncoder.matches(request.getOldPassword(), principal.getPassword())) {
            return new ResponseEntity<>("Wrong password", HttpStatus.BAD_REQUEST);

        }
        if (passwordEncoder.matches(request.getNewPassword1(), principal.getPassword())) {
            return new ResponseEntity<>("New password and old password are equals", HttpStatus.BAD_REQUEST);
        }

        userRepository.changePwd(principal.getId(),
                passwordEncoder.encode(request.getNewPassword1()),
                LocalDateTime.now());
        return new ResponseEntity<>("Password has been updated", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> updateMe(UpdateMeRequest updateMeRequest, UserPrincipal userPrincipal) {
        Optional<User> u = userRepository.findByEmail(updateMeRequest.getEmail());
        if (u.isPresent()) {
            if (u.get().getEmail().equals(updateMeRequest.getEmail())
                    && userPrincipal.getId() != u.get().getId()) {
                return new ResponseEntity<>("Email alredy in use", HttpStatus.FORBIDDEN);
            }
            u.get().setBio(updateMeRequest.getBio());
        } else {
            Optional<User> uu = userRepository.findById(userPrincipal.getId());
            uu.get().setEmail(updateMeRequest.getEmail());
            uu.get().setBio(updateMeRequest.getBio());
        }
        return new ResponseEntity<>("User info has been updated", HttpStatus.OK);
    }

    public ResponseEntity<?> getMe(UserPrincipal principal) {
        return new ResponseEntity<>(userRepository.getMe(principal.getId()), HttpStatus.OK);
    }
}