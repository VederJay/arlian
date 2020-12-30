package org.arlian.site.user.service;

import org.arlian.site.user.model.User;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.model.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserIdProjection getUserFromAuthentication(Authentication authentication){
        DefaultOidcUser defaultOidcUser = (DefaultOidcUser) authentication.getPrincipal();
        String emailAddress = defaultOidcUser.getAttribute("email");

        // Try to find the user in the database
        UserIdProjection user = userRepository.findByEmailAddress(emailAddress, UserIdProjection.class);

        // If the user doesn't exist, create a new one
        if(user == null){

            User newUser = User.builder()
                    .emailAddress(emailAddress)
                    .givenName(defaultOidcUser.getAttribute("given_name"))
                    .fullName(defaultOidcUser.getAttribute("name"))
                    .pictureUrl(defaultOidcUser.getAttribute("picture"))
                    .build();

            userRepository.save(newUser);

            user = newUser;
        }

        return user;
    }
}
