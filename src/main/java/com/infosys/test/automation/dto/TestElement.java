package com.infosys.test.automation.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestElement {
    private String name;
    private String type;
    private List<SourceElement> sources;
    private List<TargetElement> targets;
    private List<TestCaseElement> testCases;
    private Properties testProperties;
    private TestElement(){

    }
    private TestElement(String name, String type, List<SourceElement> sources, List<TargetElement> targets,
                        List<TestCaseElement> testCases, Properties testProperties){
        this.name = name;
        this.type = type;
        this.sources = sources;
        this.targets = targets;
        this.testCases = testCases;
        this.testProperties = testProperties;

    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> test \n");
        stringBuilder.append(" , name -> "+this.name+"\n");
        stringBuilder.append(" , type -> "+this.type+"\n");
        stringBuilder.append(" , Properties -> "+this.testProperties+"\n");
        stringBuilder.append(" , Sources -> [ \n");
        int sourceCnt = sources.size();
        int counter = 0;
        for(SourceElement processElement: sources ){
            if (counter ==0){
                stringBuilder.append(processElement.toString());
            } else{
                stringBuilder.append(","+processElement.toString());
            }
            counter = counter+1;
            stringBuilder.append("\n");
        }
        stringBuilder.append("]\n");
        stringBuilder.append(" , Target -> ");
        for(TargetElement processElement: targets ){
            stringBuilder.append(processElement.toString()+"\n");
        }
        int testCaseCnt = testCases.size();
        counter = 0;
        stringBuilder.append(" , TestCases -> [");
        for(TestCaseElement processElement: testCases ){
            if (counter ==0){
                stringBuilder.append(processElement.toString());
            } else{
                stringBuilder.append(","+processElement.toString());
            }
            counter = counter+1;
            stringBuilder.append("\n");
        }
        stringBuilder.append("]\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static class TestElementBuilder{
        private String name;
        private String type;
        private List<SourceElement> sources = new ArrayList<SourceElement>();
        private List<TargetElement> targets = new ArrayList<TargetElement>();
        private List<TestCaseElement> testCases = new ArrayList<TestCaseElement>();
        private Properties testProperties = new Properties();
        public TestElementBuilder createBuilder(){
            return new TestElementBuilder();
        }
        public TestElementBuilder setName(String name){
            this.name = name;
            return this;
        }
        public TestElementBuilder setType(String type){
            this.type = type;
            return this;
        }
        public TestElementBuilder addSource(SourceElement processElement){
            this.sources.add(processElement);
            return this;
        }
        public TestElementBuilder addTarget(TargetElement processElement){
            this.targets.add(processElement);
            return this;
        }
        public TestElementBuilder addTestCase(TestCaseElement processElement){
            this.testCases.add(processElement);
            return this;
        }
        public TestElementBuilder addProperty(String key, String value){
            this.testProperties.setProperty(key,value);
            return this;
        }
        public TestElement build(){
            return new TestElement(name,type,sources,targets,testCases,testProperties);
        }
    }

}
