package br.com.gw.usuario;

import br.com.gw.exception.ApplicationException;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Controlador de Login - Gerencia a autenticação de usuários.
 * Responsável por: autenticação, validação de sessão e logout.
 * 
 * Esta classe contém a lógica de negócio do login que pode ser usada
 * tanto em um Servlet quanto em outras camadas da aplicação.
 */
public class LoginControlador implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LoginControlador.class.getName());
    
    private UsuarioBO usuarioBO = new UsuarioBO();
    
    // Constantes para mensagens
    public static final String MSG_SUCESSO_LOGIN = "Login realizado com sucesso!";
    public static final String MSG_ERRO_CREDENCIAIS = "Usuario ou senha incorretos";
    public static final String MSG_ERRO_USUARIO_INATIVO = "Usuario inativo";
    public static final String MSG_ERRO_SESSAO_EXPIRADA = "Sua sessao expirou. Por favor, faca login novamente.";
    public static final String MSG_SUCESSO_LOGOUT = "Logout realizado com sucesso!";
    public static final String MSG_ERRO_CAMPOS_VAZIOS = "Usuario e senha sao obrigatorios";

    /**
     * Autentica um usuário verificando as credenciais.
     * 
     * @param usuario nome do usuário
     * @param senha senha do usuário
     * @return objeto Usuario se autenticado com sucesso, null caso contrário
     * @throws ApplicationException em caso de erro de validação ou banco de dados
     */
    public Usuario autenticar(String usuario, String senha) throws ApplicationException {
        
        // Validação inicial
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new ApplicationException("Usuário não pode ser vazio");
        }
        
        if (senha == null || senha.trim().isEmpty()) {
            throw new ApplicationException("Senha não pode ser vazia");
        }
        
        logger.info("Tentativa de autenticação para usuário: " + usuario);
        
        try {
            // Chamar BO para autenticar (inclui validações)
            Usuario usuarioAutenticado = usuarioBO.autenticar(usuario.trim(), senha.trim());
            
            if (usuarioAutenticado != null) {
                logger.info("Usuário autenticado com sucesso: " + usuarioAutenticado.getUsuario());
                return usuarioAutenticado;
            } else {
                logger.warning("Falha na autenticação: credenciais inválidas para " + usuario);
                throw new ApplicationException(MSG_ERRO_CREDENCIAIS);
            }
            
        } catch (ApplicationException e) {
            logger.warning("Erro de autenticação: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Erro geral na autenticação: " + e.getMessage());
            e.printStackTrace();
            throw new ApplicationException("Erro ao processar autenticação: " + e.getMessage(), e);
        }
    }

    /**
     * Valida se um usuário está autenticado e sua sessão é válida.
     * 
     * @param usuarioLogado usuário a validar
     * @return true se válido, false caso contrário
     */
    public boolean validarAutenticacao(Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            logger.warning("Tentativa de validação com usuário nulo");
            return false;
        }
        
        if (usuarioLogado.getId() == null || usuarioLogado.getId() <= 0) {
            logger.warning("Tentativa de validação com ID inválido");
            return false;
        }
        
        if (usuarioLogado.getAtivo() == null || !usuarioLogado.getAtivo()) {
            logger.warning("Tentativa de validação com usuário inativo: " + usuarioLogado.getUsuario());
            return false;
        }
        
        return true;
    }

    /**
     * Valida se a sessão está ativa.
     * 
     * @param sessaoAtiva se a sessão está ativa
     * @param usuario usuário da sessão (para logging)
     * @return true se válida, false caso contrário
     */
    public boolean validarSessao(boolean sessaoAtiva, Usuario usuario) {
        if (!sessaoAtiva) {
            if (usuario != null) {
                logger.warning("Sessão expirada para usuário: " + usuario.getUsuario());
            } else {
                logger.warning("Tentativa de acesso com sessão expirada");
            }
            return false;
        }
        return true;
    }

    /**
     * Faz logout do usuário, finalizando a sessão.
     * 
     * @param usuario usuário a fazer logout
     * @return mensagem de sucesso
     */
    public String logout(Usuario usuario) {
        if (usuario != null) {
            logger.info("Usuário deslogado: " + usuario.getUsuario());
            return MSG_SUCESSO_LOGOUT;
        } else {
            logger.warning("Tentativa de logout com usuário nulo");
            return "Logout realizado";
        }
    }

    /**
     * Obtém um usuário pelo ID (para recarregar dados da sessão).
     * 
     * @param usuarioId ID do usuário
     * @return objeto Usuario ou null se não encontrado
     * @throws ApplicationException em caso de erro
     */
    public Usuario obterUsuario(Integer usuarioId) throws ApplicationException {
        if (usuarioId == null || usuarioId <= 0) {
            throw new ApplicationException("ID de usuário inválido");
        }
        
        try {
            return usuarioBO.buscarPorId(usuarioId);
        } catch (ApplicationException e) {
            logger.warning("Erro ao obter usuário com ID: " + usuarioId + ": " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Erro inesperado ao obter usuário com ID: " + usuarioId);
            throw new ApplicationException("Erro ao obter dados do usuário", e);
        }
    }

    /**
     * Valida credenciais sem efetuar login (apenas verificação).
     * Útil para validação dupla de autenticação.
     * 
     * @param usuario nome do usuário
     * @param senha senha do usuário
     * @return true se credenciais são válidas, false caso contrário
     */
    public boolean validarCredenciais(String usuario, String senha) {
        try {
            Usuario usuarioValidado = usuarioBO.autenticar(usuario, senha);
            return usuarioValidado != null;
        } catch (Exception e) {
            logger.fine("Credenciais inválidas para: " + usuario);
            return false;
        }
    }

    /**
     * Obtém informações de erro formatadas.
     * 
     * @param erro exceção capturada
     * @return mensagem de erro formatada
     */
    public String obterMensagemErro(Exception erro) {
        if (erro == null) {
            return "Erro desconhecido";
        }
        
        String mensagem = erro.getMessage();
        if (mensagem == null || mensagem.isEmpty()) {
            return erro.getClass().getSimpleName();
        }
        
        return mensagem;
    }

    /**
     * Obtém dados do usuário para exibição em log de auditoria.
     * 
     * @param usuario usuário para auditar
     * @return string formatada com dados do usuário
     */
    public String obterDadosAuditoria(Usuario usuario) {
        if (usuario == null) {
            return "Usuário não disponível";
        }
        
        return String.format("ID: %d | Usuário: %s | Nome: %s | Email: %s | Ativo: %s",
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAtivo());
    }

    /**
     * Retorna o tempo de sessão recomendado em segundos.
     * 
     * @return tempo em segundos (30 minutos = 1800 segundos)
     */
    public int obterTempoSessao() {
        return 30 * 60; // 30 minutos
    }

    /**
     * Retorna uma string com as regras de validação.
     * 
     * @return regras de validação formatadas
     */
    public String obterRegrasValidacao() {
    return "- Usuario: minimo 3 caracteres\n" +
           "- Senha: minimo 4 caracteres\n" +
           "- Email: deve ser valido (contem @)\n" +
           "- Usuario deve ser unico no sistema\n" +
           "- Usuario deve estar ativo";
    }

    /**
     * Cadastra um novo usuário no sistema.
     * 
     * @param usuario objeto Usuario com os dados a cadastrar
     * @throws ApplicationException em caso de erro de validação ou banco de dados
     */
    public void cadastrarUsuario(Usuario usuario) throws ApplicationException {
        if (usuario == null) {
            throw new ApplicationException("Usuário não pode ser nulo");
        }
        
        logger.info("Tentativa de cadastro para usuário: " + usuario.getUsuario());
        
        try {
            // Chamar BO para inserir (inclui validações)
            usuarioBO.inserir(usuario);
            logger.info("Usuário cadastrado com sucesso: " + usuario.getUsuario());
        } catch (ApplicationException e) {
            logger.warning("Erro ao cadastrar usuário: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.severe("Erro geral no cadastro de usuário: " + e.getMessage());
            e.printStackTrace();
            throw new ApplicationException("Erro ao processar cadastro: " + e.getMessage(), e);
        }
    }
}
