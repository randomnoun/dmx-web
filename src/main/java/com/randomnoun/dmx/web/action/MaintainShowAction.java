package com.randomnoun.dmx.web.action;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.randomnoun.common.ErrorList;
import com.randomnoun.common.Struct;
import com.randomnoun.common.security.User;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.ShowDAO;
import com.randomnoun.dmx.dao.ShowDefDAO;
import com.randomnoun.dmx.to.ShowTO;
import com.randomnoun.dmx.web.Table;
import com.randomnoun.dmx.web.TableEditor;
import com.randomnoun.dmx.web.TableEditor.TableEditorResult;
import com.randomnoun.dmx.web.struts.ActionBase;
import com.randomnoun.dmx.web.struts.DmxHttpRequest;


/**
 * Show entry action.
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class MaintainShowAction extends ActionBase {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(MaintainShowAction.class);

	public Map getParameterMap(HttpServletRequest request) {
		Map map = new HashMap();
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String param = (String) e.nextElement();
			map.put(param, request.getParameter(param));
		}
		return map;
	}



    public static class ShowTableEditor extends TableEditor {

    	private final static String[] fieldNames = 
    		new String[] { "id", "showDefId", "name", "onCompleteShowId", "onCancelShowId", "showGroupId" };

    	private final static String[] fieldNames2 = 
    		new String[] { "id", "showDefId", "name", "onCompleteShowId", "onCancelShowId", "showGroupId", "showPropertyCount" };

    	long activeStageId = -1;
		
    	public ShowTableEditor(long activeStageId) {
    		this.activeStageId = activeStageId;
    	}
    	
    	// @TODO this is all rather silly...
    	
    	@Override
		public void createRow(Map row) throws Exception {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		ShowTO show = new ShowTO();
    		Struct.setFromMap(show, row, false, true, false, fieldNames);
    		show.setStageId(activeStageId);
    		showDAO.createShow(show);
		}

		@Override
		public void updateRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		ShowTO show = new ShowTO();
    		Struct.setFromMap(show, row, false, true, false, fieldNames);
    		show.setStageId(activeStageId);
    		showDAO.updateShow(show);
		}

		@Override
		public void deleteRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		ShowTO show = new ShowTO();
    		Struct.setFromMap(show, row, false, true, false, fieldNames);
    		showDAO.deleteShow(show);
		}

		private void init() {
    	}

    	public void removeEmptyRows(Map form) {
    		removeEmptyRows(form, fieldNames, "shows");
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
    	    valid = valid & table.checkMandatory("showDefId", 10, "Show type"); // @TODO check against IDs in DB
    	    valid = valid & table.checkNumeric("showDefId", "Show type");
    	    valid = valid & table.checkMandatory("name", 100, "Name");
    	    //valid = valid & table.checkMandatory("onCancelShowDefId", 10, "DMX offset");
    	    valid = valid & table.checkNumeric("onCancelShowId", "On cancel goto");
    	    valid = valid & table.checkNumeric("onCompleteShowId", "On complete goto");
    	    valid = valid & table.checkNumeric("showGroupId", "Show group");
    	    return valid;
    	}
    	  
    	  
    	public TableEditorResult maintainShows(Map request) {
    		TableEditorResult result;
		    List updateErrors;
		    
		    table = new Table(request, "shows", "id", String.class);
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
    			result.setRows(getShows());
    			ErrorList errors = getTable().getErrors();
    			errors.addError("Shows updated", "Table has been updated", ErrorList.SEVERITY_OK);
    			result.setErrors(errors);

    			long startTime = System.currentTimeMillis();
	    		AppConfig.getAppConfig().reloadShows();
	    		logger.info("show reload time=" + ((System.currentTimeMillis() - startTime)/1000.0) + " sec");
    		}
    		return result;
    	}
    	
    	public Map readShows(Map request) {
			Map form = new HashMap();
			List shows = getShows();
			form.put("shows", shows);
			form.put("shows_size", shows.size());
			form.put("showDefs", getShowDefs());
			form.put("followupShows", getFollowupShows());
			form.put("showGroups", getShowGroups());
			return form;
    	}
    	
    	public List getShows() {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		List<ShowTO> shows = showDAO.getShowsWithPropertyCounts("stageId=" + activeStageId);
    		// holy freaking christ. This is 12 types of wrong. Or 1:02AM types of wrong. Take your pick.
    		List showsAsMaps = new ArrayList();
    		for (ShowTO show : shows) {
    			Map showMap = new HashMap();
    			Struct.setFromObject(showMap, show, false, true, true, fieldNames2);
    			showsAsMaps.add(showMap);
    		}
    		return showsAsMaps;
    	}
    	
    	public List getShowDefs() {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		ShowDefDAO showDefDAO = new ShowDefDAO(jt);
    		return showDefDAO.getShowDefs(null);
    	}
    	
    	public List getFollowupShows() {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		return showDAO.getShows(null);
    	}

    	// @TODO deal with stageIds here
    	public List getShowGroups() {
    		List showGroups = new ArrayList();
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		Long lastShow = showDAO.getLastShowGroupId();
    		long lastShowPlusOne = (lastShow==null ? 1 : lastShow.longValue() + 1);
    		for (int i=1; i<=lastShowPlusOne; i++) {
    			Map row = new HashMap();
    			row.put("name", "Group " + i);
    			row.put("id", new Long(i));
    			showGroups.add(row);
    		}
    		return showGroups;
    	}
    	
    	
   }
      
      
    
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
		JdbcTemplate jt = appConfig.getJdbcTemplate();
		String action = request.getParameter("action");
		
		// @TODO deal with no active stages
		if (appConfig.getActiveStage()==null) {
			request.setAttribute("initMessage", "You must create a stage before adding shows");
			forward = "cnfPanel";
		} else {
			long activeStageId = appConfig.getActiveStage().getId();
			
			// if errors generated from show properties page
			if (request.getAttribute("errors")!=null) { action=""; }
			
			if (action==null) { action = ""; }
			if (action.equals("")) {
				// default action displays entry page
				ShowTableEditor tableEditor = new ShowTableEditor(activeStageId);
				request.setAttribute("form", tableEditor.readShows(null));
				
			} else if (action.equals("maintain") || action.equals("editProperties")) {
				Map form = new HashMap();
				Struct.setFromRequest(form, request);
				
				//System.out.println(Struct.structuredMapToString("form", form));
				ShowTableEditor tableEditor = new ShowTableEditor(activeStageId);
				tableEditor.removeEmptyRows(form);
				TableEditorResult result = tableEditor.maintainShows(form);
				//System.out.println("======================================");
				//System.out.println(Struct.structuredListToString("rows", result.getRows()));
				//System.out.println(Struct.structuredListToString("errors", result.getErrors()));
				form.put("shows", result.getRows());
				form.put("shows_size", result.getRows().size());
				form.put("showDefs", tableEditor.getShowDefs());
				form.put("followupShows", tableEditor.getFollowupShows());
				form.put("showGroups", tableEditor.getShowGroups());
				request.setAttribute("errors", result.getErrors());
				request.setAttribute("form", form);
				
				if (!result.getErrors().hasErrors(ErrorList.SEVERITY_INVALID) && action.equals("editProperties")) {
					forward = "showProperties";
				}
				
			} else {
				throw new IllegalArgumentException("Invalid action '" + action + "'");
			}
		}
		
        return forward;
    }
    
}
