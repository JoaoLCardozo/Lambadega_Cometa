<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Frete <c:out value="${frete.numero}"/> - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
    <script>
        function abrirJanelaRomaneio(url) {
            window.open(
                url,
                "romaneioFrete${frete.id}",
                "width=900,height=780,left=120,top=40,resizable=yes,scrollbars=yes,menubar=no,toolbar=no,location=no,status=no"
            );
            return false;
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
                    <p class="brand-subtitle">Detalhamento e histórico do frete</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=listar">Voltar</a>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Frete</span>
                <h2 class="app-title"><c:out value="${frete.numero}"/></h2>
            </div>
            <div class="app-actions">
                <span class="status-${frete.status}"><c:out value="${frete.status}"/></span>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error" role="alert"><c:out value="${erro}"/></div>
        </c:if>
        <c:if test="${not empty sucesso}">
            <div class="alert alert-success" role="alert"><c:out value="${sucesso}"/></div>
        </c:if>

        <section class="card">
            <div class="section-title">Dados do frete</div>
            <div class="details-grid">
                <div class="detail-item">
                    <span class="detail-label">Número</span>
                    <span class="detail-value"><c:out value="${frete.numero}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Status</span>
                    <span class="status-${frete.status}"><c:out value="${frete.status}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Remetente</span>
                    <span class="detail-value"><c:out value="${frete.remetente.nomeRazaoSocial}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Destinatário</span>
                    <span class="detail-value"><c:out value="${frete.destinatario.nomeRazaoSocial}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Motorista</span>
                    <span class="detail-value"><c:out value="${frete.motorista.nome}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Veículo</span>
                    <span class="detail-value"><c:out value="${frete.veiculo.placa}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Origem</span>
                    <span class="detail-value"><c:out value="${frete.municipioOrigem}"/>/<c:out value="${frete.ufOrigem}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Destino</span>
                    <span class="detail-value"><c:out value="${frete.municipioDestino}"/>/<c:out value="${frete.ufDestino}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Previsão de entrega</span>
                    <span class="detail-value"><c:out value="${frete.dataPrevisaoEntregaFormatada}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Emissão</span>
                    <span class="detail-value"><c:out value="${frete.dataEmissaoFormatada}"/></span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Peso</span>
                    <span class="detail-value"><c:out value="${frete.pesoKg}"/> kg</span>
                </div>
                <div class="detail-item">
                    <span class="detail-label">Valor total</span>
                    <span class="detail-value">R$ <c:out value="${frete.valorTotal}"/></span>
                </div>
            </div>
        </section>

        <section class="card pagination-card">
            <strong>Ações disponíveis</strong>
            <div class="pagination-actions">
                <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=romaneio&id=${frete.id}"
                   onclick="return abrirJanelaRomaneio(this.href)">Gerar romaneio PDF</a>
                <c:if test="${frete.status == 'EMITIDO'}">
                    <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=confirmarSaida&id=${frete.id}">Confirmar saída</a>
                    <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=cancelar&id=${frete.id}"
                       onclick="return confirm('Cancelar o frete ${frete.numero}?')">Cancelar frete</a>
                </c:if>
                <c:if test="${frete.status == 'SAIDA_CONFIRMADA' or frete.status == 'EM_TRANSITO'}">
                    <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=novaOcorrencia&id=${frete.id}">Registrar ocorrência</a>
                </c:if>
            </div>
        </section>

        <section class="table-wrap">
            <table class="bordaFina data-table" cellpadding="0" cellspacing="0">
                <tr><td colspan="5" class="tabela">Histórico de ocorrências</td></tr>
                <tr>
                    <td class="tabela" width="20%">Data/Hora</td>
                    <td class="tabela" width="20%">Tipo</td>
                    <td class="tabela" width="15%">Município/UF</td>
                    <td class="tabela" width="25%">Descrição</td>
                    <td class="tabela" width="20%">Recebedor</td>
                </tr>
                <c:forEach var="oc" varStatus="st" items="${frete.ocorrencias}">
                    <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
                        <td><c:out value="${oc.dataHoraFormatada}"/></td>
                        <td><c:out value="${oc.tipo}"/></td>
                        <td><c:out value="${oc.municipio}"/>/<c:out value="${oc.uf}"/></td>
                        <td><c:out value="${oc.descricao}"/></td>
                        <td><c:out value="${oc.nomeRecebedor}"/></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty frete.ocorrencias}">
                    <tr><td colspan="5" class="empty-state">Nenhuma ocorrência registrada.</td></tr>
                </c:if>
            </table>
        </section>
    </main>
</body>
</html>
