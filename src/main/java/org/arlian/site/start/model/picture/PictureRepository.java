package org.arlian.site.start.model.picture;

import org.arlian.site.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {


    @Query("SELECT pic FROM Picture pic " +
            "INNER JOIN pic.pictureGroup picGroup INNER JOIN picGroup.userPictureGroupLinks links " +
            "on links.user = ?1 and links.role in (org.arlian.site.start.model.picture.UserPictureGroupRole.OWNS, " +
            "org.arlian.site.start.model.picture.UserPictureGroupRole.SHARES) " +
            "WHERE pic.orientation = ?2 ")
    List<PictureIdAndOrientationProjection> findByUserAndOrientation(User proxyUser, Orientation orientation);


}
