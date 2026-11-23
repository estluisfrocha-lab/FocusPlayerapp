package FP.interfaz;

import FP.gestion.GestorUsuarios;
import FP.interfaces.PersistenciaDatos;
import FP.modelo.AlmacenDatos;
import FP.modelo.Usuario;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/** Pantalla de Iniciar sesión / Crear perfil (solo nombre y PIN de 4 dígitos). */
public class LoginFrame extends JFrame {

    // Paleta de la sección 4.1 del informe
    private final Color fondoPrincipal    = new Color(0x1E2A38);
    private final Color superficieTarjeta = new Color(0x2C3E50);
    private final Color colorPrimario     = new Color(0x4ECDC4);
    private final Color colorDescanso     = new Color(0xFFB88C);
    private final Color textoPrincipal    = new Color(0xF5F5F5);
    private final Color textoSecundario   = new Color(0xA0AEC0);
    private final Color bordeCampo        = new Color(0x4A5E73);

    private static final String FUENTE_TITULOS = "Poppins";
    private static final String FUENTE_TEXTOS  = "Inter";

    private final GestorUsuarios gestor;
    private Consumer<Usuario> alIngresar;
    private boolean modoCrear = false;

    private JLabel titulo;
    private JLabel subtitulo;
    private JLabel etiquetaPin;
    private JLabel ayudaPin;
    private JLabel mensaje;
    private JTextField campoNombre;
    private JPasswordField campoPin;
    private JButton botonPrincipal;
    private JButton botonCambio;
    private JLabel pregunta;

