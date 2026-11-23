
package FP.datos;

import FP.interfaces.PersistenciaDatos;
import FP.modelo.AlmacenDatos;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/** Guarda y carga el AlmacenDatos en un archivo .dat (serialización). */
public class RepositorioArchivo implements PersistenciaDatos {

    // Ruta relativa a la carpeta desde donde se ejecuta el proyecto
    private final Path ruta;

    public RepositorioArchivo() {
        this(Paths.get("datos", "focusplayer.dat"));
    }

    public RepositorioArchivo(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public AlmacenDatos cargar() throws IOException, ClassNotFoundException {
        if (!Files.exists(ruta)) {
            // Primer arranque: aún no hay archivo, se crean datos iniciales
            return AlmacenDatos.conDatosIniciales();
        }
        // Si el archivo existe pero está dañado se lanza la excepción:
        // NO se reemplaza por una lista vacía para no perder datos.
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(ruta))) {
            return (AlmacenDatos) in.readObject();
        }
    }

    @Override
    public void guardar(AlmacenDatos datos) throws IOException {
        if (ruta.getParent() != null) {
            Files.createDirectories(ruta.getParent());
        }
        // Se escribe primero en un temporal y luego se reemplaza el archivo real,
        // así un fallo a mitad de escritura no daña focusplayer.dat.
        Path temporal = Paths.get(ruta.toString() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temporal))) {
            out.writeObject(datos);
        }
        Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
    }
}