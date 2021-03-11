package com.fordev.taski.modelos;

public class ModeloVentaInventario {
    String nombreProdcuto,fechaRegistro,id;
    Long timeStamp;
    double precioProducto, cantidadProducto,valorTotalCalculadoAutomatico,precioFinalPorElUsuario,precioTotaldeTodosLosProductos;

    public ModeloVentaInventario() {
    }

    public ModeloVentaInventario(String nombreProdcuto, String fechaRegistro, String id, Long timeStamp, double precioProducto, double cantidadProducto, double valorTotalCalculadoAutomatico, double precioFinalPorElUsuario, double precioTotaldeTodosLosProductos) {
        this.nombreProdcuto = nombreProdcuto;
        this.fechaRegistro = fechaRegistro;
        this.id = id;
        this.timeStamp = timeStamp;
        this.precioProducto = precioProducto;
        this.cantidadProducto = cantidadProducto;
        this.valorTotalCalculadoAutomatico = valorTotalCalculadoAutomatico;
        this.precioFinalPorElUsuario = precioFinalPorElUsuario;
        this.precioTotaldeTodosLosProductos = precioTotaldeTodosLosProductos;
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

    public double getValorTotalCalculadoAutomatico() {
        return valorTotalCalculadoAutomatico;
    }

    public void setValorTotalCalculadoAutomatico(double valorTotalCalculadoAutomatico) {
        this.valorTotalCalculadoAutomatico = valorTotalCalculadoAutomatico;
    }

    public double getPrecioFinalPorElUsuario() {
        return precioFinalPorElUsuario;
    }

    public void setPrecioFinalPorElUsuario(double precioFinalPorElUsuario) {
        this.precioFinalPorElUsuario = precioFinalPorElUsuario;
    }

    public double getPrecioTotaldeTodosLosProductos() {
        return precioTotaldeTodosLosProductos;
    }

    public void setPrecioTotaldeTodosLosProductos(double precioTotaldeTodosLosProductos) {
        this.precioTotaldeTodosLosProductos = precioTotaldeTodosLosProductos;
    }
}
