package com.infosys.test.automation.dto;

import com.infosys.test.automation.constants.TestResultConstants;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Properties;

public class TestCaseConfig {
    private String name;
    private String type;
    private Properties testCaseProperties;
    private TestCaseConfig(){

    }
    private TestCaseConfig(String name, String type, Properties testCaseProperties){
        this.name = name;
        this.type = type;
        this.testCaseProperties = testCaseProperties;

    }

    public JSONObject executeTestCase(JSONObject testRecord) {
//        System.out.println("Test Record : "+testRecord.toJSONString());
        JSONObject testExecutionResult = new JSONObject();
        testExecutionResult.put(TestResultConstants.TESTCASENAME,name);
        String sourceColumns[] = testCaseProperties.getProperty(TestResultConstants.SOURCECOLUMNS).split(",");
        String targetColumns[] = testCaseProperties.getProperty(TestResultConstants.TARGETCOLUMNS).split(",");
        JSONObject validataionResults[] = new JSONObject[sourceColumns.length];
        for(int pos = 0 ; pos < sourceColumns.length;pos++){
            JSONObject validationResult = new JSONObject();
            validationResult.put(TestResultConstants.SOURCECOLUMN,sourceColumns[pos].split("\\.")[1]);
            validationResult.put(TestResultConstants.TARGETCOLUMN,targetColumns[pos].split("\\.")[1]);
            String sourceValue = (String)testRecord.get(sourceColumns[pos]);
            validationResult.put(TestResultConstants.SOURCEVALUE,sourceValue);
            String targetValue = (String)testRecord.get(targetColumns[pos]);
            validationResult.put(TestResultConstants.TARGETVALUE,targetValue);
//            System.out.println("Source Column : "+sourceColumns[pos].split("\\.")[1]);
//            System.out.println("Source Value : " + sourceValue);
//            System.out.println("Target Column : "+targetColumns[pos].split("\\.")[1]);
//            System.out.println("Target Value : " + targetValue);
            if (sourceValue.equals(targetValue)){
                validationResult.put(TestResultConstants.TESTRESULT,TestResultConstants.TESTRESULTSUCCESS);
            } else{
                validationResult.put(TestResultConstants.TESTRESULT,TestResultConstants.TESTRESULTFAIL);
            }
            validataionResults[pos]=validationResult;
        }
        testExecutionResult.put(TestResultConstants.VALIDATIONRESULTS, Arrays.asList(validataionResults));
        return testExecutionResult;
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

    public static class TestCaseConfigBuilder {
        private String name;
        private String type;
        private Properties testCaseProperties = new Properties();
        public TestCaseConfigBuilder createBuilder(){
            return new TestCaseConfigBuilder();
        }
        public TestCaseConfigBuilder setName(String name){
            this.name = name;
            return this;
        }
        public TestCaseConfigBuilder setType(String type){
            this.type = type;
            return this;
        }
        public TestCaseConfigBuilder addProperty(String key, String value){
            this.testCaseProperties.setProperty(key,value);
            return this;
        }
        public TestCaseConfig build(){
            return new TestCaseConfig(name,type,testCaseProperties);
        }
    }
}
