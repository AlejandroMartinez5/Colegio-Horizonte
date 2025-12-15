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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.dao.DocenteDAO;
import proyectoSeguridad.modelo.dao.UsuarioDAO;
import proyectoSeguridad.modelo.pojo.Docente;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;
import proyectoSeguridad.modelo.pojo.Usuario;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdministradorRegistrarDocenteController implements Initializable {

    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellido;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfContrasena;
    @FXML
    private TextField tfNumeroEmpleado;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Label lbMensaje;
    
    @FXML
    private Button btnVolver; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si es necesaria
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

    @FXML
    private void clicBotonRegistrarDocente(ActionEvent event) {
        String nombre = tfNombre.getText().trim();
        String apellido = tfApellido.getText().trim();
        String username = tfUsername.getText().trim();
        String contrasena = pfContrasena.getText().trim();
        String numeroEmpleado = tfNumeroEmpleado.getText().trim();

        // Validaciones básicas
        if(nombre.isEmpty() || apellido.isEmpty() || username.isEmpty() || contrasena.isEmpty() || numeroEmpleado.isEmpty()){
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Campos incompletos", "Todos los campos son obligatorios.");
            return;
        }

        try {
            if(UsuarioDAO.existeUsuario(username)){
                Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Usuario existente", "El nombre de usuario ya está registrado.");
                return;
            }

            if(DocenteDAO.existeNumeroEmpleado(numeroEmpleado)){
                Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Número de empleado existente", "El número de empleado ya está registrado.");
                return;
            }

            String contrasenaHasheada = Utilidad.calcularHash(contrasena);
            
            if (contrasenaHasheada == null) {
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error interno", "No se pudo procesar la contraseña.");
                return;
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellido(apellido);
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setContrasenaHash(contrasenaHasheada); // Guardamos el HASH
            nuevoUsuario.setRol("Docente");

            ResultadoOperacion resUsuario = UsuarioDAO.registrarUsuario(nuevoUsuario);
            if(resUsuario.isError()){
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error al registrar usuario", resUsuario.getMensaje());
                return;
            }
            
            int idUsuario = UsuarioDAO.obtenerIdUsuarioPorCredenciales(username, contrasenaHasheada);

            if (idUsuario <= 0) {
                 Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de recuperación", "El usuario se creó pero no se pudo recuperar su ID.");
                 return;
            }

            // Crear objeto Docente
            Docente nuevoDocente = new Docente();
            nuevoDocente.setIdUsuario(idUsuario);
            nuevoDocente.setNumeroEmpleado(numeroEmpleado);

            ResultadoOperacion resDocente = DocenteDAO.registrarDocente(nuevoDocente);
            if(resDocente.isError()){
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error al registrar docente", resDocente.getMensaje());
                return;
            }

            Utilidad.mostrarAlertaSimple(AlertType.INFORMATION, "Registro exitoso", "Docente registrado correctamente.");
            limpiarCampos();

        } catch (SQLException e) {
            e.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error en la base de datos", "Ocurrió un error al acceder a la base de datos: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error inesperado", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    private void limpiarCampos(){
        tfNombre.clear();
        tfApellido.clear();
        tfUsername.clear();
        pfContrasena.clear();
        tfNumeroEmpleado.clear();
    }
}