package br.com.gw.monitorfretes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MonitorFretesRankingMotorista {
    private static final Locale PT_BR = new Locale("pt", "BR");

    private String nome;
    private int entregas;
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getEntregas() { return entregas; }
    public void setEntregas(int entregas) { this.entregas = entregas; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal != null ? valorTotal : BigDecimal.ZERO; }

    public String getValorTotalFormatado() {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valorTotal);
    }
}
