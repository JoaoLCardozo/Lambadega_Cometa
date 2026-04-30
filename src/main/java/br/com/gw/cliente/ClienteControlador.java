package br.com.gw.cliente;

import br.com.gw.exception.NegocioException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/ClienteControlador")
public class ClienteControlador extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ClienteControlador.class.getName());
    private static final int LIMITE = 10;

    private final ClienteBO clienteBO = new ClienteBO();

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
                resp.sendRedirect(req.getContextPath() + "/erro.jsp");
            }
        } catch (Exception e) {
            logger.severe("Erro inesperado em ClienteControlador GET: " + e.getMessage());
            System.err.println("[ClienteControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String acao = req.getParameter("acao");
        if (acao == null) acao = "";

        try {
            switch (acao) {
                case "salvar":    salvar(req, resp);    break;
                case "atualizar": atualizar(req, resp); break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/ClienteControlador?acao=listar");
            }
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("cliente", montarClienteDaRequisicao(req));
            req.getRequestDispatcher("/WEB-INF/views/cliente/formCliente.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.severe("Erro inesperado em ClienteControlador POST: " + e.getMessage());
            System.err.println("[ClienteControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        String filtro    = req.getParameter("filtro");
        int pagina       = parsePagina(req.getParameter("pagina"));
        List<Cliente> lista  = clienteBO.listar(filtro, pagina, LIMITE);
        int totalPaginas     = clienteBO.contarPaginas(filtro, LIMITE);
        req.setAttribute("listaClientes",    lista);
        req.setAttribute("filtro",           filtro);
        req.setAttribute("paginaAtual",      pagina);
        req.setAttribute("totalPaginas",     totalPaginas);
        req.setAttribute("limiteResultados", LIMITE);
        req.getRequestDispatcher("/WEB-INF/views/cliente/listarCliente.jsp").forward(req, resp);
    }

    private void novo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("cliente", new Cliente());
        req.getRequestDispatcher("/WEB-INF/views/cliente/formCliente.jsp").forward(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        req.setAttribute("cliente", clienteBO.buscarPorId(id));
        req.getRequestDispatcher("/WEB-INF/views/cliente/formCliente.jsp").forward(req, resp);
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        clienteBO.excluir(id);
        resp.sendRedirect(req.getContextPath() + "/ClienteControlador?acao=listar");
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        clienteBO.salvar(montarClienteDaRequisicao(req));
        resp.sendRedirect(req.getContextPath() + "/ClienteControlador?acao=listar");
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        Cliente c = montarClienteDaRequisicao(req);
        c.setId(Integer.parseInt(req.getParameter("id")));
        clienteBO.atualizar(c);
        resp.sendRedirect(req.getContextPath() + "/ClienteControlador?acao=listar");
    }

    private Cliente montarClienteDaRequisicao(HttpServletRequest req) {
        Cliente c = new Cliente();
        String tipoPessoa = req.getParameter("tipoPessoa");
        if (tipoPessoa != null && !tipoPessoa.isEmpty()) {
            c.setTipoPessoa(Cliente.TipoPessoa.valueOf(tipoPessoa));
        }
        c.setNomeRazaoSocial(req.getParameter("nomeRazaoSocial"));
        c.setNomeFantasia(req.getParameter("nomeFantasia"));
        c.setDocumento(req.getParameter("documento"));
        c.setInscricaoEstadual(req.getParameter("inscricaoEstadual"));
        c.setLogradouro(req.getParameter("logradouro"));
        c.setNumero(req.getParameter("numero"));
        c.setComplemento(req.getParameter("complemento"));
        c.setBairro(req.getParameter("bairro"));
        c.setMunicipio(req.getParameter("municipio"));
        c.setUf(req.getParameter("uf"));
        c.setCep(req.getParameter("cep"));
        c.setTelefone(req.getParameter("telefone"));
        c.setEmail(req.getParameter("email"));
        String status = req.getParameter("status");
        c.setStatus(status != null && !status.isEmpty()
            ? Cliente.Status.valueOf(status)
            : Cliente.Status.ATIVO);
        return c;
    }

    private int parsePagina(String valor) {
        try { return Math.max(1, Integer.parseInt(valor)); }
        catch (Exception e) { return 1; }
    }
}