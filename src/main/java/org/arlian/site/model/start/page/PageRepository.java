package org.arlian.site.model.start.page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PageRepository extends JpaRepository<Page, Long> {

    @Query("SELECT p FROM Page p WHERE p.user.id = ?1")
    PageNameProjection findDefaultNameByUserId(long userId);

    @Query("SELECT p FROM Page p WHERE p.user.id = ?1 and p.name = ?2")
    Page findByUserIdAndName(long userId, String pageName);
}
