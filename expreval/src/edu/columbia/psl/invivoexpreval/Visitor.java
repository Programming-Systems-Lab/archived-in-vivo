


package edu.columbia.psl.invivoexpreval;

/**
 * Basis for the "visitor" pattern as described in "Gamma, Helm, Johnson,
 * Vlissides: Design Patterns".
 */
public class Visitor {
    public interface ComprehensiveVisitor
    extends ImportVisitor, TypeDeclarationVisitor, TypeBodyDeclarationVisitor, BlockStatementVisitor, AtomVisitor {
    }

    public interface ImportVisitor {
        void visitSingleTypeImportDeclaration(Java.CompilationUnit.SingleTypeImportDeclaration stid);
        void visitTypeImportOnDemandDeclaration(Java.CompilationUnit.TypeImportOnDemandDeclaration tiodd);
        void visitSingleStaticImportDeclaration(Java.CompilationUnit.SingleStaticImportDeclaration ssid);
        void visitStaticImportOnDemandDeclaration(Java.CompilationUnit.StaticImportOnDemandDeclaration siodd);
    }

    public interface TypeDeclarationVisitor {
        void visitAnonymousClassDeclaration(Java.AnonymousClassDeclaration acd);
        void visitLocalClassDeclaration(Java.LocalClassDeclaration lcd);
        void visitPackageMemberClassDeclaration(Java.PackageMemberClassDeclaration pmcd);
        void visitMemberInterfaceDeclaration(Java.MemberInterfaceDeclaration mid);
        void visitPackageMemberInterfaceDeclaration(Java.PackageMemberInterfaceDeclaration pmid);
        void visitMemberClassDeclaration(Java.MemberClassDeclaration mcd);
    }

    public interface TypeBodyDeclarationVisitor {
        void visitMemberInterfaceDeclaration(Java.MemberInterfaceDeclaration mid);
        void visitMemberClassDeclaration(Java.MemberClassDeclaration mcd);
        void visitConstructorDeclarator(Java.ConstructorDeclarator cd);
        void visitInitializer(Java.Initializer i);
        void visitMethodDeclarator(Java.MethodDeclarator md);
        void visitFieldDeclaration(Java.FieldDeclaration fd);
    }

    public interface BlockStatementVisitor {
        void visitInitializer(Java.Initializer i);
        void visitFieldDeclaration(Java.FieldDeclaration fd);
        void visitLabeledStatement(Java.LabeledStatement ls);
        void visitBlock(Java.Block b);
        void visitExpressionStatement(Java.ExpressionStatement es);
        void visitIfStatement(Java.IfStatement is);
        void visitForStatement(Java.ForStatement fs);
        void visitWhileStatement(Java.WhileStatement ws);
        void visitTryStatement(Java.TryStatement ts);
        void visitSwitchStatement(Java.SwitchStatement ss);
        void visitSynchronizedStatement(Java.SynchronizedStatement ss);
        void visitDoStatement(Java.DoStatement ds);
        void visitLocalVariableDeclarationStatement(Java.LocalVariableDeclarationStatement lvds);
        void visitReturnStatement(Java.ReturnStatement rs);
        void visitThrowStatement(Java.ThrowStatement ts);
        void visitBreakStatement(Java.BreakStatement bs);
        void visitContinueStatement(Java.ContinueStatement cs);
        void visitEmptyStatement(Java.EmptyStatement es);
        void visitLocalClassDeclarationStatement(Java.LocalClassDeclarationStatement lcds);
        void visitAlternateConstructorInvocation(Java.AlternateConstructorInvocation aci);
        void visitSuperConstructorInvocation(Java.SuperConstructorInvocation sci);
    }

    public interface AtomVisitor extends RvalueVisitor, TypeVisitor {
        
    }

    public interface TypeVisitor {
        void visitArrayType(Java.ArrayType at);
        void visitBasicType(Java.BasicType bt);
        void visitReferenceType(Java.ReferenceType rt);
        void visitRvalueMemberType(Java.RvalueMemberType rmt);
        void visitSimpleType(Java.SimpleType st);
    }

    public interface RvalueVisitor extends LvalueVisitor {
        void visitArrayLength(Java.ArrayLength al);
        void visitAssignment(Java.Assignment a);
        void visitUnaryOperation(Java.UnaryOperation uo);
        void visitBinaryOperation(Java.BinaryOperation bo);
        void visitCast(Java.Cast c);
        void visitClassLiteral(Java.ClassLiteral cl);
        void visitConditionalExpression(Java.ConditionalExpression ce);
        void visitCrement(Java.Crement c);
        void visitInstanceof(Java.Instanceof io);
        void visitMethodInvocation(Java.MethodInvocation mi);
        void visitSuperclassMethodInvocation(Java.SuperclassMethodInvocation smi);
        void visitIntegerLiteral(Java.IntegerLiteral il);
        void visitFloatingPointLiteral(Java.FloatingPointLiteral fpl);
        void visitBooleanLiteral(Java.BooleanLiteral bl);
        void visitCharacterLiteral(Java.CharacterLiteral cl);
        void visitStringLiteral(Java.StringLiteral sl);
        void visitNullLiteral(Java.NullLiteral nl);
        void visitNewAnonymousClassInstance(Java.NewAnonymousClassInstance naci);
        void visitNewArray(Java.NewArray na);
        void visitNewInitializedArray(Java.NewInitializedArray nia);
        void visitNewClassInstance(Java.NewClassInstance nci);
        void visitParameterAccess(Java.ParameterAccess pa);
        void visitQualifiedThisReference(Java.QualifiedThisReference qtr);
        void visitThisReference(Java.ThisReference tr);
    }

    public interface LvalueVisitor {
        void visitAmbiguousName(Java.AmbiguousName an);
        void visitArrayAccessExpression(Java.ArrayAccessExpression aae);
        void visitFieldAccess(Java.FieldAccess fa);
        void visitFieldAccessExpression(Java.FieldAccessExpression fae);
        void visitSuperclassFieldAccessExpression(Java.SuperclassFieldAccessExpression scfae);
        void visitLocalVariableAccess(Java.LocalVariableAccess lva);
        void visitParenthesizedExpression(Java.ParenthesizedExpression pe);
    }
}
