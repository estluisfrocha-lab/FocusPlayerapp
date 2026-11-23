
package FP.modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Sesion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fecha;
    private int minutosTrabajados;
    private int ciclos;

    /** Constructor completo según el diagrama de clases. */
    public Sesion(String fecha, int minutosTrabajados, int ciclos) {
        this.fecha = (fecha != null && !fecha.trim().isEmpty()) ? fecha : LocalDate.now().toString();
        this.minutosTrabajados = Math.max(0, minutosTrabajados);
        this.ciclos = Math.max(0, ciclos);
    }

    /** Constructor práctico que asigna automáticamente la fecha actual (formato AAAA-MM-DD). */
    public Sesion(int minutosTrabajados, int ciclos) {
        this(LocalDate.now().toString(), minutosTrabajados, ciclos);
    }

    // --- Métodos del Diagrama de Clases ---

    public String getFecha() {
        return fecha;
    }

    public int getMinutosTrabajados() {
        return minutosTrabajados;
    }

    public int getCiclos() {
        return ciclos;
    }

    @Override
    public String toString() {
        return "Fecha: " + fecha + " | Minutos: " + minutosTrabajados + " | Ciclos: " + ciclos;
    }
}