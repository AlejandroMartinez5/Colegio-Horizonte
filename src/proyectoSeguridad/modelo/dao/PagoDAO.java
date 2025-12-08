package proyectoSeguridad.modelo.dao;

import proyectoSeguridad.modelo.pojo.Pago;
import proyectoSeguridad.modelo.ConexionBD;
import java.sql.*;
import java.util.ArrayList;

public class PagoDAO {


    public static boolean agregarPago(Pago pago) {
        String sql = "INSERT INTO pagos (ID_Alumno, Monto, Concepto, Fecha_Pago) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pago.getIdAlumno());
            ps.setDouble(2, pago.getMonto());
            ps.setString(3, pago.getConcepto());
            ps.setDate(4, Date.valueOf(pago.getFechaPago()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar pago: " + e.getMessage());
            return false;
        }
    }

    // Actualizar un pago existente
    public static boolean actualizarPago(Pago pago) {
        String sql = "UPDATE pagos SET ID_Alumno = ?, Monto = ?, Concepto = ?, Fecha_Pago = ? WHERE ID_Pago = ?";
        try (Connection conn = ConexionBD.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pago.getIdAlumno());
            ps.setDouble(2, pago.getMonto());
            ps.setString(3, pago.getConcepto());
            ps.setDate(4, Date.valueOf(pago.getFechaPago()));
            ps.setInt(5, pago.getIdPago());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar pago: " + e.getMessage());
            return false;
        }
    }

    // Eliminar un pago
    public static boolean eliminarPago(int idPago) {
        String sql = "DELETE FROM pagos WHERE ID_Pago = ?";
        try (Connection conn = ConexionBD.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPago);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar pago: " + e.getMessage());
            return false;
        }
    }

    // Obtener todos los pagos
    public static ArrayList<Pago> obtenerPagos() {
        ArrayList<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagos";
        try (Connection conn = ConexionBD.abrirConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Pago pago = new Pago();
                pago.setIdPago(rs.getInt("ID_Pago"));
                pago.setIdAlumno(rs.getInt("ID_Alumno"));
                pago.setMonto(rs.getDouble("Monto"));
                pago.setConcepto(rs.getString("Concepto"));
                pago.setFechaPago(rs.getDate("Fecha_Pago").toString());
                lista.add(pago);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pagos: " + e.getMessage());
        }
        return lista;
    }

    // Obtener un pago por ID
    public static Pago obtenerPagoPorId(int idPago) {
        String sql = "SELECT * FROM pagos WHERE ID_Pago = ?";
        try (Connection conn = ConexionBD.abrirConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPago);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pago pago = new Pago();
                    pago.setIdPago(rs.getInt("ID_Pago"));
                    pago.setIdAlumno(rs.getInt("ID_Alumno"));
                    pago.setMonto(rs.getDouble("Monto"));
                    pago.setConcepto(rs.getString("Concepto"));
                    pago.setFechaPago(rs.getDate("Fecha_Pago").toString());
                    return pago;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pago por ID: " + e.getMessage());
        }
        return null;
    }
}
