package org.arlian.site.start.model.settings;

import org.arlian.site.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SettingsRepository extends JpaRepository<Settings, Long> {

    Optional<Settings> findByUser(User user);
}
