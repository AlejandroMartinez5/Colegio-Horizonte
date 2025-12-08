package proyectoSeguridad.controlador;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent; // <-- Importación necesaria para el método cerrar
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button; // <-- Importación necesaria para el método cerrar
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage; // <-- Importación necesaria para cerrar la ventana
import proyectoSeguridad.modelo.dao.UsuarioDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.Curso;

public class FXMLAdministradorConsultarAlumnosCurso_ResultadoController implements Initializable {

    @FXML private TableView<Alumno> tvAlumnos;
    @FXML private TableColumn<Alumno, String> colMatricula;
    @FXML private TableColumn<Alumno, String> colNombreCompleto;
    @FXML private Label lbNombreCurso;
    
    // Suponiendo que agregas este botón a tu FXML para cerrar la ventana de resultados
    @FXML private Button btnCerrar; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Columna Matrícula
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        // Columna Nombre completo (obtenido de UsuarioDAO)
        colNombreCompleto.setCellValueFactory(cellData -> {
            Alumno alumno = cellData.getValue();
            // Se asume que UsuarioDAO.obtenerNombreCompletoPorId retorna un String
            String nombreCompleto = UsuarioDAO.obtenerNombreCompletoPorId(alumno.getIdUsuario());
            return new javafx.beans.property.SimpleStringProperty(nombreCompleto);
        });
    }

    /**
     * Método para recibir y cargar los datos del curso y la lista de alumnos.
     * @param curso Objeto Curso.
     * @param alumnos Lista de alumnos.
     */
    public void cargarInformacionCurso(Curso curso, List<Alumno> alumnos) {
        lbNombreCurso.setText(curso.getNombreMateria());
        if (alumnos != null && !alumnos.isEmpty()) {
            tvAlumnos.setItems(FXCollections.observableArrayList(alumnos));
        } else {
            tvAlumnos.setPlaceholder(new Label("No se encontraron alumnos para este curso."));
        }
    }
    
    // --- Lógica de Cierre de Ventana (Regreso) ---

    /**
     * Cierra la ventana actual para regresar a la ventana anterior (Consulta de Curso),
     * la cual sigue abierta.
     * (Asume que este método está enlazado al botón btnCerrar o similar en el FXML).
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