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
   
    private int idDocente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utilidad.mostrarHora(lbReloj);
    }    
    
    public void setDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clicBotonConsultarAlumnos(ActionEvent event) {
        if (idDocente > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocenteConsultarAlumnos.fxml"));
                Parent root = loader.load();

                FXMLDocenteConsultarAlumnosController controlador = loader.getController();
                controlador.setDocente(this.idDocente);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Listado de Alumnos por Grupo");
                
                stage.show(); 

            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de consulta de alumnos.");
                ex.printStackTrace();
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Error", "No se ha identificado al docente en la sesión.");
        }
    }


    @FXML
    private void clicBotonSubirCalificaciones(ActionEvent event) {
        if (idDocente > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocenteSubirCalificaciones.fxml"));
                Parent root = loader.load();

                FXMLDocenteSubirCalificacionesController controlador = loader.getController();
                controlador.setDocente(this.idDocente);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Registrar Calificaciones");

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