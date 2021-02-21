package com.fordev.taski.modelos;

public class ModeloFacturaCreada {
    String id,conceptoDeVenta,notasInternas,cliente,fechaRegistro,metodoDePago,year,day,month;
    int totalCalculado;
    Long timeStamp;
    boolean estadoDePago;

    public ModeloFacturaCreada() {
    }

    public ModeloFacturaCreada(String id, String conceptoDeVenta, String notasInternas, String cliente, String fechaRegistro, String metodoDePago, String year, String day, String month, int totalCalculado, Long timeStamp, boolean estadoDePago) {
        this.id = id;
        this.conceptoDeVenta = conceptoDeVenta;
        this.notasInternas = notasInternas;
        this.cliente = cliente;
        this.fechaRegistro = fechaRegistro;
        this.metodoDePago = metodoDePago;
        this.year = year;
        this.day = day;
        this.month = month;
        this.totalCalculado = totalCalculado;
        this.timeStamp = timeStamp;
        this.estadoDePago = estadoDePago;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConceptoDeVenta() {
        return conceptoDeVenta;
    }

    public void setConceptoDeVenta(String conceptoDeVenta) {
        this.conceptoDeVenta = conceptoDeVenta;
    }

    public String getNotasInternas() {
        return notasInternas;
    }

    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(String metodoDePago) {
        this.metodoDePago = metodoDePago;
    }

    public int getTotalCalculado() {
        return totalCalculado;
    }

    public void setTotalCalculado(int totalCalculado) {
        this.totalCalculado = totalCalculado;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isEstadoDePago() {
        return estadoDePago;
    }

    public void setEstadoDePago(boolean estadoDePago) {
        this.estadoDePago = estadoDePago;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
