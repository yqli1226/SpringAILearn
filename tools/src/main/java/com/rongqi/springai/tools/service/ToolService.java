package com.rongqi.springai.tools.service;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ToolService {

    @Autowired
    TicketService ticketService;


    // 告知大模型提供了何种工具
    @Tool(description = "退票")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancel(
            // 告知大模型工具需要何种参数
            @ToolParam(description = "预定号") String ticketNumber,
            @ToolParam(description = "姓名") String name) {

        ticketService.cancel(ticketNumber, name);

        return "退票成功";
    }

    @Tool(description = "查询天气")
    public String queryWeather(@ToolParam(description = "经度") double longitude,
                               @ToolParam(description = "纬度") double latitude) {
        return "下雨天，创世神不喜欢雨天";
    }

    /**
     * 获取tools处理的方法
     * @param toolService
     * @return
     */
    public List<ToolCallback> getToolCallListTemplate(ToolService toolService){
        // 从数据库中读取代码

        // 使用cancel tool举例

        // 1.获取tools处理的方法
        Method method = ReflectionUtils.findMethod(ToolService.class, "cancel", String.class, String.class);

        // 2.构建tool的Definition 动态配置的方式@Tool @ToolParam
        ToolDefinition toolDefinition = ToolDefinition.builder()
                .name("cancel")
                .description("退票")
                .inputSchema("""
                        {
                            "type": "object",
                            "properties": {
                                "ticketNumber": {
                                    "type": "string",
                                    "description": "预定号"
                                },
                                "name": {
                                    "type": "string",
                                    "description": "姓名"
                                }
                            ,
                            "required": ["ticketNumber", "name"]
                        
                        """)
                .build();
        // 3.构建tool的call 一个toolCallback对应一个tool
        ToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(toolDefinition)
                .toolMethod(method)
                .toolObject(toolService)
                .build();


        return List.of(toolCallback);
    }

    /**
     * 获取所有带有@Tool注解的方法的ToolCallBack列表
     */
    public List<ToolCallback> getToolCallList(Object object) {
        return Arrays.stream(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Tool.class))
                .map(method -> createToolCallback(object, method))
                .collect(Collectors.toList());
    }

    /**
     * 为单个方法创建ToolCallback
     * @param object 不可使用new方法创建无法DI
     */
    private ToolCallback createToolCallback(Object object, Method method) {
       try {
           Tool tool = method.getAnnotation(Tool.class);
           ToolDefinition toolDefinition = ToolDefinition.builder()
                   .name(tool.name())
                   .description(tool.description())
                   .inputSchema(buildInputSchema(method))
                   .build();

           return MethodToolCallback.builder()
                   .toolDefinition(toolDefinition)
                   .toolMethod(method)
                   .toolObject(object)
                   .build();
       } catch (Exception e) {
           System.err.println("Failed to create tool callback for method: " + method.getName());
           e.printStackTrace();
           return null;
       }
    }

    /**
     * 根据方法参数构建JSON Schema
     * @param method
     * @return
     */
    private String buildInputSchema(Method method) {
        StringBuilder schemaBuilder = new StringBuilder();
        schemaBuilder.append("""
                {
                        "type": "object",
                    "properties": {
                """);

        Parameter[] parameters = method.getParameters();
        // 存方法参数
        List<String> requiredParams = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            ToolParam toolParam = parameter.getAnnotation(ToolParam.class);

            if (toolParam != null) {
                // 添加参数定义
                schemaBuilder.append(String.format("""
                                "%s": {
                                    "type":"%s",
                                    "description":"%s"
                                }""",
                        parameter.getName(),
                        getJsonSchemaType(parameter.getType()),
                        toolParam.description()));

                // 添加逗号分隔符
                if (i < parameters.length - 1) {
                    schemaBuilder.append(",");
                }

                requiredParams.add(parameter.getName());
            }
        }

        schemaBuilder.append("""
                },
                "required": [
                """);

        for (int i = 0; i < requiredParams.size(); i++) {
            schemaBuilder.append(String.format("\"%s\"", requiredParams.get(i)));
            if (i < requiredParams.size() - 1) {
                schemaBuilder.append(",");
            }
        }

        schemaBuilder.append("]");
        schemaBuilder.append("}");

        return schemaBuilder.toString();
    }

    /**
     * 将Java类型转换为JSON Schema类型
     * @param type
     * @return
     */
    private Object getJsonSchemaType(Class<?> type) {
        if (type == String.class) {
            return "string";
        } else if (type == Integer.class || type == int.class) {
            return "integer";
        } else if (type == Boolean.class || type == boolean.class) {
            return "boolean";
        } else if (type == Double.class || type == double.class) {
            return "number";
        } else if (type == List.class) {
            return "array";
        } else if (type == Map.class) {
            return "object";
        } else {
            return "string";
        }
    }


}
