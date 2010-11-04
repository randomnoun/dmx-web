<%@ page 
  language="java"
  contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"
  errorPage="../misc/errorPage.jsp"
%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%-- 
  TODO: use tables less, use styles instead.
--%>
<c:set var="countInfo" value="0" />
<c:set var="countError" value="0" />
<span id="serverErrorLayer">
<c:forEach var="error" items="${errors}">
<c:choose><c:when test="${error.severity == 0}"><c:set var="countInfo" value="${countInfo + 1}"/></c:when>
<c:otherwise><c:set var="countError" value="${countError + 1}"/></c:otherwise></c:choose></c:forEach>
<c:if test="${countInfo>0}">
<style>
  .whitebg { background-color: #FFFFFF; color: #000044; }
  .lbbg { background-color: #EEEEFF; color: #000044; }
</style>
<table style="border-collapse: separate; width: 100%; margin: 0px 0px 5px 0px;" bgcolor="#FFFFFF" border="0" cellspacing="0" cellpadding="3" ><tr><td>
<table style="border-collapse: separate; width: 100%; margin: 0px; border: solid blue 1px;" bgcolor="#EEEEFF" border="1" bordercolor="#000088" cellspacing="0" cellpadding="0"><tr><td>
<table style="border-collapse: separate; width: 100%; margin: 0px;" bgcolor="#EEEEFF" border="0" cellspacing="0" cellpadding="0">
  <tr class="headerinfo" style="height:3px;"><td colspan=5 height="3px" class="lbbg" style="height: 3px; font-size: 1pt;"><img src="image/spacer.gif" width=1 height=3></td></tr>
	<c:forEach var="error" items="${errors}">
	<c:if test="${error.severity == 0}">
  <tr class="headerinfo"> 
    <td width="2" valign="top" class="lbbg" ><img src="image/spacer.gif" width=2 height=1></td> 
    <td width="10" valign="top" class="lbbg" ><img src="image/infodot2.gif" width=15 height=15></td>
	<td width="5" valign="top" class="lbbg" ><img src="image/spacer.gif" width=5 height=1></td>
    <td nowrap bgcolor="#EEEEFF" valign="top" class="lbbg"> <b><c:out value="${error.shortText}" default="Info" /></b>&nbsp;&nbsp; </td>
	<td bgcolor="#EEEEFF" valign="top" width="100%" class="lbbg"> <c:out value="${error.longText}" default="No information supplied" /> 
    </td>
  </tr>
	</c:if>
	</c:forEach>
	<tr class="headerinfo" style="height:3px;"><td colspan=5 height=3 class="lbbg" style="height: 3px; font-size: 1pt;"><img src="image/spacer.gif" width=1 height=3></td></tr>
</table>
</td></tr>
</table>
</td></tr>
</table>
</c:if>


<c:if test="${countError>0}">
<style>
  .whitebg { background-color: #FFFFFF; color: #440000; }
  .lrbg { background-color: #FFEEEE; color: #440000; }
</style>
<table style="border-collapse: separate; width: 100%; margin: 0px 0px 5px 0px;" border="0" cellspacing="0" cellpadding="3"><tr><td class="whitebg">
<table style="border-collapse: separate; width: 100%; margin: 0px; border: solid red 1px;" border="1" bordercolor="#FF0000" cellspacing="0" cellpadding="0"><tr><td>
<table style="border-collapse: separate; width: 100%; margin: 0px;" border="0" cellspacing="0" cellpadding="0">
  <tr class="headererror" style="height:3px;"><td colspan=3 height="3px" class="lrbg" style="height: 3px; font-size: 1pt;"><img src="image/spacer.gif" width=1 height=3></td></tr>
	<c:forEach var="error" items="${errors}">
	<c:if test="${error.severity != 0}">
  <tr class="headererror"> 
    <td width="17" valign="top" class="lrbg"><img src="image/errordot.gif" width=15 height=15 style="margin-left:2px; margin-right: 5px;"/></td>
	<td nowrap valign="top" class="lrbg"> <b><c:out value="${error.shortText}" default="Error" /></b>&nbsp;&nbsp; </td>
    <td valign="top" width="100%" class="lrbg"> <c:out value="${error.longText}" default="An error has occurred" /> 
    </td>
  </tr>
	</c:if>
	</c:forEach>
	<tr class="headererror" style="height:3px"><td colspan=3 height="3px" class="lrbg" style="height: 3px; font-size: 1pt;"><img src="image/spacer.gif" width=1 height=3></td></tr>
</table>
</td></tr>
</table>
</td></tr>
</table>
</c:if>
</span>

