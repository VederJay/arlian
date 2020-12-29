package org.arlian.site.start.service;

import org.arlian.site.start.model.card.Card;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final UserService userService;

    public CardService(UserService userService) {
        this.userService = userService;
    }

    public boolean cardBelongsToUser(Card card, Authentication authentication) {
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        return card.getUser().getId() == userIdProjection.getId();
    }
}
