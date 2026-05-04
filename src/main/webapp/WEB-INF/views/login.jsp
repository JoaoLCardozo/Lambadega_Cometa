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
                        <div class="password-field">
                            <input type="password"
                                   name="senha"
                                   id="senha"
                                   class="inputtexto"
                                   maxlength="50"/>
                            <button type="button"
                                    class="password-toggle"
                                    id="toggleSenha"
                                    aria-controls="senha"
                                    aria-label="Mostrar senha"
                                    aria-pressed="false"
                                    title="Mostrar senha">
                                <svg class="password-icon password-icon-show is-hidden" viewBox="0 0 24 24" aria-hidden="true">
                                    <path d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                </svg>
                                <svg class="password-icon password-icon-hide" viewBox="0 0 24 24" aria-hidden="true">
                                    <path d="M2.5 12s3.5-6 9.5-6 9.5 6 9.5 6-3.5 6-9.5 6-9.5-6-9.5-6z"/>
                                    <circle cx="12" cy="12" r="3"/>
                                    <path d="M4 20 20 4"/>
                                </svg>
                            </button>
                        </div>
                    </div>
                    <div class="form-actions">
                        <input type="submit" class="inputbotao" value="Entrar"/>
                    </div>
                </div>
            </form>
        </section>
    </main>
    <script>
        document.getElementById('toggleSenha').addEventListener('click', function() {
            var campoSenha = document.getElementById('senha');
            var iconeMostrar = this.querySelector('.password-icon-show');
            var iconeOcultar = this.querySelector('.password-icon-hide');
            var mostrarSenha = campoSenha.type === 'password';

            campoSenha.type = mostrarSenha ? 'text' : 'password';
            iconeMostrar.classList.toggle('is-hidden', !mostrarSenha);
            iconeOcultar.classList.toggle('is-hidden', mostrarSenha);
            this.setAttribute('aria-label', mostrarSenha ? 'Ocultar senha' : 'Mostrar senha');
            this.setAttribute('aria-pressed', mostrarSenha ? 'true' : 'false');
            this.setAttribute('title', mostrarSenha ? 'Ocultar senha' : 'Mostrar senha');
        });
    </script>
</body>
</html>
