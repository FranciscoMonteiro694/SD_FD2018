<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
		 pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Drop Music!</title>
<body>
<s:form action="login" method="post">
	<s:text name="username:"/>
	<s:textfield name="username" />
	<br>
	<s:text name="password:"/>
	<s:textfield name="password" />
	<s:submit value="Login" />
</s:form>
<p><a href="<s:url action="registermenu"/>">Register</a></p>
</body>
</head>
</html>