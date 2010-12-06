package com.randomnoun.dmx.web.action;


import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.struts.upload.FormFile;
import org.apache.struts.upload.MultipartRequestHandler;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import bsh.BSHAmbiguousName;
import bsh.BSHClassDeclaration;
import bsh.BSHPackageDeclaration;
import bsh.Parser;
import bsh.SimpleNode;
// import bsh.SimpleNode; - package private. bastardos.

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.StreamUtils;
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;
import com.randomnoun.common.security.User;
import com.randomnoun.common.webapp.upload.FileProgressTO;
import com.randomnoun.common.webapp.upload.ProgressTO;
import com.randomnoun.dmx.Controller;
import com.randomnoun.dmx.Universe;
import com.randomnoun.dmx.channelMuxer.ChannelMuxer;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.FixtureDefDAO;
import com.randomnoun.dmx.dao.FixtureDefImageDAO;
import com.randomnoun.dmx.fixture.Fixture;
import com.randomnoun.dmx.fixture.FixtureController;
import com.randomnoun.dmx.fixture.FixtureDef;
import com.randomnoun.dmx.show.Show;
import com.randomnoun.dmx.to.FixtureDefImageTO;
import com.randomnoun.dmx.to.FixtureDefTO;
import com.randomnoun.dmx.web.ExtendedMultiPartRequestHandler;
import com.randomnoun.dmx.web.StrutsUploadForm;

