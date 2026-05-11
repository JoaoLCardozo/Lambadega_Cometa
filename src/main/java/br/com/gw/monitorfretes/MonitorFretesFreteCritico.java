package br.com.gw.monitorfretes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MonitorFretesFreteCritico {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int id;
    private String numero;
    private String destinatario;
    private String municipioDestino;
    private String ufDestino;
    private String status;
    private LocalDate dataPrevisaoEntrega;
    private int diasAtraso;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getMunicipioDestino() { return municipioDestino; }
    public void setMunicipioDestino(String municipioDestino) { this.municipioDestino = municipioDestino; }

    public String getUfDestino() { return ufDestino; }
    public void setUfDestino(String ufDestino) { this.ufDestino = ufDestino; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataPrevisaoEntrega() { return dataPrevisaoEntrega; }
    public void setDataPrevisaoEntrega(LocalDate dataPrevisaoEntrega) { this.dataPrevisaoEntrega = dataPrevisaoEntrega; }

    public int getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(int diasAtraso) { this.diasAtraso = diasAtraso; }

    public String getDataPrevisaoFormatada() {
        return dataPrevisaoEntrega != null ? dataPrevisaoEntrega.format(FORMATO_DATA) : "";
    }

    public String getDestinoFormatado() {
        if (municipioDestino == null) return "";
        return municipioDestino + (ufDestino != null && !ufDestino.isEmpty() ? "/" + ufDestino : "");
    }

    public String getSituacao() {
        if (diasAtraso > 0) {
            return diasAtraso == 1 ? "1 dia atrasado" : diasAtraso + " dias atrasado";
        }
        return "Vence hoje";
    }
}
