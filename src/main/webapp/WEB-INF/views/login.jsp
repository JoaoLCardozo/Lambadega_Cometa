<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE HTML>
<html lang="pt-BR">

<head>
    <meta charset="UTF-8">
    <title>Login - Lambadega Cometa</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/estilo.css" type="text/css">

    <style type="text/css">
        .style4 {
            font-size: 14px;
            font-weight: bold;
        }

        .login-wrapper {
            width: 100%;
            display: flex;
            justify-content: center;
            margin-top: 40px;
        }
    </style>
</head>

<body>

    <div class="login-wrapper">
        <form action="<%= request.getContextPath() %>/LoginControlador" method="post">

            <input type="hidden" name="acao" value="login">

            <table class="bordaFina" width="35%" align="center">

                <tr>
                    <td colspan="2" align="center">
                        <span class="style4">Lambadega Cometa - Sistema de Gerenciamento de Fretes</span>
                    </td>
                </tr>

                <%
                    String erro = (String) request.getAttribute("erro");
                    String sucesso = (String) request.getAttribute("sucesso");
                    String aviso = (String) request.getAttribute("aviso");
                    String usuarioInformado = (String) request.getAttribute("usuario");

                    if (usuarioInformado == null) {
                        usuarioInformado = "";
                    }
                %>

                <% if (erro != null && !erro.trim().isEmpty()) { %>
                    <tr>
                        <td colspan="2" class="CelulaZebra1" align="center">
                            <span style="color: red; font-weight: bold;">
                                <%= erro %>
                            </span>
                        </td>
                    </tr>
                <% } %>

                <% if (sucesso != null && !sucesso.trim().isEmpty()) { %>
                    <tr>
                        <td colspan="2" class="CelulaZebra1" align="center">
                            <span style="color: green; font-weight: bold;">
                                <%= sucesso %>
                            </span>
                        </td>
                    </tr>
                <% } %>

                <% if (aviso != null && !aviso.trim().isEmpty()) { %>
                    <tr>
                        <td colspan="2" class="CelulaZebra1" align="center">
                            <span style="color: #cc8800; font-weight: bold;">
                                <%= aviso %>
                            </span>
                        </td>
                    </tr>
                <% } %>

                <tr>
                    <td width="35%" class="CelulaZebra1" align="right">Usuario:</td>
                    <td width="65%" class="CelulaZebra1">
                        <input type="text"
                               name="usuario"
                               id="usuario"
                               class="inputtexto"
                               size="25"
                               maxlength="50"
                               value="<%= usuarioInformado %>"
                               autofocus>
                    </td>
                </tr>

                <tr>
                    <td class="CelulaZebra2" align="right">Senha:</td>
                    <td class="CelulaZebra2">
                        <input type="password"
                               name="senha"
                               id="senha"
                               class="inputtexto"
                               size="25"
                               maxlength="50">
                    </td>
                </tr>

                <tr>
                    <td colspan="2" align="center" class="CelulaZebra1">
                        <input type="submit" class="inputbotao" value="Entrar">
                    </td>
                </tr>

                <tr>
                    <td colspan="2" align="center" class="CelulaZebra2">
                        <small>Sistema Lambadega Cometa v1.0 - 2026</small>
                    </td>
                </tr>

            </table>

        </form>
    </div>

</body>
</html>