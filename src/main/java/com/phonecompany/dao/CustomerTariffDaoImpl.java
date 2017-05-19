package com.phonecompany.dao;

import com.phonecompany.dao.interfaces.CorporateDao;
import com.phonecompany.dao.interfaces.CustomerDao;
import com.phonecompany.dao.interfaces.CustomerTariffDao;
import com.phonecompany.dao.interfaces.TariffDao;
import com.phonecompany.exception.EntityInitializationException;
import com.phonecompany.exception.EntityNotFoundException;
import com.phonecompany.exception.PreparedStatementPopulationException;
import com.phonecompany.model.CustomerTariff;
import com.phonecompany.model.enums.CustomerProductStatus;
import com.phonecompany.util.QueryLoader;
import com.phonecompany.util.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomerTariffDaoImpl extends JdbcOperationsImpl<CustomerTariff>
        implements CustomerTariffDao {

    private QueryLoader queryLoader;
    private CustomerDao customerDao;
    private CorporateDao corporateDao;
    private TariffDao tariffDao;

    @Autowired
    public CustomerTariffDaoImpl(QueryLoader queryLoader, CustomerDao customerDao, CorporateDao corporateDao, TariffDao tariffDao) {
        this.queryLoader = queryLoader;
        this.customerDao = customerDao;
        this.corporateDao = corporateDao;
        this.tariffDao = tariffDao;
    }

    @Override
    public String getQuery(String type) {
        return queryLoader.getQuery("query.customer_tariff." + type);
    }

    @Override
    public void populateSaveStatement(PreparedStatement preparedStatement, CustomerTariff entity) {
        try {
            preparedStatement.setObject(1, TypeMapper.getNullableId(entity.getCustomer()));
            preparedStatement.setObject(2, TypeMapper.getNullableId(entity.getCorporate()));
            preparedStatement.setDouble(3, entity.getTotalPrice());
            preparedStatement.setString(4, entity.getCustomerProductStatus().name());
            preparedStatement.setObject(5, TypeMapper.getNullableId(entity.getTariff()));
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public void populateUpdateStatement(PreparedStatement preparedStatement, CustomerTariff entity) {
        try {
            preparedStatement.setObject(1, TypeMapper.getNullableId(entity.getCustomer()));
            preparedStatement.setObject(2, TypeMapper.getNullableId(entity.getCorporate()));
            preparedStatement.setDouble(3, entity.getTotalPrice());
            preparedStatement.setString(4, entity.getCustomerProductStatus().name());
            preparedStatement.setObject(5, TypeMapper.getNullableId(entity.getTariff()));
            preparedStatement.setLong(6, entity.getId());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public CustomerTariff init(ResultSet rs) {
        CustomerTariff customerTariff = new CustomerTariff();
        try {
            customerTariff.setId(rs.getLong("id"));
            customerTariff.setCustomer(customerDao.getById(rs.getLong("customer_id")));
            customerTariff.setCorporate(corporateDao.getById(rs.getLong("corporate_id")));
            customerTariff.setTotalPrice(rs.getDouble("total_price"));
            customerTariff.setCustomerProductStatus(CustomerProductStatus.valueOf(rs.getString("tariff_status")));
            customerTariff.setTariff(tariffDao.getById(rs.getLong("tariff_id")));
        } catch (SQLException e) {
            throw new EntityInitializationException(e);
        }
        return customerTariff;
    }

    @Override
    public List<CustomerTariff> getCustomerTariffsByCustomerId(Long customerId) {
        String query = this.getQuery("getTariffsByCustomerId");
        return getCustomerTariffsByClientIdQuery(customerId, query);
    }

    @Override
    public List<CustomerTariff> getCustomerTariffsByCorporateId(Long corporateId) {
        String query = this.getQuery("getTariffsByCorporateId");
        return getCustomerTariffsByClientIdQuery(corporateId, query);

    }

    private List<CustomerTariff> getCustomerTariffsByClientIdQuery(Long id, String query) {
        List<CustomerTariff> tariffs = new ArrayList<>();
        Connection conn = DataSourceUtils.getConnection(getDataSource());
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tariffs.add(init(rs));
            }
        } catch (SQLException e) {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
            throw new EntityNotFoundException(id, e);
        } finally {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
        }
        return tariffs;
    }

    @Override
    public CustomerTariff getCurrentCustomerTariff(long customerId) {
        Connection conn = DataSourceUtils.getConnection(getDataSource());
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(this.getQuery("getByCustomerId"));
            ps.setLong(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (init(rs));
            }
        } catch (SQLException e) {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
            throw new EntityNotFoundException(customerId, e);
        } finally {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
        }
        return null;
    }

    @Override
    public CustomerTariff getCurrentCorporateTariff(long corporateId) {
        Connection conn = DataSourceUtils.getConnection(getDataSource());
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(this.getQuery("getByCorporateId"));
            ps.setLong(1, corporateId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (init(rs));
            }
        } catch (SQLException e) {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
            throw new EntityNotFoundException(corporateId, e);
        } finally {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
        }
        return null;
    }

    @Override
    public CustomerTariff getCurrentActiveOrSuspendedCustomerTariff(long customerId) {
        String query = this.getQuery("getActiveOrSuspendedByCustomerId");
        return getCustomerTariffByClientIdQuery(customerId, query);
    }

    @Override
    public CustomerTariff getCurrentActiveOrSuspendedCorporateTariff(long corporateId) {
        String query = this.getQuery("getActiveOrSuspendedByCorporateId");
        return getCustomerTariffByClientIdQuery(corporateId, query);
    }

    private CustomerTariff getCustomerTariffByClientIdQuery(Long id, String query) {
        Connection conn = DataSourceUtils.getConnection(getDataSource());
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return init(rs);
            }
        } catch (SQLException e) {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
            throw new EntityNotFoundException(id, e);
        } finally {
            JdbcUtils.closeStatement(ps);
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
        }
        return null;
    }
}
