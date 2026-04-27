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
        logger.info("Cliente salvo: " + c.getRazaoSocial());
    }

    public void atualizar(Cliente c) throws NegocioException {
        if (c.getId() <= 0) throw new CadastroException("ID de cliente inválido para atualização.");
        validar(c, c.getId());
        clienteDAO.atualizar(c);
        logger.info("Cliente atualizado: " + c.getRazaoSocial());
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
        if (c.getRazaoSocial() == null || c.getRazaoSocial().trim().isEmpty()) {
            throw new CadastroException("O campo Razão Social é obrigatório.");
        }
        if (c.getCnpj() == null || c.getCnpj().trim().isEmpty()) {
            throw new CadastroException("O campo CNPJ é obrigatório.");
        }
        if (!validarCnpj(c.getCnpj())) {
            throw new CadastroException("O CNPJ informado é inválido.");
        }
        if (clienteDAO.existeCnpj(c.getCnpj(), idIgnorar)) {
            throw new CadastroException("Já existe um cliente cadastrado com este CNPJ.");
        }
        if (c.getTipo() == null) {
            throw new CadastroException("O campo Tipo é obrigatório.");
        }
        if (c.getStatus() == null) {
            c.setStatus(Cliente.Status.ATIVO);
        }
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