<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Fretes - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
    <script>
        function abrirJanelaRelatorioFretes(form) {
            var filtro = form.filtro ? form.filtro.value : "";
            var url = "${pageContext.request.contextPath}/FreteControlador?acao=relatorioFretesAbertos"
                + "&filtro=" + encodeURIComponent(filtro);

            window.open(
                url,
                "relatorioFretesAbertos",
                "width=1100,height=780,left=80,top=40,resizable=yes,scrollbars=yes,menubar=no,toolbar=no,location=no,status=no"
            );
        }
    </script>
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Operação e acompanhamento de fretes</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/index.jsp">Início</a>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Fretes</span>
                <h2 class="app-title">Gestão de fretes</h2>
            </div>
            <div class="app-actions">
                <input type="button" class="inputbotao" value="Novo frete"
                       onclick="window.location='${pageContext.request.contextPath}/FreteControlador?acao=novo'"/>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>

        <section class="card filter-card">
            <form class="filter-form" action="${pageContext.request.contextPath}/FreteControlador" method="get">
                <div class="form-field">
                    <label for="filtro">Número ou remetente</label>
                    <input type="text" name="filtro" id="filtro" class="inputtexto"
                           value="<c:out value='${filtro}'/>"/>
                </div>
                <div class="app-actions">
                    <button type="submit" name="acao" value="listar" class="inputbotao">Pesquisar</button>
                    <button type="submit" name="acao" value="exportarCsv" class="inputbotao secondary">Exportar CSV</button>
                    <button type="button" class="inputbotao secondary"
                            onclick="abrirJanelaRelatorioFretes(this.form)">Imprimir fretes em aberto</button>
                </div>
            </form>
        </section>

        <section class="table-wrap">
            <table class="bordaFina data-table" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="tabela" width="8%">Ação</td>
                    <td class="tabela" width="12%">Número</td>
                    <td class="tabela" width="20%">Remetente</td>
                    <td class="tabela" width="20%">Destinatário</td>
                    <td class="tabela" width="15%">Destino</td>
                    <td class="tabela" width="12%">Previsão</td>
                    <td class="tabela" width="13%">Status</td>
                </tr>
                <c:forEach var="f" varStatus="st" items="${listaFretes}">
                    <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
                        <td>
                            <a class="action-icon" href="${pageContext.request.contextPath}/FreteControlador?acao=detalhe&id=${f.id}"
                               title="Ver detalhe">Abrir</a>
                        </td>
                        <td><c:out value="${f.numero}"/></td>
                        <td><c:out value="${f.remetente.nomeRazaoSocial}"/></td>
                        <td><c:out value="${f.destinatario.nomeRazaoSocial}"/></td>
                        <td><c:out value="${f.municipioDestino}"/>/<c:out value="${f.ufDestino}"/></td>
                        <td><c:out value="${f.dataPrevisaoEntrega}"/></td>
                        <td><span class="status-${f.status}"><c:out value="${f.status}"/></span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty listaFretes}">
                    <tr>
                        <td colspan="7" class="empty-state">Nenhum frete encontrado.</td>
                    </tr>
                </c:if>
            </table>
        </section>

        <section class="card pagination-card">
            <strong>Página <c:out value="${paginaAtual}"/> de <c:out value="${totalPaginas}"/></strong>
            <div class="pagination-actions">
                <c:if test="${paginaAtual > 1}">
                    <c:url var="freteAnteriorUrl" value="/FreteControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual - 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                    </c:url>
                    <a class="link-button" href="${freteAnteriorUrl}">Anterior</a>
                </c:if>
                <c:if test="${paginaAtual < totalPaginas}">
                    <c:url var="freteProximoUrl" value="/FreteControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual + 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                    </c:url>
                    <a class="link-button" href="${freteProximoUrl}">Próximo</a>
                </c:if>
            </div>
        </section>
    </main>
</body>
</html>
