package proyectoSeguridad.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import proyectoSeguridad.modelo.ConexionBD;
import proyectoSeguridad.modelo.pojo.Curso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import proyectoSeguridad.modelo.pojo.Alumno;
import proyectoSeguridad.modelo.pojo.CursoDetalle;
import proyectoSeguridad.modelo.pojo.Horario;

public class CursoDAO {

    public static List<Curso> obtenerCursosPorMatricula(String matricula) throws SQLException {
        List<Curso> cursos = new ArrayList<>();

        String consulta =
            "SELECT c.ID_Curso, c.Nombre_Materia, c.Clave_Curso, c.ID_Docente " +
            "FROM Cursos c " +
            "INNER JOIN alumno_curso ac ON ac.ID_Curso = c.ID_Curso " +
            "INNER JOIN Alumnos a ON a.ID_Alumno = ac.ID_Alumno " +
            "WHERE a.Matricula = ?";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, matricula);
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Curso curso = new Curso(
                    resultado.getInt("ID_Curso"),
                    resultado.getString("Nombre_Materia"),
                    resultado.getString("Clave_Curso"),
                    resultado.getInt("ID_Docente")
                );

                cursos.add(curso);
            }

        } finally {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return cursos;
    }

    /**
     * Obtiene cursos usando ID_Alumno.
     */
    public static List<Curso> obtenerCursosPorIdAlumno(int idAlumno) throws SQLException {
        List<Curso> cursos = new ArrayList<>();

        String consulta =
            "SELECT c.ID_Curso, c.Nombre_Materia, c.Clave_Curso, c.ID_Docente " +
            "FROM Cursos c " +
            "INNER JOIN alumno_curso ac ON ac.ID_Curso = c.ID_Curso " +
            "WHERE ac.ID_Alumno = ?";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idAlumno);
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Curso curso = new Curso(
                    resultado.getInt("ID_Curso"),
                    resultado.getString("Nombre_Materia"),
                    resultado.getString("Clave_Curso"),
                    resultado.getInt("ID_Docente")
                );

                cursos.add(curso);
            }

        } finally {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return cursos;
    }
    
/**
 * Obtiene un curso por su clave única
 */
public static Curso obtenerCursoPorClave(String claveCurso) throws SQLException {
    Curso curso = null;

    String consulta = "SELECT ID_Curso, Nombre_Materia, Clave_Curso, ID_Docente " +
                      "FROM Cursos WHERE Clave_Curso = ?";

    Connection conexionBD = null;
    PreparedStatement sentencia = null;
    ResultSet resultado = null;

    try {
        conexionBD = ConexionBD.abrirConexion();
        if (conexionBD == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }

        sentencia = conexionBD.prepareStatement(consulta);
        sentencia.setString(1, claveCurso);
        resultado = sentencia.executeQuery();

        if (resultado.next()) {
            curso = new Curso(
                resultado.getInt("ID_Curso"),
                resultado.getString("Nombre_Materia"),
                resultado.getString("Clave_Curso"),
                resultado.getInt("ID_Docente")
            );
        }

    } finally {
        if (resultado != null) resultado.close();
        if (sentencia != null) sentencia.close();
        if (conexionBD != null) conexionBD.close();
    }

    return curso;
}

    public static Map<String, String> obtenerCursoYDocentePorClave(String claveCurso) throws SQLException {
        Map<String, String> datos = null;

        String query = "SELECT c.Nombre_Materia, c.Clave_Curso, " +
                       "u.Nombre, u.Apellido, u.Username " +
                       "FROM cursos c " +
                       "JOIN docentes d ON c.ID_Docente = d.ID_Docente " +
                       "JOIN usuarios u ON d.ID_Usuario = u.ID_Usuario " +
                       "WHERE c.Clave_Curso = ?";

        try (Connection conn = ConexionBD.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, claveCurso);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                datos = new HashMap<>();
                datos.put("nombreMateria", rs.getString("Nombre_Materia"));
                datos.put("claveCurso", rs.getString("Clave_Curso"));
                datos.put("nombreDocente", rs.getString("Nombre") + " " + rs.getString("Apellido"));
                datos.put("usernameDocente", rs.getString("Username"));
            }
        }

        return datos;
    }
    
    public static boolean registrarCurso(Curso curso) throws SQLException {
        boolean registrado = false;

        String consulta = "INSERT INTO Cursos (Nombre_Materia, Clave_Curso, ID_Docente) VALUES (?, ?, ?)";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            conexionBD.setAutoCommit(false); 

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, curso.getNombreMateria()); 
            sentencia.setString(2, curso.getClaveCurso()); 
            sentencia.setInt(3, curso.getIdDocente());

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                conexionBD.commit();
                registrado = true;
            } else {
                conexionBD.rollback(); 
            }

        } catch (SQLException e) {
            if (conexionBD != null) {
                conexionBD.rollback();
            }
            throw e; 
        } finally {
            if (conexionBD != null) {
                conexionBD.setAutoCommit(true);
            }
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return registrado;
    }

    public static List<Curso> obtenerTodosLosCursos() throws SQLException {
        List<Curso> cursos = new ArrayList<>();

        String consulta = "SELECT ID_Curso, Nombre_Materia, Clave_Curso, ID_Docente FROM Cursos ORDER BY Nombre_Materia";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Curso curso = new Curso(
                    resultado.getInt("ID_Curso"),
                    resultado.getString("Nombre_Materia"),
                    resultado.getString("Clave_Curso"),
                    resultado.getInt("ID_Docente")
                );
                cursos.add(curso);
            }

        } finally {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return cursos;
    }

    public static List<Curso> buscarCursos(String textoBusqueda) throws SQLException {
        List<Curso> cursos = new ArrayList<>();
        String consulta = "SELECT ID_Curso, Nombre_Materia, Clave_Curso, ID_Docente " +
                          "FROM Cursos " +
                          "WHERE Nombre_Materia LIKE ? OR Clave_Curso LIKE ? " +
                          "ORDER BY Nombre_Materia";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;
        
        String parametroBusqueda = "%" + textoBusqueda + "%";

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, parametroBusqueda); // Para Nombre_Materia
            sentencia.setString(2, parametroBusqueda); // Para Clave_Curso
            
            resultado = sentencia.executeQuery();

            while (resultado.next()) {
                Curso curso = new Curso(
                    resultado.getInt("ID_Curso"),
                    resultado.getString("Nombre_Materia"),
                    resultado.getString("Clave_Curso"),
                    resultado.getInt("ID_Docente")
                );
                cursos.add(curso);
            }

        } finally {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return cursos;
    }

    // Asegúrate de importar los nuevos POJOs:
