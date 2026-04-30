<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Veículos - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Frota, capacidade e disponibilidade</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/index.jsp">Início</a>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Veículos</span>
                <h2 class="app-title">Cadastro de veículos</h2>
            </div>
            <div class="app-actions">
                <input type="button" class="inputbotao" value="Novo veículo"
                       onclick="window.location='${pageContext.request.contextPath}/VeiculoControlador?acao=novo'"/>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>

        <section class="card filter-card">
            <form class="filter-form" action="${pageContext.request.contextPath}/VeiculoControlador" method="get">
                <input type="hidden" name="acao" value="listar">
                <div class="form-field">
                    <label for="filtro">Placa</label>
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
                    <td class="tabela" width="12%">Placa</td>
                    <td class="tabela" width="10%">Tipo</td>
                    <td class="tabela" width="8%">Ano</td>
                    <td class="tabela" width="14%">Tara (kg)</td>
                    <td class="tabela" width="16%">Capacidade (kg)</td>
                    <td class="tabela" width="13%">Volume (m3)</td>
                    <td class="tabela" width="15%">Status</td>
                </tr>
                <c:forEach var="v" varStatus="st" items="${listaVeiculos}">
                    <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
                        <td>
                            <div class="table-actions">
                                <a class="action-icon" href="${pageContext.request.contextPath}/VeiculoControlador?acao=editar&id=${v.id}">Editar</a>
                                <a class="action-icon danger" href="javascript:if(confirm('Excluir o veículo ${v.placa}?')) window.location='${pageContext.request.contextPath}/VeiculoControlador?acao=excluir&id=${v.id}'">Excluir</a>
                            </div>
                        </td>
                        <td><strong><c:out value="${v.placa}"/></strong></td>
                        <td><c:out value="${v.tipo}"/></td>
                        <td><c:out value="${v.anoFabricacao}"/></td>
                        <td><c:out value="${v.taraKg}"/></td>
                        <td><c:out value="${v.capacidadeKg}"/></td>
                        <td><c:out value="${v.volumeM3}"/></td>
                        <td><span class="status-${v.status}"><c:out value="${v.status}"/></span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty listaVeiculos}">
                    <tr><td colspan="8" class="empty-state">Nenhum veículo encontrado.</td></tr>
                </c:if>
            </table>
        </section>

        <section class="card pagination-card">
            <strong>Página <c:out value="${paginaAtual}"/> de <c:out value="${totalPaginas}"/></strong>
            <div class="pagination-actions">
                <c:if test="${paginaAtual > 1}">
                    <c:url var="veiculoAnteriorUrl" value="/VeiculoControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual - 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                    </c:url>
                    <a class="link-button" href="${veiculoAnteriorUrl}">Anterior</a>
                </c:if>
                <c:if test="${paginaAtual < totalPaginas}">
                    <c:url var="veiculoProximoUrl" value="/VeiculoControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual + 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                    </c:url>
                    <a class="link-button" href="${veiculoProximoUrl}">Próximo</a>
                </c:if>
            </div>
        </section>
    </main>
</body>
</html>
