package com.stn.redis.service;

import com.stn.redis.beans.CounterMap;
import com.stn.redis.util.JedisUtil;

import java.util.List;

public class ListService implements RedisService {

    private CounterMap counterMap;

    @Override
    public String parse() {
        String res = "没有进行有效操作";
        String key = counterMap.getKeyFields();
        String value = counterMap.getValueFields();
        int expireTime = counterMap.getExpireTime();
        if(key != null) {
            if(JedisUtil.exists(key)) {
                if(JedisUtil.type(key).equals("list")) {
                    if(value != null) {
                        if(expireTime != 0) {
                            JedisUtil.lpush(key, value);
                            JedisUtil.expire(key, expireTime);
                            res = "键：" + key + "中列表添加新值：" + value + "，key的过期时间为" + JedisUtil.ttl(key);
                        } else {
                            JedisUtil.lpush(key, value);
                            res = "键：" + key + "中列表添加新值：" + value;
                        }
                    } else {
                        if(expireTime != 0) {
                            JedisUtil.expire(key, expireTime);
                            List<String> list = JedisUtil.lrange(key, 0, -1);
                            res = "键：" + key + "中列表的值如下：";
                            for(int i = 0; i < list.size(); i++) {
                                res += list.get(i) + " ";
                            }
                        }
                    }
                }
            } else {
                if(value != null) {
                    if(expireTime != 0) {
                        JedisUtil.lpush(key, value);
                        JedisUtil.expire(key, expireTime);
                        res = "键：" + key + "中列表添加新值：" + value + "，key的过期时间为：" + expireTime;
                    } else {
                        JedisUtil.lpush(key, value);
                        res = "键：" + key + "中列表添加新值：" + value;
                    }
                }
            }
        }
        return res;
    }
}
