package edu.columbia.psl.invivoexpreval.asmeval;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LocalVariableNode;

public class InVivoAsmEval extends ClassLoader implements Opcodes {

    public Stack<InsnType> insnStack;

    private ClassWriter classWriter;

    private ClassVisitor classVisitor;

    private MethodVisitor methodVisitor;

    private FieldVisitor fieldVisitor;

    public InVivoAsmEval() {
        this.classWriter = new ClassWriter(0);
    }

    // Tracking variables
    private InVivoClassDesc currentClass;

    private InVivoMethodDesc currentMethod;

    private String lastType;

    private int currentIndex;

    private boolean inField = false;

    /*
     * This variable tracks the need to output an ALOAD for a virtual method
     * call
     */
    private boolean inMethod = false;

    private Map<String, String> currentVars = new HashMap<String, String>();

    public LocalVariableNode isCurrentMethodLocal(String var) {
        for (LocalVariableNode lvn : this.currentMethod.getLocals()) {
            if (lvn.name.equals(var))
                return lvn;
        }
        return null;
    }

    public InVivoIdentifierDesc isCurrentClassField(String var) {
        if (this.currentClass.getClassFields().isEmpty()) {
            List<InVivoIdentifierDesc> fieldNodes =
                InferenceEngine.getClassFields(this.currentClass.getClassName());
            this.currentClass.setClassFields(fieldNodes);
        }

        for (InVivoIdentifierDesc i : this.currentClass.getClassFields()) {
            if (i.getName().equals(var))
                return i;
        }
        return null;
    }

    public String getLocalDesc(String var) {
        InVivoIdentifierDesc field = this.isCurrentClassField(var);
        if (field != null)
            return field.getDesc();
        else {
            LocalVariableNode lvn = this.isCurrentMethodLocal(var);
            return lvn.desc;
        }
    }

