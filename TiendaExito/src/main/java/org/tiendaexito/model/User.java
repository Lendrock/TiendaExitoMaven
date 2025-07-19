package org.tiendaexito.model;

/**
 * Clase modelo de User
 * @author Lendrock
 */
public class User {
    private int idUser;
    private String usuario;
    private String correo;
    private String contraseña; 
    private String role; 

    public User() {
    }

    public User(int idUser, String usuario, String correo, String contraseña, String role) {
        this.idUser = idUser;
        this.usuario = usuario;
        this.correo = correo;
        this.contraseña = contraseña;
        this.role = role;
    }


    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void voidsetContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return idUser + " | " + usuario;
    }
}
