package org.tiendaexito.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.tiendaexito.system.Main;

import org.tiendaexito.database.Conexion;
import java.sql.CallableStatement;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.sql.ResultSet;
import javafx.scene.input.KeyCode; 

/**
 *
 * @author Lendrock
 */
public class registerController implements Initializable {

    private Main principal;

    @FXML
    private TextField txtUsuario, txtCorreo, txtContrasenaVisible, txtContrasenaVisible1;
    @FXML
    private PasswordField pfContrasena, pfConfirmarContrasena;
    @FXML
    private Button btnMostrarContrasena, btnMostrarContrasena1;

    @FXML
    private Button btnRegistrarme; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtContrasenaVisible.setVisible(false);
        txtContrasenaVisible1.setVisible(false);

        btnMostrarContrasena.setOnMousePressed(event -> {
            txtContrasenaVisible.setText(pfContrasena.getText());
            pfContrasena.setVisible(false);
            txtContrasenaVisible.setVisible(true);
            txtContrasenaVisible.requestFocus();
            txtContrasenaVisible.positionCaret(txtContrasenaVisible.getText().length());
        });

        btnMostrarContrasena.setOnMouseReleased(event -> {
            pfContrasena.setText(txtContrasenaVisible.getText());
            pfContrasena.setVisible(true);
            txtContrasenaVisible.setVisible(false);
            pfContrasena.requestFocus();
            pfContrasena.positionCaret(pfContrasena.getText().length());
        });

        txtContrasenaVisible.textProperty().addListener((obs, oldVal, newVal) -> {
            if (txtContrasenaVisible.isVisible()) {
                pfContrasena.setText(newVal);
            }
        });

        btnMostrarContrasena1.setOnMousePressed(event -> {
            txtContrasenaVisible1.setText(pfConfirmarContrasena.getText());
            pfConfirmarContrasena.setVisible(false);
            txtContrasenaVisible1.setVisible(true);
            txtContrasenaVisible1.requestFocus();
            txtContrasenaVisible1.positionCaret(txtContrasenaVisible1.getText().length());
        });

        btnMostrarContrasena1.setOnMouseReleased(event -> {
            pfConfirmarContrasena.setText(txtContrasenaVisible1.getText());
            pfConfirmarContrasena.setVisible(true);
            txtContrasenaVisible1.setVisible(false);
            pfConfirmarContrasena.requestFocus();
            pfConfirmarContrasena.positionCaret(pfConfirmarContrasena.getText().length());
        });

        txtContrasenaVisible1.textProperty().addListener((obs, oldVal, newVal) -> {
            if (txtContrasenaVisible1.isVisible()) {
                pfConfirmarContrasena.setText(newVal);
            }
        });

        txtUsuario.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                txtCorreo.requestFocus();
                event.consume();
            }
        });

        txtCorreo.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                pfContrasena.requestFocus();
                event.consume();
            }
        });

        pfContrasena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                pfConfirmarContrasena.requestFocus();
                event.consume();
            }
        });

        pfConfirmarContrasena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnRegistrarme.fire(); 
                event.consume();
            }
        });
        
        txtContrasenaVisible.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                pfConfirmarContrasena.requestFocus(); 
                event.consume();
            }
        });
        
        txtContrasenaVisible1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnRegistrarme.fire();
                event.consume();
            }
        });
    }

    @FXML
    private void agregarUsuario() {
        String usuario = txtUsuario.getText();
        String correo = txtCorreo.getText();
        String contrasena = pfContrasena.getText(); 
        String confirmarContrasena = pfConfirmarContrasena.getText();
        String role = "user";

        if (usuario.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Campos Vacíos");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, completa todos los campos para registrarte.");
            alerta.getDialogPane().getStylesheets().add(
                getClass().getResource("/view/alertsStyle.css").toExternalForm()); 
            alerta.getDialogPane().getStyleClass().add("alert-warning");
            alerta.showAndWait();
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error de Contraseña");
            alerta.setHeaderText(null);
            alerta.setContentText("Las contraseñas no coinciden. Por favor, inténtalo de nuevo.");
            alerta.getDialogPane().getStylesheets().add(
                getClass().getResource("/view/alertsStyle.css").toExternalForm()); 
            alerta.getDialogPane().getStyleClass().add("alert-error");
            alerta.showAndWait();
            return;
        }

        if (existeEnBaseDeDatos("usuario", usuario)) {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Usuario Ya Registrado");
            alerta.setHeaderText(null);
            alerta.setContentText("El nombre de usuario '" + usuario + "' ya está en uso. Por favor, elige otro.");
            alerta.getDialogPane().getStylesheets().add(
                getClass().getResource("/view/alertsStyle.css").toExternalForm()); 
            alerta.getDialogPane().getStyleClass().add("alert-error");
            alerta.showAndWait();
            return;
        }

        if (existeEnBaseDeDatos("correo", correo)) {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Correo Ya Registrado");
            alerta.setHeaderText(null);
            alerta.setContentText("La dirección de correo electrónico '" + correo + "' ya está registrada.");
            alerta.getDialogPane().getStylesheets().add(
                getClass().getResource("/view/alertsStyle.css").toExternalForm()); 
            alerta.getDialogPane().getStyleClass().add("alert-error");
            alerta.showAndWait();
            return;
        }

        try {
            CallableStatement enunciado = Conexion.getInstancia().getConexion()
                    .prepareCall("call sp_agregarUsuario(?, ?, ?, ?);");

            enunciado.setString(1, usuario);
            enunciado.setString(2, correo);
            enunciado.setString(3, contrasena);
            enunciado.setString(4, role);

            int registrosAgregados = enunciado.executeUpdate();

            if (registrosAgregados > 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Registro Exitoso");
                alert.setHeaderText(null);
                alert.setContentText("Usuario '" + usuario + "' registrado correctamente");
                alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/view/alertsStyle.css").toExternalForm()); 
                alert.getDialogPane().getStyleClass().add("alert-information");
                alert.showAndWait();
                limpiarCampos();
                principal.escenaLogin();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error de Registro");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo registrar el usuario");
                alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/view/alertsStyle.css").toExternalForm()); 
                alert.getDialogPane().getStyleClass().add("alert-error");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            System.err.println("Error al intentar registrar el usuario: " + e.getMessage());
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Base de Datos");
            alert.setHeaderText(null);
            alert.setContentText("Ocurrió un error al registrar el usuario." + e.getMessage());
            alert.getDialogPane().getStylesheets().add(
                    getClass().getResource("/view/alertsStyle.css").toExternalForm()); 
            alert.getDialogPane().getStyleClass().add("alert-error");
            alert.showAndWait();
        }
    }

    private boolean existeEnBaseDeDatos(String campo, String valor) {
        String sql = "select count(*) from users where " + campo + " = ?";
        try (CallableStatement callableStatement = Conexion.getInstancia().getConexion().prepareCall(sql)) {
            callableStatement.setString(1, valor);
            try (ResultSet rs = callableStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia en la base de datos: " + e.getMessage());
            return true;
        }
        return false;
    }

    private void limpiarCampos() {
        txtUsuario.clear();
        txtCorreo.clear();
        pfContrasena.clear();
        pfConfirmarContrasena.clear();
        txtContrasenaVisible.clear();
        txtContrasenaVisible1.clear();
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public void escenaLogin() {
        principal.escenaLogin();
    }
}