package proyectoSeguridad.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.dao.CalificacionDAO;
import proyectoSeguridad.modelo.dao.CursoDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.Calificacion;
import proyectoSeguridad.modelo.pojo.Curso;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLDocenteSubirCalificacionesController implements Initializable {

    @FXML
    private Button btnRegresar;
    @FXML
    private ComboBox<String> cbCursos;
    @FXML
    private ComboBox<String> cbAlumnos;
    @FXML
    private TextField tfPuntaje;
    @FXML
    private Button btnGuardar;
    @FXML
    private Label lbMensaje;

    private int idDocente;
    
    private Map<String, Integer> mapaCursos;
    private Map<String, Integer> mapaAlumnos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapaCursos = new HashMap<>();
        mapaAlumnos = new HashMap<>();
        
        tfPuntaje.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tfPuntaje.setText(oldValue);
            }
        });
    }
    
    public void setDocente(int idDocente) {
        this.idDocente = idDocente;
        cargarCursos();
    }
    
    private void cargarCursos() {
        try {
            List<Curso> cursos = CursoDAO.obtenerCursosPorIdDocente(this.idDocente);
            ObservableList<String> items = FXCollections.observableArrayList();
            
            if (cursos.isEmpty()) {
                cbCursos.setPromptText("No tienes cursos asignados");
            } else {
                for (Curso c : cursos) {
                    String etiqueta = c.getNombreMateria() + " (" + c.getClaveCurso() + ")";
                    items.add(etiqueta);
                    mapaCursos.put(etiqueta, c.getIdCurso());
                }
                cbCursos.setItems(items);
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "Error al cargar cursos.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void seleccionarCurso(ActionEvent event) {
        String seleccion = cbCursos.getSelectionModel().getSelectedItem();
        cbAlumnos.getItems().clear();
        mapaAlumnos.clear();
        cbAlumnos.setDisable(true);
        
        if (seleccion != null) {
            int idCurso = mapaCursos.get(seleccion);
            cargarAlumnos(idCurso);
        }
    }
    
    private void cargarAlumnos(int idCurso) {
        try {
            List<Alumno> alumnos = AlumnoDAO.obtenerAlumnosPorCurso(idCurso);
            ObservableList<String> items = FXCollections.observableArrayList();
            
            if (alumnos.isEmpty()) {
                cbAlumnos.setPromptText("Sin alumnos inscritos");
            } else {
                for (Alumno a : alumnos) {
                    String etiqueta = a.getNombre() + " " + a.getApellido() + " (" + a.getMatricula() + ")";
                    items.add(etiqueta);
                    mapaAlumnos.put(etiqueta, a.getIdAlumno());
                }
                cbAlumnos.setItems(items);
                cbAlumnos.setDisable(false);
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "Error al cargar alumnos.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void clicBotonGuardar(ActionEvent event) {
        lbMensaje.setText("");
        
        // 1. Validaciones
        String cursoSel = cbCursos.getSelectionModel().getSelectedItem();
        String alumnoSel = cbAlumnos.getSelectionModel().getSelectedItem();
        String puntajeStr = tfPuntaje.getText();
        
        if (cursoSel == null || alumnoSel == null || puntajeStr.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Campos Vacíos", "Seleccione curso, alumno e ingrese una calificación.");
            return;
        }
        
        double puntaje;
        try {
            puntaje = Double.parseDouble(puntajeStr);
            if (puntaje < 0 || puntaje > 10) { // Asumiendo escala de 0 a 10
                 Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Rango inválido", "La calificación debe estar entre 0 y 10.");
                 return;
            }
        } catch (NumberFormatException e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Formato inválido", "Ingrese un número válido.");
            return;
        }
        
        int idCurso = mapaCursos.get(cursoSel);
        int idAlumno = mapaAlumnos.get(alumnoSel);

        eliminarCalificacionAnteriorSiExiste(idCurso, idAlumno);
        // ------------------------------------------------------------------
        
        Calificacion nuevaCalificacion = new Calificacion();
        nuevaCalificacion.setIdCurso(idCurso);
        nuevaCalificacion.setIdAlumno(idAlumno);
        nuevaCalificacion.setPuntaje(puntaje);
        nuevaCalificacion.setFechaRegistro(LocalDate.now().toString()); 
        
        ResultadoOperacion resultado = CalificacionDAO.registrarCalificacion(nuevaCalificacion);
        
        if (!resultado.isError()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Éxito", "Calificación registrada/actualizada correctamente.");
            tfPuntaje.clear();
            cbAlumnos.getSelectionModel().clearSelection();
            lbMensaje.setText("Calificación guardada exitosamente.");
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", resultado.getMensaje());
        }
    }

    private void eliminarCalificacionAnteriorSiExiste(int idCurso, int idAlumno) {
        List<Calificacion> calificacionesCurso = CalificacionDAO.obtenerCalificacionesPorCurso(idCurso);
        
        if (calificacionesCurso != null) {
            for (Calificacion c : calificacionesCurso) {
                if (c.getIdAlumno() == idAlumno) {
                    CalificacionDAO.eliminarCalificacion(c.getIdCalificacion());
                    break; 
                }
            }
        }
    }

    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }
}