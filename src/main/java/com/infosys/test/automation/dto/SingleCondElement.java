package com.infosys.test.automation.dto;

import com.infosys.test.automation.constants.CondOperatorConstants;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class SingleCondElement implements CondElement {
    private String operator;
    private String column;
    private String value;
    private SingleCondElement(){

    }
    private SingleCondElement(String operator, String column, String value){
        this.operator = operator;
        this.column = column;
        this.value = value;
    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> single condition element\n");
        stringBuilder.append(" , condition operator -> "+operator+"\n");
        stringBuilder.append(" , condition column -> "+column+"\n");
        stringBuilder.append(" , condition value -> "+value+"\n");
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public boolean evaluateCondition(String parentRecord, String childRecord) throws ParseException {
        JSONParser jsonParser = new JSONParser();
//        System.out.println("Parent Record : "+parentRecord);
//        System.out.println("Child Record : "+childRecord);
        JSONObject parentObject = (JSONObject) jsonParser.parse(parentRecord);
//        System.out.println("Parent object : "+parentObject.toJSONString());
        JSONObject childObject = (JSONObject) jsonParser.parse(childRecord);
//        System.out.println("child object : " + childObject.toJSONString());
        String matchValue = null;
        if (value.contains("${")){
            matchValue = value.replaceAll("\\{","");
            matchValue = matchValue.replaceAll("\\}","");
            matchValue = matchValue.replaceAll("\\$","");
            matchValue = (String)parentObject.get(matchValue);
        } else{
            matchValue = value;
        }
        String childValue = (String)childObject.get(column);
        boolean condRes = false;
        switch(operator.toUpperCase(Locale.ROOT)){
            case CondOperatorConstants.eq:
                if (matchValue.contains(",")){
                    List<String> valueList=Arrays.asList(matchValue.split(","));
                    condRes = valueList.contains(childValue);
                } else{
                    condRes = matchValue.equals(childValue);
                }
                break;
            case CondOperatorConstants.neq:
                if (matchValue.contains(",")){
                    List<String> valueList=Arrays.asList(matchValue.split(","));
                    condRes = !valueList.contains(childValue);
                } else{
                    condRes = !matchValue.equals(childValue);
                }
                break;
        }
        return condRes;
    }

    public static class SingleCondElementBuilder{
        private String operator;
        private String column;
        private String value;
        public SingleCondElementBuilder setOperator(String operator){
            this.operator = operator;
            return this;
        }
        public SingleCondElementBuilder setColumn(String column){
            this.column = column;
            return this;
        }
        public SingleCondElementBuilder setValue(String value){
            this.value = value;
            return this;
        }
        public SingleCondElement build(){
            return new SingleCondElement(operator,column,value);
        }
    }
}
