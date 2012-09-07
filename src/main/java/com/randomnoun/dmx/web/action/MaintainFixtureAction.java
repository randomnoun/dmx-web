package com.randomnoun.dmx.web.action;

import java.io.*;
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
import com.randomnoun.common.jexl.ExpressionUtils;
import com.randomnoun.common.jexl.ast.TopLevelExpression;
import com.randomnoun.common.jexl.eval.EvalContext;
import com.randomnoun.common.jexl.eval.EvalException;
import com.randomnoun.common.jexl.eval.EvalFunction;
import com.randomnoun.common.jexl.eval.Evaluator;
import com.randomnoun.common.jexl.parser.ExpressionParser;
import com.randomnoun.common.jexl.parser.ParseException;
import com.randomnoun.common.jexl.parser.TokenMgrError;
import com.randomnoun.common.security.User;
import com.randomnoun.common.spring.StructuredResultReader;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.FixtureDAO;
import com.randomnoun.dmx.dao.FixtureDefDAO;
import com.randomnoun.dmx.show.ShowUtils;
import com.randomnoun.dmx.to.FixtureDefTO;
import com.randomnoun.dmx.to.FixtureTO;
import com.randomnoun.dmx.web.Table;
import com.randomnoun.dmx.web.TableEditor;
import com.randomnoun.dmx.web.TableEditor.TableEditorResult;


/**
 * Fixture entry action.
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 * 
 * @TODO allow stage to be selected in interface
 *
 * @version         $Id$
 * @author          knoxg
 */
