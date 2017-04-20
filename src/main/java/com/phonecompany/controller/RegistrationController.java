package com.phonecompany.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.phonecompany.controller.UserController.USERS_RESOURCE_NAME;
import static com.phonecompany.util.RestUtil.getResourceHeaders;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class RegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    private UserService userService;
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    public RegistrationController(UserService userService,
                                  ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(method = POST, value = "/api/users")
    public ResponseEntity<?> saveUser(@RequestBody User client) {
        LOG.info("User retrieved from the http request: " + client);

        client.setRole(UserRole.CLIENT);
        User persistedUser = this.userService.save(client);
        LOG.info("User persisted with an id: " + persistedUser.getId());

        this.eventPublisher.publishEvent(new OnRegistrationCompleteEvent(persistedUser));

        HttpHeaders resourceHeaders = getResourceHeaders(USERS_RESOURCE_NAME, persistedUser.getId());

        return new ResponseEntity<>(persistedUser, resourceHeaders, HttpStatus.CREATED);
    }
}
