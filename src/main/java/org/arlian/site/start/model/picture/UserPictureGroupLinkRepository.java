package org.arlian.site.start.model.picture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserPictureGroupLinkRepository extends JpaRepository<UserPictureGroupLink, Long> {

    @Query("SELECT upgl FROM UserPictureGroupLink upgl " +
            "WHERE upgl.pictureGroup.id = ?1 and upgl.user.id = ?2 " +
            "and (upgl.role = org.arlian.site.start.model.picture.UserPictureGroupRole.OWNS or " +
            "       upgl.role = org.arlian.site.start.model.picture.UserPictureGroupRole.SHARES)")
    Optional<UserPictureGroupLink> pictureGroupIdAndUserIdViewableMatch(long pictureGroupId, long userId);
}
