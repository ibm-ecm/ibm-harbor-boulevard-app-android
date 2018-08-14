package com.futureworkshops.datacap.common.configuration;

/**
 * Default configuration class that provides common parameters for Datacap API.
 */
public abstract class AbstractDatacapConfiguration implements IDatacapConfiguration {

    private static final String ENDPOINT = "http://ecm1.fws.io:8070/ServicewTM.svc";
    private static final String USER = "admin";
    private static final String PASSWORD = "admin";
    private static final String STATION_ID = "1";

    @Override
    public String getEndpoint() {
        return ENDPOINT;
    }

    @Override
    public String getUsername() {
        return USER;
    }

    @Override
    public String getPassword() {
        return PASSWORD;
    }

    @Override
    public String getStation() {
        return STATION_ID;
    }
}
