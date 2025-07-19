package org.tiendaexito.model;

import java.time.LocalDateTime;

/**
 * Clase modelo de Compra
 * @author Lendrock
 */
public class Compra {
    private int idCompra;
    private LocalDateTime fecha;
    private int idUser;
    private double total;
    private String metodoPago;
    private String estado;

    public Compra() {
    }

    public Compra(int idCompra, LocalDateTime fecha, int idUser, double total, String metodoPago, String estado) {
        this.idCompra = idCompra;
        this.fecha = fecha;
        this.idUser = idUser;
        this.total = total;
        this.metodoPago = metodoPago;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public int getIdUser() { 
        return idUser;
    }

    public void setIdUser(int idUser) { 
        this.idUser = idUser;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return idCompra + " | Fecha: " + fecha.toLocalDate() + " | Total: " + total + " | Estado: " + estado;
    }
}
