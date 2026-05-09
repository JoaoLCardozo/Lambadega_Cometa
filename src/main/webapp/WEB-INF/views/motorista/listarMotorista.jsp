<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Motoristas - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Equipe de transporte e documentação</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/index.jsp">Início</a>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Motoristas</span>
                <h2 class="app-title">Cadastro de motoristas</h2>
            </div>
            <div class="app-actions">
                <input type="button" class="inputbotao" value="Novo motorista"
                       onclick="window.location='${pageContext.request.contextPath}/MotoristaControlador?acao=novo'"/>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>

        <section class="card filter-card">
            <form class="filter-form" action="${pageContext.request.contextPath}/MotoristaControlador" method="get">
                <input type="hidden" name="acao" value="listar">
                <div class="form-field">
                    <label for="filtro">Nome do motorista</label>
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
                    <td class="tabela" width="26%">Nome</td>
                    <td class="tabela" width="14%">CPF</td>
                    <td class="tabela" width="10%">Categoria</td>
                    <td class="tabela" width="14%">Validade CNH</td>
                    <td class="tabela" width="12%">Vínculo</td>
                    <td class="tabela" width="12%">Status</td>
                </tr>
                <c:forEach var="m" varStatus="st" items="${listaMotoristas}">
                    <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
                        <td>
                            <div class="table-actions">
                                <a class="action-icon" href="${pageContext.request.contextPath}/MotoristaControlador?acao=editar&id=${m.id}">Editar</a>
                                <a class="action-icon danger" href="javascript:if(confirm('Excluir o motorista ${m.nome}?')) window.location='${pageContext.request.contextPath}/MotoristaControlador?acao=excluir&id=${m.id}'">Excluir</a>
                            </div>
                        </td>
                        <td><c:out value="${m.nome}"/></td>
                        <td><c:out value="${m.cpfFormatado}"/></td>
                        <td><span class="badge"><c:out value="${m.cnhCategoria}"/></span></td>
                        <td>
                            <c:out value="${m.cnhValidadeFormatada}"/>
                            <c:if test="${m.cnhVencida}">
                                <span class="status-SUSPENSO">Vencida</span>
                            </c:if>
                        </td>
                        <td><span class="status-${m.tipoVinculo}"><c:out value="${m.tipoVinculo}"/></span></td>
                        <td><span class="status-${m.status}"><c:out value="${m.status}"/></span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty listaMotoristas}">
                    <tr><td colspan="7" class="empty-state">Nenhum motorista encontrado.</td></tr>
                </c:if>
            </table>
        </section>

        <section class="card pagination-card">
            <strong>Página <c:out value="${paginaAtual}"/> de <c:out value="${totalPaginas}"/></strong>
            <div class="pagination-actions">
                <c:if test="${paginaAtual > 1}">
                    <c:url var="motoristaAnteriorUrl" value="/MotoristaControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual - 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                    </c:url>
                    <a class="link-button" href="${motoristaAnteriorUrl}">Anterior</a>
                </c:if>
                <c:if test="${paginaAtual < totalPaginas}">
                    <c:url var="motoristaProximoUrl" value="/MotoristaControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual + 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                    </c:url>
                    <a class="link-button" href="${motoristaProximoUrl}">Próximo</a>
                </c:if>
            </div>
        </section>
    </main>
</body>
</html>
