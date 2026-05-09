<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Registrar Ocorrência - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Atualização de andamento do frete</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=detalhe&id=${frete.id}">Voltar</a>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Ocorrência</span>
                <h2 class="app-title">Frete <c:out value="${frete.numero}"/></h2>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>

        <section class="card">
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

                <div class="form-grid">
                    <div class="form-field full">
                        <span class="app-eyebrow">Registro operacional</span>
                        <h3 class="app-title">Dados da ocorrência</h3>
                    </div>
                    <div class="form-field">
                        <label for="tipoOcorrencia">Tipo *</label>
                        <select name="tipo" class="inputtexto" id="tipoOcorrencia" onchange="toggleCampos(this.value)">
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
                    </div>
                    <div class="form-field">
                        <label for="dataHora">Data/Hora *</label>
                        <input type="datetime-local" name="dataHora" id="dataHora" class="inputtexto"/>
                    </div>
                    <div class="form-field">
                        <label for="uf">UF</label>
                        <select name="uf" id="uf" class="inputtexto">
                            <option value="">Carregando UFs...</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="municipio">Município</label>
                        <select name="municipio" id="municipio" class="inputtexto" disabled>
                            <option value="">Selecione a UF</option>
                        </select>
                    </div>
                    <div class="form-field full">
                        <label for="descricao">Descrição</label>
                        <textarea name="descricao" id="descricao" class="inputtexto" rows="3"></textarea>
                    </div>
                    <div class="form-field" id="camposRecebedor" style="display:none">
                        <label for="nomeRecebedor">Nome recebedor *</label>
                        <input type="text" name="nomeRecebedor" id="nomeRecebedor" class="inputtexto"/>
                    </div>
                    <div class="form-field" id="camposDocumento" style="display:none">
                        <label for="documentoRecebedor">Documento recebedor *</label>
                        <input type="text" name="documentoRecebedor" id="documentoRecebedor" class="inputtexto"/>
                    </div>
                    <div class="form-actions full">
                        <input type="submit" class="inputbotao" value="Registrar"/>
                        <input type="button" class="inputbotao secondary" value="Cancelar"
                               onclick="window.location='${pageContext.request.contextPath}/FreteControlador?acao=detalhe&id=${frete.id}'"/>
                    </div>
                </div>
            </form>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/js/localidades.js"></script>
    <script>
        LocalidadesIBGE.configurar([
            {
                ufId: 'uf',
                municipioId: 'municipio',
                municipioSemUf: 'Selecione a UF'
            }
        ]);

        function toggleCampos(tipo) {
            var rec = document.getElementById('camposRecebedor');
            var doc = document.getElementById('camposDocumento');
            if (tipo === 'ENTREGA_REALIZADA') {
                rec.style.display = '';
                doc.style.display = '';
            } else {
                rec.style.display = 'none';
                doc.style.display = 'none';
            }
        }
    </script>
</body>
</html>
