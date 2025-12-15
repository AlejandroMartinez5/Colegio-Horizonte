package proyectoSeguridad.controlador;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; 
import javafx.fxml.Initializable;
import javafx.scene.Parent; 
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage; 
import proyectoSeguridad.modelo.dao.HorarioDAO;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLDocenteHorarioController implements Initializable {

    @FXML private Button btnCerrarSesion;
    @FXML private TabPane tpDiasSemana;

    @FXML private TableView<Map<String, Object>> tvLunes;
    @FXML private TableColumn<Map<String, Object>, String> colMateriaLunes;
    @FXML private TableColumn<Map<String, Object>, String> colHoraInicioLunes;
    @FXML private TableColumn<Map<String, Object>, String> colHoraFinLunes;
    @FXML private TableColumn<Map<String, Object>, String> colAulaLunes;
    @FXML private TableColumn<Map<String, Object>, String> colClaveLunes;

    @FXML private TableView<Map<String, Object>> tvMartes;
    @FXML private TableColumn<Map<String, Object>, String> colMateriaMartes;
    @FXML private TableColumn<Map<String, Object>, String> colHoraInicioMartes;
    @FXML private TableColumn<Map<String, Object>, String> colHoraFinMartes;
    @FXML private TableColumn<Map<String, Object>, String> colAulaMartes;
    @FXML private TableColumn<Map<String, Object>, String> colClaveMartes;

    @FXML private TableView<Map<String, Object>> tvMiercoles;
    @FXML private TableColumn<Map<String, Object>, String> colMateriaMiercoles;
    @FXML private TableColumn<Map<String, Object>, String> colHoraInicioMiercoles;
    @FXML private TableColumn<Map<String, Object>, String> colHoraFinMiercoles;
    @FXML private TableColumn<Map<String, Object>, String> colAulaMiercoles;
    @FXML private TableColumn<Map<String, Object>, String> colClaveMiercoles;

    @FXML private TableView<Map<String, Object>> tvJueves;
    @FXML private TableColumn<Map<String, Object>, String> colMateriaJueves;
    @FXML private TableColumn<Map<String, Object>, String> colHoraInicioJueves;
    @FXML private TableColumn<Map<String, Object>, String> colHoraFinJueves;
    @FXML private TableColumn<Map<String, Object>, String> colAulaJueves;
    @FXML private TableColumn<Map<String, Object>, String> colClaveJueves;

    @FXML private TableView<Map<String, Object>> tvViernes;
    @FXML private TableColumn<Map<String, Object>, String> colMateriaViernes;
    @FXML private TableColumn<Map<String, Object>, String> colHoraInicioViernes;
    @FXML private TableColumn<Map<String, Object>, String> colHoraFinViernes;
    @FXML private TableColumn<Map<String, Object>, String> colAulaViernes;
    @FXML private TableColumn<Map<String, Object>, String> colClaveViernes;

    private ObservableList<Map<String, Object>> listaLunes = FXCollections.observableArrayList();
    private ObservableList<Map<String, Object>> listaMartes = FXCollections.observableArrayList();
    private ObservableList<Map<String, Object>> listaMiercoles = FXCollections.observableArrayList();
    private ObservableList<Map<String, Object>> listaJueves = FXCollections.observableArrayList();
    private ObservableList<Map<String, Object>> listaViernes = FXCollections.observableArrayList();
    
    private int idDocenteLogueado = 1; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTodasLasTablas();
        cargarHorarios();
    }


    private void configurarTodasLasTablas() {
        configurarColumnas(colMateriaLunes, colHoraInicioLunes, colHoraFinLunes, colAulaLunes, colClaveLunes);
        configurarColumnas(colMateriaMartes, colHoraInicioMartes, colHoraFinMartes, colAulaMartes, colClaveMartes);
        configurarColumnas(colMateriaMiercoles, colHoraInicioMiercoles, colHoraFinMiercoles, colAulaMiercoles, colClaveMiercoles);
        configurarColumnas(colMateriaJueves, colHoraInicioJueves, colHoraFinJueves, colAulaJueves, colClaveJueves);
        configurarColumnas(colMateriaViernes, colHoraInicioViernes, colHoraFinViernes, colAulaViernes, colClaveViernes);
        
        tvLunes.setItems(listaLunes);
        tvMartes.setItems(listaMartes);
        tvMiercoles.setItems(listaMiercoles);
        tvJueves.setItems(listaJueves);
        tvViernes.setItems(listaViernes);
    }

    private void configurarColumnas(TableColumn<Map<String, Object>, String> colMateria,
                                    TableColumn<Map<String, Object>, String> colInicio,
                                    TableColumn<Map<String, Object>, String> colFin,
                                    TableColumn<Map<String, Object>, String> colAula,
                                    TableColumn<Map<String, Object>, String> colClave) {
        
        colMateria.setCellValueFactory(data -> new SimpleObjectProperty<>((String) data.getValue().get("nombreMateria")));
        colInicio.setCellValueFactory(data -> new SimpleObjectProperty<>((String) data.getValue().get("horaInicio")));
        colFin.setCellValueFactory(data -> new SimpleObjectProperty<>((String) data.getValue().get("horaFin")));
        colAula.setCellValueFactory(data -> new SimpleObjectProperty<>((String) data.getValue().get("aula")));
        colClave.setCellValueFactory(data -> new SimpleObjectProperty<>((String) data.getValue().get("claveCurso")));
    }

    private void cargarHorarios() {
        // Limpiar listas
        listaLunes.clear();
        listaMartes.clear();
        listaMiercoles.clear();
        listaJueves.clear();
        listaViernes.clear();

        try {
            List<Map<String, Object>> todosHorarios = HorarioDAO.obtenerHorariosDeDocente(idDocenteLogueado);
    
            for (Map<String, Object> horario : todosHorarios) {
                String dia = (String) horario.get("diaSemana");
                if (dia == null) continue;
    
                switch (dia.toLowerCase()) {
                    case "lunes": listaLunes.add(horario); break;
                    case "martes": listaMartes.add(horario); break;
                    case "miércoles":
                    case "miercoles": listaMiercoles.add(horario); break;
                    case "jueves": listaJueves.add(horario); break;
                    case "viernes": listaViernes.add(horario); break;
                }
            }
        } catch (Exception e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Carga", "No se pudieron cargar los horarios del docente.");
            e.printStackTrace();
        }
    }

    
    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (Utilidad.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Desea cerrar la sesión y volver al login?")) {
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

    public void setDocente(int idDocente) {
        this.idDocenteLogueado = idDocente;
        cargarHorarios(); 
    }
}