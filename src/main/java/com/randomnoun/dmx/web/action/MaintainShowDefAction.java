package com.randomnoun.dmx.web.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.jdbc.core.JdbcTemplate;

import bsh.BSHAmbiguousName;
import bsh.BSHClassDeclaration;
import bsh.BSHPackageDeclaration;
import bsh.Parser;
import bsh.SimpleNode;

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.StreamUtils;
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;
import com.randomnoun.common.security.User;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.FixtureDAO;
import com.randomnoun.dmx.dao.ShowDAO;
import com.randomnoun.dmx.dao.ShowDefDAO;
import com.randomnoun.dmx.show.Show;
import com.randomnoun.dmx.to.FixtureDefImageTO;
import com.randomnoun.dmx.to.FixtureTO;
import com.randomnoun.dmx.to.ShowDefTO;
import com.randomnoun.dmx.to.ShowTO;

/**
 * Show definition maintenance action.
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class MaintainShowDefAction
    extends Action {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(MaintainShowDefAction.class);

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
		JdbcTemplate jt = appConfig.getJdbcTemplate();
		String forward = "success";
    	Map form = new HashMap();
    	Struct.setFromRequest(form, request);
    	ErrorList errors = new ErrorList();
    	
    	ShowDefDAO showDefDAO = new ShowDefDAO(jt);
    	ShowDefTO showDef;

    	long showDefId = -1;
    	String showDefIdString = request.getParameter("showDefId");
    	if (!Text.isBlank(showDefIdString)) { showDefId = Long.parseLong(showDefIdString); };
    	request.setAttribute("showDefId", showDefIdString);
    	String action = request.getParameter("action");
    	if (action==null) { action = ""; }
    	 
    	
    	if (action.equals("getShowDef")) {
    		showDef = showDefDAO.getShowDef(showDefId);
    		request.setAttribute("showDef", showDef);
    		
    	} else if (action.equals("newShowDef")) {
    		showDef = new ShowDefTO();
    		showDef.setId(-1);
    		showDef.setName("Untitled show");
    		showDef.setScript(getScriptTemplate());
    		request.setAttribute("showDef", showDef);

    	} else if (action.equals("deleteShowDef")) {
    		ShowTO found = null;
    		showDef = showDefDAO.getShowDef(showDefId);
    		List<ShowTO> shows = (new ShowDAO(appConfig.getJdbcTemplate())).getShows(null);
    		for (ShowTO show : shows) {
    			if (show.getShowDefId()==showDefId) {
    				found = show; 
    				break;
    			}
    		}
    		if (found!=null) {
    			request.setAttribute("showDef", showDef);
    			errors.addError("Show registered", "You cannot delete this show since it has been " +
    				"registered in the active show list. " +
    				"Remove this show from the Shows configuration page and try again.", ErrorList.SEVERITY_ERROR);
    		} else {
	    		showDefDAO.deleteShowDef(showDef);
	    		errors.addError("Show deleted", "Show definition deleted", ErrorList.SEVERITY_OK);
    		}
    		
    	} else if (request.getParameter("updateShowDef")!=null) {
    		Map showDefMap = (Map) form.get("showDef");
    		long lngId = Long.parseLong((String) showDefMap.get("id"));
    		String txtName = (String) showDefMap.get("name");
    		String txtScript = (String) showDefMap.get("script");
    		String className = null;
    		String javadoc = null;
    		errors.addErrors(validateScriptSyntax(txtScript));
    		if (!errors.hasErrors()) {
    			className = getClassName(txtScript);
    			javadoc = getJavadoc(txtScript);
    			errors.addErrors(validateScriptInstance(txtScript, className));
    		}
    		if (errors.hasErrors()) {
    			Struct.setFromRequest(form, request);
    			List errorLines = getErrorLines(errors);
    			request.setAttribute("showDef", form.get("showDef"));
    			request.setAttribute("errorLines", errorLines);
    		} else {
    			showDef = new ShowDefTO();
    			Struct.setFromMap(showDef, showDefMap, false, true, false);
    			showDef.setClassName(className);
    			showDef.setJavadoc(javadoc);
	    		if (lngId==-1) {
	    			showDefDAO.createShowDef(showDef);
	    			errors.addError("Show created", "Show definition created", ErrorList.SEVERITY_OK);
	    		} else {
	    			showDefDAO.updateShowDef(showDef);
	    			errors.addError("Show updated", "Show definition updated", ErrorList.SEVERITY_OK);
	    		}
	    		appConfig.reloadShows();
    		} 
    		
    	} else if (action.equals("")) {
    		// initial page load
    		
    	} else {
    		throw new IllegalArgumentException("Unknown action '" + action + "'");
    	}
    	
    	if (forward.equals("success")) {
    		List showDefs = showDefDAO.getShowDefs(null);
    		request.setAttribute("showDefs", showDefs);
    	}

    	request.setAttribute("errors", errors);
		return mapping.findForward(forward);
		
    }
    
    private String getScriptTemplate() {
    	try {
        	String defaultPackage = AppConfig.getAppConfig().getProperty("show.defaultPackage");
        	if (Text.isBlank(defaultPackage)) { defaultPackage = "com.example.dmx.show.script"; }
	    	InputStream is = this.getClass().getClassLoader().getResourceAsStream("default/defaultShowDef.java");
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamUtils.copyStream(is, baos);
			return "package " + defaultPackage + ";\n" + new String(baos.toByteArray());
		} catch (IOException e) {
			throw new IllegalStateException("IOException in ByteArrayOutputStream", e);
		}
    }
    	
    	
    
    public static class ScriptLocationErrorData extends ErrorList.ErrorData {
    	long lineNumber;
    	long columnNumber;
		public ScriptLocationErrorData(String errorField, String shortText, String longText,
				int severity, long lineNumber, long columnNumber) 
		{
			super(shortText, longText, errorField, severity);
			this.lineNumber = lineNumber;
			this.columnNumber = columnNumber;
		}
		public long getLineNumber() { return lineNumber; }
		public long getColumnNumber() { return columnNumber; }
    }
    
    private ErrorList validateScriptSyntax(String script) {
    	ErrorList errors = new ErrorList();
    	Parser parser = new Parser(new StringReader(script));
    	try {
			while( ! parser.Line() /* eof */ ) {
				Object node = parser.popNode(); // (See the bsh.BSH* classes)
			}
		} catch (bsh.ParseException e) {
			// ParseException.getErrorLineNumber() raises NPEs
			Pattern p = Pattern.compile("Parse error at line ([0-9]+), column ([0-9]+).*");
			Matcher m = p.matcher(e.getMessage());
			if (m.matches()) {
				errors.add(new ScriptLocationErrorData("script", "Parse error", 
					"There was an error parsing the script: " + e.getMessage(), 
					ErrorList.SEVERITY_ERROR,
					Long.parseLong(m.group(1)), Long.parseLong(m.group(2))));
			} else {
				errors.addError("script", "Parse error", "There was an error parsing the script: " +
					e.getMessage());
			}
			logger.debug("Script validation error", e);
		} catch (bsh.TokenMgrError e) {
			Pattern p = Pattern.compile("Lexical error at line ([0-9]+), column ([0-9]+).*");
			Matcher m = p.matcher(e.getMessage());
			if (m.matches()) {
				errors.add(new ScriptLocationErrorData("script", "Parse error", 
					"There was an error parsing the script: " + e.getMessage(), 
					ErrorList.SEVERITY_ERROR,
					Long.parseLong(m.group(1)), Long.parseLong(m.group(2))));
			} else {
				errors.addError("script", "Parse error", "There was an error parsing the script: " +
					e.getMessage());
			}
			logger.debug("Script validation error", e);
			
		}

		
        //ScriptEngineManager factory = new ScriptEngineManager();
        //factory.registerEngineName("Beanshell", new BshScriptEngineFactory());
        //ScriptEngine engine = factory.getEngineByName("Beanshell");
        // engine.eval("print('Hello, World')");
        return errors;
    }
    
    public String getClassName(String script) {
    	Parser parser = new Parser(new StringReader(script));
    	String packageName = null;
    	String className = null;
    	try {
			while( ! parser.Line() /* eof */ ) {
				SimpleNode node = parser.popNode(); // (See the bsh.BSH* classes)
				if (node instanceof BSHPackageDeclaration) {
					BSHPackageDeclaration packageNode = (BSHPackageDeclaration) node;
					packageName = ((BSHAmbiguousName) packageNode.getChild(0)).text;
				} else if (node instanceof BSHClassDeclaration) {
					BSHClassDeclaration classNode = (BSHClassDeclaration) node;
					className = classNode.getName();
				}
			}
		} catch (bsh.ParseException e) {
			throw new IllegalArgumentException("Invalid script", e);
		}
		if (className == null) { throw new IllegalArgumentException("No script defined"); }
		if (packageName == null) { return className; }
		return packageName + "." + className;
    }

    /** Return the first comment in the script. This should probably
     * return the first slash-asterisk-asterisk comment prior to the class declaration,
     * but that would require slightly more effort.
     * 
     * @param script script to parse
     * 
     * @return the class javadoc, hopefully
     */
    public String getJavadoc(String script) {
    	Parser parser = new Parser(new StringReader(script));
    	parser.setRetainComments(true);
    	String javadoc = null;
    	try {
			while( ! parser.Line() /* eof */ ) {
				SimpleNode node = parser.popNode(); // (See the bsh.BSH* classes)
				if (node instanceof bsh.BSHFormalComment) {
					javadoc = ((bsh.BSHFormalComment) node).text;
					break;
				}
			}
		} catch (bsh.ParseException e) {
			throw new IllegalArgumentException("Invalid script", e);
		}
		return javadoc;
    }

    
    public ErrorList validateScriptInstance(String script, String className) {
    	ErrorList errors = new ErrorList();
    	AppConfig appConfig = AppConfig.getAppConfig();
		Show showObj;
		try {
			Controller testController = new Controller();
			ScriptEngine scriptEngine = appConfig.getScriptEngine();
			ScriptContext scriptContext = scriptEngine.getContext();
			appConfig.loadFixtures(scriptContext, testController);
			scriptEngine.eval(script, scriptContext);
			String testScript =
				"import com.randomnoun.dmx.Show;\n" +
				"import " + className + ";\n" +
				"return " + className + ".class;\n" ;
			Class clazz = (Class) scriptEngine.eval(testScript, scriptContext);
			if (!Show.class.isAssignableFrom(clazz)) {
				errors.addError("script", "Invalid class", "Class " + className + " does not extend com.randomnoun.dmx.Show"); 
			} else {
				Properties nullProperties = new Properties();
				Constructor constructor = clazz.getConstructor(long.class, Controller.class, Properties.class);
				showObj = (Show) constructor.newInstance(0L, testController, nullProperties);
			}
		} catch (Exception e) {
			logger.error("Exception validating scripted show", e);
			errors.addError("script", "Invalid class", "Error whilst instantiating show: " + getStackSummary(e));
		}
    	return errors;
    }
    
    public String getStackSummary(Throwable e) {
    	String summary = "";
    	while (e!=null) {
    		summary += "(" + e.getClass().getName() + ")";
    		if (!Text.isBlank(e.getMessage())) { summary += "<br/>" + e.getMessage(); }
    		e = e.getCause();
    	}
    	return summary;
    }
    
    public List getErrorLines(ErrorList errors) {
    	List errorLines = new ArrayList();
		for (int i=0; i<errors.size(); i++) {
			if (errors.get(i) instanceof ScriptLocationErrorData) {
				ScriptLocationErrorData sled = (ScriptLocationErrorData) errors.get(i);
				errorLines.add(sled.getLineNumber());
			}
		}
		return errorLines;
    }
}

