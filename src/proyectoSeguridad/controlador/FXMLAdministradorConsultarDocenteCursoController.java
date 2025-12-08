/*
 * Alejandro Martinez Ramirez
 * 28-05-2025
 */
package proyectoSeguridad.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert; // Importación de Alert
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage; // Importación de Stage
import proyectoSeguridad.modelo.dao.CursoDAO;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdministradorConsultarDocenteCursoController implements Initializable {

    @FXML
    private TextField tfClaveCurso;
    @FXML
    private Label lbNombreMateria;
    @FXML
    private Label lbClaveCurso;
    @FXML
    private Label lbNombreDocente;
    @FXML
    private Label lbUsernameDocente;
    @FXML
    private Button btnBuscar;
    
    // Suponiendo que agregas este botón a tu FXML para volver a la ventana anterior
    @FXML
    private Button btnVolver; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limpiarLabels();
    }    

    @FXML
    private void clicBotonBuscar(ActionEvent event) {
        String claveCurso = tfClaveCurso.getText().trim();

        if (claveCurso.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING,
                    "Campo vacío", "Debe ingresar la clave del curso.");
            return;
        }

        try {
            Map<String, String> datos = CursoDAO.obtenerCursoYDocentePorClave(claveCurso);

            if (datos == null || datos.isEmpty()) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION,
                        "No encontrado", "No se encontró un curso con esa clave o no tiene docente asignado.");
                limpiarLabels();
                return;
            }

            // Mostrar los datos en los labels
            // Usamos .getOrDefault para manejar posibles valores nulos o claves faltantes
            lbNombreMateria.setText(datos.getOrDefault("nombreMateria", "N/A"));
            lbClaveCurso.setText(datos.getOrDefault("claveCurso", "N/A"));
            lbNombreDocente.setText(datos.getOrDefault("nombreDocente", "N/A"));
            lbUsernameDocente.setText(datos.getOrDefault("usernameDocente", "N/A"));

        } catch (SQLException e) {
            e.printStackTrace();
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR,
                    "Error en la base de datos", "Ocurrió un error al consultar: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR,
                    "Error inesperado", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    private void limpiarLabels() {
        lbNombreMateria.setText("---");
        lbClaveCurso.setText("---");
        lbNombreDocente.setText("---");
        lbUsernameDocente.setText("---");
    }
    
    /**
     * Cierra la ventana actual para regresar a la ventana que la invocó.
     * Este método se debe usar en un botón de "Regresar" o "Volver" en el FXML.
     * @param event 
     */
    @FXML
    private void clicBotonVolver(ActionEvent event) {
        // Obtiene la Stage (ventana) actual a partir del botón que disparó el evento
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        // Cierra la ventana, regresando a la anterior que sigue abierta.
        stageActual.close();
    }
}