    public byte[] getClassByteCode(InVivoClassDesc cls) {
        this.currentClass = cls;

        this.insnStack = new Stack<InsnType>();

        /* Class writer initialization */
        String truncatedClassName =
            cls.getClassName().substring(cls.getClassName().lastIndexOf("/") + 1) + "_InVivo";
        this.classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        this.classWriter
            .visit(V1_1, ACC_PUBLIC, truncatedClassName, null, "java/lang/Object", null);

        /* Implicit constructor */
        setMethodVisitor(getClassWriter().visitMethod(ACC_PUBLIC, "<init>", "()V", null, null));
        getMethodVisitor().visitVarInsn(ALOAD, 0);
        getMethodVisitor().visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        getMethodVisitor().visitInsn(RETURN);
        getMethodVisitor().visitMaxs(1, 1);
        getMethodVisitor().visitEnd();

        ASTParser parser = ASTParser.newParser(AST.JLS3);

        emitInVivoMethods(cls, parser);

        /* InvokeDynamic of test method goes here */

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(truncatedClassName + ".class");
            fos.write(this.classWriter.toByteArray());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InVivoAsmEval loader = new InVivoAsmEval();
        Class<?> exampleClass =
            loader.defineClass(truncatedClassName, this.classWriter.toByteArray(), 0,
                this.classWriter.toByteArray().length);

        try {
            System.out.println(exampleClass.getMethods()[0]);
            exampleClass.getMethods()[0].invoke(exampleClass.newInstance(), 1, 2);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return this.classWriter.toByteArray();
    }

    private void emitInVivoMethods(InVivoClassDesc cls, ASTParser parser) {
        for (Entry<InVivoMethodDesc, List<InVivoVariableReplacement>> a : cls.getClassMethods()
            .entrySet()) {
            this.insnStack.clear();

            InVivoMethodDesc d =
                InferenceEngine.getMethodInfo(
                    this.currentClass.getClassName().substring(
                        this.currentClass.getClassName().lastIndexOf('/') + 1),
                    a.getKey().getMethodName(), "").get(0);

            this.currentMethod = d;

            /**
             * Set up method here
             */
            this.setMethodVisitor(this.getClassWriter().visitMethod(
                this.currentMethod.getMethodAcc(), this.currentMethod.getMethodName(),
                this.currentMethod.getMethodDesc(), null, null));
            this.getMethodVisitor().visitCode();
            /*
             * The structure for the generated test method is going to be < all
             * the params | all the replacements, replacement1, replacement2,
             * replacement3....>
             */
            this.currentIndex = this.currentMethod.getLocals().size();

            for (InVivoVariableReplacement replacement : a.getValue()) {
                // Declare the instance in a variable
                if (!replacement.getType().getClassName().equals("java.lang.Integer")) {
                    this.getMethodVisitor().visitTypeInsn(NEW,
                        replacement.getType().getClassName().replace('.', '/'));
                    this.getMethodVisitor().visitInsn(DUP);
                }

                parser.setSource(replacement.getTo().toCharArray());
                parser.setKind(ASTParser.K_EXPRESSION);
                Expression expr = (Expression) parser.createAST(null);
                expr.accept(this.asmVisitor);

                this.emitByteCode();

                this.exprType = this.getExpressionType(expr);
                if (this.exprType.equals("I")) {
                    this.getMethodVisitor().visitVarInsn(ISTORE, this.currentIndex);
                } else if (this.exprType.equals("D"))
                    this.getMethodVisitor().visitVarInsn(DSTORE, this.currentIndex);
                else if (this.exprType.equals("F"))
                    this.getMethodVisitor().visitVarInsn(FSTORE, this.currentIndex);
                else {
                    this.getMethodVisitor().visitVarInsn(ASTORE, this.currentIndex);
                }
                this.currentIndex++;
            }

            this.getMethodVisitor().visitTypeInsn(NEW, a.getKey().getMethodTestClass());
            this.getMethodVisitor().visitInsn(DUP);
            this.getMethodVisitor().visitMethodInsn(INVOKESPECIAL, a.getKey().getMethodTestClass(),
                "<init>", "()V");
            // this.getMethodVisitor().visitVarInsn(ALOAD, 3);
            // this.getMethodVisitor().visitVarInsn(ILOAD, 4);
            this.getMethodVisitor().visitMethodInsn(INVOKEVIRTUAL, a.getKey().getMethodTestClass(),
                a.getKey().getMethodTestMethod(), a.getKey().getMethodDesc());

            /* Wind up method */
            String currMethReturnType = this.currentMethod.getMethodReturnType();
            if (currMethReturnType.equals("I") || currMethReturnType.equals("Z")) {
                this.getMethodVisitor().visitInsn(ICONST_0);
                this.getMethodVisitor().visitInsn(IRETURN);
            } else if (currMethReturnType.equals("D")) {
                this.getMethodVisitor().visitInsn(DCONST_0);
                this.getMethodVisitor().visitInsn(DRETURN);
            } else if (currMethReturnType.equals("F")) {
                this.getMethodVisitor().visitInsn(FCONST_0);
                this.getMethodVisitor().visitInsn(FRETURN);
            } else {
                this.getMethodVisitor().visitInsn(ACONST_NULL);
                this.getMethodVisitor().visitInsn(ARETURN);
            }

            /* ASM computes the stack size for us */
            this.getMethodVisitor().visitMaxs(0, 0);
            this.getMethodVisitor().visitEnd();
        }
    }

    public void emitByteCode() {
        /* Run through the insn stack and emit java byte code */
        while (!this.insnStack.empty()) {
            InsnType currentInsn = this.insnStack.pop();
            switch (currentInsn.getInsnType()) {
            case InsnType.NUMBER_LITERAL:
                emitNumberLiteral(currentInsn);
                break;
            case InsnType.STRING_LITERAL:
                emitStringLiteral(currentInsn);
                break;
            case InsnType.BOOLEAN_LITERAL:
                emitBooleanLiteral(currentInsn);
                break;
            case InsnType.METHOD_INVOCATION:
                emitMethodInvocation(currentInsn);
                break;
            case InsnType.CLASS_CREATION:
                emitClassCreation(currentInsn);
                break;
            case InsnType.INFIX_EXPRESSION:
                emitInfixExpression(currentInsn);
                break;
            case InsnType.SIMPLE_NAME:
                // HACK: Given that we have this case, no variable name should
                // be a part of a fully qualified class name.

                SimpleName simpleName = (SimpleName) currentInsn.getInsn();

                /* Logic for internal method parameter */
                if (!this.inField) {
                    LocalVariableNode nameRef =
                        this.isCurrentMethodLocal(simpleName.getIdentifier());
                    if (nameRef != null) {
                        if (nameRef.desc.equals("I")) {
                            this.getMethodVisitor().visitVarInsn(ILOAD, nameRef.index);
                        } else if (nameRef.desc.equals("F")) {
                            this.getMethodVisitor().visitVarInsn(FLOAD, nameRef.index);
                        } else if (nameRef.desc.equals("D")) {
                            this.getMethodVisitor().visitVarInsn(DLOAD, nameRef.index);
                        } else {
                            this.getMethodVisitor().visitVarInsn(ALOAD, nameRef.index);
                        }
                        this.lastType = nameRef.desc;
                        break;
                    }
                }

                /* Logic for field name */
                InVivoIdentifierDesc idNameRef =
                    this.isCurrentClassField(simpleName.getIdentifier());

                if (idNameRef != null) {
                    if ((ACC_STATIC & idNameRef.getAccess()) != 0)
                        this.getMethodVisitor().visitFieldInsn(GETSTATIC, idNameRef.getClassName(),
                            idNameRef.getName(), idNameRef.getDesc());
                    else {
                        this.getMethodVisitor().visitVarInsn(ALOAD, 0);
                        this.getMethodVisitor().visitFieldInsn(GETFIELD, idNameRef.getClassName(),
                            idNameRef.getName(), idNameRef.getDesc());
                    }
                }

                this.inField = false;
                break;
            case InsnType.CLASS_FIELD_ACCESS:
                this.inField = true;
                break;
            case InsnType.CLASS_METHOD_ACCESS:
                getMethodVisitor().visitVarInsn(ALOAD, 0);
                break;
            case InsnType.EXPLICIT_CAST:
                emitExplicitCast(currentInsn);
                break;
            case InsnType.ARRAY_ACCESS:
                emitArrayAccess(currentInsn);
                break;
            case InsnType.ARRAY_CREATE:
                emitArrayCreate(currentInsn);
                break;
            case InsnType.ARRAY_ELEMENT_PROLOGUE:
                emitArrayAccessProlog(currentInsn);
                break;
            case InsnType.ARRAY_ELEMENT_EPILOGUE:
                emitArrayAccessEpilogue(currentInsn);
                break;
            case InsnType.INFIX_EXPRESSION_STRING:
                emitInfixExprString();
                break;
            case InsnType.INFIX_STRINGBUILDER_PROLOGUE:
                emitInfixStringBuilderPrologue();
                break;
            case InsnType.INFIX_STRINGBUILDER_EPILOGUE:
                emitInfixStringBuilderEpilogue();
                break;
            case InsnType.INFIX_STRINGBUILDER_EXPREPILOGUE:
                emitInfixStringBuilderExprEpilogue();
                break;
            default:
                // discuss other cases with, @jon
                break;
            }
        }
    }

    private void emitInfixStringBuilderExprEpilogue() {
        this.getMethodVisitor().visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder",
            "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
    }

    private void emitInfixStringBuilderEpilogue() {
        this.getMethodVisitor().visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
    }

    private void emitInfixStringBuilderPrologue() {
        this.getMethodVisitor().visitMethodInsn(INVOKESTATIC, "java/lang/String",
            "valueOf", "(Ljava/lang/Object;)Ljava/lang/String;");
        this.getMethodVisitor().visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder",
            "<init>", "(Ljava/lang/String;)V");
    }

