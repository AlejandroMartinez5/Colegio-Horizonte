package proyectoSeguridad.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage; // Eliminamos Modality y solo usamos Stage
import proyectoSeguridad.modelo.dao.DocenteDAO;
import proyectoSeguridad.modelo.pojo.Docente;
import proyectoSeguridad.modelo.pojo.Usuario;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLPantallaPrincipalDocenteController implements Initializable {

    @FXML
    private Label lbNombre;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Button btnAlumnos;
    @FXML
    private Button btnHorarios;
    @FXML
    private Label lbReloj;

    // Atributos para almacenar la sesión
    private Usuario usuarioSesion;
    private int idDocente; // Este es el dato clave que necesitamos pasar
    @FXML
    private Button btnComunicaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializar reloj si es necesario
        // Utilidad.mostrarHora(lbReloj);
    }

    // Este método es llamado desde el Login
    public void inicializarInformacion(Usuario usuario) {
        this.usuarioSesion = usuario;
        lbNombre.setText("Docente: " + usuario.getNombre() + " " + usuario.getApellido());
        Utilidad.mostrarHora(lbReloj);

        // Obtener el ID del Docente a partir del ID de Usuario
        cargarIdDocente();
    }

    private void cargarIdDocente() {
        try {
            // Usamos el método que creamos anteriormente en DocenteDAO
            Docente docente = DocenteDAO.obtenerDocentePorIdUsuario(usuarioSesion.getIdUsuario());
            
            if (docente != null) {
                this.idDocente = docente.getIdDocente();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Datos", "No se encontró información del docente asociada a este usuario.");
                // Opcional: Cerrar sesión forzosamente si no hay datos de docente
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo recuperar la información del docente.");
            ex.printStackTrace();
        }
    }

    // --- Lógica de Navegación Externa (Cerrar Sesión) ---

    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (Utilidad.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Está seguro que desea salir?")) {
            try {
                Stage escenarioActual = (Stage) btnCerrarSesion.getScene().getWindow();
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
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
            }
        }
    }
    
    // --- Lógica de Navegación Interna (Ventanas Apiladas) ---

    /**
     * CORREGIDO: Abre la ventana de Horarios en una NUEVA Stage (apilamiento libre).
     */
    @FXML
    private void clicBotonHorarios(ActionEvent event) {
        if (idDocente > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocenteHorario.fxml"));
                Parent root = loader.load();

                FXMLDocenteHorarioController controlador = loader.getController();
                controlador.setDocente(this.idDocente);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Mi Horario");
                // *** CORRECCIÓN: Quitamos la modalidad para permitir el apilamiento libre ***
                stage.show(); 

            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de horarios.");
                ex.printStackTrace();
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Espera", "La información del docente no se ha cargado correctamente.");
        }
    }

    /**
     * CORREGIDO: Abre la pantalla de Alumnos (submenú) en una NUEVA Stage (apilamiento libre).
     */
    @FXML
    private void clicBotonAlumnos(ActionEvent event) {
        if (idDocente > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocentePantallaAlumnos.fxml"));
                Parent root = loader.load();

                FXMLDocentePantallaAlumnosController controlador = loader.getController();
                controlador.setDocente(this.idDocente); // Pasamos el ID

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Gestión de Alumnos");
                // *** CORRECCIÓN: Quitamos la modalidad para permitir el apilamiento libre ***
                stage.show(); 

            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de alumnos.");
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Espera", "La información del docente no se ha cargado correctamente.");
        }
    }

    /**
     * CORREGIDO: Abre la ventana de Comunicados en una NUEVA Stage (apilamiento libre).
     */
    @FXML
    private void clicBotonComunicaciones(ActionEvent event) {
        if (idDocente > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocenteComunicados.fxml"));
                Parent root = loader.load();

                // No se necesita pasar el ID del docente a esta vista, pero se mantiene el patrón.
                // FXMLDocenteComunicadosController controlador = loader.getController(); 

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Avisos y Comunicados");
                // *** CORRECCIÓN: Quitamos la modalidad para permitir el apilamiento libre ***
                stage.show();

            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de comunicados.");
                ex.printStackTrace();
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Espera", "La información del docente no se ha cargado correctamente.");
        }
    }
}