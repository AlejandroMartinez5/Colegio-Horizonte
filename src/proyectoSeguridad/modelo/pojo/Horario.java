package proyectoSeguridad.modelo.pojo;

public class Horario {
    
    private int idHorario;
    private int idCurso;
    private String diaSemana;
    private String horaInicio; // Formato esperado: "HH:mm:ss" o "HH:mm"
    private String horaFin;    // Formato esperado: "HH:mm:ss" o "HH:mm"
    private String aula;


    public Horario() {
    }

    public Horario(int idHorario, int idCurso, String diaSemana, String horaInicio, String horaFin, String aula) {
        this.idHorario = idHorario;
        this.idCurso = idCurso;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.aula = aula;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }
    
    @Override
    public String toString() {
        return diaSemana + ": " + horaInicio + " - " + horaFin + " (Aula: " + (aula != null ? aula : "Sin asignar") + ")";
    }
}