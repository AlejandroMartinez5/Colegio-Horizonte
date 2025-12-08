package proyectoSeguridad.controlador;

import java.io.IOException; // Necesario para FXMLLoader
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // NECESARIO
import javafx.fxml.Initializable;
import javafx.scene.Parent; // NECESARIO
import javafx.scene.Scene; // NECESARIO
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage; // NECESARIO
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
    
    // Suponiendo que se añade un botón de Volver/Regresar en el FXML
    @FXML
    private Button btnVolver; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si es necesaria
    }    

    // --- Lógica de Navegación ---
    
    /**
     * Cierra la sesión: abre la ventana de Login y cierra la ventana actual.
     * @param event 
     */
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
    
    /**
     * Cierra la ventana actual para regresar a la ventana que la invocó.
     * (Asumiendo que se añade un botón de Volver/Regresar en el FXML, con fx:id="btnVolver").
     * @param event 
     */
    @FXML
    private void clicBotonVolver(ActionEvent event) {
        // Obtenemos el botón que disparó el evento (asumiendo que se llama btnVolver)
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stageActual.close();
    }

    // --- Lógica de Registro ---

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
            // Verificar que el username no exista
            if(UsuarioDAO.existeUsuario(username)){
                Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Usuario existente", "El nombre de usuario ya está registrado.");
                return;
            }

            // Verificar que el número de empleado no exista
            if(DocenteDAO.existeNumeroEmpleado(numeroEmpleado)){
                Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Número de empleado existente", "El número de empleado ya está registrado.");
                return;
            }

            // Crear objeto Usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellido(apellido);
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setContrasenaHash(contrasena); // Se recomienda encriptar
            nuevoUsuario.setRol("Docente");

            // Registrar usuario
            ResultadoOperacion resUsuario = UsuarioDAO.registrarUsuario(nuevoUsuario);
            if(resUsuario.isError()){
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error al registrar usuario", resUsuario.getMensaje());
                return;
            }

            // Obtener ID generado del usuario
            // Nota: Se asume que este método obtiene el ID correcto del usuario recién creado.
            int idUsuario = UsuarioDAO.obtenerIdUsuarioPorCredenciales(username, contrasena);

            // Crear objeto Docente
            Docente nuevoDocente = new Docente();
            nuevoDocente.setIdUsuario(idUsuario);
            nuevoDocente.setNumeroEmpleado(numeroEmpleado);

            // Registrar docente
            ResultadoOperacion resDocente = DocenteDAO.registrarDocente(nuevoDocente);
            if(resDocente.isError()){
                // Si falla el registro del docente, se debería revertir el registro del usuario.
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