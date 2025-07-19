package org.tiendaexito.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.tiendaexito.model.DetalleCompra;
import org.tiendaexito.system.Main;

import org.tiendaexito.database.Conexion;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp; // Import for Timestamp
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import org.tiendaexito.model.Compra;
import org.tiendaexito.model.Producto;

public class detalleCompraController implements Initializable {

    private Main principal;
    private ObservableList<DetalleCompra> listaDetalleCompra;
    private ObservableList<Compra> listaCompra; // This will be null initially
    private ObservableList<Producto> listaProducto; // This will be null initially
    private DetalleCompra modeloDetalleCompra;

    private enum EstadoFormulario {
        AGREGAR, EDITAR, ELIMINAR, NINGUNO, ACTUALIZAR
    }

    EstadoFormulario estadoActual = EstadoFormulario.NINGUNO;

    @FXML private TableView<DetalleCompra> tablaDetalleCompras;
    @FXML private TextField txtIdDetalle,
                            txtCantidad, txtPrecioUnitario, txtSubtotal, txtBuscar;
    @FXML private ComboBox<Compra> cmbIdCompra;
    @FXML private ComboBox<Producto> cmbIdProducto;
    @FXML private TableColumn colIdDetalle, colIdCompra, colIdProducto,
                            colCantidad, colPrecioUnitario, colSubtotal;
    @FXML private Button btnEliminar, btnEditar, btnNuevo, btnBuscar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumna();
        // CORRECTED ORDER: Initialize ComboBox data BEFORE loading table data
        cargarDatosComboBox();       // Populates listaCompra and listaProducto
        cargarTablasDetalleCompra(); // Depends on listaCompra and listaProducto being populated
        tablaDetalleCompras.setOnMouseClicked(eh -> cargarDetalleCompraFormulario());
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumna() {
        colIdDetalle.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Integer>("idDetalle"));
        colIdCompra.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Integer>("idCompra"));
        colIdProducto.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Integer>("idProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Integer>("cantidad"));
        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Double>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<DetalleCompra, Double>("subtotal"));
    }

    private ArrayList<DetalleCompra> listarDetalleCompras() {
        ArrayList<DetalleCompra> detallesCompra = new ArrayList<>();
        try {
            // Assuming you have fixed the sp_listarDetalleCompras vs sp_listarDetalleCompra issue.
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_listarDetalleCompras();"); // Or sp_listarDetalleCompra if you kept it singular
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                detallesCompra.add(new DetalleCompra(
                        resultado.getInt("idDetalle"),
                        resultado.getInt("idCompra"),
                        resultado.getInt("idProducto"),
                        resultado.getInt("cantidad"),
                        resultado.getDouble("precioUnitario"), // Assuming you've renamed the column in DB
                        resultado.getDouble("subtotal")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar detalles de compra: " + e.getMessage());
            e.printStackTrace();
        }
        return detallesCompra;
    }

    private ArrayList<Compra> listarCompras() {
        ArrayList<Compra> compras = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_listarCompras();");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                Timestamp fechaTimestamp = resultado.getTimestamp("fecha");
                compras.add(new Compra(
                        resultado.getInt("idCompra"),
                        fechaTimestamp != null ? fechaTimestamp.toLocalDateTime() : null,
                        resultado.getInt("idUser"),
                        resultado.getDouble("total"),
                        resultado.getString("metodoPago"),
                        resultado.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar compras: " + e.getMessage());
            e.printStackTrace();
        }
        return compras;
    }

    private ArrayList<Producto> listarProductos() {
        ArrayList<Producto> productos = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_listarProductos();");
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
            System.out.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    private void cargarDetalleCompraFormulario() {
        DetalleCompra detalleCompra = tablaDetalleCompras.getSelectionModel().getSelectedItem();
        if (detalleCompra != null) {
            txtIdDetalle.setText(String.valueOf(detalleCompra.getIdDetalle()));
            // These calls now work because listaCompra and listaProducto are initialized
            cmbIdCompra.getSelectionModel().select(buscarCompraPorId(detalleCompra.getIdCompra()));
            cmbIdProducto.getSelectionModel().select(buscarProductoPorId(detalleCompra.getIdProducto()));
            txtCantidad.setText(String.valueOf(detalleCompra.getCantidad()));
            txtPrecioUnitario.setText(String.valueOf(detalleCompra.getPrecioUnitario()));
            txtSubtotal.setText(String.valueOf(detalleCompra.getSubtotal()));
        } else {
            limpiarCampos();
        }
    }

    private Compra buscarCompraPorId(int id) {
        // listaCompra is now guaranteed to be initialized here
        for (Compra c : listaCompra) {
            if (c.getIdCompra() == id) {
                return c;
            }
        }
        return null;
    }

    private Producto buscarProductoPorId(int id) {
        // listaProducto is now guaranteed to be initialized here
        for (Producto p : listaProducto) {
            if (p.getIdProducto() == id) {
                return p;
            }
        }
        return null;
    }

    private void cargarTablasDetalleCompra() {
        listaDetalleCompra = FXCollections.observableArrayList(listarDetalleCompras());
        tablaDetalleCompras.setItems(listaDetalleCompra);
        if (!listaDetalleCompra.isEmpty()) {
            tablaDetalleCompras.getSelectionModel().selectFirst();
            cargarDetalleCompraFormulario();
        } else {
            limpiarCampos();
        }
    }

    private void cargarDatosComboBox() {
        // These lines now populate the lists before they are used
        listaCompra = FXCollections.observableArrayList(listarCompras());
        cmbIdCompra.setItems(listaCompra);
        listaProducto = FXCollections.observableArrayList(listarProductos());
        cmbIdProducto.setItems(listaProducto);
    }

    private DetalleCompra cargarModeloDetalleCompra() {
        int idDetalle = txtIdDetalle.getText().isEmpty() ? 0 : Integer.parseInt(txtIdDetalle.getText());
        int idCompra = cmbIdCompra.getSelectionModel().getSelectedItem() != null ? cmbIdCompra.getSelectionModel().getSelectedItem().getIdCompra() : 0;
        int idProducto = cmbIdProducto.getSelectionModel().getSelectedItem() != null ? cmbIdProducto.getSelectionModel().getSelectedItem().getIdProducto() : 0;
        int cantidad = 0;
        double precioUnitario = 0.0;
        double subtotal = 0.0;

        try {
            cantidad = Integer.parseInt(txtCantidad.getText());
        } catch (NumberFormatException e) {
            System.err.println("Error de formato en Cantidad: " + txtCantidad.getText());
        }
        try {
            precioUnitario = Double.parseDouble(txtPrecioUnitario.getText());
        } catch (NumberFormatException e) {
            System.err.println("Error de formato en Precio Unitario: " + txtPrecioUnitario.getText());
        }
        try {
            subtotal = Double.parseDouble(txtSubtotal.getText());
        } catch (NumberFormatException e) {
            System.err.println("Error de formato en Subtotal: " + txtSubtotal.getText());
        }

        return new DetalleCompra(
                idDetalle,
                idCompra,
                idProducto,
                cantidad,
                precioUnitario,
                subtotal
        );
    }

    private void insertarDetalleCompra() {
        modeloDetalleCompra = cargarModeloDetalleCompra();

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_agregarDetalleCompra(?,?,?,?,?);");
            enunciado.setInt(1, modeloDetalleCompra.getIdCompra());
            enunciado.setInt(2, modeloDetalleCompra.getIdProducto());
            enunciado.setInt(3, modeloDetalleCompra.getCantidad());
            enunciado.setDouble(4, modeloDetalleCompra.getPrecioUnitario());
            enunciado.setDouble(5, modeloDetalleCompra.getSubtotal());

            int registrosAgregados = enunciado.executeUpdate();
            if (registrosAgregados > 0) {
                System.out.println("Detalle de compra agregado correctamente");
                cargarTablasDetalleCompra();
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar un detalle de compra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarDetalleCompra() {
        modeloDetalleCompra = cargarModeloDetalleCompra();

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_actualizarDetalleCompra(?,?,?,?,?,?);");
            enunciado.setInt(1, modeloDetalleCompra.getIdDetalle());
            enunciado.setInt(2, modeloDetalleCompra.getIdCompra());
            enunciado.setInt(3, modeloDetalleCompra.getIdProducto());
            enunciado.setInt(4, modeloDetalleCompra.getCantidad());
            enunciado.setDouble(5, modeloDetalleCompra.getPrecioUnitario());
            enunciado.setDouble(6, modeloDetalleCompra.getSubtotal());
            enunciado.execute();
            cargarTablasDetalleCompra();
        } catch (SQLException e) {
            System.out.println("Error al actualizar un detalle de compra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarDetalleCompra() {
        modeloDetalleCompra = tablaDetalleCompras.getSelectionModel().getSelectedItem();
        if (modeloDetalleCompra != null) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion()
                        .prepareCall("call sp_eliminarDetalleCompra(?);");
                enunciado.setInt(1, modeloDetalleCompra.getIdDetalle());
                enunciado.execute();
                System.out.println("Detalle de compra eliminado correctamente");
                cargarTablasDetalleCompra();
                limpiarCampos();
            } catch (SQLException e) {
                System.out.println("Error al eliminar un detalle de compra: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No hay detalle de compra seleccionado para eliminar.");
        }
    }

    private void limpiarCampos() {
        txtIdDetalle.clear();
        cmbIdCompra.getSelectionModel().clearSelection();
        cmbIdProducto.getSelectionModel().clearSelection();
        txtCantidad.clear();
        txtPrecioUnitario.clear();
        txtSubtotal.clear();
    }

    private void actualizarEstadoFormulario(EstadoFormulario estado) {
        estadoActual = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.EDITAR);

        cmbIdCompra.setDisable(!activo);
        cmbIdProducto.setDisable(!activo);
        txtCantidad.setDisable(!activo);
        txtPrecioUnitario.setDisable(!activo);
        txtSubtotal.setDisable(!activo);

        tablaDetalleCompras.setDisable(activo);
        btnBuscar.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnNuevo.setText(activo ? "GUARDAR" : "NUEVO");
        btnEliminar.setText(activo ? "CANCELAR" : "ELIMINAR");
        btnEditar.setDisable(activo);
    }

    @FXML
    private void agregarDetalleCompra() {
        switch (estadoActual) {
            case NINGUNO:
                limpiarCampos();
                System.out.println("Voy a crear un registro para DetalleCompra");
                actualizarEstadoFormulario(EstadoFormulario.AGREGAR);
                break;
            case AGREGAR:
                insertarDetalleCompra();
                System.out.println("Voy a guardar los datos ingresados");
                actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
                break;
            case EDITAR:
                if (tablaDetalleCompras.getSelectionModel().getSelectedItem() != null) {
                    actualizarDetalleCompra();
                    System.out.println("Voy a guardar edicion indicada");
                    actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
                } else {
                    System.out.println("Debe seleccionar un detalle de compra para editar.");
                }
                break;
            default:
                break;
        }
    }

    @FXML
    private void editarDetalleCompra() {
        if (tablaDetalleCompras.getSelectionModel().getSelectedItem() != null) {
            actualizarEstadoFormulario(EstadoFormulario.EDITAR);
            System.out.println("Voy a editar el registro seleccionado");
        } else {
            System.out.println("Debe seleccionar un detalle de compra para editar.");
        }
    }

    @FXML
    private void cancelarDetalleCompra() {
        if (estadoActual == EstadoFormulario.NINGUNO) {
            eliminarDetalleCompra();
            System.out.println("Voy a eliminar registro");
        } else {
            actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
            cargarTablasDetalleCompra();
            limpiarCampos();
        }
    }

    @FXML
    private void buscarDetalleCompra() {
        String textoABuscar = txtBuscar.getText().toLowerCase();
        ArrayList<DetalleCompra> resultadoBusqueda = new ArrayList<>();
        for (DetalleCompra dc : listaDetalleCompra) {
            if (String.valueOf(dc.getIdDetalle()).contains(textoABuscar) ||
                String.valueOf(dc.getIdCompra()).contains(textoABuscar) ||
                String.valueOf(dc.getIdProducto()).contains(textoABuscar)) {
                resultadoBusqueda.add(dc);
            }
        }
        tablaDetalleCompras.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaDetalleCompras.getSelectionModel().selectFirst();
            cargarDetalleCompraFormulario();
        } else {
            limpiarCampos();
        }
    }

    @FXML
    public void escenaPaginaInicio() {
        principal.escenaPaginaInicio();
    }
}