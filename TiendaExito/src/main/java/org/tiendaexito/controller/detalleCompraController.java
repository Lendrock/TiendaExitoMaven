package org.tiendaexito.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.tiendaexito.system.Main;

/**
 *
 * @author Lendrock
 */
public class detalleCompraController implements Initializable{
    
    private Main principal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    public void escenaPaginaInicio() {
        principal.escenaPaginaInicio();
    }
}