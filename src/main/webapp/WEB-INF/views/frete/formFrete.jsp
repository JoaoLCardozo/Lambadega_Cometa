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
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>

        <section class="card">
            <form action="${pageContext.request.contextPath}/FreteControlador" method="post">
                <input type="hidden" name="acao" value="emitir">
                <div class="form-grid">
                    <div class="section-title">Partes envolvidas</div>
                    <div class="form-field">
                        <label for="idRemetente">Remetente *</label>
                        <select name="idRemetente" id="idRemetente" class="inputtexto">
                            <option value="">Selecione...</option>
                            <c:forEach var="c" items="${listaClientes}">
                                <option value="${c.id}"><c:out value="${c.nomeRazaoSocial}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="idDestinatario">Destinatário *</label>
                        <select name="idDestinatario" id="idDestinatario" class="inputtexto">
                            <option value="">Selecione...</option>
                            <c:forEach var="c" items="${listaClientes}">
                                <option value="${c.id}"><c:out value="${c.nomeRazaoSocial}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="idMotorista">Motorista *</label>
                        <select name="idMotorista" id="idMotorista" class="inputtexto">
                            <option value="">Selecione...</option>
                            <c:forEach var="m" items="${listaMotoristas}">
                                <option value="${m.id}"><c:out value="${m.nome}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="idVeiculo">Veículo *</label>
                        <select name="idVeiculo" id="idVeiculo" class="inputtexto">
                            <option value="">Selecione...</option>
                            <c:forEach var="v" items="${listaVeiculos}">
                                <option value="${v.id}"><c:out value="${v.placa}"/> - <c:out value="${v.tipo}"/></option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="section-title">Origem e destino</div>
                    <div class="form-field">
                        <label for="municipioOrigem">Município origem *</label>
                        <input type="text" name="municipioOrigem" id="municipioOrigem" class="inputtexto"
                               value="<c:out value='${frete.municipioOrigem}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="ufOrigem">UF origem *</label>
                        <input type="text" name="ufOrigem" id="ufOrigem" class="inputtexto" maxlength="2"
                               value="<c:out value='${frete.ufOrigem}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="municipioDestino">Município destino *</label>
                        <input type="text" name="municipioDestino" id="municipioDestino" class="inputtexto"
                               value="<c:out value='${frete.municipioDestino}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="ufDestino">UF destino *</label>
                        <input type="text" name="ufDestino" id="ufDestino" class="inputtexto" maxlength="2"
                               value="<c:out value='${frete.ufDestino}'/>"/>
                    </div>

                    <div class="section-title">Carga</div>
                    <div class="form-field full">
                        <label for="descricaoCarga">Descrição</label>
                        <input type="text" name="descricaoCarga" id="descricaoCarga" class="inputtexto"
                               value="<c:out value='${frete.descricaoCarga}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="pesoKg">Peso (kg)</label>
                        <input type="number" step="0.01" name="pesoKg" id="pesoKg" class="inputtexto"
                               value="<c:out value='${frete.pesoKg}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="volumes">Volumes</label>
                        <input type="number" name="volumes" id="volumes" class="inputtexto"
                               value="<c:out value='${frete.volumes}'/>"/>
                    </div>

                    <div class="section-title">Financeiro</div>
                    <div class="form-field">
                        <label for="valorFrete">Valor frete (R$)</label>
                        <input type="number" step="0.01" name="valorFrete" id="valorFrete" class="inputtexto"
                               value="<c:out value='${frete.valorFrete}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="aliquotaIcms">Alíquota ICMS (%)</label>
                        <input type="number" step="0.01" name="aliquotaIcms" id="aliquotaIcms" class="inputtexto"
                               value="<c:out value='${frete.aliquotaIcms}'/>"/>
                    </div>
                    <div class="form-field">
                        <label for="dataPrevisaoEntrega">Previsão de entrega *</label>
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
</body>
</html>
