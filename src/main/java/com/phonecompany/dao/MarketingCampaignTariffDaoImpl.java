package com.phonecompany.dao;

import com.phonecompany.dao.interfaces.MarketingCampaignTariffDao;
import com.phonecompany.dao.interfaces.TariffRegionDao;
import com.phonecompany.exception.dao_layer.EntityInitializationException;
import com.phonecompany.exception.dao_layer.PreparedStatementPopulationException;
import com.phonecompany.model.MarketingCampaignTariff;
import com.phonecompany.util.TypeMapper;
import com.phonecompany.util.interfaces.QueryLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MarketingCampaignTariffDaoImpl extends JdbcOperationsImpl<MarketingCampaignTariff> implements MarketingCampaignTariffDao{

    private QueryLoader queryLoader;
    private TariffRegionDao tariffRegionDao;

    @Autowired
    public MarketingCampaignTariffDaoImpl(QueryLoader queryLoader, TariffRegionDao tariffRegionDao) {
        this.queryLoader = queryLoader;
        this.tariffRegionDao = tariffRegionDao;
    }

    @Override
    public String getQuery(String type) {
        return queryLoader.getQuery("query.marketing_campaign_tariff." + type);
    }

    @Override
    public void populateSaveStatement(PreparedStatement preparedStatement, MarketingCampaignTariff entity) {
        try {
            preparedStatement.setObject(1, TypeMapper.getNullableId(entity.getTariffRegion()));
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public void populateUpdateStatement(PreparedStatement preparedStatement, MarketingCampaignTariff entity) {
        try {
            preparedStatement.setObject(1, TypeMapper.getNullableId(entity.getTariffRegion()));
            preparedStatement.setLong(2, entity.getId());
        } catch (SQLException e) {
            throw new PreparedStatementPopulationException(e);
        }
    }

    @Override
    public MarketingCampaignTariff init(ResultSet rs) {
        MarketingCampaignTariff marketingCampaignTariff = new MarketingCampaignTariff();
        try {
            marketingCampaignTariff.setId(rs.getLong("id"));
            marketingCampaignTariff.setTariffRegion(tariffRegionDao.getById(rs.getLong("tariff_region_id")));
        } catch (SQLException e) {
            throw new EntityInitializationException(e);
        }
        return marketingCampaignTariff;
    }

    @Override
    public List<MarketingCampaignTariff> getMarketingCampaignTariffsAvailableForCustomer(Long regionId) {
        return this.executeForList(this.getQuery("getAllAvailableForCustomer"), new Object[]{regionId});
    }
}
