<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Novo Frete - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Emissão e planejamento de transporte</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=listar">Voltar</a>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Fretes</span>
                <h2 class="app-title">Emissão de frete</h2>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error" role="alert"><c:out value="${erro}"/></div>
        </c:if>

        <section class="card">
            <form action="${pageContext.request.contextPath}/FreteControlador" method="post">
                <input type="hidden" name="acao" value="emitir">
                <div class="form-grid">
                    <div class="section-title">Partes envolvidas</div>
                    <div class="form-field">
                        <label for="idRemetente">Remetente <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <select name="idRemetente" id="idRemetente" class="inputtexto">
                            <option value="">Selecione...</option>
                            <c:forEach var="c" items="${listaClientes}">
                                <option value="${c.id}"><c:out value="${c.nomeRazaoSocial}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="idDestinatario">Destinatário <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <select name="idDestinatario" id="idDestinatario" class="inputtexto">
                            <option value="">Selecione...</option>
                            <c:forEach var="c" items="${listaClientes}">
                                <option value="${c.id}"><c:out value="${c.nomeRazaoSocial}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="idMotorista">Motorista <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <select name="idMotorista" id="idMotorista" class="inputtexto">
                            <option value="">Selecione...</option>
                            <c:forEach var="m" items="${listaMotoristas}">
                                <option value="${m.id}"><c:out value="${m.nome}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="idVeiculo">Veículo <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <select name="idVeiculo" id="idVeiculo" class="inputtexto">
                            <option value="">Selecione...</option>
                            <c:forEach var="v" items="${listaVeiculos}">
                                <option value="${v.id}"><c:out value="${v.placa}"/> - <c:out value="${v.tipo}"/></option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="section-title">Origem e destino</div>
                    <div class="form-field">
                        <label for="ufOrigem">UF origem <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <select name="ufOrigem" id="ufOrigem" class="inputtexto"
                                data-selected="<c:out value='${frete.ufOrigem}'/>">
                            <option value="">Carregando UFs...</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="municipioOrigem">Município origem <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <select name="municipioOrigem" id="municipioOrigem" class="inputtexto"
                                data-selected="<c:out value='${frete.municipioOrigem}'/>" disabled>
                            <option value="">Selecione a UF origem</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="ufDestino">UF destino <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <select name="ufDestino" id="ufDestino" class="inputtexto"
                                data-selected="<c:out value='${frete.ufDestino}'/>">
                            <option value="">Carregando UFs...</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="municipioDestino">Município destino <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <select name="municipioDestino" id="municipioDestino" class="inputtexto"
                                data-selected="<c:out value='${frete.municipioDestino}'/>" disabled>
                            <option value="">Selecione a UF destino</option>
                        </select>
                    </div>

                    <div class="section-title">Carga</div>
                    <div class="form-field full">
                        <label for="descricaoCarga">Descrição</label>
                        <input type="text" name="descricaoCarga" id="descricaoCarga" class="inputtexto"
                               value="<c:out value='${frete.descricaoCarga}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="pesoKg">Peso (kg)</label>
                        <input type="text" name="pesoKg" id="pesoKg" class="inputtexto"
                               inputmode="decimal" pattern="[0-9]+([.,][0-9]{1,2})?"
                               value="<c:out value='${frete.pesoKg}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="volumes">Volumes</label>
                        <input type="text" name="volumes" id="volumes" class="inputtexto"
                               inputmode="numeric" pattern="[0-9]*"
                               value="<c:out value='${frete.volumes}'/>"/>
                    </div>

                    <div class="section-title">Financeiro</div>
                    <div class="form-field">
                        <label for="valorFrete">Valor frete (R$)</label>
                        <input type="text" name="valorFrete" id="valorFrete" class="inputtexto"
                               inputmode="decimal" pattern="[0-9]+([.,][0-9]{1,2})?"
                               value="<c:out value='${frete.valorFrete}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="aliquotaIcms">Alíquota ICMS (%)</label>
                        <input type="text" name="aliquotaIcms" id="aliquotaIcms" class="inputtexto"
                               inputmode="decimal" pattern="[0-9]+([.,][0-9]{1,2})?"
                               value="<c:out value='${frete.aliquotaIcms}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="dataPrevisaoEntrega">Previsão de entrega <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <input type="date" name="dataPrevisaoEntrega" id="dataPrevisaoEntrega" class="inputtexto"
                               value="<c:out value='${frete.dataPrevisaoEntrega}'/>"/>
                    </div>
                    <div class="form-actions full">
                        <input type="submit" class="inputbotao" value="Emitir frete"/>
                        <input type="button" class="inputbotao secondary" value="Cancelar"
                               onclick="window.location='${pageContext.request.contextPath}/FreteControlador?acao=listar'"/>
                    </div>
                </div>
            </form>
        </section>
    </main>
    <script src="${pageContext.request.contextPath}/js/localidades.js"></script>
    <script>
        LocalidadesIBGE.configurar([
            {
                ufId: 'ufOrigem',
                municipioId: 'municipioOrigem',
                municipioSemUf: 'Selecione a UF origem'
            },
            {
                ufId: 'ufDestino',
                municipioId: 'municipioDestino',
                municipioSemUf: 'Selecione a UF destino'
            }
        ]);

        function somenteDigitos(valor) {
            return valor.replace(/\D/g, '');
        }

        function somenteDecimal(valor) {
            var normalizado = valor.replace(',', '.').replace(/[^0-9.]/g, '');
            var partes = normalizado.split('.');
            var inteiro = partes.shift();
            var decimal = partes.join('').substring(0, 2);
            return decimal ? inteiro + '.' + decimal : inteiro;
        }

        function aplicarMascaraNumerica(id, formatar) {
            var campo = document.getElementById(id);
            if (!campo) return;
            campo.value = formatar(campo.value);
            campo.addEventListener('input', function() {
                this.value = formatar(this.value);
            });
        }

        aplicarMascaraNumerica('pesoKg', somenteDecimal);
        aplicarMascaraNumerica('volumes', somenteDigitos);
        aplicarMascaraNumerica('valorFrete', somenteDecimal);
        aplicarMascaraNumerica('aliquotaIcms', somenteDecimal);
    </script>
</body>
</html>
