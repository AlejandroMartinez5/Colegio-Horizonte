/*
 * Alejandro Martinez Ramirez
 * 
 */
package proyectoSeguridad.utilidades;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Utilidad {
    
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public static void mostrarAlertaSimple(AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
    
    public static boolean mostrarAlertaConfirmacion(String titulo, String mensaje) {
        Alert alertaConfirmacion = new Alert(AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle(titulo);
        alertaConfirmacion.setHeaderText(mensaje);
        
        ButtonType botonSi = new ButtonType("Sí");
        ButtonType botonNo = new ButtonType("No");
        alertaConfirmacion.getButtonTypes().setAll(botonSi, botonNo);
        
        return alertaConfirmacion.showAndWait().get() == botonSi;
    }
    
    public static Stage getEscenario(Control componente) {
        return (Stage) componente.getScene().getWindow();
    }
    
    public static void mostrarHora(Label lbReloj) {
        Timeline reloj = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                LocalDateTime ahora = LocalDateTime.now();
                LocalDateTime horaAjustada = ahora.minusHours(1);
                lbReloj.setText(horaAjustada.format(FORMATO_HORA));
            }),
            new KeyFrame(Duration.minutes(1))
        );
        reloj.setCycleCount(Timeline.INDEFINITE);
        reloj.play();
    }
    
    public static String obtenerFechaActualFormato() {
        // Define el formato deseado
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // Obtiene la fecha y hora actual
        LocalDateTime now = LocalDateTime.now();
        
        // Retorna la fecha formateada
        return dtf.format(now);
    }
    
    public static String calcularHash(String contrasena) {
        try {
            // Usamos SHA-256, un estándar seguro para proyectos académicos/medianos
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            
            // Convertimos los bytes a representación Hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}