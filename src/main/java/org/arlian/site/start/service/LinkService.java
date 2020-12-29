package org.arlian.site.start.service;

import org.arlian.site.start.model.card.Card;
import org.arlian.site.start.model.card.CardRepository;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LinkService {

    private final CardRepository cardRepository;
    private final UserService userService;

    public LinkService(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    public boolean linkBelongsToUser(Link link, Authentication authentication) {

        // Check if the link is in a card
        Optional<Card> optionalCard = cardRepository.findByLink(link);
        if(optionalCard.isEmpty())
            return false;

        // Check if the card belongs to the user
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        return optionalCard.get().getUser().getId() == userIdProjection.getId();
    }
}
