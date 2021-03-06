package com.phonecompany.service.interfaces;

import com.phonecompany.model.*;
import com.phonecompany.model.enums.OrderType;

import java.time.LocalDate;
import java.util.List;

public interface OrderService extends CrudService<Order>, OrderStatisticsService {

    Order getResumingOrderByCustomerTariff(CustomerTariff customerTariff);

    List<Order> getResumingOrderByCustomerService(CustomerServiceDto customerService);

    List<Order> getOrdersHistoryByClient(Customer customer, int page, int size);

    List<Order> getOrdersHistoryByCorporateId(long corporateId, int page, int size);

    List<Order> getOrdersHistoryForServicesByClient(Customer customer, int page, int size);

    Integer getOrdersCountByClient(Customer customer);

    Integer getOrdersCountForServicesByClient(Customer customer);

    Integer getOrdersCountByCorporateId(long corporateId);

    Order saveCustomerServiceOrder(CustomerServiceDto customerService, OrderType orderType);

    List<Statistics> getTariffOrderStatisticsByRegionAndTimePeriod(long regionId,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate);

    List<Statistics> getServiceOrderStatisticsByTimePeriod(LocalDate startDate, LocalDate endDate);

    Order getNextResumingOrder();
}
