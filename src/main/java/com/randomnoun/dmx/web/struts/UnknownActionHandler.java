package com.randomnoun.dmx.web.struts;

import org.apache.log4j.Logger;
import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.UnknownHandler;
import org.apache.struts2.config.ConfigurationManager;
import org.apache.struts2.config.RuntimeConfiguration;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.result.Result;

/** Struts could have chosen to return 404s in response to unknown URLs, like every other 
 * framework on the planet, but no, they had to go and create UnknownHandlers instead.
 * 
 * @author knoxg
 */
public class UnknownActionHandler implements UnknownHandler {

	Logger logger = Logger.getLogger(UnknownActionHandler.class);
	
	@Override
	public ActionConfig handleUnknownAction(String namespace, String actionName) throws StrutsException {
		// modified from http://www.jcgonzalez.com/struts2-create-custom-not-found-page-404
		// (downloaded by knoxg on 2017-02-09)
		logger.warn("UnknownActionHandler.handleUnknownAction() called; namespace='" + namespace + "', actionName='" + actionName + "'");
		ConfigurationManager configurationManager = Dispatcher.getInstance().getConfigurationManager();
        RuntimeConfiguration runtimeConfiguration = configurationManager.getConfiguration().getRuntimeConfiguration();
        ActionConfig actionConfig = runtimeConfiguration.getActionConfig(namespace, actionName);
        if(actionConfig == null) { // invalid url request, and this need to be handled
            actionConfig = runtimeConfiguration.getActionConfig("/app", "404");
        }
        logger.info("actionConfig=" + actionConfig);
        return actionConfig;
    }

	@Override
	public Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig,
			String resultCode) throws StrutsException {
		logger.warn("UnknownActionHandler.handleUnknownResult() called; actionContext='" + actionContext + "', " +
			"actionName='" + actionName + "', actionConfig='" + actionConfig + "', resultCode='" + resultCode + "'");
		return null;
	}

	@Override
	public Object handleUnknownActionMethod(Object action, String methodName) {
		logger.warn("UnknownActionHandler.handleUnknownActionMethod() called; action='" + action + "', methodName='" + methodName + "'");
		return null;
	}

	

}
