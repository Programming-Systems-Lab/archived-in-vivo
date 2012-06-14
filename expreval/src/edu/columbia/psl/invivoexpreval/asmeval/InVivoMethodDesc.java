package edu.columbia.psl.invivoexpreval.asmeval;

import java.util.List;

import org.objectweb.asm.tree.LocalVariableNode;

public class InVivoMethodDesc {

    private String methodName;

    private String methodDesc;

    private String methodReturnType;

    private String methodTestClass;
    
    private String methodTestMethod;
    
    private int methodAcc;

    private List<String> paramTypes;

    private List<LocalVariableNode> locals;

    public String getMethodName() {
        return this.methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDesc() {
        return this.methodDesc;
    }

    public void setMethodDesc(String methodType) {
        this.methodDesc = methodType;
        this.setMethodReturnType(this.methodDesc.substring(this.methodDesc.lastIndexOf(")") + 1));
    }

    public int getMethodAcc() {
        return this.methodAcc;
    }

    public void setMethodAcc(int methodAcc) {
        this.methodAcc = methodAcc;
    }

    public List<String> getParamTypes() {
        return this.paramTypes;
    }

    public void setParamTypes(List<String> paramTypes) {
        this.paramTypes = paramTypes;
    }

    public List<LocalVariableNode> getLocals() {
        return this.locals;
    }

    public void setLocals(List<LocalVariableNode> locals) {
        this.locals = locals;
    }

    public String getMethodReturnType() {
        return this.methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
    }

    public String getMethodTestClass() {
        return this.methodTestClass;
    }

    public void setMethodTestClass(String methodTestClass) {
        this.methodTestClass = methodTestClass;
    }

    public String getMethodTestMethod() {
        return methodTestMethod;
    }

    public void setMethodTestMethod(String methodTestMethod) {
        this.methodTestMethod = methodTestMethod;
    }

}
