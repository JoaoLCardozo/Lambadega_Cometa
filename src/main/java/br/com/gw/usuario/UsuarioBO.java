package br.com.gw.usuario;

import br.com.gw.exception.AuthenticationException;
import br.com.gw.exception.NegocioException;
import br.com.gw.exception.DAOException;
import br.com.gw.exception.RecursoNaoEncontradoException;
import br.com.gw.exception.ValidationException;
import br.com.gw.util.EmailService;
import br.com.gw.util.SegurancaUtils;
import br.com.gw.util.SenhaUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe de Lógica de Negócio (BO) para a entidade Usuario.
 * Responsável por validações e regras de negócio.
 */
public class UsuarioBO {
    
    private static final int MINUTOS_VALIDADE_CODIGO = 15;
    private static final SecureRandom RANDOM = new SecureRandom();

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private EmailService emailService = new EmailService();

    /**
     * Autentica um usuário com validações.
     * @param usuario nome do usuário
     * @param senha senha do usuário
     * @return objeto Usuario autenticado
     * @throws AuthenticationException se as credenciais forem inválidas
     * @throws ValidationException se houver erro de validação
     * @throws NegocioException para outros erros de negócio
     */
    public Usuario autenticar(String usuario, String senha) 
            throws AuthenticationException, ValidationException, NegocioException {
        
        try {
            // Validações
            if (usuario == null || usuario.trim().isEmpty()) {
                throw new ValidationException("Usuário não pode ser vazio");
            }
            
            if (senha == null || senha.trim().isEmpty()) {
                throw new ValidationException("Senha não pode ser vazia");
            }
            
            if (usuario.trim().length() < 3) {
                throw new ValidationException("Usuário deve ter pelo menos 3 caracteres");
            }
            
            if (senha.trim().length() < 4) {
                throw new ValidationException("Senha deve ter pelo menos 4 caracteres");
            }
            
            String usuarioNormalizado = usuario.trim();
            String senhaNormalizada = senha.trim();
            Usuario usuarioAutenticado = usuarioDAO.autenticar(usuarioNormalizado);
            
            if (usuarioAutenticado == null
                    || !SenhaUtils.verificar(senhaNormalizada, usuarioAutenticado.getSenha())) {
                throw new AuthenticationException("Usuário ou senha incorretos");
            }
            
            if (!usuarioAutenticado.getAtivo()) {
                throw new AuthenticationException("Usuário inativo");
            }

            if (!SenhaUtils.ehHash(usuarioAutenticado.getSenha())) {
                usuarioDAO.atualizarSenha(
                    usuarioAutenticado.getId(), SenhaUtils.gerarHash(senhaNormalizada));
            }
            
            return usuarioAutenticado;
            
        } catch (DAOException e) {
            throw new NegocioException("Erro ao autenticar usuário", e);
        }
    }

    /**
     * Busca um usuário pelo ID com validações.
     * @param id ID do usuário
     * @return objeto Usuario
     * @throws ValidationException se o ID for inválido
     * @throws NegocioException se ocorrer erro de negócio
     */
    public Usuario buscarPorId(Integer id) throws ValidationException, NegocioException {
        try {
            if (id == null || id <= 0) {
                throw new ValidationException("ID de usuário inválido");
            }
            
            Usuario usuario = usuarioDAO.buscarPorId(id);
            
            if (usuario == null) {
                throw new RecursoNaoEncontradoException("Usuário não encontrado");
            }
            
            return usuario;
            
        } catch (DAOException e) {
            throw new NegocioException("Erro ao buscar usuário", e);
        }
    }

    /**
     * Busca um usuário pelo nome de usuário.
     * @param usuario nome do usuário
     * @return objeto Usuario
     * @throws ValidationException se o usuário for inválido
     * @throws NegocioException se ocorrer erro de negócio
     */
    public Usuario buscarPorUsuario(String usuario) throws ValidationException, NegocioException {
        try {
            if (usuario == null || usuario.trim().isEmpty()) {
                throw new ValidationException("Usuário não pode ser vazio");
            }
            
            Usuario usuarioEncontrado = usuarioDAO.buscarPorUsuario(usuario);
            
            if (usuarioEncontrado == null) {
                throw new ValidationException("Usuário não encontrado");
            }
            
            return usuarioEncontrado;
            
        } catch (DAOException e) {
            throw new NegocioException("Erro ao buscar usuário", e);
        }
    }

    /**
     * Lista todos os usuários.
     * @return lista de usuários
     * @throws NegocioException se ocorrer erro de negócio
     */
    public List<Usuario> listarTodos() throws NegocioException {
        try {
            return usuarioDAO.listarTodos();
        } catch (DAOException e) {
            throw new NegocioException("Erro ao listar usuários", e);
        }
    }

    /**
     * Insere um novo usuário com validações.
     * @param usuario objeto Usuario a ser inserido
     * @throws ValidationException se os dados forem inválidos
     * @throws NegocioException se ocorrer erro de negócio
     */
    public void inserir(Usuario usuario) throws ValidationException, NegocioException {
        try {
            validarUsuario(usuario);
            usuario.setSenha(SenhaUtils.gerarHash(usuario.getSenha().trim()));
            
            // Verificar se o usuário já existe
            if (usuarioDAO.buscarPorUsuario(usuario.getUsuario()) != null) {
                throw new ValidationException("Este usuário já está registrado");
            }
            
            usuarioDAO.inserir(usuario);
            
        } catch (DAOException e) {
            throw new NegocioException("Erro ao inserir usuário", e);
        }
    }

