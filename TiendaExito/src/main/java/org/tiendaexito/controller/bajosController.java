
package org.tiendaexito.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.tiendaexito.system.Main;

/**
 *
 * @author Lendrock
 */
public class bajosController implements Initializable{
    
    private Main principal;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void setPrincipal(Main principal) {
        this.principal = principal;
    }
    
    public void escenaPaginaInicioUser(){
        principal.escenaInicioUser();
    }
    
        public void escenaGuitarras(){
        principal.escenaGuitarras();
    }
    
    public void escenaBaterias(){
        principal.escenaBaterias();
    }
    
    public void escenaTeclados(){
        principal.escenaTeclados();
    }
}