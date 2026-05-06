<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Performance de Motoristas - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
    <script>
        function abrirJanelaPerformanceMotorista(form) {
            if (!form.idMotorista.value || !form.dataInicio.value || !form.dataFim.value) {
                alert("Selecione o motorista e informe o período.");
                return;
            }

            var url = "${pageContext.request.contextPath}/FreteControlador?acao=relatorioPerformanceMotorista"
                + "&idMotorista=" + encodeURIComponent(form.idMotorista.value)
                + "&dataInicio=" + encodeURIComponent(form.dataInicio.value)
                + "&dataFim=" + encodeURIComponent(form.dataFim.value);

            window.open(
                url,
                "relatorioPerformanceMotorista",
                "width=960,height=780,left=100,top=40,resizable=yes,scrollbars=yes,menubar=no,toolbar=no,location=no,status=no"
            );
        }
    </script>
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Indicadores operacionais por motorista</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/FreteControlador?acao=listar">Voltar</a>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Relatórios</span>
                <h2 class="app-title">Performance de motoristas</h2>
            </div>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>

        <section class="card">
            <form action="${pageContext.request.contextPath}/FreteControlador" method="get">
                <div class="form-grid">
                    <div class="form-field full">
                        <label for="idMotorista">Motorista</label>
                        <select name="idMotorista" id="idMotorista" class="inputtexto" required>
                            <option value="">Selecione</option>
                            <c:forEach var="m" items="${listaMotoristas}">
                                <option value="${m.id}"><c:out value="${m.nome}"/> - CPF <c:out value="${m.cpf}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-field">
                        <label for="dataInicio">Data inicial</label>
                        <input type="date" name="dataInicio" id="dataInicio" class="inputtexto" required>
                    </div>
                    <div class="form-field">
                        <label for="dataFim">Data final</label>
                        <input type="date" name="dataFim" id="dataFim" class="inputtexto" required>
                    </div>
                </div>
                <div class="form-actions">
                    <input type="button" class="inputbotao"
                           value="Localizar"
                           onclick="abrirJanelaPerformanceMotorista(this.form)">
                    <input type="button" class="inputbotao secondary"
                           value="Cancelar"
                           onclick="window.location='${pageContext.request.contextPath}/FreteControlador?acao=listar'">
                </div>
            </form>
        </section>
    </main>
</body>
</html>
