package edu.columbia.cs.psl.invivo.nativedetector;

import java.util.LinkedList;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Method;

/**
 * @author miriammelnick
 * A class to contain metadata about a method. Includes an ASM Method, a String className, 
 * an integer for access flags, and a linkedlist of MethodInstances that invoke this method.
 */
public class MethodInstance {
	
	
	@Override
	public String toString() {
		return "MethodInstance [method=" + method + ", clazz=" + clazz + ", access=" + access + ", calledBy=" + calledBy + "]";
	}

	/**
	 * ASM method at the core of this MethodInstance object.
	 * private Method method
	 * @see Method
	 */
	private Method method;
	
	/**
	 * Fully qualified class name of the class owning this method.
	 * private String clazz
	 */
	private String clazz;
	
	/**
	 * Encoded access flags for this method.
	 * private int access
	 * @see Method
	 */
	private int access;
	
	/**
	 * List of MethodInstances that invoke this method.
	 * private LinkedList<MethodInstance> calledBy
	 * @see MethodInstance#addCaller(MethodInstance)
	 * @see MethodInstance#getCallers()
	 * @see NativeDetector#addCaller(MethodInstance, MethodInstance)
	 */
	private LinkedList<MethodInstance> calledBy = new LinkedList<MethodInstance>();
	
	/**
	 * Constructor for MethodInstance - accepts pre-formed method and a class name.
	 * @param method			Method			well-formed {@link Method}
	 * @param clazz				String			fully qualified class name
	 * @see Method
	 * @see MethodInstance#getMethod()
	 * @see MethodInstance#getClazz()
	 */
	public MethodInstance(Method method, String clazz) {
		this.method = method;
		this.clazz = clazz;
	}

	/**
	 * Constructor for MethodInstance - accepts method name, method description and class name.
	 * @param name				String			name of method
	 * @param desc				String			method descriptor
	 * @param clazz				String			fully qualified class name
	 * @see Method#Method(String, String)
	 * @see MethodInstance#getMethod()
	 * @see MethodInstance#getClazz()
	 */
	public MethodInstance(String name, String desc, String clazz) {
		this.method = new Method(name, desc); // TODO should I be using Method.getMethod here?
		this.clazz = clazz;
	}
	
	/**
	 * Constructor for MethodInstance -  accepts method name, method description, class name, and access flag.
	 * @param name				String			name of method
	 * @param desc				String			method descriptor
	 * @param clazz				String			fully qualified class name
	 * @param access			int				access flags in decimal
	 * @see Method#Method(String, String)
	 * @see MethodInstance#getMethod()
	 * @see MethodInstance#getClazz()
	 * @see MethodInstance#getAccess()
	 * @see Opcodes
	 */
	public MethodInstance(String name, String desc, String clazz, int access) {
		this.method = new Method(name, desc);
		this.clazz = clazz;
		this.access = access;
	}
	
	/**
	 * Get the Method underlying this MethodInstance.
	 * @return					Method
	 */
	public Method getMethod() {
		return method;
	}
	/**
	 * Set Method underlying this MethodInstance
	 * @param method			Method			new value for this.value
	 */
	public void setMethod(Method method) {
		this.method = method;
	}
	/**
	 * Get the owner class name.
	 * @return					String
	 */
	public String getClazz() {
		return clazz;
	}
	/**
	 * Set className that owns this MethodInstance.
	 * @param clazz				String			fully qualified class name
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * Attach a new invoker to this method.
	 * @param caller			MethodInstance	method that calls this one
	 * @see NativeDetector#addCaller(MethodInstance, MethodInstance)
	 */
	public void addCaller(MethodInstance caller) {
		this.calledBy.add(caller);
	}
	
	/**
	 * Test whether this MethodInstance is called by caller.
	 * NOTE: this will only report on the information that this MethodInstance has to this point.
	 * It is possible that this caller/callee pair is valid but has not yet been visited.
	 * @param caller			MethodInstance	method that might call this one
	 * @return					boolean			true - yes, false - no
	 */
	public boolean calledBy(MethodInstance caller) {
		if (this.calledBy.contains(caller)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Get list of MethodInstances that invoke this one.
	 * @return					LinkedList<MethodInstance>
	 * @see MethodInstance#calledBy
	 */
	public LinkedList<MethodInstance> getCallers() {
		return this.calledBy;
	}
	
	
	/**
	 * (Override) This function declares two MethodInstances A, B "equal" if and only if:
	 * ((A.getMethod().equals(B.getMethod)) && (A.getClazz().equals(B.getClazz())) == true
	 * @see Object#equals(Object)
	 * @see MethodInstance#hashCode()
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(this.getClass())) {
			MethodInstance other = (MethodInstance) obj;
			if ((other.getClazz().equals(this.getClazz())) && (other.getMethod().getName().equals(this.getMethod().getName()) && other.getMethod().getDescriptor().equals(this.getMethod().getDescriptor())))
				return true;
		}
		return false;
	}
	
	/**
	 * TODO comment hashcode
	 * mention contract
	 */
	@Override
	public int hashCode() {
		return this.getClazz().hashCode() * this.getMethod().getName().hashCode() * this.getMethod().getDescriptor().hashCode();
	}

	/**
	 * TODO comment getAccess
	 * @return				int			access number for this method
	 */
	public int getAccess() {
		return access;
	}

	/**
	 * TODO comment setAccess
	 * @param access		int			new access number for this method
	 */
	public void setAccess(int access) {
		this.access = access;
	}

	public void setCallers(LinkedList<MethodInstance> calledBy) {
		this.calledBy = calledBy;
	}

	
}