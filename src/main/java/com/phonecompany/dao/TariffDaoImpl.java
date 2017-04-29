package com.phonecompany.dao;

import com.phonecompany.dao.interfaces.TariffDao;
import com.phonecompany.exception.*;
import com.phonecompany.model.Tariff;
import com.phonecompany.model.enums.ProductStatus;
import com.phonecompany.util.QueryLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TariffDaoImpl extends CrudDaoImpl<Tariff> implements TariffDao {

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
    public void populateSaveStatement(PreparedStatement preparedStatement, Tariff entity) {
        try {
            preparedStatement.setString(1, entity.getTariffName());
            preparedStatement.setString(2, entity.getProductStatus().name());
            preparedStatement.setString(3, entity.getInternet());
            preparedStatement.setString(4, entity.getCallsInNetwork());
            preparedStatement.setString(5, entity.getCallsOnOtherNumbers());
            preparedStatement.setString(6, entity.getSms());
            preparedStatement.setString(7, entity.getMms());
            preparedStatement.setString(8, entity.getRoaming());
            preparedStatement.setBoolean(9, entity.getIsCorporate());
            preparedStatement.setDate(10, entity.getCreationDate());
            preparedStatement.setDouble(11, entity.getDiscount());
            preparedStatement.setString(12, entity.getPictureUrl());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public void populateUpdateStatement(PreparedStatement preparedStatement, Tariff entity) {
        try {
            preparedStatement.setString(1, entity.getTariffName());
            preparedStatement.setString(2, entity.getProductStatus().name());
            preparedStatement.setString(3, entity.getInternet());
            preparedStatement.setString(4, entity.getCallsInNetwork());
            preparedStatement.setString(5, entity.getCallsOnOtherNumbers());
            preparedStatement.setString(6, entity.getSms());
            preparedStatement.setString(7, entity.getMms());
            preparedStatement.setString(8, entity.getRoaming());
            preparedStatement.setBoolean(9, entity.getIsCorporate());
            preparedStatement.setDate(10, entity.getCreationDate());
            preparedStatement.setDouble(11, entity.getDiscount());
            preparedStatement.setString(12, entity.getPictureUrl());
            preparedStatement.setLong(13, entity.getId());
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
            tariff.setCreationDate(rs.getDate("creation_date"));
            tariff.setDiscount(rs.getDouble("discount"));
            tariff.setPictureUrl(rs.getString("picture_url"));
        } catch (SQLException e) {
            throw new EntityInitializationException(e);
        }
        return tariff;
    }

    @Override
    public List<Tariff> getByRegionIdAndPaging(long regionId, int page, int size) {
        List<Object> params = new ArrayList<>();
        String query = buildQuery(this.getQuery("getAll"), params, regionId);
        query += " LIMIT ? OFFSET ?";
        params.add(size);
        params.add(page * size);

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
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

    private String buildQuery(String query, List params, long regionId) {
        if (regionId != 0) {
            query += " inner join tariff_region as tr on t.id = tr.tariff_id where region_id = ? ";
            params.add(regionId);
        }
        query += " ORDER BY t.creation_date DESC ";

        return query;
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
    public Integer getCountByRegionIdAndPaging(long regionId) {
        String query = this.getQuery("getCount");
        if (regionId != 0) {
            query += " inner join tariff_region as tr on t.id = tr.tariff_id where region_id = ? ";
        }
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (regionId != 0) {
                ps.setLong(1, regionId);
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new EntityNotFoundException(regionId, e);
        }
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

}
