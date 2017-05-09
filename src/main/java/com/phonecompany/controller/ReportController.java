package com.phonecompany.controller;

import com.phonecompany.service.interfaces.XSSFService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;

import static com.phonecompany.util.FileUtil.getFilesWithExtensionFromPath;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class ReportController {

    private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);
    private static final String EXCEL_EXTENSION = "xlsx";
    private static final String CURRENT_DIRECTORY = "./";

    private XSSFService xssfService;

    @Autowired
    public ReportController(XSSFService xssfService) {
        this.xssfService = xssfService;
    }

    @RequestMapping(value = "/api/reports/{regionId}/{startDate}/{endDate}",
            method = GET, produces = "application/vnd.ms-excel")
    public ResponseEntity<?> getReportByRegionAndTimePeriod(@PathVariable("regionId") Integer regionId,
            @PathVariable("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PathVariable("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        this.xssfService.generateReport(regionId, startDate, endDate);

        InputStream xlsFileInputStream = this.getXlsStreamFromRootDirectory();

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(xlsFileInputStream));
    }

    private InputStream getXlsStreamFromRootDirectory() {
        try {
            File[] files = getFilesWithExtensionFromPath(EXCEL_EXTENSION, CURRENT_DIRECTORY);
            return new FileInputStream(files[0]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
