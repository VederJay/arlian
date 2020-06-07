package org.arlian.site.service;

import org.arlian.site.model.start.card.Card;
import org.arlian.site.model.start.card.CardRepository;
import org.arlian.site.model.start.card.CardType;
import org.arlian.site.model.start.link.Link;
import org.arlian.site.model.start.link.LinkRepository;
import org.arlian.site.model.start.page.Page;
import org.arlian.site.model.start.page.PageRepository;
import org.arlian.site.model.user.User;
import org.springframework.stereotype.Service;

@Service
public class PageService {

    // Autowired repositories
    private final PageRepository pageRepository;
    private final CardRepository cardRepository;
    private final LinkRepository linkRepository;

    public PageService(PageRepository pageRepository, CardRepository cardRepository, LinkRepository linkRepository) {
        this.pageRepository = pageRepository;
        this.cardRepository = cardRepository;
        this.linkRepository = linkRepository;
    }

    public Page createNewPage(User user, String pageName){

        Page page = new Page(user, pageName, false);
        Card card = new Card("Fresh page");
        card.setType(CardType.TEXT_LINKS);

        Link link = new Link("Use 'edit mode' to start adding cards and links", "#");
        card.addLink(link);
        page.addCard(card);

        pageRepository.save(page);
        cardRepository.save(card);
        linkRepository.save(link);

        return page;
    }
}
