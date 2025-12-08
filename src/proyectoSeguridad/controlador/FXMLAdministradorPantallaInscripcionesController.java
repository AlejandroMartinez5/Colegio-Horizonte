package proyectoSeguridad.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // Importación explícita
import javafx.fxml.Initializable;
import javafx.scene.Parent; // Importación explícita
import javafx.scene.Scene; // Importación explícita
import javafx.scene.control.Alert.AlertType; // Necesario para Utilidad.mostrarAlertaSimple
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage; // Importación explícita
import proyectoSeguridad.utilidades.Utilidad; // Asumo que esta clase maneja las alertas

/**
 * FXML Controller class
 *
 * @author wilma
 */
public class FXMLAdministradorPantallaInscripcionesController implements Initializable {

    @FXML
    private Button btnConsultarPendientes;
    @FXML
    private Button btnRegistrarPago;
    @FXML
    private Label lbMensaje;
    @FXML
    private Label lbReloj;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private Label lbNombre;
    @FXML
    private ImageView btn;
    @FXML
    private Button btnConsultarPago;

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
                
                // 3. Obtener la Stage actual (Pantalla Inscripciones) y CERRARLA
                Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
                stageActual.close();

            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
            }
        }
    }

    // --- Lógica de Navegación Interna (Ventanas Apiladas) ---

    @FXML
    private void clicBotonConsultarPendientes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAdministradorInscripcionesPendientes.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Alumnos con inscripciones pendientes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "Error al cargar la pantalla de inscripciones pendientes.");
        }
    }


    @FXML
    private void clicBotonRegistrarPago(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAdministradorRegistrarPago.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registrar Pago"); // Título ajustado para mayor claridad
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "Error al cargar la pantalla de registro de pago.");
        }
    }

    @FXML
    private void clicBotonConsultarPago(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAdministradorConsultarPagos.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Consultar Pagos"); // Título ajustado para mayor claridad
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "Error al cargar la pantalla de consulta de pagos.");
        }
    }
    
}