<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Novo Usuario - Lambadega Cometa</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>

<body>

    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">

    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td>
                <span class="style4">Criar novo usuario</span>
            </td>
            <td align="right">
                <a href="${pageContext.request.contextPath}/LoginControlador">Voltar para o login</a>
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

    <form action="${pageContext.request.contextPath}/usuario" method="post">
      <input type="hidden" name="acao" value="cadastrar">

        <table class="bordaFina" width="50%" align="center">

            <tr>
                <td class="CelulaZebra1" width="35%" align="right">Nome:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="nome" class="inputtexto" size="35" maxlength="100" value="<c:out value='${nome}'/>" required>
                </td>
            </tr>

            <tr>
                <td class="CelulaZebra2" align="right">Email:</td>
                <td class="CelulaZebra2">
                    <input type="email" name="email" class="inputtexto" size="35" maxlength="100" value="<c:out value='${email}'/>" required>
                </td>
            </tr>

            <tr>
                <td class="CelulaZebra1" align="right">Usuario:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="usuario" class="inputtexto" size="25" maxlength="50" value="<c:out value='${usuario}'/>" required>
                </td>
            </tr>

            <tr>
                <td class="CelulaZebra2" align="right">Senha:</td>
                <td class="CelulaZebra2">
                    <input type="password" name="senha" class="inputtexto" size="25" maxlength="50" required>
                </td>
            </tr>

            <tr>
                <td class="CelulaZebra1" align="right">Confirmar Senha:</td>
                <td class="CelulaZebra1">
                    <input type="password" name="confirmaSenha" class="inputtexto" size="25" maxlength="50" required>
                </td>
            </tr>

            <tr>
                <td colspan="2" align="center" class="CelulaZebra2">
                    <input type="submit" class="inputbotao" value="Cadastrar">
                    &nbsp;
                </td>
            </tr>

        </table>
    </form>

</body>
</html>