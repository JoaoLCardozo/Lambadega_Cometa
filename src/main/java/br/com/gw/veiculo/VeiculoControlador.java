package br.com.gw.veiculo;

import br.com.gw.exception.CadastroException;
import br.com.gw.exception.NegocioException;
import br.com.gw.exception.RecursoNaoEncontradoException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/VeiculoControlador")
public class VeiculoControlador extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(VeiculoControlador.class.getName());
    private static final int LIMITE = 10;

    private final VeiculoBO veiculoBO = new VeiculoBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";
        try {
            switch (acao) {
                case "listar":  listar(req, resp);  break;
                case "novo":    novo(req, resp);     break;
                case "editar":  editar(req, resp);   break;
                case "excluir": excluir(req, resp);  break;
                default:        listar(req, resp);
            }
        } catch (RecursoNaoEncontradoException e) {
            responderNaoEncontrado(req, resp, e.getMessage());
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try { listar(req, resp); } catch (NegocioException ex) {
                logger.severe("Erro ao recarregar listagem: " + ex.getMessage());
            }
        } catch (Exception e) {
            logger.severe("Erro inesperado: " + e.getMessage());
            System.err.println("[VeiculoControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "";
        try {
            switch (acao) {
                case "salvar":    salvar(req, resp);    break;
                case "atualizar": atualizar(req, resp); break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/VeiculoControlador?acao=listar");
            }
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("veiculo", montarVeiculoParaRetorno(req));
            req.getRequestDispatcher("/WEB-INF/views/veiculo/formVeiculo.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.severe("Erro inesperado POST: " + e.getMessage());
            System.err.println("[VeiculoControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        String filtro        = req.getParameter("filtro");
        String tipo          = req.getParameter("tipo");
        String status        = req.getParameter("status");
        String anoFabricacao = req.getParameter("anoFabricacao");
        int pagina           = parsePagina(req.getParameter("pagina"));
        List<Veiculo> lista  = veiculoBO.listar(filtro, tipo, status, anoFabricacao, pagina, LIMITE);
        int totalPaginas     = veiculoBO.contarPaginas(filtro, tipo, status, anoFabricacao, LIMITE);
        req.setAttribute("listaVeiculos",  lista);
        req.setAttribute("filtro",         filtro);
        req.setAttribute("tipo",           tipo);
        req.setAttribute("status",         status);
        req.setAttribute("anoFabricacao",  anoFabricacao);
        req.setAttribute("paginaAtual",    pagina);
        req.setAttribute("totalPaginas",   totalPaginas);
        req.getRequestDispatcher("/WEB-INF/views/veiculo/listarVeiculo.jsp").forward(req, resp);
    }

    private void novo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("veiculo", new Veiculo());
        req.getRequestDispatcher("/WEB-INF/views/veiculo/formVeiculo.jsp").forward(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        req.setAttribute("veiculo", veiculoBO.buscarPorId(id));
        req.getRequestDispatcher("/WEB-INF/views/veiculo/formVeiculo.jsp").forward(req, resp);
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        veiculoBO.excluir(id);
        resp.sendRedirect(req.getContextPath() + "/VeiculoControlador?acao=listar");
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        validarCamposNumericosDaRequisicao(req);
        veiculoBO.salvar(montarVeiculoDaRequisicao(req));
        resp.sendRedirect(req.getContextPath() + "/VeiculoControlador?acao=listar");
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        validarCamposNumericosDaRequisicao(req);
        Veiculo v = montarVeiculoDaRequisicao(req);
        v.setId(Integer.parseInt(req.getParameter("id")));
        veiculoBO.atualizar(v);
        resp.sendRedirect(req.getContextPath() + "/VeiculoControlador?acao=listar");
    }

    private Veiculo montarVeiculoDaRequisicao(HttpServletRequest req) {
        Veiculo v = new Veiculo();
        v.setPlaca(req.getParameter("placa"));
        v.setRntrc(req.getParameter("rntrc"));
        String ano = req.getParameter("anoFabricacao");
        if (ano != null && !ano.isEmpty()) v.setAnoFabricacao(Integer.parseInt(ano));
        String tipo = req.getParameter("tipo");
        if (tipo != null && !tipo.isEmpty()) v.setTipo(Veiculo.Tipo.valueOf(tipo));
        String tara = req.getParameter("taraKg");
        if (tara != null && !tara.isEmpty()) v.setTaraKg(parseDoubleSeguro(tara));
        String cap = req.getParameter("capacidadeKg");
        if (cap != null && !cap.isEmpty()) v.setCapacidadeKg(parseDoubleSeguro(cap));
        String vol = req.getParameter("volumeM3");
        if (vol != null && !vol.isEmpty()) v.setVolumeM3(parseDoubleSeguro(vol));
        String st = req.getParameter("status");
        v.setStatus(st != null && !st.isEmpty()
            ? Veiculo.Status.valueOf(st) : Veiculo.Status.DISPONIVEL);
        return v;
    }

    private Veiculo montarVeiculoParaRetorno(HttpServletRequest req) {
        Veiculo v = new Veiculo();
        v.setPlaca(req.getParameter("placa"));
        v.setRntrc(req.getParameter("rntrc"));

        String id = req.getParameter("id");
        if (id != null && id.matches("\\d+")) v.setId(Integer.parseInt(id));

        String ano = req.getParameter("anoFabricacao");
        if (ano != null && ano.matches("\\d+")) v.setAnoFabricacao(Integer.parseInt(ano));

        String tipo = req.getParameter("tipo");
        if (tipo != null && !tipo.isEmpty()) v.setTipo(Veiculo.Tipo.valueOf(tipo));

        v.setTaraKg(parseDoubleSeguro(req.getParameter("taraKg")));
        v.setCapacidadeKg(parseDoubleSeguro(req.getParameter("capacidadeKg")));
        v.setVolumeM3(parseDoubleSeguro(req.getParameter("volumeM3")));

        String st = req.getParameter("status");
        v.setStatus(st != null && !st.isEmpty()
            ? Veiculo.Status.valueOf(st) : Veiculo.Status.DISPONIVEL);
        return v;
    }

    private void validarCamposNumericosDaRequisicao(HttpServletRequest req) throws CadastroException {
        validarInteiro(req.getParameter("anoFabricacao"), "Ano de Fabricação");
        validarDecimal(req.getParameter("taraKg"), "Tara");
        validarDecimal(req.getParameter("capacidadeKg"), "Capacidade de Carga");
        validarDecimal(req.getParameter("volumeM3"), "Volume");
    }

    private void validarInteiro(String valor, String nomeCampo) throws CadastroException {
        String texto = normalizarTexto(valor);
        if (texto == null) return;
        if (!texto.matches("\\d+")) {
            throw new CadastroException("O campo " + nomeCampo + " deve conter somente números.");
        }
    }

    private void validarDecimal(String valor, String nomeCampo) throws CadastroException {
        String texto = normalizarTexto(valor);
        if (texto == null) return;

        try {
            double numero = Double.parseDouble(texto.replace(',', '.'));
            if (Double.isNaN(numero) || Double.isInfinite(numero)) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new CadastroException("O campo " + nomeCampo + " deve conter um número válido.");
        }
    }

    private double parseDoubleSeguro(String valor) {
        String texto = normalizarTexto(valor);
        if (texto == null) return 0;

        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String normalizarTexto(String valor) {
        if (valor == null) return null;
        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

    private int parsePagina(String valor) {
        try { return Math.max(1, Integer.parseInt(valor)); }
        catch (Exception e) { return 1; }
    }

    private void responderNaoEncontrado(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        req.setAttribute("tituloErro", "Registro não encontrado");
        req.setAttribute("mensagemErro", mensagem);
        req.getRequestDispatcher("/erro.jsp").forward(req, resp);
    }
}
