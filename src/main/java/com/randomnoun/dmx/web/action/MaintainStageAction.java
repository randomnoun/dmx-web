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
import com.randomnoun.dmx.dao.StageDAO;
import com.randomnoun.dmx.to.StageTO;
import com.randomnoun.dmx.web.Table;
import com.randomnoun.dmx.web.TableEditor;
import com.randomnoun.dmx.web.TableEditor.TableEditorResult;
import com.randomnoun.dmx.web.struts.ActionBase;
import com.randomnoun.dmx.web.struts.DmxHttpRequest;


/**
 * Stage entry action.
 *
 * Forwards generated by this action:
 * <attributes>
 * success - displays entry page
 * </attributes>
 *
 * @version         $Id$
 * @author          knoxg
 */
public class MaintainStageAction extends ActionBase {
    /** A revision marker to be used in exception stack traces. */
    public static final String _revision = "$Id$";

    /** Logger instance for this class */
    private static final Logger logger = Logger.getLogger(MaintainStageAction.class);

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
    		new String[] { "id", "name", "fixPanelBackgroundImage", "active" };
    	
    	@Override
		public void createRow(Map row) throws Exception {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		StageDAO stageDAO = new StageDAO(jt);
    		StageTO stage = new StageTO();
    		Struct.setFromMap(stage, row, false, true, false, fieldNames);
    		stageDAO.createStage(stage);
		}

		@Override
		public void updateRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
			StageDAO stageDAO = new StageDAO(jt);
			StageTO stage = new StageTO();
    		Struct.setFromMap(stage, row, false, true, false, fieldNames);
    		stageDAO.updateStage(stage);
		}

		@Override
		public void deleteRow(Map row) throws Exception {
			JdbcTemplate jt = AppConfig.getAppConfig().getJdbcTemplate();
			StageDAO stageDAO = new StageDAO(jt);
			StageTO stage = new StageTO();
    		Struct.setFromMap(stage, row, false, true, false, fieldNames);
    		stageDAO.deleteStage(stage);
		}

		private void init() {
			/*
			this.fixtureDefMap = getFixtureDefMap();
			for (int i=0; i<Universe.MAX_CHANNELS; i++) {
				occupiedDmxValues[i]=-1;
			}
			*/
    	}

    	public void removeEmptyRows(Map form) {
    		removeEmptyRows(form, fieldNames, "stages");
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
    	    valid = valid & table.checkMandatory("name", 100, "Name");
    	    return valid;
    	}
    	  
    	  
    	public TableEditorResult maintainStages(Map request) {
    		TableEditorResult result;
		    List updateErrors;
		    
		    table = new Table(request, "stages", "id", Long.class);
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
    			result.setRows(getStages());
    			ErrorList errors = getTable().getErrors();
    			errors.addError("Stages updated", "Table has been updated", ErrorList.SEVERITY_OK);
    			result.setErrors(errors);

    			long startTime = System.currentTimeMillis();
	    		AppConfig.getAppConfig().reloadDevicesFixturesAndShows(false);
	    		logger.info("stage reload time=" + ((System.currentTimeMillis() - startTime)/1000.0) + " sec");
    			
    		}
    		return result;
    	}
    	
    	public Map readFixtures(Map request) {
			Map form = new HashMap();
			List stages = getStages();
			form.put("stages", stages);
			form.put("stages_size", stages.size());
			return form;
    	}
    	
    	public List getStages() {
    		AppConfig appConfig = AppConfig.getAppConfig();
    		JdbcTemplate jt = appConfig.getJdbcTemplate();
    		StageDAO stageDAO = new StageDAO(jt);
    		List<StageTO> stages = stageDAO.getStages(null);
    		// holy freaking christ. This is 12 types of wrong. Or 1:02AM types of wrong. Take your pick.
    		List stagesAsMaps = new ArrayList();
    		for (StageTO stage : stages) {
    			Map stageMap = new HashMap();
    			Struct.setFromObject(stageMap, stage, false, true, true, fieldNames);
    			stagesAsMaps.add(stageMap);
    		}
    		return stagesAsMaps;
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
		
		if (action==null) { action = ""; }
		if (action.equals("")) {
			// default action displays entry page
			FixtureTableEditor tableEditor = new FixtureTableEditor();
			request.setAttribute("form", tableEditor.readFixtures(null));
			
		} else if (action.equals("maintain")) {
			Map form = new HashMap();
			Struct.setFromRequest(form, request);
			
			//System.out.println(Struct.structuredMapToString("form", form));
			FixtureTableEditor tableEditor = new FixtureTableEditor();
			tableEditor.removeEmptyRows(form);
			TableEditorResult result = tableEditor.maintainStages(form);
			//System.out.println("======================================");
			//System.out.println(Struct.structuredListToString("rows", result.getRows()));
			//System.out.println(Struct.structuredListToString("errors", result.getErrors()));
			form.put("stages", result.getRows());
			form.put("stages_size", result.getRows().size());
			request.setAttribute("errors", result.getErrors());
			request.setAttribute("form", form);
			
		} else {
			throw new IllegalArgumentException("Invalid action '" + action + "'");
		}

		
        return forward;
    }
    
}
