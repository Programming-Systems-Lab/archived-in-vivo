package edu.columbia.cs.psl.invivo.record.xstream;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;


public class StaticReflectionProvider extends Sun14ReflectionProvider{
	
	public StaticReflectionProvider()
	{
		super(new CatchClassErrorFieldDictionary());
	}
	@Override
	public void visitSerializableFields(Object object, Visitor visitor) {
		for (Iterator iterator = fieldDictionary.fieldsFor(object.getClass()); iterator.hasNext();) {
            Field field = (Field) iterator.next();
            if (!fieldModifiersSupported(field)) {
                continue;
            }
            validateFieldAccess(field);
            try {
                Object value = field.get(object);
                visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);

            } catch (IllegalArgumentException e) {
                throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
            } catch (IllegalAccessException e) {
                throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
            } catch (SecurityException e) {
				e.printStackTrace();
			}
        }
	}
	
	
	@Override
	protected boolean fieldModifiersSupported(Field field) {
		int modifiers = field.getModifiers();
	    return !(Modifier.isTransient(modifiers)); 
	}
}
