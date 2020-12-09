package com.stn.redis.service;

import com.stn.redis.beans.CounterMap;
import com.stn.redis.util.JedisUtil;

public class StrService implements RedisService {

    private CounterMap counterMap;

    @Override
    public String parse() {
        String res = "没有进行有效操作";
        String key = counterMap.getKeyFields();
        String value = counterMap.getValueFields();
        int expireTime = counterMap.getExpireTime();
        if (key != null) {
            if(JedisUtil.exists(key) && JedisUtil.type(key).equals("string")) {
                if (value != null) {
                    if(expireTime != 0) {
                        JedisUtil.setEx(key, expireTime, value);
                        res = "键：" + key + "，键值为：" + value + "，过期时间为：" + expireTime;
                    } else {
                        JedisUtil.set(key, value);
                        res = "键：" + key + "，键值为：" + value;
                    }
                } else {
                    if(expireTime != 0) {
                        JedisUtil.expire(key, expireTime);
                        res = "键值为：" + JedisUtil.get(key) + "，新设置过期时间为：" + expireTime + "秒";
                    } else {
                        res = "键值为：" + JedisUtil.get(key) + "，过期时间为：" + JedisUtil.ttl(key) + "秒";
                    }
                }
            } else {
                if (value != null) {
                    if(expireTime != 0) {
                        JedisUtil.setEx(key, expireTime, value);
                        res = "键：" + key + "，键值为：" + value + "，过期时间为：" + expireTime;
                    } else {
                        JedisUtil.set(key, value);
                        res = "键：" + key + "，键值为：" + value;
                    }
                }
            }
        }
        return res;
    }

}
