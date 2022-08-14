package com.ultimismc.parties.common.connection.redis;

import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

public abstract class RedisChannel extends JedisPubSub {

    @Getter private final String channel;

    public RedisChannel(String channel) {
        this.channel = channel;
    }

    public abstract void handle(String message);

    @Override
    public void onMessage(String channel, String message) {
        if (!this.channel.equals(channel))
            return;
        handle(message);
    }

}