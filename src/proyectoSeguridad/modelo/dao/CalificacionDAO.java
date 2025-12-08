package proyectoSeguridad.modelo.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import proyectoSeguridad.modelo.ConexionBD;
import proyectoSeguridad.modelo.pojo.Calificacion;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;

public class CalificacionDAO {

    /**
     * Registra una nueva calificación en la base de datos.
     * @param calificacion El objeto Calificacion a registrar.
     * @return Un objeto ResultadoOperacion con el estado y mensaje.
     */
    public static ResultadoOperacion registrarCalificacion(Calificacion calificacion) {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "INSERT INTO Calificaciones (ID_Alumno, ID_Curso, Puntaje, Fecha_Registro) VALUES (?, ?, ?, ?)"; 

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, calificacion.getIdAlumno());
            stmt.setInt(2, calificacion.getIdCurso());
            stmt.setDouble(3, calificacion.getPuntaje());
            stmt.setString(4, calificacion.getFechaRegistro()); 

            int filas = stmt.executeUpdate();
            if (filas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Calificación registrada correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo registrar la calificación.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultado.setError(true);
            resultado.setMensaje("Error al registrar la calificación: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Obtiene una lista de calificaciones para un alumno específico.
     * @param idAlumno El ID del alumno.
     * @return Lista de objetos Calificacion.
     */
    public static List<Calificacion> obtenerCalificacionesPorAlumno(int idAlumno) {
        List<Calificacion> lista = new ArrayList<>();
        String consulta = "SELECT * FROM Calificaciones WHERE ID_Alumno = ?"; 

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, idAlumno);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Calificacion c = new Calificacion(
                        rs.getInt("ID_Calificacion"),
                        rs.getInt("ID_Alumno"),
                        rs.getInt("ID_Curso"),
                        rs.getDouble("Puntaje"),
                        rs.getString("Fecha_Registro") 
                );
                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Elimina una calificación por su ID.
     * @param idCalificacion El ID de la calificación a eliminar.
     * @return true si se eliminó, false en caso contrario.
     */
    public static boolean eliminarCalificacion(int idCalificacion) {
        String consulta = "DELETE FROM Calificaciones WHERE ID_Calificacion = ?";
        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, idCalificacion);
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtiene una lista de calificaciones por curso.
     * NOTA: Este método asume la existencia de la tabla 'alumno_curso' para el filtro,
     * pero Calificaciones ya tiene ID_Curso. 
     * Se mantiene la versión original que usaste para evitar romper la lógica de tu BD.
     * * @param idCurso El ID del curso.
     * @return Lista de objetos Calificacion.
     */
    public static List<Calificacion> obtenerCalificacionesPorCurso(int idCurso) {
        List<Calificacion> calificaciones = new ArrayList<>();
        String consulta = "SELECT c.ID_Calificacion, c.ID_Alumno, c.ID_Curso, c.Puntaje, c.Fecha_Registro " +
                              "FROM Calificaciones c " + 
                              "INNER JOIN alumno_curso ac ON c.ID_Alumno = ac.ID_Alumno " + 
                              "WHERE ac.ID_Curso = ?";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, idCurso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Calificacion calificacion = new Calificacion();
                calificacion.setIdCalificacion(rs.getInt("ID_Calificacion"));
                calificacion.setIdAlumno(rs.getInt("ID_Alumno"));
                calificacion.setIdCurso(rs.getInt("ID_Curso"));
                calificacion.setPuntaje(rs.getDouble("Puntaje")); // Asegurar que sea double
                calificacion.setFechaRegistro(rs.getString("Fecha_Registro"));
                calificaciones.add(calificacion);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return calificaciones;
    }
    
    // ----------------------------------------------------------------------
    // MÉTODO REFACTORIZADO PARA BÚSQUEDA EXCLUSIVA
    // ----------------------------------------------------------------------

    /**
     * Obtiene una lista de calificaciones con detalles (curso y alumno) aplicando 
     * un filtro exclusivo por ID de Alumno o ID de Curso.
     * * La clave de la exclusividad está en el SQL:
     * - Si idAlumno es 0, se ignora el filtro de Alumno.
     * - Si idCurso es 0, se ignora el filtro de Curso.
     * * @param idAlumno El ID del alumno (0 para ignorar el filtro).
     * @param idCurso El ID del curso (0 para ignorar el filtro).
     * @return Lista de Mapas con los detalles de las calificaciones.
     * @throws SQLException Si ocurre un error con la base de datos.
     */
    public static List<Map<String, Object>> obtenerCalificacionesDetalladas(int idAlumno, int idCurso) throws SQLException {
        List<Map<String, Object>> listaDetalles = new ArrayList<>();
        
        String consulta = "SELECT C.ID_Calificacion, C.Puntaje, C.Fecha_Registro, " +
                          "CU.Nombre_Materia, CU.Clave_Curso, " +
                          "U.Nombre AS NombreAlumno, U.Apellido AS ApellidoAlumno " +
                          "FROM Calificaciones C " + 
                          "JOIN Alumnos A ON C.ID_Alumno = A.ID_Alumno " + 
                          "JOIN Cursos CU ON C.ID_Curso = CU.ID_Curso " +
                          "JOIN Usuarios U ON A.ID_Usuario = U.ID_Usuario " +
                          "WHERE (? = 0 OR C.ID_Alumno = ?) AND (? = 0 OR C.ID_Curso = ?)";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            // Parámetros para el filtro de Alumno
            stmt.setInt(1, idAlumno);
            stmt.setInt(2, idAlumno);
            
            // Parámetros para el filtro de Curso
            stmt.setInt(3, idCurso);
            stmt.setInt(4, idCurso);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("idCalificacion", rs.getInt("ID_Calificacion"));
                    detalle.put("puntaje", rs.getDouble("Puntaje")); 
                    detalle.put("fechaRegistro", rs.getString("Fecha_Registro"));
                    
                    // Combina nombre de materia y clave para el display
                    detalle.put("nombreMateria", rs.getString("Nombre_Materia") + " (" + rs.getString("Clave_Curso") + ")");
                    
                    // Combina nombre y apellido del alumno para el display
                    detalle.put("nombreAlumno", rs.getString("NombreAlumno") + " " + rs.getString("ApellidoAlumno"));
                    
                    listaDetalles.add(detalle);
                }
            }
        }
        return listaDetalles;
    }
    
       public static List<Map<String, Object>> obtenerCalificacionesDetalladasPorAlumno(int idAlumno) throws SQLException {
        List<Map<String, Object>> listaDetalles = new ArrayList<>();
        
        String consulta = "SELECT C.Puntaje, C.Fecha_Registro, " +
                          "CU.Nombre_Materia, CU.Clave_Curso, " +
                          "U_Docente.Nombre AS NombreDocente, U_Docente.Apellido AS ApellidoDocente " +
                          "FROM Calificaciones C " +
                          "JOIN Cursos CU ON C.ID_Curso = CU.ID_Curso " +
                          "JOIN Docentes D ON CU.ID_Docente = D.ID_Docente " +
                          "JOIN Usuarios U_Docente ON D.ID_Usuario = U_Docente.ID_Usuario " +
                          "WHERE C.ID_Alumno = ?";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, idAlumno);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("puntaje", rs.getDouble("Puntaje"));
                    detalle.put("fechaRegistro", rs.getString("Fecha_Registro"));
                    
                    // Detalle del curso
                    detalle.put("cursoCompleto", rs.getString("Nombre_Materia") + " (" + rs.getString("Clave_Curso") + ")");
                    
                    // Detalle del docente
                    detalle.put("docenteCompleto", rs.getString("NombreDocente") + " " + rs.getString("ApellidoDocente"));
                                        
                    listaDetalles.add(detalle);
                }
            }
        }
        return listaDetalles;
    }
}