<html>
<head>
<style>
#controller { font-size: 8pt; font-family: Arial;}
#controller TD { font-size: 8pt; font-family: Arial;}
#controller INPUT { font-size: 8pt; }
.label { width: 25px; height: 16px; text-align: right; background-color: lightblue; padding-top: 3px; margin-left: 3px; margin-bottom: 1px;}
</style>
</head>
<body>
<h2>DMX Web</h2>
<h3>Configuration:</h3>
<table>
<tr><td>DMX DLL version</td><td>something</td></tr>
<tr><td>DMX DLL loaded</td><td>OK</td></tr>
<tr><td>Device settings</td><td>something</td></tr>
</table>

<h3>Manual controller:</h3>
<table id="controller" cellspacing=0 cellpadding=0>
<% 
    for (int i=0; i<16; i++) {
%>
    <tr>
<% 
        for (int j=0; j<16; j++) {
%>
        <td><div class="label"><%= i*16+j %></div></td><td><input name="dmx.<%= i*16+j %>" value="" size="2"/></td>
<%
        }
%>
    </tr>
<% 
    } 
%>
    <tr>
        <td colspan="32"><input type="submit" value="Update..."/></td>
    </tr>
</table>

</body>
</html>
