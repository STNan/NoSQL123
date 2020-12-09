package com.stn.redis.service;

import com.stn.redis.beans.CounterMap;
import redis.clients.jedis.Jedis;

import java.text.ParseException;

public interface RedisService {
    String parse() throws ParseException;
}
