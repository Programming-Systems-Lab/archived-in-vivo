package edu.columbia.cs.psl.invivo.record.xstream;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;

public class StaticReflectionProvider extends Sun14ReflectionProvider{
	
	public StaticReflectionProvider()
	{
		super(new CatchClassErrorFieldDictionary());
	}
	@Override
	public void visitSerializableFields(Object arg0, Visitor arg1) {
		System.out.println(arg0);
		super.visitSerializableFields(arg0, arg1);

	}
	
	
	@Override
	protected boolean fieldModifiersSupported(Field field) {
		int modifiers = field.getModifiers();
	    return !(Modifier.isTransient(modifiers)); 
	}
}
