package org.arlian.site.start.controller;

import org.arlian.site.start.model.settings.Settings;
import org.arlian.site.start.model.settings.SettingsRepository;
import org.arlian.site.user.model.User;
import org.arlian.site.user.model.UserIdProjection;
import org.arlian.site.user.model.UserRepository;
import org.arlian.site.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import java.util.Optional;

@Controller
@RequestMapping("/start/settings")
public class SettingsController {

    private final UserService userService;
    private final EntityManager entityManager;
    private final SettingsRepository settingsRepository;
    private final UserRepository userRepository;

    public SettingsController(UserService userService, EntityManager entityManager,
                              SettingsRepository settingsRepository, UserRepository userRepository) {
        this.userService = userService;
        this.entityManager = entityManager;
        this.settingsRepository = settingsRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("/view")
    public String viewSettings(Model model, Authentication authentication){

        // Get pages based on user ID
        UserIdProjection userIdProjection = userService.getUserIdProjectionFromAuthentication(authentication);
        User user = entityManager.getReference(User.class, userIdProjection.getId());

        Optional<Settings> settingsOptional = settingsRepository.findByUser(user);

        Settings settings;
        if(settingsOptional.isEmpty()) {
            User actualUser = userRepository.getUserById(user.getId());
            settings = Settings.builder()
                    .user(actualUser)
                    .build();
            actualUser.setSettings(settings);
            settingsRepository.save(settings);
            userRepository.save(actualUser);
        }
        else
            settings = settingsOptional.get();

        model.addAttribute("settings", settings);

        return "pages/start/settings";
    }
}
