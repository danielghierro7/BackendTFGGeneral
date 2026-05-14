package org.example.backendtfggeneral.beans;

public class LoginRequest {
    private String busId;
    private String password;
    private Long idLinea; // <--- Añadimos este campo para saber qué ruta hace el bus

    // Constructores
    public LoginRequest() {}

    public LoginRequest(String busId, String password, Long idLinea) {
        this.busId = busId;
        this.password = password;
        this.idLinea = idLinea;
    }

    // Getters y Setters
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getIdLinea() { return idLinea; }
    public void setIdLinea(Long idLinea) { this.idLinea = idLinea; }
}