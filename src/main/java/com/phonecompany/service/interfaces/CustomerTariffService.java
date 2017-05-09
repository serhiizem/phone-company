package com.phonecompany.service.interfaces;

import com.phonecompany.model.Customer;
import com.phonecompany.model.CustomerTariff;

import java.util.List;
import java.util.Map;

public interface CustomerTariffService extends CrudService<CustomerTariff> {

    List<CustomerTariff> getByClientId(Customer customer);

    public CustomerTariff getCurrentCustomerTariff(Customer customer);

    public CustomerTariff getCurrentCustomerTariff(long customerId);

    public CustomerTariff getCurrentCorporateTariff(long corporateId);

    CustomerTariff getCurrentActiveOrSuspendedClientTariff(Customer customer);

    public CustomerTariff getCurrentActiveOrSuspendedCorporateTariff(long corporateId);

    CustomerTariff deactivateCustomerTariff(CustomerTariff customerTariff);

    CustomerTariff resumeCustomerTariff(CustomerTariff customerTariff);

    CustomerTariff suspendCustomerTariff(Map<String, Object> suspensionData);
}
