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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.dao.UsuarioDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;
import proyectoSeguridad.modelo.pojo.Usuario;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdministradorRegistrarAlumnoController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellido;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfContrasena;
    @FXML
    private TextField tfMatricula;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Label lbMensaje;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Button btnVolver; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si es necesaria
    }    

    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (mostrarAlertaConfirmacion("Cerrar Sesión", "¿Está seguro que desea cerrar la sesión y volver al login?")) {
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
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
            }
        }
    }
    
    @FXML
    private void clicBotonVolver(ActionEvent event) {
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stageActual.close();
    }
    

    @FXML
    private void clicBotonRegistrarAlumno(ActionEvent event) {
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String username = tfUsername.getText().trim();
        String contrasena = pfContrasena.getText().trim();
        String matricula = tfMatricula.getText().trim();

        if(nombre.isEmpty() || apellido.isEmpty() || username.isEmpty() || contrasena.isEmpty() || matricula.isEmpty()){
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Todos los campos son obligatorios.");
            return;
        }

        try {
            if(UsuarioDAO.existeUsuario(username)){
                mostrarAlerta(Alert.AlertType.WARNING, "Usuario existente", "El nombre de usuario ya está registrado.");
                return;
            }

            if(AlumnoDAO.existeMatricula(matricula)){
                mostrarAlerta(Alert.AlertType.WARNING, "Matrícula existente", "La matrícula ya está registrada.");
                return;
            }

            String contrasenaHasheada = Utilidad.calcularHash(contrasena);

            if (contrasenaHasheada == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error interno", "No se pudo procesar la contraseña.");
                return;
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellido(apellido);
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setContrasenaHash(contrasenaHasheada); 
            nuevoUsuario.setRol("Alumno");

            ResultadoOperacion resUsuario = UsuarioDAO.registrarUsuario(nuevoUsuario);
            if(resUsuario.isError()){
                mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar usuario", resUsuario.getMensaje());
                return;
            }

            int idUsuario = UsuarioDAO.obtenerIdUsuarioPorCredenciales(username, contrasenaHasheada);

            if (idUsuario <= 0) {
                 mostrarAlerta(Alert.AlertType.ERROR, "Error de recuperación", "El usuario se creó pero no se pudo recuperar su ID.");
                 return;
            }

            Alumno nuevoAlumno = new Alumno();
            nuevoAlumno.setIdUsuario(idUsuario);
            nuevoAlumno.setMatricula(matricula);
            nuevoAlumno.setEstadoInscripcion("Pendiente");

            ResultadoOperacion resAlumno = AlumnoDAO.registrarAlumno(nuevoAlumno);
            if(resAlumno.isError()){
                mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar alumno", resAlumno.getMensaje());
                return;
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", "Alumno registrado correctamente.");
            limpiarCampos();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error en la base de datos", "Ocurrió un error al acceder a la base de datos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    private void limpiarCampos(){
        tfNombre.clear();
        tfApellido.clear();
        tfUsername.clear();
        pfContrasena.clear();
        tfMatricula.clear();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    private boolean mostrarAlertaConfirmacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        return alerta.showAndWait().filter(response -> response == javafx.scene.control.ButtonType.OK).isPresent();
    }
}