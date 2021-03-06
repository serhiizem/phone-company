package com.phonecompany.controller;

import com.phonecompany.model.Customer;
import com.phonecompany.model.Tariff;
import com.phonecompany.model.TariffRegion;
import com.phonecompany.service.email.tariff_related_emails.TariffNotificationEmailCreator;
import com.phonecompany.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/tariff-region")
public class TariffRegionController {

    private static final Logger LOG = LoggerFactory.getLogger(TariffRegionController.class);

    private TariffService tariffService;
    private TariffRegionService tariffRegionService;
    private CustomerService customerService;
    private EmailService<Customer> emailService;
    private TariffNotificationEmailCreator tariffNotificationMailCreator;

    @Autowired
    public TariffRegionController(TariffService tariffService,
                                  TariffRegionService tariffRegionService,
                                  CustomerService customerService,
                                  EmailService<Customer> emailService,
                                  TariffNotificationEmailCreator tariffNotificationMailCreator){
        this.tariffService = tariffService;
        this.tariffRegionService = tariffRegionService;
        this.customerService = customerService;
        this.emailService = emailService;
        this.tariffNotificationMailCreator = tariffNotificationMailCreator;
    }
    
    @PostMapping
    public ResponseEntity<?> saveTariffRegion(@RequestBody List<TariffRegion> tariffRegions) {
        Tariff savedTariff = tariffService.addNewTariff(tariffRegions);
        SimpleMailMessage notificationMessage = this.tariffNotificationMailCreator
                .constructMessage(savedTariff);
        this.customerService.notifyAgreedCustomers(notificationMessage);
        return new ResponseEntity<Object>(savedTariff, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> updateTariff(@RequestBody List<TariffRegion> tariffRegions) {
        Tariff updatedTariff = tariffService.updateTariff(tariffRegions);
        return new ResponseEntity<Object>(updatedTariff, HttpStatus.OK);
    }

    @GetMapping(value = "/region/{id}")
    public ResponseEntity<?> getTariffsForRegion(@PathVariable("id") Long regionId) {
        return new ResponseEntity<Object>(tariffRegionService
                .getAllByRegionId(regionId), HttpStatus.OK);
    }
}
