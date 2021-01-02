package org.arlian.site.start.service;

import org.arlian.site.start.model.card.Card;
import org.arlian.site.start.model.card.CardRepository;
import org.arlian.site.start.model.card.CardType;
import org.arlian.site.start.model.link.Link;
import org.arlian.site.start.model.link.LinkRepository;
import org.arlian.site.start.model.page.Page;
import org.arlian.site.start.model.page.PageNameProjection;
import org.arlian.site.start.model.page.PageRepository;
import org.arlian.site.user.model.User;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
public class PageService {

    // Autowired repositories
    private final PageRepository pageRepository;
    private final CardRepository cardRepository;
    private final LinkRepository linkRepository;
    private final UserService userService;
    private final EntityManager entityManager;

    public PageService(PageRepository pageRepository, CardRepository cardRepository,
                       LinkRepository linkRepository, UserService userService,
                       EntityManager entityManager) {
        this.pageRepository = pageRepository;
        this.cardRepository = cardRepository;
        this.linkRepository = linkRepository;
        this.userService = userService;
        this.entityManager = entityManager;
    }

    public String getDefaultPageName(Authentication authentication){

        // Using the authentication, get the page name of the default page
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User proxyUser = entityManager.getReference(User.class, userIdProjection.getId());
        Optional<PageNameProjection> pageNameProjection = pageRepository.findDefaultNameByUser(proxyUser);

        // If one exists, return its name
        if(pageNameProjection.isPresent())
            return pageNameProjection.get().getName();

        // If one doesn't exist, create a page and make it default
        Page page = createNewPage(proxyUser, "home");
        page.setDefault(true);
        pageRepository.save(page);

        // Get the name of the page
        return page.getName();
    }

    public Page createNewPage(User user, String pageName){

        Page page = Page.builder()
            .name(pageName)
            .isDefault(false)
            .user(user)
            .build();

        Card card = Card.builder()
                .title("New page")
                .type(CardType.TEXT_LINKS)
                .build();

        Link link = Link.builder()
                .title("Use 'edit mode' in the bar at the top to start adding and updating cards and links")
                .url("#")
                .build();

        card.addLink(link);
        page.addCard(card);

        pageRepository.save(page);
        cardRepository.save(card);
        linkRepository.save(link);

        return page;
    }

    public Optional<Page> getOptionalForPage(Authentication authentication, String pageName) {
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User proxyUser = entityManager.getReference(User.class, userIdProjection.getId());
        return pageRepository.findByUserAndName(proxyUser, pageName);
    }

    public Optional<Page> getOptionalForPage(Authentication authentication, Long pageId) {
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User proxyUser = entityManager.getReference(User.class, userIdProjection.getId());
        return pageRepository.findByUserAndId(proxyUser, pageId);
    }
}
