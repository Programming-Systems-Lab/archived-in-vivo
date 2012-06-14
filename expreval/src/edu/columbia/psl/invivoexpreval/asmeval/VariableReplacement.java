package edu.columbia.psl.invivoexpreval.asmeval;

import org.objectweb.asm.Type;

public class VariableReplacement
{
        public String from;
        public String to;
        public int indx;
        public Type type;
        public int argIndx;
        
        public VariableReplacement(String from)
        {
                this.from = from;
        }

        @Override
        public String toString() {
                return "VariableReplacement [from=" + from + ", to=" + to + ", indx=" + indx + ", type=" + type + ", argIndx=" + argIndx + "]";
        }
}