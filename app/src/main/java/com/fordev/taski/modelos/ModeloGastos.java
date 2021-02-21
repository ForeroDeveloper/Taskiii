package com.fordev.taski.modelos;

public class ModeloGastos {
    String nombreProdcuto,fechaRegistro,id;
    Long timeStamp;
    int precioProducto, cantidadProducto,valorTotalCalculadoAutomatico,precioFinalPorElUsuario,precioTotaldeTodosLosProductos;

    public ModeloGastos() {
    }

    public ModeloGastos(String nombreProdcuto, String fechaRegistro, String id, Long timeStamp, int precioProducto, int cantidadProducto, int valorTotalCalculadoAutomatico, int precioFinalPorElUsuario, int precioTotaldeTodosLosProductos) {
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

    public int getValorTotalCalculadoAutomatico() {
        return valorTotalCalculadoAutomatico;
    }

    public void setValorTotalCalculadoAutomatico(int valorTotalCalculadoAutomatico) {
        this.valorTotalCalculadoAutomatico = valorTotalCalculadoAutomatico;
    }

    public int getPrecioFinalPorElUsuario() {
        return precioFinalPorElUsuario;
    }

    public void setPrecioFinalPorElUsuario(int precioFinalPorElUsuario) {
        this.precioFinalPorElUsuario = precioFinalPorElUsuario;
    }

    public int getPrecioTotaldeTodosLosProductos() {
        return precioTotaldeTodosLosProductos;
    }

    public void setPrecioTotaldeTodosLosProductos(int precioTotaldeTodosLosProductos) {
        this.precioTotaldeTodosLosProductos = precioTotaldeTodosLosProductos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
