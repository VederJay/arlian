package org.arlian.site.model.start.page;

import org.arlian.site.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {

    @Query("SELECT p FROM Page p WHERE p.user = ?1 and p.isDefault = true")
    PageNameProjection findDefaultNameByUser(User user);

    Page findByUserAndName(User user, String pageName);

    List<PageNameProjection> findByUser(User user);
}
