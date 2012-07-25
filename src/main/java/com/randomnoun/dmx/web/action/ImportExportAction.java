package com.randomnoun.dmx.web.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.StreamUtils;
import com.randomnoun.common.Struct;
import com.randomnoun.common.Text;
import com.randomnoun.common.security.User;
import com.randomnoun.common.webapp.struts.FileRequestWrapper;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.DeviceDAO;
import com.randomnoun.dmx.dao.DevicePropertyDAO;
import com.randomnoun.dmx.dao.FixtureDAO;
import com.randomnoun.dmx.dao.FixtureDefDAO;
import com.randomnoun.dmx.dao.FixtureDefImageDAO;
import com.randomnoun.dmx.dao.ShowDAO;
import com.randomnoun.dmx.dao.ShowDefDAO;
import com.randomnoun.dmx.dao.ShowPropertyDAO;
import com.randomnoun.dmx.dao.StageDAO;
import com.randomnoun.dmx.to.DevicePropertyTO;
import com.randomnoun.dmx.to.DeviceTO;
import com.randomnoun.dmx.to.FixtureDefImageTO;
import com.randomnoun.dmx.to.FixtureDefTO;
import com.randomnoun.dmx.to.FixtureTO;
import com.randomnoun.dmx.to.ShowDefTO;
import com.randomnoun.dmx.to.ShowPropertyTO;
import com.randomnoun.dmx.to.ShowTO;
import com.randomnoun.dmx.to.StageTO;