    /**
     * Atualiza um usuário existente com validações.
     * @param usuario objeto Usuario a ser atualizado
     * @throws ValidationException se os dados forem inválidos
     * @throws NegocioException se ocorrer erro de negócio
     */
    public void atualizar(Usuario usuario) throws ValidationException, NegocioException {
        try {
            if (usuario.getId() == null || usuario.getId() <= 0) {
                throw new ValidationException("ID de usuário inválido");
            }
            
            validarUsuario(usuario);
            usuario.setSenha(SenhaUtils.gerarHash(usuario.getSenha().trim()));
            usuarioDAO.atualizar(usuario);
            
        } catch (DAOException e) {
            throw new NegocioException("Erro ao atualizar usuário", e);
        }
    }

    /**
     * Deleta um usuário com validações.
     * @param id ID do usuário a ser deletado
     * @throws ValidationException se o ID for inválido
     * @throws NegocioException se ocorrer erro de negócio
     */
    public void deletar(Integer id) throws ValidationException, NegocioException {
        try {
            if (id == null || id <= 0) {
                throw new ValidationException("ID de usuário inválido");
            }
            
            usuarioDAO.deletar(id);
            
        } catch (DAOException e) {
            throw new NegocioException("Erro ao deletar usuário", e);
        }
    }

    public void solicitarRecuperacaoSenha(String email)
            throws ValidationException, NegocioException {
        try {
            String emailNormalizado = normalizarTextoSemHtml(email, "Email");
            validarEmail(emailNormalizado);

            Usuario usuario = usuarioDAO.buscarPorEmailAtivo(emailNormalizado);
            if (usuario == null) {
                throw new ValidationException("E-mail não encontrado ou usuário inativo.");
            }

            String codigo = gerarCodigoVerificacao();
            usuarioDAO.salvarCodigoRecuperacao(
                usuario.getId(),
                hashCodigo(codigo),
                LocalDateTime.now().plusMinutes(MINUTOS_VALIDADE_CODIGO));

            emailService.enviarCodigoRecuperacao(
                usuario.getEmail(), usuario.getNome(), codigo);
        } catch (DAOException e) {
            throw new NegocioException("Erro ao solicitar recuperação de senha", e);
        }
    }

    public void redefinirSenhaComCodigo(String email, String codigo, String novaSenha)
            throws ValidationException, NegocioException {
        try {
            String emailNormalizado = normalizarTextoSemHtml(email, "Email");
            validarEmail(emailNormalizado);

            String codigoNormalizado = codigo != null ? codigo.replaceAll("[^0-9]", "") : "";
            if (!codigoNormalizado.matches("\\d{6}")) {
                throw new ValidationException("O código de verificação deve conter 6 dígitos.");
            }

            if (novaSenha == null || novaSenha.trim().isEmpty()) {
                throw new ValidationException("Nova senha não pode ser vazia");
            }

            String senhaNormalizada = novaSenha.trim();
            if (senhaNormalizada.length() < 4) {
                throw new ValidationException("Nova senha deve ter pelo menos 4 caracteres");
            }

            Integer idUsuario = usuarioDAO.buscarUsuarioPorCodigoRecuperacao(
                emailNormalizado, hashCodigo(codigoNormalizado));
            if (idUsuario == null) {
                throw new ValidationException("Código inválido ou expirado.");
            }

            usuarioDAO.atualizarSenha(idUsuario, SenhaUtils.gerarHash(senhaNormalizada));
            usuarioDAO.marcarCodigosRecuperacaoComoUsados(idUsuario);
        } catch (DAOException e) {
            throw new NegocioException("Erro ao redefinir senha", e);
        }
    }

    /**
     * Valida os dados do usuário.
     */
    private void validarUsuario(Usuario usuario) throws ValidationException {
        if (usuario == null) {
            throw new ValidationException("Usuário não pode ser nulo");
        }
        usuario.setNome(normalizarTextoSemHtml(usuario.getNome(), "Nome"));
        usuario.setEmail(normalizarTextoSemHtml(usuario.getEmail(), "Email"));
        usuario.setUsuario(normalizarTextoSemHtml(usuario.getUsuario(), "Usuário"));
        
        if (usuario.getNome() == null) {
            throw new ValidationException("Nome não pode ser vazio");
        }
        
        if (usuario.getEmail() == null) {
            throw new ValidationException("Email não pode ser vazio");
        }
        
        validarEmail(usuario.getEmail());
        
        if (usuario.getUsuario() == null) {
            throw new ValidationException("Usuário não pode ser vazio");
        }
        
        if (usuario.getUsuario().trim().length() < 3) {
            throw new ValidationException("Usuário deve ter pelo menos 3 caracteres");
        }
        
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new ValidationException("Senha não pode ser vazia");
        }
        
        if (usuario.getSenha().trim().length() < 4) {
            throw new ValidationException("Senha deve ter pelo menos 4 caracteres");
        }
    }

    private String normalizarTextoSemHtml(String valor, String nomeCampo) throws ValidationException {
        String texto = SegurancaUtils.normalizarTexto(valor);
        if (SegurancaUtils.contemHtml(texto)) {
            throw new ValidationException("O campo " + nomeCampo + " não permite HTML ou scripts.");
        }
        return texto;
    }

    private void validarEmail(String email) throws ValidationException {
        if (email == null) {
            throw new ValidationException("Email não pode ser vazio");
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ValidationException("Email inválido");
        }
    }

    private String gerarCodigoVerificacao() {
        int codigo = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(codigo);
    }

    private String hashCodigo(String codigo) throws NegocioException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codigo.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new NegocioException("Erro ao proteger código de recuperação", e);
        }
    }
}
