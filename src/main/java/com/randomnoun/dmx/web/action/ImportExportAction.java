package com.randomnoun.dmx.web.action;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.springframework.jdbc.core.JdbcTemplate;

import com.randomnoun.common.StreamUtils;
import com.randomnoun.common.Text;
import com.randomnoun.common.security.User;
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
			items.add(newItem("Device settings", "devices"));
			
			Map itemMap = newItem("Fixture definitions");
			List itemChildren = new ArrayList();
			itemMap.put("children", itemChildren);
			List<FixtureDefTO> fixtureDefs = fixtureDefDAO.getFixtureDefs(null);
			for (int i=0; i<fixtureDefs.size(); i++) {
				itemChildren.add(newItem(fixtureDefs.get(i).getName(), "fix-" + fixtureDefs.get(i).getId()));
			}
			items.add(itemMap);
			
			itemMap = newItem("Show definitions");
			itemChildren = new ArrayList();
			itemMap.put("children", itemChildren);
			List<ShowDefTO> showDefs = showDefDAO.getShowDefs(null);
			for (int i=0; i<showDefs.size(); i++) {
				itemChildren.add(newItem(showDefs.get(i).getName(), "show-" + showDefs.get(i).getId()));
			}
			items.add(itemMap);
			
			itemMap = newItem("Stages");
			itemChildren = new ArrayList();
			itemMap.put("children", itemChildren);
			List<StageTO> stages = stageDAO.getStages(null);
			for (int i=0; i<stages.size(); i++) {
				Map stageItem = newItem(stages.get(i).getName());
				itemChildren.add(stageItem);
				List itemChildren2 = new ArrayList();
				stageItem.put("children", itemChildren2);
				itemChildren2.add(newItem("Fixtures", "stage-fix-" + stages.get(i).getId())); // @TODO add fixture/show counts
				itemChildren2.add(newItem("Shows", "stage-show-" + stages.get(i).getId()));
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
			PrintWriter pw = new PrintWriter(zos);
			pw.println("<pom>This is the pom file</pom>");
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
					devicePw.println(Text.indent("    ", device.toExportXml(deviceProperties)));
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
					InputStream is = fixtureDefImageDAO.loadImage(fdi);
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
			
			zos.close();

			response.setContentLength(baos.size());
			response.setContentType("application/vnd.randomnoun.dmxweb-zip");
			response.setHeader("Content-Disposition", "attachment; filename=\"export-" + now2 + ".dmxweb-zip\"");
			StreamUtils.copyStream(new ByteArrayInputStream(baos.toByteArray()), response.getOutputStream());
			forward = "null";
			
		} else {
			throw new IllegalArgumentException("Invalid action '" + action + "'");
		}

		
        return mapping.findForward(forward);
    }

    public String getMicrosoftPreamble(String tableName, String now) {
    	return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		    "<dataroot xmlns:od=\"urn:schemas-microsoft-com:officedata\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:noNamespaceSchemaLocation=\"" + tableName + ".xsd\" generated=\"" + now + "\">\n";
    }
    
    public Map newItem(String text, String name) {
    	Map m = new HashMap();
    	m.put("text", text);
    	if (name!=null) { m.put("name", name); }
    	return m;
    }
    
    public Map newItem(String text) {
    	return newItem(text, null); 
    }

    
}
