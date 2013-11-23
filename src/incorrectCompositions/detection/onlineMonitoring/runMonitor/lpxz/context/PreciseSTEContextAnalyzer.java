package incorrectCompositions.detection.onlineMonitoring.runMonitor.lpxz.context;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.Label;
import org.objectweb.asm.commons.EmptyVisitor;


//NBBBBBBB!!!!!!!!!!!!

/*
 * By precise stack trace, we mean the method has its signature, rather than only the name, as what is shown by eclipse console.
 * The main/run methods come last (the last entry) in the stack (or stack vector).
 * An example stack is as follows:
 * Employee$Internal.setSalaray(int)(Employee.java)
 ClientClass.clientMethod(Employee$Internal,Employee$Internal)(ClientClass.java:38)
 ClientClass.access$0(Employee$Internal,Employee$Internal)(ClientClass.java:31)
 ClientClass$1.run()(ClientClass.java:14)
 * */
public class PreciseSTEContextAnalyzer {

	// void setrunMode(Parser$runModes)

	public enum runModes {
		DEFAULT_MODE, MULTI_RAMP, RAMP_DOWN, RAMP_UP, RECORDING, STOP
	}

	static List<StackTraceElement>			appEles			= new ArrayList<StackTraceElement>();

	static HashMap<String, List<String>>	thread2stack	= new HashMap<String, List<String>>();	;

	/**
	 * Returns the canonical name of the underlying class as defined by the Java
	 * Language Specification. Returns null if the underlying class does not
	 * have a canonical name (i.e., if it is a local or anonymous class or an
	 * array whose component type does not have a canonical name).
	 * 
	 * @return the canonical name of the underlying class if it exists, and
	 *         <tt>null</tt> otherwise.
	 * @since 1.5
	 */
	// different from jdk's : X$Y, while jdk tells X.Y
	public static String getCanonicalName(Class clazz) {
		if (clazz.isArray()) {
			String canonicalName = getCanonicalName(clazz.getComponentType());
			if (canonicalName != null)
				return canonicalName + "[]";
			else
				return null;
		}
		if (clazz.isLocalClass() || clazz.isAnonymousClass()) // isLocalOrAnonymousClass(),
																// may need to
																// be expanded
																// more later
			return null;
		Class<?> enclosingClass = clazz.getEnclosingClass();
		if (enclosingClass == null) { // top level class
			return clazz.getName();
		} else {
			String enclosingName = getCanonicalName(enclosingClass);
			if (enclosingName == null)
				return null;
			return enclosingName + "$" + clazz.getSimpleName();
		}
	}

	public static String getClassMethodSig(Object object) {
		if (object instanceof Method) {
			try {
				StringBuffer sb = new StringBuffer();

				Method method = (Method) object;
				sb.append((method.getDeclaringClass().getName()) + ".");
				sb.append(method.getName() + "(");
				Class[] params = method.getParameterTypes(); // avoid clone
				for (int j = 0; j < params.length; j++) {
					// see the function
					// canonical:
					// edu.hkust.clap.lpxz.asmStack.PreciseStackGetter.runModes
					// java.lang.String[]
					// directprint:
					// edu.hkust.clap.lpxz.asmStack.PreciseStackGetter$runModes
					// [Ljava.lang.String;
					// soot:
					// edu.hkust.clap.lpxz.asmStack.PreciseStackGetter$runModes
					// java.lang.String[]
					// System.out.println(getCanonicalName(params[j]));
					sb.append(getCanonicalName(params[j]));
					if (j < (params.length - 1))
						sb.append(",");
				}
				sb.append(")");

				return sb.toString();
			} catch (Exception e) {
				return "<" + e + ">";
			}

		} else {
			try {
				StringBuffer sb = new StringBuffer();

				Constructor cons = (Constructor) object;
				sb.append((cons.getDeclaringClass().getName()) + ".");
				sb.append("<init>" + "("); // do not use method.getName(), it
											// tells the class name
				Class[] params = cons.getParameterTypes(); // avoid clone
				for (int j = 0; j < params.length; j++) {
					sb.append(getCanonicalName(params[j]));
					if (j < (params.length - 1))
						sb.append(",");
				}
				sb.append(")");

				return sb.toString();
			} catch (Exception e) {
				return "<" + e + ">";
			}
		}

	}

	public static String getClassMethodSig(StackTraceElement ele)
			throws Exception {
		Object methodorCons = getMethodOrConstructor(ele);
		return getClassMethodSig(methodorCons);
	}

