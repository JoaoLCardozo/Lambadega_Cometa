package br.com.gw.usuario;

import br.com.gw.exception.AuthenticationException;
import br.com.gw.exception.NegocioException;
import br.com.gw.exception.DAOException;
import br.com.gw.exception.ValidationException;

import java.util.List;

/**
 * Classe de Lógica de Negócio (BO) para a entidade Usuario.
 * Responsável por validações e regras de negócio.
 */
public class UsuarioBO {
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

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
            
            // Autenticar
            Usuario usuarioAutenticado = usuarioDAO.autenticar(usuario, senha);
            
            if (usuarioAutenticado == null) {
                throw new AuthenticationException("Usuário ou senha incorretos");
            }
            
            if (!usuarioAutenticado.getAtivo()) {
                throw new AuthenticationException("Usuário inativo");
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
                throw new ValidationException("Usuário não encontrado");
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

    /**
     * Valida os dados do usuário.
     */
    private void validarUsuario(Usuario usuario) throws ValidationException {
        if (usuario == null) {
            throw new ValidationException("Usuário não pode ser nulo");
        }
        
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new ValidationException("Nome não pode ser vazio");
        }
        
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email não pode ser vazio");
        }
        
        if (!usuario.getEmail().contains("@")) {
            throw new ValidationException("Email inválido");
        }
        
        if (usuario.getUsuario() == null || usuario.getUsuario().trim().isEmpty()) {
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
}
