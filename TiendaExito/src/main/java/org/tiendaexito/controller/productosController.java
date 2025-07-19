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
import org.tiendaexito.model.Producto; 
import org.tiendaexito.system.Main; 

import org.tiendaexito.database.Conexion; 
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javafx.collections.FXCollections;

/**
 * FXML Controller class for Producto
 *
 * @author Lendrock
 */
public class productosController implements Initializable {

    private Main principal;
    private ObservableList<Producto> listaProducto;
    private Producto modeloProducto;

    private enum EstadoFormulario {
        AGREGAR, EDITAR, ELIMINAR, NINGUNO, ACTUALIZAR
    }

    EstadoFormulario estadoActual = EstadoFormulario.NINGUNO;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TextField txtIdProducto, txtNombre, txtDescripcion, txtPrecio,
            txtStock, txtCategoria, txtBuscar;
    @FXML private TableColumn colIdProducto, colNombre, colDescripcion,
            colPrecio, colStock, colCategoria;
    @FXML private Button btnEliminar, btnEditar, btnNuevo, btnBuscar;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumna();
        cargarTablasProductos();
        tablaProductos.setOnMouseClicked(eh -> cargarProductoFormulario());
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumna() {
        colIdProducto.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<Producto, String>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Producto, String>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<Producto, Double>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<Producto, Integer>("stock"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<Producto, String>("categoria"));
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

    private void cargarProductoFormulario() {
        Producto producto = tablaProductos.getSelectionModel().getSelectedItem();
        if (producto != null) {
            txtIdProducto.setText(String.valueOf(producto.getIdProducto()));
            txtNombre.setText(producto.getNombre());
            txtDescripcion.setText(producto.getDescripcion());
            txtPrecio.setText(String.valueOf(producto.getPrecio()));
            txtStock.setText(String.valueOf(producto.getStock()));
            txtCategoria.setText(producto.getCategoria());
        } else {
            limpiarCampos();
        }
    }

    private void cargarTablasProductos() {
        listaProducto = FXCollections.observableArrayList(listarProductos());
        tablaProductos.setItems(listaProducto);
        if (!listaProducto.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
            cargarProductoFormulario();
        } else {
            limpiarCampos();
        }
    }

    private Producto cargarModeloProductos() {
        int idProducto = txtIdProducto.getText().isEmpty() ? 0 : Integer.parseInt(txtIdProducto.getText());
        double precio = 0.0;
        int stock = 0;
        try {
            precio = Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            System.err.println("Error de formato en precio: " + txtPrecio.getText());
            
        }
        try {
            stock = Integer.parseInt(txtStock.getText());
        } catch (NumberFormatException e) {
            System.err.println("Error de formato en stock: " + txtStock.getText());
        }


        return new Producto(
                idProducto,
                txtNombre.getText(),
                txtDescripcion.getText(),
                precio,
                stock,
                txtCategoria.getText()
        );
    }

    private void insertarProducto() {
        modeloProducto = cargarModeloProductos();

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_agregarProducto(?,?,?,?,?);");
            enunciado.setString(1, modeloProducto.getNombre());
            enunciado.setString(2, modeloProducto.getDescripcion());
            enunciado.setDouble(3, modeloProducto.getPrecio());
            enunciado.setInt(4, modeloProducto.getStock());
            enunciado.setString(5, modeloProducto.getCategoria());

            int registrosAgregados = enunciado.executeUpdate();
            if (registrosAgregados > 0) {
                System.out.println("Producto agregado correctamente");
                cargarTablasProductos();
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar un producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarProducto() {
        modeloProducto = cargarModeloProductos();

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_actualizarProducto(?,?,?,?,?,?);");
            enunciado.setInt(1, modeloProducto.getIdProducto());
            enunciado.setString(2, modeloProducto.getNombre());
            enunciado.setString(3, modeloProducto.getDescripcion());
            enunciado.setDouble(4, modeloProducto.getPrecio());
            enunciado.setInt(5, modeloProducto.getStock());
            enunciado.setString(6, modeloProducto.getCategoria());
            enunciado.execute();
            cargarTablasProductos();
        } catch (SQLException e) {
            System.out.println("Error al actualizar un producto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarProducto() {
        modeloProducto = tablaProductos.getSelectionModel().getSelectedItem();
        if (modeloProducto != null) { 
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion()
                        .prepareCall("call sp_eliminarProducto(?);");
                enunciado.setInt(1, modeloProducto.getIdProducto());
                enunciado.execute();
                System.out.println("Producto eliminado correctamente");
                cargarTablasProductos(); 
                limpiarCampos(); 
            } catch (SQLException e) {
                System.out.println("Error al eliminar un producto: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No hay producto seleccionado para eliminar.");
        }
    }

    private void limpiarCampos() {
        txtIdProducto.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtCategoria.clear();
    }

    private void actualizarEstadoFormulario(EstadoFormulario estado) {
        estadoActual = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.EDITAR);

        txtNombre.setDisable(!activo);
        txtDescripcion.setDisable(!activo);
        txtPrecio.setDisable(!activo);
        txtStock.setDisable(!activo);
        txtCategoria.setDisable(!activo);

        tablaProductos.setDisable(activo);
        btnBuscar.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnNuevo.setText(activo ? "GUARDAR" : "NUEVO");
        btnEliminar.setText(activo ? "CANCELAR" : "ELIMINAR");
        btnEditar.setDisable(activo);
    }

    @FXML
    private void agregarProducto() {
        switch (estadoActual) {
            case NINGUNO:
                limpiarCampos();
                System.out.println("Voy a crear un registro para Producto");
                actualizarEstadoFormulario(EstadoFormulario.AGREGAR);
                break;
            case AGREGAR:
                insertarProducto();
                System.out.println("Voy a guardar los datos ingresados");
                actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
                break;
            case EDITAR:
                actualizarProducto();
                System.out.println("Voy a guardar edicion indicada");
                actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
                break;
            default:
                break;
        }
    }

    @FXML
    private void editarProducto() {
        if (tablaProductos.getSelectionModel().getSelectedItem() != null) {
            actualizarEstadoFormulario(EstadoFormulario.EDITAR);
            System.out.println("Voy a editar el registro seleccionado");
        } else {
            System.out.println("Debe seleccionar un producto para editar.");
        }
    }

    @FXML
    private void cancelarProducto() {
        if (estadoActual == EstadoFormulario.NINGUNO) {
            eliminarProducto();
            System.out.println("Voy a eliminar registro");
        } else {
            actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
            cargarTablasProductos();
            limpiarCampos(); 
        }
    }

    @FXML
    private void buscarProducto() {
        String nombreABuscar = txtBuscar.getText().toLowerCase();
        ArrayList<Producto> resultadoBusqueda = new ArrayList<>();
        for (Producto p : listaProducto) {
            if (p.getNombre().toLowerCase().contains(nombreABuscar) ||
                p.getDescripcion().toLowerCase().contains(nombreABuscar) ||
                p.getCategoria().toLowerCase().contains(nombreABuscar)) {
                resultadoBusqueda.add(p);
            }
        }
        tablaProductos.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaProductos.getSelectionModel().selectFirst();
            cargarProductoFormulario();
        } else {
            limpiarCampos();
        }
    }

    @FXML
    public void escenaPaginaInicio() {
        principal.escenaPaginaInicio();
    }
}