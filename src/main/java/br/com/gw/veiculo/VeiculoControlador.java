package br.com.gw.veiculo;

import br.com.gw.exception.NegocioException;

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
            req.setAttribute("veiculo", montarVeiculoDaRequisicao(req));
            req.getRequestDispatcher("/WEB-INF/views/veiculo/formVeiculo.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.severe("Erro inesperado POST: " + e.getMessage());
            System.err.println("[VeiculoControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        String filtro  = req.getParameter("filtro");
        int pagina     = parsePagina(req.getParameter("pagina"));
        List<Veiculo> lista  = veiculoBO.listar(filtro, pagina, LIMITE);
        int totalPaginas     = veiculoBO.contarPaginas(filtro, LIMITE);
        req.setAttribute("listaVeiculos",  lista);
        req.setAttribute("filtro",         filtro);
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
        veiculoBO.salvar(montarVeiculoDaRequisicao(req));
        resp.sendRedirect(req.getContextPath() + "/VeiculoControlador?acao=listar");
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
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
        if (tara != null && !tara.isEmpty()) v.setTaraKg(Double.parseDouble(tara));
        String cap = req.getParameter("capacidadeKg");
        if (cap != null && !cap.isEmpty()) v.setCapacidadeKg(Double.parseDouble(cap));
        String vol = req.getParameter("volumeM3");
        if (vol != null && !vol.isEmpty()) v.setVolumeM3(Double.parseDouble(vol));
        String st = req.getParameter("status");
        v.setStatus(st != null && !st.isEmpty()
            ? Veiculo.Status.valueOf(st) : Veiculo.Status.DISPONIVEL);
        return v;
    }

    private int parsePagina(String valor) {
        try { return Math.max(1, Integer.parseInt(valor)); }
        catch (Exception e) { return 1; }
    }
}