package proyectoSeguridad.controlador;

import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage; 
import java.io.IOException; 
import proyectoSeguridad.modelo.dao.CursoDAO;
import proyectoSeguridad.modelo.dao.DocenteDAO;
import proyectoSeguridad.modelo.dao.HorarioDAO;
import proyectoSeguridad.modelo.pojo.Curso;
import proyectoSeguridad.modelo.pojo.Horario;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdministradorRegistrarCursoController implements Initializable {

    @FXML private Button btnCerrarSesion;
    @FXML private TextField tfNombreMateria;
    @FXML private TextField tfClaveCurso;
    @FXML private ComboBox<String> cbDocente;
    @FXML private Button btnRegistrarCurso;
    @FXML private Label lbMensaje;
    @FXML private ComboBox<String> cbDiaSemana;
    @FXML private TextField tfHoraInicio;
    @FXML private TextField tfHoraFin;
    @FXML private TextField tfAula;
    @FXML private Button btnAgregarHorario;
    @FXML private Button btnQuitarHorario;
    
    @FXML private TableView<Map<String, String>> tvHorarios;
    @FXML private TableColumn<Map<String, String>, String> colDia;
    @FXML private TableColumn<Map<String, String>, String> colInicio;
    @FXML private TableColumn<Map<String, String>, String> colFin;
    @FXML private TableColumn<Map<String, String>, String> colAula;
    @FXML private TableColumn<Map<String, String>, String> colAccion;

    @FXML private Button btnVolver;

    private Map<String, Integer> mapaDocentes;
    private ObservableList<Map<String, String>> listaHorariosTemporales; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapaDocentes = new HashMap<>();
        listaHorariosTemporales = FXCollections.observableArrayList();
        configurarTablaHorarios();
        cargarDocentes();
    }

    
    private void configurarTablaHorarios() {
        colDia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("dia")));
        colInicio.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("inicio")));
        colFin.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("fin")));
        colAula.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("aula")));
        
        tvHorarios.setItems(listaHorariosTemporales);
    }

    private void cargarDocentes() {
        try {
            List<Map<String, Object>> listaDocentesInfo = DocenteDAO.obtenerDocentesParaComboBox();
            
            if (listaDocentesInfo != null && !listaDocentesInfo.isEmpty()) {
                ObservableList<String> items = FXCollections.observableArrayList();
                
                for (Map<String, Object> docenteInfo : listaDocentesInfo) {
                    String nombreCompleto = (String) docenteInfo.get("NombreCompleto");
                    Integer idDocente = (Integer) docenteInfo.get("ID_Docente");
                    
                    items.add(nombreCompleto);
                    mapaDocentes.put(nombreCompleto, idDocente);
                }
                
                cbDocente.setItems(items);
            } else {
                cbDocente.setPromptText("No hay docentes disponibles");
                Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Advertencia", "No se encontraron docentes registrados en el sistema.");
            }
            
        } catch (SQLException e) {
            lbMensaje.setText("Error de conexión al cargar docentes.");
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos para cargar los docentes.");
            e.printStackTrace();
        }
    }


    @FXML
    private void clicBotonAgregarHorario(ActionEvent event) {
        String dia = cbDiaSemana.getSelectionModel().getSelectedItem();
        String inicio = tfHoraInicio.getText().trim();
        String fin = tfHoraFin.getText().trim();
        String aula = tfAula.getText().trim();
        
        if (dia == null || inicio.isEmpty() || fin.isEmpty()) {
            lbMensaje.setText("Día, Hora Inicio y Hora Fin son obligatorios para el horario.");
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Horario Incompleto", "Por favor, selecciona el día e ingresa las horas.");
            return;
        }

        if (!validarFormatoHora(inicio) || !validarFormatoHora(fin)) {
            lbMensaje.setText("El formato de hora debe ser HH:MM (ej. 07:00).");
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Formato Inválido", "Asegúrate de que las horas están en formato HH:MM (ej. 14:30).");
            return;
        }

        Map<String, String> horario = new HashMap<>();
        horario.put("dia", dia);
        horario.put("inicio", inicio + ":00"); 
        horario.put("fin", fin + ":00");
        horario.put("aula", aula.isEmpty() ? "Sin Asignar" : aula);

        listaHorariosTemporales.add(horario);

        cbDiaSemana.getSelectionModel().clearSelection();
        tfHoraInicio.clear();
        tfHoraFin.clear();
        tfAula.clear();
        lbMensaje.setText("Horario temporal añadido. ¡No olvide registrar el curso!");
    }
    
    @FXML
    private void clicBotonQuitarHorario(ActionEvent event) {
        Map<String, String> seleccionado = tvHorarios.getSelectionModel().getSelectedItem();
        
        if (seleccionado != null) {
            if (Utilidad.mostrarAlertaConfirmacion("Confirmar Eliminación", "¿Está seguro de quitar este horario?")) {
                listaHorariosTemporales.remove(seleccionado);
                lbMensaje.setText("Horario quitado de la lista temporal.");
            }
        } else {
            lbMensaje.setText("Debe seleccionar un horario de la tabla para quitar.");
        }
    }

    @FXML
    private void clicBotonRegistrarCurso(ActionEvent event) {
        String nombreMateria = tfNombreMateria.getText();
        String claveCurso = tfClaveCurso.getText();
        String docenteSeleccionado = cbDocente.getSelectionModel().getSelectedItem();

        if (nombreMateria.isEmpty() || claveCurso.isEmpty() || docenteSeleccionado == null) {
            lbMensaje.setText("Todos los detalles del curso (Nombre, Clave, Docente) son obligatorios.");
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Campos Incompletos", "Debe llenar Nombre, Clave y seleccionar Docente.");
            return;
        }

        if (listaHorariosTemporales.isEmpty()) {
            lbMensaje.setText("Debe agregar al menos un horario al curso.");
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Horario Requerido", "Un curso debe tener al menos un horario asignado.");
            return;
        }

        Integer idDocente = mapaDocentes.get(docenteSeleccionado);
        
        try {
            Curso cursoNuevo = new Curso(0, nombreMateria, claveCurso, idDocente); 
            boolean cursoRegistrado = CursoDAO.registrarCurso(cursoNuevo); 
            
            if (cursoRegistrado) {
                Curso cursoCompleto = CursoDAO.obtenerCursoPorClave(claveCurso);
                
                if (cursoCompleto != null) {
                    int idCursoGenerado = cursoCompleto.getIdCurso();
                    boolean horariosRegistrados = registrarMultiplesHorarios(idCursoGenerado);
                    
                    if (horariosRegistrados) {
                        lbMensaje.setText("¡Curso y Horarios registrados con éxito!");
                        Utilidad.mostrarAlertaSimple(AlertType.INFORMATION, "Registro Completo", "El curso y sus horarios fueron guardados.");
                        limpiarCampos();
                    } else {
                        lbMensaje.setText("Curso registrado, pero falló el registro de uno o más horarios.");
                        Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Registro Parcial", "El curso se registró, pero ocurrió un error con los horarios.");
                        limpiarCampos(); 
                    }
                } else {
                    lbMensaje.setText("Error crítico al recuperar el ID del curso recién creado.");
                }
                
            } else {
                lbMensaje.setText("Error: Fallo al insertar el curso. Clave duplicada.");
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Registro", "No se pudo registrar el curso. Verifique la clave del curso.");
            }

        } catch (SQLException e) {
            lbMensaje.setText("Error de Base de Datos.");
            if (e.getSQLState().startsWith("23")) { 
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Datos", "La clave de curso ya está en uso.");
            } else {
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Base de Datos", "Ocurrió un error grave en la base de datos.");
            }
            e.printStackTrace();
        }
    }
    
    private boolean registrarMultiplesHorarios(int idCurso) {
        boolean exitoTotal = true;
        
        for (Map<String, String> tempHorario : listaHorariosTemporales) {
            Horario horario = new Horario();
            horario.setIdCurso(idCurso);
            horario.setDiaSemana(tempHorario.get("dia"));
            horario.setHoraInicio(tempHorario.get("inicio"));
            horario.setHoraFin(tempHorario.get("fin"));
            horario.setAula(tempHorario.get("aula"));
            
            ResultadoOperacion resultado = HorarioDAO.registrarHorario(horario);
            
            if (resultado.isError()) {
                System.err.println("Error al registrar horario: " + resultado.getMensaje());
                exitoTotal = false;
            }
        }
        return exitoTotal;
    }
    
    private boolean validarFormatoHora(String hora) {
        Pattern pattern = Pattern.compile("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");
        Matcher matcher = pattern.matcher(hora);
        return matcher.matches();
    }

    private void limpiarCampos() {
        tfNombreMateria.setText("");
        tfClaveCurso.setText("");
        cbDocente.getSelectionModel().clearSelection();
        
        tfHoraInicio.clear();
        tfHoraFin.clear();
        tfAula.clear();
        cbDiaSemana.getSelectionModel().clearSelection();
        
        listaHorariosTemporales.clear();
        lbMensaje.setText("");
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
}