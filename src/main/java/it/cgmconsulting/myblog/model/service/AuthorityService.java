package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.entity.Authority;
import it.cgmconsulting.myblog.model.repository.AuthorityRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthorityService {

    private final AuthorityRepository authorityRepository;
    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public Optional<Authority> findByAuthorityName(String authorityName){
        return authorityRepository.findByAuthorityName(authorityName);
    }

    public Set<Authority> findByAuthorityNameIn(Set<String> authorities){
        return authorityRepository.findByAuthorityNameIn(authorities);
    }

}
