package proyectoSeguridad.controlador;

import java.io.IOException; 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent; 
import javafx.scene.Scene; 
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage; 
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdminstradorPantallaDocenteController implements Initializable {

    @FXML
    private Label lbNombre;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Label lbReloj;
    @FXML
    private Button btnConsultarDocentes;
    @FXML
    private Button btnRegistrarDocente;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    // --- Lógica de Navegación Externa (Cerrar Sesión) ---

    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (Utilidad.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Está seguro que desea cerrar la sesión y volver al login?")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLInicioSesion.fxml"));
                Parent root = loader.load();
                
                Stage stageNueva = new Stage();
                stageNueva.setScene(new Scene(root));
                stageNueva.setTitle("Inicio de Sesión");
                stageNueva.show(); 
                
                Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
                stageActual.close();

            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
            }
        }
    }


    @FXML
    private void clicBotonConsultarDocentes(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAdministradorConsultarDocentes.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Consultar Docentes"); 
        stage.setScene(new Scene(root));
        stage.show();
        
    } catch (Exception e) {
        e.printStackTrace();
        Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "Error al cargar la pantalla de consulta de docentes.");
    }
    }

    @FXML
    private void clicBotonRegistrarDocente(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAdministradorRegistrarDocente.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage(); 
        stage.setTitle("Registro de Docente"); 
        stage.setScene(new Scene(root));
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
        Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "Error al cargar la pantalla de registro de docente.");
    }
    
    }

    
}