package com.infosys.test.automation;


import com.infosys.test.automation.dto.TestConfig;

import java.util.List;

public class TestExecutor {
    private String testConfigFile;
    private List<String> parentData = null;
    public TestExecutor(String testConfigFile){
        this.testConfigFile = testConfigFile;
    }

    public void executeTest() throws Exception {
        TestConfigLoader testConfigLoader = new TestConfigLoader(this.testConfigFile);
        TestConfig testConfig = testConfigLoader.load();
        List<String> testDatas = testConfig.readTestData();
        for(String testData: testDatas){
            System.out.println("test data : "+testData);
        }
    }
}
