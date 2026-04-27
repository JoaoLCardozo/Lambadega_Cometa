<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.StringWriter" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Erro - Debug</title>
</head>
<body>
    <h2>Erro capturado</h2>

    <p><strong>Status:</strong> <%= request.getAttribute("javax.servlet.error.status_code") %></p>
    <p><strong>URI:</strong> <%= request.getAttribute("javax.servlet.error.request_uri") %></p>
    <p><strong>Mensagem:</strong> <%= request.getAttribute("javax.servlet.error.message") %></p>
    <p><strong>Servlet:</strong> <%= request.getAttribute("javax.servlet.error.servlet_name") %></p>

    <%
        Throwable erro = (Throwable) request.getAttribute("javax.servlet.error.exception");

        if (erro != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            erro.printStackTrace(pw);
    %>
            <h3>Stacktrace</h3>
            <pre><%= sw.toString() %></pre>
    <%
        }
    %>

    <br>
    <a href="<%= request.getContextPath() %>/LoginControlador">Voltar para o login</a>
</body>
</html>