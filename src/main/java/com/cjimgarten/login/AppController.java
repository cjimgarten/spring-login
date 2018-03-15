package com.cjimgarten.login;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;

/**
 * Created by chris on 3/13/18.
 */
@Controller
public class AppController {

    private static final Logger LOGGER = Logger.getLogger(AppController.class);
    private Cookie loggedIn = new Cookie("loggedIn", "false");

    @Autowired
    private UserRepository userRepository;

    @Value(value = "${com.cjimgarten.login.title}")
    private String title;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "redirect:/login";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(Model model) {

        // if user is authenticated redirect to home page
        if (loggedIn.getValue().equals("true")) {
            LOGGER.info("User authenticated -- redirecting to home page");
            return "redirect:/home";
        }

        LOGGER.info("Login form");
        model.addAttribute("title", title);
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String processLoginForm(@ModelAttribute User user, Model model) {

        LOGGER.info("Processing login form");
        LOGGER.info(user);

        // fetch data from db table
        Iterable<User> users = userRepository.findAll();

        // authenticate username and password combination
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername()) &&
                    u.getPassword().equals(user.getPassword())) {

                // successful login attempt
                loggedIn.setValue("true");
                LOGGER.info("Login attempt successful -- logging in");
                return "redirect:/home";
            }
        }

        // unsuccessful login attempt
        LOGGER.info("Login attempt unsuccessful");
        model.addAttribute("invalidLogin", "Invalid login attempt");
        return "login";
    }

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String register(Model model) {

        // if user is authenticated redirect to home page
        if (loggedIn.getValue().equals("true")) {
            LOGGER.info("User authenticated -- redirecting to home page");
            return "redirect:/home";
        }

        LOGGER.info("Register form");
        model.addAttribute("title", title);
        return "register";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String processRegisterForm(@ModelAttribute User user,
                                      @RequestParam String confirmPassword,
                                      Model model) {

        LOGGER.info("Processing register form");
        LOGGER.info(user);
        LOGGER.info(confirmPassword);

        // TODO validate username and password fields

        // confirm passwords match
        if (user.getPassword().equals(confirmPassword)) {

            // successful registration attempt
            userRepository.save(user);
            loggedIn.setValue("true");
            LOGGER.info("Registration attempt successful -- logging in");
            return "redirect:/home";
        }

        // unsuccessful registration attempt
        LOGGER.info("Registration attempt unsuccessful");
        model.addAttribute("invalidRegistration", "Invalid registration attempt");
        return "register";
    }

    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(Model model) {

        // ensure user is authenticated
        if (loggedIn.getValue().equals("false")) {
            LOGGER.info("User not authenticated -- access denied");
            return "redirect:/login";
        }

        LOGGER.info("Home page");
        model.addAttribute("title", title);
        return "home";
    }

    @RequestMapping(value = "home", method = RequestMethod.POST)
    public String logout(Model model) {
        LOGGER.info("Logging out");
        loggedIn.setValue("false");
        return "redirect:/";
    }
}
