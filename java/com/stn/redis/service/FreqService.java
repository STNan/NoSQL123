package com.stn.redis.service;

import com.stn.redis.beans.CounterMap;
import com.stn.redis.util.DateSplitUtils;
import com.stn.redis.util.DateUtil;
import com.stn.redis.util.JedisUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FreqService implements RedisService {

    private CounterMap counterMap;

    @Override
    public String parse() throws ParseException {
        String res = "没有进行有效操作";
        String key = counterMap.getKeyFields();
        String field = counterMap.getFields();
        String value = counterMap.getValueFields();
        // 有keyFields字段时
        if(key != null) {
            if(JedisUtil.exists(key)) {
                if(field != null) {
                    String[] t = DateUtil.StringFormat(field);
                    if(t.length == 1) {
                        if(JedisUtil.hexists(key, t[0])) {
                            if(value != null) {
                                long val = Long.parseLong(value);
                                JedisUtil.hincrBy(key, t[0], val);
                                res = "键:" + key + "，时段：" + t[0] + "，变化了" + val + "，现在为：" + JedisUtil.hget(key, t[0]);
                            } else {
                                res = "键:" + key + "，时段：" + t[0] + "值为：" + JedisUtil.hget(key, t[0]);
                            }
                        } else {
                            if(value != null) {
                                JedisUtil.hset(key, t[0], value);
                                res = "设置键" + key + "，时段：" + t[0] + "，值为：" + value;
                            } else {
                                res = "没有找到当前时段数据";
                            }
                        }
                    } else if (t.length == 2) {
                        String startStr = t[0];
                        String endStr = t[1];
                        SimpleDateFormat strToDate = new SimpleDateFormat("yyyyMMddHHmm");
                        Date start = strToDate.parse(startStr);
                        Date end = strToDate.parse(endStr);
                        List<DateSplitUtils.DateSplit> dateSplits = DateSplitUtils.splitDate(start, end, DateSplitUtils.IntervalType.HOUR, 1);
                        List<String> timeKeys = new ArrayList<>();
                        for(int i = 0; i < dateSplits.toArray().length; i++)
                        {
                            timeKeys.add(dateSplits.get(i).getStartDateTimeStr());
                        }
                        long total = 0;
                        for(int i = 0; i < timeKeys.size(); i++)
                        {
                            if(JedisUtil.hexists(key, timeKeys.get(i))) {
                                total += Long.parseLong(JedisUtil.hget(key, timeKeys.get(i)));
                            }
                        }
                        res = "键:" + key + "，时段" + startStr + "-->" + endStr + "，总和为：" + total;
                    }
                }
            } else {
                if(field != null) {
                    String[] t = DateUtil.StringFormat(field);
                    if(t.length == 1) {
                        if(value != null) {
                            JedisUtil.hset(key, t[0], value);
                            res = "键:" + key + "，时段：" + t[0] + "，设置值为：" + value;
                        }
                    }
                }
            }
        }

        return res;
    }
}
