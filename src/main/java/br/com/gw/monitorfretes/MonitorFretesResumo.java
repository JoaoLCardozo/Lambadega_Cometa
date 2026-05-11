package br.com.gw.monitorfretes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonitorFretesResumo {
    private static final Locale PT_BR = new Locale("pt", "BR");

    private int fretesAbertos;
    private int fretesAtrasados;
    private int entregasHoje;
    private int veiculosDisponiveis;
    private int motoristasCnhVencida;
    private int clientesAtivos;
    private BigDecimal valorFretesMes = BigDecimal.ZERO;
    private List<MonitorFretesAlerta> alertas = new ArrayList<>();
    private List<MonitorFretesFreteCritico> fretesCriticos = new ArrayList<>();
    private List<MonitorFretesIndicadorStatus> statusFretes = new ArrayList<>();
    private List<MonitorFretesRankingMotorista> rankingMotoristas = new ArrayList<>();

    public int getFretesAbertos() { return fretesAbertos; }
    public void setFretesAbertos(int fretesAbertos) { this.fretesAbertos = fretesAbertos; }

    public int getFretesAtrasados() { return fretesAtrasados; }
    public void setFretesAtrasados(int fretesAtrasados) { this.fretesAtrasados = fretesAtrasados; }

    public int getEntregasHoje() { return entregasHoje; }
    public void setEntregasHoje(int entregasHoje) { this.entregasHoje = entregasHoje; }

    public int getVeiculosDisponiveis() { return veiculosDisponiveis; }
    public void setVeiculosDisponiveis(int veiculosDisponiveis) { this.veiculosDisponiveis = veiculosDisponiveis; }

    public int getMotoristasCnhVencida() { return motoristasCnhVencida; }
    public void setMotoristasCnhVencida(int motoristasCnhVencida) { this.motoristasCnhVencida = motoristasCnhVencida; }

    public int getClientesAtivos() { return clientesAtivos; }
    public void setClientesAtivos(int clientesAtivos) { this.clientesAtivos = clientesAtivos; }

    public BigDecimal getValorFretesMes() { return valorFretesMes; }
    public void setValorFretesMes(BigDecimal valorFretesMes) {
        this.valorFretesMes = valorFretesMes != null ? valorFretesMes : BigDecimal.ZERO;
    }

    public String getValorFretesMesFormatado() {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valorFretesMes);
    }

    public List<MonitorFretesAlerta> getAlertas() { return alertas; }
    public void setAlertas(List<MonitorFretesAlerta> alertas) { this.alertas = alertas; }

    public List<MonitorFretesFreteCritico> getFretesCriticos() { return fretesCriticos; }
    public void setFretesCriticos(List<MonitorFretesFreteCritico> fretesCriticos) { this.fretesCriticos = fretesCriticos; }

    public List<MonitorFretesIndicadorStatus> getStatusFretes() { return statusFretes; }
    public void setStatusFretes(List<MonitorFretesIndicadorStatus> statusFretes) { this.statusFretes = statusFretes; }

    public List<MonitorFretesRankingMotorista> getRankingMotoristas() { return rankingMotoristas; }
    public void setRankingMotoristas(List<MonitorFretesRankingMotorista> rankingMotoristas) { this.rankingMotoristas = rankingMotoristas; }
}
