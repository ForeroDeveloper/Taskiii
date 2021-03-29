package com.fordev.taski.modelos;

public class ModeloListaPedidos {
    String nombrePedido,nombreCliente,numeroCliente;
    int cantidadPedido;

    public ModeloListaPedidos() {
    }

    public ModeloListaPedidos(String nombrePedido, String nombreCliente, String numeroCliente, int cantidadPedido) {
        this.nombrePedido = nombrePedido;
        this.nombreCliente = nombreCliente;
        this.numeroCliente = numeroCliente;
        this.cantidadPedido = cantidadPedido;
    }

    public String getNombrePedido() {
        return nombrePedido;
    }

    public void setNombrePedido(String nombrePedido) {
        this.nombrePedido = nombrePedido;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public int getCantidadPedido() {
        return cantidadPedido;
    }

    public void setCantidadPedido(int cantidadPedido) {
        this.cantidadPedido = cantidadPedido;
    }

    public String getNumeroCliente() {
        return numeroCliente;
    }

    public void setNumeroCliente(String numeroCliente) {
        this.numeroCliente = numeroCliente;
    }
}
