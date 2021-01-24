package org.arlian.site.start.model.picture;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PictureGroup {

    /**
     * Technical and meaningless ID serving as primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "picture_group_id_seq")
    @SequenceGenerator(name="picture_group_id_seq",
            sequenceName = "picture_group_id_seq", allocationSize = 5)
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

    @OneToMany(mappedBy = "pictureGroup", orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserPictureGroupLink> userPictureGroupLinks = new ArrayList<>();

    @OneToMany(mappedBy = "pictureGroup", orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Picture> pictures = new ArrayList<>();

    public void addUserPictureGroupLink(UserPictureGroupLink userPictureGroupLink) {
        userPictureGroupLinks.add(userPictureGroupLink);
    }
}
