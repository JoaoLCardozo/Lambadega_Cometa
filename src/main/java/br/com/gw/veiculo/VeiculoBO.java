package br.com.gw.veiculo;

import br.com.gw.exception.CadastroException;
import br.com.gw.exception.NegocioException;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class VeiculoBO {
    private static final Logger logger = Logger.getLogger(VeiculoBO.class.getName());
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    // Placa Mercosul: ABC1D23 — Antiga: ABC1234
    private static final Pattern PLACA = Pattern.compile(
        "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    public List<Veiculo> listar(String filtro, int pagina, int limite) throws NegocioException {
        return listar(filtro, null, null, null, pagina, limite);
    }

    public List<Veiculo> listar(String filtro, String tipo, String status,
                                String anoFabricacao, int pagina, int limite)
            throws NegocioException {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;
        return veiculoDAO.listar(filtro, tipo, status, anoFabricacao, pagina, limite);
    }

    public int contarPaginas(String filtro, int limite) throws NegocioException {
        return contarPaginas(filtro, null, null, null, limite);
    }

    public int contarPaginas(String filtro, String tipo, String status,
                             String anoFabricacao, int limite) throws NegocioException {
        if (limite < 1) limite = 10;
        int total = veiculoDAO.contarTotal(filtro, tipo, status, anoFabricacao);
        return (int) Math.ceil((double) total / limite);
    }

    public Veiculo buscarPorId(int id) throws NegocioException {
        if (id <= 0) throw new CadastroException("ID de veículo inválido.");
        Veiculo v = veiculoDAO.buscarPorId(id);
        if (v == null) throw new CadastroException("Veículo não encontrado.");
        return v;
    }

    public void salvar(Veiculo v) throws NegocioException {
        validar(v, 0);
        veiculoDAO.salvar(v);
        logger.info("Veiculo salvo: " + v.getPlaca());
    }

    public void atualizar(Veiculo v) throws NegocioException {
        if (v.getId() <= 0) throw new CadastroException("ID de veículo inválido para atualização.");
        // Regra: não pode alterar status para DISPONIVEL manualmente se estiver EM_TRANSITO
        if (v.getStatus() == Veiculo.Status.DISPONIVEL && veiculoDAO.estaEmTransito(v.getId())) {
            throw new CadastroException(
                "Não é permitido alterar o status para Disponível de um veículo em trânsito.");
        }
        validar(v, v.getId());
        veiculoDAO.atualizar(v);
        logger.info("Veiculo atualizado: " + v.getPlaca());
    }

    public void excluir(int id) throws NegocioException {
        if (id <= 0) throw new CadastroException("ID de veículo inválido.");
        veiculoDAO.excluir(id);
        logger.info("Veiculo excluido: id=" + id);
    }

    private void validar(Veiculo v, int idIgnorar) throws NegocioException {
        if (v.getPlaca() == null || v.getPlaca().trim().isEmpty()) {
            throw new CadastroException("O campo Placa é obrigatório.");
        }
        String placa = v.getPlaca().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (!PLACA.matcher(placa).matches()) {
            throw new CadastroException(
                "A placa informada é inválida. Use o formato Mercosul (ABC1D23) ou antigo (ABC1234).");
        }
        v.setPlaca(placa);
        if (veiculoDAO.existePlaca(placa, idIgnorar)) {
            throw new CadastroException("Já existe um veículo cadastrado com esta placa.");
        }
        if (v.getTipo() == null) {
            throw new CadastroException("O campo Tipo é obrigatório.");
        }
        if (v.getCapacidadeKg() <= 0) {
            throw new CadastroException("A capacidade de carga deve ser maior que zero.");
        }
        if (v.getTaraKg() >= v.getCapacidadeKg()) {
            throw new CadastroException("A tara não pode ser maior ou igual à capacidade de carga.");
        }
        if (v.getStatus() == null) {
            v.setStatus(Veiculo.Status.DISPONIVEL);
        }
    }
}
