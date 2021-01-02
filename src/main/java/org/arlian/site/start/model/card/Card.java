package org.arlian.site.start.model.card;

import lombok.*;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.user.model.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter @NoArgsConstructor @Builder
@AllArgsConstructor
public class Card {

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_id_seq")
    @SequenceGenerator(name="card_id_seq",
            sequenceName = "card_id_seq", allocationSize = 5)
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
     * Title of the card
     */
    private String title;

    /**
     * Type of the card (see CardType)
     */
    private CardType type;

    /**
     * Position of the card on the page (0= left, 2=right, 1=middle)
     */
    private int position;

    /**
     * Order of the card in the given position
     */
    private int orderNumber;


    //*******************
    // RELATED ENTITIES *
    //*******************

    @ManyToOne(fetch = FetchType.LAZY)
    private Page page;

    @OneToMany(mappedBy = "card", orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orderNumber ASC")
    private List<Link> links = new ArrayList<>();

    /**
     * The user to which this card belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    //**********************
    // GETTERS AND SETTERS *
    //**********************

    public void addLink(Link link){
        if(this.links == null)
            this.links = new ArrayList<>();

        this.links.add(link);
        link.setCard(this);
    }

    public void removeLink(Link link){
        this.links.remove(link);
    }
}
