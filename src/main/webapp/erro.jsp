<%@ page contentType="text/html" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Erro - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">

    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td class="CelulaZebra1" align="center">
                <span class="style4">Ocorreu um erro inesperado</span>
            </td>
        </tr>
        <tr>
            <td class="CelulaZebra2" align="center">
                <br>
                Não foi possível processar sua solicitação.<br>
                Por favor, tente novamente ou contate o suporte.
                <br><br>
                <a href="${pageContext.request.contextPath}/index.jsp">← Voltar ao início</a>
                &nbsp;|&nbsp;
                <a href="${pageContext.request.contextPath}/LoginControlador">Ir para o login</a>
                <br><br>
            </td>
        </tr>
    </table>
</body>
</html>