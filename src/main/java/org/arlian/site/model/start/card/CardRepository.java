package org.arlian.site.model.start.card;

import org.arlian.site.model.start.page.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByPageAndPositionOrderByOrderNumber(Page page, int position);
}
