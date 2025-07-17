package org.tiendaexito.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;

import org.tiendaexito.system.Main;
import org.tiendaexito.database.Conexion;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Lendrock
 */
public class loginController implements Initializable {

    private Main principal;

    @FXML
    private TextField txtUsuario, txtContrasenaVisible;
    @FXML
    private PasswordField pfContrasena;
    @FXML
    private Button btnMostrarContrasena, btnIniciarSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtContrasenaVisible.setVisible(false);

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

        txtUsuario.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (txtContrasenaVisible.isVisible()) {
                    txtContrasenaVisible.requestFocus();
                } else {
                    pfContrasena.requestFocus();
                }
                event.consume();
            }
        });

        pfContrasena.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnIniciarSesion.fire();
                event.consume();
            }
        });

        txtContrasenaVisible.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                btnIniciarSesion.fire();
                event.consume();
            }
        });
    }

    @FXML
    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String contrasena = pfContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Alert alerta = new Alert(AlertType.WARNING);
            alerta.setTitle("Campos Vacíos");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, ingresa tu usuario y contraseña.");
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/view/alertsStyle.css").toExternalForm());
            alerta.getDialogPane().getStyleClass().add("alert-warning");
            alerta.showAndWait();
            return;
        }

        try {
            String sql = "{call sp_verificarCredenciales(?, ?)}";
            CallableStatement enunciado = Conexion.getInstancia().getConexion().prepareCall(sql);

            enunciado.setString(1, usuario);
            enunciado.setString(2, contrasena);

            ResultSet rs = enunciado.executeQuery();

            if (rs.next()) {
                String rol = rs.getString("role");

                if ("admin".equalsIgnoreCase(rol)) {
                    principal.escenaPaginaInicio();
                    limpiarCampos();
                } else if ("user".equalsIgnoreCase(rol)) {
                    principal.escenaInicioUser();
                    limpiarCampos();
                } else {
                    Alert alerta = new Alert(AlertType.ERROR);
                    alerta.setTitle("Error de Acceso");
                    alerta.setHeaderText(null);
                    alerta.setContentText("Tu rol de usuario no está configurado para acceder al sistema.");
                    alerta.getDialogPane().getStylesheets().add(getClass().getResource("/view/alertsStyle.css").toExternalForm());
                    alerta.getDialogPane().getStyleClass().add("alert-error");
                    alerta.showAndWait();
                }
            } else {
                Alert alerta = new Alert(AlertType.ERROR);
                alerta.setTitle("Error de Inicio de Sesión");
                alerta.setHeaderText(null);
                alerta.setContentText("Usuario o contraseña incorrectos.");
                alerta.getDialogPane().getStylesheets().add(getClass().getResource("/view/alertsStyle.css").toExternalForm());
                alerta.getDialogPane().getStyleClass().add("alert-error");
                alerta.showAndWait();
            }
            rs.close();
            enunciado.close();

        } catch (SQLException e) {
            System.err.println("Error de base de datos al intentar iniciar sesión: " + e.getMessage());
            e.printStackTrace();
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error de Conexión");
            alerta.setHeaderText(null);
            alerta.setContentText("Ocurrió un error al intentar conectar con la base de datos. Inténtalo de nuevo más tarde.");
            alerta.getDialogPane().getStylesheets().add(getClass().getResource("/view/alertsStyle.css").toExternalForm());
            alerta.getDialogPane().getStyleClass().add("alert-error");
            alerta.showAndWait();
        }
    }

    private void limpiarCampos() {
        txtUsuario.clear();
        pfContrasena.clear();
        txtContrasenaVisible.clear();
    }

    public void setPrincipal(Main principal) {
        this.principal = principal;
    }

    public void escenaPaginaInicio() {
        principal.escenaPaginaInicio();
    }

    public void escenaRegister() {
        principal.escenaRegister();
    }
}