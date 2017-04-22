package com.phonecompany.controller;

import com.phonecompany.model.Customer;
import com.phonecompany.model.OnRegistrationCompleteEvent;
import com.phonecompany.model.User;
import com.phonecompany.model.enums.UserRole;
import com.phonecompany.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import static com.phonecompany.controller.UserController.USERS_RESOURCE_NAME;
import static com.phonecompany.model.enums.UserRole.CLIENT;
import static com.phonecompany.util.RestUtil.getResourceHeaders;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class RegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    public static void main(String[] args) {
        ShaPasswordEncoder encoder = new ShaPasswordEncoder();
        String root = encoder.encodePassword("root", null);

        System.out.println(root);
    }

    private UserService userService;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public RegistrationController(UserService userService,
                                  ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(method = POST, value = "/api/users")
    public ResponseEntity<?> saveUser(@RequestBody Customer customer) {
        LOG.debug("Customer retrieved from the http request: " + customer);

        customer.setRole(CLIENT);
        User persistedUser = this.userService.save(customer);
        LOG.debug("User persisted with an id: " + persistedUser.getId());

        this.eventPublisher.publishEvent(new OnRegistrationCompleteEvent(persistedUser));

        HttpHeaders resourceHeaders = getResourceHeaders(USERS_RESOURCE_NAME, persistedUser.getId());

        return new ResponseEntity<>(persistedUser, resourceHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/api/confirmRegistration")
    public ResponseEntity<? extends User> confirmRegistration(@RequestParam String token)
            throws URISyntaxException {
        LOG.debug("Token retrieved from the request parameter: {}", token);
        this.userService.activateUserByToken(token);

        URI registration = new URI("http://localhost:8090/#/user/profile/success");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(registration);

        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @RequestMapping(value = "/api/login/try", method = RequestMethod.GET)
    public ResponseEntity<?> tryLogin() {
        LOG.debug("About to fetch currently logged in role");
        org.springframework.security.core.userdetails.User securityUser = null;
        securityUser = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername());
        return new ResponseEntity<>(user.getRole(), HttpStatus.OK);
    }
}
