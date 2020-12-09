package com.stn.redis.service;

import com.stn.redis.beans.CounterMap;
import redis.clients.jedis.Jedis;

public class SetService implements RedisService {

    private CounterMap counterMap;
    private Jedis jedis;

    @Override
    public String parse() {
        return null;
    }

}
