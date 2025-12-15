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

    private ObservableList<Map<String, Object>> listaComunicados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaComunicados = FXCollections.observableArrayList();
        configurarTabla();
        cargarComunicados();
        agregarListenerDobleClic();
    }
    
    private void configurarTabla() {
        colTitulo.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("titulo")));
        colAutor.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("autor")));

        colContenido.setCellValueFactory(data -> {
            String contenido = (String) data.getValue().get("contenido");
            if (contenido != null && contenido.length() > 50) {
                return new SimpleStringProperty(contenido.substring(0, 50) + "...");
            }
            return new SimpleStringProperty(contenido);
        });

        colFecha.setCellValueFactory(data -> {
            Timestamp ts = (Timestamp) data.getValue().get("fechaPublicacion");
            String fechaStr = ts != null ? ts.toString().substring(0, 16) : "N/A";
            return new SimpleStringProperty(fechaStr);
        });
        
        tvComunicados.setItems(listaComunicados);
    }

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

    private void mostrarDetalle(Map<String, Object> item) {
        String titulo = (String) item.get("titulo");
        String contenido = (String) item.get("contenido");
        String autor = (String) item.get("autor");
        Timestamp fecha = (Timestamp) item.get("fechaPublicacion");
        
        String fechaStr = fecha != null ? fecha.toString().substring(0, 16) : "N/A";
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Comunicado: " + titulo);
        alert.setHeaderText(titulo + "\nPublicado por: " + autor + " el " + fechaStr);

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