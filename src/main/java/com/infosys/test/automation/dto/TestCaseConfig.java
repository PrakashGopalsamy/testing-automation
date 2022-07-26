package com.infosys.test.automation.dto;

import com.infosys.test.automation.constants.TestResultConstants;
import com.infosys.test.automation.exceptions.InvalidTestCaseConfigException;
import com.infosys.test.automation.exceptions.InvlaidSourceConfigException;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Properties;

public class TestCaseConfig {
    private String name;
    private String type;
    private Properties testCaseProperties;
    private TestCaseConfig(){

    }
    private TestCaseConfig(String name, String type, Properties testCaseProperties) throws InvalidTestCaseConfigException {
        this.name = name;
        this.type = type;
        this.testCaseProperties = testCaseProperties;
        validateProperties();
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

    private void validateProperties() throws InvalidTestCaseConfigException {
        boolean validProps = true;
        StringBuilder exceptionMessageBuilder = new StringBuilder();
        if (name == null || name.trim().length() == 0){
            exceptionMessageBuilder.append("The required element \"name\" is not been provided in testcase config\n");
            validProps=false;
        }
        if (type == null || type.trim().length() == 0){
            exceptionMessageBuilder.append("The required attribute \"type\" is not been provided in the testcase config \""+name+"\"\n");
            validProps=false;
        }
        String sourceColumns = testCaseProperties.getProperty(TestResultConstants.SOURCECOLUMNS);
        if (sourceColumns == null || sourceColumns.trim().length() == 0){
            exceptionMessageBuilder.append("The required testcase property \""+TestResultConstants.SOURCECOLUMNS+"\" is not provided in the testcase config \""+name+"\"\n");
            validProps=false;
        }
        String targetColumns = testCaseProperties.getProperty(TestResultConstants.TARGETCOLUMNS);
        if (targetColumns == null || targetColumns.trim().length() == 0){
            exceptionMessageBuilder.append("The required testcase property \""+TestResultConstants.TARGETCOLUMNS+"\" is not provided in the testcase config \""+name+"\"\n");
            validProps=false;
        }

        if (!validProps){
            throw new InvalidTestCaseConfigException(exceptionMessageBuilder.toString());
        }
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
        public TestCaseConfig build() throws InvalidTestCaseConfigException {
            return new TestCaseConfig(name,type,testCaseProperties);
        }
    }
}
