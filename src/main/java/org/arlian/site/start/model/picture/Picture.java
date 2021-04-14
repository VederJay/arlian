package org.arlian.site.start.model.picture;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
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
    private byte[] originalImage;


    /**
     * The actual image - reduced in size for viewing
     */
    private byte[] reducedImage;


    /**
     * The thumbnail version of the image
     */
    private byte[] thumbnail;

    /**
     * The orientation of the image
     */
    private Orientation orientation;



    //*******************
    // RELATED ENTITIES *
    //*******************


    /**
     * The picture group to which this picture belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private PictureGroup pictureGroup;
}
