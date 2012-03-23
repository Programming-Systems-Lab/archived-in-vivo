package edu.columbia.cs.psl.invivo.junit;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.columbia.cs.psl.invivo.runtime.AbstractInterceptor;
import edu.columbia.cs.psl.invivo.struct.MethodInvocation;

public class Interceptor extends AbstractInterceptor{
	public Interceptor(Object intercepted) {
		super(intercepted);
	}
	private HashMap<Integer, MethodInvocation> invocations = new HashMap<Integer, MethodInvocation>();
	private Logger logger = Logger.getLogger(Interceptor.class);

	private Integer invocationId = 0;

	@Override
	public int onEnter(Object callee, Method method, Object[] params) {
		if(isChild(callee))
			return -1;
		
		int retId = 0;
		//Get our invocation id
		synchronized(invocationId)
		{
			invocationId++;
			retId = invocationId;	
		}
		//Create a new invocation object to store
		final MethodInvocation inv = new MethodInvocation();
		inv.params = params;
		inv.method = method;
		inv.callee = callee;
		
		Class<?>[] childTestParamTypes = new Class[params.length ];
		
		Object[] childParams = new Object[params.length];
		for(int i = 0; i< params.length; i++)
		{
			childTestParamTypes[i] = params[i].getClass();
			childParams[i] = params[i];
		}
		
		
		invocations.put(retId, inv);
		
		inv.children = new MethodInvocation[1];
		
		//Arbitrarily decide to create one child invocation, which just does the same thing
		inv.children[0] = new MethodInvocation();
		inv.children[0].parent = inv;
		inv.children[0].callee = deepClone(inv.callee);
		inv.children[0].method = inv.method;
		
		inv.children[0].params = deepClone(childParams);
			
		inv.children[0].thread= createChildThread(inv.children[0]);
		inv.children[0].thread.start();
		
		return retId;
	}

	@Override
	public void onExit(Object val, int op, int id) {
		if(id < 0)
			return;
		try
		{
		MethodInvocation inv = invocations.remove(id);
		inv.returnValue = val;
		Object[] checkParams = new Object[inv.params.length + 2];
		
		for(MethodInvocation i : inv.children)
		{
			i.thread.join();
			logger.info("\tChild"+getChildId(i.callee) +" finished");
		}
		logger.info("Invocation result: " + inv);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
