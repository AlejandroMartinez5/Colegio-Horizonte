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
import javafx.scene.control.Alert.AlertType; // Necesario para Utilidad
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

    // Atributo para mantener la sesión del usuario
    private Usuario usuarioSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializaciones básicas si son necesarias
    }    

    // --- MÉTODO PARA RECIBIR LA SESIÓN ---
    
    /**
     * Inicializa la información de la pantalla con los datos del usuario logueado.
     * @param usuario El usuario (Administrador) que inició sesión.
     */
    public void inicializarInformacion(Usuario usuario) {
        this.usuarioSesion = usuario;
        
        if (lbNombre != null) {
            lbNombre.setText("Admin: " + usuario.getNombre() + " " + usuario.getApellido());
        }
        
        if (lbReloj != null) {
            Utilidad.mostrarHora(lbReloj);
        }
    }

    // --- MÉTODOS DE ACCIÓN ---

    /**
     * Cierra la sesión: abre la ventana de Login y cierra la ventana actual.
     */
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

    /**
     * CORREGIDO: Abre la ventana de Gestión de Inscripciones en una NUEVA Stage.
     */
    @FXML
    private void clicBotonInscripciones(ActionEvent event) {
        abrirVentana("/proyectoSeguridad/vista/FXMLAdministradorPantallaInscripciones.fxml", "Gestión de Inscripciones");
    }

    /**
     * CORREGIDO: Abre la ventana de Comunicados en una NUEVA Stage, pasando el ID del Administrador.
     */
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

    /**
     * CORREGIDO: Abre la ventana de Gestión de Alumnos en una NUEVA Stage.
     */
    @FXML
    private void clicBotonAlumnos(ActionEvent event) {
        abrirVentana("/proyectoSeguridad/vista/FXMLAdministradorPantallaAlumno.fxml", "Gestión de Alumnos");
    }

    /**
     * CORREGIDO: Abre la ventana de Gestión de Docentes en una NUEVA Stage.
     */
    @FXML
    private void clicBotonDocentes(ActionEvent event) {
        abrirVentana("/proyectoSeguridad/vista/FXMLAdministradorPantallaDocente.fxml", "Gestión de Docentes");
    }

    /**
     * CORREGIDO: Abre la ventana de Gestión de Cursos en una NUEVA Stage.
     */
    @FXML
    private void clicBotonCursos(ActionEvent event) {
        abrirVentana("/proyectoSeguridad/vista/FXMLAdministradorPantallaCursos.fxml", "Gestión de Cursos");
    }
    
    // --- MÉTODO AUXILIAR PARA NAVEGACIÓN A NUEVA STAGE ---
    
    /**
     * Carga un FXML en una nueva Stage.
     */
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