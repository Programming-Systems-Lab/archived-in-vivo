package edu.columbia.psl.invivoexpreval.asmeval;

import org.objectweb.asm.Type;

public class InVivoVariableReplacement
{
        private String from;
        
        private String to;
        
        private int indx;
        
        private Type type;
        
        private int argIndx;
        
        public InVivoVariableReplacement() {}

        public String getFrom() {
            return this.from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return this.to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public int getIndx() {
            return this.indx;
        }

        public void setIndx(int indx) {
            this.indx = indx;
        }

        public Type getType() {
            return this.type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getArgIndx() {
            return this.argIndx;
        }

        public void setArgIndx(int argIndx) {
            this.argIndx = argIndx;
        }
}