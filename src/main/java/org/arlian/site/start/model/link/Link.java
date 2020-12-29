package org.arlian.site.start.model.link;

import lombok.*;
import org.arlian.site.start.model.card.Card;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Getter @Setter
@NoArgsConstructor @Builder
@AllArgsConstructor
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

}
