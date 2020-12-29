package org.arlian.site.start.model.page;

import lombok.*;
import org.arlian.site.start.model.card.Card;
import org.arlian.site.user.model.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@Setter
@NoArgsConstructor @Builder
@AllArgsConstructor
public class Page {

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "page_id_seq")
    @SequenceGenerator(name="page_id_seq",
            sequenceName = "page_id_seq", allocationSize = 5)
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


    /**
     * Name of the page.
     */
    private String name;

    /**
     * Indicates whether the page is the default page for the user, i.e. the first one shown
     * when the user logs on.
     */
    private boolean isDefault;


    /********************
     * RELATED ENTITIES *
     ********************/

    /**
     * The user to which this page belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /**
     * Cards to be displayed on the page.
     */
    @OneToMany(mappedBy = "page", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Card> cards = new ArrayList<>();



    /***********************
     * GETTERS AND SETTERS *
     ***********************/

    public void addCard(Card card) {
        if(this.cards == null)
            this.cards = new ArrayList<>();
        this.cards.add(card);
        card.setPage(this);
        card.setUser(this.user);
    }

    public void removeCard(Card card){
        this.cards.remove(card);
    }
}
