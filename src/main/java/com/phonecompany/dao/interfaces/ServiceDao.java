package com.phonecompany.dao.interfaces;

import com.phonecompany.model.Service;
import com.phonecompany.model.enums.ProductStatus;
import com.phonecompany.util.Query;

import java.util.List;

public interface ServiceDao extends JdbcOperations<Service>,
        AbstractPageableDao<Service> {

    boolean isExist(Service service);

    void updateServiceStatus(long serviceId, ProductStatus productStatus);
    List<Service> getAllServicesSearch(Query query);
}
