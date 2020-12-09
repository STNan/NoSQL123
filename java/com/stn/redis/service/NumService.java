package com.stn.redis.service;

import com.stn.redis.beans.CounterMap;
import com.stn.redis.util.JedisUtil;

public class NumService implements RedisService {

    private CounterMap counterMap;

    @Override
    public String parse() {
        String res = "没有进行有效操作";
        String key = counterMap.getKeyFields();
        String value = counterMap.getValueFields();
        int expireTime = counterMap.getExpireTime();
        // 有keyFields字段时
        if (key != null) {
            // key在redis中存在时
            if (JedisUtil.exists(key) && JedisUtil.type(key).equals("string")) {
                // 有valueFields时
                if (value != null) {
                    // 有expireTime时
                    long val = Long.parseLong(value);
                    if (expireTime != 0) {
                        JedisUtil.incrBy(key, val);
                        JedisUtil.expire(key, expireTime);
                        res = "键" + key + "变化了" + val + "，距离过期还有" + expireTime + "秒" + "，现在为：" + JedisUtil.get(key);
                    } else {    // 没有expireTime时（为0）
                        JedisUtil.incrBy(key, val);
                        res = "键" + key + "变化了" + val + "，现在为：" + JedisUtil.get(key);
                    }
                } else {    // 没有valueFields时
                    // 有expireTime时
                    if (expireTime != 0) {
                        JedisUtil.expire(key, expireTime);
                        res = "键值为：" + JedisUtil.get(key) + "，新设置过期时间为" + expireTime + "秒";
                    } else {    // 没有expireTime时（为0）
                        res = "键值为：" + JedisUtil.get(key);
                    }
                }
            } else {    // key在redis中不存在时
                // 有valueFields
                if (value != null) {
                    // 有expireTime时
                    if (expireTime != 0) {
                        JedisUtil.setEx(key, expireTime, value);
                        res = "新增键：" + key + "，键值为：" + value + "，过期时间为：" + expireTime;
                    } else {    // 没有expireTime时(为0)
                        JedisUtil.set(key, value);
                        res = "新增键：" + key + "，键值为：" + value;
                    }
                } else {    // 没有valueFields
                    res = "没有找到需要展示的键";
                }
            }
        }
        return res;
    }
}
