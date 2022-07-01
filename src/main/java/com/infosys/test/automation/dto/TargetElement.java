package com.infosys.test.automation.dto;

import com.infosys.test.automation.connectors.Connector;
import com.infosys.test.automation.utils.ConnectorUtils;

import java.util.List;
import java.util.Properties;

public class TargetElement {
    private String name;
    private String type;
    private Properties targetProperties;
    private CondElement joinCondElement;
    private List<String> parentRecords = null;
    private TargetElement(){

    }
    private TargetElement(String name,String type,Properties targetProperties,CondElement joinCondElement){
        this.name = name;
        this.type = type;
        this.targetProperties = targetProperties;
        this.joinCondElement = joinCondElement;
    }

    public List<String> readTargetData(List<String> parentData) throws Exception {
        parentRecords = parentData;
        Connector targetConnector = ConnectorUtils.createConnetor(this.type,this.name,this.targetProperties,this.parentRecords,null,joinCondElement);
        this.parentRecords = targetConnector.getData();
        return this.parentRecords;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> target");
        stringBuilder.append(" , name -> "+this.name);
        stringBuilder.append(" , type -> "+this.type);
        stringBuilder.append(" , target properties -> "+this.targetProperties);
        if (joinCondElement != null){
            stringBuilder.append(" , join condition -> "+this.joinCondElement);
        }
        stringBuilder.append(" }");
        return stringBuilder.toString();
    }

    public static class TargetElementBuilder{
        private String name;
        private String type;
        private Properties targetProperties = new Properties();
        private CondElement joinCondElement;
        public TargetElement.TargetElementBuilder createBuilder(){
            return new TargetElement.TargetElementBuilder();
        }
        public TargetElement.TargetElementBuilder setName(String name){
            this.name = name;
            return this;
        }
        public TargetElement.TargetElementBuilder setType(String type){
            this.type = type;
            return this;
        }
        public TargetElement.TargetElementBuilder addProperty(String key, String value){
            this.targetProperties.setProperty(key,value);
            return this;
        }
        public TargetElement.TargetElementBuilder setJoinCondition(CondElement joinCondElement){
            this.joinCondElement = joinCondElement;
            return this;
        }
        public TargetElement build(){
            return new TargetElement(name,type,targetProperties,joinCondElement);
        }
    }
}
