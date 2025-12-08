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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.dao.ComunicadoDAO;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLDocenteComunicadosController implements Initializable {

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

    // Lista para la tabla
    private ObservableList<Map<String, Object>> listaComunicados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaComunicados = FXCollections.observableArrayList();
        configurarTabla();
        cargarComunicados();
        agregarListenerDobleClic();
    }
    
    /**
     * Configura las columnas de la tabla para leer los datos del Mapa.
     */
    private void configurarTabla() {
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("titulo")));
        colAutor.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("autor")));
        
        // Vista previa del contenido (solo los primeros 50 caracteres si es muy largo)
        colContenido.setCellValueFactory(data -> {
            String contenido = (String) data.getValue().get("contenido");
            if (contenido != null && contenido.length() > 50) {
                return new SimpleStringProperty(contenido.substring(0, 50) + "...");
            }
            return new SimpleStringProperty(contenido);
        });

        // Formateo de fecha
        colFecha.setCellValueFactory(data -> {
            Timestamp ts = (Timestamp) data.getValue().get("fechaPublicacion");
            // Formateo para mostrar solo fecha y hora
            String fechaStr = ts != null ? ts.toString().substring(0, 16) : "N/A";
            return new SimpleStringProperty(fechaStr);
        });
        
        tvComunicados.setItems(listaComunicados);
    }

    /**
     * Consulta la base de datos y llena la tabla.
     */
    private void cargarComunicados() {
        try {
            List<Map<String, Object>> resultados = ComunicadoDAO.obtenerComunicadosDetallados();
            if (resultados != null) {
                listaComunicados.addAll(resultados);
            } else {
                tvComunicados.setPlaceholder(new Label("No hay comunicados disponibles."));
            }
        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los comunicados.");
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
     * Muestra una ventana emergente con el contenido completo.
     */
    private void mostrarDetalle(Map<String, Object> item) {
        String titulo = (String) item.get("titulo");
        String contenido = (String) item.get("contenido");
        String autor = (String) item.get("autor");
        Timestamp fecha = (Timestamp) item.get("fechaPublicacion");
        
        String fechaStr = fecha != null ? fecha.toString().substring(0, 16) : "N/A";
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Comunicado: " + titulo);
        alert.setHeaderText(titulo + "\nPublicado por: " + autor + " el " + fechaStr);
        
        // Usamos TextArea para que sea scrollable y copiable
        TextArea textArea = new TextArea(contenido);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true); // Abrir expandido por defecto
        alert.showAndWait();
    }

    /**
     * CIERRE DE VENTANA: Cierra la Stage actual para regresar a la vista anterior (Menú del Docente).
     */
    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        // Obtiene la Stage actual a partir del botón
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        
        // Cierra la Stage, manteniendo la Stage anterior abierta.
        stage.close();
    }
    
}