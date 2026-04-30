<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Novo Usuário - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body class="auth-page">
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Criação de acesso ao sistema</p>
                </div>
            </div>
            <a class="link-button" href="${pageContext.request.contextPath}/LoginControlador">Login</a>
        </section>

        <c:if test="${not empty erro}">
            <div class="alert alert-error"><c:out value="${erro}"/></div>
        </c:if>

        <section class="card auth-card">
            <form action="${pageContext.request.contextPath}/usuario" method="post">
                <input type="hidden" name="acao" value="cadastrar">
                <div class="form-grid one-column">
                    <div class="form-field full">
                        <span class="app-eyebrow">Novo usuário</span>
                        <h2 class="app-title">Dados de acesso</h2>
                    </div>
                    <div class="form-field">
                        <label for="nome">Nome</label>
                        <input type="text" name="nome" id="nome" class="inputtexto" maxlength="100" value="<c:out value='${nome}'/>" required>
                    </div>
                    <div class="form-field">
                        <label for="email">Email</label>
                        <input type="email" name="email" id="email" class="inputtexto" maxlength="100" value="<c:out value='${email}'/>" required>
                    </div>
                    <div class="form-field">
                        <label for="usuario">Usuário</label>
                        <input type="text" name="usuario" id="usuario" class="inputtexto" maxlength="50" value="<c:out value='${usuario}'/>" required>
                    </div>
                    <div class="form-field">
                        <label for="senha">Senha</label>
                        <input type="password" name="senha" id="senha" class="inputtexto" maxlength="50" required>
                    </div>
                    <div class="form-field">
                        <label for="confirmaSenha">Confirmar senha</label>
                        <input type="password" name="confirmaSenha" id="confirmaSenha" class="inputtexto" maxlength="50" required>
                    </div>
                    <div class="form-actions">
                        <input type="submit" class="inputbotao" value="Cadastrar">
                    </div>
                </div>
            </form>
        </section>
    </main>
</body>
</html>
