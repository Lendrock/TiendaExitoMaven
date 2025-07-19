package org.tiendaexito.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.tiendaexito.database.Conexion;
import org.tiendaexito.model.Producto;
import org.tiendaexito.system.Main;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class tecladosController implements Initializable {

    private Main principal;
    private ObservableList<Producto> listaTeclados;
    private int indiceActual = -1;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn colIdProducto, colNombre, colDescripcion,
            colPrecio, colStock, colCategoria;

    @FXML private TextField txtIdProducto, txtNombre, txtDescripcion,
            txtPrecio, txtStock, txtCategoria, txtBuscar;

    @FXML private Button btnAnterior, btnSiguiente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnasTabla();
        cargarTeclados("Teclados");

        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                indiceActual = listaTeclados.indexOf(newSelection);
                mostrarProductoSeleccionado(newSelection);
                actualizarBotonesNavegacion();
            } else {
                limpiarCamposDetalle();
                indiceActual = -1;
                actualizarBotonesNavegacion();
            }
        });

        txtCategoria.setText("Teclados");
        txtCategoria.setEditable(false);
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumnasTabla() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
    }

    private ArrayList<Producto> listarProductosPorCategoria(String categoria) {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_listarProductosPorCategoria(?);");
            enunciado.setString(1, categoria);
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                productos.add(new Producto(
                        resultado.getInt("idProducto"),
                        resultado.getString("nombre"),
                        resultado.getString("descripcion"),
                        resultado.getDouble("precio"),
                        resultado.getInt("stock"),
                        resultado.getString("categoria")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    private void cargarTeclados(String categoria) {
        listaTeclados = FXCollections.observableArrayList(listarProductosPorCategoria(categoria));
        tablaProductos.setItems(listaTeclados);

        if (!listaTeclados.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
        } else {
            limpiarCamposDetalle();
            indiceActual = -1;
        }
        actualizarBotonesNavegacion();
    }

    private void mostrarProductoSeleccionado(Producto producto) {
        if (producto != null) {
            txtIdProducto.setText(String.valueOf(producto.getIdProducto()));
            txtNombre.setText(producto.getNombre());
            txtDescripcion.setText(producto.getDescripcion());
            txtPrecio.setText(String.format("%.2f", producto.getPrecio()));
            txtStock.setText(String.valueOf(producto.getStock()));
            txtCategoria.setText(producto.getCategoria());
            txtCategoria.setEditable(false);
        } else {
            limpiarCamposDetalle();
        }
    }

    private void limpiarCamposDetalle() {
        txtIdProducto.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtCategoria.clear();
    }

    private void actualizarBotonesNavegacion() {
        btnAnterior.setDisable(indiceActual <= 0);
        btnSiguiente.setDisable(indiceActual >= listaTeclados.size() - 1 || listaTeclados.isEmpty());
    }

    @FXML
    private void mostrarSiguiente() {
        if (indiceActual < listaTeclados.size() - 1) {
            indiceActual++;
            tablaProductos.getSelectionModel().select(indiceActual);
            tablaProductos.scrollTo(indiceActual);
        }
    }

    @FXML
    private void mostrarAnterior() {
        if (indiceActual > 0) {
            indiceActual--;
            tablaProductos.getSelectionModel().select(indiceActual);
            tablaProductos.scrollTo(indiceActual);
        }
    }

    @FXML
    private void buscarProductoUser() {
        String textoABuscar = txtBuscar.getText().toLowerCase().trim();

        if (textoABuscar.isEmpty()) {
            cargarTeclados("Teclados");
            return;
        }

        ArrayList<Producto> resultadosFiltrados = new ArrayList<>();
        for (Producto p : listarProductosPorCategoria("Teclados")) {
            if (p.getNombre().toLowerCase().contains(textoABuscar) ||
                p.getDescripcion().toLowerCase().contains(textoABuscar)) {
                resultadosFiltrados.add(p);
            }
        }

        listaTeclados = FXCollections.observableArrayList(resultadosFiltrados);
        tablaProductos.setItems(listaTeclados);

        if (!listaTeclados.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
        } else {
            limpiarCamposDetalle();
            indiceActual = -1;
            System.out.println("No se encontraron resultados para la búsqueda en la categoría Teclados.");
        }
        actualizarBotonesNavegacion();
    }

    @FXML
    public void escenaPaginaInicioUser(){
        principal.escenaInicioUser();
    }

    @FXML
    public void escenaBajos(){
        principal.escenaBajos();
    }

    @FXML
    public void escenaBaterias(){
        principal.escenaBaterias();
    }

    @FXML
    public void escenaGuitarras(){
        principal.escenaGuitarras();
    }
}