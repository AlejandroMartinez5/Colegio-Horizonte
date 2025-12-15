package proyectoSeguridad.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

public class FXMLAdministradorConsultarAlumnosCursoController implements Initializable {

    @FXML
    private TextField tfClaveCurso;
    @FXML
    private Label lbNombre;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Label lbReloj;
    
    @FXML 
    private Button btnVolver; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si es necesaria
    }

    
    @FXML
    private void clicBotonBuscar(ActionEvent event) {
        String claveCurso = tfClaveCurso.getText().trim();
        if (claveCurso.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Campo Requerido");
            alert.setHeaderText(null);
            alert.setContentText("Debes escribir la clave del curso para buscar.");
            alert.showAndWait();
            return;
        }

        try {
            Curso curso = CursoDAO.obtenerCursoPorClave(claveCurso);
            if (curso == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error de Búsqueda");
                alert.setHeaderText(null);
                alert.setContentText("Curso no encontrado. Verifica la clave.");
                alert.showAndWait();
                return;
            }

            List<Alumno> alumnos = AlumnoDAO.obtenerAlumnosPorCurso(curso.getIdCurso());
            mostrarResultado(curso, alumnos);

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Conexión");
            alert.setHeaderText(null);
            alert.setContentText("Error al conectar con la base de datos.");
            alert.showAndWait();
        }
    }

    
    private void mostrarResultado(Curso curso, List<Alumno> alumnos) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/proyectoSeguridad/vista/FXMLAdministradorConsultarAlumnosCurso_Resultado.fxml"
            ));
            Parent vista = loader.load();

            FXMLAdministradorConsultarAlumnosCurso_ResultadoController controller =
                    loader.getController();

            controller.cargarInformacionCurso(curso, alumnos);

            Stage stageNueva = new Stage();
            stageNueva.setScene(new Scene(vista));
            stageNueva.setTitle("Alumnos del curso " + curso.getNombreMateria());
            
            stageNueva.show();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Navegación");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la ventana de resultados.");
            alert.showAndWait();
        }
    }


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
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de Navegación");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo cargar la pantalla de Inicio de Sesión.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void clicBotonVolver(ActionEvent event) {
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stageActual.close();
    }


    @FXML
    private void tfNombreProyectoPresionaEnter(KeyEvent event) {
        // Lógica para llamar a clicBotonBuscar(null) si se presiona Enter en el campo de texto.
    }
}