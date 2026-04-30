package br.com.gw.usuario;

import br.com.gw.exception.ApplicationException;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

public class LoginControladorServletImpl extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LoginControladorServletImpl.class.getName());

    private LoginControlador loginControlador = new LoginControlador();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String acao = request.getParameter("acao");

        if ("logout".equals(acao)) {
            logout(request, response);
            return;
        }

        if ("sessaoExpirada".equals(acao)) {
            request.setAttribute("erro", LoginControlador.MSG_ERRO_SESSAO_EXPIRADA);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        if ("avisoInativo".equals(acao)) {
            request.setAttribute("erro", LoginControlador.MSG_ERRO_USUARIO_INATIVO);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        if ("novoUsuario".equals(acao)) {
            response.sendRedirect(request.getContextPath() + "/usuario?acao=novo");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        autenticar(request, response);
    }

    private void autenticar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String usuario = request.getParameter("usuario");
        String senha = request.getParameter("senha");

        try {
            Usuario usuarioAutenticado = loginControlador.autenticar(usuario, senha);

            HttpSession sessao = request.getSession(true);
            sessao.setAttribute("usuarioLogado", usuarioAutenticado);
            sessao.setMaxInactiveInterval(loginControlador.obterTempoSessao());

            logger.info("Login realizado com sucesso para usuario: " + usuarioAutenticado.getUsuario());

            response.sendRedirect(request.getContextPath() + "/index.jsp");

        } catch (ApplicationException e) {
            logger.warning("Falha no login: " + e.getMessage());

            request.setAttribute("erro", e.getMessage());
            request.setAttribute("usuario", usuario);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Erro inesperado no login: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("erro", "Erro inesperado ao realizar login.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
    

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        HttpSession sessao = request.getSession(false);

        if (sessao != null) {
            Usuario usuario = (Usuario) sessao.getAttribute("usuarioLogado");
            loginControlador.logout(usuario);
            sessao.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/LoginControlador");
    }
}