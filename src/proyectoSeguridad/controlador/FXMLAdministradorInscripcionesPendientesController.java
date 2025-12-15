package proyectoSeguridad.controlador;

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
import javafx.scene.Parent; 
import javafx.scene.Scene; 
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import proyectoSeguridad.modelo.dao.AlumnoDAO;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.utilidades.Utilidad;
import javafx.scene.control.Alert.AlertType;
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
import java.io.IOException; 

public class FXMLAdministradorInscripcionesPendientesController implements Initializable {
    
    private static final String RUTA_LOGO = "D:\\Documents\\Capa\\NetBeanss\\Proyecto_Seguridad\\src\\proyectoSeguridad\\recursos\\LogoColegio.png";

    @FXML private Button btnCerrarSesion;
    @FXML private TableView<Alumno> tvAlumnosPendientes;
    @FXML private TableColumn<Alumno, String> colMatricula;
    @FXML private TableColumn<Alumno, String> colNombre;
    @FXML private Label lbReloj;
    @FXML private Label lbNombre;
    @FXML private ImageView btn;
    @FXML private Button btnExportarPDF; // NUEVO ATRIBUTO FXML

    private ObservableList<Alumno> listaAlumnosPendientes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Utilidad.mostrarHora(lbReloj);

        try {
            cargarAlumnosPendientes();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utilidad.mostrarAlertaSimple(
                AlertType.ERROR,
                "Error base de datos",
                "No se pudieron cargar los alumnos pendientes: " + ex.getMessage()
            );
        }
    }

    private void cargarAlumnosPendientes() throws SQLException {
        // Columna matrícula
        colMatricula.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMatricula())
        );

        // Columna nombre completo usando el método de AlumnoDAO
        colNombre.setCellValueFactory(cellData -> {
            Alumno alumno = cellData.getValue();
            try {
                String nombreCompleto = AlumnoDAO.obtenerNombreCompleto(alumno.getIdUsuario());
                return new javafx.beans.property.SimpleStringProperty(nombreCompleto);
            } catch (SQLException e) {
                e.printStackTrace();
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });

        // Cargar lista de alumnos pendientes
        List<Alumno> lista = AlumnoDAO.obtenerAlumnosPendientes();
        listaAlumnosPendientes = FXCollections.observableArrayList(lista);
        tvAlumnosPendientes.setItems(listaAlumnosPendientes);
    }
    
    // --- Lógica de Exportación a PDF (Incluye Logo) ---

    @FXML
    private void clicExportarPDF(ActionEvent event) {
        if (listaAlumnosPendientes == null || listaAlumnosPendientes.isEmpty()) {
            Utilidad.mostrarAlertaSimple(AlertType.WARNING, "Advertencia", "No hay alumnos pendientes para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Inscripciones Pendientes");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        Stage stage = (Stage) btnExportarPDF.getScene().getWindow();
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

        try {
            Image img = Image.getInstance(RUTA_LOGO);
            img.scaleAbsolute(50, 50); 
            img.setAbsolutePosition(
                document.getPageSize().getWidth() - document.rightMargin() - 60, 
                document.getPageSize().getHeight() - document.topMargin() - 50  
            );
            document.add(img);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo cargar el logo del colegio. Verifique la ruta: " + RUTA_LOGO);
        }
        // ----------------- FIN LOGO -----------------

        // Fuentes y Estilos
        Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        // Título
        Paragraph titulo = new Paragraph("REPORTE DE INSCRIPCIONES PENDIENTES", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        
        document.add(new Paragraph(" ")); 
        document.add(new Paragraph(" ")); 
        document.add(titulo);
        document.add(new Paragraph(" "));
        
        document.add(new Paragraph("Fecha de Generación: " + Utilidad.obtenerFechaActualFormato()));
        document.add(new Paragraph("Total de Alumnos Pendientes: " + listaAlumnosPendientes.size()));
        document.add(new Paragraph(" "));

        // Tabla (2 columnas: Matrícula, Nombre Completo)
        PdfPTable table = new PdfPTable(2); 
        table.setWidthPercentage(100); 
        table.setSpacingBefore(10f);
        
        // Ajuste de ancho de columnas
        float[] columnWidths = {2.5f, 5.0f};
        table.setWidths(columnWidths);

        // Encabezados de la tabla
        agregarHeader(table, "Matrícula", fontHeader);
        agregarHeader(table, "Nombre Completo", fontHeader);

        // Llenar la tabla con los datos
        for (Alumno alumno : listaAlumnosPendientes) {
            String nombreCompleto = "";
            try {
                nombreCompleto = AlumnoDAO.obtenerNombreCompleto(alumno.getIdUsuario());
            } catch (SQLException e) {
                nombreCompleto = "Error al obtener nombre";
            }
            
            table.addCell(alumno.getMatricula());
            table.addCell(nombreCompleto);
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

    // --- Lógica de Navegación (Cerrar Sesión) ---

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
}