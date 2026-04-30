<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Motoristas - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">
    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">Cadastro de Motoristas</span></td>
            <td align="right">
                <input type="button" class="inputbotao" value="Novo Motorista"
                    onclick="window.location='${pageContext.request.contextPath}/MotoristaControlador?acao=novo'"/>
                &nbsp;<a href="${pageContext.request.contextPath}/index.jsp">← Início</a>
            </td>
        </tr>
    </table><br>
    <c:if test="${not empty erro}">
        <table width="85%" align="center"><tr>
            <td style="color:red;font-weight:bold;"><c:out value="${erro}"/></td>
        </tr></table>
    </c:if>
    <table class="bordaFina" width="85%" align="center">
        <form action="${pageContext.request.contextPath}/MotoristaControlador" method="get">
            <input type="hidden" name="acao" value="listar">
            <tr>
                <td class="CelulaZebra1" width="15%">Nome:</td>
                <td class="CelulaZebra1" width="55%">
                    <input type="text" name="filtro" class="inputtexto" size="40"
                           value="<c:out value='${filtro}'/>"/>
                </td>
                <td class="CelulaZebra1">
                    <input type="submit" class="inputbotao" value="Pesquisar"/>
                </td>
            </tr>
        </form>
    </table><br>
    <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
        <tr>
            <td class="tabela" width="3%"></td>
            <td class="tabela" width="3%"></td>
            <td class="tabela" width="30%">NOME</td>
            <td class="tabela" width="15%">CPF</td>
            <td class="tabela" width="10%">CATEGORIA</td>
            <td class="tabela" width="13%">VALIDADE CNH</td>
            <td class="tabela" width="13%">VÍNCULO</td>
            <td class="tabela" width="13%">STATUS</td>
        </tr>
        <c:forEach var="m" varStatus="st" items="${listaMotoristas}">
        <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
            <td align="center">
                <a href="${pageContext.request.contextPath}/MotoristaControlador?acao=editar&id=${m.id}">
                    <img src="${pageContext.request.contextPath}/img/edit.gif" class="imagemLink" border="0"/>
                </a>
            </td>
            <td align="center">
                <a href="javascript:if(confirm('Excluir o motorista ${m.nome}?'))
                    window.location='${pageContext.request.contextPath}/MotoristaControlador?acao=excluir&id=${m.id}'">
                    <img src="${pageContext.request.contextPath}/img/lixo.png" class="imagemLink"/>
                </a>
            </td>
            <td><c:out value="${m.nome}"/></td>
            <td><c:out value="${m.cpf}"/></td>
            <td align="center"><c:out value="${m.cnhCategoria}"/></td>
            <td align="center">
                <c:out value="${m.cnhValidade}"/>
                <c:if test="${m.cnhVencida}">
                    <span style="color:red;font-weight:bold;"> (VENCIDA)</span>
                </c:if>
            </td>
            <td><c:out value="${m.tipoVinculo}"/></td>
            <td><c:out value="${m.status}"/></td>
        </tr>
        </c:forEach>
    </table><br>
    <table class="bordaFina" width="85%" align="center">
        <tr class="CelulaZebra1">
            <td width="40%" align="center">
                Página <c:out value="${paginaAtual}"/> de <c:out value="${totalPaginas}"/>
            </td>
            <td width="30%" align="center">
                <c:if test="${paginaAtual > 1}">
                    <a href="${pageContext.request.contextPath}/MotoristaControlador?acao=listar&pagina=${paginaAtual - 1}&filtro=<c:out value='${filtro}'/>">
                        <input type="button" class="inputbotao" value="ANTERIOR"/>
                    </a>
                </c:if>
            </td>
            <td width="30%" align="center">
                <c:if test="${paginaAtual < totalPaginas}">
                    <a href="${pageContext.request.contextPath}/MotoristaControlador?acao=listar&pagina=${paginaAtual + 1}&filtro=<c:out value='${filtro}'/>">
                        <input type="button" class="inputbotao" value="PRÓXIMO"/>
                    </a>
                </c:if>
            </td>
        </tr>
    </table>
</body>
</html>