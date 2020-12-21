package com.randomnoun.common;

/* (c) 2013 randomnoun. All Rights Reserved. This work is licensed under a
 * BSD Simplified License. (http://www.randomnoun.com/bsd-simplified.html)
 */

import java.io.*;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randomnoun.common.ClassInspector;





/**
 * This class exists to assist when debugging problems with the classloaders
 * and remote method invocations. It allows developers to print debug
 * information about a class. Handy when dealing with reflected objects.
 *
 * @author knoxg
 * 
 */
public class ClassInspector {
    

    /** Produce a string representation of the fields and methods of
     *  a particular class. The output format is similar to a java source file
     *  describing the class.
     *
     * <p>Does not currently show class constructors
     *
     * @param aclass The class to retrieve signature information for
     * @return A string representation of the class signatures.
     */
    public static String getClassSignatures(Class aclass) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter out = new PrintWriter(baos);

        out.print(Modifier.toString(aclass.getModifiers()) + " ");        
        
        // does not show constructors, interfaces, inheritance
        if (aclass.isInterface()) {
            out.print("interface ");
        } else {
            out.print("class ");
        }
        out.print(aclass.getName());
        if (aclass.getSuperclass() != null && aclass.getSuperclass() != Object.class) {
            out.print(" extends " + aclass.getSuperclass().getName());
        }

        Class[] interfaces = aclass.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (i == 0) {
                out.print(" implements");
            }

            out.print(" " + interfaces[i].getName());

