package proyectoSeguridad.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.dao.CalificacionDAO;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAlumnoConsultarCalificacionesController implements Initializable {

    @FXML
    private Button btnRegresar;
    @FXML
    private TableView<Map<String, Object>> tvCalificaciones;
    @FXML
    private TableColumn<Map<String, Object>, String> colCurso;
    @FXML
    private TableColumn<Map<String, Object>, Double> colPuntaje;
    @FXML
    private TableColumn<Map<String, Object>, String> colDocente;
    @FXML
    private TableColumn<Map<String, Object>, String> colFecha;

    private ObservableList<Map<String, Object>> listaCalificaciones;
    private int idAlumno;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaCalificaciones = FXCollections.observableArrayList();
        configurarTabla();
    }
    
    /**
     * Método llamado desde el menú principal para pasar el ID del alumno.
     * @param idAlumno ID del alumno logueado.
     */
    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
        cargarCalificaciones();
    }

    private void configurarTabla() {
        // Enlace de las columnas con las claves del Mapa devuelto por el DAO
        colCurso.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("cursoCompleto")));
        colDocente.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("docenteCompleto")));
        colFecha.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("fechaRegistro")));
        
        // El puntaje es Double en el DAO, lo envolvemos en SimpleObjectProperty
        colPuntaje.setCellValueFactory(data -> new SimpleObjectProperty<>((Double) data.getValue().get("puntaje")));
        
        tvCalificaciones.setItems(listaCalificaciones);
        tvCalificaciones.setPlaceholder(new javafx.scene.control.Label("Aún no tienes calificaciones registradas."));
    }

    private void cargarCalificaciones() {
        if (idAlumno <= 0) {
            return; // No cargar si el ID no es válido
        }
        
        try {
            List<Map<String, Object>> resultados = CalificacionDAO.obtenerCalificacionesDetalladasPorAlumno(this.idAlumno);

            if (resultados != null && !resultados.isEmpty()) {
                listaCalificaciones.addAll(resultados);
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Consulta", "No se pudieron cargar tus calificaciones.");
            ex.printStackTrace();
        }
    }

    /**
     * CIERRE DE VENTANA: Cierra la Stage actual para regresar a la vista anterior (Menú del Alumno).
     */
    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        // Obtiene la Stage actual a partir del botón
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        
        // Cierra la Stage, manteniendo la Stage anterior abierta.
        stage.close();
    }
}