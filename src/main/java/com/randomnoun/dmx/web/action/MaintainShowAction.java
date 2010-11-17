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
import com.randomnoun.common.security.User;
import com.randomnoun.common.spring.StructuredResultReader;
import com.randomnoun.dmx.config.AppConfig;
import com.randomnoun.dmx.dao.ShowDAO;
import com.randomnoun.dmx.dao.ShowDefDAO;
import com.randomnoun.dmx.to.ShowTO;
import com.randomnoun.dmx.web.Table;
import com.randomnoun.dmx.web.TableEditor;
import com.randomnoun.dmx.web.TableEditor.TableEditorResult;


/**
 * Stock entry action
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class MaintainShowAction
    extends Action {
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
    		new String[] { "id", "showDefId", "name", "onCompleteShowId", "onCancelShowId" };
    	
    	// @TODO this is all rather silly...
    	
    	@Override
		public void createRow(Map row) throws Exception {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		ShowTO show = new ShowTO();
    		Struct.setFromMap(show, row, false, true, false, fieldNames);
    		showDAO.createShow(show);
		}

		@Override
		public void updateRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		ShowTO show = new ShowTO();
    		Struct.setFromMap(show, row, false, true, false, fieldNames);
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
    			AppConfig.getAppConfig().reloadFixturesAndShows();
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
			return form;
    	}
    	
    	public List getShows() {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		ShowDAO showDAO = new ShowDAO(jt);
    		List<ShowTO> shows = showDAO.getShows(null);
    		// holy freaking christ. This is 12 types of wrong. Or 1:02AM types of wrong. Take your pick.
    		List showsAsMaps = new ArrayList();
    		for (ShowTO show : shows) {
    			Map showMap = new HashMap();
    			Struct.setFromObject(showMap, show, false, true, true, fieldNames);
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
		
		if (action==null) { action = ""; }
		if (action.equals("")) {
			// default action displays entry page
			ShowTableEditor tableEditor = new ShowTableEditor();
			request.setAttribute("form", tableEditor.readShows(null));
			
		} else if (action.equals("maintain")) {
			Map form = new HashMap();
			Struct.setFromRequest(form, request);
			
			//System.out.println(Struct.structuredMapToString("form", form));
			ShowTableEditor tableEditor = new ShowTableEditor();
			tableEditor.removeEmptyRows(form);
			TableEditorResult result = tableEditor.maintainShows(form);
			//System.out.println("======================================");
			//System.out.println(Struct.structuredListToString("rows", result.getRows()));
			//System.out.println(Struct.structuredListToString("errors", result.getErrors()));
			form.put("shows", result.getRows());
			form.put("shows_size", result.getRows().size());
			form.put("showDefs", tableEditor.getShowDefs());
			form.put("followupShows", tableEditor.getFollowupShows());
			request.setAttribute("errors", result.getErrors());
			request.setAttribute("form", form);
			
			
		} else {
			throw new IllegalArgumentException("Invalid action '" + action + "'");
		}

		
        return mapping.findForward(forward);
    }
    
}
