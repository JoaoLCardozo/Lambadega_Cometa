<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Monitor de Fretes Operacional - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Monitoramento operacional de fretes</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/index.jsp">Início</a>
        </section>

        <section class="app-header monitor-fretes-hero">
            <div class="app-header-main">
                <span class="app-eyebrow">Monitor de Fretes</span>
                <h2 class="app-title">Prioridades, riscos e capacidade da operação</h2>
            </div>
            <div class="app-actions">
                <a class="inputbotao" href="${pageContext.request.contextPath}/FreteControlador?acao=novo">Novo frete</a>
                <a class="inputbotao secondary" href="${pageContext.request.contextPath}/FreteControlador?acao=listar">Ver fretes</a>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error" role="alert"><c:out value="${erro}"/></div>
        </c:if>

        <c:if test="${not empty resumo}">
            <section class="monitor-fretes-metrics" aria-label="Indicadores operacionais">
                <article class="metric-card ${resumo.fretesAtrasados > 0 ? 'metric-danger' : ''}">
                    <span class="metric-label">Fretes atrasados</span>
                    <strong class="metric-value"><c:out value="${resumo.fretesAtrasados}"/></strong>
                    <span class="metric-help">Previsão vencida e ainda em aberto</span>
                </article>
                <article class="metric-card ${resumo.entregasHoje > 0 ? 'metric-warning' : ''}">
                    <span class="metric-label">Vencem hoje</span>
                    <strong class="metric-value"><c:out value="${resumo.entregasHoje}"/></strong>
                    <span class="metric-help">Entregas com prazo para hoje</span>
                </article>
                <article class="metric-card">
                    <span class="metric-label">Fretes em aberto</span>
                    <strong class="metric-value"><c:out value="${resumo.fretesAbertos}"/></strong>
                    <span class="metric-help">Emitidos, saída confirmada ou em trânsito</span>
                </article>
                <article class="metric-card">
                    <span class="metric-label">Frota disponível</span>
                    <strong class="metric-value"><c:out value="${resumo.veiculosDisponiveis}"/></strong>
                    <span class="metric-help">Veículos livres para emissão</span>
                </article>
                <article class="metric-card ${resumo.motoristasCnhVencida > 0 ? 'metric-danger' : ''}">
                    <span class="metric-label">CNH vencida</span>
                    <strong class="metric-value"><c:out value="${resumo.motoristasCnhVencida}"/></strong>
                    <span class="metric-help">Motoristas ativos bloqueados para escala</span>
                </article>
                <article class="metric-card">
                    <span class="metric-label">Valor no mês</span>
                    <strong class="metric-value currency"><c:out value="${resumo.valorFretesMesFormatado}"/></strong>
                    <span class="metric-help">Fretes não cancelados emitidos no mês</span>
                </article>
            </section>

            <section class="monitor-fretes-layout">
                <div class="monitor-fretes-main">
                    <section class="card monitor-fretes-panel">
                        <div class="panel-heading">
                            <div>
                                <span class="app-eyebrow">Alertas</span>
                                <h3 class="panel-title">Prioridades da operação</h3>
                            </div>
                        </div>
                        <div class="alert-list">
                            <c:forEach var="alerta" items="${resumo.alertas}">
                                <article class="operation-alert alert-${alerta.nivel}">
                                    <div>
                                        <strong><c:out value="${alerta.titulo}"/></strong>
                                        <span><c:out value="${alerta.descricao}"/></span>
                                    </div>
                                    <a class="link-button" href="${pageContext.request.contextPath}${alerta.link}">
                                        <c:out value="${alerta.acao}"/>
                                    </a>
                                </article>
                            </c:forEach>
                        </div>
                    </section>

                    <section class="card monitor-fretes-panel">
                        <div class="panel-heading">
                            <div>
                                <span class="app-eyebrow">Fretes críticos</span>
                                <h3 class="panel-title">Atrasados ou vencendo hoje</h3>
                            </div>
                        </div>
                        <div class="table-wrap compact-table">
                            <table class="bordaFina data-table" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td class="tabela" width="13%">Número</td>
                                    <td class="tabela" width="25%">Destinatário</td>
                                    <td class="tabela" width="18%">Destino</td>
                                    <td class="tabela" width="13%">Previsão</td>
                                    <td class="tabela" width="16%">Situação</td>
                                    <td class="tabela" width="15%">Ação</td>
                                </tr>
                                <c:forEach var="frete" items="${resumo.fretesCriticos}">
                                    <tr>
                                        <td><strong><c:out value="${frete.numero}"/></strong></td>
                                        <td><c:out value="${frete.destinatario}"/></td>
                                        <td><c:out value="${frete.destinoFormatado}"/></td>
                                        <td><c:out value="${frete.dataPrevisaoFormatada}"/></td>
                                        <td>
                                            <span class="${frete.diasAtraso > 0 ? 'status-NAO_ENTREGUE' : 'status-EM_TRANSITO'}">
                                                <c:out value="${frete.situacao}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <a class="action-icon" href="${pageContext.request.contextPath}/FreteControlador?acao=detalhe&id=${frete.id}">Abrir</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty resumo.fretesCriticos}">
                                    <tr><td colspan="6" class="empty-state">Nenhum frete crítico no momento.</td></tr>
                                </c:if>
                            </table>
                        </div>
                    </section>
                </div>

                <aside class="monitor-fretes-side">
                    <section class="card monitor-fretes-panel">
                        <div class="panel-heading">
                            <div>
                                <span class="app-eyebrow">Distribuição</span>
                                <h3 class="panel-title">Status dos fretes</h3>
                            </div>
                        </div>
                        <div class="status-stack">
                            <c:forEach var="item" items="${resumo.statusFretes}">
                                <div class="status-row">
                                    <span class="status-${item.status}"><c:out value="${item.status}"/></span>
                                    <strong><c:out value="${item.total}"/></strong>
                                </div>
                            </c:forEach>
                            <c:if test="${empty resumo.statusFretes}">
                                <p class="empty-state">Nenhum frete cadastrado.</p>
                            </c:if>
                        </div>
                    </section>

                    <section class="card monitor-fretes-panel">
                        <div class="panel-heading">
                            <div>
                                <span class="app-eyebrow">Mês atual</span>
                                <h3 class="panel-title">Ranking de motoristas</h3>
                            </div>
                        </div>
                        <div class="ranking-list">
                            <c:forEach var="motorista" varStatus="st" items="${resumo.rankingMotoristas}">
                                <div class="ranking-row">
                                    <span class="rank-position rank-${st.count}" aria-label="${st.count}º lugar">
                                        <c:out value="${st.count}"/>º
                                    </span>
                                    <div class="ranking-info">
                                        <strong><c:out value="${motorista.nome}"/></strong>
                                        <span><c:out value="${motorista.entregas}"/> entrega(s) · <c:out value="${motorista.valorTotalFormatado}"/></span>
                                    </div>
                                </div>
                            </c:forEach>
                            <c:if test="${empty resumo.rankingMotoristas}">
                                <p class="empty-state">Ainda não há entregas concluídas neste mês.</p>
                            </c:if>
                        </div>
                    </section>

                    <section class="card monitor-fretes-panel quick-actions">
                        <a class="link-button" href="${pageContext.request.contextPath}/ClienteControlador?acao=listar&status=ATIVO&origem=monitorFretes">Clientes ativos: <c:out value="${resumo.clientesAtivos}"/></a>
                        <a class="link-button" href="${pageContext.request.contextPath}/VeiculoControlador?acao=listar&origem=monitorFretes">Consultar frota</a>
                        <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=performanceMotorista&origem=monitorFretes">Performance por motorista</a>
                    </section>
                </aside>
            </section>
        </c:if>
    </main>
</body>
</html>
