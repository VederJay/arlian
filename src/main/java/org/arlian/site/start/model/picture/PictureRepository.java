package org.arlian.site.start.model.picture;

import org.arlian.site.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {


    @Query("SELECT pic FROM Picture pic " +
            "INNER JOIN FETCH pic.pictureGroup picGroup " +
            "INNER JOIN FETCH picGroup.userPictureGroupLinks " +
            "WHERE pic.id = ?1")
    Optional<Picture> findByIdWithGroups(long pictureId);

    @Query("SELECT pic.id FROM Picture pic " +
            "INNER JOIN pic.pictureGroup picGroup " +
            "INNER JOIN picGroup.userPictureGroupLinks links " +
            "on links.user = ?1 and links.role = ?3 " +
            "WHERE pic.orientation = ?2 ")
    List<Long> findByUserAndOrientationAndRole(User proxyUser, Orientation horizontal, UserPictureGroupRole owns);

    <T> Optional<T> findById(long pictureId, Class<T> type);
}
