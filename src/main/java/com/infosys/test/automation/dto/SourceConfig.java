package com.infosys.test.automation.dto;

import com.infosys.test.automation.connectors.DataReader;
import com.infosys.test.automation.utils.DataReaderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SourceConfig {
    private String name;
    private String type;
    private List<SourceConfig> dependentSources;
    private Properties sourceProperties;
    private CondConfig filterCondConfig;
    private CondConfig joinCondConfig;
    private List<String> parentRecords = null;
    private SourceConfig(){

    }
    private SourceConfig(String name, String type, List<SourceConfig> dependentSources, Properties sourceProperties, CondConfig filterCondConfig, CondConfig joinCondConfig){
        this.name = name;
        this.type = type;
        this.dependentSources = dependentSources;
        this.sourceProperties = sourceProperties;
        this.filterCondConfig = filterCondConfig;
        this.joinCondConfig = joinCondConfig;
    }
    public List<String> readSourceData(List<String> parentData) throws Exception {
        parentRecords = parentData;
        DataReader sourceReader = DataReaderUtils.createReader(this.type,this.name,this.sourceProperties,this.parentRecords, filterCondConfig, joinCondConfig);
        this.parentRecords = sourceReader.getData();
        this.dependentSources.stream().forEach(dependentSource -> {
            try {
                this.parentRecords = dependentSource.readSourceData(this.parentRecords);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return this.parentRecords;
    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{ element type -> source");
        stringBuilder.append(" , name -> "+this.name);
        stringBuilder.append(" , type -> "+this.type);
        stringBuilder.append(" , source properties -> "+this.sourceProperties);
        if (dependentSources.size() > 0) {
        stringBuilder.append(" , dependent sources -> [\n");
            for (SourceConfig processElement : dependentSources) {
                stringBuilder.append(processElement.toString());
                stringBuilder.append("\n");
            }
        stringBuilder.append("]");
        }
        if (filterCondConfig != null){
            stringBuilder.append(" , filter condition -> "+this.filterCondConfig);
        }
        if (joinCondConfig != null){
            stringBuilder.append(" , join condition -> "+this.joinCondConfig);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static class SourceConfigBuilder {
        private String name;
        private String type;
        private List<SourceConfig> dependentSources = new ArrayList<SourceConfig>();
        private Properties sourceProperties = new Properties();
        private CondConfig filterCondConfig;
        private CondConfig joinCondConfig;
        public SourceConfigBuilder createBuilder(){
            return new SourceConfigBuilder();
        }
        public SourceConfigBuilder setName(String name){
            this.name = name;
            return this;
        }
        public SourceConfigBuilder setType(String type){
            this.type = type;
            return this;
        }
        public SourceConfigBuilder addDependetSource(SourceConfig processElement){
            this.dependentSources.add(processElement);
            return this;
        }
        public SourceConfigBuilder addProperty(String key, String value){
            this.sourceProperties.setProperty(key,value);
            return this;
        }
        public SourceConfigBuilder setFilterCondition(CondConfig filterCondConfig){
            this.filterCondConfig = filterCondConfig;
            return this;
        }
        public SourceConfigBuilder setJoinCondition(CondConfig joinCondConfig){
            this.joinCondConfig = joinCondConfig;
            return this;
        }
        public SourceConfig build(){
            return new SourceConfig(name,type,dependentSources,sourceProperties, filterCondConfig, joinCondConfig);
        }
    }

}
