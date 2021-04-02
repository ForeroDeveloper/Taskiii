package com.fordev.taski.modelos;

public class ModeloInventario {
    String nombreProdcuto,fechaRegistro,id, codigoDeBarras;
    Long timeStamp;
    double precioProducto, cantidadProducto;

    public ModeloInventario() {
    }

    public ModeloInventario(String nombreProdcuto, String fechaRegistro, String id, String codigoDeBarras, Long timeStamp, double precioProducto, double cantidadProducto) {
        this.nombreProdcuto = nombreProdcuto;
        this.fechaRegistro = fechaRegistro;
        this.id = id;
        this.codigoDeBarras = codigoDeBarras;
        this.timeStamp = timeStamp;
        this.precioProducto = precioProducto;
        this.cantidadProducto = cantidadProducto;
    }

    public String getNombreProdcuto() {
        return nombreProdcuto;
    }

    public void setNombreProdcuto(String nombreProdcuto) {
        this.nombreProdcuto = nombreProdcuto;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }

    public double getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(double cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }
}
