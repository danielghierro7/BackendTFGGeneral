package org.example.backendtfggeneral.beans;

//Dto para elegir una parada y ver cuanto tiempo le queda al bus para llegar
public class BusLlegadaDTO {
    private String nombreLinea; // Ej: "Línea 140"
    private Integer minutosRestantes;

    public String getDestinoFinal() {
        return destinoFinal;
    }

    public void setDestinoFinal(String destinoFinal) {
        this.destinoFinal = destinoFinal;
    }

    public Integer getMinutosRestantes() {
        return minutosRestantes;
    }

    public void setMinutosRestantes(Integer minutosRestantes) {
        this.minutosRestantes = minutosRestantes;
    }

    public String getNombreLinea() {
        return nombreLinea;
    }

    public void setNombreLinea(String nombreLinea) {
        this.nombreLinea = nombreLinea;
    }

    private String destinoFinal; // Opcional, pero queda muy bien en la interfaz

    public BusLlegadaDTO(String nombreLinea, Integer minutosRestantes, String destinoFinal) {
        this.nombreLinea = nombreLinea;
        this.minutosRestantes = minutosRestantes;
        this.destinoFinal = destinoFinal;
    }

}