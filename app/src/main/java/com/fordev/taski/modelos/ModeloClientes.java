package com.fordev.taski.modelos;

public class ModeloClientes {
    String nombreCliente,numeroTelefono;

    public ModeloClientes() {
    }

    public ModeloClientes(String nombreCliente, String numeroTelefono) {
        this.nombreCliente = nombreCliente;
        this.numeroTelefono = numeroTelefono;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

}
