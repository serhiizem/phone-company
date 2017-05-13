package com.phonecompany.dao;

import com.phonecompany.dao.interfaces.TariffDao;
import com.phonecompany.exception.*;
import com.phonecompany.model.Tariff;
import com.phonecompany.model.enums.ProductStatus;
import com.phonecompany.util.QueryLoader;
import com.phonecompany.util.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.phonecompany.util.TypeMapper.toLocalDate;
import static com.phonecompany.util.TypeMapper.toSqlDate;

@Repository
public class TariffDaoImpl extends AbstractPageableDaoImpl<Tariff> implements TariffDao {

    private QueryLoader queryLoader;

    @Autowired
    public TariffDaoImpl(QueryLoader queryLoader) {
        this.queryLoader = queryLoader;
    }

    @Override
    public String getQuery(String type) {
        return queryLoader.getQuery("query.tariff." + type);
    }

    @Override
    public void populateSaveStatement(PreparedStatement preparedStatement, Tariff tariff) {
        try {
            preparedStatement.setString(1, tariff.getTariffName());
            preparedStatement.setString(2, tariff.getProductStatus().name());
            preparedStatement.setString(3, tariff.getInternet());
            preparedStatement.setString(4, tariff.getCallsInNetwork());
            preparedStatement.setString(5, tariff.getCallsOnOtherNumbers());
            preparedStatement.setString(6, tariff.getSms());
            preparedStatement.setString(7, tariff.getMms());
            preparedStatement.setString(8, tariff.getRoaming());
            preparedStatement.setBoolean(9, tariff.getIsCorporate());
            preparedStatement.setDate(10, toSqlDate(tariff.getCreationDate()));
            preparedStatement.setDouble(11, tariff.getDiscount());
            preparedStatement.setString(12, tariff.getPictureUrl());
            preparedStatement.setDouble(13, tariff.getPrice());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public void populateUpdateStatement(PreparedStatement preparedStatement, Tariff tariff) {
        try {
            preparedStatement.setString(1, tariff.getTariffName());
            preparedStatement.setString(2, tariff.getProductStatus().name());
            preparedStatement.setString(3, tariff.getInternet());
            preparedStatement.setString(4, tariff.getCallsInNetwork());
            preparedStatement.setString(5, tariff.getCallsOnOtherNumbers());
            preparedStatement.setString(6, tariff.getSms());
            preparedStatement.setString(7, tariff.getMms());
            preparedStatement.setString(8, tariff.getRoaming());
            preparedStatement.setBoolean(9, tariff.getIsCorporate());
            preparedStatement.setDate(10, toSqlDate(tariff.getCreationDate()));
            preparedStatement.setDouble(11, tariff.getDiscount());
            preparedStatement.setString(12, tariff.getPictureUrl());
            preparedStatement.setDouble(13, tariff.getPrice());
            preparedStatement.setLong(14, tariff.getId());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public Tariff init(ResultSet rs) {
        Tariff tariff = new Tariff();
        try {
            tariff.setId(rs.getLong("id"));
            tariff.setTariffName(rs.getString("tariff_name"));
            tariff.setProductStatus(ProductStatus.valueOf(rs.getString("product_status")));
            tariff.setInternet(rs.getString("internet"));
            tariff.setCallsInNetwork(rs.getString("calls_in_network"));
            tariff.setCallsOnOtherNumbers(rs.getString("calls_on_other_numbers"));
            tariff.setSms(rs.getString("sms"));
            tariff.setMms(rs.getString("mms"));
            tariff.setRoaming(rs.getString("roaming"));
            tariff.setIsCorporate(rs.getBoolean("is_corporate"));
            tariff.setCreationDate(toLocalDate(rs.getDate("creation_date")));
            tariff.setDiscount(rs.getDouble("discount"));
            tariff.setPictureUrl(rs.getString("picture_url"));
            tariff.setPrice(rs.getDouble("price"));
        } catch (SQLException e) {
            throw new EntityInitializationException(e);
        }
        return tariff;
    }


    @Override
    public List<Tariff> getByRegionId(Long regionId) {
        List<Tariff> tariffs = new ArrayList<>();
        String query = this.getQuery("getAllAvailable");
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setLong(1, regionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tariffs.add(init(rs));
            }
        } catch (SQLException e) {
            throw new EntityNotFoundException(regionId, e);
        }
        return tariffs;
    }

    @Override
    public void updateTariffStatus(long tariffId, ProductStatus productStatus) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("updateStatus"))) {
            ps.setString(1, productStatus.name());
            ps.setLong(2, tariffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new EntityModificationException(tariffId, e);
        }
    }

    @Override
    public Tariff findByTariffName(String tariffName) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("findByTariffName"))) {
            ps.setString(1, tariffName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return init(rs);
            }
        } catch (SQLException e) {
            throw new EntityNotFoundException(tariffName, e);
        }
        return null;
    }

    @Override
    public String prepareWhereClause(Object... args) {
        String where = " where ";
        String name = (String) args[0];
        int status = (int) args[1];
        int type = (int) args[2];
        Date from = (Date) args[3];
        Date to = (Date) args[4];
        Boolean needAnd = false;
        if (name.length() > 0) {
            where += " t.tariff_name LIKE CONCAT('%',?,'%') ";
            this.preparedStatementParams.add(name);
            needAnd = true;
        }
        if (status == 1 || status == 2) {
            where += needAnd(needAnd);
            needAnd = true;
            where += prepareStatusClause(status);
        }
        if (type == 1 || type == 2) {
            where += needAnd(needAnd);
            needAnd = true;
            where += prepareTypeClause(type);
        }
        where += prepareDateWhereClause(from, to, needAnd);
        if (where.length() > 7)
            return where;
        else
            return "";
    }

    private String prepareStatusClause(int status) {
        String where = "";
        where += " t.product_status = ? ";
        if (status == 1)
            this.preparedStatementParams.add("ACTIVATED");
        else
            this.preparedStatementParams.add("DEACTIVATED");
        return where;
    }

    private String prepareTypeClause(int type) {
        String where = "";
        where += " t.is_corporate = ? ";
        if (type == 1)
            this.preparedStatementParams.add(true);
        else
            this.preparedStatementParams.add(false);
        return where;
    }

    private String prepareDateWhereClause(Date from, Date to, boolean needAnd) {
        String where = "";
        if (from != null && to != null) {
            where += needAnd(needAnd);
            where += " t.creation_date BETWEEN ? AND ? ";
            this.preparedStatementParams.add(from);
            this.preparedStatementParams.add(to);
        }
        if (from != null) {
            where += needAnd(needAnd);
            where += " t.creation_date >= ?";
            this.preparedStatementParams.add(from);
        }
        if (to != null) {
            where += needAnd(needAnd);
            where += " t.creation_date <= ?";
            this.preparedStatementParams.add(to);
        }
        return where;
    }

    private String needAnd(boolean needEnd) {
        if (needEnd) {
            return "and ";
        }
        return "";
    }

    @Override
    public List<Tariff> getTariffsAvailableForCustomer(long regionId, int page, int size) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("getAllWithRegionPrice"))) {
            ps.setObject(1, regionId);
            ps.setObject(2, size);
            ps.setObject(3, page * size);
            ResultSet rs = ps.executeQuery();
            List<Tariff> result = new ArrayList<>();
            while (rs.next()) {
                result.add(init(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new CrudException("Failed to load all the entities. " +
                    "Check your database connection or whether sql query is right", e);
        }
    }

    @Override
    public List<Tariff> getTariffsAvailableForCustomer(long regionId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("getAllActivatedWithRegionPrice"))) {
            ps.setObject(1, regionId);
            ResultSet rs = ps.executeQuery();
            List<Tariff> result = new ArrayList<>();
            while (rs.next()) {
                result.add(init(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new CrudException("Failed to load all the entities. " +
                    "Check your database connection or whether sql query is right", e);
        }
    }

    @Override
    public Integer getCountTariffsAvailableForCustomer(long regionId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("getCountWithRegionPrice"))) {
            ps.setObject(1, regionId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new CrudException("Failed to load all the entities. " +
                    "Check your database connection or whether sql query is right", e);
        }
    }

    @Override
    public List<Tariff> getTariffsAvailableForCorporate(int page, int size) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("getTariffsAvailableForCorporate"))) {
            ps.setObject(1, size);
            ps.setObject(2, page * size);
            ResultSet rs = ps.executeQuery();
            List<Tariff> result = new ArrayList<>();
            while (rs.next()) {
                result.add(init(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new CrudException("Failed to load all the entities. " +
                    "Check your database connection or whether sql query is right", e);
        }
    }

    @Override
    public List<Tariff> getTariffsAvailableForCorporate() {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("getAllTariffsAvailableForCorporate"))) {
            ResultSet rs = ps.executeQuery();
            List<Tariff> result = new ArrayList<>();
            while (rs.next()) {
                result.add(init(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new CrudException("Failed to load all the entities. " +
                    "Check your database connection or whether sql query is right", e);
        }
    }

    @Override
    public Integer getCountTariffsAvailableForCorporate() {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("getCountAvailableForCorporate"))) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new CrudException("Failed to load all the entities. " +
                    "Check your database connection or whether sql query is right", e);
        }
    }

    @Override
    public Tariff getByIdForSingleCustomer(long id, long regionId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(this.getQuery("getByIdForSingleCustomer"))) {
            ps.setObject(1, id);
            ps.setObject(2, regionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return this.init(rs);
            }
        } catch (SQLException e) {
            throw new CrudException("Failed to load all the entities. " +
                    "Check your database connection or whether sql query is right", e);
        }
        return null;
    }
}
