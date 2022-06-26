package com.infosys.test.automation.dto;

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
