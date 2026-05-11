<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Recuperar Senha - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body class="auth-page">
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Recuperação de acesso ao sistema</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/LoginControlador">Login</a>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error" role="alert"><c:out value="${erro}"/></div>
        </c:if>
        <c:if test="${not empty sucesso}">
            <div class="alert alert-success" role="alert"><c:out value="${sucesso}"/></div>
        </c:if>

        <section class="card auth-card">
            <form action="${pageContext.request.contextPath}/LoginControlador" method="post">
                <input type="hidden" name="acao" value="solicitarRecuperacaoSenha">
                <div class="form-grid one-column">
                    <div class="form-field full">
                        <span class="app-eyebrow">Esqueci minha senha</span>
                        <h2 class="app-title">Enviar código de verificação</h2>
                    </div>
                    <div class="form-field">
                        <label for="email">E-mail cadastrado <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <input type="email"
                               name="email"
                               id="email"
                               class="inputtexto"
                               maxlength="100"
                               inputmode="email"
                               autocomplete="email"
                               value="<c:out value='${email}'/>"
                               autofocus
                               required>
                    </div>
                    <div class="form-actions">
                        <input type="submit" class="inputbotao" value="Enviar código">
                        <a class="link-button" href="${pageContext.request.contextPath}/LoginControlador">Cancelar</a>
                    </div>
                </div>
            </form>
        </section>
    </main>
</body>
</html>
