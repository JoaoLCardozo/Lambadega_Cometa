<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Cliente - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css" type="text/css">
</head>
<body>
    <img src="${pageContext.request.contextPath}/img/topo_frota.jpg" width="40%" height="44">

    <table class="bordaFina" width="85%" align="center">
        <tr>
            <td><span class="style4">
                <c:choose>
                    <c:when test="${cliente.id > 0}">Editar Cliente</c:when>
                    <c:otherwise>Novo Cliente</c:otherwise>
                </c:choose>
            </span></td>
            <td align="right">
                <a href="${pageContext.request.contextPath}/ClienteControlador?acao=listar">← Voltar</a>
            </td>
        </tr>
    </table>
    <br>

    <c:if test="${not empty erro}">
        <table width="85%" align="center">
            <tr><td style="color:red; font-weight:bold;"><c:out value="${erro}"/></td></tr>
        </table><br>
    </c:if>

    <form action="${pageContext.request.contextPath}/ClienteControlador" method="post" name="formulario">
        <c:choose>
            <c:when test="${cliente.id > 0}">
                <input type="hidden" name="acao" value="atualizar">
                <input type="hidden" name="id" value="${cliente.id}">
            </c:when>
            <c:otherwise>
                <input type="hidden" name="acao" value="salvar">
            </c:otherwise>
        </c:choose>

        <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
            <tr><td colspan="4" class="tabela"><b>Dados Cadastrais</b></td></tr>

            <tr>
                <td class="CelulaZebra1" width="20%">Razão Social: *</td>
                <td class="CelulaZebra1" colspan="3">
                    <input type="text" name="razaoSocial" class="inputtexto" size="60" maxlength="150"
                           value="<c:out value='${cliente.razaoSocial}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Nome Fantasia:</td>
                <td class="CelulaZebra2" colspan="3">
                    <input type="text" name="nomeFantasia" class="inputtexto" size="60" maxlength="150"
                           value="<c:out value='${cliente.nomeFantasia}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra1">CNPJ: *</td>
                <td class="CelulaZebra1">
                    <input type="text" name="cnpj" class="inputtexto" size="20" maxlength="18"
                           value="<c:out value='${cliente.cnpj}'/>"/>
                </td>
                <td class="CelulaZebra1">Inscrição Estadual:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="inscricaoEstadual" class="inputtexto" size="20" maxlength="20"
                           value="<c:out value='${cliente.inscricaoEstadual}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Tipo: *</td>
                <td class="CelulaZebra2">
                    <select name="tipo" class="inputtexto">
                        <option value="">Selecione...</option>
                        <option value="REMETENTE"   ${cliente.tipo == 'REMETENTE'   ? 'selected' : ''}>Remetente</option>
                        <option value="DESTINATARIO"${cliente.tipo == 'DESTINATARIO'? 'selected' : ''}>Destinatário</option>
                        <option value="AMBOS"       ${cliente.tipo == 'AMBOS'       ? 'selected' : ''}>Ambos</option>
                    </select>
                </td>
                <td class="CelulaZebra2">Status:</td>
                <td class="CelulaZebra2">
                    <select name="status" class="inputtexto">
                        <option value="ATIVO"  ${cliente.status == 'ATIVO'  ? 'selected' : ''}>Ativo</option>
                        <option value="INATIVO"${cliente.status == 'INATIVO'? 'selected' : ''}>Inativo</option>
                    </select>
                </td>
            </tr>

            <tr><td colspan="4" class="tabela"><b>Endereço</b></td></tr>

            <tr>
                <td class="CelulaZebra1">Logradouro:</td>
                <td class="CelulaZebra1" colspan="3">
                    <input type="text" name="logradouro" class="inputtexto" size="60" maxlength="150"
                           value="<c:out value='${cliente.logradouro}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Número:</td>
                <td class="CelulaZebra2">
                    <input type="text" name="numero" class="inputtexto" size="10" maxlength="10"
                           value="<c:out value='${cliente.numero}'/>"/>
                </td>
                <td class="CelulaZebra2">Complemento:</td>
                <td class="CelulaZebra2">
                    <input type="text" name="complemento" class="inputtexto" size="30" maxlength="100"
                           value="<c:out value='${cliente.complemento}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra1">Bairro:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="bairro" class="inputtexto" size="30" maxlength="100"
                           value="<c:out value='${cliente.bairro}'/>"/>
                </td>
                <td class="CelulaZebra1">CEP:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="cep" class="inputtexto" size="10" maxlength="9"
                           value="<c:out value='${cliente.cep}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2">Município:</td>
                <td class="CelulaZebra2">
                    <input type="text" name="municipio" class="inputtexto" size="30" maxlength="100"
                           value="<c:out value='${cliente.municipio}'/>"/>
                </td>
                <td class="CelulaZebra2">UF:</td>
                <td class="CelulaZebra2">
                    <input type="text" name="uf" class="inputtexto" size="3" maxlength="2"
                           value="<c:out value='${cliente.uf}'/>"/>
                </td>
            </tr>

            <tr><td colspan="4" class="tabela"><b>Contato</b></td></tr>

            <tr>
                <td class="CelulaZebra1">Telefone:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="telefone" class="inputtexto" size="20" maxlength="20"
                           value="<c:out value='${cliente.telefone}'/>"/>
                </td>
                <td class="CelulaZebra1">E-mail:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="email" class="inputtexto" size="40" maxlength="100"
                           value="<c:out value='${cliente.email}'/>"/>
                </td>
            </tr>

            <tr>
                <td colspan="4" align="center" class="CelulaZebra2">
                    <input type="submit" class="inputbotao" value="Salvar"/>
                    &nbsp;
                    <input type="button" class="inputbotao" value="Cancelar"
                           onclick="window.location='${pageContext.request.contextPath}/ClienteControlador?acao=listar'"/>
                </td>
            </tr>
        </table>
    </form>
</body>
</html>