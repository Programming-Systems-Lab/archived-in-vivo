package edu.columbia.psl.invivoexpreval.asmeval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Type;

public class InVivoClassDesc {
    
    private String className;
    
    private Map<InVivoMethodDesc, List<InVivoVariableReplacement>> classMethods;
    
    private List<InVivoIdentifierDesc> classFields;
    
    public InVivoClassDesc() {
        classMethods = new HashMap<InVivoMethodDesc, List<InVivoVariableReplacement>>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    
    public void addMethod(InVivoMethodDesc method, List<InVivoVariableReplacement> replacements) {
        this.classMethods.put(method, replacements);
    }
    
    public Map<InVivoMethodDesc, List<InVivoVariableReplacement>> getClassMethods() {
        return this.classMethods;
    }

    public List<InVivoIdentifierDesc> getClassFields() {
        return classFields;
    }

    public void setClassFields(List<InVivoIdentifierDesc> classFields) {
        this.classFields = classFields;
    }
}
