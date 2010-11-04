package com.randomnoun.dmx.web;

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
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;
import com.randomnoun.common.db.DatabaseTO;
import com.randomnoun.common.db.DatabaseTO.TableColumnTO;
import com.randomnoun.common.db.DatabaseTO.TableTO;


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
    	ActiveXComponent axc = new ActiveXComponent("Randomnoun.DMX.USBDMXProCom");
    	_USBDMXProCom usbDMXPro = new _USBDMXProCom(axc);
    	request.setAttribute("dllVersion", usbDMXPro.getDllVersion());
    	
    	usbDMXPro.searchPorts();
    	request.setAttribute("search4", usbDMXPro.getErrorString(4));
    	usbDMXPro.init(4);
    	request.setAttribute("init4", usbDMXPro.getErrorString(4));
    	byte[] universe = new byte[512];
    	for (int i=0; i<255; i++) {
    		String value = (String) dmxValues.get(i);
    		if (!Text.isBlank(value)) {
    			universe[i] = (byte) new Long(value).longValue();
    		}
    	}
    	SafeArray safeArray = new SafeArray(Variant.VariantByte, 512);
		safeArray.fromByteArray(universe);
		usbDMXPro.setDMXValues(4, safeArray);
		usbDMXPro.send(4);
		request.setAttribute("sent4", usbDMXPro.getErrorString(4));
		usbDMXPro.close(4);

		request.setAttribute("dmx", dmxValues);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
		dispatcher.forward(request, response);
    }
}
