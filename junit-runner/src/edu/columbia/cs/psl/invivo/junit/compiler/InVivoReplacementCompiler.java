package edu.columbia.cs.psl.invivo.junit.compiler;


import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;

import org.apache.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import edu.columbia.cs.psl.invivo.junit.annotation.InvivoTest;
import edu.columbia.cs.psl.invivo.junit.annotation.TestCase;
import edu.columbia.cs.psl.invivo.junit.annotation.Tested;
import edu.columbia.cs.psl.invivo.junit.annotation.VariableReplacement;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitInvivoMethodDescription;
import edu.columbia.cs.psl.invivo.junit.rewriter.JUnitTestCaseClassInspector;
import edu.columbia.cs.psl.invivo.runtime.AbstractInterceptor;

public class InVivoReplacementCompiler {
	private static InVivoReplacementCompiler instance;
	public static InVivoReplacementCompiler getInstance(DiagnosticCollector<JavaFileObject> diagnostics, ProcessingEnvironment processingEnv) {
		if(instance == null)
			instance = new InVivoReplacementCompiler(diagnostics,processingEnv);
		return instance;
	}
	private ProcessingEnvironment processingEnv;
	
	private InVivoReplacementCompiler(DiagnosticCollector<JavaFileObject> diagnostics,ProcessingEnvironment processingEnv)
	{
		this.processingEnv = processingEnv;
	}
	private String toString(Set<Modifier> modifiers) {
		StringBuilder buf = new StringBuilder();
		for (Modifier mod : modifiers) {
			buf.append(mod.name() + " ");
		}
		return buf.substring(0, (buf.length() > 1 ? buf.length() - 1 : 0)).toString();
	}
	private void raiseError(String msg, ExecutableElement method, int ruleIndex, String type) {
		try {
			for (AnnotationMirror am : processingEnv.getElementUtils().getAllAnnotationMirrors(((ExecutableElement) method))) {
				if (!processingEnv.getElementUtils().getTypeElement(Tested.class.getName()).equals(am.getAnnotationType().asElement()))
					continue;
				this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Metamorphic error: " + msg, method, am);
				return;
			}
		} catch (Exception ex) {
			raiseError(ex.getMessage(), method);
		}
	}

	private void raiseError(String msg, Element source) {
		this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Metamorphic error: " + msg, source);
	}
//	private java.net.URLClassLoader fInternalClassLoader = java.net.URLClassLoader.newInstance(
//			new java.net.URL[0], InVivoReplacementCompiler.class.getClassLoader());

