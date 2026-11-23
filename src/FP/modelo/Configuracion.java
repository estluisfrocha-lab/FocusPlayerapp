
package FP.modelo;

import java.io.Serializable;

public class Configuracion implements Serializable {
    private static final long serialVersionUID = 1L;

    private int minutosTrabajo;
    private int minutosDescanso;
    private int volumen;
    private Sonido sonidoActual;

    /** Constructor por defecto con los valores base del diseño (25 min, 5 min, 60%). */
    public Configuracion() {
        this(25, 5, 60, null);
    }

    /** Constructor completo usado al inicializar o personalizar un perfil de usuario. */
    public Configuracion(int minutosTrabajo, int minutosDescanso, int volumen, Sonido sonidoActual) {
        setMinutosTrabajo(minutosTrabajo);
        setMinutosDescanso(minutosDescanso);
        setVolumen(volumen);
        setSonido(sonidoActual);
    }

    // --- Métodos del Diagrama de Clases ---

    public void setMinutosTrabajo(int minutosTrabajo) {
        if (minutosTrabajo < 1) {
            minutosTrabajo = 1;
        }
        this.minutosTrabajo = minutosTrabajo;
    }

    public void setMinutosDescanso(int minutosDescanso) {
        if (minutosDescanso < 1) {
            minutosDescanso = 1;
        }
        this.minutosDescanso = minutosDescanso;
    }

    public void setVolumen(int volumen) {
        if (volumen < 0) volumen = 0;
        if (volumen > 100) volumen = 100;
        this.volumen = volumen;

        // Sincroniza el volumen en tiempo real si ya hay un sonido asignado
        if (this.sonidoActual != null) {
            this.sonidoActual.setVolumen(this.volumen);
        }
    }

    public void setSonido(Sonido sonido) {
        this.sonidoActual = sonido;
        if (this.sonidoActual != null) {
            this.sonidoActual.setVolumen(this.volumen);
        }
    }

    // --- Getters para la interfaz y el temporizador ---

    public int getMinutosTrabajo() {
        return minutosTrabajo;
    }

    public int getMinutosDescanso() {
        return minutosDescanso;
    }

    public int getVolumen() {
        return volumen;
    }

    public Sonido getSonidoActual() {
        return sonidoActual;
    }
}