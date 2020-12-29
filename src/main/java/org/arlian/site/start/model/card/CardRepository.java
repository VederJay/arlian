package org.arlian.site.start.model.card;

import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.page.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByPageAndPositionOrderByOrderNumber(Page page, int position);

    @Query("SELECT c FROM Card c WHERE ?1 MEMBER OF c.links")
    Optional<Card> findByLink(Link link);
}
