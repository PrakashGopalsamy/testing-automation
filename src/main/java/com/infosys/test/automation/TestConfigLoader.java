package com.infosys.test.automation;

import com.infosys.test.automation.constants.TestConfigConstants;
import com.infosys.test.automation.dto.*;
import com.infosys.test.automation.exceptions.IllegalNodeException;
import com.infosys.test.automation.exceptions.InvalidTargetConfigException;
import com.infosys.test.automation.exceptions.InvalidTestCaseConfigException;
import com.infosys.test.automation.exceptions.InvlaidSourceConfigException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
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

    public TestConfig load() throws IllegalNodeException, FileNotFoundException, XMLStreamException, InvlaidSourceConfigException, InvalidTargetConfigException, InvalidTestCaseConfigException {
        FileInputStream fileInputStream = new FileInputStream(testConfigFile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty("javax.xml.stream.isCoalescing", true);
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStreamReader);
        String previousElement = null;
        while(xmlStreamReader.hasNext()){
            int eventType = xmlStreamReader.next();
            switch (eventType){
                case XMLStreamConstants.START_ELEMENT:{
                    previousElement = processStartElement(xmlStreamReader,previousElement);
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

    private String processStartElement(XMLStreamReader xmlStreamReader,String previousElement) throws IllegalNodeException, XMLStreamException {
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
                if (currentBuilder == null || !(currentBuilder instanceof TestConfig.TestConfigBuilder || currentBuilder instanceof SourceConfig.SourceConfigBuilder)){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container elements : "+TestConfigConstants.TEST+"|"+TestConfigConstants.SOURCE);
                }
                builderTracker.push(currentBuilder);
                currentBuilder = new SourceConfig.SourceConfigBuilder();
                ((SourceConfig.SourceConfigBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.TARGET:{
                if (currentBuilder == null || !(currentBuilder instanceof TestConfig.TestConfigBuilder)){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container element : "+TestConfigConstants.TEST);
                }
                builderTracker.push(currentBuilder);
                currentBuilder = new TargetConfig.TargetConfigBuilder();
                ((TargetConfig.TargetConfigBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.TESTCASE:{
                if (currentBuilder == null || !(currentBuilder instanceof TestConfig.TestConfigBuilder)){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container element : "+TestConfigConstants.TEST);
                }
                builderTracker.push(currentBuilder);
                currentBuilder = new TestCaseConfig.TestCaseConfigBuilder();
                ((TestCaseConfig.TestCaseConfigBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.MULTIJOINCONDITION:
            case TestConfigConstants.MULTIFILTERCONDITION :{
                if (name.equalsIgnoreCase(TestConfigConstants.MULTIJOINCONDITION) && (currentBuilder == null || !(currentBuilder instanceof MultiCondConfig.MultiCondConfigBuilder || currentBuilder instanceof SourceConfig.SourceConfigBuilder || currentBuilder instanceof TargetConfig.TargetConfigBuilder))){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container elements : "+TestConfigConstants.MULTIJOINCONDITION+" | "+TestConfigConstants.SOURCE+" | "+TestConfigConstants.TARGET);
                }
                if (name.equalsIgnoreCase(TestConfigConstants.MULTIFILTERCONDITION) && (currentBuilder == null || !(currentBuilder instanceof MultiCondConfig.MultiCondConfigBuilder || currentBuilder instanceof SourceConfig.SourceConfigBuilder))){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container elements : "+TestConfigConstants.MULTIFILTERCONDITION+" | "+TestConfigConstants.SOURCE);
                }
                builderTracker.push(currentBuilder);
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
                if (name.equalsIgnoreCase(TestConfigConstants.SINGLEJOINCONDITION) && (currentBuilder == null || !(currentBuilder instanceof MultiCondConfig.MultiCondConfigBuilder || currentBuilder instanceof SourceConfig.SourceConfigBuilder || currentBuilder instanceof TargetConfig.TargetConfigBuilder))){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container elements : "+TestConfigConstants.MULTIJOINCONDITION+" | "+TestConfigConstants.SOURCE+" | "+TestConfigConstants.TARGET);
                }
                if (name.equalsIgnoreCase(TestConfigConstants.SINGLEFILTERCONDITION) && (currentBuilder == null || !(currentBuilder instanceof MultiCondConfig.MultiCondConfigBuilder || currentBuilder instanceof SourceConfig.SourceConfigBuilder))){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container elements : "+TestConfigConstants.MULTIFILTERCONDITION+" | "+TestConfigConstants.SOURCE);
                }
                builderTracker.push(currentBuilder);
                currentBuilder = new SingleCondConfig.SingleCondConfigBuilder();
                ((SingleCondConfig.SingleCondConfigBuilder) currentBuilder).setOperator(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.COLUMN:{
                if (currentBuilder == null || !(currentBuilder instanceof SingleCondConfig.SingleCondConfigBuilder)){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container elements : "+TestConfigConstants.SINGLEFILTERCONDITION+" | "+TestConfigConstants.SINGLEJOINCONDITION);
                }
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                    String column = xmlStreamReader.getText().trim();
                    ((SingleCondConfig.SingleCondConfigBuilder) currentBuilder).setColumn(column);
                }
                break;
            }
            case TestConfigConstants.VALUE:{
                if (currentBuilder == null || !(currentBuilder instanceof SingleCondConfig.SingleCondConfigBuilder)){
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container elements : "+TestConfigConstants.SINGLEFILTERCONDITION+" | "+TestConfigConstants.SINGLEJOINCONDITION);
                }
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
                }else if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }else if (currentBuilder instanceof TargetConfig.TargetConfigBuilder){
                    ((TargetConfig.TargetConfigBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }else if (currentBuilder instanceof TestCaseConfig.TestCaseConfigBuilder){
                    ((TestCaseConfig.TestCaseConfigBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }else{
                    throw new IllegalNodeException("Element \""+name+"\" is present inside the element \""+previousElement+"\" instead of the container elements : "+TestConfigConstants.TEST+" | "+TestConfigConstants.SOURCE+" | "+TestConfigConstants.TARGET+" | "+TestConfigConstants.TESTCASE);
                }
                break;
            }
            default:{
                processProperties(xmlStreamReader,name,previousElement);
            }
        }
        return name;
    }

    private void processEndElement(XMLStreamReader xmlStreamReader) throws InvlaidSourceConfigException, InvalidTargetConfigException, InvalidTestCaseConfigException {
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

    private void processProperties(XMLStreamReader xmlStreamReader,String elementName,String parentElement) throws IllegalNodeException, XMLStreamException {
        if (!(elementName.equalsIgnoreCase(TestConfigConstants.SOURCES) &&
                elementName.equalsIgnoreCase(TestConfigConstants.TESTCASE))
        ){
            int event = xmlStreamReader.next();
            if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                String value = xmlStreamReader.getText().trim();
                String exceptionMessage = "The element \""+elementName+"\" is present inside the element \""+parentElement+"\" instead of the container elements : "+TestConfigConstants.TEST+" | "+TestConfigConstants.SOURCE+" | "+TestConfigConstants.TARGET+" | "+TestConfigConstants.TESTCASE;
                if (currentBuilder == null){
                    throw new IllegalNodeException(exceptionMessage);
                }
                if (currentBuilder instanceof TestConfig.TestConfigBuilder){
                    ((TestConfig.TestConfigBuilder) currentBuilder).addProperty(elementName.toLowerCase(Locale.ROOT),value);
                } else if (currentBuilder instanceof SourceConfig.SourceConfigBuilder){
                    ((SourceConfig.SourceConfigBuilder) currentBuilder).addProperty(elementName.toLowerCase(Locale.ROOT),value);
                } else if (currentBuilder instanceof TargetConfig.TargetConfigBuilder){
                    ((TargetConfig.TargetConfigBuilder) currentBuilder).addProperty(elementName.toLowerCase(Locale.ROOT),value);
                }else if (currentBuilder instanceof TestCaseConfig.TestCaseConfigBuilder){
                    ((TestCaseConfig.TestCaseConfigBuilder) currentBuilder).addProperty(elementName.toLowerCase(Locale.ROOT),value);
                } else{
                    throw new IllegalNodeException(exceptionMessage);
                }
            }

        }

    }
}
