package org.arlian.site.start.model.link;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    @Query("SELECT l FROM Link l WHERE l.id = ?1")
    Optional<ImageLinkThumbnail> findThumbnailById(long linkId);
}
