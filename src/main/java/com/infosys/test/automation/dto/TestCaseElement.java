package com.infosys.test.automation.dto;

import java.util.Properties;

public class TestCaseElement {
    private String name;
    private String type;
    private Properties testCaseProperties;
    private TestCaseElement(){

    }
    private TestCaseElement(String name,String type,Properties testCaseProperties){
        this.name = name;
        this.type = type;
        this.testCaseProperties = testCaseProperties;

    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> testcase");
        stringBuilder.append(" , name -> "+this.name);
        stringBuilder.append(" , type -> "+this.type);
        stringBuilder.append(" , testCase properties -> "+this.testCaseProperties);
        stringBuilder.append(" }");
        return stringBuilder.toString();
    }

    public static class TestCaseElementBuilder{
        private String name;
        private String type;
        private Properties testCaseProperties = new Properties();
        public TestCaseElement.TestCaseElementBuilder createBuilder(){
            return new TestCaseElement.TestCaseElementBuilder();
        }
        public TestCaseElement.TestCaseElementBuilder setName(String name){
            this.name = name;
            return this;
        }
        public TestCaseElement.TestCaseElementBuilder setType(String type){
            this.type = type;
            return this;
        }
        public TestCaseElement.TestCaseElementBuilder addProperty(String key, String value){
            this.testCaseProperties.setProperty(key,value);
            return this;
        }
        public TestCaseElement build(){
            return new TestCaseElement(name,type,testCaseProperties);
        }
    }
}
