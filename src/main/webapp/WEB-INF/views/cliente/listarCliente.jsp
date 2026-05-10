<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Clientes - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Cadastro comercial e operacional</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/index.jsp">Início</a>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Clientes</span>
                <h2 class="app-title">Cadastro de clientes</h2>
            </div>
            <div class="app-actions">
                <input type="button" class="inputbotao" value="Novo cliente"
                       onclick="window.location='${pageContext.request.contextPath}/ClienteControlador?acao=novo'"/>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>
        <c:if test="${not empty sucesso}">
            <div class="alert alert-success"><c:out value="${sucesso}"/></div>
        </c:if>

        <section class="card filter-card">
            <form class="filter-form" action="${pageContext.request.contextPath}/ClienteControlador" method="get" name="formulario">
                <input type="hidden" name="acao" value="listar">
                <div class="form-field">
                    <label for="filtro">Nome ou razão social</label>
                    <input type="text" name="filtro" id="filtro" class="inputtexto"
                           value="<c:out value='${filtro}'/>"/>
                </div>
                <div class="app-actions">
                    <input type="submit" class="inputbotao" value="Pesquisar"/>
                </div>
            </form>
        </section>

        <section class="table-wrap">
            <table class="bordaFina data-table" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="tabela" width="12%">Ações</td>
                    <td class="tabela" width="8%">Tipo</td>
                    <td class="tabela" width="30%">Nome / Razão social</td>
                    <td class="tabela" width="20%">Nome fantasia</td>
                    <td class="tabela" width="18%">CPF / CNPJ</td>
                    <td class="tabela" width="12%">Status</td>
                </tr>
                <c:forEach var="cliente" varStatus="st" items="${listaClientes}">
                    <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
                        <td>
                            <div class="table-actions">
                                <a class="action-icon" href="${pageContext.request.contextPath}/ClienteControlador?acao=editar&id=${cliente.id}" title="Editar">Editar</a>
                                <a class="action-icon danger" href="javascript:if(confirm('Excluir o cliente ${cliente.nomeRazaoSocial}?')) window.location='${pageContext.request.contextPath}/ClienteControlador?acao=excluir&id=${cliente.id}'" title="Excluir">Excluir</a>
                            </div>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${cliente.tipoPessoa == 'F'}"><span class="status-PF">PF</span></c:when>
                                <c:when test="${cliente.tipoPessoa == 'J'}"><span class="status-PJ">PJ</span></c:when>
                                <c:otherwise><c:out value="${cliente.tipoPessoa}"/></c:otherwise>
                            </c:choose>
                        </td>
                        <td><c:out value="${cliente.nomeRazaoSocial}"/></td>
                        <td><c:out value="${cliente.nomeFantasia}"/></td>
                        <td><c:out value="${cliente.documentoFormatado}"/></td>
                        <td><span class="status-${cliente.status}"><c:out value="${cliente.status}"/></span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty listaClientes}">
                    <tr><td colspan="6" class="empty-state">Nenhum cliente encontrado.</td></tr>
                </c:if>
            </table>
        </section>

        <section class="card pagination-card">
            <strong>Página <c:out value="${paginaAtual}"/> de <c:out value="${totalPaginas}"/></strong>
            <div class="pagination-actions">
                <c:if test="${paginaAtual > 1}">
                    <c:url var="clienteAnteriorUrl" value="/ClienteControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual - 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                    </c:url>
                    <a class="link-button" href="${clienteAnteriorUrl}">Anterior</a>
                </c:if>
                <c:if test="${paginaAtual < totalPaginas}">
                    <c:url var="clienteProximoUrl" value="/ClienteControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual + 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                    </c:url>
                    <a class="link-button" href="${clienteProximoUrl}">Próximo</a>
                </c:if>
            </div>
        </section>
    </main>
</body>
</html>
