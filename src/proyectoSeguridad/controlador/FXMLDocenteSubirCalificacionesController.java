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
    
    // Mapas para obtener IDs a partir de la selección en los ComboBox
    private Map<String, Integer> mapaCursos;
    private Map<String, Integer> mapaAlumnos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapaCursos = new HashMap<>();
        mapaAlumnos = new HashMap<>();
        
        // Listener para validar que solo se escriban números y un punto decimal
        tfPuntaje.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tfPuntaje.setText(oldValue);
            }
        });
    }
    
    /**
     * Recibe el ID del docente y carga sus cursos.
     */
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
            // Nota: Se asume que AlumnoDAO.obtenerAlumnosPorCurso() también carga Nombre y Apellido
            List<Alumno> alumnos = AlumnoDAO.obtenerAlumnosPorCurso(idCurso);
            ObservableList<String> items = FXCollections.observableArrayList();
            
            if (alumnos.isEmpty()) {
                cbAlumnos.setPromptText("Sin alumnos inscritos");
            } else {
                for (Alumno a : alumnos) {
                    // Muestra Nombre Apellido (Matricula)
                    // Nota: Se asume que los objetos Alumno devueltos contienen Nombre y Apellido.
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
        
        // 2. Preparar objeto
        int idCurso = mapaCursos.get(cursoSel);
        int idAlumno = mapaAlumnos.get(alumnoSel);
        
        // --- VALIDACIÓN DE EXISTENCIA: Reemplazar calificación anterior ---
        eliminarCalificacionAnteriorSiExiste(idCurso, idAlumno);
        // ------------------------------------------------------------------
        
        Calificacion nuevaCalificacion = new Calificacion();
        nuevaCalificacion.setIdCurso(idCurso);
        nuevaCalificacion.setIdAlumno(idAlumno);
        nuevaCalificacion.setPuntaje(puntaje);
        // Generamos fecha actual (YYYY-MM-DD)
        nuevaCalificacion.setFechaRegistro(LocalDate.now().toString()); 
        
        // 3. Guardar en BD (Registrar la nueva)
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
    
    /**
     * Busca si ya existe una calificación para el alumno en el curso dado.
     * Si existe, la elimina para permitir registrar la nueva (simulando un UPDATE).
     */
    private void eliminarCalificacionAnteriorSiExiste(int idCurso, int idAlumno) {
        // Obtenemos todas las calificaciones del curso
        List<Calificacion> calificacionesCurso = CalificacionDAO.obtenerCalificacionesPorCurso(idCurso);
        
        if (calificacionesCurso != null) {
            for (Calificacion c : calificacionesCurso) {
                // Si encontramos una calificación que pertenezca al alumno seleccionado
                if (c.getIdAlumno() == idAlumno) {
                    // La eliminamos usando su ID único
                    CalificacionDAO.eliminarCalificacion(c.getIdCalificacion());
                    // Rompemos el ciclo asumiendo que solo debería haber una
                    break; 
                }
            }
        }
    }

    /**
     * CIERRE DE VENTANA: Cierra la Stage actual para regresar a la vista anterior (Menú del Docente).
     */
    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        // Obtiene la Stage actual a partir del botón
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        
        // Cierra la Stage, manteniendo la Stage anterior abierta.
        stage.close();
    }
}