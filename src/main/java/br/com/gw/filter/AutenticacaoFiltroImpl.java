package br.com.gw.filter;

import br.com.gw.usuario.Usuario;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

public class AutenticacaoFiltroImpl implements Filter {
    private static final Logger logger = Logger.getLogger(AutenticacaoFiltroImpl.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("AutenticacaoFiltro inicializado");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession sessao = httpRequest.getSession(false);

        String contextPath = httpRequest.getContextPath();
        String requestPath = httpRequest.getRequestURI().substring(contextPath.length());

        if (ehRecursoPublico(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        if (sessao != null && sessao.getAttribute("usuarioLogado") != null) {
            Usuario usuario = (Usuario) sessao.getAttribute("usuarioLogado");

            if (AutenticacaoFiltro.validarAutenticacao(usuario)) {
                AutenticacaoFiltro.registrarAcesso(usuario, requestPath);
                chain.doFilter(request, response);
            } else {
                sessao.invalidate();
                AutenticacaoFiltro.registrarNegacao(usuario, requestPath, "usuario_invalido");
                httpResponse.sendRedirect(contextPath + "/LoginControlador?acao=avisoInativo");
            }
        } else {
            AutenticacaoFiltro.registrarNegacao(null, requestPath, "sessao_expirada");
            httpResponse.sendRedirect(contextPath + "/LoginControlador?acao=sessaoExpirada");
        }
    }

    @Override
    public void destroy() {
        logger.info("AutenticacaoFiltro destruido");
    }

    private boolean ehRecursoPublico(String caminho) {
        String[] publicos = {
            "/LoginControlador",
            "/erro.jsp",
            "/index.jsp",
            ".css",
            ".js",
            ".jpg",
            ".png",
            ".gif",
            ".ico",
            ".svg",
            ".woff",
            ".ttf"
        };

        for (String p : publicos) {
            if (caminho.contains(p)) {
                return true;
            }
        }

        return false;
    }
}
