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
import javafx.scene.control.Alert.AlertType; // Necesario para Utilidad.mostrarAlertaSimple
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import proyectoSeguridad.utilidades.Utilidad; // Asumo que esta clase maneja las alertas

/**
 * FXML Controller class
 *
 * @author wilma
 */
public class FXMLAdministradorPantallaAlumnoController implements Initializable {

    @FXML
    private Label lbNombre;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Label lbReloj;
    @FXML
    private Button btnConsultarAlumnos;
    @FXML
    private Button btnConsultarCalificaciones;
    @FXML
    private Button btnRegistrarAlumno;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    // --- Lógica de Navegación Externa (Cerrar Sesión) ---

    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (Utilidad.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Está seguro que desea cerrar la sesión y volver al login?")) {
            try {
                // 1. Cargar el FXML de Inicio de Sesión
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLInicioSesion.fxml"));
                Parent root = loader.load();
                
                // 2. Abrir la nueva Stage (Login)
                Stage stageNueva = new Stage();
                stageNueva.setScene(new Scene(root));
                stageNueva.setTitle("Inicio de Sesión");
                stageNueva.show(); 
                
                // 3. Obtener la Stage actual (Pantalla Alumno) y CERRARLA
                Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
                stageActual.close();

            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
            }
        }
    }

    // --- Lógica de Navegación Interna (Ventanas Apiladas) ---

    /**
     * CORREGIDO: Abre la ventana de Consulta de Alumnos en una NUEVA Stage,
     * manteniendo la ventana actual abierta.
     * @param event 
     */
    @FXML
    private void clicBotonConsultarAlumnos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorConsultarAlumnos.fxml"
            ));

            Parent root = loader.load();

            // *** CORRECCIÓN: Crear una NUEVA Stage en lugar de reemplazar la Scene actual ***
            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Consultar Alumnos");
            stageNueva.show(); 

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "Error al cargar la pantalla de consulta de alumnos.");
        }
    }

    /**
     * CORREGIDO: Abre la ventana de Consulta de Calificaciones en una NUEVA Stage,
     * manteniendo la ventana actual abierta.
     * @param event 
     */
    @FXML
    private void clicBotonConsultarCalificaciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorConsultarCalificaciones.fxml"
            ));

            Parent root = loader.load();

            // *** CORRECCIÓN: Crear una NUEVA Stage en lugar de reemplazar la Scene actual ***
            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Consultar Calificaciones");
            stageNueva.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "Error al cargar la pantalla de consulta de calificaciones.");
        }
    }

    /**
     * CORREGIDO: Abre la ventana de Registro de Alumno en una NUEVA Stage,
     * manteniendo la ventana actual abierta.
     * @param event 
     */
    @FXML
    private void clicBotonRegistrarAlumno(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorRegistrarAlumno.fxml"
            ));

            Parent root = loader.load();
            
            // *** CORRECCIÓN: Crear una NUEVA Stage en lugar de reemplazar la Scene actual ***
            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Registrar Alumno");
            stageNueva.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "Error al cargar la pantalla de registro de alumnos.");
        }
    }   
}