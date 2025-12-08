package proyectoSeguridad.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import proyectoSeguridad.modelo.ConexionBD;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;
import proyectoSeguridad.modelo.pojo.Usuario;

public class UsuarioDAO {
    
   public static int obtenerIdUsuarioPorCredenciales(String username, String passwordHash) throws SQLException {
    int idUsuario = 0;        
    String consulta = "SELECT ID_Usuario FROM Usuarios WHERE Username = ? AND Contrasena_Hash = ?";

    Connection conexionBD = null;
    PreparedStatement sentencia = null;
    ResultSet resultado = null;

    try {
        conexionBD = ConexionBD.abrirConexion();
        if (conexionBD == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }

        sentencia = conexionBD.prepareStatement(consulta);
        sentencia.setString(1, username);
        sentencia.setString(2, passwordHash);
        resultado = sentencia.executeQuery();

        if (resultado.next()) {
            idUsuario = resultado.getInt("ID_Usuario");
        }
    } finally {
        if (resultado != null) resultado.close();
        if (sentencia != null) sentencia.close();
        if (conexionBD != null) conexionBD.close();
    }

    return idUsuario;
}

    
public static ResultadoOperacion registrarUsuario(Usuario usuario) throws SQLException {
    ResultadoOperacion resultado = new ResultadoOperacion();
    String consulta = "INSERT INTO Usuarios (Nombre, Apellido, Username, Contrasena_Hash, Rol) VALUES (?, ?, ?, ?, ?)";
    
    Connection conexionBD = null;
    PreparedStatement sentencia = null;

    try {
        conexionBD = ConexionBD.abrirConexion();
        if (conexionBD == null) {
            throw new SQLException("No hay conexión con la base de datos.");
        }

        sentencia = conexionBD.prepareStatement(consulta);
        sentencia.setString(1, usuario.getNombre());
        sentencia.setString(2, usuario.getApellido());
        sentencia.setString(3, usuario.getUsername());
        sentencia.setString(4, usuario.getContrasenaHash());
        sentencia.setString(5, usuario.getRol());

        int filasAfectadas = sentencia.executeUpdate();

        if (filasAfectadas == 1) {
            resultado.setError(false);
            resultado.setMensaje("Usuario registrado correctamente.");
        } else {
            resultado.setError(true);
            resultado.setMensaje("No se pudo registrar el usuario. Inténtelo más tarde.");
        }

    } finally {
        if (sentencia != null) sentencia.close();
        if (conexionBD != null) conexionBD.close();
    }

    return resultado;
}

    
    public static boolean existeUsuario(String username) throws SQLException {
        boolean existe = false;
        String consulta = "SELECT COUNT(*) FROM usuarios WHERE Username = ?";
        
        Connection conexionBD = null;
        PreparedStatement sentencia = null;
        ResultSet resultado = null;

        try {
            conexionBD = ConexionBD.abrirConexion();
            if (conexionBD == null) {
                throw new SQLException("No hay conexión con la base de datos.");
            }

            sentencia = conexionBD.prepareStatement(consulta);
            sentencia.setString(1, username);
            resultado = sentencia.executeQuery();

            if (resultado.next()) {
                if (resultado.getInt(1) > 0) {
                    existe = true;
                }
            }
        } finally {
            if (resultado != null) { 
                resultado.close(); 
            }
            if (sentencia != null) { 
                sentencia.close();
            }
            if (conexionBD != null) { 
                conexionBD.close();
            }
        }
        return existe;
    }
    
    public static String obtenerRolPorIdUsuario(int idUsuario) throws SQLException {
    String rol = null;
    String consulta = "SELECT Rol FROM Usuarios WHERE ID_Usuario = ?";

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
            rol = resultado.getString("Rol");
        }

    } finally {
        if (resultado != null) resultado.close();
        if (sentencia != null) sentencia.close();
        if (conexionBD != null) conexionBD.close();
    }

    return rol;
}

public static String obtenerNombreCompletoPorId(int idUsuario) {
    String nombreCompleto = null;

    try {
        Connection conexion = ConexionBD.abrirConexion();
        String query = "SELECT Nombre, Apellido FROM usuarios WHERE ID_Usuario = ?";
        PreparedStatement stmt = conexion.prepareStatement(query);
        stmt.setInt(1, idUsuario);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String nombre = rs.getString("Nombre");
            String apellido = rs.getString("Apellido");
            nombreCompleto = nombre + " " + apellido;
        }

        conexion.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

    return nombreCompleto;
}

public static String obtenerUsernamePorIdUsuario(int idUsuario) throws SQLException {
    String username = null;
    String consulta = "SELECT Username FROM Usuarios WHERE ID_Usuario = ?";

    try (Connection conexionBD = ConexionBD.abrirConexion();
         PreparedStatement sentencia = conexionBD.prepareStatement(consulta)) {

        sentencia.setInt(1, idUsuario);
        try (ResultSet resultado = sentencia.executeQuery()) {
            if (resultado.next()) {
                username = resultado.getString("Username");
            }
        }
    }

    return username;
}

}