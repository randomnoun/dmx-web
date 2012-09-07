package com.randomnoun.dmx.web.action;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URLEncoder;
import java.sql.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.Text;
import com.randomnoun.common.Struct;
import com.randomnoun.common.http.HttpUtil;
import com.randomnoun.common.security.User;
import com.randomnoun.common.spring.StructuredResultReader;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.DeviceDAO;
import com.randomnoun.dmx.dao.DevicePropertyDAO;
import com.randomnoun.dmx.dmxDevice.DmxDevice;
import com.randomnoun.dmx.PropertyDef;
import com.randomnoun.dmx.to.DevicePropertyTO;
import com.randomnoun.dmx.to.DeviceTO;
import com.randomnoun.dmx.web.Table;
import com.randomnoun.dmx.web.TableEditor;
import com.randomnoun.dmx.web.TableEditor.TableEditorResult;

/**
 * Device property maintainance action.
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class MaintainDevicePropertyAction
    extends Action {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(MaintainDevicePropertyAction.class);

	public Map getParameterMap(HttpServletRequest request) {
		Map map = new HashMap();
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String param = (String) e.nextElement();
			map.put(param, request.getParameter(param));
		}
		return map;
	}



    public static class DevicePropertyTableEditor extends TableEditor {

    	private final static String[] fieldNames = 
    		new String[] { "id", "key", "value" };
    	
    	private long deviceId;
    	
    	public DevicePropertyTableEditor(long deviceId) {
    		this.deviceId = deviceId;
    	}
    	
    	@Override
		public void createRow(Map row) throws Exception {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		DevicePropertyDAO devicePropertyDAO = new DevicePropertyDAO(jt);
    		DevicePropertyTO device = new DevicePropertyTO();
    		device.setDeviceId(deviceId);
    		Struct.setFromMap(device, row, false, true, false, fieldNames);
    		devicePropertyDAO.createDeviceProperty(device);
		}

		@Override
		public void updateRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
			DevicePropertyDAO devicePropertyDAO = new DevicePropertyDAO(jt);
    		DevicePropertyTO deviceProperty = new DevicePropertyTO();
    		deviceProperty.setDeviceId(deviceId);
    		Struct.setFromMap(deviceProperty, row, false, true, false, fieldNames);
    		devicePropertyDAO.updateDeviceProperty(deviceProperty);
		}

		@Override
		public void deleteRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
			DevicePropertyDAO devicePropertyDAO = new DevicePropertyDAO(jt);
			DevicePropertyTO deviceProperty = new DevicePropertyTO();
			deviceProperty.setDeviceId(deviceId);
    		Struct.setFromMap(deviceProperty, row, false, true, false, fieldNames);
    		devicePropertyDAO.deleteDeviceProperty(deviceProperty);
		}

		private void init() {
    	}

    	public void removeEmptyRows(Map form) {
    		removeEmptyRows(form, fieldNames, "deviceProperties");
    	}
    	
    	/**
    	 * Validates information passed down from the presentation layer.  Checks for
    	 * mandatory and maximum length.
    	 *
    	 * @param row the row containing information for each stocked item to be maintained
    	 *
    	 * @return true if validation successful or false otherwise
    	 */
    	public boolean validateRow(Map row) {
    	    boolean valid = true;
    	    valid = valid & table.checkMandatory("key", 100, "Key"); // @TODO check against IDs in DB
    	    valid = valid & table.checkMandatory("value", 255, "Value"); // @TODO check against IDs in DB
    	    return valid;
    	}
    	  
    	  
    	public TableEditorResult maintainDeviceProperties(Map request) {
    		TableEditorResult result;
		    List updateErrors;
		    
		    table = new Table(request, "deviceProperties", "id", Long.class);
		    table.setEditableKey(false);
		    init();

		    // table validation and maintenance
    		if (!validateTable() || !maintainTable()) {
		        // updates failed
    			result = getResult();
    			result.setRows(getTable().getRows());
    			result.setErrors(getTable().getErrors());
    		} else {
    			result = getResult();
    			result.setRows(getDeviceProperties());
    			ErrorList errors = getTable().getErrors();
    			errors.addError("Devices updated", "Table has been updated", ErrorList.SEVERITY_OK);
    			result.setErrors(errors);
    			// @TODO probably delay this until all changes have been made
    			long startTime = System.currentTimeMillis();
    			AppConfig.getAppConfig().reloadDevicesFixturesAndShows(true);
	    		logger.info("device reload time=" + ((System.currentTimeMillis() - startTime)/1000.0) + " sec");

    		}
    		return result;
    	}
    	
    	public Map readDeviceProperties(Map request) {
			Map form = new HashMap();
			List deviceProperties = getDeviceProperties();
			form.put("deviceProperties", deviceProperties);
			form.put("deviceProperties_size", deviceProperties.size());
			return form;
    	}
    	
    	public List getDeviceProperties() {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		DeviceDAO deviceDAO = new DeviceDAO(jt);
    		DevicePropertyDAO devicePropertyDAO = new DevicePropertyDAO(jt);
    		//Device device = appConfig.getDevice(deviceId);
    		DeviceTO deviceTO = deviceDAO.getDevice(deviceId);
    		DmxDevice device = null;
    		//List<DevicePropertyTO> properties = new ArrayList<DevicePropertyTO>();
    		try {
				Class clazz = Class.forName(deviceTO.getClassName());
				Constructor con = clazz.getConstructor(Map.class);
				device = (DmxDevice) con.newInstance(new Object[] { null });
			} catch (Exception e) {
				logger.error("Could not construct DmxDevice '" + deviceTO.getClassName() + "'", e);
			}
    		
    		List<DevicePropertyTO> properties = devicePropertyDAO.getDeviceProperties("deviceId=" + deviceId);
    		List propertiesAsMaps = new ArrayList();
    		for (DevicePropertyTO property : properties) {
    			Map devicePropertyMap = new HashMap();
    			Struct.setFromObject(devicePropertyMap, property, false, true, true, fieldNames);
    			propertiesAsMaps.add(devicePropertyMap);
    		}
    		if (device!=null) {
	    		List defaultProperties = device.getDefaultProperties();
	    		for (Iterator i = defaultProperties.iterator(); i.hasNext(); ) {
	    			PropertyDef defaultProperty = (PropertyDef) i.next();
	    			if (Struct.getStructuredListItem(propertiesAsMaps, "key", defaultProperty.getKey())==null) {
	    				Map newProperty = new HashMap();
	    				newProperty.put("key", defaultProperty.getKey());
	    				newProperty.put("value", defaultProperty.getDefaultValue());
	    				newProperty.put("description", defaultProperty.getDescription());
	    				propertiesAsMaps.add(newProperty);
	    			} else {
	    				Map newProperty = Struct.getStructuredListItem(propertiesAsMaps, "key", defaultProperty.getKey());
	    				newProperty.put("description", defaultProperty.getDescription());
	    			}
	    		}
    		}
    		Struct.sortStructuredList(propertiesAsMaps, "key");
    		return propertiesAsMaps;
    	}
    	
   }
      
      
    
    /**
     * Perform this struts action. See the javadoc for this
     * class for more details.
     *
     * @param mapping The struts ActionMapping that triggered this Action
     * @param actionForm An ActionForm (if available) holding user input for this Action
     * @param request The HttpServletRequest for this action
     * @param response The HttpServletResponse for this action
     *
     * @return An ActionForward representing the result to return to the end-user
     *
     * @throws Exception If an exception occurred during action processing
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public ActionForward execute(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response)
        throws Exception 
    {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		AppConfig appConfig = AppConfig.getAppConfig();
		String forward = "success";
		JdbcTemplate jt = appConfig.getJdbcTemplate();
		String action = request.getParameter("action");
		
		long deviceId = Long.parseLong(request.getParameter("deviceId"));
		request.setAttribute("deviceId", new Long(deviceId));
		
		if (action==null) { action = ""; }
		if (action.equals("") || action.equals("editProperties")) {
			// default action displays entry page
			DevicePropertyTableEditor tableEditor = new DevicePropertyTableEditor(deviceId);
			request.setAttribute("form", tableEditor.readDeviceProperties(null));
			
		} else if (action.equals("maintain")) {
			Map form = new HashMap();
			Struct.setFromRequest(form, request);
			
			//System.out.println(Struct.structuredMapToString("form", form));
			DevicePropertyTableEditor tableEditor = new DevicePropertyTableEditor(deviceId);
			tableEditor.removeEmptyRows(form);
			TableEditorResult result = tableEditor.maintainDeviceProperties(form);
			//System.out.println("======================================");
			//System.out.println(Struct.structuredListToString("rows", result.getRows()));
			//System.out.println(Struct.structuredListToString("errors", result.getErrors()));
			form.put("deviceProperties", result.getRows());
			form.put("deviceProperties_size", result.getRows().size());
			request.setAttribute("errors", result.getErrors());
			request.setAttribute("form", form);
			
		} else {
			throw new IllegalArgumentException("Invalid action '" + action + "'");
		}

		
        return mapping.findForward(forward);
    }
    
}
