package org.tiendaexito.system;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tiendaexito.controller.bajosController;
import org.tiendaexito.controller.bateriasController;
import org.tiendaexito.controller.comprasController;
import org.tiendaexito.controller.detalleCompraController;
import org.tiendaexito.controller.guitarrasController;
import org.tiendaexito.controller.inicioController; 
import org.tiendaexito.controller.inicioUserController;
import org.tiendaexito.controller.loginController;
import org.tiendaexito.controller.productosController;
import org.tiendaexito.controller.registerController;
import org.tiendaexito.controller.tecladosController;

/**
 *
 * @author Lendrock
 */
public class Main extends Application {
    private static String URL = "/view/";
    private Stage escenarioPrincipal;
    private Scene escena;

    @Override
    public void start(Stage stage) throws Exception {
        this.escenarioPrincipal = stage;

        escenaLogin(); 
        stage.setTitle("Tienda El Exito"); 
        stage.show();
    }

    public Initializable cambiarEscena(String fxml, double ancho, double alto) throws IOException {
        Initializable interfazCargada = null;

        FXMLLoader cargadorFXML = new FXMLLoader();

        InputStream archivoFXML = Main.class.getResourceAsStream(URL + fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Main.class.getResource(URL + fxml));

        escena = new Scene(cargadorFXML.load(archivoFXML), ancho, alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene(); 

        interfazCargada = cargadorFXML.getController();

        return interfazCargada;
    }

    public void escenaPaginaInicio() {
        try {
            inicioController pic = (inicioController) cambiarEscena("InicioView.fxml", 885, 640);
            pic.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a InicioView");
            ex.printStackTrace();
        }
    }
    public void escenaCompras() {
        try {
            comprasController cm = (comprasController) cambiarEscena("ComprasView.fxml", 950, 500);
            cm.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a comprasView");
            ex.printStackTrace();
        }
    }//
    public void escenaProductos() {
        try {
            productosController pc = (productosController) cambiarEscena("ProductosView.fxml", 950, 500);
            pc.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a productosView");
            ex.printStackTrace();
        }
    }
    public void escenaDetalleCompra() {
        try {
            detalleCompraController dcc = (detalleCompraController) cambiarEscena("detalleCompraView.fxml", 950, 500);
            dcc.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a detalleCompraView");
            ex.printStackTrace();
        }
    }
    public void escenaLogin() {
        try {
            loginController lc = (loginController) cambiarEscena("LoginView.fxml", 606, 600);
            lc.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a LoginView");
            ex.printStackTrace();
        }
    }
    public void escenaRegister() {
        try {
            registerController rc = (registerController) cambiarEscena("RegistroView.fxml", 606, 600);
            rc.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a RegistroView");
            ex.printStackTrace();
        }
    }
    public void escenaInicioUser() {
        try {
            inicioUserController iuc = (inicioUserController) cambiarEscena("InicioUserView.fxml", 885, 640);
            iuc.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a InicioUserView");
            ex.printStackTrace();
        }
    }
    public void escenaGuitarras() {
        try {
            guitarrasController gc = (guitarrasController) cambiarEscena("GuitarrasView.fxml", 898.6, 643);
            gc.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a GuitarrasView");
            ex.printStackTrace();
        }
    }
    public void escenaTeclados() {
        try {
            tecladosController tc = (tecladosController) cambiarEscena("TecladosView.fxml", 898.6, 643);
            tc.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a TecladosView");
            ex.printStackTrace();
        }
    }
    public void escenaBajos() {
        try {
            bajosController bc = (bajosController) cambiarEscena("BajosView.fxml", 898.6, 643);
            bc.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a BajosView");
            ex.printStackTrace();
        }
    }
    public void escenaBaterias() {
        try {
            bateriasController bac = (bateriasController) cambiarEscena("BateriasView.fxml", 898.6, 643);
            bac.setPrincipal(this);
        } catch (IOException ex) {
            System.out.println("Error al cambio a BateriasView");
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}