	public static List<String> getPreciseStackTraceContext(String topmostMsig) {
		String tname = Thread.currentThread().getName();
		List<String> stack = thread2stack.get(tname);
		if (stack == null) {
			stack = new ArrayList<String>();
			thread2stack.put(tname, stack);
		}
		stack.clear();

		StackTraceElement[] eles = Thread.currentThread().getStackTrace();
		appEles.clear();
		for (int i = 0; i < eles.length; i++) {// remove those non-app stes, like sun.reflection
			if (PreciseSTEContextEncoderDecoder.isApplicationContext(eles[i].toString())) {
				appEles.add(eles[i]);
			}
		}

		for (int i = 0; i < appEles.size(); i++) {
			StackTraceElement elei = appEles.get(i);
			if (i == 0) { // msig tells about the topmost scenario,
				String lineNORemoved = PreciseSTEContextHelper
						.tryRemoveLineSpec(elei.toString());
				String lastPartInSTE = PreciseSTEContextHelper
						.getPartInLastBrace(lineNORemoved);
				String classmethodsig = PreciseSTEContextHelper
						.getFullMethodArg_sootsig(topmostMsig);// always
				stack.add(classmethodsig + "(" + lastPartInSTE + ")");

			} else {
				try {
					String classmethodsig = getClassMethodSig(elei);// ASM here.
																	// precise,
																	// but very
																	// inefficient!!
					String partInlastBrace = PreciseSTEContextHelper
							.getPartInLastBrace(elei.toString());

					stack.add(classmethodsig + "(" + partInlastBrace + ")");

				} catch (Exception e) {
				}
			}
		}

		return stack;
	}

	public static Object getMethodOrConstructor(
			final StackTraceElement stackTraceElement) throws Exception {
		final String stackTraceClassName = stackTraceElement.getClassName();
		final String stackTraceMethodName = stackTraceElement.getMethodName();
		final int stackTraceLineNumber = stackTraceElement.getLineNumber();
		Class<?> stackTraceClass = Class.forName(stackTraceClassName);
		Method[] methods = stackTraceClass.getDeclaredMethods();
		int samename = 0;
		Method toret = null;
		for (Method method : methods) {
			if (method.getName().equals(stackTraceMethodName)) {
				samename++;
				toret = method;
			}
		}
		if (samename == 1)
			return toret;

		// I am only using AtomicReference as a container to dump a String into,
		// feel free to ignore it for now
		final AtomicReference<String> methodDescriptorReference = new AtomicReference<String>();

		String classFileResourceName = "/"
				+ stackTraceClassName.replaceAll("\\.", "/") + ".class";
		InputStream classFileStream = stackTraceClass
				.getResourceAsStream(classFileResourceName);

		if (classFileStream == null) {
			throw new RuntimeException(
					"Could not acquire the class file containing for the calling class");
		}

		try {
			ClassReader classReader = new ClassReader(classFileStream);
			classReader.accept(new EmptyVisitor() {
				@Override
				// asm is general in method or construct, but reflection is not
				public MethodVisitor visitMethod(int access, final String name,
						final String desc, String signature, String[] exceptions) {
					if (!name.equals(stackTraceMethodName)) {
						return null;
					}

					return new EmptyVisitor() {
						@Override
						public void visitLineNumber(int line, Label start) {
							if (line == stackTraceLineNumber) {
								methodDescriptorReference.set(desc);
							}
						}
					};
				}
			}, 0);
		} finally {
			classFileStream.close();
		}

		String methodDescriptor = methodDescriptorReference.get();

		if (methodDescriptor == null) {
			throw new RuntimeException("Could not find line "
					+ stackTraceLineNumber);
		}

		// reflection consier constructs non-methods.
		if (!stackTraceMethodName.equals("<init>")) {
			for (Method method : stackTraceClass.getDeclaredMethods()) {

				if (stackTraceMethodName.equals(method.getName())
						&& methodDescriptor.equals(Type
								.getMethodDescriptor(method))) {
					return method;
				}
			}
		} else {
			// reflection does not treat <init> as methods... we do not conside
			// cinit
			for (Constructor cons : stackTraceClass.getDeclaredConstructors()) {
				if (methodDescriptor
						.equals(Type.getConstructorDescriptor(cons))) {
					return cons;
				}
			}
		}

		throw new RuntimeException("Could not find the calling method");
	}

	// get the first met one..
	public static Object getMethodOrConstructorWithoutLineHint(
			final StackTraceElement stackTraceElement) {
		final String stackTraceClassName = stackTraceElement.getClassName();
		final String stackTraceMethodName = stackTraceElement.getMethodName();

		Class<?> stackTraceClass;
		Object ret = null;
		try {
			stackTraceClass = Class.forName(stackTraceClassName);
			if (!stackTraceMethodName.equals("<init>")) {
				for (Method method : stackTraceClass.getDeclaredMethods()) {
					if (stackTraceMethodName.equals(method.getName())) {
						ret = method;
						break;
					}
				}
			} else {
				for (Constructor cons : stackTraceClass
						.getDeclaredConstructors()) {
					{ // stackTraceMethodName.equals(cons.getName()) <init> and
						// spec.jbb.JBBmain
						ret = cons; // randomly return one
						break;
					}
				}
			}

		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Could not find the calling method");
		}

		return ret;

	}

	public static void main(String[] args) {

	}

	public PreciseSTEContextAnalyzer() {
		Thread.dumpStack();// <init> is used in jdk stackTrace.
	}

}