/**
 * Import/export action
 * 
 * @TODO may want to store metadata with a stage without any fixtures/shows.
 *   although really ? ... really ?
 *   could put a 'metadata' node in there. but why.
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class ImportExportAction
    extends Action {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(ImportExportAction.class);

    
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
		
		if (ServletFileUpload.isMultipartContent(request)) {
    	    request = new FileRequestWrapper(request); 
        }
		
		String action = request.getParameter("action");

		FixtureDefDAO fixtureDefDAO = new FixtureDefDAO(jt);
		FixtureDefImageDAO fixtureDefImageDAO = new FixtureDefImageDAO(jt);
		ShowDefDAO showDefDAO = new ShowDefDAO(jt);
		FixtureDAO fixtureDAO = new FixtureDAO(jt);
		ShowDAO showDAO = new ShowDAO(jt);
		ShowPropertyDAO showPropertyDAO = new ShowPropertyDAO(jt);
		DeviceDAO deviceDAO = new DeviceDAO(jt);
		DevicePropertyDAO devicePropertyDAO = new DevicePropertyDAO(jt);
		
		StageDAO stageDAO = new StageDAO(jt);
		
		if (action==null) { action = ""; }
		if (action.equals("")) {
			// default action displays entry page
			List topLevel = new ArrayList();
			Map topLevelMap = newItem("Everything");
			topLevel.add(topLevelMap);
			
			List items = new ArrayList();
			topLevelMap.put("children", items);
			items.add(newItem("Device settings", "devices", "icnDevice.png"));
			
			Map itemMap = newItem("Fixture definitions", null, "icnFixtureDef2.png");
			List itemChildren = new ArrayList();
			itemMap.put("children", itemChildren);
			List<FixtureDefTO> fixtureDefs = fixtureDefDAO.getFixtureDefs(null);
			for (int i=0; i<fixtureDefs.size(); i++) {
				itemChildren.add(newItem(fixtureDefs.get(i).getName(), "fix-" + fixtureDefs.get(i).getId(), "icnFixtureDef2.png"));
			}
			items.add(itemMap);
			
			itemMap = newItem("Show definitions", null, "icnShowDef2.png");
			itemChildren = new ArrayList();
			itemMap.put("children", itemChildren);
			List<ShowDefTO> showDefs = showDefDAO.getShowDefs(null);
			for (int i=0; i<showDefs.size(); i++) {
				itemChildren.add(newItem(showDefs.get(i).getName(), "show-" + showDefs.get(i).getId(), "icnShowDef2.png"));
			}
			items.add(itemMap);
			
			itemMap = newItem("Stages", null, "icnStage.png");
			itemChildren = new ArrayList();
			itemMap.put("children", itemChildren);
			List<StageTO> stages = stageDAO.getStages(null);
			for (int i=0; i<stages.size(); i++) {
				Map stageItem = newItem(stages.get(i).getName(), null, "icnStage.png");
				itemChildren.add(stageItem);
				List itemChildren2 = new ArrayList();
				stageItem.put("children", itemChildren2);
				itemChildren2.add(newItem("Fixtures", "stage-fix-" + stages.get(i).getId(), "icnFixture2.png")); // @TODO add fixture/show counts
				itemChildren2.add(newItem("Shows", "stage-show-" + stages.get(i).getId(), "icnShow.png"));
			}
			items.add(itemMap);
			
			request.setAttribute("exportItems", topLevel);
			
		} else if (action.equals("export")) {
			List<Long> exportFixtureDefs = new ArrayList<Long>();
			List<Long> exportShowDefs = new ArrayList<Long>();
			List<Long> exportStageFixtures = new ArrayList<Long>();
			List<Long> exportStageShows = new ArrayList<Long>();
			boolean exportDevices = false;
			
			for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
				String p = (String) e.nextElement();
				logger.debug("param '" + p + "'");
				if (p.startsWith("fix-")) { exportFixtureDefs.add(Long.parseLong(p.substring(4))); }
				if (p.startsWith("show-")) { exportShowDefs.add(Long.parseLong(p.substring(5))); }
				if (p.startsWith("stage-fix-")) { exportStageFixtures.add(Long.parseLong(p.substring(10))); }
				if (p.startsWith("stage-show-")) { exportStageShows.add(Long.parseLong(p.substring(11))); }
				if (p.equals("devices")) { exportDevices = true; }
			}
			
			Set stageSet = new HashSet<Long>();
			stageSet.addAll(exportStageFixtures);
			stageSet.addAll(exportStageShows);
			List<Long> exportStages = new ArrayList<Long>(stageSet);

			ByteArrayOutputStream fixtureDefOs = new ByteArrayOutputStream();
			ByteArrayOutputStream showDefOs = new ByteArrayOutputStream();
			ByteArrayOutputStream fixtureOs = new ByteArrayOutputStream();
			ByteArrayOutputStream showOs = new ByteArrayOutputStream();
			ByteArrayOutputStream stageOs = new ByteArrayOutputStream();
			ByteArrayOutputStream deviceOs = new ByteArrayOutputStream();
			ByteArrayOutputStream exportOs = new ByteArrayOutputStream();
			
			
			PrintWriter fixtureDefPw = new PrintWriter(fixtureDefOs);
			PrintWriter showDefPw = new PrintWriter(showDefOs);
			PrintWriter fixturePw = new PrintWriter(fixtureOs);
			PrintWriter showPw = new PrintWriter(showOs);
			PrintWriter stagePw = new PrintWriter(stageOs);
			PrintWriter devicePw = new PrintWriter(deviceOs);
			PrintWriter exportPw = new PrintWriter(exportOs);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			ZipEntry ze = new ZipEntry("pom.xml");
			zos.putNextEntry(ze);
			
			ByteArrayOutputStream pomOs = new ByteArrayOutputStream();
			String defaultGroupId = appConfig.getProperty("importExport.defaultGroupId");
			String defaultScm = appConfig.getProperty("importExport.defaultScm");
        	if (Text.isBlank(defaultGroupId)) { defaultGroupId = "com.example.dmx"; }
        	if (Text.isBlank(defaultScm)) { defaultScm = "scm:cvs:pserver:you@your-cvs-server:/your-repos:export-dmx-web"; }
        	
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("default/exportedPom.xml");
    		if (is==null) { throw new IllegalStateException("Could not find resource 'default/exportedPom.xml'"); }
    		String pomXml = new String(StreamUtils.getByteArray(is), "UTF-8");
    		is.close();
    		pomXml = Text.replaceString(pomXml, "{GROUPID_GOES_HERE}", defaultGroupId);
    		pomXml = Text.replaceString(pomXml, "{SCM_GOES_HERE}", defaultScm);
			
			PrintWriter pw = new PrintWriter(zos);
			pw.print(pomXml);
			pw.flush();
			
			/*
			 pom.xml
src/main/com/whatever/fixture/TheFixtureDef.java
src/main/com/whatever/fixture/TheFixtureController.java
src/main/com/whatever/show/TheShow.java
src/main/resources/fixture.xml (universe/dmx patch data)
src/main/resources/show.xml (showid, properties etc)
src/main/resources/export.xml (date of export, totals etc)
			 */
			
			// 2012-06-25T12:11:53 - for use in microsoft preamble
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date now = new Date();
			String now1 = sdf.format(now);
			
			// 2012-06-25T12-11-53 - for use in zip filename
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
			String now2 = sdf2.format(now);
			
			//fixtureDefPw.println(getMicrosoftPreamble("fixtureDef", now1));
			//fixtureDefImagePw.println(getMicrosoftPreamble("fixtureDefImage", now1));
			
			devicePw.println("<devices>\n");
			if (exportDevices) {
				logger.info("Exporting devices");
				List<DeviceTO> devices = deviceDAO.getDevices(null);
				for (DeviceTO device : devices) {
					List<DevicePropertyTO> deviceProperties = devicePropertyDAO.getDeviceProperties("deviceId=" + device.getId());
					device.setDeviceProperties(deviceProperties);
					devicePw.println(Text.indent("    ", device.toExportXml()));
				}
			}
			devicePw.println("</devices>\n");
			devicePw.flush();
			
			stagePw.println("<stages>\n");
			for (long stageId : exportStages) {
				logger.info("Exporting stage " + stageId);
				StageTO stage = stageDAO.getStage(stageId);
				stagePw.println(Text.indent("    ", stage.toExportXml()));
			}
			stagePw.println("</stages>\n");
			stagePw.flush();
			
			fixtureDefPw.println("<fixtureDefs>");
			for (long fixtureDefId : exportFixtureDefs) {
				logger.info("Exporting fixtureDef " + fixtureDefId);
				FixtureDefTO fixtureDef = fixtureDefDAO.getFixtureDef(fixtureDefId);
				ze = new ZipEntry("src/main/beanshell/" + Text.replaceString(fixtureDef.getFixtureDefClassName(), ".", "/") + ".beanshell");
				zos.putNextEntry(ze);
				zos.write(fixtureDef.getFixtureDefScript().getBytes());

				ze = new ZipEntry("src/main/beanshell/" + Text.replaceString(fixtureDef.getFixtureControllerClassName(), ".", "/") + ".beanshell");
				zos.putNextEntry(ze);
				zos.write(fixtureDef.getFixtureControllerScript().getBytes());
			
				List<FixtureDefImageTO> fixtureDefImages = fixtureDefImageDAO.getFixtureDefImages("fixtureDefId=" + fixtureDef.getId());
				for (FixtureDefImageTO fdi : fixtureDefImages) {
					ze = new ZipEntry("src/main/resources/fixtureDefs/" + fixtureDef.getId() + "/" + fdi.getName());
					zos.putNextEntry(ze);
					// FileInputStream fis = new FileInputStream(fdi.getFileLocation());
					is = fixtureDefImageDAO.loadImage(fdi);
					StreamUtils.copyStream(is, zos);
					is.close();
					
					//fixtureDefImagePw.println(Text.indent("    ", fdi.toExportXml()));
				}

				// could use annotations for these sorts of things, maybe.
				// although why.
				fixtureDefPw.println(Text.indent("    ", fixtureDef.toExportXml(fixtureDefImages)));
			}
			fixtureDefPw.println("</fixtureDefs>\n");
			fixtureDefPw.flush();
			
			fixturePw.println("<fixtures>\n");
			for (long stageId : exportStageFixtures) {
				List<FixtureTO> fixtures = fixtureDAO.getFixtures("stageId=" + stageId);
				for (FixtureTO fixture : fixtures) {
					fixturePw.println(Text.indent("    ", fixture.toExportXml()));
				}
			}
			fixturePw.println("</fixtures>\n");
			fixturePw.flush();

			showDefPw.println("<showDefs>\n");
			for (long showDefId : exportShowDefs) {
				logger.info("Exporting showDef " + showDefId);
				ShowDefTO showDef = showDefDAO.getShowDef(showDefId);
				ze = new ZipEntry("src/main/beanshell/" + Text.replaceString(showDef.getClassName(), ".", "/") + ".beanshell");
				zos.putNextEntry(ze);
				zos.write(showDef.getScript().getBytes());
				
				showDefPw.println(Text.indent("    ", showDef.toExportXml()));
			}
			showDefPw.println("</showDefs>\n");
			showDefPw.flush();
			
			showPw.println("<shows>\n");
			for (long stageId : exportStageShows) {
				List<ShowTO> shows = showDAO.getShows("stageId=" + stageId);
				for (ShowTO show : shows) {
					List<ShowPropertyTO> showProperties = showPropertyDAO.getShowProperties("showId=" + show.getId());
					showPw.println(Text.indent("    ", show.toExportXml(showProperties)));
				}
			}
			showPw.println("</shows>\n");
			showPw.flush();
			
			ze = new ZipEntry("src/main/resources/device.xml");
			zos.putNextEntry(ze);
			zos.write(deviceOs.toByteArray());

			ze = new ZipEntry("src/main/resources/stage.xml");
			zos.putNextEntry(ze);
			zos.write(stageOs.toByteArray());

			ze = new ZipEntry("src/main/resources/fixtureDef.xml");
			zos.putNextEntry(ze);
			zos.write(fixtureDefOs.toByteArray());

			ze = new ZipEntry("src/main/resources/fixture.xml");
			zos.putNextEntry(ze);
			zos.write(fixtureOs.toByteArray());

			ze = new ZipEntry("src/main/resources/showDef.xml");
			zos.putNextEntry(ze);
			zos.write(showDefOs.toByteArray());

			ze = new ZipEntry("src/main/resources/show.xml");
			zos.putNextEntry(ze);
			zos.write(showOs.toByteArray());
			
			ze = new ZipEntry("src/test/java/com/randomnoun/dmx/BeanshellTest.java");
			zos.putNextEntry(ze);
			InputStream tis = this.getClass().getResourceAsStream("BeanshellTest.java");
			if (tis!=null) {
				StreamUtils.copyStream(tis,  zos);
				tis.close();
			}
			
			zos.close();

			response.setContentLength(baos.size());
			response.setContentType("application/vnd.randomnoun.dmxweb-zip");
			response.setHeader("Content-Disposition", "attachment; filename=\"export-" + now2 + ".dmxweb-zip\"");
			StreamUtils.copyStream(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
			forward = "null";
			
		} else if (action.equals("import") || action.equals("import2")) {
			// @XXX: stop any shows etc
			
			ErrorList errors = new ErrorList();
			boolean upload = action.equals("import");
			String userFilename = null;
			FileItem file = null;
			if (upload) {
				file = ((FileRequestWrapper) request).getFile("importFile");
				userFilename = file.getName();
				if (userFilename.contains(":")) { userFilename = userFilename.substring(userFilename.indexOf(":")+1); }
				if (userFilename.contains("/")) { userFilename = userFilename.substring(userFilename.indexOf("/")+1); }
				if (userFilename.contains("\\")) { userFilename = userFilename.substring(userFilename.indexOf("\\")+1); }
				if (userFilename.equals("")) {
					errors.addError("Missing file", "You must enter a file to import");
				}
			} else {
				if (!request.getParameter("localFilename").equals(session.getAttribute("localFilename"))) {
					errors.addError("Invalid request", "The previously uploaded file is no longer available. Please upload again.");
				}
			}
				
			if (!errors.hasErrors()){

				// TODO: un-unicode them or something as well
				String fileUploadTempPath = appConfig.getProperty("webapp.fileUpload.tempDir");
				String localFilename;
				File f;
				if (upload) {
					logger.info("Received fileUpload (userFilename='" + userFilename + "')");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	    			localFilename = sdf.format(new Date()) + "-" + userFilename;
					f = new File(fileUploadTempPath + File.separator + localFilename);
					FileOutputStream fos = new FileOutputStream(f);
					StreamUtils.copyStream(file.getInputStream(), fos);
					fos.close();
				} else {
	    			localFilename = request.getParameter("localFilename");
					f = new File(fileUploadTempPath + File.separator + localFilename);
				}
				
				Map<String, byte[]> zipMap = new HashMap<String, byte[]>();
				FileInputStream fis = new FileInputStream(f);
				ZipInputStream zis = new ZipInputStream(fis);
				ZipEntry ze = zis.getNextEntry();
				while (ze!=null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					StreamUtils.copyStream(zis, baos);
					zipMap.put(ze.getName(), baos.toByteArray());
					ze = zis.getNextEntry();
				}
				zis.close();
				fis.close();

				List topLevel = new ArrayList();
				Map topLevelMap = newImportItem("Everything", null, null, true, false, false, null);
				topLevel.add(topLevelMap);
				
				List items = new ArrayList();
				topLevelMap.put("children", items);
				Map itemMap;

				if (zipMap.containsKey("src/main/resources/device.xml")) {
					Map deviceMap = newImportItem("Device settings", "devices", "icnDevice.png", false, true, false, "Device settings will replace existing device settings");
					deviceMap.put("header", true);
					items.add(deviceMap);
					List<DeviceTO> currentDevices = deviceDAO.getDevices(null);
					List<DeviceTO> devices = parseDevices(new ByteArrayInputStream(zipMap.get("src/main/resources/device.xml")));
					
					if (!upload && request.getParameter("devices")!=null) {
						logger.info("Removing existing devices");
						for (DeviceTO device : currentDevices) {
							// @TODO cascade deletes in the database perhaps
							logger.info("Removing device '" + device.getName() + "'");
							List<DevicePropertyTO> deviceProperties = devicePropertyDAO.getDeviceProperties("deviceId=" + device.getId());
							for (DevicePropertyTO deviceProperty : deviceProperties) {
								devicePropertyDAO.deleteDeviceProperty(deviceProperty);
							}
							deviceDAO.deleteDevice(device);
						}
						logger.info("Importing devices");
						// @TODO validate this
						for (DeviceTO device : devices) {
							logger.info("Importing device '" + device.getName() + "'");
							long deviceId = deviceDAO.createDevice(device);
							for (DevicePropertyTO deviceProperty : device.getDeviceProperties()) {
								logger.info("Importing property '" + deviceProperty.getKey() + "'");
								deviceProperty.setDeviceId(deviceId);
								devicePropertyDAO.createDeviceProperty(deviceProperty);
							}
						}
					}
				}
				
				if (zipMap.containsKey("src/main/resources/fixtureDef.xml")) {
					itemMap = newImportItem("Fixture definitions", null, "icnFixtureDef2.png", false, false, false, null);
					List itemChildren = new ArrayList();
					itemMap.put("children", itemChildren);
					List<FixtureDefTO> currentFixtureDefs = fixtureDefDAO.getFixtureDefs(null);
					List<FixtureDefTO> fixtureDefs = parseFixtureDefs(new ByteArrayInputStream(zipMap.get("src/main/resources/fixtureDef.xml")));
					for (int i=0; i<fixtureDefs.size(); i++) {
						// this is probably all pointless
						FixtureDefTO byName = (FixtureDefTO) Struct.getStructuredListObject(currentFixtureDefs, "name", fixtureDefs.get(i).getName()); // can rename these
						//List otherFixtureDefs = new ArrayList<FixtureDefTO>(currentFixtureDefs);
						//if (byName!=null) { otherFixtureDefs.remove(byName); }
						FixtureDefTO byFDCN = (FixtureDefTO) Struct.getStructuredListObject(currentFixtureDefs, "fixtureDefClassName", fixtureDefs.get(i).getFixtureDefClassName()); // not so much these. Unless it's been generated.
						FixtureDefTO byFCCN = (FixtureDefTO) Struct.getStructuredListObject(currentFixtureDefs, "fixtureControllerClassName", fixtureDefs.get(i).getFixtureControllerClassName()); // not so much these. Unless it's been generated.
						FixtureDefTO byCMCN = (FixtureDefTO) Struct.getStructuredListObject(currentFixtureDefs, "channelMuxerClassName", fixtureDefs.get(i).getChannelMuxerClassName()); // not so much these. Unless it's been generated.
						logger.info("this=" + fixtureDefs.get(i).getName() +
							", thisCMCN=" + fixtureDefs.get(i).getChannelMuxerClassName() +
							", byName=" + (byName==null ? null : byName.getName()) + 
							", byFDCN=" + (byFDCN==null ? null : byFDCN.getName()) + 
							", byFCCN=" + (byFCCN==null ? null : byFCCN.getName()) + 
							", byCMCN=" + (byCMCN==null ? null : byCMCN.getName()));
						boolean canAdd = byFDCN==null && byFCCN==null && byCMCN==null && byName==null;
						boolean canReplace = byName!=null && byName==byFDCN && byName==byFCCN && byName==byCMCN; // replace infers replaceWithRename
						itemChildren.add(newImportItem(fixtureDefs.get(i).getName(), "fix-" + fixtureDefs.get(i).getId(), "icnFixtureDef2.png", 
							canAdd, canReplace, !(canAdd || canReplace), 
							canAdd ? null :
							(canReplace ? "A fixture with this name already exists" :
							 "One of the classes in this fixture definition is used by another fixture")));
						if (!upload && request.getParameter("fix-" + fixtureDefs.get(i).getId())!=null) {
							logger.info("Importing fixtureDef '" + fixtureDefs.get(i).getName() + "'");
						}
					}
					items.add(itemMap);
				}
				
				if (zipMap.containsKey("src/main/resources/showDef.xml")) {
					itemMap = newImportItem("Show definitions", null, "icnShowDef2.png", false, false, false, null);
					List itemChildren = new ArrayList();
					itemMap.put("children", itemChildren);
					List<ShowDefTO> currentShowDefs = showDefDAO.getShowDefs(null);
					List<ShowDefTO> showDefs = parseShowDefs(new ByteArrayInputStream(zipMap.get("src/main/resources/showDef.xml")));
					for (int i=0; i<showDefs.size(); i++) {
						ShowDefTO byName = (ShowDefTO) Struct.getStructuredListObject(currentShowDefs, "name", showDefs.get(i).getName()); // can rename these
						boolean canAdd = byName==null;
						itemChildren.add(newImportItem(showDefs.get(i).getName(), "show-" + showDefs.get(i).getId(), "icnShowDef2.png",
							canAdd, !canAdd, false, canAdd ? null : "A show definition with this name already exists"));
						if (!upload && request.getParameter("show-" + showDefs.get(i).getId())!=null) {
							logger.info("Importing showDef '" + showDefs.get(i).getName() + "'");
						}
					}
					items.add(itemMap);
				}
				
				
				if (zipMap.containsKey("src/main/resources/stage.xml")) {
					itemMap = newImportItem("Stages", null, "icnStage.png", false, false, false, null);
					List itemChildren = new ArrayList();
					itemMap.put("children", itemChildren);
					List<StageTO> currentStages = stageDAO.getStages(null);
					List<StageTO> stages = parseStages(new ByteArrayInputStream(zipMap.get("src/main/resources/stage.xml")));
					for (int i=0; i<stages.size(); i++) {
						StageTO byName = (StageTO) Struct.getStructuredListObject(currentStages, "name", stages.get(i).getName()); // can rename these
						boolean canAdd = byName==null;
						// @XXX: the booleans below are a really bad idea
						Map stageItem = newImportItem(stages.get(i).getName(), "stage-" + stages.get(i).getId(), "icnStage.png", 
							canAdd, !canAdd, false, canAdd ? null : "A stage with this name already exists");
						itemChildren.add(stageItem);
						List itemChildren2 = new ArrayList();
						stageItem.put("children", itemChildren2);
						// the booleans here will change depending on the parent node selection. arg.
						itemChildren2.add(newImportItem("Fixtures", "stage-fix-" + stages.get(i).getId(), "icnFixture2.png",
							canAdd, !canAdd, false, canAdd ? null : "A stage with this name already exists")); // @TODO add fixture/show counts
						itemChildren2.add(newImportItem("Shows", "stage-show-" + stages.get(i).getId(), "icnShow.png",
							canAdd, !canAdd, false, canAdd ? null : "A stage with this name already exists"));
						
						if (!upload && request.getParameter("stage-" + stages.get(i).getId())!=null) {
							logger.info("Importing stage '" + stages.get(i).getName() + "'");
						}
						if (!upload && request.getParameter("stage-fix-" + stages.get(i).getId())!=null) {
							logger.info("Importing fixtures for stage '" + stages.get(i).getName() + "'");
						}
						if (!upload && request.getParameter("stage-show-" + stages.get(i).getId())!=null) {
							logger.info("Importing shows for stage '" + stages.get(i).getName() + "'");
						}
					}
					items.add(itemMap);
				}
				
				session.setAttribute("localFilename", localFilename); // @XXX: probably a security risk
				request.setAttribute("localFilename", localFilename);
				request.setAttribute("importItems", topLevel);
			}
		
    	} else {
			throw new IllegalArgumentException("Invalid action '" + action + "'");
		}

		
        return mapping.findForward(forward);
    }

    /** Parse a string of XML text using a SAX contentHandler. Nothing is returned by this method - it 
	 * is assumed that the contentHandler supplied maintains it's own state as it parses the XML supplied,
	 * and that this state can be extracted from this object afterwards.
	 * 
	 * @param contentHandler a SAX content handler 
	 * @param is inputstream containing the XML data
	 * 
	 * @throws SAXException if the document could not be parsed
	 * @throws IllegalException if the parser could not be initialised, or an I/O error occurred 
	 *   (should not happen since we're just dealing with strings)
	 */
	public static void processContentHandler(ContentHandler contentHandler, InputStream is) throws SAXException {
		 SAXParserFactory factory = SAXParserFactory.newInstance();
		 try {
			 SAXParser saxParser = factory.newSAXParser();
			 XMLReader xmlReader = saxParser.getXMLReader();
			 xmlReader.setContentHandler(contentHandler);
			 xmlReader.parse(new InputSource(is));
		 } catch (IOException ioe) {
		 	throw (IllegalStateException) new IllegalStateException("IO Exception reading from string").initCause(ioe);
		 } catch (ParserConfigurationException pce) {
			throw (IllegalStateException) new IllegalStateException("Could not initialise parser").initCause(pce);		 		
		 }
	}
    
	/** Create a stack-based XML parser. Similar to the apache digester, but without
	 * the dozen or so dependent JARs.
	 * 
	 * <p>Only element text is captured 
	 * <p>Element attributes are not parsed by this class.
	 * <p>Mixed text/element nodes are not parsed by this class.
	 * 
	 */
	public abstract static class AbstractStackContentHandler implements ContentHandler 
	{
		/** Logger instance for this class */
		public static final Logger logger = Logger.getLogger(AbstractStackContentHandler.class);

		/** Location in stack */
		String stack = "";
		int stackDepth = 0;
		String text = null;     // text captured so far
		
		// unused interface methods
		public void setDocumentLocator(Locator locator) { }
		public void startDocument() throws SAXException { }
		public void endDocument() throws SAXException { }
		public void startPrefixMapping(String prefix, String uri) throws SAXException { }
		public void endPrefixMapping(String prefix) throws SAXException { }
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException { }
		public void processingInstruction(String target, String data) throws SAXException { }
		public void skippedEntity(String name) throws SAXException { }

		public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXException 
		{
			stack = stack.equals("") ? qName : stack + "/" + qName;
			text = "";
			element(stack);
		}
		public void characters(char[] ch, int start, int length) throws SAXException {
			text += new String(ch, start, length);
		}
		public void endElement(String uri, String localName, String qName)
			throws SAXException 
		{
			elementText(stack, text);
			text = ""; // probably not necessary
			stack = stack.contains("/") ? stack.substring(0, stack.lastIndexOf("/")) : "";
		}
		public abstract void element(String path) throws SAXException;
		public abstract void elementText(String path, String content) throws SAXException;
		//public abstract Object getResult();
	}
    
	private void parseXml(InputStream is, ContentHandler contentHandler) throws IOException, ParseException {
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	SAXParser saxParser;
    	XMLReader xmlReader;
		try {
			saxParser = factory.newSAXParser();
			xmlReader = saxParser.getXMLReader(); // seems pointless
		} catch (Exception e) {
			throw new IllegalStateException("Could not create SAX parser", e);
		}
		xmlReader.setContentHandler(contentHandler);
		try {
			xmlReader.parse(new InputSource(is));
		} catch (SAXException se) {
			throw (ParseException) new ParseException("Could not parse XML", -1).initCause(se);
		}
	}
	
	public static class DeviceContentHandler extends AbstractStackContentHandler {
		List<DeviceTO> result = new ArrayList<DeviceTO>();
		//List<DevicePropertyTO> properties = null; // not used
		DeviceTO d = null;
		DevicePropertyTO prop = null;
		Pattern p1 = Pattern.compile("^devices/device/(name|className|type|active|universeNumber)$");
		Pattern p2 = Pattern.compile("^devices/device/deviceProperties/deviceProperty/(key|value)$");
		public void element(String path) throws SAXException {
			//logger.info("Parsing '" + path + "'");
			if (stack.equals("devices/device")) {
				d = new DeviceTO();
				d.setDeviceProperties(new ArrayList<DevicePropertyTO>());
				result.add(d);
			} else if (stack.equals("devices/device/deviceProperties")) {
				// properties = new ArrayList<DevicePropertyTO>();
			} else if (stack.equals("devices/device/deviceProperties/deviceProperty")) {
				prop = new DevicePropertyTO();
				d.getDeviceProperties().add(prop);
			}
		}
		public void elementText(String path, String content) throws SAXException {
			//logger.info("Parsing text in '" + path + "'");
			Matcher m1 = p1.matcher(stack);
			if (m1.matches()) {
				Struct.setValue(d, m1.group(1), content, false, true, false);
			} else {
				Matcher m2 = p2.matcher(stack);
				if (m2.matches()) {
					Struct.setValue(prop, m2.group(1), content, false, true, false);
				}
			}
		}
	}
	
    private List<DeviceTO> parseDevices(ByteArrayInputStream is) throws IOException, ParseException {
    	DeviceContentHandler dch = new DeviceContentHandler();
    	parseXml(is, dch);
    	return dch.result;
	}
    
	
	public static class FixtureDefContentHandler extends AbstractStackContentHandler {
		List<FixtureDefTO> result = new ArrayList<FixtureDefTO>();
		List<FixtureDefImageTO> images = new ArrayList<FixtureDefImageTO>(); // not used 
		FixtureDefTO fd = null;
		FixtureDefImageTO fdi = null;
		Pattern p1 = Pattern.compile("^fixtureDefs/fixtureDef/(id|name|fixtureDefClassName|fixtureControllerClassName|channelMuxerClassName|dmxChannels)$");
		Pattern p2 = Pattern.compile("^fixtureDefs/fixtureDef/fixtureDefImages/fixtureDefImage/(fixtureDefId|name|description|size|contentType)$");
		public void element(String path) throws SAXException {
			//logger.info("Parsing '" + path + "'");
			if (stack.equals("fixtureDefs/fixtureDef")) {
				fd = new FixtureDefTO();
				result.add(fd);
			} else if (stack.equals("fixtureDefs/fixtureDef/fixtureDefImages/fixtureDefImage")) {
				fdi = new FixtureDefImageTO();
			}
		}
		public void elementText(String path, String content) throws SAXException {
			//logger.info("Parsing text in '" + path + "'");
			Matcher m = p1.matcher(stack);
			if (m.matches()) {
				Struct.setValue(fd, m.group(1), content, false, true, false);
			} else {
				Matcher m2 = p2.matcher(stack);
				if (m.matches()) {
					Struct.setValue(fd, m.group(1), content, false, true, false);
				} 
			}
		}
	}
	
    private List<FixtureDefTO> parseFixtureDefs(ByteArrayInputStream is) throws IOException, ParseException {
    	FixtureDefContentHandler fdch = new FixtureDefContentHandler();
    	parseXml(is, fdch);
    	return fdch.result;
	}

    public static class ShowDefContentHandler extends AbstractStackContentHandler {
		List<ShowDefTO> result = new ArrayList<ShowDefTO>();
		ShowDefTO sd = null;
		Pattern p1 = Pattern.compile("^showDefs/showDef/(id|name|className|javadoc|isRecorded)$");
		public void element(String path) throws SAXException {
			//logger.info("Parsing '" + path + "'");
			if (stack.equals("showDefs/showDef")) {
				sd = new ShowDefTO();
				result.add(sd);
			}
		}
		public void elementText(String path, String content) throws SAXException {
			//logger.info("Parsing text in '" + path + "'");
			Matcher m = p1.matcher(stack);
			if (m.matches()) {
				Struct.setValue(sd, m.group(1).equals("isRecorded") ? "recorded" : m.group(1), content, false, true, false);
			}
		}
	}
	
    private List<ShowDefTO> parseShowDefs(ByteArrayInputStream is) throws IOException, ParseException {
    	ShowDefContentHandler sdch = new ShowDefContentHandler();
    	parseXml(is, sdch);
    	return sdch.result;
	}
    
    public static class StageContentHandler extends AbstractStackContentHandler {
		List<StageTO> result = new ArrayList<StageTO>();
		StageTO s = null;
		Pattern p1 = Pattern.compile("^stages/stage/(id|name|filename|active|fixPanelBackgroundImage)$");
		public void element(String path) throws SAXException {
			//logger.info("Parsing '" + path + "'");
			if (stack.equals("stages/stage")) {
				s = new StageTO();
				result.add(s);
			}
		}
		public void elementText(String path, String content) throws SAXException {
			//logger.info("Parsing text in '" + path + "'");
			Matcher m = p1.matcher(stack);
			if (m.matches()) {
				Struct.setValue(s, m.group(1), content, false, true, false);
			}
		}
	}
	
    private List<StageTO> parseStages(ByteArrayInputStream is) throws IOException, ParseException {
    	StageContentHandler sch = new StageContentHandler();
    	parseXml(is, sch);
    	return sch.result;
	}

    
	public String getMicrosoftPreamble(String tableName, String now) {
    	return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		    "<dataroot xmlns:od=\"urn:schemas-microsoft-com:officedata\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:noNamespaceSchemaLocation=\"" + tableName + ".xsd\" generated=\"" + now + "\">\n";
    }
    
	// new item with replace
	/**
	 * 
	 * @param text description of item
	 * @param name HTML name of checkbox element
	 * @param image icon image
	 * @param canAdd if true, item can be imported as-is with no conflicts
	 * @param canAddWithRename if true, item can be imported if it is renamed
	 * @param canReplace if ture, item can be imported to replace an existing item
	 * 
	 * @param reason if non-null, gives reason for canAdd values
	 * 
	 * @return
	 */
	public Map<String, Object> newImportItem(String text, String name, String image, boolean canAdd, boolean canReplace, boolean showError, String reason) {
    	Map<String, Object> m = new HashMap<String, Object>();
    	m.put("text", text);
    	if (name!=null) { m.put("name", name); }
    	if (image!=null) { m.put("image", image); }
    	m.put("canAdd", canAdd); 
    	m.put("canReplace", canReplace);
    	m.put("showError", showError);
    	m.put("reason", reason);
    	return m;
    }

	/*
	public Map newImportHeader(String text, String name, String image) {
    	Map m = new HashMap();
    	m.put("text", text);
    	if (name!=null) { m.put("name", name); }
    	if (image!=null) { m.put("image", image); }
    	m.put("header", true);
    	m.put("canAdd", true); 
    	return m;
    }
    */
	
    public Map<String, Object> newItem(String text, String name, String image) {
    	Map<String, Object> m = new HashMap<String, Object>();
    	m.put("text", text);
    	if (name!=null) { m.put("name", name); }
    	if (image!=null) { m.put("image", image); }
    	return m;
    }
    
    public Map<String, Object> newItem(String text) {
    	return newItem(text, null, null); 
    }
    
    

    
}
