package com.fordev.taski.modelos;

public class ModeloInventario {
    String nombreProdcuto,fechaRegistro,id;
    Long timeStamp;
    int precioProducto, cantidadProducto;

    public ModeloInventario() {
    }

    public ModeloInventario(String nombreProdcuto, String fechaRegistro, String id, Long timeStamp, int precioProducto, int cantidadProducto) {
        this.nombreProdcuto = nombreProdcuto;
        this.fechaRegistro = fechaRegistro;
        this.id = id;
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

    public int getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(int precioProducto) {
        this.precioProducto = precioProducto;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }
}
