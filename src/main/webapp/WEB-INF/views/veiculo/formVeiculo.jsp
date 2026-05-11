<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Veículo - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">
                <c:choose>
                    <c:when test="${veiculo.id > 0}">Editar Veículo</c:when>
                    <c:otherwise>Novo Veículo</c:otherwise>
                </c:choose>
            </span></td>
            <td align="right">
                <a href="${pageContext.request.contextPath}/VeiculoControlador?acao=listar">← Voltar</a>
            </td>
        </tr>
    </table><br>
    <c:if test="${not empty erro}">
        <div class="alert alert-error"><c:out value="${erro}"/></div>
    </c:if>
    <form action="${pageContext.request.contextPath}/VeiculoControlador" method="post">
        <c:choose>
            <c:when test="${veiculo.id > 0}">
                <input type="hidden" name="acao" value="atualizar">
                <input type="hidden" name="id"   value="${veiculo.id}">
            </c:when>
            <c:otherwise>
                <input type="hidden" name="acao" value="salvar">
            </c:otherwise>
        </c:choose>
        <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
            <tr>
                <td class="CelulaZebra1" width="25%">Placa: *</td>
                <td class="CelulaZebra1">
                    <input type="text" name="placa" id="placa" class="inputtexto" size="10" maxlength="8"
                           autocapitalize="characters" autocomplete="off"
                           placeholder="ABC-1234 ou ABC1D23"
                           value="<c:out value='${veiculo.placa}'/>" style="text-transform:uppercase"/>
                </td>
                <td class="CelulaZebra1">RNTRC:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="rntrc" id="rntrc" class="inputtexto" size="20" maxlength="8"
                           inputmode="numeric" pattern="[0-9]*" autocomplete="off"
                           placeholder="00000000"
                           value="<c:out value='${veiculo.rntrc}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Tipo: *</td>
                <td class="CelulaZebra2">
                    <select name="tipo" class="inputtexto">
                        <option value="">Selecione...</option>
                        <option value="TRUCK"     ${veiculo.tipo == 'TRUCK'     ? 'selected' : ''}>Truck</option>
                        <option value="CARRETA"   ${veiculo.tipo == 'CARRETA'   ? 'selected' : ''}>Carreta</option>
                        <option value="VAN"       ${veiculo.tipo == 'VAN'       ? 'selected' : ''}>Van</option>
                        <option value="UTILITARIO"${veiculo.tipo == 'UTILITARIO'? 'selected' : ''}>Utilitário</option>
                    </select>
                </td>
                <td class="CelulaZebra2">Ano Fabricação: *</td>
                <td class="CelulaZebra2">
                    <input type="text" name="anoFabricacao" id="anoFabricacao" class="inputtexto"
                           size="6" maxlength="4" inputmode="numeric" pattern="[0-9]*"
                           value="<c:out value='${veiculo.anoFabricacao}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra1">Tara (kg):</td>
                <td class="CelulaZebra1">
                    <input type="text" name="taraKg" id="taraKg" class="inputtexto" size="12"
                           inputmode="decimal" pattern="[0-9]+([.,][0-9]{1,2})?"
                           value="<c:out value='${veiculo.taraKg}'/>"/>
                </td>
                <td class="CelulaZebra1">Capacidade (kg): *</td>
                <td class="CelulaZebra1">
                    <input type="text" name="capacidadeKg" id="capacidadeKg" class="inputtexto" size="12"
                           inputmode="decimal" pattern="[0-9]+([.,][0-9]{1,2})?"
                           value="<c:out value='${veiculo.capacidadeKg}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Volume (m³):</td>
                <td class="CelulaZebra2">
                    <input type="text" name="volumeM3" id="volumeM3" class="inputtexto" size="10"
                           inputmode="decimal" pattern="[0-9]+([.,][0-9]{1,2})?"
                           value="<c:out value='${veiculo.volumeM3}'/>"/>
                </td>
                <td class="CelulaZebra2">Status:</td>
                <td class="CelulaZebra2">
                    <select name="status" class="inputtexto">
                        <option value="DISPONIVEL"   ${veiculo.status == 'DISPONIVEL'   ? 'selected' : ''}>Disponível</option>
                        <option value="RESERVADO"    ${veiculo.status == 'RESERVADO'    ? 'selected' : ''}>Reservado</option>
                        <option value="EM_VIAGEM"    ${veiculo.status == 'EM_VIAGEM'    ? 'selected' : ''}>Em Viagem</option>
                        <option value="EM_MANUTENCAO"${veiculo.status == 'EM_MANUTENCAO'? 'selected' : ''}>Em Manutenção</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="4" align="center" class="CelulaZebra1">
                    <input type="submit" class="inputbotao" value="Salvar"/>
                    &nbsp;
                    <input type="button" class="inputbotao" value="Cancelar"
                           onclick="window.location='${pageContext.request.contextPath}/VeiculoControlador?acao=listar'"/>
                </td>
            </tr>
        </table>
    </form>
    <script>
        function aplicarMascaraPlaca() {
            var campo = document.getElementById('placa');
            if (!campo) return;

            function formatar(valor) {
                var placa = valor.toUpperCase().replace(/[^A-Z0-9]/g, '').substring(0, 7);
                if (/^[A-Z]{3}\d{1,4}$/.test(placa)) {
                    return placa.replace(/^([A-Z]{3})(\d{1,4})$/, '$1-$2');
                }
                return placa;
            }

            campo.value = formatar(campo.value);
            campo.addEventListener('input', function() {
                this.value = formatar(this.value);
            });
        }

        aplicarMascaraPlaca();

        function somenteDigitos(valor, tamanho) {
            return valor.replace(/\D/g, '').substring(0, tamanho);
        }

        function somenteDecimal(valor) {
            var normalizado = valor.replace(',', '.').replace(/[^0-9.]/g, '');
            var partes = normalizado.split('.');
            var inteiro = partes.shift();
            var decimal = partes.join('').substring(0, 2);
            return decimal ? inteiro + '.' + decimal : inteiro;
        }

        function aplicarMascara(id, formatar) {
            var campo = document.getElementById(id);
            if (!campo) return;
            campo.value = formatar(campo.value);
            campo.addEventListener('input', function() {
                this.value = formatar(this.value);
            });
        }

        aplicarMascara('rntrc', function(valor) { return somenteDigitos(valor, 8); });
        aplicarMascara('anoFabricacao', function(valor) { return somenteDigitos(valor, 4); });
        aplicarMascara('taraKg', somenteDecimal);
        aplicarMascara('capacidadeKg', somenteDecimal);
        aplicarMascara('volumeM3', somenteDecimal);
    </script>
</body>
</html>
