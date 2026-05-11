package br.com.gw.monitorfretes;

import br.com.gw.exception.NegocioException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@WebServlet("/MonitorFretesControlador")
public class MonitorFretesControlador extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MonitorFretesControlador.class.getName());

    private final MonitorFretesBO monitorFretesBO = new MonitorFretesBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("resumo", monitorFretesBO.buscarResumo());
            req.getRequestDispatcher("/WEB-INF/views/monitorfretes/monitorFretes.jsp").forward(req, resp);
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/monitorfretes/monitorFretes.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.severe("Erro inesperado no monitor de fretes: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }
}
