package it.cgmconsulting.myblog.model.repository;

import it.cgmconsulting.myblog.model.data.entity.Avatar;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.data.payload.response.AuthorResponse;
import it.cgmconsulting.myblog.model.data.payload.response.GetMeResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameOrEmail(String username, String email);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByConfirmCode(String confirmCode);

    /*Obbligatorie quando faccio delle query che modificano*/
    @Modifying
    @Transactional
    @Query(value = "UPDATE user u SET u.password= :newPwd, updated_at = :now WHERE id = :id", nativeQuery = true)
    void changePwd(@Param("id") long id, @Param("newPwd") String newPwd, @Param("now")LocalDateTime now);

    /* JPQL = Java Persistent Query Language*/
    @Query(value = "SELECT new it.cgmconsulting.myblog.model.data.payload.response.GetMeResponse(" +
            "u.id, " +
            "u.username, " +
            "u.email, " +
            "u.bio, " +
            "CAST(u.createdAt as LocalDate) as createdAt, " +
            "a.filename, " +
            "a.filetype, " +
            "a.data" +
            ") FROM User u " +
            "LEFT JOIN Avatar a ON a.avatarId.user.id = u.id " +
            "WHERE u.id = :id ")
    GetMeResponse getMe(@Param("id") long id);

    @Query(value = """
                   SELECT new it.cgmconsulting.myblog.model.data.payload.response.AuthorResponse(
                   u.id, 
                   u.username, 
                   a.filename, 
                   a.filetype, 
                   a.data, 
                   u.bio, 
                   (SELECT COUNT(p.id) FROM Post p WHERE p.author.id = u.id
                   AND (p.publishedAt IS NOT NULL AND p.publishedAt < :now )) as writtenPost
                   ) FROM User u 
                   LEFT JOIN Avatar a ON a.avatarId.user.id = u.id 
                   INNER JOIN u.authorities auth 
                   WHERE auth.authorityName = :authorityName 
                   """)
    List<AuthorResponse> getUsersByAuthority(@Param("authorityName") String authorityName,
                                             @Param("now") LocalDateTime now);
}