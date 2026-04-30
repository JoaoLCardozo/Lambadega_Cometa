<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Login - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body class="login-page">
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Acesse o sistema de gerenciamento de fretes</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/LoginControlador?acao=novoUsuario">Criar usuário</a>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>
        <c:if test="${not empty sucesso}">
            <div class="alert alert-success"><c:out value="${sucesso}"/></div>
        </c:if>
        <c:if test="${not empty aviso}">
            <div class="alert alert-warning"><c:out value="${aviso}"/></div>
        </c:if>

        <section class="card login-card">
            <form action="${pageContext.request.contextPath}/LoginControlador" method="post">
                <input type="hidden" name="acao" value="login">
                <div class="form-grid one-column">
                    <div class="form-field full">
                        <span class="app-eyebrow">Entrada segura</span>
                        <h2 class="app-title">Acesso ao sistema</h2>
                    </div>
                    <div class="form-field">
                        <label for="usuario">Usuário</label>
                        <input type="text"
                               name="usuario"
                               id="usuario"
                               class="inputtexto"
                               maxlength="50"
                               value="<c:out value='${usuario}'/>"
                               autofocus/>
                    </div>
                    <div class="form-field">
                        <label for="senha">Senha</label>
                        <input type="password"
                               name="senha"
                               id="senha"
                               class="inputtexto"
                               maxlength="50"/>
                    </div>
                    <div class="form-actions">
                        <input type="submit" class="inputbotao" value="Entrar"/>
                    </div>
                </div>
            </form>
        </section>
    </main>
</body>
</html>
