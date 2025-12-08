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
import proyectoSeguridad.modelo.pojo.Docente;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;

public class DocenteDAO {

    public static Docente obtenerDocentePorId(int idDocente) throws SQLException {
        Docente docente = null;
        String consulta = "SELECT * FROM Docentes WHERE ID_Docente = ?";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idDocente);
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                docente = new Docente(
                    resultado.getInt("ID_Docente"),
                    resultado.getString("Numero_Empleado"),
                    resultado.getInt("ID_Usuario")
                );
            }

        } finally {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return docente;
    }

    public static ResultadoOperacion registrarDocente(Docente docente) throws SQLException {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "INSERT INTO Docentes (Numero_Empleado, ID_Usuario) VALUES (?, ?)";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, docente.getNumeroEmpleado());
            sentencia.setInt(2, docente.getIdUsuario());

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Docente registrado correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo registrar al docente.");
            }

        } finally {
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return resultado;
    }

    public static boolean existeNumeroEmpleado(String numeroEmpleado) throws SQLException {
        boolean existe = false;
        String consulta = "SELECT COUNT(*) FROM Docentes WHERE Numero_Empleado = ?";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, numeroEmpleado);
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

    public static Docente obtenerDocentePorIdUsuario(int idUsuario) throws SQLException {
        Docente docente = null;
        String consulta = "SELECT * FROM Docentes WHERE ID_Usuario = ?";

        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setInt(1, idUsuario);
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                docente = new Docente(
                    resultado.getInt("ID_Docente"),
                    resultado.getString("Numero_Empleado"),
                    resultado.getInt("ID_Usuario")
                );
            }

        } finally {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexionBD != null) conexionBD.close();
        }

        return docente;
    }
    
    public static Docente obtenerDocentePorNumeroEmpleado(String numeroEmpleado) throws SQLException {
    Docente docente = null;
    String consulta = "SELECT * FROM Docentes WHERE Numero_Empleado = ?";

    Connection conexionBD = null;
    PreparedStatement sentencia = null;
    ResultSet resultado = null;

    try {
        conexionBD = ConexionBD.abrirConexion();
        if (conexionBD == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }

        sentencia = conexionBD.prepareStatement(consulta);
        sentencia.setString(1, numeroEmpleado);
        resultado = sentencia.executeQuery();

        if (resultado.next()) {
            docente = new Docente(
                resultado.getInt("ID_Docente"),
                resultado.getString("Numero_Empleado"),
                resultado.getInt("ID_Usuario")
            );
        }

    } finally {
        if (resultado != null) resultado.close();
        if (sentencia != null) sentencia.close();
        if (conexionBD != null) conexionBD.close();
    }

    return docente;
}

    public static String obtenerUsernamePorIdUsuario(int idUsuario) throws SQLException {
    String username = null;
    String consulta = "SELECT Username FROM Usuarios WHERE ID_Usuario = ?";

    Connection conexionBD = null;
    PreparedStatement sentencia = null;
    ResultSet resultado = null;

    try {
        conexionBD = ConexionBD.abrirConexion();
        if (conexionBD == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }

        sentencia = conexionBD.prepareStatement(consulta);
        sentencia.setInt(1, idUsuario);
        resultado = sentencia.executeQuery();

        if (resultado.next()) {
            username = resultado.getString("Username");
        }

    } finally {
        if (resultado != null) resultado.close();
        if (sentencia != null) sentencia.close();
        if (conexionBD != null) conexionBD.close();
    }

    return username;
}
    
public static List<Map<String, Object>> obtenerDocentesParaComboBox() throws SQLException {
    List<Map<String, Object>> listaDocentes = new ArrayList<>();
    
    String consulta = "SELECT d.ID_Docente, u.Nombre, u.Apellido, d.Numero_Empleado " +
                      "FROM Docentes d " +
                      "JOIN Usuarios u ON d.ID_Usuario = u.ID_Usuario " +
                      "ORDER BY u.Apellido, u.Nombre";

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
            Map<String, Object> docenteInfo = new HashMap<>();
            String nombreCompleto = resultado.getString("Apellido") + " " + resultado.getString("Nombre") + 
                                    " (No. Emp: " + resultado.getString("Numero_Empleado") + ")";
            
            docenteInfo.put("ID_Docente", resultado.getInt("ID_Docente"));
            docenteInfo.put("NombreCompleto", nombreCompleto);
            listaDocentes.add(docenteInfo);
        }
        
    } finally {
        if (resultado != null) resultado.close();
        if (sentencia != null) sentencia.close();
        if (conexionBD != null) conexionBD.close();
    }
    
    return listaDocentes;
}

}
