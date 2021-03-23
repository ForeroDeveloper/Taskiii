package com.fordev.taski;

public class UserInfoBasica {
    String nombreNegocio,nitNegocio,nombrePropietario,tipoDeDocumento,numeroDeDocumento,correoElectronico,primeraVez,ubicacionNegocio,direccionNegocio,telefonoNegocio,apellidoPropietario,
            correoUsuarioLogin,fechaFinPremium,celularUsuarioLogin;
    Boolean premium;


    public UserInfoBasica() {
    }

    public UserInfoBasica(String nombreNegocio, String nitNegocio, String nombrePropietario, String tipoDeDocumento, String numeroDeDocumento, String correoElectronico, String primeraVez, String ubicacionNegocio, String direccionNegocio, String telefonoNegocio, String apellidoPropietario, String correoUsuarioLogin, String fechaFinPremium, String celularUsuarioLogin, Boolean premium) {
        this.nombreNegocio = nombreNegocio;
        this.nitNegocio = nitNegocio;
        this.nombrePropietario = nombrePropietario;
        this.tipoDeDocumento = tipoDeDocumento;
        this.numeroDeDocumento = numeroDeDocumento;
        this.correoElectronico = correoElectronico;
        this.primeraVez = primeraVez;
        this.ubicacionNegocio = ubicacionNegocio;
        this.direccionNegocio = direccionNegocio;
        this.telefonoNegocio = telefonoNegocio;
        this.apellidoPropietario = apellidoPropietario;
        this.correoUsuarioLogin = correoUsuarioLogin;
        this.fechaFinPremium = fechaFinPremium;
        this.celularUsuarioLogin = celularUsuarioLogin;
        this.premium = premium;
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

    public String getUbicacionNegocio() {
        return ubicacionNegocio;
    }

    public void setUbicacionNegocio(String ubicacionNegocio) {
        this.ubicacionNegocio = ubicacionNegocio;
    }

    public String getDireccionNegocio() {
        return direccionNegocio;
    }

    public void setDireccionNegocio(String direccionNegocio) {
        this.direccionNegocio = direccionNegocio;
    }

    public String getTelefonoNegocio() {
        return telefonoNegocio;
    }

    public void setTelefonoNegocio(String telefonoNegocio) {
        this.telefonoNegocio = telefonoNegocio;
    }

    public String getApellidoPropietario() {
        return apellidoPropietario;
    }

    public void setApellidoPropietario(String apellidoPropietario) {
        this.apellidoPropietario = apellidoPropietario;
    }

    public String getCorreoUsuarioLogin() {
        return correoUsuarioLogin;
    }

    public void setCorreoUsuarioLogin(String correoUsuarioLogin) {
        this.correoUsuarioLogin = correoUsuarioLogin;
    }

    public String getFechaFinPremium() {
        return fechaFinPremium;
    }

    public void setFechaFinPremium(String fechaFinPremium) {
        this.fechaFinPremium = fechaFinPremium;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getCelularUsuarioLogin() {
        return celularUsuarioLogin;
    }

    public void setCelularUsuarioLogin(String celularUsuarioLogin) {
        this.celularUsuarioLogin = celularUsuarioLogin;
    }
}
