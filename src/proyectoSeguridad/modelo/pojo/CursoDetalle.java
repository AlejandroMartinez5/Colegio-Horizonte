package proyectoSeguridad.modelo.pojo;

import java.util.List;

/**
 * POJO de Vista (View Model) para mostrar la información completa del curso en la tabla:
 * Nombre, Clave, Docente, y Horarios asociados.
 */
public class CursoDetalle {
    
    private int idCurso;
    private String nombreMateria;
    private String claveCurso;
    private String nombreDocente; // Nombre resuelto (Docente + Apellido)
    private List<Horario> horarios; // Lista de horarios asociados

    public CursoDetalle() {
    }

    public CursoDetalle(int idCurso, String nombreMateria, String claveCurso, String nombreDocente, List<Horario> horarios) {
        this.idCurso = idCurso;
        this.nombreMateria = nombreMateria;
        this.claveCurso = claveCurso;
        this.nombreDocente = nombreDocente;
        this.horarios = horarios;
    }

    // Getters
    public int getIdCurso() {
        return idCurso;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public String getClaveCurso() {
        return claveCurso;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }
    
    /**
     * Devuelve una cadena legible con todos los horarios.
     * Esto se utiliza para mostrar el horario en una sola columna de la TableView.
     */
    public String getHorariosTexto() {
        if (horarios == null || horarios.isEmpty()) {
            return "Sin Horario Asignado";
        }
        StringBuilder sb = new StringBuilder();
        for (Horario h : horarios) {
            sb.append(h.getDiaSemana())
              .append(": ")
              .append(h.getHoraInicio().substring(0, 5)) // Formato HH:MM
              .append(" - ")
              .append(h.getHoraFin().substring(0, 5))
              .append(" (")
              .append(h.getAula())
              .append("); ");
        }
        // Quita el último "; "
        return sb.toString().trim();
    }
    
    // Setters
    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public void setClaveCurso(String claveCurso) {
        this.claveCurso = claveCurso;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
    }
}