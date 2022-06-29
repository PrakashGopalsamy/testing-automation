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
        List<String> filteredChildRecords = parent.stream().filter(childRecord ->{
            System.out.println("Parent Record : "+childRecord);
            return child.stream().anyMatch(parentRecord -> parentRecord.equals(childRecord));
        }

        ).collect(Collectors.toList());
        for (String flt: filteredChildRecords){
            System.out.println("Filtered record : "+flt);
        }
        List<String> framedResult = parent.stream().flatMap(parentRecord ->{
                    System.out.println("Parent Record : "+parentRecord);
                    List<String> matchChild = child.stream().map(childRecord -> {
                        if (childRecord.equals(parentRecord)){
                            return parentRecord+":"+childRecord;
                        } else{
                            return parentRecord+":NA";
                        }
                    }).collect(Collectors.toList());
                    return matchChild.stream();
                }
        ).collect(Collectors.toList());
        for (String flt: framedResult){
            System.out.println("Result record : "+flt);
        }
    }
}
