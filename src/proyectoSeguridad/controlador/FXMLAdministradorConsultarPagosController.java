package proyectoSeguridad.controlador;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
    
    @FXML
    private Button btnVolver; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIdPago.setCellValueFactory(new PropertyValueFactory<>("idPago"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
    }    

    
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

    private List<Pago> obtenerUltimosPagos(int idAlumno, int cantidad) {
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

    @FXML
    private void clicBotonVolver(ActionEvent event) {
        Stage stageActual = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stageActual.close();
    }
}