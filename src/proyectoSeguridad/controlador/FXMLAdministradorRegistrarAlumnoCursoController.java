package proyectoSeguridad.controlador;

import java.io.IOException; 
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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

public class FXMLAdministradorRegistrarAlumnoCursoController implements Initializable {

    // --- Atributos FXML ---
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ComboBox<String> cbAlumno;
    @FXML
    private ComboBox<String> cbCurso;
    @FXML
    private TextField tfBuscarAlumno; 
    @FXML
    private TextField tfBuscarCurso;  
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
    private ObservableList<String> listaCompletaAlumnos;
    private ObservableList<String> listaCompletaCursos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapaAlumnos = new HashMap<>();
        mapaCursos = new HashMap<>();
        listaCompletaAlumnos = FXCollections.observableArrayList();
        listaCompletaCursos = FXCollections.observableArrayList();
        
        cargarAlumnos(); 
        cargarCursos();
        
        cbAlumno.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                verificarEstadoInscripcion(newValue);
            } else {
                lbEstadoInscripcion.setText("");
                btnInscribir.setDisable(true); 
            }
        });

        btnInscribir.setDisable(true); 
    }


    private void cargarAlumnos() {
        listaCompletaAlumnos.clear();
        mapaAlumnos.clear();
        
        try {
            List<proyectoSeguridad.modelo.pojo.Alumno> alumnos = AlumnoDAO.obtenerTodosLosAlumnosConDetalle(); 

            for (proyectoSeguridad.modelo.pojo.Alumno alumno : alumnos) {
                // Usamos los campos Nombre y Apellido que ya fueron cargados por el DAO
                String nombreCompleto = alumno.getNombre() + " " + alumno.getApellido();
                String itemDisplay = nombreCompleto + " (" + alumno.getMatricula() + ")";
                
                listaCompletaAlumnos.add(itemDisplay);
                mapaAlumnos.put(itemDisplay, alumno.getIdAlumno());
            }
            cbAlumno.setItems(listaCompletaAlumnos);
            
        } catch (SQLException e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Carga", "No se pudo cargar la lista completa de alumnos.");
            e.printStackTrace();
        }
    }
    
    @FXML
    private void filtrarAlumnos() {
        String texto = tfBuscarAlumno.getText().toLowerCase();
        
        if (texto.isEmpty()) {
            cbAlumno.setItems(listaCompletaAlumnos);
            return;
        }

        ObservableList<String> listaFiltrada = listaCompletaAlumnos.stream()
            .filter(item -> item.toLowerCase().contains(texto))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        cbAlumno.setItems(listaFiltrada);
        if (!cbAlumno.isShowing() && listaFiltrada.size() > 0) {
            cbAlumno.show();
        }
    }


    private void cargarCursos() {
        listaCompletaCursos.clear();
        mapaCursos.clear();
        
        try {
            List<Curso> cursos = CursoDAO.obtenerTodosLosCursos();

            for (Curso curso : cursos) {
                String itemDisplay = curso.getNombreMateria() + " (" + curso.getClaveCurso() + ")";
                listaCompletaCursos.add(itemDisplay);
                mapaCursos.put(itemDisplay, curso.getIdCurso());
            }
            cbCurso.setItems(listaCompletaCursos);

        } catch (SQLException e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Carga", "No se pudo cargar la lista completa de cursos.");
            e.printStackTrace();
        }
    }

    @FXML
    private void filtrarCursos() {
        String texto = tfBuscarCurso.getText().toLowerCase();
        
        if (texto.isEmpty()) {
            cbCurso.setItems(listaCompletaCursos);
            return;
        }

        ObservableList<String> listaFiltrada = listaCompletaCursos.stream()
            .filter(item -> item.toLowerCase().contains(texto))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        cbCurso.setItems(listaFiltrada);
        if (!cbCurso.isShowing() && listaFiltrada.size() > 0) {
            cbCurso.show();
        }
    }
    
    private void verificarEstadoInscripcion(String alumnoSeleccionado) {
        Integer idAlumno = mapaAlumnos.get(alumnoSeleccionado);
        if (idAlumno == null) return;
        
        try {
            boolean pagado = AlumnoDAO.InscripcionPagada(idAlumno);
            if (pagado) {
                lbEstadoInscripcion.setText("Estado de Inscripción: PAGADO ✅");
                btnInscribir.setDisable(false);
            } else {
                lbEstadoInscripcion.setText("Estado de Inscripción: PENDIENTE ⚠️");
                btnInscribir.setDisable(true); 
            }
        } catch (SQLException e) {
            lbEstadoInscripcion.setText("Error al verificar estado de pago.");
            e.printStackTrace();
        }
    }

    @FXML
    private void clicBotonInscribirAlumnoCurso(ActionEvent event) {
        String alumnoSeleccionado = cbAlumno.getSelectionModel().getSelectedItem();
        String cursoSeleccionado = cbCurso.getSelectionModel().getSelectedItem();

        if (alumnoSeleccionado == null || cursoSeleccionado == null) {
            lbMensaje.setText("Debe seleccionar un alumno y un curso de la lista.");
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Selección Incompleta", "Por favor, seleccione un alumno y un curso de las listas desplegables.");
            return;
        }

        Integer idAlumno = mapaAlumnos.get(alumnoSeleccionado);
        Integer idCurso = mapaCursos.get(cursoSeleccionado);
        
        if (idAlumno == null || idCurso == null) {
            lbMensaje.setText("Error: ID interno no encontrado.");
            return;
        }
        
        // Re-verificar si la inscripción está pagada justo antes de inscribir
        try {
            boolean pagado = AlumnoDAO.InscripcionPagada(idAlumno);
            if (!pagado) {
                lbMensaje.setText("No se puede inscribir: Pago Pendiente.");
                Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Pago Pendiente", "No se puede completar la inscripción hasta que el alumno haya liquidado su pago.");
                return;
            }
        } catch (SQLException e) {
            lbMensaje.setText("Error al verificar el estado de pago.");
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Conexión", "No se pudo verificar el estado de pago del alumno.");
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
                // 1. Cargar el FXML de Inicio de Sesión
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLInicioSesion.fxml"));
                Parent root = loader.load();
                
                // 2. Abrir la nueva Stage (Login)
                Stage stageNueva = new Stage();
                stageNueva.setScene(new Scene(root));
                stageNueva.setTitle("Inicio de Sesión");
                stageNueva.show(); 
                
                // 3. Obtener la Stage actual y CERRARLA
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
        // Obtenemos el botón que disparó el evento (asumiendo que se llama btnVolver)
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stageActual.close();
    }

    private void limpiarCampos() {
        tfBuscarAlumno.setText("");
        tfBuscarCurso.setText("");
        cbAlumno.getSelectionModel().clearSelection();
        cbCurso.getSelectionModel().clearSelection();
        lbMensaje.setText("");
        lbEstadoInscripcion.setText("");
        cargarAlumnos();  
        cargarCursos();
    }
}