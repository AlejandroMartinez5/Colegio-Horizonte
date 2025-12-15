package proyectoSeguridad.controlador;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.dao.UsuarioDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.Curso;

public class FXMLAdministradorConsultarAlumnosMatricula_ResultadoController implements Initializable {

    @FXML private Label lbNombreAlumno;
    @FXML private Label lbMatricula;

    @FXML private TableView<Curso> tvCursos;
    @FXML private TableColumn<Curso, String> colMateria;
    @FXML private TableColumn<Curso, String> colClave;
    
    @FXML private Button btnCerrar; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    private void configurarTabla() {
        colMateria.setCellValueFactory(new PropertyValueFactory<>("nombreMateria"));
        colClave.setCellValueFactory(new PropertyValueFactory<>("claveCurso"));
    }

    
    public void cargarInformacionAlumno(Alumno alumno, List<Curso> cursos) {
        if (alumno == null) {
            lbNombreAlumno.setText("Alumno no encontrado");
            lbMatricula.setText("-");
            return;
        }
        
        String nombreCompleto = UsuarioDAO.obtenerNombreCompletoPorId(alumno.getIdUsuario());

        lbNombreAlumno.setText(nombreCompleto != null ? nombreCompleto : "Nombre no disponible");
        lbMatricula.setText(alumno.getMatricula());

        if (cursos != null) {
            tvCursos.setItems(FXCollections.observableArrayList(cursos));
        } else {
            tvCursos.setPlaceholder(new Label("El alumno no está inscrito en cursos."));
        }
    }
    
    
    @FXML
    private void clicBotonCerrar(ActionEvent event) {
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stageActual.close();
    }
}