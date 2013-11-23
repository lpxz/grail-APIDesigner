/*
 * Copyright (C) 2007 Jï¿?io Vilmar Gesser.
 * 
 * This file is part of Java 1.5 parser and Abstract Syntax Tree.
 *
 * Java 1.5 parser and Abstract Syntax Tree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Java 1.5 parser and Abstract Syntax Tree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Java 1.5 parser and Abstract Syntax Tree.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Created on 05/10/2006
 */
package incorrectCompositions.detection.setupCompositionMetadataInSource;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.DumpVisitor;

import java.util.Iterator;
import java.util.Stack;

import utils.ASTlpxzHelper;
import utils.CentralConstants;

import atomicCompositions.serialization.SerializableComposition;

/**
 * @author Julio Vilmar Gesser
 */

public final class AddLibAPIMarkerVisitor extends DumpVisitor {
	public SerializableComposition	compositionInProgress	= null;

	public void setCompositionInProgress(
			SerializableComposition compositionInProgress) {
		this.compositionInProgress = compositionInProgress;
	}

	// the following code: 1) maintain the full name of the current class, 
	//   				   2) check the constraint: some fields must be added.
	Stack	innerClassStack	= new Stack();
	boolean	fieldAdded		= false;

	public void visit(CompilationUnit n, Object arg) {
		fieldAdded = false;
		if (n.getPackage() != null) {
			n.getPackage().accept(this, arg);
			innerClassStack.push(n.getPackage().getName());
		}
		if (n.getImports() != null) {
			for (ImportDeclaration i : n.getImports()) {
				i.accept(this, arg);
			}
			printer.printLn();
		}
		if (n.getTypes() != null) {
			for (Iterator<TypeDeclaration> i = n.getTypes().iterator(); i
					.hasNext();) {
				i.next().accept(this, arg);
				printer.printLn();
				if (i.hasNext()) {
					printer.printLn();
				}
			}
		}

		if (n.getPackage() != null) {
			Object pObject = innerClassStack.pop();
			if (pObject != n.getPackage().getName())
				throw new RuntimeException("check the pairing");
		}

		if (fieldAdded == false)
			throw new RuntimeException(
					"you did not find any hit APIs in the library class?");
		this.compositionInProgress = null;
		fieldAdded = false;
	}

	public void visit(ClassOrInterfaceDeclaration n, Object arg) {
		printJavadoc(n.getJavaDoc(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());

		if (n.isInterface()) {
			printer.print("interface ");
		} else {
			printer.print("class ");
		}

		printer.print(n.getName());
		innerClassStack.push(n.getName());

		printTypeParameters(n.getTypeParameters(), arg);//// soot does not maintain the typeparameters at the bytecode level.ignore it.

		if (n.getExtends() != null) {
			printer.print(" extends ");
			for (Iterator<ClassOrInterfaceType> i = n.getExtends().iterator(); i
					.hasNext();) {
				ClassOrInterfaceType c = i.next();
				c.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}

		if (n.getImplements() != null) {
			printer.print(" implements ");
			for (Iterator<ClassOrInterfaceType> i = n.getImplements()
					.iterator(); i.hasNext();) {
				ClassOrInterfaceType c = i.next();
				c.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}

		printer.printLn(" {");
		printer.indent();
		if (n.getMembers() != null) {
			printMembers(n.getMembers(), arg);
			if (ASTlpxzHelper.checkClass(innerClassStack,
					compositionInProgress.getLibClass())) {
				addField(compositionInProgress);
				System.err.println("the library-side marker field added!");

			}
		}
		printer.unindent();
		printer.print("}");

		Object pObject = innerClassStack.pop();
		if (pObject != n.getName())
			throw new RuntimeException(
					"checking the paring between push and pop");
	}

	private void addField(SerializableComposition compositionInProgress2) {
		fieldAdded = true;
		String fieldname = getFieldName(compositionInProgress2);
		printer.print("int " + fieldname + ";\n");
	}

	private String getFieldName(SerializableComposition compositionInProgress2) {
		return CentralConstants.ACprefix + compositionInProgress2.getId()
				+ "_field";
	}

	public void visit(MethodDeclaration n, Object arg) {
		printJavadoc(n.getJavaDoc(), arg);
		printMemberAnnotations(n.getAnnotations(), arg);
		printModifiers(n.getModifiers());
		printTypeParameters(n.getTypeParameters(), arg);
		if (n.getTypeParameters() != null) {
			printer.print(" ");
		}

		n.getType().accept(this, arg);
		printer.print(" ");
		printer.print(n.getName());

		printer.print("(");
		if (n.getParameters() != null) {
			for (Iterator<Parameter> i = n.getParameters().iterator(); i
					.hasNext();) {
				Parameter p = i.next();
				p.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		printer.print(")");

		for (int i = 0; i < n.getArrayCount(); i++) {
			printer.print("[]");
		}

		if (n.getThrows() != null) {
			printer.print(" throws ");
			for (Iterator<NameExpr> i = n.getThrows().iterator(); i.hasNext();) {
				NameExpr name = i.next();
				name.accept(this, arg);
				if (i.hasNext()) {
					printer.print(", ");
				}
			}
		}
		if (n.getBody() == null) {
			printer.print(";");
		} else {
			printer.print(" ");
			BlockStmt body = n.getBody();
			if (ASTlpxzHelper.checkClass(innerClassStack,
					compositionInProgress.getLibClass())
					&& atomicAPI(n)) {
				bodyInProgress = body;
				body.accept(this, arg);
				bodyInProgress = null;
			} else {
				body.accept(this, arg);
			}

		}
	}

	private boolean atomicAPI(MethodDeclaration n) {
		return n.toString().contains("synchronized");
	}

	BlockStmt	bodyInProgress	= null;

	public void visit(BlockStmt n, Object arg) {

		printer.printLn("{");

		if (n.getStmts() != null) {
			printer.indent();
			if (n == bodyInProgress) {
				addMarkerToMethod(compositionInProgress);
			}
			for (Statement s : n.getStmts()) {
				s.accept(this, arg);
				printer.printLn();
			}
			printer.unindent();
		}

		printer.print("}");

	}

	private void addMarkerToMethod(
			SerializableComposition compositionInProgress2) {
		printer.print(getFieldName(compositionInProgress2) + "=100;\n");
	}

}
