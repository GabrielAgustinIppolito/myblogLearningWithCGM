package it.cgmconsulting.myblog.model.data.entity;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.AvatarId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.*;

import java.util.Objects;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Avatar {
    // Embedded Id significa che la primary key sarà un altra classe
    @EmbeddedId
    private AvatarId avatarId;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String filetype;

    @Lob // --> da un dato di tipo tinyblob sul db
    @Column(nullable = false, columnDefinition = "BLOB") //, columnDefinition = "BLOB") --> aumenta la dimensione
    private byte[] data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Avatar avatar = (Avatar) o;
        return Objects.equals(getAvatarId(), avatar.getAvatarId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAvatarId());
    }
}
