package com.ultimismc.parties.common.platform;

import com.ultimismc.parties.common.connection.Connection;
import redis.clients.jedis.JedisPool;

public interface PlatformPlugin<P> {

    Connection<JedisPool> getRedis();

}