    private void emitInfixExprString() {
        this.getMethodVisitor().visitTypeInsn(NEW, "java/lang/StringBuilder");
        this.getMethodVisitor().visitInsn(DUP);
    }

    private void emitArrayAccessProlog(InsnType currentInsn) {
        int index = currentInsn.getIndex();
        emitPushInteger(index);
    }

    private void emitPushInteger(int index) {
        switch (index) {
        case 1:
            this.getMethodVisitor().visitInsn(ICONST_0);
            break;
        case 2:
            this.getMethodVisitor().visitInsn(ICONST_1);
            break;
        case 3:
            this.getMethodVisitor().visitInsn(ICONST_2);
            break;
        case 4:
            this.getMethodVisitor().visitInsn(ICONST_3);
            break;
        case 5:
            this.getMethodVisitor().visitInsn(ICONST_5);
            break;
        default:
            this.getMethodVisitor().visitIntInsn(BIPUSH, index);
            break;
        }
    }

    private void emitArrayAccessEpilogue(InsnType currentInsn) {
        Expression expr = currentInsn.getInsn();
        if (this.getExpressionType(expr).equals("I")) {
            this.getMethodVisitor().visitInsn(IASTORE);
        } else if (this.getExpressionType(expr).equals("D")) {
            this.getMethodVisitor().visitInsn(DASTORE);
        } else if (this.getExpressionType(expr).equals("F")) {
            this.getMethodVisitor().visitInsn(FASTORE);
        } else if (this.getExpressionType(expr).equals("L")) {
            this.getMethodVisitor().visitInsn(LASTORE);
        } else {
            this.getMethodVisitor().visitInsn(AASTORE);
        }
        this.getMethodVisitor().visitInsn(DUP);
    }

