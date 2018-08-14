package com.futureworkshops.android.autocapture.domain;

import com.futureworkshops.datacap.common.configuration.AbstractDatacapConfiguration;

/**
 * Configuration used by the autocapture sample app
 */
public class AutocaptureDatacapConfiguration extends AbstractDatacapConfiguration {

    @Override
    public String getDatacapApplication() {
        return "MobileTemplate";
    }
}
