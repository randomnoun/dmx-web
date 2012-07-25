package com.randomnoun.dmx.web;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.log4j.Logger;

public class JmxUtils {

	static Logger logger = Logger.getLogger(JmxUtils.class);
	
	/** Hopefully generic JMX functions */
    public static synchronized MBeanServer getMBeanServer() {
        long t1=System.currentTimeMillis();
        MBeanServer server = null;
        if( MBeanServerFactory.findMBeanServer(null).size() > 0 ) {
            server=(MBeanServer)MBeanServerFactory.findMBeanServer(null).get(0);
            if( logger.isDebugEnabled() ) {
            	logger.debug("Using existing MBeanServer " + (System.currentTimeMillis() - t1 ));
            }
        } else {
            server = ManagementFactory.getPlatformMBeanServer();
            if( logger.isDebugEnabled() ) {
            	logger.debug("Creating MBeanServer "+ (System.currentTimeMillis() - t1 ));
            }
        }
        return (server);
    }

	public static String getImpact(int impact) {
		if (impact == MBeanOperationInfo.ACTION) {
			return "ACTION";
		} else if (impact == MBeanOperationInfo.ACTION_INFO) {
			return "ACTION_INFO";
		} else if (impact == MBeanOperationInfo.INFO) {
			return "INFO";
		} else if (impact == MBeanOperationInfo.UNKNOWN) {
			return "UNKNOWN";
		} else {
			throw new IllegalStateException("Unknown impact '" + impact + "'");
		}
	}

	/**
	 * Preloads the ObjectName instances and sorts them into a Map indexed by
	 * domain. Within domains, a further map of types name to object names
	 * are created
	 * 
	 * @param server the JMX server to retrieve objects from
	 *
	 * @return map of mbeans in a structure of domain -> type -> list of objectNames
	 * 
	 * @throws MalforedObjectNameException
	 */
	public static Map getObjectNames( MBeanServer server )
	throws MalformedObjectNameException
	{
	    Map objectNames = new TreeMap();
        Set objectNameSet = server.queryNames( null, null );
        for( Iterator i = objectNameSet.iterator(); i.hasNext(); ) {
		     ObjectName name = (ObjectName) i.next();
		     
		 	 String domain = name.getDomain();
		 	 Map typeNames = (Map) objectNames.get(domain);
		 	 if (typeNames == null) {
			     typeNames = new TreeMap();
			     objectNames.put( domain, typeNames );
			 }
			 // Search the typeNames map to match the type of this object
			 String typeName = name.getKeyProperty("type");
			 if (typeName == null) typeName = "none";
			 List v = (List) typeNames.get(typeName);
			 if (v==null) {
			 	 v = new ArrayList();
				 typeNames.put(typeName, v);
			 }
			 v.add(name);
        }
	    return objectNames;
	}
	
