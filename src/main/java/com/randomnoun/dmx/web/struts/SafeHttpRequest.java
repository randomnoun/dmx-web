package com.randomnoun.dmx.web.struts;

import java.util.Enumeration;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class SafeHttpRequest extends HttpServletRequestWrapper {

	public SafeHttpRequest(HttpServletRequest request) {
		super(request);
	}
	/** will eventually do that ESAPI stuff, but for now */
	
	public String getParameterUnsafe(String name){
		return super.getParameter(name);
	}
	@SuppressWarnings("unchecked")
	public Enumeration<String> getParameterNamesUnsafe() {
		return super.getParameterNames();
	}
	public String[] getParameterValuesUnsafe(String name){
		return super.getParameterValues(name);
	}
	@SuppressWarnings("unchecked")
	public Map<String, String[]> getParameterMapUnsafe() {
		return super.getParameterMap();
	}
	public Cookie[] getCookiesUnsafe() {
		return super.getCookies();
	}
	public String getHeaderUnsafe(String name) {
		return super.getHeader(name);
	}
	@SuppressWarnings("unchecked")
	public Enumeration<String> getHeadersUnsafe(String name) {
		return super.getHeaders(name);
	}
	@SuppressWarnings("unchecked")
	public Enumeration<String> getHeaderNamesUnsafe() {
		return super.getHeaderNames();
	}



}
