package proyectoSeguridad.modelo.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import proyectoSeguridad.modelo.ConexionBD;
import proyectoSeguridad.modelo.pojo.Comunicado;
import proyectoSeguridad.modelo.pojo.ResultadoOperacion;

public class ComunicadoDAO {


    public static ResultadoOperacion registrarComunicado(Comunicado comunicado) {
        ResultadoOperacion resultado = new ResultadoOperacion();
        String consulta = "INSERT INTO Comunicados (ID_Usuario_Admin, Titulo, Contenido) VALUES (?, ?, ?)";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta)) {

            stmt.setInt(1, comunicado.getIdUsuarioAdmin());
            stmt.setString(2, comunicado.getTitulo());
            stmt.setString(3, comunicado.getContenido());

            int filas = stmt.executeUpdate();
            if (filas == 1) {
                resultado.setError(false);
                resultado.setMensaje("Comunicado publicado correctamente.");
            } else {
                resultado.setError(true);
                resultado.setMensaje("No se pudo publicar el comunicado.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultado.setError(true);
            resultado.setMensaje("Error al registrar el comunicado: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Obtiene una lista de comunicados con los detalles del administrador que los publicó.
     * @return Lista de Mapas con datos del comunicado y nombre del autor.
     */
    public static List<Map<String, Object>> obtenerComunicadosDetallados() {
        List<Map<String, Object>> lista = new ArrayList<>();
        
        String consulta = "SELECT C.ID_Comunicado, C.Titulo, C.Contenido, C.Fecha_Publicacion, " +
                          "U.Nombre AS AutorNombre, U.Apellido AS AutorApellido " +
                          "FROM Comunicados C " +
                          "INNER JOIN Usuarios U ON C.ID_Usuario_Admin = U.ID_Usuario " +
                          "ORDER BY C.Fecha_Publicacion DESC";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> detalle = new HashMap<>();
                detalle.put("idComunicado", rs.getInt("ID_Comunicado"));
                detalle.put("titulo", rs.getString("Titulo"));
                detalle.put("contenido", rs.getString("Contenido"));
                detalle.put("fechaPublicacion", rs.getTimestamp("Fecha_Publicacion"));
                detalle.put("autor", rs.getString("AutorNombre") + " " + rs.getString("AutorApellido"));

                lista.add(detalle);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    /**
     * Obtiene todos los comunicados registrados (versión simple, sin JOIN).
     * @return Lista de objetos Comunicado.
     */
    public static List<Comunicado> obtenerTodosLosComunicados() {
        List<Comunicado> lista = new ArrayList<>();
        String consulta = "SELECT ID_Comunicado, ID_Usuario_Admin, Titulo, Contenido, Fecha_Publicacion " +
                          "FROM Comunicados ORDER BY Fecha_Publicacion DESC";

        try (Connection conexion = ConexionBD.abrirConexion();
             PreparedStatement stmt = conexion.prepareStatement(consulta);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Comunicado c = new Comunicado(
                        rs.getInt("ID_Comunicado"),
                        rs.getInt("ID_Usuario_Admin"),
                        rs.getString("Titulo"),
                        rs.getString("Contenido"),
                        rs.getTimestamp("Fecha_Publicacion")
                );
                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}