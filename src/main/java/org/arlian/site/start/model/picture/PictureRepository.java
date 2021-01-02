package org.arlian.site.start.model.picture;

import org.arlian.site.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Long> {


    List<PictureIdProjection> findByUser(User proxyUser);
}
