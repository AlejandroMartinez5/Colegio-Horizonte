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
import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.Usuario;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLPantallaPrincipalAlumnoController implements Initializable {

    @FXML
    private Label lbNombre;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Button btnCalificaciones;
    @FXML
    private Button btnHorarios;
    @FXML
    private Label lbReloj;
    @FXML
    private Button btnInscripciones;
    @FXML
    private Button btnComunicaciones;

    private Usuario usuarioSesion;
    private int idAlumno;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    public void inicializarInformacion(Usuario usuario) {
        this.usuarioSesion = usuario;
        
        lbNombre.setText("Hola, " + usuario.getNombre() + " " + usuario.getApellido());
        
        Utilidad.mostrarHora(lbReloj);
        
        cargarIdAlumno();
    }

    private void cargarIdAlumno() {
        try {
            Alumno alumno = AlumnoDAO.obtenerAlumnoPorIdUsuario(usuarioSesion.getIdUsuario());
            
            if (alumno != null) {
                this.idAlumno = alumno.getIdAlumno();
            } else {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Datos", 
                        "No se encontró información escolar asociada a tu usuario.");
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Conexión", 
                    "No se pudo recuperar la información del alumno.");
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
            }
        }
    }

    @FXML
    private void clicBotonCalificaciones(ActionEvent event) {
        if (idAlumno > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAlumnoConsultarCalificaciones.fxml"));
                Parent root = loader.load();

                FXMLAlumnoConsultarCalificacionesController controlador = loader.getController();
                controlador.setIdAlumno(this.idAlumno);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Mis Calificaciones");
                stage.show(); 

            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de calificaciones.");
                ex.printStackTrace();
            }
        } else {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Espera", "Cargando información del alumno...");
        }
    }

    @FXML
    private void clicBotonConsultarHorarios(ActionEvent event) {
        if (idAlumno > 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAlumnoConsultarHorarios.fxml"));
                Parent root = loader.load();

                FXMLAlumnoConsultarHorariosController controlador = loader.getController();
                controlador.setIdAlumno(this.idAlumno); 

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Mi Horario");
                stage.show();

            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de horarios.");
                ex.printStackTrace();
            }
        } else {
             Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Espera", "Cargando información del alumno...");
        }
    }

    @FXML
    private void clicBotonInscripciones(ActionEvent event) {
        Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Próximamente", "Módulo de Inscripciones en construcción.");
    }
    
    @FXML
    private void clicBotonComunicaciones(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAlumnoComunicados.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Avisos y Comunicados");
            stage.show();

        } catch (IOException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de comunicados.");
            ex.printStackTrace();
        }
    }
}