package org.arlian.site.start.model.picture;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.arlian.site.user.model.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserPictureGroupLink {

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "upgl_id_seq")
    @SequenceGenerator(name="upgl_id_seq",
            sequenceName = "upgl_id_seq", allocationSize = 5)
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


    //*******************
    // RELATED ENTITIES *
    //*******************

    /**
     * The user in the link.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    /**
     * The role the user has with respect to the picture group.
     */
    private UserPictureGroupRole role;

    /**
     * The picture group in the link.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private PictureGroup pictureGroup;

}
