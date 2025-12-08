package proyectoSeguridad.modelo.pojo;


public class AlumnoCurso {
    
    private int IdAlumnoCurso;
    private int IdCurso;
    private int idAlumno;

    public AlumnoCurso() {
    }

    public AlumnoCurso(int IdAlumnoCurso, int IdCurso, int idAlumno) {
        this.IdAlumnoCurso = IdAlumnoCurso;
        this.IdCurso = IdCurso;
        this.idAlumno = idAlumno;
    }


    public int getIdAlumnoCurso() {
        return IdAlumnoCurso;
    }

    public void setIdAlumnoCurso(int IdAlumnoCurso) {
        this.IdAlumnoCurso = IdAlumnoCurso;
    }

    public int getIdCurso() {
        return IdCurso;
    }

    public void setIdCurso(int IdCurso) {
        this.IdCurso = IdCurso;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }
}
