package org.arlian.site.start.model.picture;

import org.arlian.site.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PictureGroupRepository extends JpaRepository<PictureGroup, Long> {

    @Query("SELECT pg FROM PictureGroup pg INNER JOIN pg.userPictureGroupLinks link " +
            "on link.role = org.arlian.site.start.model.picture.UserPictureGroupRole.OWNS " +
            "and link.user = ?1")
    Optional<PictureGroup> findOwnedByUser(User proxyUserFromAuthentication);
}
