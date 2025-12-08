package proyectoSeguridad.controlador;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.FileChooser; 

// Importaciones para PDF (iText 5.x)
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image; 
import java.io.File;
import java.io.FileOutputStream;


import proyectoSeguridad.modelo.dao.CursoDAO;
import proyectoSeguridad.modelo.pojo.CursoDetalle;
import proyectoSeguridad.utilidades.Utilidad;

public class FXMLAdministradorConsultarCursosController implements Initializable {
    
    // RUTA DEL LOGO (Actualizada con la ruta que proporcionaste)
    private static final String RUTA_LOGO = "D:\\Documents\\Capa\\NetBeanss\\Proyecto_Seguridad\\src\\proyectoSeguridad\\recursos\\LogoColegio.png";

    // Atributos FXML de la tabla
    @FXML private TextField tfBusqueda;
    @FXML private TableView<CursoDetalle> tvCursos;
    @FXML private TableColumn<CursoDetalle, String> colNombreMateria;
    @FXML private TableColumn<CursoDetalle, String> colClaveCurso;
    @FXML private TableColumn<CursoDetalle, String> colNombreDocente; 
    @FXML private TableColumn<CursoDetalle, String> colHorario;     
    
    // Atributos FXML de botones
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnRegistrar;
    @FXML private Button btnBuscar;
    @FXML private Button btnExportarPDF;
    
    // Lista observable para la TableView
    private ObservableList<CursoDetalle> listaCursos;

    // --- Inicialización ---

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaCursos = FXCollections.observableArrayList();
        configurarTabla();  
        cargarCursos(null);
    }

    private void configurarTabla() {
        colNombreMateria.setCellValueFactory(new PropertyValueFactory<>("nombreMateria"));
        colClaveCurso.setCellValueFactory(new PropertyValueFactory<>("claveCurso"));
        colNombreDocente.setCellValueFactory(new PropertyValueFactory<>("nombreDocente"));
        colHorario.setCellValueFactory(new PropertyValueFactory<>("horariosTexto")); 
        
        tvCursos.setItems(listaCursos);
    }
    
    private void cargarCursos(String textoBusqueda) {
        listaCursos.clear();
        try {
            List<CursoDetalle> cursosObtenidos = CursoDAO.obtenerCursosDetallados(textoBusqueda);
            
            if (cursosObtenidos != null && !cursosObtenidos.isEmpty()) {
                listaCursos.addAll(cursosObtenidos);
                tvCursos.setPlaceholder(new Label(""));
            } else {
                tvCursos.setPlaceholder(new Label("No se encontraron cursos con los criterios de búsqueda."));
            }
            
        } catch (SQLException e) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Conexión", "No se pudo conectar a la base de datos para cargar los cursos.");
            e.printStackTrace();
        }
    }

    // --- Métodos de Acción ---

    @FXML
    private void clicBuscarCurso(ActionEvent event) {
        String textoBusqueda = tfBusqueda.getText().trim();
        cargarCursos(textoBusqueda);
    }

    /**
     * CORREGIDO: Abre la ventana de registro de curso en una NUEVA Stage 
     * sin cerrar la ventana actual (Consulta de Cursos).
     * @param event 
     */
    @FXML
    private void clicBotonRegistrarCurso(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLAdministradorRegistrarCurso.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            // 1. Crear una NUEVA Stage
            Stage stageNueva = new Stage();
            stageNueva.setScene(scene);
            stageNueva.setTitle("Registrar Nuevo Curso y Horarios");
            
            // 2. Mostrar la nueva Stage. La ventana actual (Consulta de Cursos) NO se cierra.
            stageNueva.show();
            
        } catch (IOException ex) {
            Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo abrir la ventana de registro de curso. Revise la ruta del FXML.");
            ex.printStackTrace();
        }
    }
    
    // --- Lógica de Exportación a PDF (Incluye Logo) ---

    @FXML
    private void clicExportarPDF(ActionEvent event) {
        if (listaCursos.isEmpty()) {
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Advertencia", "No hay cursos en la tabla para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Cursos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage); 

        if (file != null) {
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

        // ----------------- CARGAR Y AÑADIR LOGO -----------------
        try {
            // Usa la ruta de archivo directo proporcionada por el usuario
            Image img = Image.getInstance(RUTA_LOGO);
            
            // Ajustar tamaño del logo
            img.scaleAbsolute(50, 50);  
            
            // Posicionar el logo (arriba a la derecha)
            img.setAbsolutePosition(
                document.getPageSize().getWidth() - document.rightMargin() - 60, 
                document.getPageSize().getHeight() - document.topMargin() - 50  
            );
            document.add(img);
        } catch (Exception e) {
            // Si el logo falla, continúa generando el PDF sin él
            System.err.println("Advertencia: No se pudo cargar el logo del colegio. Verifique la ruta: " + RUTA_LOGO);
        }
        // ----------------- FIN LOGO -----------------

        // Fuentes y Estilos
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        // Título (Añade espacio para el logo)
        Paragraph titulo = new Paragraph("REPORTE DE CURSOS DETALLADOS", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        
        document.add(new Paragraph(" ")); 
        document.add(new Paragraph(" ")); 
        document.add(titulo);
        document.add(new Paragraph(" "));
        
        // Resumen
        String busqueda = tfBusqueda.getText().trim().isEmpty() ? "Búsqueda: Todos los cursos" : "Búsqueda: " + tfBusqueda.getText().trim();
        document.add(new Paragraph(busqueda));
        document.add(new Paragraph(" "));

        // Tabla (4 columnas)
        PdfPTable table = new PdfPTable(4); 
        table.setWidthPercentage(100); 
        table.setSpacingBefore(10f);
        
        // Ajuste de ancho de columnas
        float[] columnWidths = {2.0f, 1.0f, 1.5f, 3.0f};
        table.setWidths(columnWidths);

        // Encabezados de la tabla
        agregarHeader(table, "Materia", fontHeader);
        agregarHeader(table, "Clave Curso", fontHeader);
        agregarHeader(table, "Docente Asignado", fontHeader);
        agregarHeader(table, "Horario y Aula", fontHeader);

        // Llenar la tabla con los datos
        for (CursoDetalle curso : listaCursos) {
            table.addCell(curso.getNombreMateria());
            table.addCell(curso.getClaveCurso());
            table.addCell(curso.getNombreDocente());
            table.addCell(curso.getHorariosTexto()); 
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
    

    /**
     * Cierra la sesión: abre la ventana de Login y cierra la ventana actual.
     * (Esta lógica ya estaba correcta)
     * @param event 
     */
    @FXML
    private void clicBotonCerrarSesion(ActionEvent event) {
        if (Utilidad.mostrarAlertaConfirmacion("Cerrar Sesión", "¿Está seguro que desea cerrar la sesión y volver al login?")) {
            try {
                Stage escenarioActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectoSeguridad/vista/FXMLInicioSesion.fxml"));
                Parent root = loader.load();
                
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Inicio de Sesión");
                stage.show();
                
                escenarioActual.close();
            } catch (IOException ex) {
                Utilidad.mostrarAlertaSimple(AlertType.ERROR, "Error de Navegación", "No se pudo cerrar la sesión correctamente.");
                ex.printStackTrace();
            }
        }
    }
}