// import proyectoSeguridad.modelo.pojo.CursoDetalle;
// import proyectoSeguridad.modelo.dao.HorarioDAO;
// import proyectoSeguridad.modelo.pojo.Horario;

// Dentro de la clase CursoDAO:

    /**
     * Obtiene una lista detallada de cursos incluyendo el nombre del docente y carga los horarios.
     * @param textoBusqueda Opcional, para filtrar por nombre o clave de curso.
     * @return Lista de objetos CursoDetalle.
     */
    public static List<CursoDetalle> obtenerCursosDetallados(String textoBusqueda) throws SQLException {
        List<CursoDetalle> listaDetalles = new ArrayList<>();
        
        // Consulta base que une Cursos, Docentes y Usuarios
        String consulta = "SELECT C.ID_Curso, C.Nombre_Materia, C.Clave_Curso, C.ID_Docente, " +
                          "U.Nombre AS NombreDocente, U.Apellido AS ApellidoDocente " +
                          "FROM Cursos C " +
                          "INNER JOIN Docentes D ON C.ID_Docente = D.ID_Docente " +
                          "INNER JOIN Usuarios U ON D.ID_Usuario = U.ID_Usuario " +
                          "WHERE (? IS NULL OR C.Nombre_Materia LIKE ? OR C.Clave_Curso LIKE ?) " +
                          "ORDER BY C.Nombre_Materia";

        String parametroBusqueda = (textoBusqueda == null || textoBusqueda.trim().isEmpty()) ? null : "%" + textoBusqueda + "%";

        try (Connection conexionBD = ConexionBD.abrirConexion();
             PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

            // Asignación de parámetros LIKE
            if (parametroBusqueda != null) {
                sentencia.setString(1, parametroBusqueda);
                sentencia.setString(2, parametroBusqueda);
                sentencia.setString(3, parametroBusqueda);
            } else {
                // Si no hay búsqueda, se establece NULL para que la condición WHERE funcione
                sentencia.setNull(1, Types.VARCHAR); 
                sentencia.setNull(2, Types.VARCHAR);
                sentencia.setNull(3, Types.VARCHAR);
            }

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    int idCurso = resultado.getInt("ID_Curso");
                    
                    // 1. Obtener Horarios para este curso (llamada a otro DAO)
                    List<Horario> horarios = HorarioDAO.obtenerHorariosPorCurso(idCurso);
                    
                    // 2. Construir el objeto CursoDetalle
                    CursoDetalle detalle = new CursoDetalle(
                        idCurso,
                        resultado.getString("Nombre_Materia"),
                        resultado.getString("Clave_Curso"),
                        resultado.getString("NombreDocente") + " " + resultado.getString("ApellidoDocente"),
                        horarios
                    );

                    listaDetalles.add(detalle);
                }
            }
        }
        return listaDetalles;
    }
    
    public static List<Curso> obtenerCursosPorIdDocente(int idDocente) throws SQLException {
    List<Curso> cursos = new ArrayList<>();
    String consulta = "SELECT ID_Curso, Nombre_Materia, Clave_Curso, ID_Docente FROM Cursos WHERE ID_Docente = ?";
    
    try (Connection conn = ConexionBD.abrirConexion();
         PreparedStatement stmt = conn.prepareStatement(consulta)) {
        
        stmt.setInt(1, idDocente);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Curso c = new Curso();
            c.setIdCurso(rs.getInt("ID_Curso"));
            c.setNombreMateria(rs.getString("Nombre_Materia"));
            c.setClaveCurso(rs.getString("Clave_Curso"));
            c.setIdDocente(rs.getInt("ID_Docente"));
            cursos.add(c);
        }
    }
    return cursos;
}

}