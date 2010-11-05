package com.randomnoun.dmx.web;

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
import com.randomnoun.dmx.protocol.dmxUsbPro.JavaWidget;
import com.randomnoun.dmx.protocol.dmxUsbPro.JavaWidgetTranslator;


/**
 * dmx test
 * 
 */
public class DmxServlet extends HttpServlet {

	/** Logger for this class */
    public static final Logger logger = Logger.getLogger(DmxServlet.class);

    /** Url to send back to browser */
    public String location;
    
    @Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		location = config.getInitParameter("location");
		if (location == null) { location = "/"; }
	}
    
	/** Post method; just defers to get
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
    
	/** See class documentation
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	Map form = new HashMap();
    	Struct.setFromRequest(form, request);
    	HttpSession session = request.getSession(true);
    	
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
	    	JavaWidget widget = new JavaWidget("COM4");
	    	JavaWidgetTranslator translator;
			try {
				translator = widget.openPort();
		    	
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
		RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
    }
}
