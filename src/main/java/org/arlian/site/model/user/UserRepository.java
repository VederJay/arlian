package org.arlian.site.model.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    <T> T findByEmailAddress(String emailAddress, Class <T> type);
}