    public LoginFrame(AlmacenDatos datos, PersistenciaDatos repositorio) {
        this.gestor = new GestorUsuarios(datos, repositorio);
        // Mientras no exista la pantalla de Inicio, solo se muestra un aviso.
        // Cuando exista, se conecta con setAlIngresar(...)
        this.alIngresar = usuario -> JOptionPane.showMessageDialog(this,
                "Sesión iniciada como " + usuario.getNombre()
                + ".\nAquí se abrirá la pantalla de Inicio.",
                "Focus Player", JOptionPane.INFORMATION_MESSAGE);

        setTitle("Focus Player - Inicio de sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 640);
        setMinimumSize(new Dimension(460, 600));
        setLocationRelativeTo(null);

        construirInterfaz();
        actualizarModo();
    }

    /** Acción que se ejecuta cuando el usuario entra o crea su perfil con éxito. */
    public void setAlIngresar(Consumer<Usuario> alIngresar) {
        this.alIngresar = alIngresar;
    }

    private void construirInterfaz() {
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(fondoPrincipal);

        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(superficieTarjeta);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createEmptyBorder(36, 36, 30, 36));
        tarjeta.setPreferredSize(new Dimension(400, 520));

        JLabel logo = crearEtiqueta("Focus Player", FUENTE_TITULOS, Font.BOLD, 26, colorPrimario);
        JLabel lema = crearEtiqueta("Concentración con ciclos de trabajo y descanso",
                FUENTE_TEXTOS, Font.PLAIN, 12, textoSecundario);

        titulo = crearEtiqueta("Iniciar sesión", FUENTE_TITULOS, Font.BOLD, 24, textoPrincipal);
        subtitulo = crearEtiqueta(" ", FUENTE_TEXTOS, Font.PLAIN, 12, textoSecundario);

        JLabel etiquetaNombre = crearEtiqueta("Nombre", FUENTE_TEXTOS, Font.PLAIN, 13, textoSecundario);
        campoNombre = crearCampoTexto();

        etiquetaPin = crearEtiqueta("PIN (4 dígitos)", FUENTE_TEXTOS, Font.PLAIN, 13, textoSecundario);
        campoPin = new JPasswordField();
        estilizarCampo(campoPin);
        ((AbstractDocument) campoPin.getDocument()).setDocumentFilter(new FiltroPin());

        ayudaPin = crearEtiqueta("Elige 4 números que recuerdes; los usarás para entrar.",
                FUENTE_TEXTOS, Font.PLAIN, 11, textoSecundario);

        mensaje = crearEtiqueta(" ", FUENTE_TEXTOS, Font.BOLD, 12, colorDescanso);

        botonPrincipal = new JButton();
        botonPrincipal.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 14));
        botonPrincipal.setForeground(fondoPrincipal);
        botonPrincipal.setBackground(colorPrimario);
        botonPrincipal.setOpaque(true);
        botonPrincipal.setBorderPainted(false);
        botonPrincipal.setFocusPainted(false);
        botonPrincipal.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonPrincipal.setAlignmentX(LEFT_ALIGNMENT);
        botonPrincipal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        botonPrincipal.setPreferredSize(new Dimension(300, 46));
        botonPrincipal.addActionListener(e -> procesar());

        pregunta = crearEtiqueta(" ", FUENTE_TEXTOS, Font.PLAIN, 12, textoSecundario);
        pregunta.setHorizontalAlignment(SwingConstants.CENTER);

        botonCambio = new JButton();
        botonCambio.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 12));
        botonCambio.setForeground(colorPrimario);
        botonCambio.setContentAreaFilled(false);
        botonCambio.setOpaque(false);
        botonCambio.setBorderPainted(false);
        botonCambio.setFocusPainted(false);
        botonCambio.setCursor(new Cursor(Cursor.HAND_CURSOR));
        botonCambio.setAlignmentX(LEFT_ALIGNMENT);
        botonCambio.addActionListener(e -> {
            modoCrear = !modoCrear;
            actualizarModo();
        });

        tarjeta.add(logo);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(lema);
        tarjeta.add(Box.createVerticalStrut(26));
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(subtitulo);
        tarjeta.add(Box.createVerticalStrut(20));
        tarjeta.add(etiquetaNombre);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(campoNombre);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(etiquetaPin);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(campoPin);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(ayudaPin);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(mensaje);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(botonPrincipal);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(pregunta);
        tarjeta.add(botonCambio);

        fondo.add(tarjeta);
        setContentPane(fondo);
        getRootPane().setDefaultButton(botonPrincipal);
    }

    /** Cambia los textos según estemos iniciando sesión o creando un perfil. */
    private void actualizarModo() {
        if (modoCrear) {
            titulo.setText("Crear perfil");
            subtitulo.setText("Solo necesitas un nombre y un PIN");
            etiquetaPin.setText("Crea tu PIN (4 dígitos)");
            ayudaPin.setVisible(true);
            botonPrincipal.setText("Crear perfil");
            pregunta.setText("¿Ya tienes perfil?");
            botonCambio.setText("Iniciar sesión");
        } else {
            titulo.setText("Iniciar sesión");
            subtitulo.setText("Entra con tu nombre y tu PIN");
            etiquetaPin.setText("PIN (4 dígitos)");
            ayudaPin.setVisible(false);
            botonPrincipal.setText("Iniciar sesión");
            pregunta.setText("¿Aún no tienes perfil?");
            botonCambio.setText("Crear perfil");
        }
        campoPin.setText("");
        mensaje.setText(" ");
        campoNombre.requestFocusInWindow();
    }

    private void procesar() {
        String nombre = campoNombre.getText().trim();
        String pin = new String(campoPin.getPassword());
        try {
            Usuario usuario;
            if (modoCrear) {
                usuario = gestor.crearPerfil(nombre, pin);
            } else {
                usuario = gestor.iniciarSesion(nombre, pin);
                if (usuario == null) {
                    mensaje.setText("Nombre o PIN incorrecto.");
                    campoPin.setText("");
                    return;
                }
            }
            campoPin.setText("");
            mensaje.setText(" ");
            alIngresar.accept(usuario);
        } catch (IllegalArgumentException ex) {
            mensaje.setText(ex.getMessage());
        } catch (IOException ex) {
            mensaje.setText("No se pudo guardar el perfil: " + ex.getMessage());
        }
    }

    private JLabel crearEtiqueta(String texto, String fuente, int estilo, int tamano, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font(fuente, estilo, tamano));
        etiqueta.setForeground(color);
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        return etiqueta;
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        estilizarCampo(campo);
        return campo;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 15));
        campo.setForeground(textoPrincipal);
        campo.setCaretColor(textoPrincipal);
        campo.setBackground(fondoPrincipal);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bordeCampo, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        campo.setPreferredSize(new Dimension(300, 44));
    }

    /** Deja escribir solo números y máximo 4 caracteres en el campo del PIN. */
    private static class FiltroPin extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr)
                throws BadLocationException {
            replace(fb, offset, 0, texto, attr);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int largo, String texto, AttributeSet attr)
                throws BadLocationException {
            if (texto == null) {
                texto = "";
            }
            String soloNumeros = texto.replaceAll("\\D", "");
            int actual = fb.getDocument().getLength();
            int cabe = 4 - (actual - largo);
            if (cabe <= 0 && !soloNumeros.isEmpty()) {
                return;
            }
            if (soloNumeros.length() > cabe) {
                soloNumeros = soloNumeros.substring(0, Math.max(0, cabe));
            }
            super.replace(fb, offset, largo, soloNumeros, attr);
        }
    }
}
