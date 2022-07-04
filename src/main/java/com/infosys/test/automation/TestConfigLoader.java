package com.infosys.test.automation;

import com.infosys.test.automation.constants.TestConfigConstants;
import com.infosys.test.automation.dto.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Locale;
import java.util.Stack;

public class TestConfigLoader {
    String testConfigFile ;
    Object currentBuilder = null;
    Class<?> currentBuilderType = null;
    Stack<Object> builderTracker = new Stack<>();
    TestConfig testMetadata = null;

    public TestConfigLoader(String testConfigFile){
        this.testConfigFile = testConfigFile;
    }

    public TestConfig load() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(testConfigFile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStreamReader);
        while(xmlStreamReader.hasNext()){
            int eventType = xmlStreamReader.next();
            switch (eventType){
                case XMLStreamConstants.START_ELEMENT:{
                    processStartElement(xmlStreamReader);
                    break;
                }
                case XMLStreamConstants.END_ELEMENT:{
                    processEndElement(xmlStreamReader);
                    break;
                }
            }
        }
        return testMetadata;

    }

    private void processStartElement(XMLStreamReader xmlStreamReader) throws Exception{
        String name = xmlStreamReader.getLocalName();
//        System.out.println("Element Name : "+name);
        switch (name.toUpperCase(Locale.ROOT)){
            case TestConfigConstants.TEST:{
                currentBuilderType = TestConfig.TestConfigBuilder.class;
                currentBuilder = new TestConfig.TestConfigBuilder();
                ((TestConfig.TestConfigBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.SOURCE:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new SourceConfig.SourceConfigBuilder();
                ((SourceConfig.SourceConfigBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.TARGET:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new TargetConfig.TargetConfigBuilder();
                ((TargetConfig.TargetConfigBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.TESTCASE:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new TestCaseConfig.TestCaseConfigBuilder();
                ((TestCaseConfig.TestCaseConfigBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.MULTIJOINCONDITION:
            case TestConfigConstants.MULTIFILTERCONDITION :{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new MultiCondConfig.MultiCondConfigBuilder();
                int attriCount = xmlStreamReader.getAttributeCount();
//                System.out.println("Multi Condition elem");
//                for (int i=0 ; i<attriCount;i++){
//                    System.out.println("Element name : "+xmlStreamReader.getAttributeName(i));
//                    System.out.println("Element value : "+xmlStreamReader.getAttributeValue(i));
//                }
                ((MultiCondConfig.MultiCondConfigBuilder) currentBuilder).setLogicalOp(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.SINGLEJOINCONDITION:
            case TestConfigConstants.SINGLEFILTERCONDITION:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new SingleCondConfig.SingleCondConfigBuilder();
                ((SingleCondConfig.SingleCondConfigBuilder) currentBuilder).setOperator(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.COLUMN:{
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                    String column = xmlStreamReader.getText().trim();
                    ((SingleCondConfig.SingleCondConfigBuilder) currentBuilder).setColumn(column);
                }
                break;
            }
            case TestConfigConstants.VALUE:{
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                    String value = xmlStreamReader.getText().trim();
                    ((SingleCondConfig.SingleCondConfigBuilder) currentBuilder).setValue(value);
                }
                break;
            }
            case TestConfigConstants.NAME:{
                xmlStreamReader.next();
                if (currentBuilder instanceof TestConfig.TestConfigBuilder){
                    ((TestConfig.TestConfigBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }
                if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }
                if (currentBuilder instanceof TargetConfig.TargetConfigBuilder){
                    ((TargetConfig.TargetConfigBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }
                if (currentBuilder instanceof TestCaseConfig.TestCaseConfigBuilder){
                    ((TestCaseConfig.TestCaseConfigBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }
                break;
            }
            default:{
                processProperties(xmlStreamReader,name);
            }
        }
    }

    private void processEndElement(XMLStreamReader xmlStreamReader){
        String name = xmlStreamReader.getLocalName();
//        System.out.println("Element Name : "+name);
        switch (name.toUpperCase(Locale.ROOT)){
            case TestConfigConstants.TEST:{
               testMetadata = ((TestConfig.TestConfigBuilder)currentBuilder).build();
                break;
            }
            case TestConfigConstants.SOURCE:{
                SourceConfig source = ((SourceConfig.SourceConfigBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                if (currentBuilder instanceof TestConfig.TestConfigBuilder){
                    ((TestConfig.TestConfigBuilder) currentBuilder).addSource(source);
                }
                if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).addDependetSource(source);
                }
                break;
            }
            case TestConfigConstants.TARGET:{
                TargetConfig target = ((TargetConfig.TargetConfigBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                ((TestConfig.TestConfigBuilder)currentBuilder).addTarget(target);
                break;
            }
            case TestConfigConstants.TESTCASE:{
                TestCaseConfig testCase = ((TestCaseConfig.TestCaseConfigBuilder) currentBuilder).build();
                currentBuilder = builderTracker.pop();
                ((TestConfig.TestConfigBuilder)currentBuilder).addTestCase(testCase);
                break;
            }
            case TestConfigConstants.SINGLEFILTERCONDITION:{
                SingleCondConfig singleCondElem = ((SingleCondConfig.SingleCondConfigBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                if (currentBuilder instanceof MultiCondConfig.MultiCondConfigBuilder){
                    ((MultiCondConfig.MultiCondConfigBuilder) currentBuilder).setSingleCond(singleCondElem);
                }
                if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).setFilterCondition(singleCondElem);
                }
                break;
            }
            case TestConfigConstants.SINGLEJOINCONDITION:{
                SingleCondConfig singleCondElem = ((SingleCondConfig.SingleCondConfigBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                if (currentBuilder instanceof MultiCondConfig.MultiCondConfigBuilder){
                    ((MultiCondConfig.MultiCondConfigBuilder) currentBuilder).setSingleCond(singleCondElem);
                }
                if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).setJoinCondition(singleCondElem);
                }
                if (currentBuilder instanceof TargetConfig.TargetConfigBuilder){
                    ((TargetConfig.TargetConfigBuilder) currentBuilder).setJoinCondition(singleCondElem);
                }
                break;
            }
            case TestConfigConstants.MULTIFILTERCONDITION:{
                MultiCondConfig multiCondConfig = ((MultiCondConfig.MultiCondConfigBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                if (currentBuilder instanceof MultiCondConfig.MultiCondConfigBuilder){
                    ((MultiCondConfig.MultiCondConfigBuilder) currentBuilder).setMultiCond(multiCondConfig);
                }
                if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).setFilterCondition(multiCondConfig);
                }
                break;
            }
            case TestConfigConstants.MULTIJOINCONDITION:{
                MultiCondConfig multiCondConfig = ((MultiCondConfig.MultiCondConfigBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                if (currentBuilder instanceof MultiCondConfig.MultiCondConfigBuilder){
                    ((MultiCondConfig.MultiCondConfigBuilder) currentBuilder).setMultiCond(multiCondConfig);
                }
                if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).setJoinCondition(multiCondConfig);
                }
                if (currentBuilder instanceof TargetConfig.TargetConfigBuilder){
                    ((TargetConfig.TargetConfigBuilder) currentBuilder).setJoinCondition(multiCondConfig);
                }
                break;
            }
        }
    }

    private void processProperties(XMLStreamReader xmlStreamReader,String elementName) throws Exception{
        if (!(elementName.equalsIgnoreCase("Sources") &&
                elementName.equalsIgnoreCase("MultiFilterCondition") &&
                elementName.equalsIgnoreCase("SingleFilterCondition") &&
                elementName.equalsIgnoreCase("TestCases") &&
                elementName.equalsIgnoreCase("SingleFilterCondition") &&
                elementName.equalsIgnoreCase("SingleJoinCondition") &&
                elementName.equalsIgnoreCase("column") &&
        elementName.equalsIgnoreCase("value"))
        ){
            int event = xmlStreamReader.next();
            if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                String value = xmlStreamReader.getText().trim();
                if (currentBuilder instanceof TestConfig.TestConfigBuilder){
                    ((TestConfig.TestConfigBuilder) currentBuilder).addProperty(elementName.toLowerCase(Locale.ROOT),value);
                }
                if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).addProperty(elementName.toLowerCase(Locale.ROOT),value);
                }
                if (currentBuilder instanceof TargetConfig.TargetConfigBuilder){
                    ((TargetConfig.TargetConfigBuilder) currentBuilder).addProperty(elementName.toLowerCase(Locale.ROOT),value);
                }
                if (currentBuilder instanceof TestCaseConfig.TestCaseConfigBuilder){
                    ((TestCaseConfig.TestCaseConfigBuilder) currentBuilder).addProperty(elementName.toLowerCase(Locale.ROOT),value);
                }
            }

        }

    }
}
