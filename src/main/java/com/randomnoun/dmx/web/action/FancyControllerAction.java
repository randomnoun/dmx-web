package com.randomnoun.dmx.web.action;

import gnu.io.PortInUseException;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXVersion;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.ExceptionContainer;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidget;
import com.randomnoun.dmx.protocol.dmxUsbPro.UsbProWidgetTranslator;
import com.randomnoun.dmx.show.Show;

/**
 * Manual controller action
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class FancyControllerAction
    extends Action {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(FancyControllerAction.class);

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
		String forward = "json";
    	Map form = new HashMap();
    	Struct.setFromRequest(form, request);
    	Map result = new HashMap();

    	Controller controller = appConfig.getController();
    	String action = request.getParameter("action");
    	String fixtureIdString = request.getParameter("fixtureId");
    	int fixtureId = -1;
    	Fixture fixture = null;
    	FixtureController fixtureController = null;
    	if (!Text.isBlank(fixtureIdString)) { 
    		fixtureId = Integer.parseInt(fixtureIdString);
    		fixture = controller.getFixtures().get(fixtureId);
    		fixtureController = fixture.getFixtureController();
    	}
    	if (action==null) { action = ""; }
    	

    	
    	if (action.equals("")) {
    		List shows = new ArrayList();
    		List fixtures = new ArrayList();
    		Map fixtureDefs = new HashMap();
    		for (Fixture f : controller.getFixtures()) {
    			FixtureDef fd = f.getFixtureDef();
    			String fdName = fd.getClass().getName();
    			Map m = new HashMap();
    			m.put("dmxOffset", f.getStartDmxChannel());
    			m.put("name", f.getName());
    			m.put("type", fdName);
    			fixtures.add(m);
    			if (!fixtureDefs.containsKey(fdName)) {
    				Map m2 = new HashMap();
    				m2.put("dmxChannels", fd.getNumDmxChannels());
    				fixtureDefs.put(fdName, m2);
    			}
    		}
    		for (int i=0; i<appConfig.getShows().size(); i++) {
    			Show s = appConfig.getShows().get(i);
    			Map m = new HashMap();
    			m.put("id", new Long(i));
    			m.put("name", s.getName());
    			shows.add(m);
    		}
    		forward="success";
    		request.setAttribute("fixtures", fixtures);
	    	request.setAttribute("controller", controller);
	    	request.setAttribute("universe", controller.getUniverse());
    		request.setAttribute("fixtureDefs", fixtureDefs);
    		request.setAttribute("shows", shows);
    	
    	} else if (action.equals("poll")) {
    		// just poll the goddamn thing every second or so.
    		String panel = request.getParameter("panel");
    		if (panel.equals("dmx")) {
    			Universe u = appConfig.getController().getUniverse();
    			int[] d = u.getAllDmxChannelValues();
    			String j = "";
    			for (int i=1; i<255; i++) { j += d[i] + ","; }; 
    			j += "";
    			result.put("dmx", j); 
    			
    		} else if (panel.equals("show")) {
    			List<Show> shows = appConfig.getShows();
    			List showResult = new ArrayList();
    			for (int i=0; i<shows.size(); i++) {
    				Show show = shows.get(i);
    				HashMap m = new HashMap();
    				m.put("state", show.getState());
    				m.put("time", show.getShowTime());
    				showResult.add(m);
    			}
    			result.put("shows", showResult);
    		}
    		
    		
    		/*
    		String eventMaskString = request.getParameter("eventMask");
    		String since = Long.parseLong(request.getParameter("since"));
    		List events = appConfig.getEvents(eventMask, since);
    		if (events!=null) {
    			request.setAttribute("json", Struct.structuredListToJson(events));
    		} else {
    			// NB: this could make the server appear unresponsive if this consumes
    			// all the HTTP-handling threads
    			appConfig.getEventMonitor(eventMask).wait(20*1000); // fire off another AJAX request in 20seconds regardless
    			List events = appConfig.getEvents(eventMask, since);
    			request.setAttribute("json", Struct.structuredListToJson(events));
    		}
    		forward="json";
    		*/
    		
    	} else if (action.equals("setDmxValues")) {
    		List dmxValues = (List) form.get("dmx");
    		byte[] dmxData = new byte[512];
	    	for (int i=1; i<=255; i++) {
	    		String value = (String) dmxValues.get(i);
	    		if (!Text.isBlank(value)) {
	    			controller.getUniverse().setDmxChannelValue(i, (int) new Long(value).longValue());
	    		}
	    	}
	    	request.setAttribute("message", "DMX values set");
	    	
    	} else if (action.equals("blackOut")) {
    		if (fixtureId == -1) {
	    		controller.blackOut();
	    		result.put("message", "controller blackOut");
    		} else {
	    		fixtureController.blackOut();
	    		result.put("message", "fixture " + fixtureId + " '" + fixture.getName() + "' blackOut");
    		}
    	} else if (action.equals("setColor")) {
    		int red = Integer.parseInt(request.getParameter("r"));
    		int green = Integer.parseInt(request.getParameter("g"));
    		int blue = Integer.parseInt(request.getParameter("b"));
    		fixtureController.setColor(new Color(red, green, blue));
    		result.put("message", "fixture " + fixtureId + " '" + fixture.getName() + "' color set to " + red + ", " + green + ", " + blue);
    		
    	} else if (action.equals("setPan")) {
    		double pan = Double.parseDouble(request.getParameter("p"));
    		fixtureController.panTo(pan);
    		result.put("message", "fixture " + fixtureId + " '" + fixture.getName() + "' pan set to " + pan);
    		
    	} else if (action.equals("setTilt")) {
    		double tilt = Double.parseDouble(request.getParameter("t"));
    		fixtureController.tiltTo(tilt);
    		result.put("message", "fixture " + fixtureId + " '" + fixture.getName() + "' tilt set to " + tilt);
    		
    	} else if (action.equals("startShow")) {
    		int showId = Integer.parseInt(request.getParameter("showId"));
    		Show show = appConfig.getShows().get(showId);
    		appConfig.startShow(showId);
    		result.put("message", "Show " + showId + " '" + show.getName() + "' started " +
    			(show.getLength()==Long.MAX_VALUE ? " (continuous)" : " (length=" + show.getLength() + "msec)"));
    		
    	} else if (action.equals("cancelShow")) {
    		int showId = -1;
    		if (!Text.isBlank(request.getParameter("showId"))) {
    			showId = Integer.parseInt(request.getParameter("showId"));
    		}
    		if (showId != -1) {
	    		Show show = appConfig.getShows().get(showId);
	    		appConfig.cancelShow(showId);
	    		result.put("message", "Show " + showId + " '" + show.getName() + "' cancel requested");
    		} else {
    			for (Show s : appConfig.getShows()) {
    				appConfig.cancelShow(s.getId());
    			}
    			result.put("message", "Cancel all shows requested");
    		}
    	
    	} else if (action.equals("fixtureBlackout")) {
    		int c=0;
    		String[] fixtureIdStrings = request.getParameter("fixtureIds").split(",");
    		for (String iterationId : fixtureIdStrings) {
    			Fixture f = controller.getFixture(Integer.parseInt(iterationId));
    			f.blackOut();
    			c++;
    		}
    		result.put("message", c + " fixture(s) blacked out");

    	} else if (action.equals("fixtureDim")) {
    		int c=0;
    		String[] fixtureIdStrings = request.getParameter("fixtureIds").split(",");
    		int v = Integer.parseInt(request.getParameter("v"));
    		for (String iterationId : fixtureIdStrings) {
    			Fixture f = controller.getFixture(Integer.parseInt(iterationId));
    			f.getFixtureController().setMasterDimmer(v);
    			c++;
    		}
    		result.put("message", c + " fixture(s) set to " + (100*v/255) + "%");
    		
    		
    	} else if (action.equals("getExceptions")) {
    		List exceptions = new ArrayList();
    		List<ExceptionContainer.TimestampedException> e1 = controller.getAudioController().getExceptions();
    		synchronized(e1) {
    			for (int i=0; i<e1.size(); i++) {
    				ExceptionContainer.TimestampedException te = e1.get(i);
    				Map m = new HashMap(); 
    				m.put("message", te.getException().getMessage());
    				exceptions.add(m);
    			}
    		}
    		e1 = appConfig.getDmxDeviceExceptions();
    		synchronized(e1) {
    			for (int i=0; i<e1.size(); i++) {
    				ExceptionContainer.TimestampedException te = e1.get(i);
    				Map m = new HashMap(); 
    				m.put("message", te.getException().getMessage());
    				exceptions.add(m);
    			}
    		}
    		List<AppConfig.TimestampedShowException> e2 = appConfig.getShowExceptions();
    		synchronized(e2) {
    			for (int i=0; i<e2.size(); i++) {
    				ExceptionContainer.TimestampedException te = e1.get(i);
    				Map m = new HashMap(); 
    				m.put("message", te.getException().getMessage());
    				exceptions.add(m);
    			}
    		}
    		result.put("exceptions", exceptions);
    		
    	} else {
    		
    		throw new IllegalArgumentException("Unknown action '" + action + "'");
    	}
    	
    	
    	request.setAttribute("json", Struct.structuredMapToJson(result));
		return mapping.findForward(forward);
		
    }
}
