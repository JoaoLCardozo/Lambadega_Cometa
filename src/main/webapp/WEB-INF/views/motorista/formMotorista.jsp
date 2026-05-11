<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Motorista - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">
                <c:choose>
                    <c:when test="${motorista.id > 0}">Editar Motorista</c:when>
                    <c:otherwise>Novo Motorista</c:otherwise>
                </c:choose>
            </span></td>
            <td align="right">
                <a href="${pageContext.request.contextPath}/MotoristaControlador?acao=listar">← Voltar</a>
            </td>
        </tr>
    </table><br>
    <c:if test="${not empty erro}">
        <div class="alert alert-error" role="alert"><c:out value="${erro}"/></div>
    </c:if>
    <form action="${pageContext.request.contextPath}/MotoristaControlador" method="post">
        <c:choose>
            <c:when test="${motorista.id > 0}">
                <input type="hidden" name="acao" value="atualizar">
                <input type="hidden" name="id"   value="${motorista.id}">
            </c:when>
            <c:otherwise>
                <input type="hidden" name="acao" value="salvar">
            </c:otherwise>
        </c:choose>
        <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
            <tr>
                <td class="CelulaZebra1" width="20%">Nome: <span class="required-marker" aria-label="obrigatório">*</span></td>
                <td class="CelulaZebra1" colspan="3">
                    <input type="text" name="nome" class="inputtexto" size="50" maxlength="100"
                           value="<c:out value='${motorista.nome}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">CPF: <span class="required-marker" aria-label="obrigatório">*</span></td>
                <td class="CelulaZebra2">
                    <input type="text" name="cpf" id="cpf" class="inputtexto" size="15" maxlength="14"
                           inputmode="numeric" autocomplete="off"
                           placeholder="000.000.000-00"
                           value="<c:out value='${motorista.cpf}'/>"/>
                </td>
                <td class="CelulaZebra2">Data Nascimento:</td>
                <td class="CelulaZebra2">
                    <input type="date" name="dataNascimento" class="inputtexto"
                           value="<c:out value='${motorista.dataNascimento}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra1">Telefone:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="telefone" id="telefone" class="inputtexto" size="20" maxlength="15"
                           inputmode="tel" autocomplete="tel"
                           placeholder="(00) 00000-0000"
                           value="<c:out value='${motorista.telefone}'/>"/>
                </td>
                <td class="CelulaZebra1">Tipo Vínculo: <span class="required-marker" aria-label="obrigatório">*</span></td>
                <td class="CelulaZebra1">
                    <select name="tipoVinculo" class="inputtexto">
                        <option value="">Selecione...</option>
                        <option value="FUNCIONARIO" ${motorista.tipoVinculo == 'FUNCIONARIO' ? 'selected' : ''}>Funcionário</option>
                        <option value="AGREGADO"    ${motorista.tipoVinculo == 'AGREGADO'    ? 'selected' : ''}>Agregado</option>
                        <option value="TERCEIRO"    ${motorista.tipoVinculo == 'TERCEIRO'    ? 'selected' : ''}>Terceiro</option>
                    </select>
                </td>
            </tr>
            <tr><td colspan="4" class="tabela"><b>CNH</b></td></tr>
            <tr>
                <td class="CelulaZebra1">Número CNH: <span class="required-marker" aria-label="obrigatório">*</span></td>
                <td class="CelulaZebra1">
                    <input type="text" name="cnhNumero" id="cnhNumero" class="inputtexto" size="20" maxlength="11"
                           inputmode="numeric" pattern="[0-9]*" autocomplete="off"
                           placeholder="Somente numeros"
                           value="<c:out value='${motorista.cnhNumero}'/>"/>
                </td>
                <td class="CelulaZebra1">Categoria: <span class="required-marker" aria-label="obrigatório">*</span></td>
                <td class="CelulaZebra1">
                    <select name="cnhCategoria" class="inputtexto">
                        <option value="">Selecione...</option>
                        <c:forEach var="cat" items="${['A','B','C','D','E']}">
                            <option value="${cat}" ${motorista.cnhCategoria == cat ? 'selected' : ''}>${cat}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Validade CNH: <span class="required-marker" aria-label="obrigatório">*</span></td>
                <td class="CelulaZebra2">
                    <input type="date" name="cnhValidade" class="inputtexto"
                           value="<c:out value='${motorista.cnhValidade}'/>"/>
                </td>
                <td class="CelulaZebra2">Status:</td>
                <td class="CelulaZebra2">
                    <select name="status" class="inputtexto">
                        <option value="ATIVO"    ${motorista.status == 'ATIVO'    ? 'selected' : ''}>Ativo</option>
                        <option value="INATIVO"  ${motorista.status == 'INATIVO'  ? 'selected' : ''}>Inativo</option>
                        <option value="SUSPENSO" ${motorista.status == 'SUSPENSO' ? 'selected' : ''}>Suspenso</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="4" align="center" class="CelulaZebra1">
                    <input type="submit" class="inputbotao" value="Salvar"/>
                    &nbsp;
                    <input type="button" class="inputbotao" value="Cancelar"
                           onclick="window.location='${pageContext.request.contextPath}/MotoristaControlador?acao=listar'"/>
                </td>
            </tr>
        </table>
    </form>
    <script>
        function somenteDigitos(valor, tamanho) {
            return valor.replace(/\D/g, '').substring(0, tamanho);
        }

        function mascaraCpf(valor) {
            valor = somenteDigitos(valor, 11);
            valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
            valor = valor.replace(/(\d{3})(\d)/, '$1.$2');
            valor = valor.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
            return valor;
        }

        function mascaraTelefone(valor) {
            valor = somenteDigitos(valor, 11);
            if (valor.length <= 10) {
                valor = valor.replace(/^(\d{2})(\d)/, '($1) $2');
                valor = valor.replace(/(\d{4})(\d{1,4})$/, '$1-$2');
            } else {
                valor = valor.replace(/^(\d{2})(\d)/, '($1) $2');
                valor = valor.replace(/(\d{5})(\d{1,4})$/, '$1-$2');
            }
            return valor;
        }

        function aplicarMascara(id, mascara) {
            var campo = document.getElementById(id);
            if (!campo) return;
            campo.value = mascara(campo.value);
            campo.addEventListener('input', function() {
                this.value = mascara(this.value);
            });
        }

        aplicarMascara('cpf', mascaraCpf);
        aplicarMascara('telefone', mascaraTelefone);
        aplicarMascara('cnhNumero', function(valor) {
            return somenteDigitos(valor, 11);
        });
    </script>
</body>
</html>
