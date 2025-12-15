package proyectoSeguridad.controlador;

import java.io.IOException; // NECESARIO
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; 
import javafx.fxml.Initializable;
import javafx.scene.Parent; 
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage; 
import proyectoSeguridad.modelo.dao.AlumnoCursoDAO;
import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.dao.CursoDAO;
import proyectoSeguridad.modelo.pojo.AlumnoCurso;
import proyectoSeguridad.modelo.pojo.Curso;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLRegistrarAlumnoCursoController implements Initializable {

    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ComboBox<String> cbAlumno;
    @FXML
    private ComboBox<String> cbCurso;
    @FXML
    private Button btnInscribir;
    @FXML
    private Label lbMensaje;
    @FXML
    private Label lbEstadoInscripcion; 

    @FXML
    private Button btnVolver;

    private Map<String, Integer> mapaAlumnos;
    private Map<String, Integer> mapaCursos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapaAlumnos = new HashMap<>();
        mapaCursos = new HashMap<>();
        
        cargarAlumnos();
        cargarCursos();
        
        cbAlumno.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                verificarEstadoInscripcion(newValue);
            } else {
                lbEstadoInscripcion.setText("");
            }
        });
    }

    private void cargarAlumnos() {
        try {
            List<proyectoSeguridad.modelo.pojo.Alumno> alumnos = AlumnoDAO.obtenerAlumnosPendientes(); 
            ObservableList<String> items = FXCollections.observableArrayList();

            for (proyectoSeguridad.modelo.pojo.Alumno alumno : alumnos) {

                String nombre = AlumnoDAO.obtenerNombreCompleto(alumno.getIdUsuario());
                String itemDisplay = nombre + " (" + alumno.getMatricula() + ")";
                
                items.add(itemDisplay);
                mapaAlumnos.put(itemDisplay, alumno.getIdAlumno());
            }
            cbAlumno.setItems(items);
            
        } catch (SQLException e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Carga", "No se pudo cargar la lista de alumnos.");
            e.printStackTrace();
        }
    }
    
    private void cargarCursos() {
        try {
            List<Curso> cursos = CursoDAO.obtenerTodosLosCursos();
            ObservableList<String> items = FXCollections.observableArrayList();

            for (Curso curso : cursos) {
                String itemDisplay = curso.getNombreMateria() + " (" + curso.getClaveCurso() + ")";
                items.add(itemDisplay);
                mapaCursos.put(itemDisplay, curso.getIdCurso());
            }
            cbCurso.setItems(items);

        } catch (SQLException e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Carga", "No se pudo cargar la lista de cursos.");
            e.printStackTrace();
        }
    }
    
    private void verificarEstadoInscripcion(String alumnoSeleccionado) {
        Integer idAlumno = mapaAlumnos.get(alumnoSeleccionado);
        if (idAlumno == null) return;
        
        try {
            boolean pagado = AlumnoDAO.InscripcionPagada(idAlumno);
            if (pagado) {
                lbEstadoInscripcion.setText("Estado: PAGADO ✅");
                btnInscribir.setDisable(false);
            } else {
                lbEstadoInscripcion.setText("Estado: PENDIENTE ⚠️");
                // Si la inscripción no está pagada, se deshabilita el botón
                btnInscribir.setDisable(true); 
            }
        } catch (SQLException e) {
            lbEstadoInscripcion.setText("Error al verificar pago.");
            e.printStackTrace();
        }
    }

    @FXML
    private void clicBotonInscribirAlumnoCurso(ActionEvent event) {
        String alumnoSeleccionado = cbAlumno.getSelectionModel().getSelectedItem();
        String cursoSeleccionado = cbCurso.getSelectionModel().getSelectedItem();

        if (alumnoSeleccionado == null || cursoSeleccionado == null) {
            lbMensaje.setText("Debe seleccionar un alumno y un curso.");
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Campos Incompletos", "Debe seleccionar un alumno y un curso.");
            return;
        }

        Integer idAlumno = mapaAlumnos.get(alumnoSeleccionado);
        Integer idCurso = mapaCursos.get(cursoSeleccionado);
        
        if (idAlumno == null || idCurso == null) {
            lbMensaje.setText("Error al obtener IDs internos. Recargue la ventana.");
            return;
        }

        if (btnInscribir.isDisable()) {
             Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Pago Pendiente", "No se puede inscribir: el pago del alumno está pendiente.");
             return;
        }

        try {
            if (AlumnoCursoDAO.existeInscripcion(idCurso, idAlumno)) {
                lbMensaje.setText("El alumno ya está inscrito en este curso.");
                Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Inscripción Duplicada", "El alumno ya se encuentra inscrito en el curso seleccionado.");
                return;
            }
            
            AlumnoCurso ac = new AlumnoCurso(0, idCurso, idAlumno);
            ResultadoOperacion resultado = AlumnoCursoDAO.registrarAlumnoEnCurso(ac);

            if (!resultado.isError()) {
                lbMensaje.setText(resultado.getMensaje());
                Utilidad.mostrarAlertaSimple(AlertType.INFORMATION, "Éxito", resultado.getMensaje());
                limpiarCampos();
            } else {
                lbMensaje.setText("Error: " + resultado.getMensaje());
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Registro", resultado.getMensaje());
            }

        } catch (SQLException e) {
            lbMensaje.setText("Error de Base de Datos.");
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Conexión", "Ocurrió un error al intentar inscribir al alumno en el curso: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (Utilidad.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Está seguro que desea cerrar la sesión y volver al login?")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLInicioSesion.fxml"));
                Parent root = loader.load();
                
                Stage stageNueva = new Stage();
                stageNueva.setScene(new Scene(root));
                stageNueva.setTitle("Inicio de Sesión");
                stageNueva.show(); 
                
                Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
                stageActual.close();

            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
            }
        }
    }

    @FXML
    private void clicBotonVolver(ActionEvent event) {
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stageActual.close();
    }
    
    private void limpiarCampos() {
        cbAlumno.getSelectionModel().clearSelection();
        cbCurso.getSelectionModel().clearSelection();
        lbMensaje.setText("");
        lbEstadoInscripcion.setText("");
        btnInscribir.setDisable(true); 
    }
}