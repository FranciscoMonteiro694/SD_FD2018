<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Drop Music!</title>
<body>
<s:form action="register" method="post">
    <s:text name="Indique o seu username:"/>
    <s:textfield name="username" />
    <br>
    <s:text name="Indique a sua password:"/>
    <s:textfield name="password" />
    <br>
    <s:submit value="Register" />
</s:form>
<p><a href="<s:url action="index"/>">Voltar</a></p>
</body>
</head>
</html>