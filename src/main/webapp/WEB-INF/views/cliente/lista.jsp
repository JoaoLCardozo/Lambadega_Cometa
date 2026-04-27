<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Clientes - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">

    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">Cadastro de Clientes</span></td>
            <td align="right">
                <input type="button" class="inputbotao" value="Novo Cliente"
                    onclick="window.location='${pageContext.request.contextPath}/ClienteControlador?acao=novo'"/>
                &nbsp;
                <a href="${pageContext.request.contextPath}/index.jsp">← Início</a>
            </td>
        </tr>
    </table>
    <br>

    <%-- Mensagem de erro --%>
    <c:if test="${not empty erro}">
        <table width="85%" align="center">
            <tr><td style="color:red; font-weight:bold;"><c:out value="${erro}"/></td></tr>
        </table>
    </c:if>

    <%-- Filtro --%>
    <table class="bordaFina" width="85%" align="center">
        <form action="${pageContext.request.contextPath}/ClienteControlador" method="get" name="formulario">
            <input type="hidden" name="acao" value="listar">
            <tr>
                <td class="CelulaZebra1" width="20%">Razão Social:</td>
                <td class="CelulaZebra1" width="50%">
                    <input type="text" name="filtro" class="inputtexto" size="40"
                           value="<c:out value='${filtro}'/>"/>
                </td>
                <td class="CelulaZebra1">
                    <input type="submit" class="inputbotao" value="Pesquisar"/>
                </td>
            </tr>
        </form>
    </table>
    <br>

    <%-- Listagem --%>
    <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
        <tr>
            <td class="tabela" width="3%"></td>
            <td class="tabela" width="3%"></td>
            <td class="tabela" width="35%">RAZÃO SOCIAL</td>
            <td class="tabela" width="20%">NOME FANTASIA</td>
            <td class="tabela" width="18%">CNPJ</td>
            <td class="tabela" width="10%">TIPO</td>
            <td class="tabela" width="11%">STATUS</td>
        </tr>
        <c:forEach var="cliente" varStatus="st" items="${listaClientes}">
        <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
            <td align="center">
                <a href="${pageContext.request.contextPath}/ClienteControlador?acao=editar&id=${cliente.id}"
                   title="Editar">
                    <img src="${pageContext.request.contextPath}/img/edit.gif" class="imagemLink" border="0"/>
                </a>
            </td>
            <td align="center">
                <a href="javascript:if(confirm('Excluir o cliente ${cliente.razaoSocial}?'))
                    window.location='${pageContext.request.contextPath}/ClienteControlador?acao=excluir&id=${cliente.id}'"
                   title="Excluir">
                    <img src="${pageContext.request.contextPath}/img/lixo.png" class="imagemLink"/>
                </a>
            </td>
            <td><c:out value="${cliente.razaoSocial}"/></td>
            <td><c:out value="${cliente.nomeFantasia}"/></td>
            <td><c:out value="${cliente.cnpj}"/></td>
            <td><c:out value="${cliente.tipo}"/></td>
            <td><c:out value="${cliente.status}"/></td>
        </tr>
        </c:forEach>
    </table>
    <br>

    <%-- Paginação --%>
    <table class="bordaFina" width="85%" align="center">
        <tr class="CelulaZebra1">
            <td width="40%" align="center">
                Página <c:out value="${paginaAtual}"/> de <c:out value="${totalPaginas}"/>
            </td>
            <td width="30%" align="center">
                <c:if test="${paginaAtual > 1}">
                    <a href="${pageContext.request.contextPath}/ClienteControlador?acao=listar&pagina=${paginaAtual - 1}&filtro=<c:out value='${filtro}'/>">
                        <input type="button" class="inputbotao" value="ANTERIOR"/>
                    </a>
                </c:if>
            </td>
            <td width="30%" align="center">
                <c:if test="${paginaAtual < totalPaginas}">
                    <a href="${pageContext.request.contextPath}/ClienteControlador?acao=listar&pagina=${paginaAtual + 1}&filtro=<c:out value='${filtro}'/>">
                        <input type="button" class="inputbotao" value="PRÓXIMO"/>
                    </a>
                </c:if>
            </td>
        </tr>
    </table>
</body>
</html>