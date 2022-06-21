package com.infosys.test.automation.dto;

public class JoinCondElement {
    private String operator;
    private String column;
    private String value;
    private JoinCondElement conditionOne;
    private JoinCondElement conditionTwo;
    private JoinCondElement(){

    }
    private JoinCondElement(String operator,String column, String value, JoinCondElement conditionOne,
                            JoinCondElement conditionTwo){
        this.operator = operator;
        this.column = column;
        this.value = value;
        this.conditionOne = conditionOne;
        this.conditionTwo = conditionTwo;
    }
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> join condition element\n");
        stringBuilder.append(" , join operator -> "+operator+"\n");
        stringBuilder.append(" , join column -> "+column+"\n");
        stringBuilder.append(" , join value -> "+value+"\n");
        if (conditionOne != null){
            stringBuilder.append(" , join condition one -> "+conditionOne+"\n");
        }
        if (conditionTwo != null){
            stringBuilder.append(" , join condition two -> "+conditionTwo+"\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static class JoinCondElementBuilder{
        private String operator;
        private String column;
        private String value;
        private JoinCondElement conditionOne;
        private JoinCondElement conditionTwo;
        public JoinCondElement.JoinCondElementBuilder setOperator(String operator){
            this.operator = operator;
            return this;
        }
        public JoinCondElement.JoinCondElementBuilder setColumn(String column){
            this.column = column;
            return this;
        }
        public JoinCondElement.JoinCondElementBuilder setValue(String value){
            this.value = value;
            return this;
        }
        public JoinCondElement.JoinCondElementBuilder setJoinCondition(JoinCondElement joinCondElement){
            if (conditionOne == null){
                conditionOne = joinCondElement;
            } else{
                conditionTwo = joinCondElement;
            }
            return this;
        }
        public JoinCondElement build(){
            return new JoinCondElement(operator,column,value,conditionOne,conditionTwo);
        }
    }
}
