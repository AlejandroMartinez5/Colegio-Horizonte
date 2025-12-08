package proyectoSeguridad.controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.dao.CursoDAO;
import proyectoSeguridad.modelo.pojo.Curso;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLDocenteConsultarAlumnosController implements Initializable {

    @FXML
    private Button btnRegresar;
    @FXML
    private Button btnExportarPDF;
    @FXML
    private ComboBox<String> cbCursos;
    
    // Cambiamos a Map para mayor flexibilidad sin tocar el POJO
    @FXML
    private TableView<Map<String, Object>> tvAlumnos;
    @FXML
    private TableColumn<Map<String, Object>, String> colMatricula;
    @FXML
    private TableColumn<Map<String, Object>, String> colNombre;
    @FXML
    private TableColumn<Map<String, Object>, String> colApellido;
    @FXML
    private TableColumn<Map<String, Object>, String> colCalificacion; // Nueva columna

    private int idDocente;
    private Map<String, Integer> mapaCursos;
    private ObservableList<Map<String, Object>> listaAlumnos;
    
    private String nombreCursoActual = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        mapaCursos = new HashMap<>();
        listaAlumnos = FXCollections.observableArrayList();
        configurarTabla();
    }
    
    public void setDocente(int idDocente) {
        this.idDocente = idDocente;
        cargarCursosDocente();
    }

    private void configurarTabla() {
        // Configuramos las columnas para leer del Mapa
        colMatricula.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("matricula")));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("nombre")));
        colApellido.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("apellido")));
        colCalificacion.setCellValueFactory(data -> new SimpleStringProperty((String) data.getValue().get("calificacion")));
        
        tvAlumnos.setItems(listaAlumnos);
        tvAlumnos.setPlaceholder(new javafx.scene.control.Label("Seleccione un curso para ver los alumnos."));
    }

    private void cargarCursosDocente() {
        try {
            List<Curso> cursos = CursoDAO.obtenerCursosPorIdDocente(this.idDocente);
            ObservableList<String> itemsCombo = FXCollections.observableArrayList();
            
            if (cursos.isEmpty()) {
                cbCursos.setPromptText("No tienes cursos asignados.");
            } else {
                for (Curso curso : cursos) {
                    String etiqueta = curso.getNombreMateria() + " (" + curso.getClaveCurso() + ")";
                    itemsCombo.add(etiqueta);
                    mapaCursos.put(etiqueta, curso.getIdCurso());
                }
                cbCursos.setItems(itemsCombo);
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error de Conexión", "No se pudieron cargar los cursos del docente.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void seleccionarCurso(ActionEvent event) {
        String seleccion = cbCursos.getSelectionModel().getSelectedItem();
        
        if (seleccion != null) {
            this.nombreCursoActual = seleccion; 
            Integer idCursoSeleccionado = mapaCursos.get(seleccion);
            if (idCursoSeleccionado != null) {
                cargarAlumnosDelCurso(idCursoSeleccionado);
            }
        }
    }
    
    private void cargarAlumnosDelCurso(int idCurso) {
        listaAlumnos.clear();
        try {
            // Usamos la NUEVA consulta del DAO
            List<Map<String, Object>> alumnosObtenidos = AlumnoDAO.obtenerAlumnosConCalificacion(idCurso);
            
            if (alumnosObtenidos != null && !alumnosObtenidos.isEmpty()) {
                listaAlumnos.addAll(alumnosObtenidos);
            } else {
                tvAlumnos.setPlaceholder(new javafx.scene.control.Label("Este curso no tiene alumnos inscritos aún."));
            }
        } catch (SQLException ex) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error", "No se pudo cargar la lista de alumnos.");
            ex.printStackTrace();
        }
    }

    @FXML
    private void clicBotonExportarPDF(ActionEvent event) {
        if (listaAlumnos.isEmpty()) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.WARNING, "Sin datos", "No hay alumnos en la lista para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Lista de Alumnos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_" + nombreCursoActual.replace(" ", "_").replace("(", "").replace(")", "") + ".pdf");
        
        Stage stage = (Stage) btnExportarPDF.getScene().getWindow();
        File archivo = fileChooser.showSaveDialog(stage);

        if (archivo != null) {
            generarDocumentoPDF(archivo);
        }
    }

    private void generarDocumentoPDF(File archivo) {
        Document documento = new Document();
        
        try {
            PdfWriter.getInstance(documento, new FileOutputStream(archivo));
            documento.open();

            // Título
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Reporte de Calificaciones - Colegio Horizonte", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            
            // Subtítulo
            Paragraph subtitulo = new Paragraph("Curso: " + nombreCursoActual);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            documento.add(subtitulo);

            // Tabla PDF
            PdfPTable tabla = new PdfPTable(4); 
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{2f, 4f, 4f, 2f}); 
            
            Font fontCabecera = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            agregarCeldaCabecera(tabla, "Matrícula", fontCabecera);
            agregarCeldaCabecera(tabla, "Nombre", fontCabecera);
            agregarCeldaCabecera(tabla, "Apellidos", fontCabecera);
            agregarCeldaCabecera(tabla, "Calificación", fontCabecera);

            // Datos desde el Mapa
            for (Map<String, Object> alumno : listaAlumnos) {
                // Se asume que nombre y apellido están en campos separados en el mapa
                tabla.addCell((String) alumno.get("matricula"));
                tabla.addCell((String) alumno.get("nombre"));
                tabla.addCell((String) alumno.get("apellido"));
                tabla.addCell((String) alumno.get("calificacion"));
            }

            documento.add(tabla);
            
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("Generado el: " + new java.util.Date().toString()));

            documento.close();
            
            Utilidad.mostrarAlertaSimple(Alert.AlertType.INFORMATION, "Éxito", "Reporte PDF guardado correctamente.");

        } catch (DocumentException | FileNotFoundException e) {
            Utilidad.mostrarAlertaSimple(Alert.AlertType.ERROR, "Error al Exportar", "Error al crear PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void agregarCeldaCabecera(PdfPTable tabla, String texto, Font fuente) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(5);
        tabla.addCell(celda);
    }

    /**
     * CIERRE DE VENTANA: Cierra la Stage actual para regresar a la vista anterior (Menú del Docente).
     */
    @FXML
    private void clicBotonRegresar(ActionEvent event) {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }
}