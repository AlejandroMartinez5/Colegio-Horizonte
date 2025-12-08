package proyectoSeguridad.modelo.pojo;

import java.sql.Timestamp;

public class Comunicado {
    
    private int idComunicado;
    private int idUsuarioAdmin; // ID del usuario que publica el comunicado
    private String titulo;
    private String contenido;
    private Timestamp fechaPublicacion; 

    // Constructor vacío
    public Comunicado() {
    }

    // Constructor completo (útil para consultas)
    public Comunicado(int idComunicado, int idUsuarioAdmin, String titulo, String contenido, Timestamp fechaPublicacion) {
        this.idComunicado = idComunicado;
        this.idUsuarioAdmin = idUsuarioAdmin;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaPublicacion = fechaPublicacion;
    }

    // Constructor para registro (el ID y la Fecha se generan automáticamente en la DB)
    public Comunicado(int idUsuarioAdmin, String titulo, String contenido) {
        this.idUsuarioAdmin = idUsuarioAdmin;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    // --- Getters y Setters ---

    public int getIdComunicado() {
        return idComunicado;
    }

    public void setIdComunicado(int idComunicado) {
        this.idComunicado = idComunicado;
    }

    public int getIdUsuarioAdmin() {
        return idUsuarioAdmin;
    }

    public void setIdUsuarioAdmin(int idUsuarioAdmin) {
        this.idUsuarioAdmin = idUsuarioAdmin;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Timestamp getFechaPublicacion() {
        return fechaPublicacion;
    }
    
    // Para la visualización en la interfaz (formateo simple)
    public String getFechaPublicacionString() {
        return fechaPublicacion != null ? fechaPublicacion.toString().substring(0, 16) : "";
    }

    public void setFechaPublicacion(Timestamp fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }
}