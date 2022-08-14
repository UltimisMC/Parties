package com.ultimismc.parties.common.connection.redis;

import com.ultimismc.parties.common.connection.Connection;
import com.ultimismc.parties.common.connection.ConnectionResult;
import com.ultimismc.parties.common.connection.Credentials;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisConnection implements Connection<JedisPool> {

    private final Credentials credentials;
    private final JedisPool jedisPool;
    @Getter private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public RedisConnection(Credentials credentials) {
        this.credentials = credentials;
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(1000);
        config.setMaxIdle(0);
        this.jedisPool = new JedisPool(
                config,
                credentials.getHost(),
                credentials.getPort(),
                5000,
                credentials.getPassword()
        );
    }

    @Override
    public JedisPool getPool() {
        return jedisPool;
    }

    @Override
    public Credentials getCredentials() {
        return credentials;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Jedis getResource() {
        return getPool().getResource();
    }

    @Override
    public ConnectionResult open() {
        try (Jedis jedis = getResource()) {
            jedis.ping();
            return ConnectionResult.SUCCESS;
        } catch (Exception exception) {
            return ConnectionResult.FAILURE;
        }
    }

    public void subscribe(RedisChannel channel) {
        executorService.execute(() -> {
            try (Jedis jedis = getResource()) {
                jedis.subscribe(channel, channel.getChannel());
            }
        });
    }

}
