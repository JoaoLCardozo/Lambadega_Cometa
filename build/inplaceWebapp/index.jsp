<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Proteção: se não há usuário na sessão, redireciona para o Controller --%>
<c:if test="${empty sessionScope.usuarioLogado}">
    <c:redirect url="/LoginControlador"/>
</c:if>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Início - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
</head>
<body>

    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">

    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td class="CelulaZebra1">
                <span class="style4">🚚 Lambadega Cometa — Sistema de Gerenciamento de Fretes</span>
            </td>
            <td class="CelulaZebra1" align="right">
                Bem-vindo, <strong><c:out value="${sessionScope.usuarioLogado.nome}"/></strong>
                &nbsp;|&nbsp;
                <a href="${pageContext.request.contextPath}/LoginControlador?acao=logout">Logout</a>
            </td>
        </tr>
    </table>

    <br>

    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td class="tabela" align="center" width="25%">
                <a href="${pageContext.request.contextPath}/ClienteControlador?acao=listar">
                    👤 Clientes
                </a>
            </td>
            <td class="tabela" align="center" width="25%">
                <a href="${pageContext.request.contextPath}/MotoristaControlador?acao=listar">
                    🧑 Motoristas
                </a>
            </td>
            <td class="tabela" align="center" width="25%">
                <a href="${pageContext.request.contextPath}/VeiculoControlador?acao=listar">
                    🚚 Veículos
                </a>
            </td>
            <td class="tabela" align="center" width="25%">
                <a href="${pageContext.request.contextPath}/FreteControlador?acao=listar">
                    📦 Fretes
                </a>
            </td>
        </tr>
    </table>

</body>
</html>
