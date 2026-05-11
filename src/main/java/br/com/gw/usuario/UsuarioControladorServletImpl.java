package br.com.gw.usuario;

import br.com.gw.exception.ValidationException;
import br.com.gw.exception.NegocioException;
import br.com.gw.exception.RecursoNaoEncontradoException;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet Controlador para operações de Usuário.
 * Responsável por: cadastro, listagem, edição e exclusão de usuários.
 * 
 * Mapeia requisições da camada de apresentação (JSP) para a camada de negócio (BO).
 */
public class UsuarioControladorServletImpl extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(UsuarioControladorServletImpl.class.getName());

    private UsuarioBO usuarioBO = new UsuarioBO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String acao = request.getParameter("acao");

        if ("listar".equals(acao)) {
            listarUsuarios(request, response);
            return;
        }

        if ("editar".equals(acao)) {
            editarUsuario(request, response);
            return;
        }

        if ("novo".equals(acao)) {
            request.getRequestDispatcher("/WEB-INF/views/usuario/formUsuario.jsp").forward(request, response);
            return;
        }

        // Ação padrão: ir para formulário de novo usuário
        request.getRequestDispatcher("/WEB-INF/views/usuario/formUsuario.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String acao = request.getParameter("acao");

        if ("cadastrar".equals(acao)) {
            cadastrarUsuario(request, response);
            return;
        }

        if ("atualizar".equals(acao)) {
            atualizarUsuario(request, response);
            return;
        }

        if ("deletar".equals(acao)) {
            deletarUsuario(request, response);
            return;
        }

        // Ação padrão: volta para formulário
        response.sendRedirect(request.getContextPath() + "/usuario?acao=novo");
    }

    /**
     * Cadastra um novo usuário.
     */
    private void cadastrarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String usuario = request.getParameter("usuario");
        String senha = request.getParameter("senha");
        String confirmaSenha = request.getParameter("confirmaSenha");

        try {
            // Validação: senhas devem coincidir
            if (senha == null || !senha.equals(confirmaSenha)) {
                throw new ValidationException("As senhas não coincidem");
            }

            // Criar objeto usuário
            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setEmail(email);
            novoUsuario.setUsuario(usuario);
            novoUsuario.setSenha(senha);
            novoUsuario.setAtivo(true);

            // Chamar BO para inserir (já valida e verifica duplicata)
            usuarioBO.inserir(novoUsuario);

            logger.info("Usuário cadastrado com sucesso: " + usuario);

            // Sucesso: redirecionar para login ou listar usuários
            request.setAttribute("sucesso", "Usuário cadastrado com sucesso! Por favor, faça login.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);

        } catch (ValidationException e) {
            logger.warning("Erro de validação no cadastro: " + e.getMessage());

            request.setAttribute("erro", e.getMessage());
            request.setAttribute("nome", nome);
            request.setAttribute("email", email);
            request.setAttribute("usuario", usuario);
            request.getRequestDispatcher("/WEB-INF/views/usuario/formUsuario.jsp").forward(request, response);

        } catch (NegocioException e) {
            logger.warning("Erro de negócio no cadastro: " + e.getMessage());

            request.setAttribute("erro", e.getMessage());
            request.setAttribute("nome", nome);
            request.setAttribute("email", email);
            request.setAttribute("usuario", usuario);
            request.getRequestDispatcher("/WEB-INF/views/usuario/formUsuario.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Erro inesperado no cadastro: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("erro", "Erro inesperado ao cadastrar usuário.");
            request.getRequestDispatcher("/WEB-INF/views/usuario/formUsuario.jsp").forward(request, response);
        }
    }

    /**
     * Lista todos os usuários (apenas para administradores).
     */
    private void listarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // TODO: Verificar permissão de administrador
            java.util.List<Usuario> usuarios = usuarioBO.listarTodos();

            request.setAttribute("usuarios", usuarios);
            request.getRequestDispatcher("/WEB-INF/views/usuario/listarUsuario.jsp").forward(request, response);

        } catch (NegocioException e) {
            logger.warning("Erro ao listar usuários: " + e.getMessage());

            request.setAttribute("erro", "Erro ao listar usuários: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/usuario/listarUsuario.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Erro inesperado ao listar usuários: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("erro", "Erro inesperado ao listar usuários.");
            request.getRequestDispatcher("/WEB-INF/views/usuario/listarUsuario.jsp").forward(request, response);
        }
    }

    /**
     * Edita um usuário existente.
     */
    private void editarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");

        try {
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new ValidationException("ID do usuário não informado");
            }

            Integer id = Integer.parseInt(idParam);
            Usuario usuario = usuarioBO.buscarPorId(id);

            request.setAttribute("usuario", usuario);
            request.getRequestDispatcher("/WEB-INF/views/usuario/formUsuario.jsp").forward(request, response);

        } catch (RecursoNaoEncontradoException e) {
            responderNaoEncontrado(request, response, e.getMessage());
        } catch (ValidationException | NegocioException e) {
            logger.warning("Erro ao editar usuário: " + e.getMessage());

            request.setAttribute("erro", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/usuario/listarUsuario.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            logger.warning("ID inválido: " + idParam);

            request.setAttribute("erro", "ID de usuário inválido");
            request.getRequestDispatcher("/WEB-INF/views/usuario/listarUsuario.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Erro inesperado ao editar usuário: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("erro", "Erro inesperado ao editar usuário.");
            request.getRequestDispatcher("/WEB-INF/views/usuario/listarUsuario.jsp").forward(request, response);
        }
    }

    /**
     * Atualiza um usuário existente.
     */
    private void atualizarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String usuario = request.getParameter("usuario");

        try {
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new ValidationException("ID do usuário não informado");
            }

            Integer id = Integer.parseInt(idParam);

            Usuario usuarioAtualizado = new Usuario();
            usuarioAtualizado.setId(id);
            usuarioAtualizado.setNome(nome);
            usuarioAtualizado.setEmail(email);
            usuarioAtualizado.setUsuario(usuario);

            usuarioBO.atualizar(usuarioAtualizado);

            logger.info("Usuário atualizado com sucesso: " + usuario);

            request.setAttribute("sucesso", "Usuário atualizado com sucesso!");
            response.sendRedirect(request.getContextPath() + "/usuario?acao=listar");

        } catch (ValidationException | NegocioException e) {
            logger.warning("Erro ao atualizar usuário: " + e.getMessage());

            request.setAttribute("erro", e.getMessage());
            try {
                request.setAttribute("usuario", usuarioBO.buscarPorId(Integer.parseInt(idParam)));
            } catch (ValidationException | NegocioException ex) {
                logger.warning("Erro ao buscar usuário para exibir formulário: " + ex.getMessage());
            }
            request.getRequestDispatcher("/WEB-INF/views/usuario/formUsuario.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Erro inesperado ao atualizar usuário: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("erro", "Erro inesperado ao atualizar usuário.");
            request.getRequestDispatcher("/WEB-INF/views/usuario/formUsuario.jsp").forward(request, response);
        }
    }

    /**
     * Deleta um usuário.
     */
    private void deletarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");

        try {
            if (idParam == null || idParam.trim().isEmpty()) {
                throw new ValidationException("ID do usuário não informado");
            }

            Integer id = Integer.parseInt(idParam);

            usuarioBO.deletar(id);

            logger.info("Usuário deletado com sucesso: " + id);

            response.sendRedirect(request.getContextPath() + "/usuario?acao=listar");

        } catch (ValidationException | NegocioException e) {
            logger.warning("Erro ao deletar usuário: " + e.getMessage());

            request.setAttribute("erro", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/usuario/listarUsuario.jsp").forward(request, response);

        } catch (Exception e) {
            logger.severe("Erro inesperado ao deletar usuário: " + e.getMessage());
            e.printStackTrace();

            request.setAttribute("erro", "Erro inesperado ao deletar usuário.");
            request.getRequestDispatcher("/WEB-INF/views/usuario/listarUsuario.jsp").forward(request, response);
        }
    }

    private void responderNaoEncontrado(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        request.setAttribute("tituloErro", "Registro não encontrado");
        request.setAttribute("mensagemErro", mensagem);
        request.getRequestDispatcher("/erro.jsp").forward(request, response);
    }
}
