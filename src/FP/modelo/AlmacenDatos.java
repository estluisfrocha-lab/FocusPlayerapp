package FP.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AlmacenDatos implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Usuario> usuarios = new ArrayList<>();
    private Queue<Sonido> colaSonidos = new LinkedList<>();
    private int[][][] imagenFondo;

    public static AlmacenDatos conDatosIniciales() {
        AlmacenDatos almacen = new AlmacenDatos();
        almacen.completarDatosIniciales();
        return almacen;
    }

    public void completarDatosIniciales() {
        if (colaSonidos == null) {
            colaSonidos = new LinkedList<>();
        }

        agregarSonidoSiNoExiste(new Sonido("Lluvia", "/FP/sonidos/lluvia.wav"));
        agregarSonidoSiNoExiste(new Sonido("Bosque", "/FP/sonidos/bosque.wav"));
        agregarSonidoSiNoExiste(new Sonido("Olas", "/FP/sonidos/olas.wav"));

        if (usuarios.isEmpty()) {
            Sonido sonidoInicial = buscarSonidoPorNombre("Lluvia");
            Configuracion configPorDefecto = new Configuracion(25, 5, 60, sonidoInicial);
            usuarios.add(new Usuario("Usuario Demo", "1234", configPorDefecto));
        }
    }

    private void agregarSonidoSiNoExiste(Sonido nuevo) {
        for (Sonido existente : getColaSonidos()) {
            if (existente.getNombre().equalsIgnoreCase(nuevo.getNombre())) {
                return;
            }
        }
        colaSonidos.offer(nuevo);
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /** Busca un usuario por su nombre, sin importar mayúsculas o minúsculas. */
    public Usuario buscarUsuarioPorNombre(String nombre) {
        if (nombre == null) {
            return null;
        }
        String buscado = nombre.trim();
        for (Usuario u : usuarios) {
            if (u.getNombre().equalsIgnoreCase(buscado)) {
                return u;
            }
        }
        return null;
    }

    /** Registra un nuevo perfil si el nombre no está repetido. */
    public boolean registrarUsuario(Usuario nuevo) {
        if (nuevo == null || buscarUsuarioPorNombre(nuevo.getNombre()) != null) {
            return false;
        }
        usuarios.add(nuevo);
        return true;
    }

    // --- Gestión de Cola de Sonidos ---

    public Queue<Sonido> getColaSonidos() {
        if (colaSonidos == null) {
            colaSonidos = new LinkedList<>();
        }
        return colaSonidos;
    }

    /** Retorna la cantidad de sonidos disponibles en la aplicación. */
    public int getCantidadSonidos() {
        return getColaSonidos().size();
    }

    public Sonido buscarSonidoPorNombre(String nombre) {
        for (Sonido s : getColaSonidos()) {
            if (s.getNombre().equalsIgnoreCase(nombre)) {
                return s;
            }
        }
        return null;
    }

    // --- Gestión de Matriz de Imagen de Fondo (1920x1080 RGB) ---

    public int[][][] getImagenFondo() {
        return imagenFondo;
    }

    public void setImagenFondo(int[][][] imagenFondo) {
        this.imagenFondo = imagenFondo;
    }
}
