package br.com.gw.monitorfretes;

import br.com.gw.exception.NegocioException;

import java.util.ArrayList;
import java.util.List;

public class MonitorFretesBO {
    private final MonitorFretesDAO monitorFretesDAO = new MonitorFretesDAO();

    public MonitorFretesResumo buscarResumo() throws NegocioException {
        MonitorFretesResumo resumo = monitorFretesDAO.buscarResumo();
        resumo.setAlertas(montarAlertas(resumo));
        return resumo;
    }

    private List<MonitorFretesAlerta> montarAlertas(MonitorFretesResumo resumo) {
        List<MonitorFretesAlerta> alertas = new ArrayList<>();

        if (resumo.getFretesAtrasados() > 0) {
            alertas.add(new MonitorFretesAlerta(
                "critico",
                resumo.getFretesAtrasados() + " frete(s) atrasado(s)",
                "Priorize contato com motorista e destinatário antes de emitir novos fretes críticos.",
                "/FreteControlador?acao=listar",
                "Abrir fretes"
            ));
        }

        if (resumo.getEntregasHoje() > 0) {
            alertas.add(new MonitorFretesAlerta(
                "atencao",
                resumo.getEntregasHoje() + " entrega(s) vencem hoje",
                "Acompanhe saída, ocorrência e confirmação de entrega para não virar atraso.",
                "/FreteControlador?acao=listar",
                "Acompanhar"
            ));
        }

        if (resumo.getMotoristasCnhVencida() > 0) {
            alertas.add(new MonitorFretesAlerta(
                "critico",
                resumo.getMotoristasCnhVencida() + " motorista(s) com CNH vencida",
                "Regularize os cadastros antes de escalar novos fretes.",
                "/MotoristaControlador?acao=listar",
                "Ver motoristas"
            ));
        }

        if (resumo.getVeiculosDisponiveis() == 0) {
            alertas.add(new MonitorFretesAlerta(
                "critico",
                "Nenhum veículo disponível",
                "A operação está sem frota livre para novas emissões.",
                "/VeiculoControlador?acao=listar",
                "Ver frota"
            ));
        }

        if (alertas.isEmpty()) {
            alertas.add(new MonitorFretesAlerta(
                "ok",
                "Operação sem alertas críticos",
                "Fretes, motoristas e frota estão dentro dos principais limites monitorados.",
                "/FreteControlador?acao=novo",
                "Novo frete"
            ));
        }

        return alertas;
    }
}
