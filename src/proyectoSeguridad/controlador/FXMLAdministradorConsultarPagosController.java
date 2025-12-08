package proyectoSeguridad.controlador;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent; // Importación necesaria para el método volver
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage; // Importación necesaria para el método volver
import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.dao.PagoDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.Pago;

public class FXMLAdministradorConsultarPagosController implements Initializable {

    @FXML
    private TextField txtBuscarMatricula;
    @FXML
    private Button btnBuscar;
    @FXML
    private TableView<Pago> tablaPagos;
    @FXML
    private TableColumn<Pago, Integer> colIdPago;
    @FXML
    private TableColumn<Pago, String> colFecha;
    @FXML
    private TableColumn<Pago, Double> colMonto;
    @FXML
    private TableColumn<Pago, String> colConcepto;
    @FXML
    private Label lbMensaje;
    
    // Suponiendo que agregas este botón a tu FXML para volver a la ventana anterior
    @FXML
    private Button btnVolver; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar las columnas de la tabla
        colIdPago.setCellValueFactory(new PropertyValueFactory<>("idPago"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
    }    

    // --- Lógica de Búsqueda ---
    
    @FXML
    private void buscarPago() {
        lbMensaje.setText("");
        String matricula = txtBuscarMatricula.getText().trim();

        if (matricula.isEmpty()) {
            lbMensaje.setText("Ingrese la matrícula del alumno.");
            return;
        }

        try {
            Alumno alumno = AlumnoDAO.obtenerAlumnoPorMatricula(matricula);
            if (alumno == null) {
                lbMensaje.setText("Alumno no encontrado.");
                tablaPagos.setItems(FXCollections.observableArrayList());
                return;
            }

            // Obtener últimos 5 pagos del alumno
            List<Pago> pagos = obtenerUltimosPagos(alumno.getIdAlumno(), 5);
            if (pagos.isEmpty()) {
                lbMensaje.setText("No se encontraron pagos para este alumno.");
            } else {
                lbMensaje.setText("Mostrando últimos pagos del alumno: " + alumno.getMatricula());
            }

            tablaPagos.setItems(FXCollections.observableArrayList(pagos));

        } catch (Exception e) {
            lbMensaje.setText("Error al buscar pagos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para obtener últimos N pagos
    private List<Pago> obtenerUltimosPagos(int idAlumno, int cantidad) {
        // NOTA: Se asume que PagoDAO.obtenerPagos() obtiene todos los pagos de la BD.
        // Sería más eficiente tener un DAO que filtre directamente por idAlumno y limite el resultado en la consulta SQL.
        List<Pago> pagos = PagoDAO.obtenerPagos(); 
        
        // Filtrar por el alumno
        pagos.removeIf(p -> p.getIdAlumno() != idAlumno);
        
        // Ordenar por fecha descendente
        pagos.sort((p1, p2) -> p2.getFechaPago().compareTo(p1.getFechaPago()));
        
        // Limitar la cantidad
        if (pagos.size() > cantidad) {
            pagos = pagos.subList(0, cantidad);
        }
        return pagos;
    }
    
    // --- Lógica de Navegación (Regreso) ---
    
    /**
     * Cierra la ventana actual para regresar a la ventana que la invocó.
     * Este método se debe usar en un botón de "Regresar" o "Volver" en el FXML.
     * @param event 
     */
    @FXML
    private void clicBotonVolver(ActionEvent event) {
        // Obtiene la Stage (ventana) actual a partir del botón que disparó el evento
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        
        // Cierra la ventana, regresando a la anterior que sigue abierta.
        stageActual.close();
    }
}