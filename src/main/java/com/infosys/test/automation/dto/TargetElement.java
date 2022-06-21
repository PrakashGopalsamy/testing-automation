package com.infosys.test.automation.dto;

import java.util.Properties;

public class TargetElement {
    private String name;
    private String type;
    private Properties targetProperties;
    private TargetElement(){

    }
    private TargetElement(String name,String type,Properties targetProperties){
        this.name = name;
        this.type = type;
        this.targetProperties = targetProperties;

    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> target");
        stringBuilder.append(" , name -> "+this.name);
        stringBuilder.append(" , type -> "+this.type);
        stringBuilder.append(" , target properties -> "+this.targetProperties);
        stringBuilder.append(" }");
        return stringBuilder.toString();
    }

    public static class TargetElementBuilder{
        private String name;
        private String type;
        private Properties targetProperties = new Properties();
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
        public TargetElement build(){
            return new TargetElement(name,type,targetProperties);
        }
    }
}
