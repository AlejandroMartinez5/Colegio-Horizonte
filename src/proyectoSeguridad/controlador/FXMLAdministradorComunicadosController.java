package proyectoSeguridad.controlador;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
// Importaciones de JavaFX para la navegación (Stage, Scene, Parent, FXMLLoader)
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage; 
import proyectoSeguridad.modelo.dao.ComunicadoDAO;
import proyectoSeguridad.modelo.pojo.Comunicado;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdministradorComunicadosController implements Initializable {

    // Controles de Publicación
    @FXML private Button btnCerrarSesion;
    @FXML private TextField tfTitulo;
    @FXML private TextArea taContenido;
    @FXML private Button btnPublicar;
    @FXML private Label lbMensajePublicacion;
    @FXML private Label lbErrorTitulo;
    @FXML private Label lbErrorContenido;
    
    // Controles de Consulta
    @FXML private TableView<Map<String, Object>> tvComunicados;
    @FXML private TableColumn<Map<String, Object>, String> colTitulo;
    @FXML private TableColumn<Map<String, Object>, String> colContenido;
    @FXML private TableColumn<Map<String, Object>, String> colFecha;
    @FXML private TableColumn<Map<String, Object>, String> colAutor;

    // Datos de Sesión (Simulación: Esto debe venir del login)
    private int idUsuarioAdmin = 3; 

    // Lista observable
    private ObservableList<Map<String, Object>> listaComunicados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaComunicados = FXCollections.observableArrayList();
        configurarTabla();
        cargarComunicados();
        agregarListenerATabla(); 
    }
    
    // --- Métodos de Inicialización y Carga ---

    private void configurarTabla() {
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("titulo")));
        colContenido.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("contenido")));
        colAutor.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("autor")));
        
        colFecha.setCellValueFactory(data -> {
            Timestamp ts = (Timestamp) data.getValue().get("fechaPublicacion");
            String fechaFormateada = ts != null ? ts.toString().substring(0, 16) : "N/A"; 
            return new SimpleStringProperty(fechaFormateada);
        });

        tvComunicados.setItems(listaComunicados);
    }

    private void cargarComunicados() {
        listaComunicados.clear();
        try {
            List<Map<String, Object>> comunicadosObtenidos = ComunicadoDAO.obtenerComunicadosDetallados();

            if (comunicadosObtenidos != null && !comunicadosObtenidos.isEmpty()) {
                listaComunicados.addAll(comunicadosObtenidos);
            } else {
                tvComunicados.setPlaceholder(new Label("No hay comunicados publicados."));
            }

        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Consulta", "No se pudo cargar la lista de comunicados.");
            e.printStackTrace();
        }
    }
    
    private void agregarListenerATabla() {
        tvComunicados.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                Map<String, Object> seleccionado = tvComunicados.getSelectionModel().getSelectedItem();
                
                if (seleccionado != null) {
                    mostrarDetalleComunicado(seleccionado);
                }
            }
        });
    }

    private void mostrarDetalleComunicado(Map<String, Object> comunicado) {
        String titulo = (String) comunicado.get("titulo");
        String contenido = (String) comunicado.get("contenido");
        String autor = (String) comunicado.get("autor");
        Timestamp fecha = (Timestamp) comunicado.get("fechaPublicacion");
        
        String fechaStr = fecha != null ? fecha.toString().substring(0, 16) : "N/A";
        
        String cabecera = "Publicado por: " + autor + " el " + fechaStr;
        
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Detalle del Comunicado");
        alerta.setHeaderText(cabecera);
        
        TextArea textArea = new TextArea(contenido);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);
        
        alerta.getDialogPane().setExpandableContent(expContent);
        alerta.getDialogPane().setExpanded(true);
        alerta.getDialogPane().setPrefSize(600, 400); 

        alerta.showAndWait();
    }


    // --- Métodos de Acción ---

    @FXML
    private void clicBotonPublicar(ActionEvent event) {
        String titulo = tfTitulo.getText().trim();
        String contenido = taContenido.getText().trim();
        
        limpiarMensajesDeError();

        if (validarCampos(titulo, contenido)) {
            Comunicado comunicado = new Comunicado(idUsuarioAdmin, titulo, contenido);
            ResultadoOperacion resultado = ComunicadoDAO.registrarComunicado(comunicado);

            if (!resultado.isError()) {
                Utilidad.mostrarAlertaSimple(AlertType.INFORMATION, "Éxito", resultado.getMensaje());
                limpiarFormulario();
                cargarComunicados(); 
            } else {
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error", resultado.getMensaje());
                lbMensajePublicacion.setText("Error al publicar.");
            }
        }
    }

    private boolean validarCampos(String titulo, String contenido) {
        boolean esValido = true;
        
        if (titulo.isEmpty() || titulo.length() > 100) {
            lbErrorTitulo.setText("Título requerido (máx. 100 caracteres).");
            esValido = false;
        }
        
        if (contenido.isEmpty()) {
            lbErrorContenido.setText("Contenido del mensaje requerido.");
            esValido = false;
        }
        
        return esValido;
    }
    
    private void limpiarMensajesDeError() {
        lbErrorTitulo.setText("");
        lbErrorContenido.setText("");
        lbMensajePublicacion.setText("");
    }
    
    private void limpiarFormulario() {
        tfTitulo.clear();
        taContenido.clear();
        lbMensajePublicacion.setText("Comunicado listo para la siguiente publicación.");
    }
    
    public void setIdUsuarioAdmin(int idAdmin) {
        this.idUsuarioAdmin = idAdmin;
    }
    
    /**
     * MÉTODOS DE NAVEGACIÓN
     * Este método cierra la ventana actual (Administrador) y abre la de Inicio de Sesión.
     */
    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        try {
            // 1. Cargar el FXML de la nueva ventana (Inicio de Sesión)
            FXMLLoader cargadorFXML = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLInicioSesion.fxml"));
            Parent vista = cargadorFXML.load();
            Scene escena = new Scene(vista);
            
            // 2. Crear una nueva Stage (ventana) para el Login
            Stage stageNueva = new Stage();
            stageNueva.setScene(escena);
            stageNueva.setTitle("Inicio de Sesión");
            
            // 3. Mostrar la nueva Stage.
            stageNueva.show(); 
            
            // 4. Obtener la Stage actual (la del Administrador) y CERRARLA.
            Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
            stageActual.close();
            
        } catch (Exception e) {
            System.err.println("Error al cargar la vista de Inicio de Sesión: " + e.getMessage());
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cargar la pantalla de Inicio de Sesión.");
            e.printStackTrace();
        }
    }
    
    /**
     * NOTA: Para cualquier otra navegación dentro de la sesión (ej. Menú Principal),
     * debes usar el patrón de "ventanas apiladas" que te comenté, donde solo
     * se llama a stageNueva.show() y NO se cierra la ventana actual.
     * Ejemplo para un botón 'btnIrAMenu':
     *
     * @FXML
     * private void clicBotonIrAMenuPrincipal(ActionEvent event) {
     * try {
     * FXMLLoader cargadorFXML = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLMenuPrincipal.fxml"));
     * Parent vista = cargadorFXML.load();
     * Stage stageNueva = new Stage();
     * stageNueva.setScene(new Scene(vista));
     * stageNueva.setTitle("Menú Principal");
     * stageNueva.show(); 
     * // La ventana actual NO se cierra
     * } catch (Exception e) {
     * // ... manejo de error
     * }
     * }
     */
}