package com.phonecompany.service.interfaces;

import com.phonecompany.model.Service;
import com.phonecompany.model.enums.ProductStatus;
import com.phonecompany.model.paging.PagingResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ServiceService extends CrudService<Service>,
        SimpleStatisticsGenerating<LocalDate, Long> {

    PagingResult<Service> getServicesByProductCategoryId(int page, int size, int productCategoryId,
                                                         String partOfName, double priceFrom, double priceTo,
                                                         int status, int orderBy, String orderByType);

    List<Service> getServicesByStatus(ProductStatus status);

    List<Service> getAllActiveServicesWithDiscount();

    List<Service> getTopActiveServices();

    Service save(Service service);

    void updateServiceStatus(long serviceId, ProductStatus productStatus);

    Service getById(Long id);

    Map<String, Object> getAllServicesSearch(int page, int size, String name, String status,
                                             int lowerPrice, int upperPrice);

    List<Service> applyDiscount(List<Service> services);
}
