<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Novo Frete - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">
    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">Emissão de Frete</span></td>
            <td align="right">
                <a href="${pageContext.request.contextPath}/FreteControlador?acao=listar">← Voltar</a>
            </td>
        </tr>
    </table><br>
    <c:if test="${not empty erro}">
        <table width="85%" align="center"><tr>
            <td style="color:red;font-weight:bold;"><c:out value="${erro}"/></td>
        </tr></table><br>
    </c:if>
    <form action="${pageContext.request.contextPath}/FreteControlador" method="post">
        <input type="hidden" name="acao" value="emitir">
        <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
            <tr><td colspan="4" class="tabela"><b>Partes Envolvidas</b></td></tr>
            <tr>
                <td class="CelulaZebra1" width="20%">Remetente: *</td>
                <td class="CelulaZebra1">
                    <select name="idRemetente" class="inputtexto">
                        <option value="">Selecione...</option>
                        <c:forEach var="c" items="${listaClientes}">
                            <option value="${c.id}"><c:out value="${c.nomeRazaoSocial}"/></option>
                        </c:forEach>
                    </select>
                </td>
                <td class="CelulaZebra1">Destinatário: *</td>
                <td class="CelulaZebra1">
                    <select name="idDestinatario" class="inputtexto">
                        <option value="">Selecione...</option>
                        <c:forEach var="c" items="${listaClientes}">
                            <option value="${c.id}"><c:out value="${c.nomeRazaoSocial}"/></option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Motorista: *</td>
                <td class="CelulaZebra2">
                    <select name="idMotorista" class="inputtexto">
                        <option value="">Selecione...</option>
                        <c:forEach var="m" items="${listaMotoristas}">
                            <option value="${m.id}"><c:out value="${m.nome}"/></option>
                        </c:forEach>
                    </select>
                </td>
                <td class="CelulaZebra2">Veículo: *</td>
                <td class="CelulaZebra2">
                    <select name="idVeiculo" class="inputtexto">
                        <option value="">Selecione...</option>
                        <c:forEach var="v" items="${listaVeiculos}">
                            <option value="${v.id}"><c:out value="${v.placa}"/> - <c:out value="${v.tipo}"/></option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr><td colspan="4" class="tabela"><b>Origem / Destino</b></td></tr>
            <tr>
                <td class="CelulaZebra1">Município Origem: *</td>
                <td class="CelulaZebra1">
                    <input type="text" name="municipioOrigem" class="inputtexto" size="30"
                           value="<c:out value='${frete.municipioOrigem}'/>"/>
                </td>
                <td class="CelulaZebra1">UF Origem: *</td>
                <td class="CelulaZebra1">
                    <input type="text" name="ufOrigem" class="inputtexto" size="3" maxlength="2"
                           value="<c:out value='${frete.ufOrigem}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Município Destino: *</td>
                <td class="CelulaZebra2">
                    <input type="text" name="municipioDestino" class="inputtexto" size="30"
                           value="<c:out value='${frete.municipioDestino}'/>"/>
                </td>
                <td class="CelulaZebra2">UF Destino: *</td>
                <td class="CelulaZebra2">
                    <input type="text" name="ufDestino" class="inputtexto" size="3" maxlength="2"
                           value="<c:out value='${frete.ufDestino}'/>"/>
                </td>
            </tr>
            <tr><td colspan="4" class="tabela"><b>Carga</b></td></tr>
            <tr>
                <td class="CelulaZebra1">Descrição:</td>
                <td class="CelulaZebra1" colspan="3">
                    <input type="text" name="descricaoCarga" class="inputtexto" size="60"
                           value="<c:out value='${frete.descricaoCarga}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Peso (kg):</td>
                <td class="CelulaZebra2">
                    <input type="number" step="0.01" name="pesoKg" class="inputtexto" size="12"
                           value="<c:out value='${frete.pesoKg}'/>"/>
                </td>
                <td class="CelulaZebra2">Volumes:</td>
                <td class="CelulaZebra2">
                    <input type="number" name="volumes" class="inputtexto" size="6"
                           value="<c:out value='${frete.volumes}'/>"/>
                </td>
            </tr>
            <tr><td colspan="4" class="tabela"><b>Financeiro</b></td></tr>
            <tr>
                <td class="CelulaZebra1">Valor Frete (R$):</td>
                <td class="CelulaZebra1">
                    <input type="number" step="0.01" name="valorFrete" class="inputtexto" size="12"
                           value="<c:out value='${frete.valorFrete}'/>"/>
                </td>
                <td class="CelulaZebra1">Alíquota ICMS (%):</td>
                <td class="CelulaZebra1">
                    <input type="number" step="0.01" name="aliquotaIcms" class="inputtexto" size="6"
                           value="<c:out value='${frete.aliquotaIcms}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Previsão de Entrega: *</td>
                <td class="CelulaZebra2">
                    <input type="date" name="dataPrevisaoEntrega" class="inputtexto"
                           value="<c:out value='${frete.dataPrevisaoEntrega}'/>"/>
                </td>
                <td class="CelulaZebra2"></td>
                <td class="CelulaZebra2"></td>
            </tr>
            <tr>
                <td colspan="4" align="center" class="CelulaZebra1">
                    <input type="submit" class="inputbotao" value="Emitir Frete"/>
                    &nbsp;
                    <input type="button" class="inputbotao" value="Cancelar"
                           onclick="window.location='${pageContext.request.contextPath}/FreteControlador?acao=listar'"/>
                </td>
            </tr>
        </table>
    </form>
</body>
</html>