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
            <c:choose>
                <c:when test="${param.origem == 'monitorFretes'}">
                    <a class="link-button" href="${pageContext.request.contextPath}/MonitorFretesControlador">Monitor de Fretes</a>
                </c:when>
                <c:otherwise>
                    <a class="link-button" href="${pageContext.request.contextPath}/index.jsp">Início</a>
                </c:otherwise>
            </c:choose>
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
            <form class="filter-form cliente-filter-form" action="${pageContext.request.contextPath}/ClienteControlador" method="get" name="formulario">
                <input type="hidden" name="acao" value="listar">
                <c:if test="${param.origem == 'monitorFretes'}">
                    <input type="hidden" name="origem" value="monitorFretes">
                </c:if>
                <div class="form-field">
                    <label for="filtro">Nome ou razão social</label>
                    <input type="text" name="filtro" id="filtro" class="inputtexto"
                           value="<c:out value='${filtro}'/>"/>
                </div>
                <div class="form-field">
                    <label for="documento">CPF / CNPJ</label>
                    <input type="text" name="documento" id="documento" class="inputtexto"
                           inputmode="numeric" autocomplete="off"
                           value="<c:out value='${documento}'/>"/>
                </div>
                <div class="form-field">
                    <label for="municipio">Município</label>
                    <input type="text" name="municipio" id="municipio" class="inputtexto"
                           value="<c:out value='${municipio}'/>"/>
                </div>
                <div class="form-field">
                    <label for="tipoPessoa">Tipo</label>
                    <select name="tipoPessoa" id="tipoPessoa" class="inputtexto">
                        <option value="">Todos</option>
                        <option value="F" ${tipoPessoa == 'F' ? 'selected' : ''}>Físico</option>
                        <option value="J" ${tipoPessoa == 'J' ? 'selected' : ''}>Jurídico</option>
                    </select>
                </div>
                <div class="form-field">
                    <label for="status">Status</label>
                    <select name="status" id="status" class="inputtexto">
                        <option value="">Todos</option>
                        <option value="ATIVO" ${status == 'ATIVO' ? 'selected' : ''}>Ativo</option>
                        <option value="INATIVO" ${status == 'INATIVO' ? 'selected' : ''}>Inativo</option>
                    </select>
                </div>
                <div class="app-actions">
                    <input type="submit" class="inputbotao" value="Pesquisar"/>
                    <a class="link-button" href="${pageContext.request.contextPath}/ClienteControlador?acao=listar">Limpar</a>
                </div>
            </form>
        </section>

        <section class="table-wrap">
            <table class="bordaFina data-table" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="tabela" width="10%">Ações</td>
                    <td class="tabela" width="7%">Tipo</td>
                    <td class="tabela" width="25%">Nome / Razão social</td>
                    <td class="tabela" width="18%">Nome fantasia</td>
                    <td class="tabela" width="16%">Município</td>
                    <td class="tabela" width="14%">CPF / CNPJ</td>
                    <td class="tabela" width="10%">Status</td>
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
                        <td><c:out value="${cliente.municipio}"/></td>
                        <td><c:out value="${cliente.documentoFormatado}"/></td>
                        <td><span class="status-${cliente.status}"><c:out value="${cliente.status}"/></span></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty listaClientes}">
                    <tr><td colspan="7" class="empty-state">Nenhum cliente encontrado.</td></tr>
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
                        <c:param name="documento" value="${documento}"/>
                        <c:param name="municipio" value="${municipio}"/>
                        <c:param name="tipoPessoa" value="${tipoPessoa}"/>
                        <c:param name="status" value="${status}"/>
                        <c:if test="${param.origem == 'monitorFretes'}">
                            <c:param name="origem" value="monitorFretes"/>
                        </c:if>
                    </c:url>
                    <a class="link-button" href="${clienteAnteriorUrl}">Anterior</a>
                </c:if>
                <c:if test="${paginaAtual < totalPaginas}">
                    <c:url var="clienteProximoUrl" value="/ClienteControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual + 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                        <c:param name="documento" value="${documento}"/>
                        <c:param name="municipio" value="${municipio}"/>
                        <c:param name="tipoPessoa" value="${tipoPessoa}"/>
                        <c:param name="status" value="${status}"/>
                        <c:if test="${param.origem == 'monitorFretes'}">
                            <c:param name="origem" value="monitorFretes"/>
                        </c:if>
                    </c:url>
                    <a class="link-button" href="${clienteProximoUrl}">Próximo</a>
                </c:if>
            </div>
        </section>
    </main>
    <script>
        function mascaraDocumentoFiltro(valor) {
            var digitos = valor.replace(/\D/g, '').substring(0, 14);
            if (digitos.length > 11) {
                return digitos
                    .replace(/^(\d{2})(\d)/, '$1.$2')
                    .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
                    .replace(/\.(\d{3})(\d)/, '.$1/$2')
                    .replace(/(\d{4})(\d)/, '$1-$2');
            }
            return digitos
                .replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
        }

        var filtroDocumento = document.getElementById('documento');
        if (filtroDocumento) {
            filtroDocumento.value = mascaraDocumentoFiltro(filtroDocumento.value);
            filtroDocumento.addEventListener('input', function() {
                this.value = mascaraDocumentoFiltro(this.value);
            });
        }
    </script>
</body>
</html>
