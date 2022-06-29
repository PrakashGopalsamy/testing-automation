package com.infosys.test.automation;

import com.infosys.test.automation.dto.TestElement;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Tester {
    public static void main(String args[]) throws Exception{
//        TestCaseLoader testCaseLoader = new TestCaseLoader("J:\\testing_automation\\testautomation.xml");
//        TestElement testMetadata = testCaseLoader.load();
//        System.out.println("Test Metadata : "+testMetadata.toString());
        System.out.println("Starting test");
        List<String> parent = new ArrayList<>();
        parent.add("one");
        parent.add("two");
        parent.add("three");
        parent.add("four");
        parent.add("five");
        List<String> child = new ArrayList<>();
        child.add("three");
        child.add("five");
        List<String> filteredChildRecords = child.stream().filter(childRecord ->{
            System.out.println("Child Record : "+childRecord);
            return parent.stream().anyMatch(parentRecord -> parentRecord.equals(childRecord));
        }

        ).collect(Collectors.toList());
        for (String flt: filteredChildRecords){
            System.out.println("Filtered record : "+flt);
        }
    }
}
