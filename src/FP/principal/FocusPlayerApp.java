package FP.principal;

import FP.datos.RepositorioArchivo;
import FP.interfaces.PersistenciaDatos;
import FP.interfaz.BienvenidaFrame;
import FP.interfaz.LoginFrame;
import FP.interfaz.MenuPrincipalFrame;
import FP.modelo.AlmacenDatos;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class FocusPlayerApp {

    public static void main(String[] args) {
        PersistenciaDatos repositorio = new RepositorioArchivo();
        AlmacenDatos almacen;

        try {
            almacen = repositorio.cargar();
            almacen.completarDatosIniciales();
            repositorio.guardar(almacen);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "No se pudieron cargar los datos:\n" + e.getMessage()
                    + "\n\nRevisa el archivo datos/focusplayer.dat.",
                    "Error al iniciar Focus Player", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final AlmacenDatos datos = almacen;

        SwingUtilities.invokeLater(() -> {
            BienvenidaFrame bienvenida = new BienvenidaFrame(() -> {
                LoginFrame login = new LoginFrame(datos, repositorio);
                    login.setAlIngresar(usuario -> {
    login.dispose();
    new MenuPrincipalFrame (usuario, datos, repositorio).setVisible(true);
});
                login.setLocationRelativeTo(null);
                login.setVisible(true);
            });

            bienvenida.setLocationRelativeTo(null);
            bienvenida.setVisible(true);
        });
    }
}