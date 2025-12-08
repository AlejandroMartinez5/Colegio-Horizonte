package proyectoSeguridad.controlador;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent; // NECESARIO
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // NECESARIO
import javafx.fxml.Initializable;
import javafx.scene.Parent; // NECESARIO
import javafx.scene.Scene; // NECESARIO
import javafx.scene.control.Alert.AlertType; // NECESARIO para Utilidad.mostrarAlertaSimple
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.stage.Stage; // NECESARIO
import java.io.IOException; // NECESARIO
import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.dao.PagoDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.Pago;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;
import proyectoSeguridad.utilidades.Utilidad; // Asumo que esta clase maneja las alertas

public class FXMLAdministradorRegistrarPagoController implements Initializable {

    @FXML
    private ComboBox<Alumno> comboAlumnos;
    @FXML
    private DatePicker datePickerPago;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private ImageView btn;
    @FXML
    private Label lbMensaje;

    // Suponiendo que se añade un botón de Volver/Regresar en el FXML
    @FXML
    private Button btnVolver;
    
    // Valores fijos
    private static final double MONTO_INSCRIPCION = 1312.50;
    private static final String CONCEPTO_INSCRIPCION = "Inscripción";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarAlumnos();
    }

    private void cargarAlumnos() {
        try {
            // Obtenemos SOLO los alumnos con estado de inscripción "Pendiente"
            List<Alumno> alumnos = AlumnoDAO.obtenerAlumnosPendientes(); 
            comboAlumnos.setItems(FXCollections.observableArrayList(alumnos));

            // Mostrar solo la matrícula en el ComboBox (ya implementado correctamente)
            comboAlumnos.setCellFactory(lv -> new ListCell<Alumno>() {
                @Override
                protected void updateItem(Alumno item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getMatricula());
                }
            });

            comboAlumnos.setButtonCell(new ListCell<Alumno>() {
                @Override
                protected void updateItem(Alumno item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getMatricula());
                }
            });

        } catch (Exception e) {
            lbMensaje.setText("Error al cargar alumnos: " + e.getMessage());
            // Usar Utilidad para mostrar el error globalmente
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Carga", "No se pudieron cargar los alumnos pendientes.");
        }
    }

    @FXML
    private void registrarPago() {
        lbMensaje.setText("");

        Alumno alumnoSeleccionado = comboAlumnos.getValue();
        String fecha = (datePickerPago.getValue() != null) ? datePickerPago.getValue().toString() : "";

        if (alumnoSeleccionado == null) {
            lbMensaje.setText("Seleccione un alumno.");
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Selección Requerida", "Debe seleccionar un alumno para registrar el pago.");
            return;
        }
        if (fecha.isEmpty()) {
            lbMensaje.setText("Seleccione la fecha del pago.");
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Fecha Requerida", "Debe seleccionar la fecha del pago.");
            return;
        }

        // Crear objeto Pago con valores fijos
        Pago pago = new Pago();
        pago.setIdAlumno(alumnoSeleccionado.getIdAlumno());
        pago.setMonto(MONTO_INSCRIPCION);
        pago.setConcepto(CONCEPTO_INSCRIPCION);
        pago.setFechaPago(fecha);

        boolean exito = PagoDAO.agregarPago(pago);

        if (exito) {
            try {
                // Actualizar estado del alumno a "Pagado"
                ResultadoOperacion resultado = AlumnoDAO.actualizarEstadoInscripcion(alumnoSeleccionado.getIdAlumno(), "Pagado");
                
                if (!resultado.isError()) {
                    lbMensaje.setText("Pago registrado y alumno actualizado a Pagado.");
                    Utilidad.mostrarAlertaSimple(AlertType.INFORMATION, "Éxito", "Pago registrado y alumno actualizado.");
                    limpiarCampos();
                    cargarAlumnos(); // refrescar ComboBox
                } else {
                    lbMensaje.setText("Pago registrado, pero no se pudo actualizar el alumno.");
                    Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Registro Parcial", "Pago registrado, pero falló la actualización del estado del alumno.");
                }
            } catch (Exception e) {
                lbMensaje.setText("Pago registrado, pero error al actualizar alumno: " + e.getMessage());
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Sistema", "Pago registrado, pero ocurrió un error crítico al actualizar el estado del alumno.");
            }
        } else {
            lbMensaje.setText("Error al registrar el pago.");
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Registro", "Fallo al guardar el pago en la base de datos.");
        }
    }

    private void limpiarCampos() {
        comboAlumnos.getSelectionModel().clearSelection();
        datePickerPago.setValue(null);
    }

    // --- Métodos de Navegación ---

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
}