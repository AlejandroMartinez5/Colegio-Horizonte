package proyectoSeguridad.controlador;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.dao.ComunicadoDAO;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAlumnoComunicadosController implements Initializable {

    @FXML
    private Button btnRegresar;
    @FXML
    private TableView<Map<String, Object>> tvComunicados;
    @FXML
    private TableColumn<Map<String, Object>, String> colTitulo;
    @FXML
    private TableColumn<Map<String, Object>, String> colFecha;
    @FXML
    private TableColumn<Map<String, Object>, String> colAutor;
    @FXML
    private TableColumn<Map<String, Object>, String> colContenido;

    // Lista observable para la tabla
    private ObservableList<Map<String, Object>> listaComunicados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaComunicados = FXCollections.observableArrayList();
        configurarTabla();
        cargarComunicados();
        agregarListenerDobleClic();
    }
    
    /**
     * Configura las columnas para leer los datos del Mapa devuelto por el DAO.
     */
    private void configurarTabla() {
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("titulo")));
        colAutor.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("autor")));
        
        // Vista previa corta del contenido (primeros 50 caracteres)
        colContenido.setCellValueFactory(data -> {
            String contenido = (String) data.getValue().get("contenido");
            if (contenido != null && contenido.length() > 50) {
                return new SimpleStringProperty(contenido.substring(0, 50) + "...");
            }
            return new SimpleStringProperty(contenido);
        });

        // Formato de fecha y hora
        colFecha.setCellValueFactory(data -> {
            Timestamp ts = (Timestamp) data.getValue().get("fechaPublicacion");
            String fechaStr = ts != null ? ts.toString().substring(0, 16) : "N/A";
            return new SimpleStringProperty(fechaStr);
        });
        
        tvComunicados.setItems(listaComunicados);
        tvComunicados.setPlaceholder(new javafx.scene.control.Label("No hay comunicados publicados actualmente."));
    }

    /**
     * Carga la lista de comunicados desde la Base de Datos.
     */
    private void cargarComunicados() {
        try {
            // Reutilizamos el DAO existente que trae todos los comunicados públicos
            List<Map<String, Object>> resultados = ComunicadoDAO.obtenerComunicadosDetallados();
            if (resultados != null) {
                listaComunicados.addAll(resultados);
            }
        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los avisos.");
            e.printStackTrace();
        }
    }

    /**
     * Permite ver el mensaje completo al dar doble clic.
     */
    private void agregarListenerDobleClic() {
        tvComunicados.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                Map<String, Object> seleccionado = tvComunicados.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    mostrarDetalle(seleccionado);
                }
            }
        });
    }

    /**
     * Muestra una alerta con el contenido completo del comunicado.
     */
    private void mostrarDetalle(Map<String, Object> item) {
        String titulo = (String) item.get("titulo");
        String contenido = (String) item.get("contenido");
        String autor = (String) item.get("autor");
        Timestamp fecha = (Timestamp) item.get("fechaPublicacion");
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso Institucional");
        alert.setHeaderText(titulo + "\nPublicado por: " + autor + " (" + fecha + ")");
        
        // TextArea de solo lectura para contenido largo
        TextArea textArea = new TextArea(contenido);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true); 
        alert.showAndWait();
    }

    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }
}