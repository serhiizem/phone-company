package com.phonecompany.controller;

import com.phonecompany.model.Customer;
import com.phonecompany.model.CustomerTariff;
import com.phonecompany.service.interfaces.CustomerService;
import com.phonecompany.service.interfaces.CustomerTariffService;
import com.phonecompany.service.interfaces.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/customer-tariffs")
public class CustomerTariffController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerTariffController.class);

    private CustomerTariffService customerTariffService;
    private CustomerService customerService;

    @Autowired
    public CustomerTariffController(CustomerTariffService customerTariffService,
                                    CustomerService customerService) {
        this.customerTariffService = customerTariffService;
        this.customerService = customerService;
    }

    @GetMapping(value = "/current")
    public ResponseEntity<?> getCurrentCustomerTariff() {
        CustomerTariff customerTariff = customerTariffService.getCurrentCustomerTariff(customerService.getCurrentlyLoggedInUser());
        LOGGER.debug("Current customer tariff {}", customerTariff);
        return new ResponseEntity<Object>(customerTariff, HttpStatus.OK);
    }

    @RequestMapping(value = "/customer-tariff", method = RequestMethod.GET)
    public ResponseEntity<?> getCurrentActiveOrSuspendedCustomerTariff() {
        Customer customer = customerService.getCurrentlyLoggedInUser();
        CustomerTariff customerTariff = customerTariffService.getCurrentActiveOrSuspendedClientTariff(customer);
        return new ResponseEntity<Object>(customerTariff, HttpStatus.OK);
    }

    @GetMapping(value = "/customer/{id}")
    public ResponseEntity<?> getCustomerTariff(@PathVariable("id") long customerId) {
        Customer customer = customerService.getById(customerId);
        CustomerTariff customerTariff = customerTariffService.getCurrentActiveOrSuspendedClientTariff(customer);
        return new ResponseEntity<Object>(customerTariff, HttpStatus.OK);
    }

    @GetMapping(value = "/corporate/{id}")
    public ResponseEntity<?> getCorporateTariff(@PathVariable("id") long corporateId) {
        CustomerTariff customerTariff = customerTariffService.getCurrentActiveOrSuspendedCorporateTariff(corporateId);
        return new ResponseEntity<Object>(customerTariff, HttpStatus.OK);
    }

    @PatchMapping(value = "/resume")
    public ResponseEntity<?> resumeTariff(@RequestBody CustomerTariff customerTariff){
        CustomerTariff updatedCustomerTariff = customerTariffService.resumeCustomerTariff(customerTariff);
        return new ResponseEntity<Object>(updatedCustomerTariff, HttpStatus.OK);
    }

    @PatchMapping(value = "/deactivate")
    public ResponseEntity<Void> deactivateCustomerTariff(@RequestBody CustomerTariff customerTariff) {
        customerTariffService.deactivateCustomerTariff(customerTariff);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/suspend")
    public ResponseEntity<Void> suspendCustomerTariff(@RequestBody Map<String, Object> data) {
        this.customerTariffService.suspendCustomerTariff(data);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
