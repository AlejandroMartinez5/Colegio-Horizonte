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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class FXMLAdministradorConsultarAlumnosController implements Initializable {

    @FXML
    private Label lbNombre;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Label lbReloj;
    @FXML
    private Button btnConsultarAlumnosMatricula;
    @FXML
    private Button btnConsultarAlumnosCurso;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización
    }    

    
    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/proyectoSeguridad/vista/FXMLInicioSesion.fxml"
            ));
            Parent root = loader.load();
            
            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Inicio de Sesión");
            stageNueva.show(); 
            
            Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
            stageActual.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Navegación");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la pantalla de Inicio de Sesión.");
            alert.showAndWait();
        }
    }


    @FXML
    private void clicBotonConsultarAlumnosMatricula(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorConsultarAlumnosMatricula.fxml"
            ));
            Parent root = loader.load();

            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Consulta por Matrícula");
            
            stageNueva.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Navegación");
            alert.setHeaderText(null);
            alert.setContentText("Error al cargar la ventana de consulta por Matrícula.");
            alert.showAndWait();
        }
    }

    
    @FXML
    private void clicBotonConsultarAlumnosCurso(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorConsultarAlumnosCurso.fxml"
            ));
            Parent root = loader.load();

            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Consulta por Curso");
            
            stageNueva.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Navegación");
            alert.setHeaderText(null);
            alert.setContentText("Error al cargar la ventana de consulta por Curso.");
            alert.showAndWait();
        }
    }
}