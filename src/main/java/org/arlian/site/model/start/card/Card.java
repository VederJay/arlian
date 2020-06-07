package org.arlian.site.model.start.card;

import org.arlian.site.model.start.link.Link;
import org.arlian.site.model.start.page.Page;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
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


    /********************
     * RELATED ENTITIES *
     ********************/

    @ManyToOne(fetch = FetchType.LAZY)
    private Page page;

    @OneToMany(mappedBy = "card", orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("orderNumber ASC")
    private List<Link> links = new ArrayList<>();


    /****************
     * CONSTRUCTORS *
     ****************/

    public Card(){}

    public Card(String title) {
        this.title = title;
    }


    /***********************
     * GETTERS AND SETTERS *
     ***********************/

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void addLink(Link link){
        this.links.add(link);
        link.setCard(this);
    }

    public void removeLink(Link link){
        this.links.remove(link);
    }
}
