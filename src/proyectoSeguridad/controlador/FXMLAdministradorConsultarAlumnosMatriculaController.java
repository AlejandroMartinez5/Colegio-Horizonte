package proyectoSeguridad.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.dao.CursoDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.Curso;

public class FXMLAdministradorConsultarAlumnosMatriculaController implements Initializable {

    @FXML private Label lbNombre;
    @FXML private Button btnCerrarSesion;
    @FXML private Label lbReloj;
    @FXML private TextField tfMatriculaEstudiante;
    @FXML
    private ImageView btn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Opcional: lógica inicial
    }

    // --- Lógica de Cerrar Sesión ---

    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/proyectoSeguridad/vista/FXMLInicioSesion.fxml"
            ));
            Parent root = loader.load();
            
            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Inicio de Sesión");
            stageNueva.show(); 
            
            Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
            stageActual.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarAlerta("Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
        }
    }
    
    // --- Lógica de Búsqueda (Navegación Interna) ---

    @FXML
    private void clicBotonBuscar(ActionEvent event) {

        String matricula = tfMatriculaEstudiante.getText().trim();

        // --- VALIDACIONES ---
        if (matricula.isEmpty()) {
            mostrarAlerta("Dato requerido", "Debes ingresar una matrícula.");
            return;
        }

        if (matricula.length() < 4) {
            mostrarAlerta("Matrícula inválida", "La matrícula debe contener al menos 4 caracteres.");
            return;
        }

        try {
            Alumno alumno = AlumnoDAO.obtenerAlumnoPorMatricula(matricula);

            if (alumno == null) {
                mostrarAlerta("No encontrado", "No existe un alumno con esa matrícula.");
                return;
            }

            List<Curso> cursos = CursoDAO.obtenerCursosPorIdAlumno(alumno.getIdAlumno());
            
            // --- CARGAR VENTANA DE RESULTADOS EN UNA NUEVA STAGE ---
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorConsultarAlumnosMatricula_Resultado.fxml"
            ));

            Parent root = loader.load();

            FXMLAdministradorConsultarAlumnosMatricula_ResultadoController controlador =
                loader.getController();

            controlador.cargarInformacionAlumno(alumno, cursos);

            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(root));
            stageNueva.setTitle("Cursos del Alumno: " + matricula);
            
            stageNueva.show(); 

        } catch (SQLException ex) {
            mostrarAlerta("Error en Base de Datos", "Ocurrió un error al consultar la información.");
            ex.printStackTrace();
        } catch (IOException ex) {
             mostrarAlerta("Error de Navegación", "No se pudo cargar la ventana de resultados.");
             ex.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void tfNombreProyectoPresionaEnter(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            clicBotonBuscar(new ActionEvent());
        }
    }
}