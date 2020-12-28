package org.arlian.site.model.user;

import lombok.*;
import org.arlian.site.model.start.card.Card;
import org.arlian.site.model.start.page.Page;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User implements UserIdProjection{

    /**
     * Short name the user likes to be called by.
     */
    private String givenName;

    /**
     * Full name of the user, consisting of first name and surname.
     */
    private String fullName;

    /**
     * Email address of the user.
     */
    private String emailAddress;

    /**
     * Url to a profile picture of the user.
     */
    private String pictureUrl;

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name="user_id_seq",
            sequenceName = "user_id_seq", allocationSize = 5)
    private long id;

    /**
     * LocalDateTime of when the object was first persisted.
     */
    @CreationTimestamp
    private LocalDateTime createDateTime;

    /**
     * LocalDateTime of when the object was most recently updated.
     */
    @UpdateTimestamp
    private LocalDateTime updateDateTime;


    /********************
     * RELATED ENTITIES *
     ********************/

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Page> pages = new ArrayList<>();

    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Card> cards = new ArrayList<>();


    /***********************
     * GETTERS AND SETTERS *
     ***********************/

    public void addPage(Page page){
        this.pages.add(page);
        page.setUser(this);
    }

    public void removePage(Page page){
        this.pages.remove(page);
    }
}
