<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Fretes - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
    <style>
        .status-EMITIDO          { color: #555;   font-weight: bold; }
        .status-SAIDA_CONFIRMADA { color: #0066cc; font-weight: bold; }
        .status-EM_TRANSITO      { color: #cc6600; font-weight: bold; }
        .status-ENTREGUE         { color: #008800; font-weight: bold; }
        .status-NAO_ENTREGUE     { color: #cc0000; font-weight: bold; }
        .status-CANCELADO        { color: #999;    font-weight: bold; text-decoration: line-through; }
    </style>
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">
    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">Gestão de Fretes</span></td>
            <td align="right">
                <input type="button" class="inputbotao" value="Novo Frete"
                    onclick="window.location='${pageContext.request.contextPath}/FreteControlador?acao=novo'"/>
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
        <form action="${pageContext.request.contextPath}/FreteControlador" method="get">
            <input type="hidden" name="acao" value="listar">
            <tr>
                <td class="CelulaZebra1" width="15%">Nº / Remetente:</td>
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
            <td class="tabela" width="12%">NÚMERO</td>
            <td class="tabela" width="20%">REMETENTE</td>
            <td class="tabela" width="20%">DESTINATÁRIO</td>
            <td class="tabela" width="15%">DESTINO</td>
            <td class="tabela" width="12%">PREVISÃO</td>
            <td class="tabela" width="18%">STATUS</td>
        </tr>
        <c:forEach var="f" varStatus="st" items="${listaFretes}">
        <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
            <td align="center">
                <a href="${pageContext.request.contextPath}/FreteControlador?acao=detalhe&id=${f.id}"
                   title="Ver detalhe">
                    <img src="${pageContext.request.contextPath}/img/edit.gif" class="imagemLink" border="0"/>
                </a>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/FreteControlador?acao=detalhe&id=${f.id}">
                    <c:out value="${f.numero}"/>
                </a>
            </td>
            <td><c:out value="${f.remetente.razaoSocial}"/></td>
            <td><c:out value="${f.destinatario.razaoSocial}"/></td>
            <td><c:out value="${f.municipioDestino}"/>/<c:out value="${f.ufDestino}"/></td>
            <td align="center"><c:out value="${f.dataPrevisaoEntrega}"/></td>
            <td>
                <span class="status-${f.status}">
                    <c:out value="${f.status}"/>
                </span>
            </td>
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
                    <a href="${pageContext.request.contextPath}/FreteControlador?acao=listar&pagina=${paginaAtual - 1}&filtro=<c:out value='${filtro}'/>">
                        <input type="button" class="inputbotao" value="ANTERIOR"/>
                    </a>
                </c:if>
            </td>
            <td width="30%" align="center">
                <c:if test="${paginaAtual < totalPaginas}">
                    <a href="${pageContext.request.contextPath}/FreteControlador?acao=listar&pagina=${paginaAtual + 1}&filtro=<c:out value='${filtro}'/>">
                        <input type="button" class="inputbotao" value="PRÓXIMO"/>
                    </a>
                </c:if>
            </td>
        </tr>
    </table>
</body>
</html>