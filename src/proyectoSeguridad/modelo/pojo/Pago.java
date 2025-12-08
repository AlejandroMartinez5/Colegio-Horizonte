package proyectoSeguridad.modelo.pojo;

public class Pago {
    
    private int idPago;
    private int idAlumno;
    private double monto;
    private String concepto;
    private String fechaPago;

    public Pago() {
    }

    public Pago(int idPago, int idAlumno, double monto, String concepto, String fechaPago) {
        this.idPago = idPago;
        this.idAlumno = idAlumno;
        this.monto = monto;
        this.concepto = concepto;
        this.fechaPago = fechaPago;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }
}