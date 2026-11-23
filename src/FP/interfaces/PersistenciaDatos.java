package FP.interfaces;

import java.io.IOException;
import FP.modelo.AlmacenDatos;

public interface PersistenciaDatos {
    AlmacenDatos cargar() throws IOException, ClassNotFoundException;
    void guardar(AlmacenDatos datos) throws IOException;
}
