package com.infosys.test.automation;

import com.infosys.test.automation.constants.TestConfigConstants;
import com.infosys.test.automation.dto.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Locale;
import java.util.Stack;

public class TestCaseLoader {
    String testConfig ;
    Object currentBuilder = null;
    Class<?> currentBuilderType = null;
    Stack<Object> builderTracker = new Stack<>();
    TestElement testMetadata = null;

    public TestCaseLoader (String testConfig){
        this.testConfig = testConfig;
    }

    public TestElement load() throws Exception{
        FileInputStream fileInputStream = new FileInputStream(testConfig);
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
        System.out.println("Element Name : "+name);
        switch (name.toUpperCase(Locale.ROOT)){
            case TestConfigConstants.TEST:{
                currentBuilderType = TestElement.TestElementBuilder.class;
                currentBuilder = new TestElement.TestElementBuilder();
                ((TestElement.TestElementBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.SOURCE:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new SourceElement.SourceElementBuilder();
                ((SourceElement.SourceElementBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.TARGET:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new TargetElement.TargetElementBuilder();
                ((TargetElement.TargetElementBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.TESTCASE:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new TestCaseElement.TestCaseElementBuilder();
                ((TestCaseElement.TestCaseElementBuilder) currentBuilder).setType(xmlStreamReader.getAttributeValue(0));
                break;
            }
            case TestConfigConstants.FILTERCONDITION:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new FilterCondElement.FilterCondElementBuilder();
                break;
            }
            case TestConfigConstants.JOINCONDITION:{
                if (currentBuilder !=null){
                    builderTracker.push(currentBuilder);
                }
                currentBuilder = new JoinCondElement.JoinCondElementBuilder();
                break;
            }
            case TestConfigConstants.COLUMN:{
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                    String column = xmlStreamReader.getText().trim();
                    if (currentBuilder instanceof FilterCondElement.FilterCondElementBuilder){
                        ((FilterCondElement.FilterCondElementBuilder) currentBuilder).setColumn(column);
                    }
                    if (currentBuilder instanceof JoinCondElement.JoinCondElementBuilder){
                        ((JoinCondElement.JoinCondElementBuilder) currentBuilder).setColumn(column);
                    }
                }
                break;
            }
            case TestConfigConstants.VALUE:{
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                    String value = xmlStreamReader.getText().trim();
                    if (currentBuilder instanceof FilterCondElement.FilterCondElementBuilder){
                        ((FilterCondElement.FilterCondElementBuilder) currentBuilder).setValue(value);
                    }
                    if (currentBuilder instanceof JoinCondElement.JoinCondElementBuilder){
                        ((JoinCondElement.JoinCondElementBuilder) currentBuilder).setValue(value);
                    }
                }
                break;
            }
            case TestConfigConstants.OPERATOR:{
                int event = xmlStreamReader.next();
                if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                    String operator = xmlStreamReader.getText().trim();
                    if (currentBuilder instanceof FilterCondElement.FilterCondElementBuilder){
                        ((FilterCondElement.FilterCondElementBuilder) currentBuilder).setOperator(operator);
                    }
                    if (currentBuilder instanceof JoinCondElement.JoinCondElementBuilder){
                        ((JoinCondElement.JoinCondElementBuilder) currentBuilder).setOperator(operator);
                    }
                }
                break;
            }
            case TestConfigConstants.NAME:{
                xmlStreamReader.next();
                if (currentBuilder instanceof TestElement.TestElementBuilder){
                    ((TestElement.TestElementBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }
                if (currentBuilder instanceof SourceElement.SourceElementBuilder){
                    ((SourceElement.SourceElementBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }
                if (currentBuilder instanceof TargetElement.TargetElementBuilder){
                    ((TargetElement.TargetElementBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
                }
                if (currentBuilder instanceof TestCaseElement.TestCaseElementBuilder){
                    ((TestCaseElement.TestCaseElementBuilder) currentBuilder).setName(xmlStreamReader.getText().trim());
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
        System.out.println("Element Name : "+name);
        switch (name.toUpperCase(Locale.ROOT)){
            case TestConfigConstants.TEST:{
               testMetadata = ((TestElement.TestElementBuilder)currentBuilder).build();
                break;
            }
            case TestConfigConstants.SOURCE:{
                SourceElement source = ((SourceElement.SourceElementBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                if (currentBuilder instanceof TestElement.TestElementBuilder){
                    ((TestElement.TestElementBuilder) currentBuilder).addSource(source);
                }
                if (currentBuilder instanceof SourceElement.SourceElementBuilder){
                    ((SourceElement.SourceElementBuilder) currentBuilder).addDependetSource(source);
                }
                break;
            }
            case TestConfigConstants.TARGET:{
                TargetElement target = ((TargetElement.TargetElementBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                ((TestElement.TestElementBuilder)currentBuilder).addTarget(target);
                break;
            }
            case TestConfigConstants.TESTCASE:{
                TestCaseElement testCase = ((TestCaseElement.TestCaseElementBuilder) currentBuilder).build();
                currentBuilder = builderTracker.pop();
                ((TestElement.TestElementBuilder)currentBuilder).addTestCase(testCase);
                break;
            }
            case TestConfigConstants.FILTERCONDITION:{
                FilterCondElement filterCondElem = ((FilterCondElement.FilterCondElementBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                if (currentBuilder instanceof FilterCondElement.FilterCondElementBuilder){
                    ((FilterCondElement.FilterCondElementBuilder) currentBuilder).setFilterCondition(filterCondElem);
                }
                if (currentBuilder instanceof SourceElement.SourceElementBuilder){
                    ((SourceElement.SourceElementBuilder) currentBuilder).setFilterCondition(filterCondElem);
                }
                break;
            }
            case TestConfigConstants.JOINCONDITION:{
                JoinCondElement joinCondElement = ((JoinCondElement.JoinCondElementBuilder)currentBuilder).build();
                currentBuilder = builderTracker.pop();
                if (currentBuilder instanceof JoinCondElement.JoinCondElementBuilder){
                    ((JoinCondElement.JoinCondElementBuilder) currentBuilder).setJoinCondition(joinCondElement);
                }
                if (currentBuilder instanceof SourceElement.SourceElementBuilder){
                    ((SourceElement.SourceElementBuilder) currentBuilder).setJoinCondition(joinCondElement);
                }
                break;
            }
        }
    }

    private void processProperties(XMLStreamReader xmlStreamReader,String elementName) throws Exception{
        if (!(elementName.equalsIgnoreCase("Sources") &&
                elementName.equalsIgnoreCase("FilterConditions") &&
                elementName.equalsIgnoreCase("JoinConditions") &&
                elementName.equalsIgnoreCase("TestCases") &&
                elementName.equalsIgnoreCase("FilterCondition") &&
                elementName.equalsIgnoreCase("JoinCondition") &&
                elementName.equalsIgnoreCase("column") &&
        elementName.equalsIgnoreCase("value"))
        ){
            int event = xmlStreamReader.next();
            if (event == XMLStreamConstants.CHARACTERS && !xmlStreamReader.isWhiteSpace()){
                String value = xmlStreamReader.getText().trim();
                if (currentBuilder instanceof TestElement.TestElementBuilder){
                    ((TestElement.TestElementBuilder) currentBuilder).addProperty(elementName,value);
                }
                if (currentBuilder instanceof SourceElement.SourceElementBuilder){
                    ((SourceElement.SourceElementBuilder) currentBuilder).addProperty(elementName,value);
                }
                if (currentBuilder instanceof TargetElement.TargetElementBuilder){
                    ((TargetElement.TargetElementBuilder) currentBuilder).addProperty(elementName,value);
                }
                if (currentBuilder instanceof TestCaseElement.TestCaseElementBuilder){
                    ((TestCaseElement.TestCaseElementBuilder) currentBuilder).addProperty(elementName,value);
                }
            }

        }

    }
}
