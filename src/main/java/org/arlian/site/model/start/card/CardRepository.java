package org.arlian.site.model.start.card;

import org.arlian.site.model.start.link.Link;
import org.arlian.site.model.start.page.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByPageAndPositionOrderByOrderNumber(Page page, int position);

    @Query("SELECT c FROM Card c WHERE ?1 MEMBER OF c.links")
    Optional<Card> findByLink(Link link);
}