	/** Retrieves information regarding the JMX object passed in. Used to populate the
	 * JMX tree in the debug pane.
	 * 
	 * This object returns a map with the following structure:
	 * <attributes>
	 * className - (String) the className of the object instance
	 * attributes - a Map describing the attributes associated with this JMX MBean. The
	 *   key for the map is the attribute name, and the value is a map with the following structure:
	 *   <attributes>
	 *     description - a text description of the attribute
	 *     type - the class type of the attribute
	 *     value - the current value of the attribute (or "" if the value is null)
	 *   </attributes>
	 * operations - a Map describing the operations that can be performed in this MBean.
	 *   The key for the map is the operation name, and the value is a Map with the following
	 *   structure:
	 *   <attributes>
	 *     impact - the impact of performing this MBean. This returns one of the
	 *       MBeanOperationInfo constants as a String, i.e. "ACTION", "ACTION_INFO", "INFO" or
	 *       "UNKNOWN"
	 *     return type - the class name of the result of this operation
	 *     params - a Map representing the parameters passed into this operation. The key
	 *       of each entry in the map is the parameter name, and the value is another Map
	 *       with the following structure:
	 *       <attributes>
	 *         name - name of the parameter
	 *         type - the class name of the parameter
	 *       </attributes>
	 *     description - a text desription of the operation
	 *     result - is only present for "INFO"-impact operations with no parameters. Contains 
	 *       the result when the operation is invoked. Is set to "(null)" if the result is null.
	 *   </attributes>
	 * </attributes> 
	 * 
	 * @param server The JMX server to retrieve information from
	 * @param objectName The object we are interested in
	 * 
	 * @return a structure as defined above
	 * 
	 * @throws AttributeNotFoundException
	 * @throws IntrospectionException
	 * @throws ReflectionException
	 * @throws InstanceNotFoundException
	 * @throws MBeanException
	 */
	public static Map describeObjectName(MBeanServer server, ObjectName objectName) 
	throws AttributeNotFoundException, IntrospectionException, ReflectionException, InstanceNotFoundException, MBeanException {
		Map result = new HashMap();

		ObjectInstance objectInstance = server.getObjectInstance(objectName);
		result.put("className", objectInstance.getClassName());

		// list attributes
		Map attributes = new HashMap();
		result.put("attributes", attributes);
		MBeanInfo info = server.getMBeanInfo(objectName);
		MBeanAttributeInfo[] attrInfos = info.getAttributes();
		for (int i = 0; i < attrInfos.length; i++) {
			Map attribute = new HashMap();
			
			MBeanAttributeInfo attrInfo = attrInfos[i];
			String name = attrInfo.getName();
			String type = attrInfo.getType();
			String description = attrInfo.getDescription();
			Object value = server.getAttribute(objectName, name);

			// attribute.put("name", name);
			attribute.put("description", description==null ? "" : description);
			attribute.put("type", type);
			attribute.put("value", value==null ? "" : value);
			attributes.put(name, attribute);
			
			// System.out.println(" attr " + name + " (" + type + ") " + value);
			if (description!=null) { logger.debug("      " + description); } 
		}
		
		// list operations
		Map operations = new HashMap();
		result.put("operations", operations);
		MBeanOperationInfo[] operationInfos = info.getOperations();
		for (int i = 0; i < operationInfos.length; i++) {
			Map operation = new HashMap();
			MBeanOperationInfo operationInfo = operationInfos[i];
			operations.put(operationInfo.getName(), operation);
			operation.put("return type", operationInfo.getReturnType());
			// System.out.print(" op " + Text.getLastComponent(operationInfo.getReturnType()) + " " + operationInfo.getName() + "(");
			Map params = new LinkedHashMap();
			operation.put("params", params);
			MBeanParameterInfo[] paramInfos = operationInfo.getSignature();
			for (int j = 0; j < paramInfos.length; j++) {
				MBeanParameterInfo paramInfo = paramInfos[j];
				Map param = new HashMap();
				param.put("name", paramInfo.getName());
				param.put("type", paramInfo.getType());
				params.put(paramInfo.getName(), param);
			}
			operation.put("impact", getImpact(operationInfo.getImpact()));
			operation.put("description", operationInfo.getDescription());
			Object opResult = null;
			if (operationInfo.getImpact() == MBeanOperationInfo.INFO && paramInfos.length == 0) {
				opResult = server.invoke(objectName, operationInfo.getName(), new Object[] {}, new String[] {});
				if (opResult==null) {
					// newList.add("(null)");
					opResult = "(null)";
				} else if (opResult instanceof ObjectName) {
					List newList = new ArrayList(1);
					newList.add(opResult);
					opResult = newList;
				}
				/* if (opResult instanceof String[]) {
					String[] stringArrayOpResult = (String[]) opResult;
					for (int k = 0; k < stringArrayOpResult.length; k++) {
						System.out.println("   " + k + ": " + stringArrayOpResult[k]);
					}
				} else {
					System.out.println("   " + result);
				} */
			}
			if (opResult!=null) { operation.put("result", opResult); } 
		}
		
		return result;
	}
	
	
}
