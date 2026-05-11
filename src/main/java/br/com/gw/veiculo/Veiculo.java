package br.com.gw.veiculo;

import java.io.Serializable;

public class Veiculo implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Tipo   { TRUCK, CARRETA, VAN, UTILITARIO }
    public enum Status { DISPONIVEL, RESERVADO, EM_VIAGEM, EM_MANUTENCAO }

    private int    id;
    private String placa;
    private String rntrc;
    private Integer anoFabricacao;
    private Tipo   tipo;
    private Double taraKg;
    private double capacidadeKg;
    private Double volumeM3;
    private Status status;

    public Veiculo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getRntrc() { return rntrc; }
    public void setRntrc(String rntrc) { this.rntrc = rntrc; }

    public Integer getAnoFabricacao() { return anoFabricacao; }
    public void setAnoFabricacao(Integer anoFabricacao) { this.anoFabricacao = anoFabricacao; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public Double getTaraKg() { return taraKg; }
    public void setTaraKg(Double taraKg) { this.taraKg = taraKg; }

    public double getCapacidadeKg() { return capacidadeKg; }
    public void setCapacidadeKg(double capacidadeKg) { this.capacidadeKg = capacidadeKg; }

    public Double getVolumeM3() { return volumeM3; }
    public void setVolumeM3(Double volumeM3) { this.volumeM3 = volumeM3; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getStatusRotulo() {
        if (status == null) return "";
        switch (status) {
            case DISPONIVEL:
                return "Disponível";
            case RESERVADO:
                return "Reservado";
            case EM_VIAGEM:
                return "Em Viagem";
            case EM_MANUTENCAO:
                return "Manutenção";
            default:
                return status.name();
        }
    }

    public double getPesoUtil() {
        return capacidadeKg - (taraKg != null ? taraKg : 0.0);
    }

    @Override
    public String toString() {
        return "Veiculo{id=" + id + ", placa='" + placa + "'}";
    }
}
