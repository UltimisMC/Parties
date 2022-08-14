package com.ultimismc.parties.common.connection;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Credentials {

    private final String host, username, password, database;
    private final int port;

}
