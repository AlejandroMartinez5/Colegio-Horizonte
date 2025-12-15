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

public class FXMLAdministradorConsultarAlumnosCurso_ResultadoController implements Initializable {

    @FXML private TableView<Alumno> tvAlumnos;
    @FXML private TableColumn<Alumno, String> colMatricula;
    @FXML private TableColumn<Alumno, String> colNombreCompleto;
    @FXML private Label lbNombreCurso;
    
    @FXML private Button btnCerrar; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        colNombreCompleto.setCellValueFactory(cellData -> {
            Alumno alumno = cellData.getValue();
            String nombreCompleto = UsuarioDAO.obtenerNombreCompletoPorId(alumno.getIdUsuario());
            return new javafx.beans.property.SimpleStringProperty(nombreCompleto);
        });
    }

    
    public void cargarInformacionCurso(Curso curso, List<Alumno> alumnos) {
        lbNombreCurso.setText(curso.getNombreMateria());
        if (alumnos != null && !alumnos.isEmpty()) {
            tvAlumnos.setItems(FXCollections.observableArrayList(alumnos));
        } else {
            tvAlumnos.setPlaceholder(new Label("No se encontraron alumnos para este curso."));
        }
    }
    
    
    @FXML
    private void clicBotonCerrar(ActionEvent event) {
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stageActual.close();
    }
}