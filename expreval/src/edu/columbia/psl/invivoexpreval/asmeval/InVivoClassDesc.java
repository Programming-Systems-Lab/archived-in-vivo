package edu.columbia.psl.invivoexpreval.asmeval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
        return this.classFields;
    }

    public void setClassFields(List<InVivoIdentifierDesc> classFields) {
        this.classFields = classFields;
    }

    public Entry<InVivoMethodDesc, List<InVivoVariableReplacement>> getClassMethod(String name,
        String desc) {
        for (Entry<InVivoMethodDesc, List<InVivoVariableReplacement>> a : this.getClassMethods()
            .entrySet()) {
            if (a.getKey().getMethodName().equals(name))
                return a;
        }
        return null;
    }
}