    private void emitArrayCreate(InsnType currentInsn) {
        ArrayCreation arrayExpr = (ArrayCreation) currentInsn.getInsn();
        this.emitPushInteger(arrayExpr.getInitializer().expressions().size());
        String elementType = arrayExpr.getType().getElementType().toString();
        if (elementType.equals("int")) {
            this.getMethodVisitor().visitIntInsn(NEWARRAY, T_INT);
        } else if (elementType.equals("double")) {
            this.getMethodVisitor().visitIntInsn(NEWARRAY, T_DOUBLE);
        } else if (elementType.equals("float")) {
            this.getMethodVisitor().visitIntInsn(NEWARRAY, T_FLOAT);
        } else if (elementType.equals("long")) {
            this.getMethodVisitor().visitIntInsn(NEWARRAY, T_LONG);
        } else {
            this.getMethodVisitor().visitTypeInsn(ANEWARRAY, elementType.replace(".", "/"));
        }
        this.getMethodVisitor().visitInsn(DUP);
    }

    private void emitArrayAccess(InsnType currentInsn) {
        ArrayAccess arrayExpr = (ArrayAccess) currentInsn.getInsn();
        String arrayType = this.getExpressionType(arrayExpr.getArray()).replace("[", "");
        if (arrayType.equals("I")) {
            this.getMethodVisitor().visitInsn(IALOAD);
        } else if (arrayType.equals("D")) {
            this.getMethodVisitor().visitInsn(DALOAD);
        } else if (arrayType.equals("F")) {
            this.getMethodVisitor().visitInsn(FALOAD);
        } else if (arrayType.equals("L")) {
            this.getMethodVisitor().visitInsn(LALOAD);
        } else if (arrayType.equals("Z")) {
            this.getMethodVisitor().visitInsn(BALOAD);
        } else {
            this.getMethodVisitor().visitInsn(AALOAD);
        }
    }

