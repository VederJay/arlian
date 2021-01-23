package org.arlian.site.user.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    <T> T findByEmailAddress(String emailAddress, Class <T> type);

    User getUserById(long id);
}
