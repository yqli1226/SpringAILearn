package com.rongqi.springai.moremodelstructuredagent.common;

import java.util.Map;

public class AiJob {
    public record Job(JobType jobType, Map<String,String> keyInfos) {

    }

    public enum JobType {

        CANCEL("CANCEL", "取消"),
        QUERY("QUERY", "查询"),
        OTHER("OTHER", "其他");

        private final String code;
        private final String name;

        JobType(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() { return code; }

        public String getName() { return name; }
    }
}
