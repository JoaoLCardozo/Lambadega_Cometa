<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Veículos - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">
    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">Cadastro de Veículos</span></td>
            <td align="right">
                <input type="button" class="inputbotao" value="Novo Veículo"
                    onclick="window.location='${pageContext.request.contextPath}/VeiculoControlador?acao=novo'"/>
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
        <form action="${pageContext.request.contextPath}/VeiculoControlador" method="get">
            <input type="hidden" name="acao" value="listar">
            <tr>
                <td class="CelulaZebra1" width="15%">Placa:</td>
                <td class="CelulaZebra1" width="55%">
                    <input type="text" name="filtro" class="inputtexto" size="20"
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
            <td class="tabela" width="12%">PLACA</td>
            <td class="tabela" width="10%">TIPO</td>
            <td class="tabela" width="8%">ANO</td>
            <td class="tabela" width="14%">TARA (kg)</td>
            <td class="tabela" width="16%">CAPACIDADE (kg)</td>
            <td class="tabela" width="13%">VOLUME (m³)</td>
            <td class="tabela" width="13%">STATUS</td>
        </tr>
        <c:forEach var="v" varStatus="st" items="${listaVeiculos}">
        <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
            <td align="center">
                <a href="${pageContext.request.contextPath}/VeiculoControlador?acao=editar&id=${v.id}">
                    <img src="${pageContext.request.contextPath}/img/edit.gif" class="imagemLink" border="0"/>
                </a>
            </td>
            <td align="center">
                <a href="javascript:if(confirm('Excluir o veículo ${v.placa}?'))
                    window.location='${pageContext.request.contextPath}/VeiculoControlador?acao=excluir&id=${v.id}'">
                    <img src="${pageContext.request.contextPath}/img/lixo.png" class="imagemLink"/>
                </a>
            </td>
            <td><c:out value="${v.placa}"/></td>
            <td><c:out value="${v.tipo}"/></td>
            <td align="center"><c:out value="${v.anoFabricacao}"/></td>
            <td align="right"><c:out value="${v.taraKg}"/></td>
            <td align="right"><c:out value="${v.capacidadeKg}"/></td>
            <td align="right"><c:out value="${v.volumeM3}"/></td>
            <td><c:out value="${v.status}"/></td>
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
                    <a href="${pageContext.request.contextPath}/VeiculoControlador?acao=listar&pagina=${paginaAtual - 1}&filtro=<c:out value='${filtro}'/>">
                        <input type="button" class="inputbotao" value="ANTERIOR"/>
                    </a>
                </c:if>
            </td>
            <td width="30%" align="center">
                <c:if test="${paginaAtual < totalPaginas}">
                    <a href="${pageContext.request.contextPath}/VeiculoControlador?acao=listar&pagina=${paginaAtual + 1}&filtro=<c:out value='${filtro}'/>">
                        <input type="button" class="inputbotao" value="PRÓXIMO"/>
                    </a>
                </c:if>
            </td>
        </tr>
    </table>
</body>
</html>