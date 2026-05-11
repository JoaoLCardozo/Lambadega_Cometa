package br.com.gw.motorista;

import br.com.gw.exception.CadastroException;
import br.com.gw.exception.NegocioException;

import java.util.List;
import java.util.logging.Logger;

public class MotoristaBO {
    private static final Logger logger = Logger.getLogger(MotoristaBO.class.getName());
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();

    public List<Motorista> listar(String filtro, int pagina, int limite) throws NegocioException {
        return listar(filtro, null, null, null, null, pagina, limite);
    }

    public List<Motorista> listar(String filtro, String cpf, String status,
                                  String tipoVinculo, String cnhCategoria,
                                  int pagina, int limite) throws NegocioException {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;
        return motoristaDAO.listar(filtro, cpf, status, tipoVinculo, cnhCategoria, pagina, limite);
    }

    public int contarPaginas(String filtro, int limite) throws NegocioException {
        return contarPaginas(filtro, null, null, null, null, limite);
    }

    public int contarPaginas(String filtro, String cpf, String status,
                             String tipoVinculo, String cnhCategoria,
                             int limite) throws NegocioException {
        if (limite < 1) limite = 10;
        int total = motoristaDAO.contarTotal(filtro, cpf, status, tipoVinculo, cnhCategoria);
        return (int) Math.ceil((double) total / limite);
    }

    public Motorista buscarPorId(int id) throws NegocioException {
        if (id <= 0) throw new CadastroException("ID de motorista inválido.");
        Motorista m = motoristaDAO.buscarPorId(id);
        if (m == null) throw new CadastroException("Motorista não encontrado.");
        return m;
    }

    public void salvar(Motorista m) throws NegocioException {
        validar(m, 0);
        motoristaDAO.salvar(m);
        logger.info("Motorista salvo: " + m.getNome());
    }

    public void atualizar(Motorista m) throws NegocioException {
        if (m.getId() <= 0) throw new CadastroException("ID de motorista inválido para atualização.");
        validar(m, m.getId());
        motoristaDAO.atualizar(m);
        logger.info("Motorista atualizado: " + m.getNome());
    }

    public void excluir(int id) throws NegocioException {
        if (id <= 0) throw new CadastroException("ID de motorista inválido.");
        if (motoristaDAO.possuiFretesAtivos(id)) {
            throw new CadastroException(
                "Não é permitido excluir um motorista com fretes em andamento.");
        }
        motoristaDAO.excluir(id);
        logger.info("Motorista excluido: id=" + id);
    }

    public void inativar(int id) throws NegocioException {
        if (motoristaDAO.possuiFretesAtivos(id)) {
            throw new CadastroException(
                "Não é permitido inativar um motorista com fretes em andamento.");
        }
        Motorista m = buscarPorId(id);
        m.setStatus(Motorista.Status.INATIVO);
        motoristaDAO.atualizar(m);
    }

    private void validar(Motorista m, int idIgnorar) throws NegocioException {
        if (m.getNome() == null || m.getNome().trim().isEmpty()) {
            throw new CadastroException("O campo Nome é obrigatório.");
        }
        if (m.getCpf() == null || m.getCpf().trim().isEmpty()) {
            throw new CadastroException("O campo CPF é obrigatório.");
        }
        if (!validarCpf(m.getCpf())) {
            throw new CadastroException("O CPF informado é inválido.");
        }
        if (motoristaDAO.existeCpf(m.getCpf(), idIgnorar)) {
            throw new CadastroException("Já existe um motorista cadastrado com este CPF.");
        }
        if (m.getCnhNumero() == null || m.getCnhNumero().trim().isEmpty()) {
            throw new CadastroException("O campo Número da CNH é obrigatório.");
        }
        if (m.getCnhCategoria() == null) {
            throw new CadastroException("O campo Categoria da CNH é obrigatório.");
        }
        if (m.getCnhValidade() == null) {
            throw new CadastroException("O campo Validade da CNH é obrigatório.");
        }
        if (m.getTipoVinculo() == null) {
            throw new CadastroException("O campo Tipo de Vínculo é obrigatório.");
        }
        if (m.getStatus() == null) {
            m.setStatus(Motorista.Status.ATIVO);
        }
    }

    public static boolean validarCpf(String cpf) {
        if (cpf == null) return false;
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++)
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        int dig1 = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        soma = 0;
        for (int i = 0; i < 10; i++)
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        int dig2 = soma % 11 < 2 ? 0 : 11 - (soma % 11);

        return Character.getNumericValue(cpf.charAt(9))  == dig1
            && Character.getNumericValue(cpf.charAt(10)) == dig2;
    }
}
