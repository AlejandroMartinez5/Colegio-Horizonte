package proyectoSeguridad.modelo.pojo;

public class Alumno {
    
    private int idAlumno;
    private String matricula;
    private int idUsuario;
    private String estadoInscripcion;
    
    // Atributos extendidos para visualización
    private String nombre;
    private String apellido;

    public Alumno() {
    }

    // Constructor completo actualizado
    public Alumno(int idAlumno, String matricula, int idUsuario, String estadoInscripcion, String nombre, String apellido) {
        this.idAlumno = idAlumno;
        this.matricula = matricula;
        this.idUsuario = idUsuario;
        this.estadoInscripcion = estadoInscripcion;
        this.nombre = nombre;
        this.apellido = apellido;
    }
    
    // Constructor antiguo (para compatibilidad si lo usas en otros lados sin nombre)
    public Alumno(int idAlumno, String matricula, int idUsuario, String estadoInscripcion) {
        this.idAlumno = idAlumno;
        this.matricula = matricula;
        this.idUsuario = idUsuario;
        this.estadoInscripcion = estadoInscripcion;
    }

    public int getIdAlumno() { return idAlumno; }
    public void setIdAlumno(int idAlumno) { this.idAlumno = idAlumno; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getEstadoInscripcion() { return estadoInscripcion; }
    public void setEstadoInscripcion(String estadoInscripcion) { this.estadoInscripcion = estadoInscripcion; }

    // GETTERS Y SETTERS NECESARIOS PARA LA TABLA
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}