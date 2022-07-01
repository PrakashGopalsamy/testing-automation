package com.infosys.test.automation;

import com.infosys.test.automation.connectors.Connector;
import com.infosys.test.automation.dto.CondElement;
import com.infosys.test.automation.dto.SingleCondElement;
import com.infosys.test.automation.utils.ConnectorUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Tester {
    public static void main(String args[]) throws Exception{
//        TestCaseLoader testCaseLoader = new TestCaseLoader("J:\\testing_automation\\testautomation.xml");
//        TestElement testMetadata = testCaseLoader.load();
//        System.out.println("Test Metadata : "+testMetadata.toString());
//        System.out.println("Starting test");
//        List<String> parent = new ArrayList<>();
//        parent.add("one");
//        parent.add("two");
//        parent.add("three");
//        parent.add("four");
//        parent.add("five");
//        List<String> child = new ArrayList<>();
//        child.add("three");
//        child.add("five");
//        List<String> filteredChildRecords = parent.stream().filter(childRecord ->{
//            System.out.println("Parent Record : "+childRecord);
//            return child.stream().anyMatch(parentRecord -> parentRecord.equals(childRecord));
//        }
//
//        ).collect(Collectors.toList());
//        for (String flt: filteredChildRecords){
//            System.out.println("Filtered record : "+flt);
//        }
//        List<String> framedResult = parent.stream().flatMap(parentRecord ->{
//                    System.out.println("Parent Record : "+parentRecord);
//                    List<String> matchChild = child.stream().map(childRecord -> {
//                        if (childRecord.equals(parentRecord)){
//                            return parentRecord+":"+childRecord;
//                        } else{
//                            return parentRecord+":NA";
//                        }
//                    }).collect(Collectors.toList());
//                    return matchChild.stream();
//                }
//        ).collect(Collectors.toList());
//        for (String flt: framedResult){
//            System.out.println("Result record : "+flt);
//        }
//        List<String> parentRecords = new ArrayList<>();
//        parentRecords.add("{\"deptid\":\"1\",\"deptname\":\"IT\"}");
//        parentRecords.add("{\"deptid\":\"2\",\"deptname\":\"Mech\"}");
//        parentRecords.add("{\"deptid\":\"2\",\"deptname\":\"Aero\"}");
//        SingleCondElement.SingleCondElementBuilder singleCondElementBuilder = new SingleCondElement.SingleCondElementBuilder();
//        singleCondElementBuilder.setOperator("neq").setColumn("empdept").setValue("IT,Mech");
//        CondElement joinCond = singleCondElementBuilder.build();
//        SingleCondElement.SingleCondElementBuilder singleCondElementBuilderJn = new SingleCondElement.SingleCondElementBuilder();
//        singleCondElementBuilderJn.setOperator("eq").setColumn("empdept").setValue("${deptname}");
//        CondElement joinCondJn = singleCondElementBuilderJn.build();
//        Properties fileProperties = new Properties();
//        fileProperties.setProperty("filename","emp.txt");
//        fileProperties.setProperty("sourcecolumns","empid,empname,empdept");
//        fileProperties.setProperty("columndelimiter",",");
//        JSONParser jsonParser = new JSONParser();
//        JSONObject jsonObject = (JSONObject) jsonParser.parse(parentRecords.get(0));
//        System.out.println("Chekck : " +jsonObject.toJSONString());
//        Class<?> connectorClass = checkProvider();
//        Connector dataConnector = ConnectorUtils.createConnector(connectorClass,"EmpFile",fileProperties,null,joinCond,joinCondJn);
////        FlatFileConnector flatFileConnector = new FlatFileConnector("EmpFile",fileProperties,null,joinCond,joinCondJn);
//        System.out.println("Going to read data");
//        List<String> datas =dataConnector.getData();
//        for(String data:datas){
//            System.out.println("Data : "+data);
//        }
//        System.out.println("Completed reading data");
        runTestExecutor();
    }

    private static Class<?> checkProvider() throws ClassNotFoundException {
        Class<?> connectorClass = ConnectorUtils.getConnectorClass("flatfile");
        System.out.println("Class Name : "+connectorClass);
        return connectorClass;
    }

    private static void runTestExecutor() throws Exception {
        TestExecutor testExecutor = new TestExecutor("J:\\testing_automation\\flatfiletestconfig.xml");
        testExecutor.executeTest();
    }
}
