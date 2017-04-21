package com.phonecompany.model;

import com.phonecompany.model.enums.OrderStatus;

import javax.validation.constraints.NotNull;
import java.sql.Date;

public class CustomerService extends DomainEntity{

    @NotNull(message = "Customer must not be null")
    private Customer customer;
    @NotNull(message = "Service must not be null")
    private Service service;
    @NotNull(message = "Date must not be null")
    private Date orderDate;
    @NotNull(message = "Price must not be null")
    private double price;
    @NotNull(message = "Order status must not be null")
    private OrderStatus orderStatus;

    public CustomerService(){}

    public CustomerService(Customer customer, Service service, Date orderDate, double price, OrderStatus orderStatus) {
        this.customer = customer;
        this.service = service;
        this.orderDate = orderDate;
        this.price = price;
        this.orderStatus = orderStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
