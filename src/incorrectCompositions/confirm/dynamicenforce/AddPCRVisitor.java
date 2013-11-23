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
package incorrectCompositions.confirm.dynamicenforce;

import incorrectCompositions.detection.onlineMonitoring.runMonitor.lpxz.context.PreciseSTEContextHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.DumpVisitor;

import java.util.Iterator;

import utils.ASTlpxzHelper;
import utils.CentralConstants;
import utils.SootHelper;
import atomicCompositions.serialization.SerializableComposition;

public final class AddPCRVisitor extends DumpVisitor {
	public SerializableComposition	compositionInProgress	= null;

	public void setCompositionInProgress(
			SerializableComposition compositionInProgress) {
		this.compositionInProgress = compositionInProgress;
	}

	//	public int	hitCount	= 0;

	//for constraint (only one client method is hit) checking.
	public void visit(CompilationUnit n, Object arg) {
		if (n.getPackage() != null) {
			n.getPackage().accept(this, arg);
		}
		if (n.getImports() != null) {
			for (ImportDeclaration i : n.getImports()) {
				i.accept(this, arg);
			}
			printer.print("import " + DynamicWitness.runtimeLibName);
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

		//redundant code but good habit:
		this.compositionInProgress = null;
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

			String clientMethod = compositionInProgress.getClientMethod();
			String rContainer = compositionInProgress
					.getrInvokeContainerMethodString();

			if (ASTlpxzHelper.checkMethod(n, clientMethod)) {
				inClientMethod = true;
			} else if (ASTlpxzHelper.checkMethod(n, rContainer)) {
				inrContainer = true;
			}

			body.accept(this, arg);
			inClientMethod = false; // blind set.
			inrContainer = false;
		}
	}

	boolean	inClientMethod	= false;
	boolean	inrContainer	= false;

	//soot:    public static void main(java.lang.String[])
	//ast:     public static void main(String[] args) 

	// ambiguous packages.
	// If there are two classes: A.String and B.String (A and B are packages), the source code (ast) has to be explicit about which String to use.

	// about inner class.
	//soot: Employee$Internal
	//ast: Employee.Internal

	public void visit(ExpressionStmt n, Object arg) {
		if (inClientMethod) {
			// add PC.
			int pline = compositionInProgress.getFirstInvokeSite();
			if (pline >= n.getBeginLine() && pline <= n.getEndLine()) {
				String pInvokeAPIName = PreciseSTEContextHelper
						.getMethName_sootsig(compositionInProgress
								.getFirstInvokedAPI());
				if (!n.toString().contains(pInvokeAPIName))
					throw new RuntimeException("assertion failure");
				String argname = AddPCRVisitor0.pthis;
				printer.print(DynamicWitness.runtimeLibName
						+ ".bidAtP(Thread.currentThread(), " + argname + ");\n"); //add pinvoke, i.e., first invoke
			}

			int cline = compositionInProgress.getLastInvokeSite();
			if (cline >= n.getBeginLine() && cline <= n.getEndLine()) {
				String cInvokeAPIName = PreciseSTEContextHelper
						.getMethName_sootsig(compositionInProgress
								.getLastInvokedAPI());
				if (!n.toString().contains(cInvokeAPIName))
					throw new RuntimeException("assertion failure");
				String argname = AddPCRVisitor0.cthis;
				printer.print(DynamicWitness.runtimeLibName
						+ ".bidAtC(Thread.currentThread(), " + argname + ");\n"); //add pinvoke, i.e., first invoke
			}

		}
		if (inrContainer) {
			int rline = compositionInProgress.getrInokeSite();
			if (rline >= n.getBeginLine() && rline <= n.getEndLine()) {
				String rInvokeAPIName = PreciseSTEContextHelper
						.getMethName_sootsig(compositionInProgress
								.getrInvokeAPI());
				if (!n.toString().contains(rInvokeAPIName))
					throw new RuntimeException("assertion failure");
				String argname = AddPCRVisitor0.rthisString;
				printer.print(DynamicWitness.runtimeLibName
						+ ".checkAtR(Thread.currentThread(), " + argname
						+ ");\n"); //add pinvoke, i.e., first invoke
			}
		}
		n.getExpression().accept(this, arg);
		printer.print(";");
	}

}
