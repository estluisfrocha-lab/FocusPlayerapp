
package focusplayerapp;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import FP.datos.RepositorioArchivo;
import FP.interfaces.PersistenciaDatos;
import FP.interfaz.BienvenidaFrame;
import FP.interfaz.LoginFrame;
import FP.modelo.AlmacenDatos;


public class FocusPlayerApp {
    public static void main(String[] args) {
       PersistenciaDatos repositorio = new RepositorioArchivo();
        AlmacenDatos almacen;

        try {
            almacen = repositorio.cargar();
            // Si es la primera ejecución, carga los sonidos base (Lluvia, Bosque, Olas)
            // y un perfil de usuario por defecto si la lista está vacía.
            almacen.completarDatosIniciales();
            repositorio.guardar(almacen); // crea/actualiza datos/focusplayer.dat
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudieron cargar los datos:\n" + e.getMessage()
                    + "\n\nRevisa el archivo datos/focusplayer.dat (no se sobrescribió).",
                    "Error al iniciar Focus Player", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final AlmacenDatos datos = almacen;

        SwingUtilities.invokeLater(() -> {
            BienvenidaFrame bienvenida = new BienvenidaFrame(() -> {
                // Abre la pantalla para ingresar/seleccionar el perfil (Documento y Nombre)
                LoginFrame login = new LoginFrame(datos, repositorio);
                login.setLocationRelativeTo(null);
                login.setVisible(true);
            });

            bienvenida.setLocationRelativeTo(null);
            bienvenida.setVisible(true);
        });
    }
}