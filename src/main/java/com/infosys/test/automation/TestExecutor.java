package com.infosys.test.automation;


import com.infosys.test.automation.dto.TestConfig;
import com.infosys.test.automation.reportgenerator.ReportGenerator;
import com.infosys.test.automation.utils.ReportGeneratorUtils;

import java.util.List;

public class TestExecutor {
    private String testConfigFile;
    private List<String> parentData = null;
    public TestExecutor(String testConfigFile){
        this.testConfigFile = testConfigFile;
    }

    public String executeTest() throws Exception {
        TestConfigLoader testConfigLoader = new TestConfigLoader(this.testConfigFile);
        TestConfig testConfig = testConfigLoader.load();
        System.out.println("Test Config : "+testConfig);
        List<String> testDatas = testConfig.readTestData();
//        for(String testData: testDatas){
//            System.out.println("test data : "+testData);
//        }
        String testResult = testConfig.executeTestCases();
        System.out.println("Test Result : "+testResult);
        generateTestReport(testConfig.getReportFormat(),testConfig.getTestName(),testConfig.getReportFolder(),testResult);
        return testResult;
    }

    public void generateTestReport(String testReporFomat,String testName,String testReportLoc,String testResult) throws Exception {
        ReportGenerator reportGenerator = ReportGeneratorUtils.createReportGenerator(testReporFomat);
        reportGenerator.generateReport(testName,testReportLoc,testResult);
    }
}
