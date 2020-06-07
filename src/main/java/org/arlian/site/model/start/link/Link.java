package org.arlian.site.model.start.link;

import org.arlian.site.model.start.card.Card;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Link {

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_id_seq")
    @SequenceGenerator(name="link_id_seq",
            sequenceName = "link_id_seq", allocationSize = 5)
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
     * Title of the link
     */
    private String title;

    /**
     * Url to which the link refers
     */
    private String url;

    /**
     * Order of the link in the card
     */
    private int orderNumber;


    /********************
     * RELATED ENTITIES *
     ********************/

    @ManyToOne(fetch = FetchType.LAZY)
    private Card card;


    /****************
     * CONSTRUCTORS *
     ****************/

    public Link(){}

    public Link(String title, String url){
        this.title = title;
        this.url = url;
    }


    /***********************
     * GETTERS AND SETTERS *
     ***********************/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
