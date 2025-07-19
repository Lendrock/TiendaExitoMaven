package org.tiendaexito.model;

/**
 * Clase modelo de DetalleCompra
 * @author Lendrock
 */
public class DetalleCompra {
    private int idDetalle;
    private int idCompra;
    private int idProducto;
    private int cantidad;
    private double precioUnitario; 
    private double subtotal;

    public DetalleCompra() {
    }

    public DetalleCompra(int idDetalle, int idCompra, int idProducto, int cantidad, double precioUnitario, double subtotal) {
        this.idDetalle = idDetalle;
        this.idCompra = idCompra;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return idDetalle + " | Compra: " + idCompra + " | Producto: " + idProducto + " | Cantidad: " + cantidad + " | Subtotal: " + subtotal;
    }
}