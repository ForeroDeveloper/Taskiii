package com.fordev.taski;

public class Constants {

    public String nombreProveedor,nombreProducto,cantidad,precioCosto,precioVenta,utilidad,totalcosto, pagado,fechaRegistro;


    public Constants() {
    }

    public Constants(String nombreProveedor, String nombreProducto, String cantidad, String precioCosto, String precioVenta, String utilidad, String totalcosto, String pagado, String fechaRegistro) {
        this.nombreProveedor = nombreProveedor;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.utilidad = utilidad;
        this.totalcosto = totalcosto;
        this.pagado = pagado;
        this.fechaRegistro = fechaRegistro;
    }

    public String getPagado() {
        return pagado;
    }

    public void setPagado(String pagado) {
        this.pagado = pagado;
    }

    public String getTotalcosto() {
        return totalcosto;
    }

    public void setTotalcosto(String totalcosto) {
        this.totalcosto = totalcosto;
    }

    public String getUtilidad() {
        return utilidad;
    }

    public void setUtilidad(String utilidad) {
        this.utilidad = utilidad;
    }

    public String getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(String precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(String precioCosto) {
        this.precioCosto = precioCosto;
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

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