    private void emitExplicitCast(InsnType currentInsn) {
        CastExpression castExpr = (CastExpression) currentInsn.getInsn();
        String castSrc = this.getExpressionType(castExpr.getExpression());
        String castDst = castExpr.getType().toString();
        if (castSrc.equals("I")) {
            if (castDst.equals("java.lang.Integer") || castDst.equals("int")) {
                getMethodVisitor().visitInsn(I2F);
            } else if (castDst.equals("java.lang.Double") || castDst.equals("double")) {
                getMethodVisitor().visitInsn(I2D);
            } else if (castDst.equals("java.lang.Long") || castDst.equals("long")) {
                getMethodVisitor().visitInsn(I2L);
            } else if (castDst.equals("java.lang.Byte") || castDst.equals("byte")) {
                getMethodVisitor().visitInsn(I2B);
            } else if (castDst.equals("java.lang.Char") || castDst.equals("char")) {
                getMethodVisitor().visitInsn(I2C);
            } else if (castDst.equals("java.lang.Short") || castDst.equals("short")) {
                getMethodVisitor().visitInsn(I2S);
            } else {
                this.getMethodVisitor().visitMethodInsn(INVOKESTATIC, "java/lang/Integer",
                    "valueOf", "(I)Ljava/lang/Integer;");
            }
        } else if (castSrc.equals("F")) {
            if (castDst.equals("java.lang.Integer") || castDst.equals("int")) {
                getMethodVisitor().visitInsn(F2I);
            } else if (castDst.equals("java.lang.Double") || castDst.equals("double")) {
                getMethodVisitor().visitInsn(F2D);
            } else if (castDst.equals("java.lang.Long") || castDst.equals("long")) {
                getMethodVisitor().visitInsn(F2L);
            } else {
                this.getMethodVisitor().visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf",
                    "(F)Ljava/lang/Float;");
            }

        } else if (castSrc.equals("D")) {
            if (castDst.equals("java.lang.Integer") || castDst.equals("int")) {
                getMethodVisitor().visitInsn(D2I);
            } else if (castDst.equals("java.lang.Float") || castDst.equals("float")) {
                getMethodVisitor().visitInsn(D2F);
            } else if (castDst.equals("java.lang.Long") || castDst.equals("long")) {
                getMethodVisitor().visitInsn(D2L);
            } else {
                this.getMethodVisitor().visitMethodInsn(INVOKESTATIC, "java/lang/Double",
                    "valueOf", "(D)Ljava/lang/Double;");
            }
        } else if (castSrc.equals("L")) {
            if (castDst.equals("java.lang.Integer") || castDst.equals("int")) {
                getMethodVisitor().visitInsn(L2I);
            } else if (castDst.equals("java.lang.Double") || castDst.equals("double")) {
                getMethodVisitor().visitInsn(L2D);
            } else if (castDst.equals("java.lang.Float") || castDst.equals("float")) {
                getMethodVisitor().visitInsn(L2F);
            } else {
                this.getMethodVisitor().visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf",
                    "(S)Ljava/lang/Long;");
            }
        }
    }

    private void emitInfixExpression(InsnType currentInsn) {
        InfixExpression infixExpr = (InfixExpression) currentInsn.getInsn();
        Operator infixOp = infixExpr.getOperator();

        /*
         * We know what type it is because heuristically, the left and right
         * most operands will be parsed before we get to this point. So type
         * info stored at that point can be used.
         */

        /* Arithmetic operators */
        if (infixOp.equals(Operator.PLUS)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(IADD);
            } else if (this.lastType.equals("D")) {
                this.getMethodVisitor().visitInsn(DADD);
            } else if (this.lastType.equals("F")) {
                this.getMethodVisitor().visitInsn(FADD);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LADD);
            }
        } else if (infixOp.equals(Operator.MINUS)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(ISUB);
            } else if (this.lastType.equals("D")) {
                this.getMethodVisitor().visitInsn(DSUB);
            } else if (this.lastType.equals("F")) {
                this.getMethodVisitor().visitInsn(FSUB);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LSUB);
            }
        } else if (infixOp.equals(Operator.DIVIDE)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(IDIV);
            } else if (this.lastType.equals("D")) {
                this.getMethodVisitor().visitInsn(DDIV);
            } else if (this.lastType.equals("F")) {
                this.getMethodVisitor().visitInsn(FDIV);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LSUB);
            }
        } else if (infixOp.equals(Operator.TIMES)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(IMUL);
            } else if (this.lastType.equals("D")) {
                this.getMethodVisitor().visitInsn(DMUL);
            } else if (this.lastType.equals("F")) {
                this.getMethodVisitor().visitInsn(FMUL);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LDIV);
            }
        } else if (infixOp.equals(Operator.REMAINDER)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(IREM);
            } else if (this.lastType.equals("D")) {
                this.getMethodVisitor().visitInsn(DREM);
            } else if (this.lastType.equals("F")) {
                this.getMethodVisitor().visitInsn(FREM);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LREM);
            }
        } /* Conditionals */
        else if (infixOp.equals(Operator.CONDITIONAL_AND)) {

        } else if (infixOp.equals(Operator.CONDITIONAL_OR)) {

        } /* Comparison operators */
        else if (infixOp.equals(Operator.EQUALS)) {

        } else if (infixOp.equals(Operator.GREATER)) {

        } else if (infixOp.equals(Operator.GREATER_EQUALS)) {

        } else if (infixOp.equals(Operator.LESS)) {

        } else if (infixOp.equals(Operator.LESS_EQUALS)) {

        } else if (infixOp.equals(Operator.NOT_EQUALS)) {

        } /* Boolean operators */
        else if (infixOp.equals(Operator.AND)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(IAND);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LAND);
            }
        } else if (infixOp.equals(Operator.OR)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(IOR);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LOR);
            }
        } else if (infixOp.equals(Operator.XOR)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(IXOR);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LXOR);
            }
        } else if (infixOp.equals(Operator.LEFT_SHIFT)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(ISHL);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LSHR);
            }
        } else if (infixOp.equals(Operator.RIGHT_SHIFT_SIGNED)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(ISHR);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LSHR);
            }
        } else if (infixOp.equals(Operator.RIGHT_SHIFT_UNSIGNED)) {
            if (this.lastType.equals("I")) {
                this.getMethodVisitor().visitInsn(IUSHR);
            } else if (this.lastType.equals("L")) {
                this.getMethodVisitor().visitInsn(LUSHR);
            }
        }
    }

    private void emitClassCreation(InsnType currentInsn) {
        String className;
        className = ((ClassInstanceCreation) currentInsn.getInsn()).getType().toString();
        List<Expression> args = ((ClassInstanceCreation) currentInsn.getInsn()).arguments();

        StringBuilder desc = new StringBuilder();
        for (Expression arg : args) {
            desc.append(this.getExpressionType(arg));
        }
        getMethodVisitor().visitMethodInsn(INVOKESPECIAL, className.replace('.', '/'), "<init>",
            "()V");
    }

    private void emitMethodInvocation(InsnType currentInsn) {
        MethodInvocation invocation = (MethodInvocation) currentInsn.getInsn();
        String fullExpression = invocation.toString();
        String methodName = ((MethodInvocation) currentInsn.getInsn()).getName().getIdentifier();
        String predName = fullExpression.substring(0, fullExpression.indexOf(methodName) - 1);

        String varName = predName.substring(predName.lastIndexOf(".") + 1);

        StringBuilder invocationDesc = new StringBuilder();

        for (Object expr : invocation.arguments()) {
            invocationDesc.append(this.getExpressionType((Expression) expr));
        }

        if (this.isCurrentClassField(varName) != null || this.isCurrentMethodLocal(varName) != null) {

            String className = this.getLocalDesc(predName);
            String simpleName =
                className.substring(className.lastIndexOf('/') + 1, className.length() - 1);

            InVivoMethodDesc methodDesc =
                InferenceEngine.getMethodInfo(simpleName, methodName, invocationDesc.toString())
                    .get(0);
            getMethodVisitor().visitMethodInsn(INVOKEVIRTUAL,
                className.substring(1, className.length() - 1), methodName,
                methodDesc.getMethodDesc());
        } else {
            /*
             * Static invocation case. We assume that the return type is the
             * class name (for convenience)
             */
            getMethodVisitor().visitMethodInsn(INVOKESTATIC, predName.replace('.', '/'),
                methodName, // Method call itself
                "(" + invocationDesc.toString() + ")L" + predName.replace('.', '/') + ";");
        }
    }

    private void emitBooleanLiteral(InsnType currentInsn) {
        if (currentInsn.getInsn().equals("true"))
            getMethodVisitor().visitInsn(ICONST_1);
        else
            getMethodVisitor().visitInsn(ICONST_0);
        this.lastType = "Z";
    }

    private void emitStringLiteral(InsnType currentInsn) {
        getMethodVisitor().visitLdcInsn(currentInsn.getInsn().toString());
        this.lastType = this.getExpressionType(currentInsn.getInsn());
    }

    private void emitNumberLiteral(InsnType currentInsn) {
        if (currentInsn.getInsn().toString().contains(".")) {
            getMethodVisitor().visitLdcInsn(new Double(currentInsn.getInsn().toString()));
            this.lastType = "D";
        } else if (currentInsn.getInsn().toString().endsWith("L")) {
            getMethodVisitor().visitLdcInsn(new Long(currentInsn.getInsn().toString()));
            this.lastType = "L";
        } else if (currentInsn.getInsn().toString().endsWith("F")) {
            getMethodVisitor().visitLdcInsn(new Float(currentInsn.getInsn().toString()));
            this.lastType = "F";
        } else {
            try {
                this.emitPushInteger(Integer.valueOf(currentInsn.getInsn().toString()));
                this.lastType = "I";
            } catch (NumberFormatException nfe) {
                getMethodVisitor().visitLdcInsn(new Long(currentInsn.getInsn().toString()));
                this.lastType = "L";
            }
        }
    }

    private String exprType;

    public String getExpressionType(Expression expr) {
        // Why java sucks? No anonymous functions and closures...
        ASTVisitor exprVisitor = new ASTVisitor() {
            public boolean visit(ClassInstanceCreation node) {
                InVivoAsmEval.this.exprType = node.getType().toString();
                return false;
            }

            public boolean visit(MethodInvocation node) {
                String fullExpression = node.toString();
                String methodName = node.getName().getIdentifier();
                String predName =
                    fullExpression.substring(0, fullExpression.indexOf(methodName) - 1);

                String varName = predName.substring(predName.lastIndexOf(".") + 1);

                StringBuilder invocationDesc = new StringBuilder();

                for (Object expr : node.arguments()) {
                    invocationDesc.append(InVivoAsmEval.this.getExpressionType((Expression) expr));
                }

                if (InVivoAsmEval.this.isCurrentClassField(varName) != null
                    || InVivoAsmEval.this.isCurrentMethodLocal(varName) != null) {

                    String className = InVivoAsmEval.this.getLocalDesc(predName);
                    String simpleName =
                        className.substring(className.lastIndexOf('/') + 1, className.length() - 1);

                    InVivoMethodDesc methodDesc =
                        InferenceEngine.getMethodInfo(simpleName, methodName,
                            invocationDesc.toString()).get(0);
                    InVivoAsmEval.this.exprType = methodDesc.getMethodReturnType();
                } else {
                    /**
                     * If its a static case, simply return the class name which
                     * has been suitably modified.
                     */
                    InVivoAsmEval.this.exprType = "L" + predName.replace(".", "/") + ";";
                }

                return false;
            }

            public boolean visit(InfixExpression node) {
                InVivoAsmEval.this.exprType = getExpressionType(node.getLeftOperand());
                return false;
            }

            public boolean visit(PrimitiveType node) {
                return false;
            }

            public boolean visit(NumberLiteral node) {
                if (node.toString().contains("."))
                    InVivoAsmEval.this.exprType = "D";
                else
                    InVivoAsmEval.this.exprType = "I";
                return false;
            }

            public boolean visit(StringLiteral node) {
                InVivoAsmEval.this.exprType = "Ljava/lang/String;";
                return false;
            }

            public boolean visit(BooleanLiteral node) {
                InVivoAsmEval.this.exprType = "Z";
                return false;
            }

            public boolean visit(SimpleName node) {
                LocalVariableNode lvnodeRef = isCurrentMethodLocal(node.toString());

                if (lvnodeRef != null) {
                    InVivoAsmEval.this.exprType = lvnodeRef.desc;
                    return false;
                }

                InVivoIdentifierDesc idNameRef = isCurrentClassField(node.getIdentifier());

                if (idNameRef != null)
                    InVivoAsmEval.this.exprType = idNameRef.getDesc();

                return false;
            }

            public boolean visit(CastExpression node) {
                /**
                 * Even in casts, the expressions MUST be fully qualified.
                 */
                Type castType = node.getType();
                InVivoAsmEval.this.exprType = castType.toString();
                return false;
            }
            
            public boolean visit(ArrayAccess node) {
                InVivoAsmEval.this.exprType = InVivoAsmEval.this.getExpressionType(node.getArray());
                return false;
            }
        };

        expr.accept(exprVisitor);

        return this.exprType;
    }

    private ASTVisitor asmVisitor = new ASTVisitor() {
        public boolean visit(ClassInstanceCreation node) {
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.CLASS_CREATION, node));

            ListIterator<?> li = node.arguments().listIterator(node.arguments().size());
            while (li.hasPrevious()) {
                ((Expression) li.previous()).accept(InVivoAsmEval.this.asmVisitor);
            }

            return false;
        }

        public boolean visit(MethodInvocation node) {
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.METHOD_INVOCATION, node));

            ListIterator<?> li = node.arguments().listIterator(node.arguments().size());
            while (li.hasPrevious()) {
                ((Expression) li.previous()).accept(InVivoAsmEval.this.asmVisitor);
            }

            if (node.toString().startsWith("this."))
                InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.CLASS_METHOD_ACCESS, node));

            return false;
        }

        public boolean visit(InfixExpression node) {
            if (InVivoAsmEval.this.getExpressionType(node).equals("Ljava/lang/String;")) {
                InVivoAsmEval.this.insnStack.push(new InsnType(
                    InsnType.INFIX_STRINGBUILDER_EPILOGUE, node.getRightOperand()));
                
                for (int i = node.extendedOperands().size() - 1; i >= 0; i--) {
                    InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.INFIX_STRINGBUILDER_EXPREPILOGUE, node));
                    ((Expression) node.extendedOperands().get(i)).accept(InVivoAsmEval.this.asmVisitor);
                }
                
                InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.INFIX_STRINGBUILDER_EXPREPILOGUE, node));
                node.getRightOperand().accept(InVivoAsmEval.this.asmVisitor);
                
                InVivoAsmEval.this.insnStack.push(new InsnType(
                    InsnType.INFIX_STRINGBUILDER_PROLOGUE, node.getLeftOperand()));
                
                node.getLeftOperand().accept(InVivoAsmEval.this.asmVisitor);
                
                InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.INFIX_EXPRESSION_STRING,
                    node));
                
                return false;
            } else
                InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.INFIX_EXPRESSION, node));
            return true;
        }

        public boolean visit(PrimitiveType node) {
            return true;
        }

        public boolean visit(NumberLiteral node) {
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.NUMBER_LITERAL, node));
            return true;
        }

        public boolean visit(StringLiteral node) {
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.STRING_LITERAL, node));
            return true;
        }

        public boolean visit(BooleanLiteral node) {
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.BOOLEAN_LITERAL, node));
            return true;
        }

        public boolean visit(SimpleName node) {
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.SIMPLE_NAME, node));
            return true;
        }

        public boolean visit(FieldAccess node) {
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.CLASS_FIELD_ACCESS, node));
            return true;
        }

        public boolean visit(CastExpression node) {
            /**
             * Even in casts, the expressions MUST be fully qualified.
             */
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.EXPLICIT_CAST, node));
            return true;
        }

        public boolean visit(ArrayAccess node) {
            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.ARRAY_ACCESS, node));
            node.getIndex().accept(InVivoAsmEval.this.asmVisitor);
            node.getArray().accept(InVivoAsmEval.this.asmVisitor);
            return false;
        }

        public boolean visit(ArrayCreation node) {
            for (int i = node.getInitializer().expressions().size() - 1; i >= 0; i--) {
                InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.ARRAY_ELEMENT_EPILOGUE,
                    (Expression) node.getInitializer().expressions().get(i)));
                ((Expression) node.getInitializer().expressions().get(i))
                    .accept(InVivoAsmEval.this.asmVisitor);
                InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.ARRAY_ELEMENT_PROLOGUE, i));
            }

            InVivoAsmEval.this.insnStack.push(new InsnType(InsnType.ARRAY_CREATE, node));
            return false;
        }

    };

    // Getters and setters

    public ClassWriter getClassWriter() {
        return this.classWriter;
    }

    public void setClassWriter(ClassWriter cw) {
        this.classWriter = cw;
    }

    public ClassVisitor getClassVisitor() {
        return this.classVisitor;
    }

    public void setClassVisitor(ClassVisitor cv) {
        this.classVisitor = cv;
    }

    public MethodVisitor getMethodVisitor() {
        return this.methodVisitor;
    }

    public void setMethodVisitor(MethodVisitor mv) {
        this.methodVisitor = mv;
    }

    public FieldVisitor getFieldVisitor() {
        return this.fieldVisitor;
    }

    public void setFieldVisitor(FieldVisitor fv) {
        this.fieldVisitor = fv;
    }

    public boolean isInMethod() {
        return this.inMethod;
    }

    public void setInMethod(boolean inMethod) {
        this.inMethod = inMethod;
    }

    public Map<String, String> getCurrentVars() {
        return this.currentVars;
    }

    public void setCurrentVars(Map<String, String> currentVars) {
        this.currentVars = currentVars;
    }
}
