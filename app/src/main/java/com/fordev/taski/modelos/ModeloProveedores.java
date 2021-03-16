package com.fordev.taski.modelos;

public class ModeloProveedores {
    String nombreProveedor,numeroTelefono,descProveedor;

    public ModeloProveedores() {
    }

    public ModeloProveedores(String nombreCliente, String numeroTelefono, String descProveedor) {
        this.nombreProveedor = nombreCliente;
        this.numeroTelefono = numeroTelefono;
        this.descProveedor = descProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getDescProveedor() {
        return descProveedor;
    }

    public void setDescProveedor(String descProveedor) {
        this.descProveedor = descProveedor;
    }
}
