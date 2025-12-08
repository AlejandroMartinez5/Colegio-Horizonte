package proyectoSeguridad.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import proyectoSeguridad.modelo.ConexionBD;
import proyectoSeguridad.modelo.pojo.AlumnoCurso;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;

public class AlumnoCursoDAO {

    public static ResultadoOperacion registrarAlumnoEnCurso(AlumnoCurso ac) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "INSERT INTO alumno_curso (ID_Curso, ID_Alumno) VALUES (?, ?)";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, ac.getIdCurso());
            sentencia.setInt(2, ac.getIdAlumno());

            int filas = sentencia.executeUpdate();

            if (filas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Alumno inscrito correctamente en el curso.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo inscribir al alumno. Intente más tarde.");
            }

        } finally {
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return resultado;
    }

    public static boolean existeInscripcion(int idCurso, int idAlumno) throws SQLException {
        boolean existe = false;
        String consulta = "SELECT COUNT(*) FROM alumno_curso WHERE ID_Curso = ? AND ID_Alumno = ?";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idCurso);
            sentencia.setInt(2, idAlumno);
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                existe = resultado.getInt(1) > 0;
            }

        } finally {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return existe;
    }

public static List<AlumnoCurso> obtenerAlumnosPorCurso(int idCurso) throws SQLException {
    List<AlumnoCurso> lista = new ArrayList<>();
    String consulta = "SELECT * FROM alumno_curso WHERE ID_Curso = ?";

    Connection conexionBD = null;
    PreparedStatement sentencia = null;
    ResultSet resultado = null;

    try {
        conexionBD = ConexionBD.abrirConexion();
        if (conexionBD == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }

        sentencia = conexionBD.prepareStatement(consulta);
        sentencia.setInt(1, idCurso);
        resultado = sentencia.executeQuery();

        while (resultado.next()) {
            AlumnoCurso ac = new AlumnoCurso(
                resultado.getInt("ID_AlumnoCurso"),
                resultado.getInt("ID_Curso"),
                resultado.getInt("ID_Alumno")
            );

            lista.add(ac);
        }

    } finally {
        if (resultado != null) resultado.close();
        if (sentencia != null) sentencia.close();
        if (conexionBD != null) conexionBD.close();
    }

    return lista;
}



    public static ResultadoOperacion eliminarInscripcion(int idAlumnoCurso) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "DELETE FROM alumno_curso WHERE ID_AlumnoCurso = ?";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idAlumnoCurso);

            int filas = sentencia.executeUpdate();

            if (filas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Inscripción eliminada correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo eliminar la inscripción.");
            }

        } finally {
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return resultado;
    }


    public static AlumnoCurso obtenercPorId(int idAlumnoCurso) throws SQLException {
        AlumnoCurso ac = null;
        String consulta = "SELECT * FROM alumno_curso WHERE ID_AlumnoCurso = ?";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idAlumnoCurso);
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                ac = new AlumnoCurso(
                    resultado.getInt("ID_AlumnoCurso"),
                    resultado.getInt("ID_Curso"),
                    resultado.getInt("ID_Alumno")
                );
            }

        } finally {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return ac;
    }
}