            if (i < interfaces.length - 1) {
                out.print(",");
            }
        }

        out.println(" {");
        Field[] fields = aclass.getDeclaredFields();
        Field field;
        Method[] methods = aclass.getDeclaredMethods();
        Method method;
        Constructor[] constructors = aclass.getConstructors();
        Constructor constructor;
        Class[] params;
        Class[] exceptions;

        out.println();
        out.println("  // Constructors");
        for (int i = 0; i < constructors.length; i++) {
            constructor = constructors[i];
            out.print("  " + Modifier.toString(constructor.getModifiers()) + " " + constructor.getName() + "(");
            params = constructor.getParameterTypes();
            for (int j = 0; j < params.length; j++) {
                out.print(shortClassName(params[j].getName()));
                if (j < params.length - 1) {
                    out.print(", ");
                }
            }

            out.print(")");
            exceptions = constructor.getExceptionTypes();
            if (exceptions.length > 0) {
                out.print(" throws ");
                for (int j = 0; j < exceptions.length; j++) {
                    out.print(shortClassName(exceptions[j].getName()));

                    if (j < exceptions.length - 1) {
                        out.print(", ");
                    }
                }
            }
            out.println(";");
        }


        out.println();
        out.println("  // Fields");
        for (int i = 0; i < fields.length; i++) {
            field = fields[i];
            out.println("  " + Modifier.toString(field.getModifiers()) + " " + shortClassName(field.getClass().getName()) + " " + field.getName() + ";");
        }

        out.println();
        out.println("  // Methods");
        for (int i = 0; i < methods.length; i++) {
            method = methods[i];
            out.print("  " + Modifier.toString(method.getModifiers()) + " " + shortClassName(method.getReturnType().getName()) + " " + method.getName() + "(");
            params = method.getParameterTypes();
            for (int j = 0; j < params.length; j++) {
            	out.print(shortClassName(params[j].getName()));
                if (j < params.length - 1) {
                    out.print(", ");
                }
            }

            out.print(")");
            exceptions = method.getExceptionTypes();
            if (exceptions.length > 0) {
                out.print(" throws ");
                for (int j = 0; j < exceptions.length; j++) {
                    out.print(shortClassName(exceptions[j].getName()));
                    if (j < exceptions.length - 1) {
                        out.print(", ");
                    }
                }
            }
            out.println(";");
        }

        out.println("}");
        out.flush();

        return baos.toString();
    }

    /**
     * Returns a list of all constants defined in a class. This method
     * retrieves a list of
     * all public static final fields with a given field name prefix
     * (e.g. "OPERATOR_") and returns a list of the <b>values</b> of these fields. These
     * objects can be of any type.
     *
     * @param constantClass the type-safe enumerated constant class whose values are to be
     * listed.
     * @return a list containing the string representations of those values, as returned by
     * calling <code>toString()</code> on each value.
     */
    public static List getConstants(Class clazz, String prefix) {
        if (clazz == null) {
            throw new NullPointerException("class cannot be null");
        }

        if (prefix == null) {
            throw new NullPointerException("prefix cannot be null");
        }

        List constantValues = new ArrayList();
        Object instance;
        Field[] fields = clazz.getDeclaredFields();
        int modifierMask = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

        for (int i = 0; i < fields.length; ++i) {
            Field field = fields[i];
            String name = field.getName();

            if (name.startsWith(prefix) && !name.equals("_revision") && ((field.getModifiers() & modifierMask) == modifierMask)) {
                try {
                    instance = field.get(null);
                    constantValues.add(instance);
                } catch (IllegalAccessException iae) {
                    // ok, so they're not public final after all...
                    // we can safely ignore them.
                }
            }
        }

        return constantValues;
    }

	/**
	 * Returns a map of all constants defined in a class. This method
	 * retrieves a list of
	 * all public static final fields with a given field name prefix
	 * (e.g. "OPERATOR_") and returns a map containing constant names to constant values. 
	 * Value objects can be of any type.
	 *
	 * @param constantClass the type-safe enumerated constant class whose values are to be
	 * listed.
	 * @return a list containing the string representations of those values, as returned by
	 * calling <code>toString()</code> on each value.
	 */
	public static Map getConstantsMap(Class clazz, String prefix) {
		if (clazz == null) {
			throw new NullPointerException("class cannot be null");
		}
		if (prefix == null) {
			throw new NullPointerException("prefix cannot be null");
		}

		Map constantMap = new HashMap();
		Object instance;
		Field[] fields = clazz.getDeclaredFields();
		int modifierMask = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

		for (int i = 0; i < fields.length; ++i) {
			Field field = fields[i];
			String name = field.getName();

			if (name.startsWith(prefix) && !name.equals("_revision") && ((field.getModifiers() & modifierMask) == modifierMask)) {
				try {
					instance = field.get(null);
					constantMap.put(name, instance);
				} catch (IllegalAccessException iae) {
					// ok, so they're not public final after all...
					// we can safely ignore them.
				}
			}
		}
		return (Map) constantMap;
	}


    /** Returns true if a class exists, false otherwise */
    public static boolean classExists(String className) {
        try {
            Class clazz = Class.forName(className);
            return true;
        } catch (ClassNotFoundException cnfe) {
            return false;
        }
    }
    
    /** Trims the "java.lang." package name from a classname if it is present (so "java.lang.Integer"
     * will be returned as "Integer").
     * 
     * @param className classname to return short version of
     * 
     * @return the short version of the class name
     */
    public static String shortClassName(String className) {
    	if (className.startsWith("[")) {
    		className = getTypeNameString(className);
    	}
    	if (className.startsWith("java.lang.") && className.indexOf(".", 10)==-1) {
    		return className.substring(10);
    	} else {
    		return className;
    	}
    }
    
    /** Convert a java native type signature into something more java-ish 
     * (e.g. "]Ljava.lang.String;" will return "String[]"; "B" will return "byte",
     * that sort of thing). Does not handle argument types.
     * 
     * @param typeName a java native type signature
     * 
     * @return a more java-like representation
     */
    public static String getTypeNameString(String typeName) {
    	String javaType = "";
    	int arrayCount = 0;
    	if (typeName==null) { throw new NullPointerException("null typeName"); }
    	while (typeName.startsWith("[")) {
    		arrayCount++; typeName = typeName.substring(1);
    	}
    	if (typeName.equals("Z")) { javaType = "boolean"; } 
		else if (typeName.equals("B")) { javaType = "byte"; }
		else if (typeName.equals("C")) { javaType = "char"; }
		else if (typeName.equals("S")) { javaType = "short"; }
		else if (typeName.equals("I")) { javaType = "int"; }
		else if (typeName.equals("F")) { javaType = "long"; }
		else if (typeName.equals("D")) { javaType = "float"; }
		else if (typeName.equals("B")) { javaType = "double"; }
		else if (typeName.startsWith("L")) {
			if (!typeName.endsWith(";")) {
				throw new IllegalArgumentException("Illegal typeName '" + typeName + "' (expected trailing ';')");
			}
			javaType = typeName.substring(1, typeName.length()-1); 
		}
		for (int i=0; i<arrayCount; i++) {
			javaType += "[]";
		}
		return javaType;
    }
    
    
    public static void main (String args[]) {
        System.out.println(getClassSignatures(ClassInspector.class));
    } 
    
}
