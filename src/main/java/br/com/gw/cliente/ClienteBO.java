package br.com.gw.cliente;

import br.com.gw.exception.CadastroException;
import br.com.gw.exception.NegocioException;

import java.util.List;
import java.util.logging.Logger;

public class ClienteBO {
    private static final Logger logger = Logger.getLogger(ClienteBO.class.getName());

    private final ClienteDAO clienteDAO = new ClienteDAO();

    public List<Cliente> listar(String filtro, int pagina, int limite) throws NegocioException {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;
        return clienteDAO.listar(filtro, pagina, limite);
    }

    public int contarPaginas(String filtro, int limite) throws NegocioException {
        int total = clienteDAO.contarTotal(filtro);
        return (int) Math.ceil((double) total / limite);
    }

    public Cliente buscarPorId(int id) throws NegocioException {
        if (id <= 0) throw new CadastroException("ID de cliente inválido.");
        Cliente c = clienteDAO.buscarPorId(id);
        if (c == null) throw new CadastroException("Cliente não encontrado.");
        return c;
    }

    public void salvar(Cliente c) throws NegocioException {
        validar(c, 0);
        clienteDAO.salvar(c);
        logger.info("Cliente salvo: " + c.getNomeRazaoSocial());
    }

    public void atualizar(Cliente c) throws NegocioException {
        if (c.getId() <= 0) throw new CadastroException("ID de cliente inválido para atualização.");
        validar(c, c.getId());
        clienteDAO.atualizar(c);
        logger.info("Cliente atualizado: " + c.getNomeRazaoSocial());
    }

    public void excluir(int id) throws NegocioException {
        if (id <= 0) throw new CadastroException("ID de cliente inválido.");
        if (clienteDAO.possuiFretes(id)) {
            throw new CadastroException("Não é permitido excluir um cliente que possui fretes cadastrados.");
        }
        clienteDAO.excluir(id);
        logger.info("Cliente excluido: id=" + id);
    }

    private void validar(Cliente c, int idIgnorar) throws NegocioException {
        if (c.getTipoPessoa() == null) {
            throw new CadastroException("O tipo de pessoa (F/J) é obrigatório.");
        }
        if (c.getNomeRazaoSocial() == null || c.getNomeRazaoSocial().trim().isEmpty()) {
            throw new CadastroException("O campo Nome/Razão Social é obrigatório.");
        }
        String documento = somenteDigitos(c.getDocumento());
        if (documento == null || documento.isEmpty()) {
            throw new CadastroException("O campo Documento é obrigatório.");
        }
        c.setDocumento(documento);
        
        // Validar documento conforme tipo
        if (c.getTipoPessoa() == Cliente.TipoPessoa.F) {
            // Validar CPF
            if (!validarCpf(documento)) {
                throw new CadastroException("O CPF informado é inválido.");
            }
        } else if (c.getTipoPessoa() == Cliente.TipoPessoa.J) {
            // Validar CNPJ
            if (!validarCnpj(documento)) {
                throw new CadastroException("O CNPJ informado é inválido.");
            }
        }
        
        if (clienteDAO.existeDocumento(documento, idIgnorar)) {
            throw new CadastroException("Já existe um cliente cadastrado com este documento.");
        }
        if (c.getStatus() == null) {
            c.setStatus(Cliente.Status.ATIVO);
        }
    }

    private String somenteDigitos(String valor) {
        return valor != null ? valor.replaceAll("[^0-9]", "") : null;
    }

    /**
     * Valida CPF pelo dígito verificador.
     */
    public static boolean validarCpf(String cpf) {
        if (cpf == null) return false;

        // Remove máscara
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) return false;

        // Rejeita sequências repetidas
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int[] pesos1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * pesos1[i];
        }
        int dig1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * pesos2[i];
        }
        int dig2 = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        return cpf.charAt(9) == (char)('0' + dig1) && cpf.charAt(10) == (char)('0' + dig2);
    }

    /**
     * Valida CNPJ pelo dígito verificador.
     */
    public static boolean validarCnpj(String cnpj) {
        if (cnpj == null) return false;

        // Remove máscara
        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (cnpj.length() != 14) return false;

        // Rejeita sequências repetidas
        if (cnpj.matches("(\\d)\\1{13}")) return false;

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos1[i];
        }
        int dig1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * pesos2[i];
        }
        int dig2 = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        return Character.getNumericValue(cnpj.charAt(12)) == dig1
            && Character.getNumericValue(cnpj.charAt(13)) == dig2;
    }
}
