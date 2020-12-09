package com.stn.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stn.redis.beans.ActionMap;
import com.stn.redis.beans.CounterBean;
import com.stn.redis.beans.CounterMap;
import com.stn.redis.service.RedisBaseFactory;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    // counter映射
    private static final HashMap<String, CounterMap> counterMap = new HashMap<>();
    // action映射
    private static final HashMap<String, ActionMap> actionMap = new HashMap<>();
    // typeFactory
    private static final RedisBaseFactory REDIS_BASE_FACTORY = new RedisBaseFactory();
    // 终端输入
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    // 终端输出
    private static final PrintWriter out = new PrintWriter(System.err, true);

    public static void loadConfigJson() {
        try {
            out.println();
            out.println("读取Json配置文件中...");
            // 清空
            counterMap.clear();
            actionMap.clear();
            String actionsPath = "src/main/resources/actions.json";
            String countersPath = "src/main/resources/counters.json";

            //获取配置文件，并转为String
            InputStream inputStream = new FileInputStream(actionsPath);
            String actionsText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream = new FileInputStream(countersPath);
            String countersText = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            // 将配置文件中的action转为实体类并放入actionMap中
            JSONObject obj1 = JSON.parseObject(actionsText);
            JSONArray actions = obj1.getJSONArray("actions");
            for(int i = 0; i < actions.size(); i++) {
                String actionName = (String) actions.getJSONObject(i).get("name");
                List<CounterBean> retrieve = new ArrayList<>();
                List<CounterBean> save = new ArrayList<>();
                JSONArray retrieveArray = actions.getJSONObject(i).getJSONArray("retrieve");
                JSONArray saveArray = actions.getJSONObject(i).getJSONArray("save");
                for(int j = 0; j < retrieveArray.size(); j++) {
                    retrieve.add(retrieveArray.getJSONObject(j).toJavaObject(CounterBean.class));
                }
                for(int j = 0; j < saveArray.size(); j++) {
                    save.add(saveArray.getJSONObject(j).toJavaObject(CounterBean.class));
                }
                ActionMap actionMap = new ActionMap(retrieve, save);
                Main.actionMap.put(actionName, actionMap);
            }

            // 将配置文件中的counter转为实体类并放入counterMap中
            JSONObject obj2 = JSON.parseObject(countersText);
            JSONArray counters = obj2.getJSONArray("counters");
            for(int i = 0; i < counters.size(); i++) {
                CounterMap counterMap = counters.getJSONObject(i).toJavaObject(CounterMap.class);
                Main.counterMap.put(counterMap.getCounterName(), counterMap);
            }
            out.println("读取完毕！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<CounterMap> resolveAction(String actionName) {
        ActionMap actionMap = Main.actionMap.get(actionName);
        List<CounterMap> counterList = new ArrayList<>();
        actionMap.getRetrieve().forEach(counterConfig -> {
            counterList.add(counterMap.get(counterConfig.getCounterName()));
        });
        actionMap.getSave().forEach(counterConfig -> {
            counterList.add(counterMap.get(counterConfig.getCounterName()));
        });
        return counterList;
    }

    public static void resolveCounters(List<CounterMap> counterList) {
        counterList.forEach(counter -> {
            System.out.println(counter.getCounterName() + "执行中...");
            String res = null;
            try {
                res = REDIS_BASE_FACTORY.getResolver(counter.getType(), counter).parse();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(res);
        });
        out.println();
    }

    private static int getChoice() throws IOException {
        int input;
        do {
            try {
                out.println();
                out.print("[0]  退出\n"
                        + "[1]  显示所有actions\n"
                        + "[2]  执行action\n"
                        + "请选择> ");
                out.flush();

                input = Integer.parseInt(in.readLine());

                out.println();

                if (0 <= input && 3 >= input) {
                    break;
                } else {
                    out.println("错误的选择:  " + input);
                }
            } catch (NumberFormatException nfe) {
                out.println(nfe);
            }
        } while (true);

        return input;
    }

    private static void showAllActions() {
        out.println("您配置中的actions如下：");
        actionMap.forEach((name, action) -> out.println(name));
    }

    private static void toResolveAction() throws IOException {
        out.print("请输入想要执行的action：");
        out.flush();
        String name = in.readLine();
        // 去actionMap中查询是否存在
        if (actionMap.containsKey(name)) {
            // 将指定action中Counter取出
            List<CounterMap> counterList = resolveAction(name);
            // 执行counters
            resolveCounters(counterList);
        } else {
            out.println("您输入的action不存在");
        }
    }


    public static void main(String[] args) throws Exception {
        // 加载Json配置文件
        loadConfigJson();
        out.println("开始启动...");
        // 获取用户所选择的操作
        int choice = getChoice();
        while (choice != 0) {
            if (choice == 1) {
                showAllActions();
            } else if (choice == 2) {
                toResolveAction();
            }
            choice = getChoice();
        }
    }

    public static HashMap<String, CounterMap> getCounterMap() {
        return counterMap;
    }

    public static HashMap<String, ActionMap> getActionMap() {
        return actionMap;
    }
}
