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
import org.tiendaexito.model.Compra; 
import org.tiendaexito.model.User;
import org.tiendaexito.system.Main;
import org.tiendaexito.database.Conexion;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class for Compra
 *
 * @author Lendrock
 */
public class comprasController implements Initializable {

    private Main principal;
    private ObservableList<Compra> listaCompra;
    private ObservableList<User> listaUsuarios; 
    private Compra modeloCompra;

    private enum EstadoFormulario {
        AGREGAR, EDITAR, ELIMINAR, NINGUNO
    }

    EstadoFormulario estadoActual = EstadoFormulario.NINGUNO;

    @FXML private TableView<Compra> tablaCompras;
    @FXML private TextField txtIdCompra, txtTotal, txtMetodoPago, txtEstado, txtBuscar; 
    @FXML private DatePicker dpFecha; 
    @FXML private ComboBox cbIdUser; 
    @FXML private TableColumn colIdCompra, colFecha, colIdUser, colTotal, 
            colMetodoPago, colEstado;
    @FXML private Button btnEliminar, btnEditar, btnNuevo, btnBuscar;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumna();
        cargarListaUsuarios(); 
        cargarTablasCompras();
        tablaCompras.setOnMouseClicked(eh -> cargarCompraFormulario());
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    private void configurarColumna() {
        colIdCompra.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colIdUser.setCellValueFactory(new PropertyValueFactory<>("idUser")); 
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private ArrayList<User> listarUsuarios() {
        ArrayList<User> users = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_listarUsuarios();"); 
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                users.add(new User( 
                        resultado.getInt("idUser"),
                        resultado.getString("usuario"),
                        resultado.getString("correo"),
                        null, 
                        resultado.getString("role")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    private void cargarListaUsuarios() {
        listaUsuarios = FXCollections.observableArrayList(listarUsuarios());
        cbIdUser.setItems(listaUsuarios);
    }


    private ArrayList<Compra> listarCompras() {
        ArrayList<Compra> compras = new ArrayList<>();
        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall("call sp_listarCompras();");
            ResultSet resultado = enunciado.executeQuery();
            while (resultado.next()) {
                compras.add(new Compra(
                        resultado.getInt("idCompra"),
                        resultado.getTimestamp("fecha").toLocalDateTime(),
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

    private void cargarCompraFormulario() {
        Compra compra = tablaCompras.getSelectionModel().getSelectedItem();
        if (compra != null) {
            txtIdCompra.setText(String.valueOf(compra.getIdCompra()));
            dpFecha.setValue(compra.getFecha().toLocalDate());


            User selectedUser = null;
            for (User user : listaUsuarios) {
                if (user.getIdUser() == compra.getIdUser()) { 
                    selectedUser = user;
                    break;
                }
            }
            cbIdUser.getSelectionModel().select(selectedUser);

            txtTotal.setText(String.valueOf(compra.getTotal()));
            txtMetodoPago.setText(compra.getMetodoPago());
            txtEstado.setText(compra.getEstado());
        } else {
            limpiarCampos();
        }
    }

    private void cargarTablasCompras() {
        listaCompra = FXCollections.observableArrayList(listarCompras());
        tablaCompras.setItems(listaCompra);
        if (!listaCompra.isEmpty()) {
            tablaCompras.getSelectionModel().selectFirst();
            cargarCompraFormulario();
        } else {
            limpiarCampos();
        }
    }

    private Compra cargarModeloCompras() {
        int idCompra = txtIdCompra.getText().isEmpty() ? 0 : Integer.parseInt(txtIdCompra.getText());
        int idUser = 0;
        double total = 0.0;
        LocalDateTime fecha = null;

        if (cbIdUser.getSelectionModel().getSelectedItem() != null) {
            idUser = ((User) cbIdUser.getSelectionModel().getSelectedItem()).getIdUser();
        } else {
            System.err.println("Error: No se ha seleccionado un usuario en el ComboBox.");
        }

        try {
            total = Double.parseDouble(txtTotal.getText());
        } catch (NumberFormatException e) {
            System.err.println("Error de formato en total: " + txtTotal.getText());
        }
        if (dpFecha.getValue() != null) {
            fecha = dpFecha.getValue().atStartOfDay();
        } else {
            System.err.println("Error: No se ha seleccionado una fecha.");
        }

        return new Compra(
                idCompra,
                fecha,
                idUser,
                total,
                txtMetodoPago.getText(),
                txtEstado.getText()
        );
    }

    private void insertarCompra() {
        modeloCompra = cargarModeloCompras();

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_agregarCompra(?,?,?,?,?);");
            enunciado.setTimestamp(1, Timestamp.valueOf(modeloCompra.getFecha()));
            enunciado.setInt(2, modeloCompra.getIdUser()); 
            enunciado.setDouble(3, modeloCompra.getTotal());
            enunciado.setString(4, modeloCompra.getMetodoPago());
            enunciado.setString(5, modeloCompra.getEstado());

            int registrosAgregados = enunciado.executeUpdate();
            if (registrosAgregados > 0) {
                System.out.println("Compra agregada correctamente");
                cargarTablasCompras();
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar una compra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarCompra() {
        modeloCompra = cargarModeloCompras();

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_actualizarCompra(?,?,?,?,?,?);");
            enunciado.setInt(1, modeloCompra.getIdCompra());
            enunciado.setTimestamp(2, Timestamp.valueOf(modeloCompra.getFecha()));
            enunciado.setInt(3, modeloCompra.getIdUser()); // Actualizado a getIdUser
            enunciado.setDouble(4, modeloCompra.getTotal());
            enunciado.setString(5, modeloCompra.getMetodoPago());
            enunciado.setString(6, modeloCompra.getEstado());
            enunciado.execute();
            cargarTablasCompras();
        } catch (SQLException e) {
            System.out.println("Error al actualizar una compra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void eliminarCompra() {
        modeloCompra = tablaCompras.getSelectionModel().getSelectedItem();
        if (modeloCompra != null) {
            try {
                CallableStatement enunciado = Conexion.getInstancia().getConexion()
                        .prepareCall("call sp_eliminarCompra(?);");
                enunciado.setInt(1, modeloCompra.getIdCompra());
                enunciado.execute();
                System.out.println("Compra eliminada correctamente");
                cargarTablasCompras();
                limpiarCampos();
            } catch (SQLException e) {
                System.out.println("Error al eliminar una compra: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No hay compra seleccionada para eliminar.");
        }
    }

    private void limpiarCampos() {
        txtIdCompra.clear();
        dpFecha.setValue(null);
        cbIdUser.getSelectionModel().clearSelection(); 
        txtTotal.clear();
        txtMetodoPago.clear();
        txtEstado.clear();
    }

    private void actualizarEstadoFormulario(EstadoFormulario estado) {
        estadoActual = estado;
        boolean activo = (estado == EstadoFormulario.AGREGAR || estado == EstadoFormulario.EDITAR);

        dpFecha.setDisable(!activo);
        cbIdUser.setDisable(!activo); 
        txtTotal.setDisable(!activo);
        txtMetodoPago.setDisable(!activo);
        txtEstado.setDisable(!activo);

        tablaCompras.setDisable(activo);
        btnBuscar.setDisable(activo);
        txtBuscar.setDisable(activo);

        btnNuevo.setText(activo ? "GUARDAR" : "NUEVO");
        btnEliminar.setText(activo ? "CANCELAR" : "ELIMINAR");
        btnEditar.setDisable(activo);
    }

    @FXML
    private void agregarCompra() {
        switch (estadoActual) {
            case NINGUNO:
                limpiarCampos();
                System.out.println("Voy a crear un registro para Compra");
                actualizarEstadoFormulario(EstadoFormulario.AGREGAR);
                break;
            case AGREGAR:
                insertarCompra();
                System.out.println("Voy a guardar los datos ingresados");
                actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
                break;
            case EDITAR:
                actualizarCompra();
                System.out.println("Voy a guardar edición indicada");
                actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
                break;
            default:
                break;
        }
    }

    @FXML
    private void editarCompra() {
        if (tablaCompras.getSelectionModel().getSelectedItem() != null) {
            actualizarEstadoFormulario(EstadoFormulario.EDITAR);
            System.out.println("Voy a editar el registro seleccionado");
        } else {
            System.out.println("Debe seleccionar una compra para editar.");
        }
    }

    @FXML
    private void cancelarCompra() {
        if (estadoActual == EstadoFormulario.NINGUNO) {
            eliminarCompra();
            System.out.println("Voy a eliminar registro");
        } else {
            actualizarEstadoFormulario(EstadoFormulario.NINGUNO);
            cargarTablasCompras();
            limpiarCampos();
        }
    }

    @FXML
    private void buscarCompra() {
        String textoBusqueda = txtBuscar.getText().toLowerCase();
        ArrayList<Compra> resultadoBusqueda = new ArrayList<>();
        for (Compra c : listaCompra) {
            if (String.valueOf(c.getIdCompra()).contains(textoBusqueda) ||
                String.valueOf(c.getIdUser()).contains(textoBusqueda) || 
                c.getMetodoPago().toLowerCase().contains(textoBusqueda) ||
                c.getEstado().toLowerCase().contains(textoBusqueda)) {
                resultadoBusqueda.add(c);
            }
        }
        tablaCompras.setItems(FXCollections.observableArrayList(resultadoBusqueda));
        if (!resultadoBusqueda.isEmpty()) {
            tablaCompras.getSelectionModel().selectFirst();
            cargarCompraFormulario();
        } else {
            limpiarCampos();
        }
    }

    @FXML
    public void escenaPaginaInicio() {
        principal.escenaPaginaInicio();
    }
}
