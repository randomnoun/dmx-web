package com.randomnoun.dmx.web.action;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;
import com.randomnoun.common.security.User;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.show.Show;
import com.randomnoun.dmx.web.struts.ActionBase;
import com.randomnoun.dmx.web.struts.DmxHttpRequest;

import gnu.io.RXTXCommDriver;
import gnu.io.RXTXVersion;

/**
 * Manual controller action.
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class ControllerAction
    extends ActionBase {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(ControllerAction.class);

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
    public String execute(DmxHttpRequest request, HttpServletResponse response)
        throws Exception
    {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		AppConfig appConfig = AppConfig.getAppConfig();
		String forward = "success";
    	Map form = new HashMap();
    	Struct.setFromRequest(form, request);

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
    	} else if (action.equals("setDmxValues")) {
    		List dmxValues = (List) form.get("dmx");
    		byte[] dmxData = new byte[512];
	    	for (int i=1; i<=255; i++) {
	    		String value = (String) dmxValues.get(i);
	    		if (!Text.isBlank(value)) {
	    			controller.getUniverse(0).setDmxChannelValue(i, (int) new Long(value).longValue());
	    		}
	    	}
	    	request.setAttribute("message", "DMX values set");
    	} else if (action.equals("blackOut")) {
    		if (fixtureId == -1) {
	    		controller.blackOut();
	    		request.setAttribute("message", "controller blackOut");
    		} else {
	    		fixtureController.blackOut();
	    		request.setAttribute("message", "fixture " + fixtureId + " '" + fixture.getName() + "' blackOut");
    		}
    	} else if (action.equals("setColor")) {
    		int red = Integer.parseInt(request.getParameter("r"));
    		int green = Integer.parseInt(request.getParameter("g"));
    		int blue = Integer.parseInt(request.getParameter("b"));
    		fixtureController.setColor(new Color(red, green, blue));
    		request.setAttribute("message", "fixture " + fixtureId + " '" + fixture.getName() + "' color set to " + red + ", " + green + ", " + blue);
    	} else if (action.equals("setPan")) {
    		double pan = Double.parseDouble(request.getParameter("p"));
    		fixtureController.panTo(pan);
    		request.setAttribute("message", "fixture " + fixtureId + " '" + fixture.getName() + "' pan set to " + pan);
    	} else if (action.equals("setTilt")) {
    		double tilt = Double.parseDouble(request.getParameter("t"));
    		request.setAttribute("message", "fixture " + fixtureId + " '" + fixture.getName() + "' tilt set to " + tilt);
    		fixtureController.tiltTo(tilt);
    	} else if (action.equals("startShow")) {
    		int showId = Integer.parseInt(request.getParameter("showId"));
    		Show show = appConfig.getShow(showId);
    		appConfig.startShow(showId);
    		request.setAttribute("message", "Show " + showId + " '" + show.getName() + "' started " +
    			(show.getLength()==Long.MAX_VALUE ? " (continuous)" : " (length=" + show.getLength() + "msec)"));
    	} else if (action.equals("cancelShow")) {
    		int showId = Integer.parseInt(request.getParameter("showId"));
    		Show show = appConfig.getShow(showId);
    		appConfig.cancelShow(showId);
    		request.setAttribute("message", "Show " + showId + " '" + show.getName() + "' cancel requested");
    	}
    	
    	request.setAttribute("shows", appConfig.getShows());
    	request.setAttribute("controller", controller);
    	request.setAttribute("universe", controller.getUniverse(0));
    	
		return "success";
		
    }
}
