package com.infosys.test.automation;

import com.infosys.test.automation.dto.TestElement;

public class Tester {
    public static void main(String args[]) throws Exception{
        TestCaseLoader testCaseLoader = new TestCaseLoader("J:\\testing_automation\\testautomation.xml");
        TestElement testMetadata = testCaseLoader.load();
        System.out.println("Test Metadata : "+testMetadata.toString());
    }
}
