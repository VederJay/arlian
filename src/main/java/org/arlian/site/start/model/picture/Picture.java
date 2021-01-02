package org.arlian.site.start.model.picture;

import lombok.*;
import org.arlian.site.user.model.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Picture {

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "picture_id_seq")
    @SequenceGenerator(name="picture_id_seq",
            sequenceName = "picture_id_seq", allocationSize = 5)
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
     * The actual image
     */
    private byte[] image;


    //*******************
    // RELATED ENTITIES *
    //*******************

    /**
     * The user to which this picture belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