	public void compileTestCode(MetamorphicClassFile c) {
		c.markDone();
		//Parse out the method info
		HashMap<String, HashMap<String, ArrayList<String>>> methodInfoCache = new HashMap<String, HashMap<String,ArrayList<String>>>();
		
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(c.getFile().openWriter());
		} catch (IOException e1) {
			raiseError("Unable to write file", c.getTypeElement());
		}
		try {

			String thisClassName = c.getTypeElement().getSimpleName().toString()+"_InvivoJUnitTests";
			// PrintStream bw = System.out;
			StringBuilder buf = new StringBuilder();
			buf.append("package ");
			buf.append(((PackageElement) c.getTypeElement().getEnclosingElement()).getQualifiedName());
			buf.append(";\n");
			buf.append("public class " +thisClassName + " {\n");
			buf.append("private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("+thisClassName+".class);\n");
			for (ExecutableElement m : c.getMethods()) {
				int i = 0;
				for (TestCase testCase : m.getAnnotation(Tested.class).cases()) {
					
					/*
					 	private static Method testmutiplymethod = AbstractInterceptor.getMethod("testMultiply", SimpleExampleTest.class);
@SuppressWarnings("all")
public static  void testmultiply() {
	
	try {
		testmutiplymethod.invoke(new SimpleExampleTest(), 3,4);
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
					 */
					TypeMirror testClassMirror = null;
					try
					{
						Class clzzz = testCase.clazz();
					}
					catch(MirroredTypeException mte)
					{
						testClassMirror = mte.getTypeMirror();
					}
					String testClassName = ( (TypeElement) ((DeclaredType) testClassMirror).asElement()).getQualifiedName().toString();
					
					buf.append("private static java.lang.reflect.Method ");
					buf.append(testCase.method());
					buf.append("method = edu.columbia.cs.psl.invivo.runtime.AbstractInterceptor.getMethod(\"");
					buf.append(m.getSimpleName());
					buf.append("\", ");
					buf.append(testClassName);
					buf.append(".class);\n");
					buf.append("@SuppressWarnings(\"all\")\npublic static ");
					buf.append(" void ");
					buf.append("test" + m.getSimpleName() + "() {");

					
					
//					Method testMethod = AbstractInterceptor.getMethod(testCase.method(), testClass);
//					if(java.lang.reflect.Modifier.isStatic(testMethod.getModifiers()))
//					{
//						//TODO
//					}
//					else
					{
						buf.append("try{\n");
						buf.append(testCase.method());
						buf.append("method.invoke(new ");
						buf.append(testClassName);
						buf.append("()");
						String[] params = null;
						for(Element e : ((DeclaredType) testClassMirror).asElement().getEnclosedElements())
						{
							if(e.getKind().equals(ElementKind.METHOD))
							{
								ExecutableElement em = ((ExecutableElement) e);
								if(em.getSimpleName().toString().equals(testCase.method()))
									params = em.getAnnotation(InvivoTest.class).replacements();
							}
						}
						if(params == null)
							raiseError("Unable to find test method, " + testCase.method() + " on class " + ((DeclaredType) testClassMirror).asElement().getSimpleName().toString(), m);
						//TODO make sure that all params are covered
						if(params.length > 0)
							buf.append(", ");
						for(int j = 0; j < params.length; j++)
						{
							String setTo = null;
							for(VariableReplacement r : testCase.replacements())
							{
								if(r.from().equals(params[j]))
									setTo = r.to();
							}
							buf.append(setTo);
							if(j < params.length - 1)
								buf.append(", ");
						}
						buf.append(");\n");
						buf.append("} catch(IllegalArgumentException e){ "+thisClassName+".logger.error(\"Unable to invoke test method "+testCase.method()+"\", e); }\n");
						buf.append("catch(IllegalAccessException e){ "+thisClassName+".logger.error(\"Unable to invoke test method "+testCase.method()+"\", e); }\n");
						buf.append("catch(java.lang.reflect.InvocationTargetException e){ "+thisClassName+".logger.error(\"Unable to invoke test method "+testCase.method()+"\", e); }\n");
					}
					buf.append("}\n");
//					StringBuilder realParams = new StringBuilder();
//					
//					for (VariableElement param : m.getParameters()) {
//						String type = param.asType().toString();
//						if (param.asType().getKind().isPrimitive()) {
//							type = Constants.primitiveToObject.get(type);
//						}
//						realParams.append(toString(param.getModifiers()));
//						realParams.append(" ");
//						realParams.append(type);
//						realParams.append(" ");
//						realParams.append(param.getSimpleName());
//						realParams.append(", ");
//					}
//					
//					buf.append(realParams);
//					buf.append(c.getTypeElement().getSimpleName() + " " + Constants.TEST_OBJECT_PARAM_NAME + ", java.lang.reflect.Method "
//							+ Constants.TEST_METHOD_PARAM_NAME);
//					buf.append(", ");
//					buf.append(m.getReturnType());
//					buf.append(" result");
//					buf.append(") throws Exception {\n");
//
//					String formattedRule = formatRule(m.getSimpleName().toString(), rule);
//
//					if (formattedRule == null || formattedRule.length() < 1)
//						raiseError("Unparsable rule", m, i, "test");
//					buf.append("return " + getCastString(m.getReturnType()) + " " + formatRule(m.getSimpleName().toString(), rule) + ";\n");
//					buf.append("\n}\n");
//					buf.append("@SuppressWarnings(\"all\")\npublic static ");
//					buf.append(" boolean ");
//					buf.append(m.getSimpleName() + "_Check" + i + " (" + m.getReturnType() + " orig, " + m.getReturnType() + " metamorphic"+ (realParams.length() > 0 ? ","+realParams.substring(0,realParams.length()-2) : "")+")");
//					buf.append(" {\n");
//					if (!m.getReturnType().getKind().isPrimitive())
//						buf.append("if(orig == null && metamorphic != null) return false; if(orig == null && metamorphic == null) return true;");
//
//					formattedRule = formatRuleCheck(m, rule, m.getReturnType(), i);
//
//					if (formattedRule == null || formattedRule.length() < 1)
//						raiseError("Unparsable rule", m, i, "check");
//
//					buf.append("return " + formattedRule + "\n");
//					buf.append("\n}");
//
//					i++;
				}
			}
			buf.append("\n}");
				bw.append(buf);
		} catch (Exception ex) {
			raiseError("Unknown error " + ex.getClass() + ": " + ex.getMessage(), c.getTypeElement());
		}
		try {
			bw.close();
		} catch (IOException ex) {
			raiseError("Unable to write file", c.getTypeElement());
		}

	}


	private String getCastString(TypeMirror type) {
		if (!type.getKind().isPrimitive())
			return "(" + type + ")";
		else {
			return "("+Constants.primitiveToObject.get(type.getKind().name().toLowerCase())+")";
		}
//		return "(Object)";
	}


	private String getBoxedType(TypeKind kind) {
		if(kind == TypeKind.BOOLEAN)
			return "Boolean";
		else if(kind == TypeKind.BYTE)
			return "Byte";
		else if(kind == TypeKind.DOUBLE)
			return "Double";
		else if(kind == TypeKind.FLOAT)
			return "Float";
		else if(kind == TypeKind.INT)
			return "Integer";
		else if(kind == TypeKind.LONG)
			return "Long";
		else if(kind == TypeKind.SHORT)
			return "Short";
		return null;
	}


}
