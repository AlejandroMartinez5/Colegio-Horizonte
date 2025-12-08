package proyectoSeguridad.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import proyectoSeguridad.modelo.ConexionBD;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;

public class AlumnoDAO {

        public static List<proyectoSeguridad.modelo.pojo.Alumno> obtenerTodosLosAlumnosConDetalle() throws SQLException {
        List<proyectoSeguridad.modelo.pojo.Alumno> alumnos = new ArrayList<>();
        
        // Traemos todos los alumnos haciendo JOIN con Usuarios (no hay filtro WHERE)
        String consulta = 
            "SELECT a.ID_Alumno, a.Matricula, a.ID_Usuario, a.estadoInscripcion, u.Nombre, u.Apellido " +
            "FROM Alumnos a " +
            "INNER JOIN Usuarios u ON a.ID_Usuario = u.ID_Usuario " +
            "ORDER BY u.Apellido, u.Nombre";

        try (Connection conexionBD = proyectoSeguridad.modelo.ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                proyectoSeguridad.modelo.pojo.Alumno alumno = new proyectoSeguridad.modelo.pojo.Alumno();
                alumno.setIdAlumno(resultado.getInt("ID_Alumno"));
                alumno.setMatricula(resultado.getString("Matricula"));
                alumno.setIdUsuario(resultado.getInt("ID_Usuario"));
                alumno.setEstadoInscripcion(resultado.getString("estadoInscripcion"));
                
                // Cargamos los campos de detalle directamente
                alumno.setNombre(resultado.getString("Nombre"));
                alumno.setApellido(resultado.getString("Apellido")); 
                alumnos.add(alumno);
            }
        }
        return alumnos;
    }
    
    public static Alumno obtenerAlumnoPorId(int idAlumno) throws SQLException {
        Alumno alumno = null;
        String consulta = "SELECT * FROM Alumnos WHERE ID_Alumno = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setInt(1, idAlumno);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    alumno = new Alumno(
                        resultado.getInt("ID_Alumno"),
                        resultado.getString("Matricula"),
                        resultado.getInt("ID_Usuario"),
                        resultado.getString("estadoInscripcion"),
                        "", "" // Inicializamos nombres vacíos si no hacemos JOIN aquí
                    );
                }
            }
        }
        return alumno;
    }

    public static ResultadoOperacion registrarAlumno(Alumno alumno) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "INSERT INTO Alumnos (Matricula, ID_Usuario, estadoInscripcion) VALUES (?, ?, ?)";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setString(1, alumno.getMatricula());
            sentencia.setInt(2, alumno.getIdUsuario());
            sentencia.setString(3, alumno.getEstadoInscripcion() != null ? alumno.getEstadoInscripcion() : "Pendiente");

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Alumno registrado correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo registrar el alumno. Inténtelo más tarde.");
            }
        }
        return resultado;
    }

    public static boolean existeMatricula(String matricula) throws SQLException {
        boolean existe = false;
        String consulta = "SELECT COUNT(*) FROM Alumnos WHERE Matricula = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setString(1, matricula);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    existe = resultado.getInt(1) > 0;
                }
            }
        }
        return existe;
    }

    public static Alumno obtenerAlumnoPorIdUsuario(int idUsuario) throws SQLException {
        Alumno alumno = null;
        String consulta = "SELECT * FROM Alumnos WHERE ID_Usuario = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setInt(1, idUsuario);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    alumno = new Alumno(
                        resultado.getInt("ID_Alumno"),
                        resultado.getString("Matricula"),
                        resultado.getInt("ID_Usuario"),
                        resultado.getString("estadoInscripcion"),
                        "", ""
                    );
                }
            }
        }
        return alumno;
    }

    public static Alumno obtenerAlumnoPorMatricula(String matricula) throws SQLException {
        Alumno alumno = null;
        String consulta = "SELECT * FROM Alumnos WHERE Matricula = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setString(1, matricula);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    alumno = new Alumno(
                        resultado.getInt("ID_Alumno"),
                        resultado.getString("Matricula"),
                        resultado.getInt("ID_Usuario"),
                        resultado.getString("estadoInscripcion"),
                        "", ""
                    );
                }
            }
        }
        return alumno;
    }

    // *** AQUÍ ESTÁ LA CORRECCIÓN CLAVE ***
    public static List<Alumno> obtenerAlumnosPorCurso(int idCurso) throws SQLException {
        List<Alumno> alumnos = new ArrayList<>();
        
        // CORREGIDO: Ahora hacemos JOIN con Usuarios para obtener Nombre y Apellido
        String consulta =
            "SELECT a.ID_Alumno, a.Matricula, a.ID_Usuario, a.estadoInscripcion, " +
            "u.Nombre, u.Apellido " +
            "FROM Alumnos a " +
            "INNER JOIN Usuarios u ON a.ID_Usuario = u.ID_Usuario " +
            "INNER JOIN alumno_curso ac ON ac.ID_Alumno = a.ID_Alumno " +
            "WHERE ac.ID_Curso = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setInt(1, idCurso);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    Alumno alumno = new Alumno();
                    alumno.setIdAlumno(resultado.getInt("ID_Alumno"));
                    alumno.setMatricula(resultado.getString("Matricula"));
                    alumno.setIdUsuario(resultado.getInt("ID_Usuario"));
                    alumno.setEstadoInscripcion(resultado.getString("estadoInscripcion"));
                    
                    // Seteamos los campos de texto que se mostrarán en la tabla
                    alumno.setNombre(resultado.getString("Nombre"));
                    alumno.setApellido(resultado.getString("Apellido"));
                    
                    alumnos.add(alumno);
                }
            }
        }
        return alumnos;
    }

    public static String obtenerNombreCompleto(int idUsuario) throws SQLException {
        String nombreCompleto = null;
        String consulta = "SELECT Nombre, Apellido FROM Usuarios WHERE ID_Usuario = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setInt(1, idUsuario);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    nombreCompleto = resultado.getString("Nombre") + " " + resultado.getString("Apellido");
                }
            }
        }
        return nombreCompleto;
    }

    public static ResultadoOperacion actualizarEstadoInscripcion(int idAlumno, String nuevoEstado) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "UPDATE Alumnos SET estadoInscripcion = ? WHERE ID_Alumno = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setString(1, nuevoEstado);
            sentencia.setInt(2, idAlumno);

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Estado de inscripción actualizado correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo actualizar el estado de inscripción.");
            }
        }
        return resultado;
    }

    public static boolean InscripcionPagada(int idAlumno) throws SQLException {
        boolean pagado = false;
        String consulta = "SELECT estadoInscripcion FROM Alumnos WHERE ID_Alumno = ?";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            sentencia.setInt(1, idAlumno);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    String estado = resultado.getString("estadoInscripcion");
                    pagado = "Pagado".equalsIgnoreCase(estado);
                }
            }
        }
        return pagado;
    }
    
    public static List<Alumno> obtenerAlumnosPendientes() throws SQLException {
        List<Alumno> alumnosPendientes = new ArrayList<>();
        // En este caso, si necesitas el nombre para mostrarlo en la lista de pendientes, 
        // también deberías hacer el JOIN, aunque aquí lo dejo simple según tu original.
        String consulta = 
            "SELECT a.*, u.Nombre, u.Apellido " + 
            "FROM Alumnos a " +
            "INNER JOIN Usuarios u ON a.ID_Usuario = u.ID_Usuario " +
            "WHERE a.estadoInscripcion = 'Pendiente'";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                Alumno alumno = new Alumno();
                alumno.setIdAlumno(resultado.getInt("ID_Alumno"));
                alumno.setMatricula(resultado.getString("Matricula"));
                alumno.setIdUsuario(resultado.getInt("ID_Usuario"));
                alumno.setEstadoInscripcion(resultado.getString("estadoInscripcion"));
                alumno.setNombre(resultado.getString("Nombre"));
                alumno.setApellido(resultado.getString("Apellido"));
                
                alumnosPendientes.add(alumno);
            }
        }
        return alumnosPendientes;
    }
    
       public static List<Map<String, Object>> obtenerAlumnosConCalificacion(int idCurso) throws SQLException {
        List<Map<String, Object>> lista = new ArrayList<>();
        
        // LEFT JOIN para traer la calificación si existe, o NULL si no
        String consulta = 
            "SELECT a.Matricula, u.Nombre, u.Apellido, c.Puntaje " +
            "FROM Alumnos a " +
            "INNER JOIN Usuarios u ON a.ID_Usuario = u.ID_Usuario " +
            "INNER JOIN alumno_curso ac ON ac.ID_Alumno = a.ID_Alumno " +
            "LEFT JOIN Calificaciones c ON c.ID_Alumno = a.ID_Alumno AND c.ID_Curso = ac.ID_Curso " +
            "WHERE ac.ID_Curso = ?";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {
             
            stmt.setInt(1, idCurso);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("matricula", rs.getString("Matricula"));
                    fila.put("nombre", rs.getString("Nombre"));
                    fila.put("apellido", rs.getString("Apellido"));
                    
                    // Manejo del puntaje nulo
                    Object puntaje = rs.getObject("Puntaje");
                    fila.put("calificacion", puntaje != null ? puntaje.toString() : " - ");
                    
                    lista.add(fila);
                }
            }
        }
        return lista;
}
}
