package proyectoSeguridad.modelo.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import proyectoSeguridad.modelo.ConexionBD;
import proyectoSeguridad.modelo.pojo.Horario;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;

public class HorarioDAO {

    // Nota: Si no tienes HorarioDAO, este método debe ir en CursoDAO.
// Se asume que tienes columnas para DÍA_SEMANA y HORA_INICIO/FIN relacionadas con el Curso.

    /**
     * Obtiene los detalles completos del horario de un alumno (cursos, días, horas y docente).
     * @param idAlumno El ID del alumno.
     * @return Lista de Mapas con los detalles del horario.
     */
    public static List<Map<String, Object>> obtenerHorarioPorAlumno(int idAlumno) throws SQLException {
        List<Map<String, Object>> listaHorario = new ArrayList<>();
        
        // CORRECCIÓN CLAVE: Seleccionamos Dia_Semana, Hora_Inicio y Hora_Fin de la tabla Horarios (H)
        String consulta = "SELECT CU.Nombre_Materia, CU.Clave_Curso, H.Dia_Semana, H.Hora_Inicio, H.Hora_Fin, " +
                          "U_Docente.Nombre AS DocenteNombre, U_Docente.Apellido AS DocenteApellido, H.Aula " + // También traemos el aula
                          "FROM alumno_curso AC " +
                          "JOIN Cursos CU ON AC.ID_Curso = CU.ID_Curso " +
                          // *** AQUI ESTÁ EL JOIN QUE FALTABA O ESTABA MAL EN LA CONSULTA ANTERIOR ***
                          "JOIN Horarios H ON CU.ID_Curso = H.ID_Curso " + 
                          "JOIN Docentes D ON CU.ID_Docente = D.ID_Docente " +
                          "JOIN Usuarios U_Docente ON D.ID_Usuario = U_Docente.ID_Usuario " +
                          "WHERE AC.ID_Alumno = ?";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, idAlumno);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("materia", rs.getString("Nombre_Materia"));
                    detalle.put("clave", rs.getString("Clave_Curso"));
                    
                    // Datos de Horario (Ahora sí existen)
                    detalle.put("dia", rs.getString("Dia_Semana"));
                    detalle.put("horaInicio", rs.getString("Hora_Inicio")); 
                    detalle.put("horaFin", rs.getString("Hora_Fin"));
                    detalle.put("aula", rs.getString("Aula"));
                    
                    detalle.put("docente", rs.getString("DocenteNombre") + " " + rs.getString("DocenteApellido"));
                                        
                    listaHorario.add(detalle);
                }
            }
        }
        return listaHorario;
    }
    
    public static List<Map<String, Object>> obtenerHorariosDeDocente(int idDocente) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String consulta = "SELECT h.ID_Horario, h.Dia_Semana, h.Hora_Inicio, h.Hora_Fin, h.Aula, " +
                          "c.Nombre_Materia, c.Clave_Curso " +
                          "FROM Horarios h " +
                          "INNER JOIN Cursos c ON h.ID_Curso = c.ID_Curso " +
                          "WHERE c.ID_Docente = ? " +
                          "ORDER BY CASE " +
                          " WHEN h.Dia_Semana = 'Lunes' THEN 1 " +
                          " WHEN h.Dia_Semana = 'Martes' THEN 2 " +
                          " WHEN h.Dia_Semana = 'Miércoles' THEN 3 " +
                          " WHEN h.Dia_Semana = 'Jueves' THEN 4 " +
                          " WHEN h.Dia_Semana = 'Viernes' THEN 5 " +
                          " WHEN h.Dia_Semana = 'Sábado' THEN 6 " +
                          " END, h.Hora_Inicio";

        try (java.sql.Connection conexion = proyectoSeguridad.modelo.ConexionBD.abrirConexion();
             java.sql.PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, idDocente);
            java.sql.ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> fila = new java.util.HashMap<>();
                fila.put("idHorario", rs.getInt("ID_Horario"));
                fila.put("diaSemana", rs.getString("Dia_Semana")); // Importante para filtrar en el controller
                fila.put("horaInicio", rs.getString("Hora_Inicio"));
                fila.put("horaFin", rs.getString("Hora_Fin"));
                fila.put("aula", rs.getString("Aula"));
                fila.put("nombreMateria", rs.getString("Nombre_Materia"));
                fila.put("claveCurso", rs.getString("Clave_Curso"));
                
                lista.add(fila);
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    public static ResultadoOperacion registrarHorario(Horario horario) {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "INSERT INTO Horarios (ID_Curso, Dia_Semana, Hora_Inicio, Hora_Fin, Aula) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, horario.getIdCurso());
            stmt.setString(2, horario.getDiaSemana());
            stmt.setString(3, horario.getHoraInicio()); // Formato esperado "HH:mm:ss"
            stmt.setString(4, horario.getHoraFin());    // Formato esperado "HH:mm:ss"
            stmt.setString(5, horario.getAula());       // Puede ser null

            int filas = stmt.executeUpdate();
            if (filas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Horario registrado correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo registrar el horario.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultado.setError(true);
            resultado.setMensaje("Error al registrar el horario: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Obtiene la lista de horarios asignados a un curso específico.
     * @param idCurso El ID del curso a consultar.
     * @return Lista de objetos Horario.
     */
    public static List<Horario> obtenerHorariosPorCurso(int idCurso) {
        List<Horario> lista = new ArrayList<>();
        String consulta = "SELECT ID_Horario, ID_Curso, Dia_Semana, Hora_Inicio, Hora_Fin, Aula " +
                          "FROM Horarios WHERE ID_Curso = ? ORDER BY Dia_Semana, Hora_Inicio";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, idCurso);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Horario h = new Horario();
                h.setIdHorario(rs.getInt("ID_Horario"));
                h.setIdCurso(rs.getInt("ID_Curso"));
                h.setDiaSemana(rs.getString("Dia_Semana"));
                // Convertimos el Time de SQL a String para el POJO
                h.setHoraInicio(rs.getString("Hora_Inicio")); 
                h.setHoraFin(rs.getString("Hora_Fin"));
                h.setAula(rs.getString("Aula"));
                
                lista.add(h);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Elimina un horario específico por su ID.
     * @param idHorario El ID del horario a eliminar.
     * @return ResultadoOperacion.
     */
    public static ResultadoOperacion eliminarHorario(int idHorario) {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "DELETE FROM Horarios WHERE ID_Horario = ?";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, idHorario);
            int filas = stmt.executeUpdate();

            if (filas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Horario eliminado correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo eliminar el horario (tal vez no exista).");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultado.setError(true);
            resultado.setMensaje("Error al eliminar el horario: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Actualiza la información de un horario existente.
     * @param horario Objeto con los nuevos datos.
     * @return ResultadoOperacion.
     */
    public static ResultadoOperacion actualizarHorario(Horario horario) {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "UPDATE Horarios SET Dia_Semana = ?, Hora_Inicio = ?, Hora_Fin = ?, Aula = ? WHERE ID_Horario = ?";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setString(1, horario.getDiaSemana());
            stmt.setString(2, horario.getHoraInicio());
            stmt.setString(3, horario.getHoraFin());
            stmt.setString(4, horario.getAula());
            stmt.setInt(5, horario.getIdHorario());

            int filas = stmt.executeUpdate();

            if (filas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Horario actualizado correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo actualizar el horario.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultado.setError(true);
            resultado.setMensaje("Error al actualizar: " + e.getMessage());
        }
        
        return resultado;
    }
}