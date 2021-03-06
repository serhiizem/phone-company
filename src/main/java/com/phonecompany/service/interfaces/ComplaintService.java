package com.phonecompany.service.interfaces;

import com.phonecompany.model.*;
import com.phonecompany.service.xssfHelper.*;

import java.time.LocalDate;
import java.util.Map;

public interface ComplaintService extends CrudService<Complaint> {
    Complaint createComplaint(Complaint complaint);

    Complaint createComplaintByCsr(Complaint complaint);

    Map<String, Object> getComplaints(String category, String status, int page, int size, String partOfEmail,
                                      String dateFrom, String dateTo, String partOfSubject, int orderBy,
                                      String orderByType);

    Map<String, Object> getComplaintsByCustomer(int id, int page, int size);

    Map<String, Object> getComplaintsByResponsible(long responsibleId, String category, int page, int size,
                                                   String partOfEmail, String dateFrom, String dateTo,
                                                   String partOfSubject, int orderBy, String orderByType);

    Complaint setStatusIntraprocess(Complaint complaint);

    Complaint setStatusAccomplished(Complaint complaint, String comment);

    Map<String, Object> getAllComplaintsSearch(int page, int size, String email, String status, String category);

    WeeklyComplaintsAmount getComplaintStatistics();

    SheetDataSet<LocalDate, Long> getComplaintStatisticsDataSet(long regionId,
                                                                LocalDate startDate,
                                                                LocalDate endDate);
}
