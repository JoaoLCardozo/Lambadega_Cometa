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
            <form class="filter-form veiculo-filter-form" action="${pageContext.request.contextPath}/VeiculoControlador" method="get">
                <input type="hidden" name="acao" value="listar">
                <c:if test="${param.origem == 'monitorFretes'}">
                    <input type="hidden" name="origem" value="monitorFretes">
                </c:if>
                <div class="form-field">
                    <label for="filtro">Placa</label>
                    <input type="text" name="filtro" id="filtro" class="inputtexto"
                           value="<c:out value='${filtro}'/>"/>
                </div>
                <div class="form-field">
                    <label for="tipo">Tipo</label>
                    <select name="tipo" id="tipo" class="inputtexto">
                        <option value="">Todos</option>
                        <option value="TRUCK" ${tipo == 'TRUCK' ? 'selected' : ''}>Truck</option>
                        <option value="CARRETA" ${tipo == 'CARRETA' ? 'selected' : ''}>Carreta</option>
                        <option value="VAN" ${tipo == 'VAN' ? 'selected' : ''}>Van</option>
                        <option value="UTILITARIO" ${tipo == 'UTILITARIO' ? 'selected' : ''}>Utilitário</option>
                    </select>
                </div>
                <div class="form-field">
                    <label for="status">Status</label>
                    <select name="status" id="status" class="inputtexto">
                        <option value="">Todos</option>
                        <option value="DISPONIVEL" ${status == 'DISPONIVEL' ? 'selected' : ''}>Disponível</option>
                        <option value="RESERVADO" ${status == 'RESERVADO' ? 'selected' : ''}>Reservado</option>
                        <option value="EM_VIAGEM" ${status == 'EM_VIAGEM' ? 'selected' : ''}>Em Viagem</option>
                        <option value="EM_MANUTENCAO" ${status == 'EM_MANUTENCAO' ? 'selected' : ''}>Manutenção</option>
                    </select>
                </div>
                <div class="form-field">
                    <label for="anoFabricacao">Ano</label>
                    <input type="text" name="anoFabricacao" id="anoFabricacao" class="inputtexto"
                           inputmode="numeric" pattern="[0-9]*" maxlength="4"
                           value="<c:out value='${anoFabricacao}'/>"/>
                </div>
                <div class="app-actions">
                    <input type="submit" class="inputbotao" value="Pesquisar"/>
                    <c:url var="limparVeiculosUrl" value="/VeiculoControlador">
                        <c:param name="acao" value="listar"/>
                        <c:if test="${param.origem == 'monitorFretes'}">
                            <c:param name="origem" value="monitorFretes"/>
                        </c:if>
                    </c:url>
                    <a class="link-button" href="${limparVeiculosUrl}">Limpar</a>
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
                        <td><span class="status-${v.status}"><c:out value="${v.statusRotulo}"/></span></td>
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
                        <c:param name="tipo" value="${tipo}"/>
                        <c:param name="status" value="${status}"/>
                        <c:param name="anoFabricacao" value="${anoFabricacao}"/>
                        <c:if test="${param.origem == 'monitorFretes'}">
                            <c:param name="origem" value="monitorFretes"/>
                        </c:if>
                    </c:url>
                    <a class="link-button" href="${veiculoAnteriorUrl}">Anterior</a>
                </c:if>
                <c:if test="${paginaAtual < totalPaginas}">
                    <c:url var="veiculoProximoUrl" value="/VeiculoControlador">
                        <c:param name="acao" value="listar"/>
                        <c:param name="pagina" value="${paginaAtual + 1}"/>
                        <c:param name="filtro" value="${filtro}"/>
                        <c:param name="tipo" value="${tipo}"/>
                        <c:param name="status" value="${status}"/>
                        <c:param name="anoFabricacao" value="${anoFabricacao}"/>
                        <c:if test="${param.origem == 'monitorFretes'}">
                            <c:param name="origem" value="monitorFretes"/>
                        </c:if>
                    </c:url>
                    <a class="link-button" href="${veiculoProximoUrl}">Próximo</a>
                </c:if>
            </div>
        </section>
    </main>
    <script>
        var filtroPlaca = document.getElementById('filtro');
        if (filtroPlaca) {
            filtroPlaca.addEventListener('input', function() {
                this.value = this.value.toUpperCase();
            });
        }

        var filtroAno = document.getElementById('anoFabricacao');
        if (filtroAno) {
            filtroAno.addEventListener('input', function() {
                this.value = this.value.replace(/\D/g, '').substring(0, 4);
            });
        }
    </script>
</body>
</html>
