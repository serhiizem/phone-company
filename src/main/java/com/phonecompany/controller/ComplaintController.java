package com.phonecompany.controller;

import com.phonecompany.model.Complaint;
import com.phonecompany.model.enums.ComplaintCategory;
import com.phonecompany.service.interfaces.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping(value = "api/complaints")
public class ComplaintController {

    private static final Logger LOG = LoggerFactory.getLogger(ComplaintController.class);

    private ComplaintService complaintService;
    private UserController userController;

    @Autowired
    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping(value = "")
    public ResponseEntity<?> createComplaint(@RequestBody Complaint complaint) {
        complaint.setUser(userController.getUser());
        Complaint createdComplaint = complaintService.createComplaint(complaint);
        LOG.debug("Complaint added {}", createdComplaint);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping(value = "/complaints")
    public Collection<Complaint> getAllComplaints() {
        LOG.info("Retrieving all the complaints contained in the database");
//        List<Complaint> complaints = complaintService.getAll();
//        LOG.info("Complaints fetched from the database: " + complaints);

        return complaintService.getAll();
    }

    @GetMapping(value = "/categories")
    public Collection<ComplaintCategory> getAllComplaintCategory() {
        LOG.info("Retrieving all the complaint categories");

        return complaintService.getAllComplaintCategory();
    }
}
