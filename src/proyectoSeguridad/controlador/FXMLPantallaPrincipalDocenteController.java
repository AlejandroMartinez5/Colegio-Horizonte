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
import javafx.stage.Stage; 
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

    private Usuario usuarioSesion;
    private int idDocente; 
    @FXML
    private Button btnComunicaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void inicializarInformacion(Usuario usuario) {
        this.usuarioSesion = usuario;
        lbNombre.setText("Docente: " + usuario.getNombre() + " " + usuario.getApellido());
        Utilidad.mostrarHora(lbReloj);
        cargarIdDocente();
    }

    private void cargarIdDocente() {
        try {
            Docente docente = DocenteDAO.obtenerDocentePorIdUsuario(usuarioSesion.getIdUsuario());
            
            if (docente != null) {
                this.idDocente = docente.getIdDocente();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Datos", "No se encontró información del docente asociada a este usuario.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Conexión", "No se pudo recuperar la información del docente.");
            ex.printStackTrace();
        }
    }

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
                stage.show(); 

            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de horarios.");
                ex.printStackTrace();
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Espera", "La información del docente no se ha cargado correctamente.");
        }
    }
    
    @FXML
    private void clicBotonAlumnos(ActionEvent event) {
        if (idDocente > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocentePantallaAlumnos.fxml"));
                Parent root = loader.load();

                FXMLDocentePantallaAlumnosController controlador = loader.getController();
                controlador.setDocente(this.idDocente); 

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Gestión de Alumnos");
                stage.show(); 

            } catch (IOException ex) {
                ex.printStackTrace();
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de alumnos.");
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Espera", "La información del docente no se ha cargado correctamente.");
        }
    }

    @FXML
    private void clicBotonComunicaciones(ActionEvent event) {
        if (idDocente > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLDocenteComunicados.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Avisos y Comunicados");
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