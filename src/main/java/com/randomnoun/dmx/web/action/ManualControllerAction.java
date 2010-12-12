package com.randomnoun.dmx.web.action;

import gnu.io.PortInUseException;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXVersion;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.jdbc.core.JdbcTemplate;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;
import com.jacobgen.dmx._USBDMXProCom;
import com.randomnoun.common.ExceptionUtils;
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;
import com.randomnoun.common.db.DatabaseTO;
import com.randomnoun.common.db.DatabaseTO.TableColumnTO;
import com.randomnoun.common.db.DatabaseTO.TableTO;
import com.randomnoun.common.security.User;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidget;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidgetTranslator;

/**
 * Manual controller action. This creates a new 
 * UsbProWidgetTranslator object and uses it directly, and therefore
 * should probably be removed from the application. 
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class ManualControllerAction
    extends Action {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(ManualControllerAction.class);

    /**
     * Perform this struts action. See the javadoc for this
     * class for more details.
     *
     * @param mapping The struts ActionMapping that triggered this Action
     * @param form An ActionForm (if available) holding user input for this Action
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
    	Map form = new HashMap();
    	Struct.setFromRequest(form, request);
    	
    	List dmxValues = (List) form.get("dmx");
    	String startCode = (String) form.get("startCode");
    	String jarVersion = RXTXVersion.getVersion();
    	String dllVersion = "unknown";
    	try {
    		dllVersion = RXTXVersion.nativeGetVersion();
    	} catch (Error e1) {
    		try {
    			dllVersion = RXTXCommDriver.nativeGetVersion();
    		} catch (Exception e2) {
    			logger.error("Exception 1 determining version: ", e1);
    			logger.error("Exception 2 determining version: ", e2);
    			dllVersion = "Exception determining version: " + e1.getMessage();
    		}
    	}
    	request.setAttribute("rxtx.jarVersion", jarVersion);
    	request.setAttribute("rxtx.dllVersion", dllVersion);
    	
    	if (dmxValues!=null) {
    		Map config = new Properties();
    		config.put("portName", "COM4");
	    	UsbProWidget widget = new UsbProWidget(config);
	    	UsbProWidgetTranslator translator;
			try {
				translator = widget.getUsbProWidgetTranslator();
		    	byte startCodeByte = Text.isBlank(startCode) ? 0 : (byte) new Long(startCode).longValue();
		    	byte[] dmxData = new byte[512];
		    	for (int i=0; i<255; i++) {
		    		String value = (String) dmxValues.get(i);
		    		if (!Text.isBlank(value)) {
		    			dmxData[i] = (byte) new Long(value).longValue();
		    		}
		    	}
	    		translator.sendOutputOnlySendDMXPacketRequest(startCodeByte, dmxData);
	    		request.setAttribute("sent4", "OK"); 
	    	} catch (Exception e) {
	    		logger.error(e);
	    		request.setAttribute("sent4", ExceptionUtils.getStackTrace(e));
	    	} finally {
	    		widget.close();
	    	}
    	}
		request.setAttribute("dmx", dmxValues);
		request.setAttribute("startCode", startCode);
		return mapping.findForward("success");
		
    }
}
