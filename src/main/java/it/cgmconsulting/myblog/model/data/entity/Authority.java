package it.cgmconsulting.myblog.model.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @ToString
//@EqualsAndHashCode
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement
//    @EqualsAndHashCode.Include
    private long id;

//    @EqualsAndHashCode.Exclude --> in automatico lo esclude se c'è un include
    @Column(length = 20, nullable = false, unique = true)
    private String authorityName; //atuthority_name on db

    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return getId() == authority.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
