package proyectoSeguridad.modelo.pojo;

public class Docente {
    
    private int idDocente;
    private String numeroEmpleado;
    private int idUsuario;

    public Docente() {
    }

    public Docente(int idDocente, String numeroEmpleado, int idUsuario) {
        this.idDocente = idDocente;
        this.numeroEmpleado = numeroEmpleado;
        this.idUsuario = idUsuario;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(String numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
