package com.phonecompany.controller;

import com.phonecompany.model.Role;
import com.phonecompany.model.User;
import com.phonecompany.service.interfaces.RoleService;
import com.phonecompany.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Yurii on 14.04.2017.
 */
@RestController
public class LoginController {

    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/api/roles", method = RequestMethod.GET)
    public List<Role> getAllRoles(){
        return roleService.getAll();
    }

    @RequestMapping(value = "/api/login/try", method = RequestMethod.GET)
    public ResponseEntity<Void> tryLogin(){
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

}
