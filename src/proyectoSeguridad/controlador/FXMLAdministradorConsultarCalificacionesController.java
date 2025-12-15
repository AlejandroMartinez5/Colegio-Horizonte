package proyectoSeguridad.controlador;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// Importaciones para PDF (iText 5.x)
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import proyectoSeguridad.modelo.dao.CalificacionDAO; 
import proyectoSeguridad.modelo.dao.CursoDAO;
import proyectoSeguridad.modelo.pojo.Curso;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdministradorConsultarCalificacionesController implements Initializable {

    // Controles FXML (Solo curso)
    @FXML private Button btnCerrarSesion;
    @FXML private TextField tfBuscarCurso;
    @FXML private ComboBox<String> cbCurso;
    @FXML private Button btnConsultarCurso;
    
    // Controles de la Tabla
    @FXML private TableView<Map<String, Object>> tvCalificaciones; 
    @FXML private TableColumn<Map<String, Object>, String> colNombreMateria;
    @FXML private TableColumn<Map<String, Object>, String> colAlumno;
    @FXML private TableColumn<Map<String, Object>, Double> colPuntaje;
    @FXML private TableColumn<Map<String, Object>, String> colFechaRegistro;
    @FXML private Label lbResumen;

    // Listas y Mapas de datos
    private Map<String, Integer> mapaCursos;
    private ObservableList<String> listaCompletaCursos;
    private ObservableList<Map<String, Object>> listaCalificaciones;

    // --- Inicialización ---

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapaCursos = new HashMap<>();
        listaCompletaCursos = FXCollections.observableArrayList();
        listaCalificaciones = FXCollections.observableArrayList();
        
        configurarTablaConMapas();
        cargarDatosFiltro();
    }

    private void configurarTablaConMapas() {
        
        colNombreMateria.setCellValueFactory(data -> 
            new SimpleObjectProperty<>((String) data.getValue().get("nombreMateria")));
        
        colAlumno.setCellValueFactory(data -> 
            new SimpleObjectProperty<>((String) data.getValue().get("nombreAlumno")));
        
        colPuntaje.setCellValueFactory(data -> 
            new SimpleObjectProperty<>((Double) data.getValue().get("puntaje")));
            
        colFechaRegistro.setCellValueFactory(data -> 
            new SimpleObjectProperty<>((String) data.getValue().get("fechaRegistro")));
        
        tvCalificaciones.setItems(listaCalificaciones);
    }
    
    private void cargarDatosFiltro() {
        try {
            List<Curso> cursos = CursoDAO.obtenerTodosLosCursos();
            for (Curso curso : cursos) {
                String display = curso.getNombreMateria() + " (" + curso.getClaveCurso() + ")";
                listaCompletaCursos.add(display);
                mapaCursos.put(display, curso.getIdCurso());
            }
            cbCurso.setItems(listaCompletaCursos);
            
        } catch (SQLException e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Carga", "No se pudieron cargar los filtros de cursos.");
            e.printStackTrace();
        }
    }

    // --- Lógica de Filtrado (Busca en tiempo real en ComboBox) ---

    @FXML
    private void filtrarCursos() {
        String texto = tfBuscarCurso.getText().toLowerCase();
        ObservableList<String> listaFiltrada = listaCompletaCursos.stream()
            .filter(item -> item.toLowerCase().contains(texto))
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        cbCurso.setItems(listaFiltrada);
        if (!cbCurso.isShowing() && !listaFiltrada.isEmpty()) cbCurso.show(); 
    }
    
    // --- Lógica de Consulta (Solo por Curso) ---

    @FXML
    private void clicConsultar(ActionEvent event) {
        String cursoSeleccionado = cbCurso.getSelectionModel().getSelectedItem();
        
        if (cursoSeleccionado == null) {
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Advertencia", "Debe seleccionar un curso.");
            return;
        }
        
        Integer idCurso = mapaCursos.get(cursoSeleccionado);
        if (idCurso == null) return;

        // idAlumno = 0 (ignorar), idCurso > 0 (filtrar)
        consultarCalificaciones(0, idCurso, cursoSeleccionado); 
    }

    private void consultarCalificaciones(int idAlumno, int idCurso, String filtroDisplay) {
        listaCalificaciones.clear();
        lbResumen.setText("Cargando resultados...");
        
        try {
            // Se usa idAlumno=0 y el DAO se encargará de filtrar solo por idCurso.
            List<Map<String, Object>> calificaciones = CalificacionDAO.obtenerCalificacionesDetalladas(idAlumno, idCurso);
        
            if (calificaciones.isEmpty()) {
                tvCalificaciones.setPlaceholder(new Label("No se encontraron calificaciones para el curso seleccionado."));
                lbResumen.setText("Filtro: " + filtroDisplay + ". Total: 0 calificaciones.");
                return;
            }

            listaCalificaciones.addAll(calificaciones);
            
            // Calcular promedio
            double sumaPuntajes = calificaciones.stream()
                                             .mapToDouble(map -> (Double) map.get("puntaje"))
                                             .sum();
            
            double promedio = calificaciones.size() > 0 ? sumaPuntajes / calificaciones.size() : 0;
            
            tvCalificaciones.setItems(listaCalificaciones);
            lbResumen.setText(String.format("Filtro: %s. Total: %d calificaciones. Promedio: %.2f", 
                                             filtroDisplay, calificaciones.size(), promedio));
        } catch (SQLException e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Consulta", "Ocurrió un error al consultar las calificaciones.");
            e.printStackTrace();
        }
    }
    
    // --- Lógica de Exportación a PDF ---

    @FXML
    private void clicExportarPDF(ActionEvent event) {
        if (listaCalificaciones.isEmpty()) {
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Advertencia", "No hay calificaciones para exportar.");
            return;
        }

        // 1. Abrir diálogo para guardar el archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Calificaciones");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        Stage stage = (Stage) btnConsultarCurso.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage); 

        if (file != null) {
            // Asegurar que la extensión .pdf esté presente
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }
            
            try {
                exportarDatosAPDF(file);
                Utilidad.mostrarAlertaSimple(AlertType.INFORMATION, "Éxito", "Reporte exportado exitosamente a: " + file.getAbsolutePath());
            } catch (Exception e) {
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Exportación", "Ocurrió un error al generar el PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void exportarDatosAPDF(File file) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Fuentes y Estilos
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        // Título
        Paragraph titulo = new Paragraph("REPORTE DE CALIFICACIONES", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" "));
        
        // Resumen
        document.add(new Paragraph(lbResumen.getText()));
        document.add(new Paragraph(" "));

        // Tabla (4 columnas)
        PdfPTable table = new PdfPTable(4); 
        table.setWidthPercentage(100); 
        table.setSpacingBefore(10f);
        
        // Ancho de columnas para mejorar el espacio
        float[] columnWidths = {2.5f, 2.5f, 1.5f, 2.0f};
        table.setWidths(columnWidths);

        // Encabezados de la tabla
        agregarHeader(table, "Materia", fontHeader);
        agregarHeader(table, "Alumno", fontHeader);
        agregarHeader(table, "Puntaje", fontHeader);
        agregarHeader(table, "Fecha Registro", fontHeader);

        // Llenar la tabla con los datos
        for (Map<String, Object> calificacion : listaCalificaciones) {
            table.addCell(calificacion.get("nombreMateria").toString());
            table.addCell(calificacion.get("nombreAlumno").toString());
            table.addCell(String.format("%.2f", (Double) calificacion.get("puntaje"))); 
            table.addCell(calificacion.get("fechaRegistro").toString());
        }

        document.add(table);
        document.close();
    }

    private void agregarHeader(PdfPTable table, String headerText, Font font) {
        PdfPCell header = new PdfPCell(new Phrase(headerText, font));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(header);
    }
    
    // --- Otros Métodos de Acción (Cerrar Sesión) ---

    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (Utilidad.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Está seguro que desea cerrar la sesión?")) {
            try {
                // 1. Cargar el FXML de Inicio de Sesión
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/proyectoSeguridad/vista/FXMLInicioSesion.fxml"
                ));
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
}