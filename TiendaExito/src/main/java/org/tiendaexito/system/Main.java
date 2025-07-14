package org.tiendaexito.system; // Changed package to match the image

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tiendaexito.controller.inicioController; 

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

        escenaPaginaInicio(); 
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

    public static void main(String[] args) {
        launch(args);
    }
}