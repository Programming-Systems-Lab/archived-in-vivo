package edu.columbia.cs.psl.invivo.record.xstream;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;

public class StaticReflectionProvider extends Sun14ReflectionProvider{
	@Override
	protected boolean fieldModifiersSupported(Field field) {
		int modifiers = field.getModifiers();
	    return !(Modifier.isTransient(modifiers)); 
	}
}
