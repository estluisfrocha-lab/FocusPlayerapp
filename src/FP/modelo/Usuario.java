package FP.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String pin;
    private Configuracion configuracion;
    private List<Sesion> historial;

    /** Constructor completo usado al registrar o inicializar un perfil con su configuración. */
    public Usuario(String nombre, String pin, Configuracion configuracion) {
        this.nombre = nombre;
        this.pin = pin;
        this.configuracion = (configuracion != null) ? configuracion : new Configuracion();
        this.historial = new ArrayList<>();
    }

    /** Constructor sobrecargado que asigna una configuración por defecto automáticamente. */
    public Usuario(String nombre, String pin) {
        this(nombre, pin, new Configuracion());
    }

    /** Un PIN válido son exactamente 4 dígitos (0 a 9). */
    public static boolean pinValido(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }

    // --- Métodos del Diagrama de Clases ---

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Compara el PIN escrito con el PIN que el usuario creó. */
    public boolean verificarPin(String intento) {
        return pin != null && pin.equals(intento);
    }

    public Configuracion getConfiguracion() {
        if (configuracion == null) {
            configuracion = new Configuracion();
        }
        return configuracion;
    }

    public void setConfiguracion(Configuracion configuracion) {
        this.configuracion = configuracion;
    }

    /** Agrega una sesión de enfoque completada al historial del usuario. */
    public void agregarSesion(Sesion sesion) {
        if (sesion != null) {
            getHistorial().add(sesion);
        }
    }

    /** Devuelve la lista de sesiones registradas para calcular las estadísticas. */
    public List<Sesion> getHistorial() {
        if (historial == null) {
            historial = new ArrayList<>();
        }
        return historial;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
