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

import java.util.Optional;

@Service
public class LinkService {

    private final CardRepository cardRepository;
    private final UserService userService;
    private final LinkRepository linkRepository;

    public LinkService(CardRepository cardRepository, UserService userService, LinkRepository linkRepository) {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.linkRepository = linkRepository;
    }

    public void deleteLinkIfAllowed(long linkId, Authentication authentication)
            throws BadRequestException {

        Link link = getLinkIfAllowed(linkId, authentication);
        linkRepository.delete(link);
    }

    public void updateLinkWithLinkIfAllowed(long linkId, Link updatedLink, Authentication authentication)
            throws BadRequestException {

        Link link = getLinkIfAllowed(linkId, authentication);

        // update fields
        link.setTitle(updatedLink.getTitle());
        link.setUrl(updatedLink.getUrl());
        if(updatedLink.getImage() != null)
            link.setImage(updatedLink.getImage());

        // save
        linkRepository.save(link);
    }

    public Link getLinkIfAllowed(long linkId, Authentication authentication)
            throws BadRequestException {

        Link link = linkRepository.findById(linkId).orElseThrow(BadRequestException::new);

        if(linkBelongsToUser(link, authentication))
            return link;
        else
            throw new BadRequestException();
    }

    private boolean linkBelongsToUser(Link link, Authentication authentication) {

        // Check if the link is in a card
        Optional<Card> optionalCard = cardRepository.findByLink(link);
        if(optionalCard.isEmpty())
            return false;

        // Check if the card belongs to the user
        UserIdProjection userIdProjection = userService.getUserFromAuthentication(authentication);
        return optionalCard.get().getUser().getId() == userIdProjection.getId();
    }
}
