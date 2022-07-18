package com.infosys.test.automation.dto;

import com.infosys.test.automation.constants.TestConfigConstants;
import com.infosys.test.automation.constants.TestResultConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class TestConfig {
    private String name;
    private String type;
    private List<SourceConfig> sources;
    private List<TargetConfig> targets;
    private List<TestCaseConfig> testCases;
    private Properties testProperties;
    private List<String> testRecords = null;
    private TestConfig(){

    }
    private TestConfig(String name, String type, List<SourceConfig> sources, List<TargetConfig> targets,
                       List<TestCaseConfig> testCases, Properties testProperties){
        this.name = name;
        this.type = type;
        this.sources = sources;
        this.targets = targets;
        this.testCases = testCases;
        this.testProperties = testProperties;

    }

    private void readSourceData() throws Exception {
        this.sources.stream().forEach(source -> {
            try {
                this.testRecords = source.readSourceData(this.testRecords);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void readTargetData() throws Exception {
        this.targets.stream().forEach(target -> {
            try {
                this.testRecords = target.readTargetData(this.testRecords);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> readTestData() throws Exception {
        readSourceData();
//        for(String sourcData: testRecords){
//            System.out.println("Source data: "+sourcData);
//        }
        readTargetData();
        return this.testRecords;
    }

    public String executeTestCases(){
        JSONObject testResult = new JSONObject();
        testResult.put(TestResultConstants.TESTNAME,name);
        JSONParser jsonParser = new JSONParser();
        List<JSONObject> testResults = testRecords.stream().map(testRecord -> {
            JSONObject testExec = new JSONObject();
                try {
                    JSONObject testData = (JSONObject) jsonParser.parse(testRecord);
                    testExec.put(TestResultConstants.TESTDATA, testData);
                    List<JSONObject> testCaseExecutionRes = testCases.stream().map(testCase -> testCase.executeTestCase(testData)).collect(Collectors.toList());
                    testExec.put(TestResultConstants.TESTEXECRESULT,testCaseExecutionRes);
                    testExec.put("testdataparsed","1");
                } catch (ParseException e){
                    e.printStackTrace();
                    testExec.put(TestResultConstants.TESTDATA, testRecord);
                    testExec.put("testdataparsed","0");
                }
            return testExec;
        }).collect(Collectors.toList());
        testResult.put(TestResultConstants.TESTRESULTS,testResults);
        return testResult.toJSONString();
    }


    public String getTestName(){

        return this.name;
    }

    public String getReportFolder(){

        return this.testProperties.getProperty(TestConfigConstants.TESTREPORTLOCATION);
    }

    public String getReportFormat(){

        return this.testProperties.getProperty(TestConfigConstants.TESTREPORTFORMAT);
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
        for(SourceConfig processElement: sources ){
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
        for(TargetConfig processElement: targets ){
            stringBuilder.append(processElement.toString()+"\n");
        }
        int testCaseCnt = testCases.size();
        counter = 0;
        stringBuilder.append(" , TestCases -> [");
        for(TestCaseConfig processElement: testCases ){
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

    public static class TestConfigBuilder {
        private String name;
        private String type;
        private List<SourceConfig> sources = new ArrayList<SourceConfig>();
        private List<TargetConfig> targets = new ArrayList<TargetConfig>();
        private List<TestCaseConfig> testCases = new ArrayList<TestCaseConfig>();
        private Properties testProperties = new Properties();
        public TestConfigBuilder createBuilder(){
            return new TestConfigBuilder();
        }
        public TestConfigBuilder setName(String name){
            this.name = name;
            return this;
        }
        public TestConfigBuilder setType(String type){
            this.type = type;
            return this;
        }
        public TestConfigBuilder addSource(SourceConfig processElement){
            this.sources.add(processElement);
            return this;
        }
        public TestConfigBuilder addTarget(TargetConfig processElement){
            this.targets.add(processElement);
            return this;
        }
        public TestConfigBuilder addTestCase(TestCaseConfig processElement){
            this.testCases.add(processElement);
            return this;
        }
        public TestConfigBuilder addProperty(String key, String value){
            this.testProperties.setProperty(key,value);
            return this;
        }
        public TestConfig build(){
            return new TestConfig(name,type,sources,targets,testCases,testProperties);
        }
    }

}