public class MaintainFixtureAction
    extends Action {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(MaintainFixtureAction.class);

	public Map getParameterMap(HttpServletRequest request) {
		Map map = new HashMap();
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String param = (String) e.nextElement();
			map.put(param, request.getParameter(param));
		}
		return map;
	}



    public static class FixtureTableEditor extends TableEditor {

    	private final static String[] fieldNames = 
    		new String[] { "id", "fixtureDefId", "name", "universeNumber", "dmxOffset", "sortOrder", "x", "y", "z",
    		"lookingAtX", "lookingAtY", "lookingAtZ", "upX", "upY", "upZ", "fixPanelType", "fixPanelX", "fixPanelY" };
    	
    	// for row removal; universe number and panel type is prepopulated 
    	private final static String[] fieldNames2 = 
    		new String[] { "id", "fixtureDefId", "name", "dmxOffset", "sortOrder", "x", "y", "z",
    		"lookingAtX", "lookingAtY", "lookingAtZ", "upX", "upY", "upZ", "fixPanelX", "fixPanelY" };
        	
    	
    	Map fixtureDefMap = null;
    	int[] occupiedDmxValues = new int[Universe.MAX_CHANNELS];
    	long activeStageId = -1;
    			
    	public FixtureTableEditor(long activeStageId) {
    		this.activeStageId = activeStageId;
    	}
    	
    	@Override
		public void createRow(Map row) throws Exception {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		FixtureDAO fixtureDAO = new FixtureDAO(jt);
    		FixtureTO fixture = new FixtureTO();
    		Struct.setFromMap(fixture, row, false, true, false, fieldNames);
    		fixture.setStageId(activeStageId);
    		fixtureDAO.createFixture(fixture);
		}

		@Override
		public void updateRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
    		FixtureDAO fixtureDAO = new FixtureDAO(jt);
    		FixtureTO fixture = new FixtureTO();
    		Struct.setFromMap(fixture, row, false, true, false, fieldNames);
    		fixture.setStageId(activeStageId);
    		fixtureDAO.updateFixture(fixture);
		}

		@Override
		public void deleteRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
    		FixtureDAO fixtureDAO = new FixtureDAO(jt);
    		FixtureTO fixture = new FixtureTO();
    		Struct.setFromMap(fixture, row, false, true, false, fieldNames);
    		fixtureDAO.deleteFixture(fixture);
		}

		private void init() {
			this.fixtureDefMap = getFixtureDefsMap();
			for (int i=0; i<Universe.MAX_CHANNELS; i++) {
				occupiedDmxValues[i]=-1;
			}
    	}

    	public void removeEmptyRows(Map form) {
    		removeEmptyRows(form, fieldNames2, "fixtures");
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
    	    valid = valid & table.checkMandatory("fixtureDefId", 10, "Fixture type"); // @TODO check against IDs in DB
    	    valid = valid & table.checkMandatory("name", 100, "Name");
    	    valid = valid & table.checkMandatory("universeNumber", 10, "universeNumber");
    	    valid = valid & table.checkNumeric("universeNumber", "universeNumber");
    	    valid = valid & table.checkMandatory("dmxOffset", 3, "DMX offset");
    	    valid = valid & table.checkNumeric("dmxOffset", "DMX offset");
    	    valid = valid & table.checkNumeric("sortOrder", "Sort order");
    	    valid = valid & table.checkNumeric("fixPanelX", "Fixture panel X position");
    	    valid = valid & table.checkNumeric("fixPanelY", "Fixture panel Y position");
    	    valid = valid & table.checkFloat("x", "X Position");
    	    valid = valid & table.checkFloat("y", "Y Position");
    	    valid = valid & table.checkFloat("z", "Z Position");
    	    valid = valid & table.checkFloat("lookingAtX", "Looking at X Position");
    	    valid = valid & table.checkFloat("lookingAtY", "Looking at Y Position");
    	    valid = valid & table.checkFloat("lookingAtZ", "Looking at Z Position");
    	    valid = valid & table.checkFloat("upX", "Up X Vector");
    	    valid = valid & table.checkFloat("upY", "Up Y Vector");
    	    valid = valid & table.checkFloat("upZ", "Up Z Vector");
    	    valid = valid & table.checkMandatoryInclusive(
    	    	new String[] { "x", "y", "z" },
    	    	new String[] { "X Position", "Y Position", "Z Position" });
    	    valid = valid & table.checkMandatoryInclusive(
    	    	new String[] { "lookingAtX", "lookingAtY", "lookingAtZ" },
    	    	new String[] { "Looking at X Position", "Looking at Y Position", "Looking at Z Position" });
    	    valid = valid & table.checkMandatoryInclusive(
    	    	new String[] { "upX", "upY", "upZ" },
    	    	new String[] { "Up X Vector", "Up Y Vector", "Up Z Vector" });
    	    valid = valid & table.checkMandatoryInclusive(
    	    	new String[] { "fixPanelX", "fixPanelY" },
    	    	new String[] { "Fixture panel X position", "Fixture panel Y position" });
    	    if (valid) {
    	    	// @TODO sanity checks on fixture panel location
    	    	
	    	    long fixtureDefId = Long.parseLong(table.getRowValue("fixtureDefId"));
	    	    long dmxOffset = Long.parseLong(table.getRowValue("dmxOffset"));
	    	    long universeNumber = Long.parseLong(table.getRowValue("universeNumber"));
	    	    long numChannels = ((Long) ((Map) fixtureDefMap.get(fixtureDefId)).get("dmxChannels")).longValue();
	    	    
	    	    // @TODO do this across all universes
	    	    if (universeNumber==1) {
		    	    for (int i=1; i<=numChannels; i++) {
		    	    	int otherFixtureId = occupiedDmxValues[(int) dmxOffset+i-1];
		    	    	if (otherFixtureId==-1) {
		    	    		occupiedDmxValues[(int) dmxOffset+i-1]=table.getCurrentRow();
		    	    	} else {
		    	    		table.getErrors().addError(
		    	    			"fixtures[" + table.getCurrentRow() + "].dmxOffset,fixtures[" + otherFixtureId + "].dmxOffset", 
		    	    		   "DMX conflict", "The fixtures '" + table.getRowValue("name") + "' and '" + 
		    	    		   ((Map)table.getRows().get(otherFixtureId)).get("name") + 
		    	    		   "' overlap on some DMX channels", 
		    	    		   ErrorList.SEVERITY_INVALID);
		    	    		valid = false;
		    	    		break;
		    	    	}
		    	    }
	    	    }
    	    }
    	    if (valid && !Text.isBlank(table.getRowValue("x")) && 
    	    		!Text.isBlank(table.getRowValue("lookingAtX")) &&
    	    		!Text.isBlank(table.getRowValue("upX")) ) 
    	    {
    			// a 2D plane with the points (x,y,z) with normal vector upX, upY, upZ
    			//
    			// upX(x-initialX) + upY(y-initialY) + upZ(z-initialZ) = 0
    			//
    			// should validate that the lookingAtX, lookingAtY and lookingAtZ lie on 
    			// this plane
    	    	
	    	    double initialX = Double.parseDouble(table.getRowValue("x"));
	    	    double initialY = Double.parseDouble(table.getRowValue("y"));
	    	    double initialZ = Double.parseDouble(table.getRowValue("z"));
	    	    double lookingAtX = Double.parseDouble(table.getRowValue("lookingAtX"));
	    	    double lookingAtY = Double.parseDouble(table.getRowValue("lookingAtY"));
	    	    double lookingAtZ = Double.parseDouble(table.getRowValue("lookingAtZ"));
	    	    double upX = Double.parseDouble(table.getRowValue("upX"));
	    	    double upY = Double.parseDouble(table.getRowValue("upY"));
	    	    double upZ = Double.parseDouble(table.getRowValue("upZ"));
	    	    
	    	    double check = upX*(lookingAtX-initialX) +
	    	      upY*(lookingAtY-initialY) +
	    	      upZ*(lookingAtZ-initialZ);
	    	    /*
	    	    if (Math.abs(check)>0.001) { // arbitrary error threshold
    	    		table.getErrors().addError(
    	    			"fixtures[" + table.getCurrentRow() + "].lookingAtX," +
    	    			"fixtures[" + table.getCurrentRow() + "].lookingAtY," +
    	    			"fixtures[" + table.getCurrentRow() + "].lookingAtZ", 
    	    		   "Geometry error", "The looking at point " +
    	    		   "(" + lookingAtX + ", " + lookingAtY + ", " + lookingAtZ + ") does " +
    	    		   "not lie on the plane located at (" + initialX + ", " + initialY + ", " + initialZ + ") " +
    	    		   " with normal vector (" + upX + ", " + upY + ", " + upZ + ")", 
    	    		   ErrorList.SEVERITY_INVALID);
	    	    }
	    	    */
	    	    // @TODO convert upX, upY, upZ to unit length
    	    }
    	    
    	    
    	    return valid;
    	}
    	  
    	  
    	public TableEditorResult maintainFixtures(Map request) {
    		TableEditorResult result;
		    List updateErrors;
		    
		    table = new Table(request, "fixtures", "id", String.class);
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
    			result.setRows(getFixtures());
    			ErrorList errors = getTable().getErrors();
    			errors.addError("Fixtures updated", "Table has been updated", ErrorList.SEVERITY_OK);
    			result.setErrors(errors);
    			
	    		long startTime = System.currentTimeMillis();
				AppConfig.getAppConfig().reloadDevicesFixturesAndShows(false);
	    		logger.info("fixture reload time=" + ((System.currentTimeMillis() - startTime)/1000.0) + " sec");
    		}
    		return result;
    	}
    	
    	public Map readFixtures(Map request) {
			Map form = new HashMap();
			List fixtures = getFixtures();
			form.put("fixtureDefs", getFixtureDefs());
			form.put("fixtureDefMap", getFixtureDefsMap());
			form.put("fixtures", fixtures);
			form.put("fixtures_size", fixtures.size());
			form.put("fixPanelTypes", getFixPanelTypes());
			setLastFreeOffset(form, (Map) form.get("fixtureDefMap"), (List) form.get("fixtures"));
			return form;
    	}

    	/** Sets the lastFreeUniverse and lastFreeOffset request attributes */ 
	    public void setLastFreeOffset(Map form, Map fixtureDefMap, List fixtures) {
		    long lastUniverse=1, lastOffset=0;
		    for (int i=0; i<fixtures.size(); i++) {
			    long thisFixtureDefId = ((Number) ((Map)fixtures.get(i)).get("fixtureDefId")).longValue();
			    long thisUniverse = ((Number) ((Map)fixtures.get(i)).get("universeNumber")).longValue();
			    long thisOffset = ((Number) ((Map)fixtures.get(i)).get("dmxOffset")).longValue();
			    long thisDmxChannels = ((Number) ((Map)fixtureDefMap.get(thisFixtureDefId)).get("dmxChannels")).longValue();
			    if (thisUniverse>lastUniverse) {
				    lastUniverse=thisUniverse;
				    lastOffset=thisOffset + thisDmxChannels - 1;
			    } else if (thisUniverse==lastUniverse) {
				    lastOffset=Math.max(lastOffset, thisOffset + thisDmxChannels - 1);
			    }
		    }
		    lastOffset = lastOffset + 1;
		    if (lastOffset==513) { lastOffset=0; lastUniverse++; }
		    form.put("lastFreeUniverse", lastUniverse);
		    form.put("lastFreeOffset", lastOffset);
	    }

    	public List getFixtures() {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		FixtureDAO fixtureDAO = new FixtureDAO(jt);
    		List<FixtureTO> fixtures = fixtureDAO.getFixtures("stageId=" + activeStageId);
    		// holy freaking christ. This is 12 types of wrong. Or 1:02AM types of wrong. Take your pick.
    		List fixturesAsMaps = new ArrayList();
    		for (FixtureTO fixture : fixtures) {
    			Map fixtureMap = new HashMap();
    			Struct.setFromObject(fixtureMap, fixture, false, true, true, fieldNames);
    			fixturesAsMaps.add(fixtureMap);
    		}
    		return fixturesAsMaps;
    	}
    	
    	public List getFixtureDefs() {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		FixtureDefDAO fixtureDefDAO = new FixtureDefDAO(jt);
    		return fixtureDefDAO.getFixtureDefs(null);
    	}
    	
    	public Map getFixtureDefsMap() {
    		// @XXX: this hits the FixtureDefDAO twice for no particular reason
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		FixtureDefDAO fixtureDefDAO = new FixtureDefDAO(jt);
    		List<FixtureDefTO> fixtureDefs = fixtureDefDAO.getFixtureDefs(null);
    		Map fixtureDefsMap = new HashMap();
    		for (FixtureDefTO fixtureDef : fixtureDefs) {
    			Map fixtureDefMap = new HashMap();
    			fixtureDefMap.put("dmxChannels", new Long(fixtureDef.getDmxChannels()));
    			fixtureDefMap.put("htmlImg16", fixtureDef.getHtmlImg16());
    			fixtureDefsMap.put(new Long(fixtureDef.getId()), fixtureDefMap);
    			// fixtureDefs[f.type]["img16"]
    		}
    		return fixtureDefsMap;
    	}
    	
    	public List getFixPanelTypes() {
    		ArrayList fixPanelTypes = new ArrayList();
    		addElement(fixPanelTypes, "L", "Large (default)");
    		addElement(fixPanelTypes, "S", "Small (half-size)");
    		addElement(fixPanelTypes, "M", "Matrix (5x5 px)");
    		return fixPanelTypes;
    	}
    	
    	public void addElement(List list, String key, String value) {
    		Map m = new HashMap();
    		m.put("id", key);
    		m.put("name", value);
    		list.add(m);
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
		FixtureDAO fixtureDAO = new FixtureDAO(jt);
		String action = request.getParameter("action");
		
		if (appConfig.getActiveStage()==null) {
			request.setAttribute("initMessage", "You must create a stage before adding fixtures");
			forward = "cnfPanel";
		} else {
			long activeStageId = appConfig.getActiveStage().getId();
			
			if (action==null) { action = ""; }
			if (action.equals("")) {
				// default action displays entry page
				FixtureTableEditor tableEditor = new FixtureTableEditor(activeStageId);
				request.setAttribute("form", tableEditor.readFixtures(null));
				
			} else if (action.equals("maintain")) {
				Map form = new HashMap();
				Struct.setFromRequest(form, request);
				
				//System.out.println(Struct.structuredMapToString("form", form));
				FixtureTableEditor tableEditor = new FixtureTableEditor(activeStageId);
				tableEditor.removeEmptyRows(form);
				TableEditorResult result = tableEditor.maintainFixtures(form);
				//System.out.println("======================================");
				//System.out.println(Struct.structuredListToString("rows", result.getRows()));
				//System.out.println(Struct.structuredListToString("errors", result.getErrors()));
				form.put("fixtureDefs", tableEditor.getFixtureDefs());
				form.put("fixtureDefMap", tableEditor.getFixtureDefsMap());
				form.put("fixPanelTypes", tableEditor.getFixPanelTypes());
				form.put("fixtures", result.getRows());
				form.put("fixtures_size", result.getRows().size());
				tableEditor.setLastFreeOffset(form, (Map) form.get("fixtureDefMap"), (List) form.get("fixtures"));
				request.setAttribute("errors", result.getErrors());
				request.setAttribute("form", form);
			
			} else if (action.equals("clearAll")) {
				fixtureDAO.deleteFixtures("stageId=" + activeStageId);
				FixtureTableEditor tableEditor = new FixtureTableEditor(activeStageId);
				request.setAttribute("form", tableEditor.readFixtures(null));
				ErrorList errors = new ErrorList();
    			errors.addError("Fixtures cleared", "All fixtures have been removed from the active stage", ErrorList.SEVERITY_OK);
    			request.setAttribute("errors", errors);
				
			} else if (action.equals("rfPreview") || action.equals("rfRepeatFixtures")) {
				Map<String, String> form = new HashMap();
				Struct.setFromRequest(form, request, new String[] {
				 "rfFixtureDefId", "rfCountX", "rfCountY", "rfName", "rfUniverseStart",
				 "rfDmxOffsetStart", "rfDmxOffsetGap", "rfDmxAllocation",
				 "rfDmxOffsetCalc", "rfDmxUniverseCalc",
				 "rfPanelX", "rfPanelY",
				 "rfPositionX","rfPositionY","rfPositionZ", 
				 "rfLookingAtX","rfLookingAtY","rfLookingAtZ", 
				 "rfUpX","rfUpY","rfUpZ" });
				
				// @TODO CSV upload if that's what they're doing
				long fixtureDefId = Long.parseLong(form.get("rfFixtureDefId"));
				long cx = Long.parseLong(form.get("rfCountX"));
				long cy = Long.parseLong(form.get("rfCountY"));
				// hmm.
				// long u = Long.parseLong(form.get("rfUniverseNumber"));
				//long dmxOffset = Long.parseLong(form.get("rfDmxOffset"));
				//long dmxOffsetGap = Long.parseLong(form.get("rfDmxOffsetGap"));
				TopLevelExpression dmxOffsetExpr = parseExpression(form.get("rfDmxOffsetCalc"));
				TopLevelExpression dmxUniverseExpr = parseExpression(form.get("rfDmxUniverseCalc"));
				TopLevelExpression panelXExpr = parseExpression(form.get("rfPanelX"));
				TopLevelExpression panelYExpr = parseExpression(form.get("rfPanelY"));
				TopLevelExpression positionXExpr = parseExpression(form.get("rfPositionX"));
				TopLevelExpression positionYExpr = parseExpression(form.get("rfPositionY"));
				TopLevelExpression positionZExpr = parseExpression(form.get("rfPositionZ"));
				TopLevelExpression lookingAtXExpr = parseExpression(form.get("rfLookingAtX"));
				TopLevelExpression lookingAtYExpr = parseExpression(form.get("rfLookingAtY"));
				TopLevelExpression lookingAtZExpr = parseExpression(form.get("rfLookingAtZ"));
				TopLevelExpression upXExpr = parseExpression(form.get("rfUpX"));
				TopLevelExpression upYExpr = parseExpression(form.get("rfUpY"));
				TopLevelExpression upZExpr = parseExpression(form.get("rfUpZ"));
				
				// @TODO everything else in Math.*
				Map functions = new HashMap();
				functions.put("floor", new EvalFunction(){
					public Object evaluate(String functionName, EvalContext context, List arguments)
						throws EvalException {
						if (arguments.size() != 1) { throw new EvalException(functionName + "() must contain one parameter"); }
			            if (arguments.get(0) == null) { throw new EvalException(functionName + "() parameter cannot be null"); }
			            if (!(arguments.get(0) instanceof Number)) {
			                throw new EvalException(functionName + "() parameter must be a numeric type");
			            }
			            return Math.floor( ((Number)arguments.get(0)).doubleValue() );				
			        }});
				functions.put("iif", new EvalFunction(){
					public Object evaluate(String functionName, EvalContext context, List arguments)
						throws EvalException {
						if (arguments.size() != 3) { throw new EvalException(functionName + "() must contain three parameters"); }
			            if (arguments.get(0) == null) { throw new EvalException(functionName + "() parameter 1 cannot be null"); }
			            if (!(arguments.get(0) instanceof Boolean)) {
			                throw new EvalException(functionName + "() parameter must be a boolean type");
			            }
			            boolean b = ((Boolean)arguments.get(0)).booleanValue();
			            return b ? arguments.get(1) : arguments.get(2);
			        }});
				functions.put("fillDmxOffset", new EvalFunction(){
					public Object evaluate(String functionName, EvalContext context, List arguments)
						throws EvalException {
						if (arguments.size() != 4) { throw new EvalException(functionName + "() must contain four parameters"); }
						for (int i=0; i<3; i++) { if (arguments.get(i) == null) { throw new EvalException(functionName + "() parameter " + (i+1) + " cannot be null"); } }
						for (int i=0; i<3; i++) { if (!(arguments.get(i) instanceof Number)) { throw new EvalException(functionName + "() parameter " + (i+1) + " must be numeric"); } }
						int startUniverse = ((Number) arguments.get(0)).intValue();
						int startOffset = ((Number) arguments.get(1)).intValue();
						int numChannels = ((Number) arguments.get(2)).intValue();
						int n = ((Number) arguments.get(3)).intValue();
			            return ShowUtils.fillDmxOffset(startUniverse, startOffset, numChannels, n);
			        }});
				functions.put("fillDmxUniverse", new EvalFunction(){
					public Object evaluate(String functionName, EvalContext context, List arguments)
						throws EvalException {
						if (arguments.size() != 4) { throw new EvalException(functionName + "() must contain four parameters"); }
						for (int i=0; i<3; i++) { if (arguments.get(i) == null) { throw new EvalException(functionName + "() parameter " + (i+1) + " cannot be null"); } }
						for (int i=0; i<3; i++) { if (!(arguments.get(i) instanceof Number)) { throw new EvalException(functionName + "() parameter " + (i+1) + " must be numeric"); } }
						int startUniverse = ((Number) arguments.get(0)).intValue();
						int startOffset = ((Number) arguments.get(1)).intValue();
						int numChannels = ((Number) arguments.get(2)).intValue();
						int n = ((Number) arguments.get(3)).intValue();
			            return ShowUtils.fillDmxUniverse(startUniverse, startOffset, numChannels, n);
			        }});
				
				List rows = new ArrayList();
				
				int n=0;
				for (int y=0; y<cy; y++) {
					List row = new ArrayList();
					rows.add(row);
					for (int x=0; x<cx; x++) {
						Map cell = new HashMap();
						row.add(cell);
						cell.put("x", x); cell.put("y", y);
						String name = form.get("rfName");
						name = Text.replaceString(name, "{x}", String.valueOf(x));
						name = Text.replaceString(name, "{y}", String.valueOf(y));
						name = Text.replaceString(name, "{n}", String.valueOf(n));
						cell.put("name", name);
						//cell.put("offset", "u" + u + "-offset" + dmxOffset);
						
						// @TODO allow side-affects here ?
						Long dmxUniverse=evalLong(dmxUniverseExpr, functions, x, y, n);
						Long dmxOffset=evalLong(dmxOffsetExpr, functions, x, y, n);
						Long panelX=evalLong(panelXExpr, functions, x, y, n);
						Long panelY=evalLong(panelYExpr, functions, x, y, n);
						Long positionX=evalLong(positionXExpr, functions, x, y, n);
						Long positionY=evalLong(positionYExpr, functions, x, y, n);
						Long positionZ=evalLong(positionZExpr, functions, x, y, n);
						Long lookingAtX=evalLong(lookingAtXExpr, functions, x, y, n);
						Long lookingAtY=evalLong(lookingAtYExpr, functions, x, y, n);
						Long lookingAtZ=evalLong(lookingAtZExpr, functions, x, y, n);
						Long upX=evalLong(upXExpr, functions, x, y, n);
						Long upY=evalLong(upYExpr, functions, x, y, n);
						Long upZ=evalLong(upZExpr, functions, x, y, n);
				        
				        if (dmxUniverse!=null && dmxOffset!=null) { cell.put("offset", "u" + dmxUniverse.intValue() + "-offset" + dmxOffset.intValue()); };
				        if (panelX!=null && panelY!=null) { cell.put("panel", "(" + panelX + ", " + panelY + ")"); }
				        if (positionX!=null && positionY!=null && positionZ!=null) { cell.put("position", "(" + positionX + ", " + positionY + ", " + positionZ + ")"); }
				        if (lookingAtX!=null && lookingAtY!=null && lookingAtZ!=null) { cell.put("lookingAt", "(" + lookingAtX + ", " + lookingAtY + ", " + lookingAtZ + ")"); }
				        if (upX!=null && upY!=null && upZ!=null) { cell.put("up", "(" + upX + ", " + upY + ", " + upZ + ")"); }
				        
				        //dmxOffset += dmxOffsetGap + 3; /* @TODO get fixture channel count */
				        n++;
				        
				        if (action.equals("rfRepeatFixtures")) {
				        	// @TODO ensure these things are set first
				        	FixtureTO fixture = new FixtureTO();
				        	fixture.setFixtureDefId(fixtureDefId);
				        	fixture.setName(name);
				        	fixture.setStageId(activeStageId);
				        	fixture.setFixPanelType("M"); // matrix
				        	fixture.setDmxOffset(dmxOffset.longValue());
				        	fixture.setUniverseNumber(dmxUniverse.longValue());
				        	fixture.setFixPanelX(panelX.longValue());
				        	fixture.setFixPanelY(panelY.longValue());
				        	fixture.setX(positionX.longValue());
				        	fixture.setY(positionY.longValue());
				        	fixture.setZ(positionZ.longValue());
				        	fixture.setLookingAtX(lookingAtX.longValue());
				        	fixture.setLookingAtY(lookingAtY.longValue());
				        	fixture.setLookingAtZ(lookingAtZ.longValue());
				        	fixture.setUpX(upX.longValue());
				        	fixture.setUpY(upY.longValue());
				        	fixture.setUpZ(upZ.longValue());
				        	fixtureDAO.createFixture(fixture);
				        }
					}
				}
				
				
				if (action.equals("rfRepeatFixtures")) {
		    		long startTime = System.currentTimeMillis();
					AppConfig.getAppConfig().reloadDevicesFixturesAndShows(false);
		    		logger.info("fixture reload time=" + ((System.currentTimeMillis() - startTime)/1000.0) + " sec");

					
					FixtureTableEditor tableEditor = new FixtureTableEditor(activeStageId);
					request.setAttribute("form", tableEditor.readFixtures(null));
					ErrorList errors = new ErrorList();
	    			errors.addError("Fixtures added", "Table updated", ErrorList.SEVERITY_OK);
	    			request.setAttribute("errors", errors);
					forward = "success";
					
				} else if (action.equals("rfPreview")) {
					Map json = new HashMap();
					json.put("rows", rows);
					PrintWriter pw = response.getWriter();
					pw.println("<script>top.rfUpdatePreview(" + Struct.structuredMapToJson(json) + ");</script>\n");
					pw.flush();
					forward = "null"; // maps to NullForward
				} else {
					throw new IllegalStateException("Expected 'rfRepeatFixtures' or 'rfPreview'; found '" + action + "'");
				}
				
			} else {
				throw new IllegalArgumentException("Invalid action '" + action + "'");
			}

			List rfDmxAllocations = new ArrayList();
			addElement(rfDmxAllocations, "R", "rows then columns");
			addElement(rfDmxAllocations, "C", "columns then rows");
			request.setAttribute("rfDmxAllocations", rfDmxAllocations);
		}
		
        return mapping.findForward(forward);
    }

    private TopLevelExpression parseExpression(String expr) throws java.text.ParseException {
    	StringReader reader = new StringReader(expr);
        ExpressionParser parser = new ExpressionParser(reader);
        try {
        	TopLevelExpression expr2 = parser.TopLevelExpression();;
        	return expr2;
        } catch (ParseException pe) {
        	logger.error("ParseException for expression '" + expr + "'");
            throw new java.text.ParseException(pe.getMessage(), -1);
        } catch (TokenMgrError tme) {
        	logger.error("TokenMgrError for expression '" + expr + "'");
            throw new java.text.ParseException(tme.getMessage(), -1);
        }
    }
    
    private Long evalLong(TopLevelExpression expr, Map functions, long x, long y, long n) {
    	String exprString = null;
    	try {
    		
    		exprString = ExpressionUtils.expressionToString(expr);
    		logger.info("Evaluating '" + exprString + "' with x=" + x + ", y=" + y + ", n=" + n);
	        Evaluator evaluator = new Evaluator();
	        EvalContext evalContext = new EvalContext();
			evalContext.setVariable("x", new Long(x));
	        evalContext.setVariable("y", new Long(y));
	        evalContext.setVariable("n", new Long(n));
	        evalContext.setFunctions(functions);
	        
	        Object result = evaluator.visit(expr, evalContext);
	        logger.info("Evaluating '" + exprString + "' as " + result);
	        return ((Number)result).longValue();
    	} catch (Exception e) {
    		// TODO log or return an error string
    		logger.error("Exception evaluating '" + exprString + "'", e);
    		return null;
    	}
    }
    
    private void addElement(List list, String id, String name) {
    	Map row = new HashMap();
    	row.put("id", id);
    	row.put("name", name);
    	list.add(row);
    }
    
}
