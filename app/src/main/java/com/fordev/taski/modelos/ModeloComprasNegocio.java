package com.fordev.taski.modelos;

public class ModeloComprasNegocio {
    String nombreProveedor,nombreProducto,codigoDeBarras,id,fechaRegistro,month;
    double cantidadProducto,precioCosto,precioVenta,utilidad,costoTotal;
    boolean pagado;

    public ModeloComprasNegocio() {
    }

    public ModeloComprasNegocio(String nombreProveedor, String nombreProducto, String codigoDeBarras, String id, String month, double cantidadProducto, double precioCosto, double precioVenta, double utilidad, double costoTotal, boolean pagado) {
        this.nombreProveedor = nombreProveedor;
        this.nombreProducto = nombreProducto;
        this.codigoDeBarras = codigoDeBarras;
        this.id = id;
        this.month = month;
        this.cantidadProducto = cantidadProducto;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.utilidad = utilidad;
        this.costoTotal = costoTotal;
        this.pagado = pagado;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
    }

    public double getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(double cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(double precioCosto) {
        this.precioCosto = precioCosto;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public double getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(double utilidad) {
        this.utilidad = utilidad;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(double costoTotal) {
        this.costoTotal = costoTotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
