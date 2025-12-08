package proyectoSeguridad.modelo.pojo;

public class Calificacion {

    private int idCalificacion;
    private int idAlumno;
    private int idCurso;
    private double puntaje;
    private String fechaRegistro; 

    public Calificacion() {
    }

    public Calificacion(int idCalificacion, int idAlumno, int idCurso, double puntaje, String fechaRegistro) {
        this.idCalificacion = idCalificacion;
        this.idAlumno = idAlumno;
        this.idCurso = idCurso;
        this.puntaje = puntaje;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdCalificacion() {
        return idCalificacion;
    }

    public void setIdCalificacion(int idCalificacion) {
        this.idCalificacion = idCalificacion;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public double getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(double puntaje) {
        this.puntaje = puntaje;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
