package org.example.backendtfggeneral.beans;



public class LoginRequest {
    private String busId;
    private String password;

    // Constructores
    public LoginRequest() {}
    public LoginRequest(String busId, String password) {
        this.busId = busId;
        this.password = password;
    }

    // Getters y Setters
    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}