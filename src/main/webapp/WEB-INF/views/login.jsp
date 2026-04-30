<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Login - Lambadega Cometa</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>

<body>

    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">

    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td>
                <span class="style4">Login - Sistema de Gerenciamento de Fretes</span>
            </td>
            <td align="right">
                <input type="button"
                       class="inputbotao"
                       value="Criar novo usuario"
                       onclick="window.location='${pageContext.request.contextPath}/LoginControlador?acao=novoUsuario'"/>
            </td>
        </tr>
    </table>

    <br>

    <c:if test="${not empty erro}">
        <table width="85%" align="center">
            <tr>
                <td style="color:red;font-weight:bold;">
                    <c:out value="${erro}"/>
                </td>
            </tr>
        </table>
        <br>
    </c:if>

    <c:if test="${not empty sucesso}">
        <table width="85%" align="center">
            <tr>
                <td style="color:green;font-weight:bold;">
                    <c:out value="${sucesso}"/>
                </td>
            </tr>
        </table>
        <br>
    </c:if>

    <c:if test="${not empty aviso}">
        <table width="85%" align="center">
            <tr>
                <td style="color:#cc8800;font-weight:bold;">
                    <c:out value="${aviso}"/>
                </td>
            </tr>
        </table>
        <br>
    </c:if>

    <table class="bordaFina" width="40%" align="center">
        <form action="${pageContext.request.contextPath}/LoginControlador" method="post">
            <input type="hidden" name="acao" value="login">

            <tr>
                <td colspan="2" align="center" class="CelulaZebra1">
                    <span class="style4">Acesso ao Sistema</span>
                </td>
            </tr>

            <tr>
                <td class="CelulaZebra1" width="35%" align="right">Usuario:</td>
                <td class="CelulaZebra1" width="65%">
                    <input type="text"
                           name="usuario"
                           id="usuario"
                           class="inputtexto"
                           size="25"
                           maxlength="50"
                           value="<c:out value='${usuario}'/>"
                           autofocus/>
                </td>
            </tr>

            <tr>
                <td class="CelulaZebra2" align="right">Senha:</td>
                <td class="CelulaZebra2">
                    <input type="password"
                           name="senha"
                           id="senha"
                           class="inputtexto"
                           size="25"
                           maxlength="50"/>
                </td>
            </tr>

            <tr>
                <td colspan="2" align="center" class="CelulaZebra1">
                    <input type="submit" class="inputbotao" value="Entrar"/>
                    &nbsp;
                </td>
            </tr>

            <tr>
                <td colspan="2" align="center" class="CelulaZebra2">
                    <small>Sistema Lambadega Cometa v1.0 - 2026</small>
                </td>
            </tr>
        </form>
    </table>

</body>
</html>