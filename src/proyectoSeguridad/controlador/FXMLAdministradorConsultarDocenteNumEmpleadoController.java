package proyectoSeguridad.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert; // Importación de Alert
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage; // Importación de Stage
import proyectoSeguridad.modelo.dao.DocenteDAO;
import proyectoSeguridad.modelo.dao.UsuarioDAO;
import proyectoSeguridad.modelo.pojo.Docente;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdministradorConsultarDocenteNumEmpleadoController implements Initializable {

    @FXML
    private TextField tfNumeroEmpleado;
    @FXML
    private Label lbNombre;
    @FXML
    private Label lbApellido;
    @FXML
    private Label lbUsername;
    @FXML
    private Label lbNumeroEmpleado;
    @FXML
    private Button btnBuscar;
    
    // Suponiendo que agregas este botón a tu FXML para volver a la ventana anterior
    @FXML
    private Button btnVolver; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        limpiarLabels();
    }    

    // --- Lógica de Búsqueda ---
    
    @FXML
    private void clicBotonBuscar(ActionEvent event) {
        String numeroEmpleado = tfNumeroEmpleado.getText().trim();

        if (numeroEmpleado.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, 
                    "Campo vacío", "Debe ingresar un número de empleado.");
            return;
        }

        try {
            Docente docente = DocenteDAO.obtenerDocentePorNumeroEmpleado(numeroEmpleado);

            if (docente == null) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, 
                        "No encontrado", "No se encontró un docente con ese número de empleado.");
                limpiarLabels();
                return;
            }

            // Obtener información del usuario
            String nombreCompleto = UsuarioDAO.obtenerNombreCompletoPorId(docente.getIdUsuario());
            String username = UsuarioDAO.obtenerUsernamePorIdUsuario(docente.getIdUsuario());

            // Separar nombre y apellido
            // Nota: Esta separación es básica y asume que el primer espacio separa el nombre y el apellido.
            String[] partesNombre = nombreCompleto != null ? nombreCompleto.split("\\s+", 2) : new String[]{"---", "---"};
            String nombre = partesNombre.length > 0 ? partesNombre[0] : "---";
            String apellido = partesNombre.length > 1 ? partesNombre[1] : "---";

            // Mostrar los datos en los labels
            lbNombre.setText(nombre);
            lbApellido.setText(apellido);
            lbUsername.setText(username != null ? username : "---");
            lbNumeroEmpleado.setText(docente.getNumeroEmpleado());

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
        lbNombre.setText("---");
        lbApellido.setText("---");
        lbUsername.setText("---");
        lbNumeroEmpleado.setText("---");
    }
    
    // --- Lógica de Navegación (Regreso) ---
    
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