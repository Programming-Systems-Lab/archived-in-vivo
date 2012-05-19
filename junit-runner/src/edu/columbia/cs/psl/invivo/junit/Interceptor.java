package edu.columbia.cs.psl.invivo.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.columbia.cs.psl.invivo.junit.annotation.TestCase;
import edu.columbia.cs.psl.invivo.junit.annotation.Tested;
import edu.columbia.cs.psl.invivo.runtime.AbstractInterceptor;
import edu.columbia.cs.psl.invivo.runtime.AbstractLazyCloningInterceptor;
import edu.columbia.cs.psl.invivo.struct.MethodInvocation;

public class Interceptor extends AbstractLazyCloningInterceptor {
	public Interceptor(Object intercepted) {
		super(intercepted);
	}

	private HashMap<Integer, MethodInvocation> invocations = new HashMap<Integer, MethodInvocation>();
	private Logger logger = Logger.getLogger(Interceptor.class);

	private Integer invocationId = 0;

	@Override
	public int onEnter(Object callee, Method method, Object[] params) {
		if (getThreadChildId() != 0)
			return -1;

		int retId = 0;
		TestCase[] cases = method.getAnnotation(Tested.class).cases();
		// Get our invocation id
		synchronized (invocationId) {
			invocationId++;
			retId = invocationId;
		}
		
		// Create a new invocation object to store
		final MethodInvocation inv = new MethodInvocation();
		inv.setParams(params);
		inv.setMethod(method);
		inv.setCallee(callee);

		invocations.put(retId, inv);

		inv.setChildren(new MethodInvocation[cases.length]);
		for(int i = 0; i<cases.length; i++)
		{
			inv.getChildren()[i] = new MethodInvocation();
			inv.getChildren()[i].setParent(inv);

			try {
				Class cl = Class.forName(callee.getClass().getName()+"_InvivoJUnitTests");
				Method testMethod = getMethod("test"+method.getName()+i, cl);
				inv.getChildren()[i].setCallee(cl.newInstance());
				inv.getChildren()[i].setMethod(testMethod);
			} catch (Exception ex) {
				logger.error("Error creating child: " + ex);
			}
			inv.getChildren()[i].setParams(new Object[0]);
			
			inv.getChildren()[i].setThread(createChildThread(inv.getChildren()[i]));
			inv.getChildren()[i].getThread().start();
		}
		
		return retId;
	}

	@Override
	public void onExit(Object val, int op, int id) {
		if (id < 0)
			return;
		try {
			MethodInvocation inv = invocations.remove(id);
			inv.setReturnValue(val);

			for (MethodInvocation i : inv.getChildren()) {
				i.getThread().join();
				logger.info("\tChild" + getChildId(i.getCallee()) + " finished");
			}
			logger.info("Invocation result: " + inv);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
