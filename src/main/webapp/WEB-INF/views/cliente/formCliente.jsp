<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Cliente - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>

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
        <div class="alert alert-error"><c:out value="${erro}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/ClienteControlador" method="post" name="formulario">
        <c:choose>
            <c:when test="${cliente.id > 0}">
                <input type="hidden" name="acao" value="atualizar">
                <input type="hidden" name="id"   value="${cliente.id}">
            </c:when>
            <c:otherwise>
                <input type="hidden" name="acao" value="salvar">
            </c:otherwise>
        </c:choose>

        <table class="bordaFina" width="85%" align="center" cellpadding="2" cellspacing="1">
            <tr><td colspan="4" class="tabela"><b>Dados Cadastrais</b></td></tr>

            <tr>
                <td class="CelulaZebra1" width="20%">Tipo Pessoa: *</td>
                <td class="CelulaZebra1" colspan="3">
                    <input type="radio" name="tipoPessoa" id="tipoPF" value="F"
                           onchange="adaptarFormulario('F')"
                           ${cliente.tipoPessoa == 'F' || empty cliente.tipoPessoa ? 'checked' : ''}/>
                    <label for="tipoPF">Pessoa Física (CPF)</label>
                    &nbsp;&nbsp;
                    <input type="radio" name="tipoPessoa" id="tipoPJ" value="J"
                           onchange="adaptarFormulario('J')"
                           ${cliente.tipoPessoa == 'J' ? 'checked' : ''}/>
                    <label for="tipoPJ">Pessoa Jurídica (CNPJ)</label>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2" id="labelNome">Nome: *</td>
                <td class="CelulaZebra2" colspan="3">
                    <input type="text" name="nomeRazaoSocial" id="nomeRazaoSocial"
                           class="inputtexto" size="60" maxlength="150"
                           value="<c:out value='${cliente.nomeRazaoSocial}'/>"/>
                </td>
            </tr>
            <tr id="trNomeFantasia">
                <td class="CelulaZebra1">Nome Fantasia:</td>
                <td class="CelulaZebra1" colspan="3">
                    <input type="text" name="nomeFantasia" class="inputtexto" size="60" maxlength="150"
                           value="<c:out value='${cliente.nomeFantasia}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra2" id="labelDocumento">CPF: *</td>
                <td class="CelulaZebra2">
                    <input type="text" name="documento" id="documento"
                           class="inputtexto" size="20" maxlength="18"
                           placeholder="000.000.000-00"
                           value="<c:out value='${cliente.documentoFormatado}'/>"/>
                </td>
                <td class="CelulaZebra2" id="labelIE">Inscrição Estadual:</td>
                <td class="CelulaZebra2" id="tdIE">
                    <input type="text" name="inscricaoEstadual" class="inputtexto"
                           size="20" maxlength="20"
                           value="<c:out value='${cliente.inscricaoEstadual}'/>"/>
                </td>
            </tr>
            <tr>
                <td class="CelulaZebra1">Status:</td>
                <td class="CelulaZebra1">
                    <select name="status" class="inputtexto">
                        <option value="ATIVO"   ${cliente.status == 'ATIVO'   || empty cliente.status ? 'selected' : ''}>Ativo</option>
                        <option value="INATIVO" ${cliente.status == 'INATIVO' ? 'selected' : ''}>Inativo</option>
                    </select>
                </td>
                <td class="CelulaZebra1" colspan="2"></td>
            </tr>

            <tr><td colspan="4" class="tabela"><b>Endereço</b></td></tr>

            <tr>
                <td class="CelulaZebra1">CEP:</td>
                <td class="CelulaZebra1">
                    <%-- maxlength=9 alinhado com VARCHAR(9) no banco --%>
                    <input type="text" name="cep" id="cep" class="inputtexto"
                           size="10" maxlength="9" style="max-width: 150px;"
                           placeholder="00000-000"
                           value="<c:out value='${cliente.cep}'/>"/>
                </td>
                <td class="CelulaZebra1">UF:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="uf" class="inputtexto" size="3" maxlength="2"
                           style="max-width: 72px;"
                           value="<c:out value='${cliente.uf}'/>"/>
                </td>
            </tr>
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
                           style="max-width: 130px;"
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
                <td class="CelulaZebra1">Município:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="municipio" class="inputtexto" size="30" maxlength="100"
                           value="<c:out value='${cliente.municipio}'/>"/>
                </td>
            </tr>

            <tr><td colspan="4" class="tabela"><b>Contato</b></td></tr>

            <tr>
                <td class="CelulaZebra1">Telefone:</td>
                <td class="CelulaZebra1">
                    <input type="text" name="telefone" id="telefone" class="inputtexto"
                           size="20" maxlength="15"
                           placeholder="(00) 00000-0000"
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

    <script>
        function adaptarFormulario(tipo) {
            var isPJ = tipo === 'J';
            document.getElementById('labelNome').textContent      = isPJ ? 'Razão Social: *' : 'Nome: *';
            document.getElementById('labelDocumento').textContent = isPJ ? 'CNPJ: *'         : 'CPF: *';
            document.getElementById('documento').maxLength        = isPJ ? 18 : 14;
            document.getElementById('documento').placeholder      = isPJ ? '00.000.000/0000-00' : '000.000.000-00';
            document.getElementById('trNomeFantasia').style.display = isPJ ? '' : 'none';
            document.getElementById('labelIE').style.display        = isPJ ? '' : 'none';
            document.getElementById('tdIE').style.display           = isPJ ? '' : 'none';
            document.getElementById('documento').value = '';
        }

        function mascaraCpf(v) {
            v = v.replace(/\D/g, '').substring(0, 11);
            v = v.replace(/(\d{3})(\d)/, '$1.$2');
            v = v.replace(/(\d{3})(\d)/, '$1.$2');
            v = v.replace(/(\d{3})(\d{1,2})$/, '$1-$2');
            return v;
        }

        function mascaraCnpj(v) {
            v = v.replace(/\D/g, '').substring(0, 14);
            v = v.replace(/^(\d{2})(\d)/, '$1.$2');
            v = v.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3');
            v = v.replace(/\.(\d{3})(\d)/, '.$1/$2');
            v = v.replace(/(\d{4})(\d)/, '$1-$2');
            return v;
        }

        function mascaraCep(v) {
            v = v.replace(/\D/g, '').substring(0, 8);
            v = v.replace(/(\d{5})(\d)/, '$1-$2');
            return v;
        }

        function mascaraTelefone(v) {
            v = v.replace(/\D/g, '').substring(0, 11);
            if (v.length <= 10) {
                v = v.replace(/^(\d{2})(\d)/, '($1) $2');
                v = v.replace(/(\d{4})(\d{4})$/, '$1-$2');
            } else {
                v = v.replace(/^(\d{2})(\d)/, '($1) $2');
                v = v.replace(/(\d{5})(\d{4})$/, '$1-$2');
            }
            return v;
        }

        var ultimoCepConsultado = '';
        var consultaCepAtual = 0;

        function campoFormulario(nome) {
            return document.forms.formulario.elements[nome];
        }

        function preencherEnderecoPorCep(dados) {
            campoFormulario('logradouro').value = dados.logradouro || '';
            campoFormulario('bairro').value = dados.bairro || '';
            campoFormulario('municipio').value = dados.localidade || '';
            campoFormulario('uf').value = dados.uf || '';
            campoFormulario('numero').focus();
        }

        function consultarCep() {
            var cep = campoFormulario('cep').value.replace(/\D/g, '');

            if (cep.length !== 8 || cep === ultimoCepConsultado) {
                return;
            }

            ultimoCepConsultado = cep;
            var consulta = ++consultaCepAtual;

            fetch('https://viacep.com.br/ws/' + cep + '/json/')
                .then(function(response) {
                    if (!response.ok) {
                        throw new Error('Erro ao consultar CEP');
                    }
                    return response.json();
                })
                .then(function(dados) {
                    if (consulta !== consultaCepAtual) {
                        return;
                    }

                    if (dados.erro) {
                        alert('CEP não encontrado.');
                        return;
                    }

                    preencherEnderecoPorCep(dados);
                })
                .catch(function() {
                    if (consulta === consultaCepAtual) {
                        ultimoCepConsultado = '';
                        alert('Não foi possível consultar o CEP agora.');
                    }
                });
        }

        document.getElementById('documento').addEventListener('input', function() {
            var tipo = document.querySelector('input[name="tipoPessoa"]:checked').value;
            this.value = tipo === 'F' ? mascaraCpf(this.value) : mascaraCnpj(this.value);
        });

        document.getElementById('cep').addEventListener('input', function() {
            this.value = mascaraCep(this.value);
            consultarCep();
        });

        document.getElementById('cep').addEventListener('blur', consultarCep);

        document.getElementById('telefone').addEventListener('input', function() {
            this.value = mascaraTelefone(this.value);
        });

        // Inicializar ao carregar — não limpa documento em edição
        window.addEventListener('load', function() {
            var tipo = document.querySelector('input[name="tipoPessoa"]:checked').value;
            var isPJ = tipo === 'J';
            document.getElementById('labelNome').textContent      = isPJ ? 'Razão Social: *' : 'Nome: *';
            document.getElementById('labelDocumento').textContent = isPJ ? 'CNPJ: *'         : 'CPF: *';
            document.getElementById('documento').maxLength        = isPJ ? 18 : 14;
            document.getElementById('documento').placeholder      = isPJ ? '00.000.000/0000-00' : '000.000.000-00';
            document.getElementById('trNomeFantasia').style.display = isPJ ? '' : 'none';
            document.getElementById('labelIE').style.display        = isPJ ? '' : 'none';
            document.getElementById('tdIE').style.display           = isPJ ? '' : 'none';
            document.getElementById('documento').value = isPJ
                ? mascaraCnpj(document.getElementById('documento').value)
                : mascaraCpf(document.getElementById('documento').value);
        });
    </script>
</body>
</html>
