package com.ultimismc.parties.common.connection;

public interface Connection<P> {

    P getPool();

    <R> R getResource();

    Credentials getCredentials();

    ConnectionResult open();

}
