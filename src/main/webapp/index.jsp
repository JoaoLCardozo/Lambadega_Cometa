<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${empty sessionScope.usuarioLogado}">
    <c:redirect url="/LoginControlador"/>
</c:if>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Início - Lambadega Cometa</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/design-system.css" type="text/css">
</head>
<body>
    <main class="app-shell">
        <section class="app-brand">
            <div class="brand-row">
                <span class="brand-mark">LC</span>
                <div class="brand-copy">
                    <h1 class="brand-title">Lambadega Cometa</h1>
                    <p class="brand-subtitle">Sistema de gerenciamento de fretes</p>
                </div>
            </div>
            <div class="app-actions">
                <span>Bem-vindo, <strong><c:out value="${sessionScope.usuarioLogado.nome}"/></strong></span>
                <a class="link-button" href="${pageContext.request.contextPath}/LoginControlador?acao=logout">Sair</a>
            </div>
        </section>

        <section class="app-header">
            <div class="app-header-main">
                <span class="app-eyebrow">Painel inicial</span>
                <h2 class="app-title">Escolha uma área para começar</h2>
            </div>
        </section>

        <nav class="nav-grid" aria-label="Módulos do sistema">
            <a class="nav-card" href="${pageContext.request.contextPath}/ClienteControlador?acao=listar">
                <span class="nav-icon">CL</span>
                <span class="nav-label">Clientes</span>
                <span class="nav-help">Cadastro e consulta de remetentes e destinatários</span>
            </a>
            <a class="nav-card" href="${pageContext.request.contextPath}/MotoristaControlador?acao=listar">
                <span class="nav-icon">MO</span>
                <span class="nav-label">Motoristas</span>
                <span class="nav-help">Dados, CNH, vínculo e situação operacional</span>
            </a>
            <a class="nav-card" href="${pageContext.request.contextPath}/VeiculoControlador?acao=listar">
                <span class="nav-icon">VE</span>
                <span class="nav-label">Veículos</span>
                <span class="nav-help">Frota, capacidade, status e manutenção</span>
            </a>
            <a class="nav-card" href="${pageContext.request.contextPath}/FreteControlador?acao=listar">
                <span class="nav-icon">FR</span>
                <span class="nav-label">Fretes</span>
                <span class="nav-help">Emissão, acompanhamento e ocorrências</span>
            </a>
        </nav>
    </main>
</body>
</html>
