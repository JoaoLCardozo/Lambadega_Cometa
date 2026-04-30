<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Frete <c:out value="${frete.numero}"/> - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
    <style>
        .status-EMITIDO          { color: #555;    font-weight:bold; }
        .status-SAIDA_CONFIRMADA { color: #0066cc; font-weight:bold; }
        .status-EM_TRANSITO      { color: #cc6600; font-weight:bold; }
        .status-ENTREGUE         { color: #008800; font-weight:bold; }
        .status-NAO_ENTREGUE     { color: #cc0000; font-weight:bold; }
        .status-CANCELADO        { color: #999;    font-weight:bold; text-decoration:line-through; }
    </style>
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">
    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">Frete: <c:out value="${frete.numero}"/></span></td>
            <td align="right"><a href="${pageContext.request.contextPath}/FreteControlador?acao=listar">← Voltar</a></td>
        </tr>
    </table><br>
    <c:if test="${not empty erro}">
        <table width="85%" align="center"><tr>
            <td style="color:red;font-weight:bold;"><c:out value="${erro}"/></td>
        </tr></table><br>
    </c:if>

    <%-- Dados do frete --%>
    <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
        <tr><td colspan="4" class="tabela"><b>Dados do Frete</b></td></tr>
        <tr>
            <td class="CelulaZebra1" width="20%">Número:</td>
            <td class="CelulaZebra1"><c:out value="${frete.numero}"/></td>
            <td class="CelulaZebra1" width="20%">Status:</td>
            <td class="CelulaZebra1">
                <span class="status-${frete.status}"><c:out value="${frete.status}"/></span>
            </td>
        </tr>
        <tr>
            <td class="CelulaZebra2">Remetente:</td>
            <td class="CelulaZebra2"><c:out value="${frete.remetente.nomeRazaoSocial}"/></td>
            <td class="CelulaZebra2">Destinatário:</td>
            <td class="CelulaZebra2"><c:out value="${frete.destinatario.nomeRazaoSocial}"/></td>
        </tr>
        <tr>
            <td class="CelulaZebra1">Motorista:</td>
            <td class="CelulaZebra1"><c:out value="${frete.motorista.nome}"/></td>
            <td class="CelulaZebra1">Veículo:</td>
            <td class="CelulaZebra1"><c:out value="${frete.veiculo.placa}"/></td>
        </tr>
        <tr>
            <td class="CelulaZebra2">Origem:</td>
            <td class="CelulaZebra2"><c:out value="${frete.municipioOrigem}"/>/<c:out value="${frete.ufOrigem}"/></td>
            <td class="CelulaZebra2">Destino:</td>
            <td class="CelulaZebra2"><c:out value="${frete.municipioDestino}"/>/<c:out value="${frete.ufDestino}"/></td>
        </tr>
        <tr>
            <td class="CelulaZebra1">Previsão Entrega:</td>
            <td class="CelulaZebra1"><c:out value="${frete.dataPrevisaoEntrega}"/></td>
            <td class="CelulaZebra1">Emissão:</td>
            <td class="CelulaZebra1"><c:out value="${frete.dataEmissao}"/></td>
        </tr>
        <tr>
            <td class="CelulaZebra2">Peso (kg):</td>
            <td class="CelulaZebra2"><c:out value="${frete.pesoKg}"/></td>
            <td class="CelulaZebra2">Valor Total:</td>
            <td class="CelulaZebra2">R$ <c:out value="${frete.valorTotal}"/></td>
        </tr>
    </table><br>

    <%-- Ações disponíveis por status --%>
    <table class="bordaFina" width="85%" align="center">
        <tr class="CelulaZebra1">
            <td align="center">
                <c:if test="${frete.status == 'EMITIDO'}">
                    <a href="${pageContext.request.contextPath}/FreteControlador?acao=confirmarSaida&id=${frete.id}">
                        <input type="button" class="inputbotao" value="✓ Confirmar Saída"/>
                    </a>
                    &nbsp;
                    <a href="${pageContext.request.contextPath}/FreteControlador?acao=cancelar&id=${frete.id}"
                       onclick="return confirm('Cancelar o frete ${frete.numero}?')">
                        <input type="button" class="inputbotao" value="✗ Cancelar Frete"/>
                    </a>
                </c:if>
                <c:if test="${frete.status == 'SAIDA_CONFIRMADA' or frete.status == 'EM_TRANSITO'}">
                    <a href="${pageContext.request.contextPath}/FreteControlador?acao=novaOcorrencia&id=${frete.id}">
                        <input type="button" class="inputbotao" value="+ Registrar Ocorrência"/>
                    </a>
                </c:if>
            </td>
        </tr>
    </table><br>

    <%-- Ocorrências --%>
    <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
        <tr><td colspan="5" class="tabela"><b>Histórico de Ocorrências</b></td></tr>
        <tr>
            <td class="tabela" width="20%">DATA/HORA</td>
            <td class="tabela" width="20%">TIPO</td>
            <td class="tabela" width="15%">MUNICÍPIO/UF</td>
            <td class="tabela" width="25%">DESCRIÇÃO</td>
            <td class="tabela" width="20%">RECEBEDOR</td>
        </tr>
        <c:forEach var="oc" varStatus="st" items="${frete.ocorrencias}">
        <tr class="${st.count % 2 == 0 ? 'CelulaZebra1' : 'CelulaZebra2'}">
            <td><c:out value="${oc.dataHora}"/></td>
            <td><c:out value="${oc.tipo}"/></td>
            <td><c:out value="${oc.municipio}"/>/<c:out value="${oc.uf}"/></td>
            <td><c:out value="${oc.descricao}"/></td>
            <td><c:out value="${oc.nomeRecebedor}"/></td>
        </tr>
        </c:forEach>
        <c:if test="${empty frete.ocorrencias}">
            <tr><td colspan="5" class="CelulaZebra1" align="center">Nenhuma ocorrência registrada.</td></tr>
        </c:if>
    </table>
</body>
</html>