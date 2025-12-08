package proyectoSeguridad.controlador;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent; // <-- Importación necesaria
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button; // <-- Importación necesaria
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage; // <-- Importación necesaria
import proyectoSeguridad.modelo.dao.UsuarioDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.Curso;

public class FXMLAdministradorConsultarAlumnosMatricula_ResultadoController implements Initializable {

    @FXML private Label lbNombreAlumno;
    @FXML private Label lbMatricula;

    @FXML private TableView<Curso> tvCursos;
    @FXML private TableColumn<Curso, String> colMateria;
    @FXML private TableColumn<Curso, String> colClave;
    
    // Suponiendo que agregas este botón a tu FXML para cerrar la ventana
    @FXML private Button btnCerrar; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    private void configurarTabla() {
        colMateria.setCellValueFactory(new PropertyValueFactory<>("nombreMateria"));
        colClave.setCellValueFactory(new PropertyValueFactory<>("claveCurso"));
    }

    /**
     * Carga la información del alumno y sus cursos en la interfaz.
     * @param alumno El objeto Alumno encontrado.
     * @param cursos La lista de cursos del alumno.
     */
    public void cargarInformacionAlumno(Alumno alumno, List<Curso> cursos) {
        if (alumno == null) {
            lbNombreAlumno.setText("Alumno no encontrado");
            lbMatricula.setText("-");
            return;
        }
        
        // Se asume que UsuarioDAO.obtenerNombreCompletoPorId retorna un String
        String nombreCompleto = UsuarioDAO.obtenerNombreCompletoPorId(alumno.getIdUsuario());

        lbNombreAlumno.setText(nombreCompleto != null ? nombreCompleto : "Nombre no disponible");
        lbMatricula.setText(alumno.getMatricula());

        if (cursos != null) {
            tvCursos.setItems(FXCollections.observableArrayList(cursos));
        } else {
            tvCursos.setPlaceholder(new Label("El alumno no está inscrito en cursos."));
        }
    }
    
    // --- Lógica de Cierre de Ventana (Regreso) ---

    /**
     * Cierra la ventana actual para regresar a la ventana anterior (Consulta por Matrícula),
     * la cual sigue abierta.
     * (Este método debe estar enlazado a un botón de "Cerrar", "Aceptar" o similar en el FXML).
     * @param event 
     */
    @FXML
    private void clicBotonCerrar(ActionEvent event) {
        // Obtiene la Stage actual (ventana de resultados)
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        // Cierra la ventana
        stageActual.close();
    }
}