package com.infosys.test.automation.dto;

public class MultiCondElement implements CondElement {
    private String logicalOp;
    private SingleCondElement filterElem1;
    private SingleCondElement filterElem2;
    private MultiCondElement filterElem3;
    private MultiCondElement filterElem4;
    private MultiCondElement(){

    }
    private MultiCondElement(String logicalOp, SingleCondElement filterElem1,
                             SingleCondElement filterElem2, MultiCondElement filterElem3,
                             MultiCondElement filterElem4){
        this.logicalOp = logicalOp;
        this.filterElem1 = filterElem1;
        this.filterElem2 = filterElem2;
        this.filterElem3 = filterElem3;
        this.filterElem4 = filterElem4;
    }

    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" { element type -> multi condition element\n");
        stringBuilder.append(" , logical operator -> "+logicalOp+"\n");
        if (filterElem1 != null){
            stringBuilder.append(" , single condition element -> "+filterElem1.toString()+"\n");
        }
        if (filterElem2 != null){
            stringBuilder.append(" , single condition element -> "+filterElem2.toString()+"\n");
        }
        if (filterElem3 != null){
            stringBuilder.append(" , multi condition element -> "+filterElem3.toString()+"\n");
        }
        if (filterElem4 != null){
            stringBuilder.append(" , multi condition element -> "+filterElem4.toString()+"\n");
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public static class MultiCondElementBuilder{
        private String logicalOp;
        private SingleCondElement filterElem1;
        private SingleCondElement filterElem2;
        private MultiCondElement filterElem3;
        private MultiCondElement filterElem4;

        public MultiCondElementBuilder setLogicalOp(String logicalOp){
//            System.out.println("Multi Condition logical op : "+logicalOp);
            this.logicalOp = logicalOp;
            return this;
        }

        public MultiCondElementBuilder setSingleCond(SingleCondElement singleFltrElem){
            if (this.filterElem1 == null){
                this.filterElem1 = singleFltrElem;
            } else if (this.filterElem2 == null){
                this.filterElem2 = singleFltrElem;
            }
            return this;
        }
        public MultiCondElementBuilder setMultiCond(MultiCondElement multiFltrElem){
            if (this.filterElem3 == null){
                this.filterElem3 = multiFltrElem;
            } else if (this.filterElem4 == null){
                this.filterElem4 = multiFltrElem;
            }
            return this;
        }

        public MultiCondElement build(){
            return new MultiCondElement(this.logicalOp,this.filterElem1,this.filterElem2,this.filterElem3,this.filterElem4);
        }
    }

}
