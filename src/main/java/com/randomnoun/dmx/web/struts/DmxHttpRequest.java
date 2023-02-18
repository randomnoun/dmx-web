package com.randomnoun.dmx.web.struts;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Logger;

import com.randomnoun.common.StreamUtil;

public class DmxHttpRequest extends SafeHttpRequest {

	private byte[] wrappedRequestBody;
	public Logger logger = Logger.getLogger(DmxHttpRequest.class);

	
	public DmxHttpRequest(HttpServletRequest request) {
		// if we don't have a session at this point, should probably raise an exception
		super(request);
	}
	public byte[] getWrappedRequestBody() throws IOException {
		HttpServletRequest wrappedRequest = (HttpServletRequest) super.getRequest();
		if (wrappedRequestBody==null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamUtil.copyStream(wrappedRequest.getInputStream(), baos);
			baos.flush();
			wrappedRequestBody = baos.toByteArray();
			logger.info("wrb.size=" + wrappedRequestBody.length);
		}
		return wrappedRequestBody;
	}
	
	// things that are stored in the session, but we access through the request
	public static void copySessionDataToRequest(HttpSession session, HttpServletRequest request) {
		/*
		request.setAttribute("user",  session.getAttribute("user"));
		request.setAttribute("userConfig",  session.getAttribute("userConfig"));
		request.setAttribute("requestIp",  session.getAttribute("requestIp"));
		request.setAttribute("security",  session.getAttribute("security"));
		request.setAttribute("securityRoles",  session.getAttribute("securityRoles"));
		request.setAttribute("workspaceId",  session.getAttribute("workspaceId"));
		request.setAttribute("userSessionId",  session.getAttribute("userSessionId"));
		*/
	}
	
	// attributes mirrored from the session
	// public User getUser() { return (User) this.getAttribute("user"); }
	// public UserConfig getUserConfig() { return (UserConfig) this.getAttribute("userConfig"); }
	
	/*
	public String getRequestIp() { return (String) this.getAttribute("user"); }
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getSecurity() { return (Map<String, Map<String, Object>>) this.getAttribute("security"); }
	public List getSecurityRoles() { return (List) this.getAttribute("securityRoles"); }
	public Long getWorkspaceId() { return (Long) this.getAttribute("workspaceId"); }
	public Long getUserSessionId() { return (Long) this.getAttribute("userSessionId"); }
	*/
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(getWrappedRequestBody());
		return new ServletInputStream() {
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new StringReader(new String(getWrappedRequestBody(), this.getCharacterEncoding())));
	}
}
