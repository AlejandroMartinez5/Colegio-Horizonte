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

/**
 * FXML Controller class
 *
 * @author wilma
 */
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
        // Inicialización normal
    }    

    // --- Métodos de Navegación ---
    
    /**
     * Cierra la ventana actual y abre la ventana de Inicio de Sesión.
     * @param event 
     */
    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        try {
            // 1. Cargar el FXML de Inicio de Sesión
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/proyectoSeguridad/vista/FXMLInicioSesion.fxml"
            ));
            Parent root = loader.load();
            
            // 2. Abrir la nueva Stage
            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Inicio de Sesión");
            stageNueva.show(); 
            
            // 3. Cerrar la Stage actual
            Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
            stageActual.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            // Utilidad.mostrarAlertaSimple si existe, sino, se usa Alert de JavaFX
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Navegación");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la pantalla de Inicio de Sesión.");
            alert.showAndWait();
        }
    }


    /**
     * Abre la ventana de consulta por Matrícula en una nueva Stage (se mantiene la actual).
     * @param event 
     */
    @FXML
    private void clicBotonConsultarAlumnosMatricula(ActionEvent event) {
        try {
            // 1. Cargar el FXML de la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorConsultarAlumnosMatricula.fxml"
            ));
            Parent root = loader.load();

            // 2. Crear una nueva Stage para mostrar la ventana
            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Consulta por Matrícula");
            
            // 3. Mostrar la nueva ventana. La ventana actual NO se cierra.
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

    /**
     * Abre la ventana de consulta por Curso en una nueva Stage (se mantiene la actual).
     * @param event 
     */
    @FXML
    private void clicBotonConsultarAlumnosCurso(ActionEvent event) {
        try {
            // 1. Cargar el FXML de la nueva vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorConsultarAlumnosCurso.fxml"
            ));
            Parent root = loader.load();

            // 2. Crear una nueva Stage para mostrar la ventana
            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Consulta por Curso");
            
            // 3. Mostrar la nueva ventana. La ventana actual NO se cierra.
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