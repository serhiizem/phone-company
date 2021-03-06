package com.phonecompany.model;

import com.phonecompany.model.enums.CustomerProductStatus;

import javax.validation.constraints.NotNull;

public class CustomerTariff extends DomainEntity {

    private Customer customer;
    private Corporate corporate;
    @NotNull(message = "Total price must not be null")
    private double totalPrice;
    @NotNull(message = "Order status must not be null")
    private CustomerProductStatus customerProductStatus;
    @NotNull(message = "Tariff must not be null")
    private Tariff tariff;

    public CustomerTariff() {
    }

    public CustomerTariff(Customer customer, Corporate corporate,
                          double totalPrice, CustomerProductStatus customerProductStatus,
                          Tariff tariff) {
        this.customer = customer;
        this.corporate = corporate;
        this.totalPrice = totalPrice;
        this.customerProductStatus = customerProductStatus;
        this.tariff = tariff;
    }

    public CustomerTariff(Long id, Customer customer, Corporate corporate,
                          double totalPrice, CustomerProductStatus customerProductStatus,
                          Tariff tariff) {
        super(id);
        this.customer = customer;
        this.corporate = corporate;
        this.totalPrice = totalPrice;
        this.customerProductStatus = customerProductStatus;
        this.tariff = tariff;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Corporate getCorporate() {
        return corporate;
    }

    public void setCorporate(Corporate corporate) {
        this.corporate = corporate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public CustomerProductStatus getCustomerProductStatus() {
        return customerProductStatus;
    }

    public void setCustomerProductStatus(CustomerProductStatus customerProductStatus) {
        this.customerProductStatus = customerProductStatus;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    @Override
    public String toString() {
        return "CustomerTariff{" +
                "customer=" + customer +
                ", corporate=" + corporate +
                ", totalPrice=" + totalPrice +
                ", customerProductStatus=" + customerProductStatus +
                ", tariff=" + tariff +
                "} " + super.toString();
    }
}
