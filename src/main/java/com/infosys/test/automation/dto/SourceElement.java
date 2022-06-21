package com.infosys.test.automation.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SourceElement {
    private String name;
    private String type;
    private List<SourceElement> dependentSources;
    private Properties sourceProperties;
    private FilterCondElement filterCondElement;
    private JoinCondElement joinCondElement;
    private SourceElement(){

    }
    private SourceElement(String name, String type, List<SourceElement> dependentSources, Properties sourceProperties,FilterCondElement filterCondElement,JoinCondElement joinCondElement){
        this.name = name;
        this.type = type;
        this.dependentSources = dependentSources;
        this.sourceProperties = sourceProperties;
        this.filterCondElement = filterCondElement;
        this.joinCondElement = joinCondElement;
    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ element type -> source");
        stringBuilder.append(" , name -> "+this.name);
        stringBuilder.append(" , type -> "+this.type);
        stringBuilder.append(" , source properties -> "+this.sourceProperties);
        if (dependentSources.size() > 0) {
        stringBuilder.append(" , dependent sources -> [\n");
            for (SourceElement processElement : dependentSources) {
                stringBuilder.append(processElement.toString());
                stringBuilder.append("\n");
            }
        stringBuilder.append("]");
        }
        if (filterCondElement != null){
            stringBuilder.append(" , filter condition -> "+this.filterCondElement);
        }
        if (joinCondElement != null){
            stringBuilder.append(" , join condition -> "+this.joinCondElement);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static class SourceElementBuilder{
        private String name;
        private String type;
        private List<SourceElement> dependentSources = new ArrayList<SourceElement>();
        private Properties sourceProperties = new Properties();
        private FilterCondElement filterCondElement;
        private JoinCondElement joinCondElement;
        public SourceElement.SourceElementBuilder createBuilder(){
            return new SourceElement.SourceElementBuilder();
        }
        public SourceElement.SourceElementBuilder setName(String name){
            this.name = name;
            return this;
        }
        public SourceElement.SourceElementBuilder setType(String type){
            this.type = type;
            return this;
        }
        public SourceElement.SourceElementBuilder addDependetSource(SourceElement processElement){
            this.dependentSources.add(processElement);
            return this;
        }
        public SourceElement.SourceElementBuilder addProperty(String key, String value){
            this.sourceProperties.setProperty(key,value);
            return this;
        }
        public SourceElementBuilder setFilterCondition(FilterCondElement filterCondElement){
            this.filterCondElement = filterCondElement;
            return this;
        }
        public SourceElementBuilder setJoinCondition(JoinCondElement joinCondElement){
            this.joinCondElement = joinCondElement;
            return this;
        }
        public SourceElement build(){
            return new SourceElement(name,type,dependentSources,sourceProperties,filterCondElement,joinCondElement);
        }
    }

}
