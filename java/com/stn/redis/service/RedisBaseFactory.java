package com.stn.redis.service;

import com.stn.redis.beans.CounterMap;

import java.util.HashMap;
import java.util.Map;

public class RedisBaseFactory {

    private Map<String, RedisService> listTypeServiceMap = new HashMap<>();

    public RedisBaseFactory() {
        listTypeServiceMap.put("num", new NumService());
        listTypeServiceMap.put("freq", new FreqService());
        listTypeServiceMap.put("str", new StrService());
        listTypeServiceMap.put("list", new ListService());
        listTypeServiceMap.put("set", new SetService());
        listTypeServiceMap.put("zset", new ZsetService());
    }

    public RedisService getResolver(String type, CounterMap counterMap) {
        return listTypeServiceMap.get(type);
    }

}
