package com.futureworkshops.android.harborboulevard.domain;

import com.futureworkshops.datacap.common.configuration.AbstractDatacapConfiguration;

/**
 * Configuration used by the harbor boulevard sample app
 */
public class HarbourBoulevardDatacapConfiguration extends AbstractDatacapConfiguration {

    @Override
    public String getDatacapApplication() {
        return "AccountOpening";
    }
}
