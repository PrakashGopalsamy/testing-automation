package com.infosys.test.automation.dto;

public class FilterCondElement {
    private String operator;
    private String column;
    private String value;
    private FilterCondElement conditionOne;
    private FilterCondElement conditionTwo;
    private FilterCondElement(){

    }
    private FilterCondElement(String operator,String column, String value, FilterCondElement conditionOne,
                              FilterCondElement conditionTwo){
        this.operator = operator;
        this.column = column;
        this.value = value;
        this.conditionOne = conditionOne;
        this.conditionTwo = conditionTwo;
    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> filter condition element\n");
        stringBuilder.append(" , filter operator -> "+operator+"\n");
        stringBuilder.append(" , filter column -> "+column+"\n");
        stringBuilder.append(" , filter value -> "+value+"\n");
        if (conditionOne != null){
            stringBuilder.append(" , Filter condition one -> "+conditionOne+"\n");
        }
        if (conditionTwo != null){
            stringBuilder.append(" , Filter condition two -> "+conditionTwo+"\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static class FilterCondElementBuilder{
        private String operator;
        private String column;
        private String value;
        private FilterCondElement conditionOne;
        private FilterCondElement conditionTwo;
        public FilterCondElementBuilder setOperator(String operator){
            this.operator = operator;
            return this;
        }
        public FilterCondElementBuilder setColumn(String column){
            this.column = column;
            return this;
        }
        public FilterCondElementBuilder setValue(String value){
            this.value = value;
            return this;
        }
        public FilterCondElementBuilder setFilterCondition(FilterCondElement filterCondElem){
            if (conditionOne == null){
                conditionOne = filterCondElem;
            } else{
                conditionTwo = filterCondElem;
            }
            return this;
        }
        public FilterCondElement build(){
            return new FilterCondElement(operator,column,value,conditionOne,conditionTwo);
        }
    }
}
