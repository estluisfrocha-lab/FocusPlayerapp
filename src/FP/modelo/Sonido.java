package FP.modelo;

import java.io.Serializable;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.Timer;

public class Sonido implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String rutaArchivo;
    private int volumen = 60;

    private transient Clip clip;
    private transient Timer timerVistaPrevia;

    public Sonido(String nombre, String rutaArchivo) {
        this.nombre = nombre;
        this.rutaArchivo = rutaArchivo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public void reproducir() {
        iniciarAudio(true);
    }

    public void reproducirVistaPrevia() {
        iniciarAudio(false);
        if (timerVistaPrevia != null && timerVistaPrevia.isRunning()) {
            timerVistaPrevia.stop();
        }
        timerVistaPrevia = new Timer(8000, e -> detener());
        timerVistaPrevia.setRepeats(false);
        timerVistaPrevia.start();
    }

    private void iniciarAudio(boolean enBucle) {
        detener();
        try {
            String rutaNormalizada = rutaArchivo.replace("/focusplayer/", "/FP/");
            URL url = getClass().getResource(rutaNormalizada);

            if (url == null) {
                System.err.println("[Focus Player - Audio] No se encontró el archivo: " + rutaNormalizada);
                return;
            }

            AudioInputStream streamOriginal = AudioSystem.getAudioInputStream(url);
            AudioFormat formatoBase = streamOriginal.getFormat();

            AudioFormat formatoCompatible = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    formatoBase.getSampleRate(),
                    16,
                    formatoBase.getChannels(),
                    formatoBase.getChannels() * 2,
                    formatoBase.getSampleRate(),
                    false
            );

            AudioInputStream streamFinal = streamOriginal;
            if (!formatoBase.matches(formatoCompatible)
                    && AudioSystem.isConversionSupported(formatoCompatible, formatoBase)) {
                streamFinal = AudioSystem.getAudioInputStream(formatoCompatible, streamOriginal);
            }

            clip = AudioSystem.getClip();
            clip.open(streamFinal);
            aplicarGanancia(volumen);

            if (enBucle) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("[Focus Player - Audio] Error al reproducir '" + nombre + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void detener() {
        if (timerVistaPrevia != null && timerVistaPrevia.isRunning()) {
            timerVistaPrevia.stop();
        }
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
            clip = null;
        }
    }

    public void setVolumen(int volumen) {
        this.volumen = Math.max(0, Math.min(100, volumen));
        aplicarGanancia(this.volumen);
    }

    private void aplicarGanancia(int porcentaje) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (porcentaje <= 0) {
                gain.setValue(gain.getMinimum());
            } else {
                float dB = (float) (20.0 * Math.log10(porcentaje / 100.0));
                dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
                gain.setValue(dB);
            }
        }
    }
}