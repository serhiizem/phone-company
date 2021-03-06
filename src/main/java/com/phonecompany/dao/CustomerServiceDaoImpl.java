package com.phonecompany.dao;

import com.phonecompany.dao.interfaces.CustomerDao;
import com.phonecompany.dao.interfaces.CustomerServiceDao;
import com.phonecompany.dao.interfaces.ServiceDao;
import com.phonecompany.exception.dao_layer.EntityInitializationException;
import com.phonecompany.exception.dao_layer.EntityNotFoundException;
import com.phonecompany.exception.dao_layer.PreparedStatementPopulationException;
import com.phonecompany.model.CustomerServiceDto;
import com.phonecompany.model.enums.CustomerProductStatus;
import com.phonecompany.util.interfaces.QueryLoader;
import com.phonecompany.util.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerServiceDaoImpl extends JdbcOperationsImpl<CustomerServiceDto>
        implements CustomerServiceDao {

    private QueryLoader queryLoader;
    private CustomerDao customerDao;
    private ServiceDao serviceDao;

    @Autowired
    public CustomerServiceDaoImpl(QueryLoader queryLoader, CustomerDao customerDao, ServiceDao serviceDao) {
        this.queryLoader = queryLoader;
        this.customerDao = customerDao;
        this.serviceDao = serviceDao;
    }

    @Override
    public String getQuery(String type) {
        return queryLoader.getQuery("query.customer_service." + type);
    }

    @Override
    public void populateSaveStatement(PreparedStatement preparedStatement, CustomerServiceDto entity) {
        try {
            preparedStatement.setObject(1, TypeMapper.getNullableId(entity.getCustomer()));
            preparedStatement.setObject(2, TypeMapper.getNullableId(entity.getService()));
            preparedStatement.setDouble(3, entity.getPrice());
            preparedStatement.setString(4, entity.getCustomerProductStatus().name());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public void populateUpdateStatement(PreparedStatement preparedStatement, CustomerServiceDto entity) {
        try {
            preparedStatement.setObject(1, TypeMapper.getNullableId(entity.getCustomer()));
            preparedStatement.setObject(2, TypeMapper.getNullableId(entity.getService()));
            preparedStatement.setDouble(3, entity.getPrice());
            preparedStatement.setString(4, entity.getCustomerProductStatus().name());
            preparedStatement.setLong(5, entity.getId());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public CustomerServiceDto init(ResultSet rs) {
        CustomerServiceDto customerService = new CustomerServiceDto();
        try {
            customerService.setId(rs.getLong("id"));
            customerService.setCustomer(customerDao.getById(rs.getLong("customer_id")));
            customerService.setService(serviceDao.getById(rs.getLong("service_id")));
            customerService.setPrice(rs.getDouble("price"));
            customerService.setCustomerProductStatus(CustomerProductStatus.valueOf(rs.getString("service_status")));

        } catch (SQLException e) {
            throw new EntityInitializationException(e);
        }
        return customerService;
    }

    @Override
    public List<CustomerServiceDto> getCustomerServicesByCustomerId(long customerId) {
        return this.executeForList(this.getQuery("getByCustomerId"), new Object[]{customerId});
    }

    @Override
    public boolean isCustomerServiceAlreadyPresent(long serviceId, long customerId) {
        String getByServiceAndCustomerIdQuery = this.getQuery("getByServiceAndCustomerId");
        try (Connection conn = this.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(getByServiceAndCustomerIdQuery)) {
            ps.setLong(1, serviceId);
            ps.setLong(2, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) != 0;
            }
        } catch (SQLException e) {
            throw new EntityNotFoundException(serviceId, e);
        }
        return false;
    }

    @Override
    public List<CustomerServiceDto> getCurrentCustomerServices(long customerId) {
        return this.executeForList(this.getQuery("getActiveOrSuspendedByCustomerId"), new Object[]{customerId});
    }
}