/**
 * Fixture definition maintenance action
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class MaintainFixtureDefAction
    extends Action {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(MaintainFixtureDefAction.class);

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
		JdbcTemplate jt = appConfig.getJdbcTemplate();
		String forward = "success";
    	Map form = new HashMap();
    	Struct.setFromRequest(form, request);
    	ErrorList errors = new ErrorList();
    	
    	FixtureDefDAO fixtureDefDAO = new FixtureDefDAO(jt);
    	FixtureDefImageDAO fixtureDefImageDAO = new FixtureDefImageDAO(appConfig.getJdbcTemplate());
    	
    	FixtureDefTO fixtureDef;
    	List<FixtureDefImageTO> fixtureDefImages;

    	long fixtureDefId = -1;
    	String fixtureDefIdString = request.getParameter("fixtureDefId");
    	request.setAttribute("fixtureDefId", fixtureDefIdString);
    	if (!Text.isBlank(fixtureDefIdString)) { fixtureDefId = Long.parseLong(fixtureDefIdString); };
    	String action = request.getParameter("action");
    	if (action==null) { action = ""; }
    	 
    	
    	if (action.equals("getFixtureDef")) {
    		fixtureDef = fixtureDefDAO.getFixtureDef(fixtureDefId);
    		fixtureDefImages = fixtureDefImageDAO.getFixtureDefImages(fixtureDef);
    		request.setAttribute("fixtureDef", fixtureDef);
    		request.setAttribute("fixtureDefImages", fixtureDefImages);
    		
    	} else if (action.equals("newFixtureDef")) {
    		fixtureDef = new FixtureDefTO();
    		fixtureDef.setId(-1);
    		fixtureDef.setName("name of fixture");
    		fixtureDef.setFixtureDefScript(getFixtureDefScriptTemplate());
    		fixtureDef.setFixtureControllerScript(getFixtureControllerScriptTemplate());
    		request.setAttribute("fixtureDef", fixtureDef);
    				
    	} else if (request.getParameter("updateFixtureDef")!=null) {
    		Map fixtureDefMap = (Map) form.get("fixtureDef");
    		long lngId = Long.parseLong((String) fixtureDefMap.get("id"));
    		String fixtureDefScript = (String) fixtureDefMap.get("fixtureDefScript");
    		String fixtureControllerScript = (String) fixtureDefMap.get("fixtureControllerScript");
    		String channelMuxerScript = (String) fixtureDefMap.get("channelMuxerScript");
    		String fixtureDefClassName = null;
    		String fixtureControllerClassName = null;
    		String channelMuxerClassName = null;
			List errorLines = getErrorLines(errors);
    		
    		ErrorList fixtureDefErrors = validateScriptSyntax("fixture definition", fixtureDefScript);
    		ErrorList fixtureControllerErrors = validateScriptSyntax("fixture controller", fixtureControllerScript);
    		ErrorList channelMuxerErrors = validateScriptSyntax("channel muxer", channelMuxerScript);
    		List fixtureDefErrorLines = getErrorLines(fixtureDefErrors);
    		List fixtureControllerErrorLines = getErrorLines(fixtureControllerErrors);
    		List channelMuxerErrorLines = getErrorLines(channelMuxerErrors);
    		
    		errors.addErrors(fixtureDefErrors);
    		errors.addErrors(fixtureControllerErrors);
    		errors.addErrors(channelMuxerErrors);
    		
    		if (!errors.hasErrors()) {
    			fixtureDefClassName = getClassName(fixtureDefScript);
    			fixtureControllerClassName = getClassName(fixtureControllerScript);
    			channelMuxerClassName = getClassName(channelMuxerScript);
    			ErrorList instanceErrors = validateScriptInstances(fixtureDefScript, fixtureDefClassName, fixtureControllerScript, fixtureControllerClassName, channelMuxerScript, channelMuxerClassName);
    			fixtureDefErrorLines.addAll(getErrorLines(instanceErrors));
    			errors.addErrors(instanceErrors);
    		}
    		if (errors.hasErrors()) {
    			Struct.setFromRequest(form, request);
    			request.setAttribute("fixtureDefErrorLines", fixtureDefErrorLines);
    			request.setAttribute("fixtureControllerErrorLines", fixtureControllerErrorLines);
    			request.setAttribute("channelMuxerErrorLines", channelMuxerErrorLines);
    			request.setAttribute("fixtureDef", form.get("fixtureDef"));
    		} else {
    			fixtureDef = new FixtureDefTO();
    			Struct.setFromMap(fixtureDef, fixtureDefMap, false, true, false);
    			fixtureDef.setFixtureDefClassName(getClassName(fixtureDef.getFixtureDefScript()));
    			fixtureDef.setFixtureControllerClassName(getClassName(fixtureDef.getFixtureControllerScript()));
	    		if (lngId==-1) {
	    			fixtureDefDAO.createFixtureDef(fixtureDef);
	    			errors.addError("Fixture created", "Fixture definition created", ErrorList.SEVERITY_OK);
	    		} else {
	    			fixtureDefDAO.updateFixtureDef(fixtureDef);
	    			errors.addError("Fixture updated", "Fixture definition updated", ErrorList.SEVERITY_OK);
	    		}
	    		appConfig.reloadFixturesAndShows();
    		}
    		if (lngId!=-1) {
    			FixtureDefTO tmp = new FixtureDefTO();
    			tmp.setId(lngId);
        		fixtureDefImages = fixtureDefImageDAO.getFixtureDefImages(tmp);
        		request.setAttribute("fixtureDefImages", fixtureDefImages);
    		}
    		
    	} else if (action.equals("getProgress")) {
    		logger.debug("Getting progress in session '" + request.getSession().getId() + "'"); 
    		FileProgressTO progressTO = (FileProgressTO) session.getAttribute(ExtendedMultiPartRequestHandler.PROGRESS_SESSION_KEY);
    		if (progressTO==null) {
    			request.setAttribute("json", "{ 'percentDone' : 0 }" );
    		} else {
    			request.setAttribute("json", progressToJson(progressTO));
    		}
    	    forward="json";
    	
    	} else if (action.equals("submitFile")) {
    		String script = "";
    		if (request.getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED)!=null) {
    			// errors.addError("Image not uploaded", "Maximum sizelimit exceeded", ErrorList.SEVERITY_OK);
    			script = "parent.edtCompletedUploadError(\"Maximum sizelimit exceeded\");";
    		} else {
    			String description = ((String[]) actionForm.getMultipartRequestHandler().getAllElements().get("description"))[0];
	            Map files = actionForm.getMultipartRequestHandler().getFileElements();
	            FormFile file = (FormFile) files.get("attachment");
	            if (file.getFileSize()==0) {
	            	script = "parent.edtCompletedUploadError(\"Zero-byte file submitted\");";
	            } else {
		            FixtureDefImageTO fixtureDefImage = new FixtureDefImageTO();
		            fixtureDefImage.setFixtureDefId(fixtureDefId);
		            fixtureDefImage.setName(file.getFileName());
		            fixtureDefImage.setDescription(description);
		            fixtureDefImage.setSize(file.getFileSize());
		            fixtureDefImage.setContentType(file.getContentType());
		            fixtureDefImageDAO.createFixtureDefImage(fixtureDefImage);
		            fixtureDefImageDAO.saveImage(fixtureDefImage, file.getInputStream());
		            // errors.addError("Image uploaded", "Documentation file '" + fileName + "' (" + fileSize + " bytes) uploaded OK", ErrorList.SEVERITY_OK);
		            script = "parent.edtCompletedUploadOK(" + fixtureDefImage.getId() + ", \"" + fixtureDefImage.getSizeInUnits() + 
		              "\", \"" + Text.escapeJavascript2(fixtureDefImage.getName()) + "\", \"" + Text.escapeJavascript2(fixtureDefImage.getDescription()) + "\");";
	            }
    		}
    		request.setAttribute("script", script);
    		forward = "script";
    		
    	} else if (action.equals("getFile")) {
    		long fileId = Long.parseLong(request.getParameter("fileId"));
    		FixtureDefImageTO fixtureDefImage = fixtureDefImageDAO.getFixtureDefImage(fileId);
    		response.setContentType(fixtureDefImage.getContentType());
    		response.setContentLength((int) fixtureDefImage.getSize());
    		InputStream is = fixtureDefImageDAO.loadImage(fixtureDefImage);
    		StreamUtils.copyStream(is, response.getOutputStream());
    		forward = null;
    		
    	} else if (action.equals("deleteFile")) {
    		Map result = new HashMap();
    		try {
	    		long fileId = Long.parseLong(request.getParameter("fileId"));
	    		FixtureDefImageTO fixtureDefImage = fixtureDefImageDAO.getFixtureDefImage(fileId);
	    		fixtureDefImageDAO.deleteFixtureDefImage(fixtureDefImage);
	    		result.put("result", "success");
	    		result.put("fileId", new Long(fileId));
    		} catch (Exception e) {
    			logger.error("Exception deleting file", e);
    			result.put("result", "failure");
    			result.put("message", e.getMessage());
    		}
    		request.setAttribute("json", Struct.structuredMapToJson(result));
    		forward = "json";
    		
    	} else if (action.equals("")) {
    		// initial page load
    		
    	} else {
    		throw new IllegalArgumentException("Unknown action '" + action + "'");
    	}
    	
    	if (forward.equals("success")) {
    		List fixtureDefs = fixtureDefDAO.getFixtureDefs(null);
    		request.setAttribute("fixtureDefs", fixtureDefs);
    	}

    	request.setAttribute("errors", errors);
    	if (forward==null) {
    		return null;
    	} else {
    		return mapping.findForward(forward);
    	}
		
    }
    
    private String getFixtureDefScriptTemplate() {
    	String defaultPackage = AppConfig.getAppConfig().getProperty("fixture.defaultPackage");
    	if (Text.isBlank(defaultPackage)) { defaultPackage = "com.randomnoun.dmx.fixture.script"; }
    	return
    	"package " + defaultPackage + ";\n" +
    	"\n" +
    	"import java.awt.Color;\n" +
    	"\n" +
    	"import com.randomnoun.dmx.Fixture;\n" +
    	"import com.randomnoun.dmx.FixtureController;\n" +
    	"import com.randomnoun.dmx.FixtureDef;\n" +
    	"import com.randomnoun.dmx.channel.MacroChannelDef;\n" +
    	"import com.randomnoun.dmx.channel.MacroChannelDef.Macro;\n" +
    	"import com.randomnoun.dmx.channel.dimmer.BlueDimmerChannelDef;\n" +
    	"import com.randomnoun.dmx.channel.dimmer.GreenDimmerChannelDef;\n" +
    	"import com.randomnoun.dmx.channel.dimmer.MasterDimmerChannelDef;\n" +
    	"import com.randomnoun.dmx.channel.dimmer.RedDimmerChannelDef;\n" +
    	"import com.randomnoun.dmx.channel.SpeedChannelDef;\n" +
    	"import com.randomnoun.dmx.channel.StrobeChannelDef;\n" +
    	"import com.randomnoun.dmx.channelMuxer.ChannelMuxer;\n" +
    	"import com.randomnoun.dmx.channelMuxer.MacroChannelMuxer;\n" +
    	"import com.randomnoun.dmx.channelMuxer.filter.MasterDimmerChannelMuxer;\n" +
    	"import com.randomnoun.dmx.channelMuxer.primitive.ColorChannelMuxer;\n" +
    	"import com.randomnoun.dmx.channelMuxer.timed.StrobeChannelMuxer;\n" +
    	"import com.randomnoun.dmx.channelMuxer.timed.TimedColorGradientChannelMuxer;\n" +
    	"import com.randomnoun.dmx.timeSource.DistortedTimeSource;\n" +
    	"import com.randomnoun.dmx.timeSource.TimeSource;\n" +
    	"import com.randomnoun.dmx.timeSource.UniverseTimeSource;\n" +
    	"\n" +
    	"/** Fixture definition for xxxx\n" +
    	" * \n" +
    	" * @author name\n" +
    	" */\n" +
    	"public class X0177FixtureDef extends FixtureDef {\n" +
    	"\n" +
    	"	\n" +
    	"	public X0177FixtureDef() {\n" +
    	"		this.vendor = \"Chinese sweatshop workers\";\n" +
    	"		this.model = \"MODEL-12345\";\n" +
    	"		this.maxWattage = 20;\n" +
    	"		this.numDmxChannels=7;\n" +
    	"		\n" +
    	"		this.addChannelDef(new MasterDimmerChannelDef(0));\n" +
    	"		this.addChannelDef(new RedDimmerChannelDef(1));\n" +
    	"		this.addChannelDef(new GreenDimmerChannelDef(2));\n" +
    	"		this.addChannelDef(new BlueDimmerChannelDef(3));\n" +
    	"		this.addChannelDef(new StrobeChannelDef(4, 0, 2, 8, 10, 250)); // 2Hz->10Hz strobe\n" +
    	"	}\n" +
    	"	\n" +
    	"	\n" +
    	"	public ChannelMuxer getChannelMuxer(Fixture fixture) {\n" +
    	"		TimeSource universeTimeSource = new UniverseTimeSource(fixture.getUniverse());\n" +
    	"		// output is determined by\n" +
    	"		//   color DMX values + strobe DMX value\n" +
    	"		// and the fixture can then be dimmed by the master dimmer DMX value\n" +
    	"		\n" +
    	"		ChannelMuxer colorMuxer = new ColorChannelMuxer(fixture);\n" +
    	"		StrobeChannelMuxer strobeMuxer = new StrobeChannelMuxer(colorMuxer, universeTimeSource);\n" +
    	"		return new MasterDimmerChannelMuxer(strobeMuxer);\n" +
    	"	}\n" +
    	"	\n" +
    	"	public FixtureController getFixtureController(Fixture fixture) {\n" +
    	"		return new X0177FixtureController(fixture);\n" +
    	"	}\n" +
    	"	\n" +
    	"}\n";
    }
    
    public String getFixtureControllerScriptTemplate() {
    	String defaultPackage = AppConfig.getAppConfig().getProperty("fixture.defaultPackage");
    	if (Text.isBlank(defaultPackage)) { defaultPackage = "com.randomnoun.dmx.fixture.script"; }

    	return
    	"package " + defaultPackage + ";\n" +
    	"\n" +
    	"import java.awt.Color;\n" +
    	"\n" +
    	"import com.randomnoun.dmx.Fixture;\n" +
    	"import com.randomnoun.dmx.FixtureController;\n" +
    	"import com.randomnoun.dmx.FixtureDef;\n" +
    	"\n" +
    	"public class X0177FixtureController extends FixtureController {\n" +
    	"	public X0177FixtureController(Fixture fixture) {\n" +
    	"		super(fixture);\n" +
    	"	}\n" +
    	"}\n";
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
    
    private ErrorList validateScriptSyntax(String scriptType, String script) {
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
					"There was an error parsing the " + scriptType + " script: " + e.getMessage(), 
					ErrorList.SEVERITY_ERROR,
					Long.parseLong(m.group(1)), Long.parseLong(m.group(2))));
			} else {
				errors.addError("script", "Parse error", "There was an error parsing the " + scriptType + " script: " +
					e.getMessage());
			}
			logger.debug("Script validation error", e);
		} catch (bsh.TokenMgrError e) {
			Pattern p = Pattern.compile("Lexical error at line ([0-9]+), column ([0-9]+).*");
			Matcher m = p.matcher(e.getMessage());
			if (m.matches()) {
				errors.add(new ScriptLocationErrorData("script", "Parse error", 
					"There was an error parsing the " + scriptType + " script: " + e.getMessage(), 
					ErrorList.SEVERITY_ERROR,
					Long.parseLong(m.group(1)), Long.parseLong(m.group(2))));
			} else {
				errors.addError("script", "Parse error", "There was an error parsing the " + scriptType + " script: " +
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
    
    public ErrorList validateScriptInstances(
    		String fixtureDefScript, String fixtureDefClassName, 
    		String fixtureControllerScript, String fixtureControllerClassName,
    		String channelMuxerScript, String channelMuxerClassName) 
    {
    	ErrorList errors = new ErrorList();
    	AppConfig appConfig = AppConfig.getAppConfig();
    	Fixture fixtureObj = null;
		FixtureDef fixtureDefObj = null;
		FixtureController fixtureControllerObj = null;
		ChannelMuxer channelMuxerObj = null;
		ScriptEngine scriptEngine = null;
		ScriptContext scriptContext = null;
		Universe nullUniverse = new Universe();
		Controller testController = new Controller();
		
		try {
			scriptEngine = appConfig.getScriptEngine();
			scriptContext = scriptEngine.getContext();
			appConfig.loadFixtures(scriptContext, testController);
			scriptEngine.eval(fixtureDefScript, scriptContext);
			scriptEngine.eval(fixtureControllerScript, scriptContext);
			scriptEngine.eval(channelMuxerScript, scriptContext);
			
			String testScript =
				"import com.randomnoun.dmx.fixture.FixtureDef;\n" +
				"import " + fixtureDefClassName + ";\n" +
				"return " + fixtureDefClassName + ".class;\n" ;
			// @TODO check class before instantiating
			Class clazz = (Class) scriptEngine.eval(testScript, scriptContext);
			if (!FixtureDef.class.isAssignableFrom(clazz)) {
				errors.addError("script", "Invalid class", "Class " + fixtureDefClassName + " does not extend com.randomnoun.dmx.fixture.Fixture"); 
			} else {
				Map nullProperties = new HashMap();
				Constructor constructor = clazz.getConstructor();
				fixtureDefObj = (FixtureDef) constructor.newInstance();
				fixtureObj = new Fixture("testFixture", fixtureDefObj, nullUniverse, 1);
				ChannelMuxer channelMuxer = fixtureDefObj.getChannelMuxer(fixtureObj);
			}
			fixtureObj = new Fixture("Fixture validation", fixtureDefObj, nullUniverse, 1);
		} catch (Exception e) {
			logger.error("Exception validating scripted fixture", e);
			errors.addError("script", "Invalid class", "Error whilst instantiating fixture definition: " + getStackSummary(e));
		}
		
		if (!errors.hasErrors()) {
			try {
				String testScript =
					"import com.randomnoun.dmx.fixture.FixtureController;\n" +
					"import " + fixtureControllerClassName + ";\n" +
					"return " + fixtureControllerClassName + ".class;\n" ;
				// @TODO check class before instantiating
				Class clazz = (Class) scriptEngine.eval(testScript, scriptContext);
				if (!FixtureController.class.isAssignableFrom(clazz)) {
					errors.addError("script", "Invalid class", "Class " + fixtureControllerClassName + " does not extend com.randomnoun.dmx.fixture.FixtureController"); 
				} else {
					Map nullProperties = new HashMap();
					Constructor constructor = clazz.getConstructor(Fixture.class);
					fixtureControllerObj = (FixtureController) constructor.newInstance(fixtureObj);
				}
			} catch (Exception e) {
				logger.error("Exception validating scripted fixture controller", e);
				errors.addError("script", "Invalid class", "Error whilst instantiating fixture controller definition: " + getStackSummary(e));
			}
		}
		
		if (!errors.hasErrors()) {
			try {
				String testScript =
					"import com.randomnoun.dmx.channelMuxer.ChannelMuxer;\n" +
					"import " + channelMuxerClassName + ";\n" +
					"return " + channelMuxerClassName + ".class;\n" ;
				// @TODO check class before instantiating
				Class clazz = (Class) scriptEngine.eval(testScript, scriptContext);
				if (!ChannelMuxer.class.isAssignableFrom(clazz)) {
					errors.addError("script", "Invalid class", "Class " + channelMuxerClassName + " does not extend com.randomnoun.dmx.channelMuxer.ChannelMuxer"); 
				} else {
					Map nullProperties = new HashMap();
					Constructor constructor = clazz.getConstructor(Fixture.class);
					channelMuxerObj = (ChannelMuxer) constructor.newInstance(fixtureObj);
				}
			} catch (Exception e) {
				logger.error("Exception validating scripted channel muxer", e);
				errors.addError("script", "Invalid class", "Error whilst instantiating channel muxer definition: " + getStackSummary(e));
			}
		}

		
    	return errors;
    }

    

    /** Returns the name of the first class defined in the script */
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
					if (packageName == null) { return className; }
					return packageName + "." + className;
				}
			}
		} catch (bsh.ParseException e) {
			throw new IllegalArgumentException("Invalid script", e);
		}
		if (className == null) { throw new IllegalArgumentException("No script defined"); }
		throw new IllegalStateException("Internal error (non-null className)");  // This codepath can't execute
    }
    
    /** Returns the javadoc of the first class defined in the script */
    public String getJavadoc(String script) {
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
					if (packageName == null) { return className; }
					return packageName + "." + className;
				}
			}
		} catch (bsh.ParseException e) {
			throw new IllegalArgumentException("Invalid script", e);
		}
		if (className == null) { throw new IllegalArgumentException("No script defined"); }
		throw new IllegalStateException("Internal error (non-null className)");  // This codepath can't execute
    }

    
    
    public String getStackSummary(Throwable e) {
    	String summary = "";
    	while (e!=null) {
    		summary += "(" + e.getClass().getName() + ")";
    		if (!Text.isBlank(e.getMessage())) { summary += "\n" + e.getMessage(); }
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
    
    public String progressToJson(FileProgressTO progress) {
    	String subtasksJSON;
    	if (progress.getSubtasks()==null) {
    		subtasksJSON = "null";
    	} else {
    		subtasksJSON = "";
	    	synchronized(progress.getSubtasks()) {
	    		for (int i=0; i<progress.getSubtasks().size(); i++) {
	    			ProgressTO subtask = (ProgressTO) progress.getSubtasks().get(i);
		    		subtasksJSON += (subtasksJSON.equals("") ? "" : ", ") + progressToJson(progress);
		    	}
	    	}
	    	subtasksJSON = "[" + subtasksJSON + "]";
    	}
    	return 
    	  "{ 'percentDone' : " + progress.getPercentDone() +  "', " +
    	  "  'status' : '" + Text.escapeJavascript2(progress.getStatus()) + "', " +  // use enums
    	  "  'subTasks' : " + subtasksJSON + " }";
    }

    
}

