<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Redefinir Senha - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body class="auth-page">
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Validação do código de recuperação</p>
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
                <input type="hidden" name="acao" value="redefinirSenha">
                <div class="form-grid one-column">
                    <div class="form-field full">
                        <span class="app-eyebrow">Código enviado</span>
                        <h2 class="app-title">Criar nova senha</h2>
                    </div>
                    <div class="form-field">
                        <label for="email">E-mail <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <input type="email"
                               name="email"
                               id="email"
                               class="inputtexto"
                               maxlength="100"
                               inputmode="email"
                               autocomplete="email"
                               value="<c:out value='${email}'/>"
                               required>
                    </div>
                    <div class="form-field">
                        <label for="codigo">Código de verificação <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <input type="text"
                               name="codigo"
                               id="codigo"
                               class="inputtexto"
                               maxlength="6"
                               inputmode="numeric"
                               pattern="[0-9]*"
                               autocomplete="one-time-code"
                               autofocus
                               required>
                    </div>
                    <div class="form-field">
                        <label for="novaSenha">Nova senha <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <input type="password"
                               name="novaSenha"
                               id="novaSenha"
                               class="inputtexto"
                               maxlength="50"
                               required>
                    </div>
                    <div class="form-field">
                        <label for="confirmaSenha">Confirmar nova senha <span class="required-marker" aria-label="obrigatório">*</span></label>
                        <input type="password"
                               name="confirmaSenha"
                               id="confirmaSenha"
                               class="inputtexto"
                               maxlength="50"
                               required>
                    </div>
                    <div class="form-actions">
                        <input type="submit" class="inputbotao" value="Alterar senha">
                        <a class="link-button" href="${pageContext.request.contextPath}/LoginControlador?acao=esqueciSenha">Reenviar código</a>
                    </div>
                </div>
            </form>
        </section>
    </main>
    <script>
        var codigo = document.getElementById('codigo');
        if (codigo) {
            codigo.addEventListener('input', function() {
                this.value = this.value.replace(/\D/g, '').substring(0, 6);
            });
        }
    </script>
</body>
</html>
