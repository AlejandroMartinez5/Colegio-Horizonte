package proyectoSeguridad.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType; 
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.pojo.Usuario;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLPantallaPrincipalAdministradorController implements Initializable {

    @FXML
    private Label lbNombre;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Label lbReloj;
    @FXML
    private Button btnInscripciones;
    @FXML
    private Button btnComunicaciones;
    @FXML
    private Button btnAlumnos;
    @FXML
    private Button btnDocentes;
    @FXML
    private Button btnCursos;

    private Usuario usuarioSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializaciones básicas si son necesarias
    }    

    public void inicializarInformacion(Usuario usuario) {
        this.usuarioSesion = usuario;
        
        if (lbNombre != null) {
            lbNombre.setText("Admin: " + usuario.getNombre() + " " + usuario.getApellido());
        }
        
        if (lbReloj != null) {
            Utilidad.mostrarHora(lbReloj);
        }
    }

    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (Utilidad.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Está seguro que desea salir?")) {
            try {
                Stage escenarioActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLInicioSesion.fxml"));
                Parent root = loader.load();
                
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Inicio de Sesión");
                stage.show();
                
                escenarioActual.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
            }
        }
    }

    @FXML
    private void clicBotonInscripciones(ActionEvent event) {
        abrirVentana("/proyectoSeguridad/vista/FXMLAdministradorPantallaInscripciones.fxml", "Gestión de Inscripciones");
    }

    @FXML
    private void clicBotonComunicaciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAdministradorComunicados.fxml"));
            Parent root = loader.load();

            FXMLAdministradorComunicadosController controlador = loader.getController();
            
            if (usuarioSesion != null) {
                controlador.setIdUsuarioAdmin(usuarioSesion.getIdUsuario());
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestión de Comunicados");
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Comunicados.");
        }
    }

    @FXML
    private void clicBotonAlumnos(ActionEvent event) {
        abrirVentana("/proyectoSeguridad/vista/FXMLAdministradorPantallaAlumno.fxml", "Gestión de Alumnos");
    }

    @FXML
    private void clicBotonDocentes(ActionEvent event) {
        abrirVentana("/proyectoSeguridad/vista/FXMLAdministradorPantallaDocente.fxml", "Gestión de Docentes");
    }

    @FXML
    private void clicBotonCursos(ActionEvent event) {
        abrirVentana("/proyectoSeguridad/vista/FXMLAdministradorPantallaCursos.fxml", "Gestión de Cursos");
    }
    
    private void abrirVentana(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla: " + titulo);
        }
    }
}