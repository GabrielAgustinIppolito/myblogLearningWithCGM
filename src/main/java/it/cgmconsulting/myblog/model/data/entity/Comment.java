package it.cgmconsulting.myblog.model.data.entity;

import it.cgmconsulting.myblog.model.data.common.Creation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
//@Table(name = "comment", schema = "myblog_corso") con schema gli dico che è una tabella del db myblog_corso
@Getter @Setter @NoArgsConstructor @ToString
public class Comment extends Creation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @OneToOne
    @JoinColumn(name="author", nullable = false)
    private User author;

    @Column(nullable = false)
    private String comment;

    private boolean censored;

    @ManyToOne
    @JoinColumn(name="parent")
    private Comment parent;

    public Comment(Post post, User author, String comment) {
        this.post = post;
        this.author = author;
        this.comment = comment;
    }

    public Comment(Post post, User author, String comment, Comment parent) {
        this.post = post;
        this.author = author;
        this.comment = comment;
        this.parent = parent;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return getId() == comment.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
