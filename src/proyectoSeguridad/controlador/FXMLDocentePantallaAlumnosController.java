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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLDocentePantallaAlumnosController implements Initializable {

    @FXML
    private Label lbNombre;
    @FXML
    private Button btnRegresar;
    @FXML
    private Button btnConsultarAlumnos;
    @FXML
    private Button btnSubirCalificaciones;
    @FXML
    private Label lbReloj;
    
    // Atributo esencial para las operaciones del docente
    private int idDocente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utilidad.mostrarHora(lbReloj);
    }    
    
    // Método para recibir el ID del docente desde la pantalla principal
    public void setDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    /**
     * REGRESO: Cierra la ventana actual y regresa al menú principal del docente,
     * el cual debe seguir abierto en la Stage anterior. (Lógica correcta)
     * @param event 
     */
    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }

    /**
     * NAVEGACIÓN INTERNA: Abre la ventana de Consulta de Alumnos en una NUEVA Stage,
     * SIN bloquear la ventana actual (no modal).
     * @param event 
     */
    @FXML
    private void clicBotonConsultarAlumnos(ActionEvent event) {
        if (idDocente > 0) {
            try {
                // 1. Cargar el FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocenteConsultarAlumnos.fxml"));
                Parent root = loader.load();

                // 2. Obtener el controlador y pasar el ID
                FXMLDocenteConsultarAlumnosController controlador = loader.getController();
                controlador.setDocente(this.idDocente);

                // 3. Mostrar la ventana en una nueva Stage
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Listado de Alumnos por Grupo");
                
                // *** CORRECCIÓN CLAVE: Usamos show() en lugar de showAndWait() ***
                stage.show(); 

            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de consulta de alumnos.");
                ex.printStackTrace();
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Error", "No se ha identificado al docente en la sesión.");
        }
    }

    /**
     * NAVEGACIÓN INTERNA: Abre la ventana de Subir Calificaciones en una NUEVA Stage,
     * SIN bloquear la ventana actual (no modal).
     * @param event 
     */
    @FXML
    private void clicBotonSubirCalificaciones(ActionEvent event) {
        if (idDocente > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocenteSubirCalificaciones.fxml"));
                Parent root = loader.load();

                // Obtener el controlador y pasar el ID
                FXMLDocenteSubirCalificacionesController controlador = loader.getController();
                controlador.setDocente(this.idDocente);

                // Mostrar la ventana en una nueva Stage
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Registrar Calificaciones");
                
                // *** CORRECCIÓN CLAVE: Usamos show() en lugar de showAndWait() ***
                stage.show(); 

            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudo abrir la ventana.");
                ex.printStackTrace();
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Error", "No se ha identificado al docente.");
        }
    }
}