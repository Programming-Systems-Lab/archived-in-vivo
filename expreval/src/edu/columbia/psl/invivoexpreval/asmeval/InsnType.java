package edu.columbia.psl.invivoexpreval.asmeval;

import org.eclipse.jdt.core.dom.Expression;

public class InsnType {
    public static final int CLASS_CREATION = 0;

    public static final int METHOD_INVOCATION = 1;

    public static final int INFIX_EXPRESSION = 2;

    public static final int STRING_LITERAL = 3;

    public static final int BOOLEAN_LITERAL = 4;

    public static final int NUMBER_LITERAL = 5;

    public static final int SIMPLE_NAME = 6;

    public static final int CLASS_FIELD_ACCESS = 7;
    
    public static final int CLASS_METHOD_ACCESS = 8;
    
    public static final int EXPLICIT_CAST = 9;
    
    public static final int ARRAY_ACCESS = 10;

    public static final int ARRAY_CREATE = 11;
    
    public static final int ARRAY_ELEMENT_EPILOGUE = 12;
    
    public static final int ARRAY_ELEMENT_PROLOGUE = 13;
    
    public static final int INFIX_STRINGBUILDER_PROLOGUE = 14;
    
    public static final int INFIX_STRINGBUILDER_EPILOGUE = 15;
    
    public static final int INFIX_EXPRESSION_STRING = 16;
    
    public static final int INFIX_STRINGBUILDER_EXPREPILOGUE = 17;
    
    private int insnType;

    private Expression insn;

    private int index;
    
    public InsnType(int iType, Expression insn) {
        this.setInsnType(iType);
        this.setInsn(insn);
    }
    
    public InsnType(int iType, int idx) {
        this.setInsnType(iType);
        this.setIndex(idx);
    }

    public int getInsnType() {
        return this.insnType;
    }

    public void setInsnType(int insnType) {
        this.insnType = insnType;
    }

    public Expression getInsn() {
        return this.insn;
    }

    public void setInsn(Expression insn) {
        this.insn = insn;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}