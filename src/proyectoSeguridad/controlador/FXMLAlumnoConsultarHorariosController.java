package proyectoSeguridad.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import proyectoSeguridad.modelo.dao.HorarioDAO; 
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAlumnoConsultarHorariosController implements Initializable {

    @FXML
    private Button btnRegresar;
    @FXML
    private TableView<Map<String, Object>> tvHorarios;
    @FXML
    private TableColumn<Map<String, Object>, String> colMateria;
    @FXML
    private TableColumn<Map<String, Object>, String> colDia;
    @FXML
    private TableColumn<Map<String, Object>, String> colInicio;
    @FXML
    private TableColumn<Map<String, Object>, String> colFin;
    @FXML
    private TableColumn<Map<String, Object>, String> colDocente;
    @FXML
    private TableColumn<Map<String, Object>, String> colAula; 

    private ObservableList<Map<String, Object>> listaHorarios;
    private int idAlumno;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaHorarios = FXCollections.observableArrayList();
        configurarTabla();
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
        cargarHorarios();
    }

    private void configurarTabla() {
        colMateria.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("materia")));
        colDia.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("dia")));
        colInicio.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("horaInicio")));
        colFin.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("horaFin")));
        colDocente.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("docente")));
        colAula.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("aula"))); 
        
        tvHorarios.setItems(listaHorarios);
        tvHorarios.setPlaceholder(new javafx.scene.control.Label("No tienes cursos asignados ni horarios registrados."));
    }

    private void cargarHorarios() {
        if (idAlumno <= 0) {
            return; 
        }
        
        try {
            List<Map<String, Object>> resultados = HorarioDAO.obtenerHorarioPorAlumno(this.idAlumno);

            if (resultados != null && !resultados.isEmpty()) {
                listaHorarios.addAll(resultados);
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Consulta", "No se pudo cargar tu horario.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }
}