package FP.gestion;

import FP.interfaces.PersistenciaDatos;
import FP.modelo.AlmacenDatos;
import FP.modelo.Configuracion;
import FP.modelo.Usuario;
import java.io.IOException;

/** Reglas para iniciar sesión y crear perfiles (la ventana de login solo usa esta clase). */
public class GestorUsuarios {

    private final AlmacenDatos almacen;
    private final PersistenciaDatos repositorio;

    public GestorUsuarios(AlmacenDatos almacen, PersistenciaDatos repositorio) {
        this.almacen = almacen;
        this.repositorio = repositorio;
    }

    /** Devuelve el usuario si el nombre y el PIN coinciden; si no, devuelve null. */
    public Usuario iniciarSesion(String nombre, String pin) {
        Usuario usuario = almacen.buscarUsuarioPorNombre(nombre);
        if (usuario != null && usuario.verificarPin(pin)) {
            return usuario;
        }
        return null;
    }

    /**
     * Crea un perfil nuevo con la configuración por defecto (25 min, 5 min, 60 %, sonido Lluvia)
     * y lo guarda en el archivo. Lanza IllegalArgumentException con un mensaje listo para mostrar
     * si el nombre o el PIN no son válidos.
     */
    public Usuario crearPerfil(String nombre, String pin) throws IOException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Escribe un nombre.");
        }
        if (!Usuario.pinValido(pin)) {
            throw new IllegalArgumentException("El PIN debe tener exactamente 4 números.");
        }
        Configuracion config = new Configuracion(25, 5, 60, almacen.buscarSonidoPorNombre("Lluvia"));
        Usuario nuevo = new Usuario(nombre.trim(), pin, config);
        if (!almacen.registrarUsuario(nuevo)) {
            throw new IllegalArgumentException("Ya existe un perfil con ese nombre.");
        }
        repositorio.guardar(almacen);
        return nuevo;
    }
}
