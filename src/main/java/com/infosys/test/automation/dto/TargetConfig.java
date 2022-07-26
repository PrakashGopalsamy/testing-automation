package com.infosys.test.automation.dto;

import com.infosys.test.automation.connectors.DataReader;
import com.infosys.test.automation.exceptions.InvalidTargetConfigException;
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
    private TargetConfig(String name, String type, Properties targetProperties, CondConfig joinCondConfig) throws InvalidTargetConfigException {
        this.name = name;
        this.type = type;
        this.targetProperties = targetProperties;
        this.joinCondConfig = joinCondConfig;
        validateProperties();
    }

    public List<String> readTargetData(List<String> parentData) throws Exception {
        parentRecords = parentData;
        DataReader targetReader = DataReaderUtils.createReader(this.type,this.name,this.targetProperties,this.parentRecords,null, joinCondConfig);
        this.parentRecords = targetReader.getData();
        return this.parentRecords;
    }

    private void validateProperties() throws InvalidTargetConfigException {
        boolean validProps = true;
        StringBuilder exceptionMessageBuilder = new StringBuilder();
        if (name == null || name.trim().length() == 0){
            exceptionMessageBuilder.append("The required element \"name\" is not been provided in target config\n");
            validProps=false;
        }
        if (type == null || type.trim().length() == 0){
            exceptionMessageBuilder.append("The required attribute \"type\" is not been provided in the target config \""+name+"\"\n");
            validProps=false;
        }
        if (targetProperties.stringPropertyNames().size() == 0){
            exceptionMessageBuilder.append("No Target config properties like \"sourcecolumns\" is provided in the target config \""+name+"\"\n");
            validProps=false;
        }

        if (!validProps){
            throw new InvalidTargetConfigException(exceptionMessageBuilder.toString());
        }
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
        public TargetConfig build() throws InvalidTargetConfigException {
            return new TargetConfig(name,type,targetProperties, joinCondConfig);
        }
    }
}
