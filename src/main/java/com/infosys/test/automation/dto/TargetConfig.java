package com.infosys.test.automation.dto;

import com.infosys.test.automation.connectors.DataReader;
import com.infosys.test.automation.utils.DataReaderUtils;

import java.util.List;
import java.util.Properties;

public class TargetConfig {
    private String name;
    private String type;
    private Properties targetProperties;
    private CondConfig joinCondConfig;
    private List<String> parentRecords = null;
    private TargetConfig(){

    }
    private TargetConfig(String name, String type, Properties targetProperties, CondConfig joinCondConfig){
        this.name = name;
        this.type = type;
        this.targetProperties = targetProperties;
        this.joinCondConfig = joinCondConfig;
    }

    public List<String> readTargetData(List<String> parentData) throws Exception {
        parentRecords = parentData;
        DataReader targetReader = DataReaderUtils.createReader(this.type,this.name,this.targetProperties,this.parentRecords,null, joinCondConfig);
        this.parentRecords = targetReader.getData();
        return this.parentRecords;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> target");
        stringBuilder.append(" , name -> "+this.name);
        stringBuilder.append(" , type -> "+this.type);
        stringBuilder.append(" , target properties -> "+this.targetProperties);
        if (joinCondConfig != null){
            stringBuilder.append(" , join condition -> "+this.joinCondConfig);
        }
        stringBuilder.append(" }");
        return stringBuilder.toString();
    }

    public static class TargetConfigBuilder {
        private String name;
        private String type;
        private Properties targetProperties = new Properties();
        private CondConfig joinCondConfig;
        public TargetConfigBuilder createBuilder(){
            return new TargetConfigBuilder();
        }
        public TargetConfigBuilder setName(String name){
            this.name = name;
            return this;
        }
        public TargetConfigBuilder setType(String type){
            this.type = type;
            return this;
        }
        public TargetConfigBuilder addProperty(String key, String value){
            this.targetProperties.setProperty(key,value);
            return this;
        }
        public TargetConfigBuilder setJoinCondition(CondConfig joinCondConfig){
            this.joinCondConfig = joinCondConfig;
            return this;
        }
        public TargetConfig build(){
            return new TargetConfig(name,type,targetProperties, joinCondConfig);
        }
    }
}
