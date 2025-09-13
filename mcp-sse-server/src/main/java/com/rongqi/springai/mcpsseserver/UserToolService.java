package com.rongqi.springai.mcpsseserver;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserToolService {

    Map<String, Double> map = Map.of(
            "关羽", 99D,
            "张飞", 90D,
            "赵云", 92D,
            "马超", 88D
    );

    @Tool(description = "获取武将武力值")
    public String getHeroPower(@ToolParam(description = "姓名") String name) {
        if (map.containsKey(name)) {
            return name + "武力值为：" + map.get(name);
        }

        return "你还未获得该武将";
    }

}