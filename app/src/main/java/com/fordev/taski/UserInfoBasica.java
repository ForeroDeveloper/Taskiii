package com.fordev.taski;

public class UserInfoBasica {
    String nombreNegocio,nitNegocio,nombrePropietario,tipoDeDocumento,numeroDeDocumento,correoElectronico,primeraVez;


    public UserInfoBasica() {
    }

    public UserInfoBasica(String nombreNegocio, String nitNegocio, String nombrePropietario, String tipoDeDocumento, String numeroDeDocumento, String correoElectronico, String primeraVez) {
        this.nombreNegocio = nombreNegocio;
        this.nitNegocio = nitNegocio;
        this.nombrePropietario = nombrePropietario;
        this.tipoDeDocumento = tipoDeDocumento;
        this.numeroDeDocumento = numeroDeDocumento;
        this.correoElectronico = correoElectronico;
        this.primeraVez = primeraVez;
    }

    public String getNombreNegocio() {
        return nombreNegocio;
    }

    public void setNombreNegocio(String nombreNegocio) {
        this.nombreNegocio = nombreNegocio;
    }

    public String getNitNegocio() {
        return nitNegocio;
    }

    public void setNitNegocio(String nitNegocio) {
        this.nitNegocio = nitNegocio;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getTipoDeDocumento() {
        return tipoDeDocumento;
    }

    public void setTipoDeDocumento(String tipoDeDocumento) {
        this.tipoDeDocumento = tipoDeDocumento;
    }

    public String getNumeroDeDocumento() {
        return numeroDeDocumento;
    }

    public void setNumeroDeDocumento(String numeroDeDocumento) {
        this.numeroDeDocumento = numeroDeDocumento;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getPrimeraVez() {
        return primeraVez;
    }

    public void setPrimeraVez(String primeraVez) {
        this.primeraVez = primeraVez;
    }
}
