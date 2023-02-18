package com.randomnoun.dmx.web.struts;

import org.apache.struts2.result.StrutsResultSupport;

import com.opensymphony.xwork2.ActionInvocation;

public class NullResult extends StrutsResultSupport {

	/** generated serialVersionUID */
	private static final long serialVersionUID = 2101542086876381559L;

	@Override
	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {
		// absolutely nothing
	}

}
