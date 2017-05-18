package com.phonecompany.controller;

import com.phonecompany.model.WeeklyComplaintStatistics;
import com.phonecompany.model.WeeklyOrderStatistics;
import com.phonecompany.service.StatisticsService;
import com.phonecompany.service.interfaces.ComplaintService;
import com.phonecompany.service.interfaces.OrderService;
import com.phonecompany.service.interfaces.TariffService;
import com.phonecompany.service.interfaces.XSSFService;
import com.phonecompany.service.xssfHelper.SheetDataSet;
import com.phonecompany.service.xssfHelper.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

import static com.phonecompany.util.FileUtil.getFilesWithExtensionFromPath;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "api/reports")
public class ReportController {

    private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);
    private TariffService tariffService;
    private OrderService orderService;
    private ComplaintService complaintService;
    private StatisticsService statisticsService;
    private XSSFService<LocalDate, Long> xssfService;

    @Autowired
    public ReportController(TariffService tariffService,
                            OrderService orderService,
                            ComplaintService complaintService,
                            StatisticsService statisticsService,
                            XSSFService<LocalDate, Long> xssfService) {
        this.tariffService = tariffService;
        this.orderService = orderService;
        this.complaintService = complaintService;
        this.statisticsService = statisticsService;
        this.xssfService = xssfService;
    }

    @RequestMapping(value = "/{regionId}/{startDate}/{endDate}", method = GET, produces = "application/octet-stream")
    public ResponseEntity<?> getOrderReportByRegionAndTimePeriod(@PathVariable("regionId") Integer regionId,
                                                                 @PathVariable("startDate") LocalDate startDate,
                                                                 @PathVariable("endDate") LocalDate endDate) {
        List<Statistics> tariffStatisticsData = this.tariffService
                .getTariffStatisticsData(regionId, startDate, endDate);

        SheetDataSet<LocalDate, Long> tariffStatisticsDataSet = statisticsService
                .prepareStatisticsDataSet("Tariffs", tariffStatisticsData,
                        startDate, endDate);

        InputStream reportInputStream = xssfService.generateReport(tariffStatisticsDataSet);

        return this.getOctetStreamResponseEntity(reportInputStream);

    }

    @RequestMapping(value = "/complaint/{regionId}/{startDate}/{endDate}", method = GET, produces = "application/vnd.ms-excel")
    public ResponseEntity<?> getComplaintReportByRegionAndTimePeriod(@PathVariable("regionId") Integer regionId,
                                                                     @PathVariable("startDate") LocalDate startDate,
                                                                     @PathVariable("endDate") LocalDate endDate) {
        List<Statistics> complaintStatistics = this.complaintService
                .getComplaintStatisticsByRegionAndTimePeriod(regionId, startDate, endDate);

        SheetDataSet<LocalDate, Long> complaintsDataSet = this.statisticsService
                .prepareStatisticsDataSet("Complaints", complaintStatistics,
                        startDate, endDate);
        InputStream reportInputStream = xssfService.generateReport(complaintsDataSet);

        return this.getOctetStreamResponseEntity(reportInputStream);
    }

    @GetMapping("/order-statistics")
    public ResponseEntity<?> getOrderStatisticsForTheLastMonthByWeeks() {

        WeeklyOrderStatistics orderStatistics = this.orderService.getOrderStatistics();

        return new ResponseEntity<>(orderStatistics, HttpStatus.OK);
    }

    @GetMapping("/complaints-statistics")
    public ResponseEntity<?> getComplaintStatisticsForTheLastMonthByWeeks() {
        LOG.debug("Get complaints statistic");
        WeeklyComplaintStatistics complaintStatistics = this.complaintService.getComplaintStatistics();

        return new ResponseEntity<>(complaintStatistics, HttpStatus.OK);
    }

    private ResponseEntity<?> getOctetStreamResponseEntity(InputStream inputStream) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(inputStream));
    }
}
