package org.arlian.site.model.start.page;

import org.arlian.site.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {

    @Query("SELECT p FROM Page p WHERE p.user = ?1 and p.isDefault = true")
    Optional<PageNameProjection> findDefaultNameByUser(User user);

    Optional<Page> findByUserAndName(User user, String pageName);

    List<PageNameProjection> findByUser(User user);
}
