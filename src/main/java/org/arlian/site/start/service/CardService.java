package org.arlian.site.start.service;

import org.arlian.site.generic.model.BadRequestException;
import org.arlian.site.start.model.card.Card;
import org.arlian.site.start.model.card.CardRepository;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.link.LinkRepository;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final UserService userService;
    private final CardRepository cardRepository;
    private final LinkRepository linkRepository;

    public CardService(UserService userService, CardRepository cardRepository, LinkRepository linkRepository) {
        this.userService = userService;
        this.cardRepository = cardRepository;
        this.linkRepository = linkRepository;
    }

    public boolean cardBelongsToUser(Card card, Authentication authentication) {
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        return card.getUser().getId() == userIdProjection.getId();
    }

    public void addLinkToCardIfOwnedByUser(long cardId, Link link, Authentication authentication) throws BadRequestException {

        Card card = cardRepository.findById(cardId).orElseThrow(BadRequestException::new);

        if(cardBelongsToUser(card, authentication)){
            card.addLink(link);
            cardRepository.save(card);
            linkRepository.save(link);
        }
        else
            throw new BadRequestException();
    }
}
