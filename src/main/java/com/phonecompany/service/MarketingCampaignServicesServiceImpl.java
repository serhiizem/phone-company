package com.phonecompany.service;

import com.phonecompany.annotations.ServiceStereotype;
import com.phonecompany.dao.interfaces.MarketingCampaignServicesDao;
import com.phonecompany.model.MarketingCampaignServices;
import com.phonecompany.service.interfaces.MarketingCampaignServicesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceStereotype
public class MarketingCampaignServicesServiceImpl extends CrudServiceImpl<MarketingCampaignServices>
        implements MarketingCampaignServicesService {

    private MarketingCampaignServicesDao marketingCampaignServicesDao;

    private static final Logger LOG = LoggerFactory.getLogger(MarketingCampaignServicesServiceImpl.class);

    @Autowired
    public MarketingCampaignServicesServiceImpl(MarketingCampaignServicesDao marketingCampaignServicesDao) {
        this.marketingCampaignServicesDao = marketingCampaignServicesDao;
    }

}
