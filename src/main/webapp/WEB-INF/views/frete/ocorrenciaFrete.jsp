<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Registrar Ocorrência - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">
    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">Registrar Ocorrência — Frete <c:out value="${frete.numero}"/></span></td>
            <td align="right">
                <a href="${pageContext.request.contextPath}/FreteControlador?acao=detalhe&id=${frete.id}">← Voltar</a>
            </td>
        </tr>
    </table><br>
    <c:if test="${not empty erro}">
        <table width="85%" align="center"><tr>
            <td style="color:red;font-weight:bold;"><c:out value="${erro}"/></td>
        </tr></table><br>
    </c:if>
    <form action="${pageContext.request.contextPath}/FreteControlador" method="post">
        <input type="hidden" name="idFrete" value="${frete.id}">

        <c:choose>
            <c:when test="${frete.status == 'SAIDA_CONFIRMADA'}">
                <input type="hidden" name="acao" value="emTransito">
            </c:when>
            <c:when test="${frete.status == 'EM_TRANSITO'}">
                <input type="hidden" name="acao" value="ocorrencia">
            </c:when>
        </c:choose>

        <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
            <tr>
                <td class="CelulaZebra1" width="25%">Tipo: *</td>
                <td class="CelulaZebra1">
                    <select name="tipo" class="inputtexto" id="tipoOcorrencia"
                            onchange="toggleCampos(this.value)">
                        <option value="">Selecione...</option>
                        <c:if test="${frete.status == 'SAIDA_CONFIRMADA'}">
                            <option value="EM_ROTA">Em Rota</option>
                        </c:if>
                        <c:if test="${frete.status == 'EM_TRANSITO'}">
                            <option value="EM_ROTA">Em Rota</option>
                            <option value="ENTREGA_REALIZADA">Entrega Realizada</option>
                            <option value="TENTATIVA_ENTREGA">Tentativa de Entrega</option>
                            <option value="AVARIA">Avaria</option>
                            <option value="EXTRAVIO">Extravio</option>
                            <option value="OUTROS">Outros</option>
                        </c:if>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Data/Hora: *</td>
                <td class="CelulaZebra2">
                    <input type="datetime-local" name="dataHora" class="inputtexto"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra1">Município:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="municipio" class="inputtexto" size="30"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">UF:</td>
                <td class="CelulaZebra2">
                    <input type="text" name="uf" class="inputtexto" size="3" maxlength="2"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra1">Descrição:</td>
                <td class="CelulaZebra1">
                    <textarea name="descricao" class="inputtexto" rows="3" cols="50"></textarea>
                </td>
            </tr>
            <tr id="camposRecebedor" style="display:none">
                <td class="CelulaZebra2">Nome Recebedor: *</td>
                <td class="CelulaZebra2">
                    <input type="text" name="nomeRecebedor" class="inputtexto" size="40"/>
                </td>
            </tr>
            <tr id="camposDocumento" style="display:none">
                <td class="CelulaZebra1">Documento Recebedor: *</td>
                <td class="CelulaZebra1">
                    <input type="text" name="documentoRecebedor" class="inputtexto" size="20"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center" class="CelulaZebra2">
                    <input type="submit" class="inputbotao" value="Registrar"/>
                    &nbsp;
                    <input type="button" class="inputbotao" value="Cancelar"
                           onclick="window.location='${pageContext.request.contextPath}/FreteControlador?acao=detalhe&id=${frete.id}'"/>
                </td>
            </tr>
        </table>
    </form>
    <script>
        function toggleCampos(tipo) {
            var rec = document.getElementById('camposRecebedor');
            var doc = document.getElementById('camposDocumento');
            if (tipo === 'ENTREGA_REALIZADA') {
                rec.style.display = ''; doc.style.display = '';
            } else {
                rec.style.display = 'none'; doc.style.display = 'none';
            }
        }
    </script>
</body>
</html>