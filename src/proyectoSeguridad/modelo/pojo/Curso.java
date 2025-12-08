package proyectoSeguridad.modelo.pojo;

public class Curso {
    
    private int idCurso;
    private String nombreMateria;
    private String claveCurso;
    private int idDocente;

    public Curso() {
    }

    public Curso(int idCurso, String nombreMateria, String claveCurso, int idDocente) {
        this.idCurso = idCurso;
        this.nombreMateria = nombreMateria;
        this.claveCurso = claveCurso;
        this.idDocente = idDocente;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public String getClaveCurso() {
        return claveCurso;
    }

    public void setClaveCurso(String claveCurso) {
        this.claveCurso = claveCurso;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }
}
