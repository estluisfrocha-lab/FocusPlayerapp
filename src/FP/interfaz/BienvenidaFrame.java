package FP.interfaz;

import FP.datos.RepositorioArchivo;
import FP.interfaces.PersistenciaDatos;
import FP.modelo.AlmacenDatos;
import FP.modelo.Sonido;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class BienvenidaFrame extends JFrame {

    private final Color fondoPrincipal    = new Color(0x1E2A38);
    private final Color panelLateral      = new Color(0x17212B);
    private final Color superficieTarjeta = new Color(0x2C3E50);
    private final Color colorPrimario     = new Color(0x4ECDC4);
    private final Color colorDescanso     = new Color(0xFFB88C);
    private final Color textoPrincipal    = new Color(0xF5F5F5);
    private final Color textoSecundario   = new Color(0xA0AEC0);

    private static final String FUENTE_TITULOS = "Poppins";
    private static final String FUENTE_TEXTOS  = "Inter";

    private JPanel panelContenido;
    private CardLayout tarjetas;
    private Sonido sonidoVistaPrevia;

    private final Runnable accionLogin;

    public BienvenidaFrame(Runnable accionLogin) {
        this.accionLogin = accionLogin;

        setTitle("Focus Player - Bienvenida");
        setSize(1050, 680);
        setMinimumSize(new Dimension(850, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        crearMenuLateral();
        crearContenido();
    }

    private void crearMenuLateral() {
        JPanel menu = new JPanel();
        menu.setBackground(panelLateral);
        menu.setPreferredSize(new Dimension(215, 0));
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(25, 15, 20, 15));

        JLabel logo = new JLabel("Focus Player");
        logo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 24));
        logo.setForeground(colorPrimario);
        logo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtituloMarca = new JLabel("ENFOQUE & PRODUCTIVIDAD");
        subtituloMarca.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 10));
        subtituloMarca.setForeground(textoSecundario);
        subtituloMarca.setAlignmentX(CENTER_ALIGNMENT);

        menu.add(logo);
        menu.add(Box.createVerticalStrut(5));
        menu.add(subtituloMarca);
        menu.add(Box.createVerticalStrut(40));

        agregarBotonMenu(menu, "⌂   Inicio", "inicio");
        agregarBotonMenu(menu, "♪   Sonidos", "sonidos");
        agregarBotonMenu(menu, "◷   Método Pomodoro", "metodo");

        menu.add(Box.createVerticalGlue());

        JButton acceso = new JButton("Iniciar sesión");
        acceso.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 14));
        acceso.setForeground(fondoPrincipal);
        acceso.setBackground(colorPrimario);
        acceso.setOpaque(true);
        acceso.setBorderPainted(false);
        acceso.setFocusPainted(false);
        acceso.setCursor(new Cursor(Cursor.HAND_CURSOR));
        acceso.setAlignmentX(CENTER_ALIGNMENT);
        acceso.setMaximumSize(new Dimension(180, 43));
        acceso.setPreferredSize(new Dimension(180, 43));

        acceso.addActionListener(e -> abrirLogin());

        menu.add(acceso);
        menu.add(Box.createVerticalStrut(18));

        JLabel pie = new JLabel("Concentración en cada ciclo");
        pie.setForeground(textoSecundario);
        pie.setFont(new Font(FUENTE_TEXTOS, Font.ITALIC, 11));
        pie.setAlignmentX(CENTER_ALIGNMENT);
        menu.add(pie);

        add(menu, BorderLayout.WEST);
    }

    private void agregarBotonMenu(JPanel menu, String nombre, String pagina) {
        JButton boton = new JButton(nombre);
        boton.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 14));
        boton.setForeground(textoPrincipal);
        boton.setBackground(panelLateral);
        boton.setOpaque(true);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setMaximumSize(new Dimension(195, 45));
        boton.setPreferredSize(new Dimension(195, 45));
        boton.setAlignmentX(CENTER_ALIGNMENT);
        boton.setMargin(new Insets(10, 12, 10, 5));

        boton.addActionListener(e -> {
            detenerVistaPrevia();
            tarjetas.show(panelContenido, pagina);
        });

        menu.add(boton);
        menu.add(Box.createVerticalStrut(8));
    }

    private void crearContenido() {
        tarjetas = new CardLayout();
        panelContenido = new JPanel(tarjetas);
        panelContenido.setBackground(fondoPrincipal);

        panelContenido.add(crearInicio(), "inicio");
        panelContenido.add(crearVistaSonidos(), "sonidos");
        panelContenido.add(crearVistaMetodo(), "metodo");

        add(panelContenido, BorderLayout.CENTER);
    }

    private JPanel crearInicio() {
        JPanel pagina = new JPanel(new BorderLayout());
        pagina.setBackground(fondoPrincipal);

        JPanel contenido = new JPanel();
        contenido.setBackground(fondoPrincipal);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));

        JLabel etiqueta = new JLabel("FOCUS PLAYER  |  TÉCNICA POMODORO & SONIDOS AMBIENTALES");
        etiqueta.setForeground(colorPrimario);
        etiqueta.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 12));
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);

        JPanel portada = new JPanel(new BorderLayout(20, 10));
        portada.setBackground(superficieTarjeta);
        portada.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3B5066), 1),
                BorderFactory.createEmptyBorder(24, 26, 24, 24)));
        portada.setMaximumSize(new Dimension(Integer.MAX_VALUE, 275));
        portada.setAlignmentX(LEFT_ALIGNMENT);

        JPanel textoPortada = new JPanel();
        textoPortada.setOpaque(false);
        textoPortada.setLayout(new BoxLayout(textoPortada, BoxLayout.Y_AXIS));

        JLabel pequeno = new JLabel("ORGANIZA TU TIEMPO Y MIDE TU CONSTANCIA");
        pequeno.setForeground(colorDescanso);
        pequeno.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 11));

        JLabel titulo = new JLabel("<html>Mantén tu enfoque<br>en cada ciclo.</html>");
        titulo.setForeground(textoPrincipal);
        titulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 32));

        JLabel descripcion = new JLabel(
                "<html>Alterna sesiones de trabajo y descanso acompañadas<br>"
                + "de sonidos de la naturaleza y revisa tu progreso.</html>");
        descripcion.setForeground(textoSecundario);
        descripcion.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 14));

        JButton comenzar = new JButton("Ingresar a mi perfil  →");
        comenzar.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 13));
        comenzar.setForeground(fondoPrincipal);
        comenzar.setBackground(colorPrimario);
        comenzar.setOpaque(true);
        comenzar.setBorderPainted(false);
        comenzar.setFocusPainted(false);
        comenzar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comenzar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        comenzar.setAlignmentX(LEFT_ALIGNMENT);
        comenzar.addActionListener(e -> abrirLogin());

        textoPortada.add(pequeno);
        textoPortada.add(Box.createVerticalStrut(10));
        textoPortada.add(titulo);
        textoPortada.add(Box.createVerticalStrut(10));
        textoPortada.add(descripcion);
        textoPortada.add(Box.createVerticalStrut(18));
        textoPortada.add(comenzar);

        JLabel fotoPrincipal = imagen("/FP/imagenes/portada_focus.png", 290, 215);
        fotoPrincipal.setHorizontalAlignment(SwingConstants.CENTER);
        fotoPrincipal.setBorder(BorderFactory.createLineBorder(colorPrimario, 1));

        portada.add(textoPortada, BorderLayout.CENTER);
        portada.add(fotoPrincipal, BorderLayout.EAST);

        JLabel subtitulo = new JLabel("AMBIENTES SONOROS DISPONIBLES");
        subtitulo.setForeground(textoPrincipal);
        subtitulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 19));
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel apoyo = new JLabel("Selecciona el entorno auditivo ideal para tus sesiones de estudio o trabajo");
        apoyo.setForeground(textoSecundario);
        apoyo.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 12));
        apoyo.setAlignmentX(LEFT_ALIGNMENT);

        JPanel tarjetasSonidos = new JPanel(new GridLayout(1, 3, 16, 0));
        tarjetasSonidos.setOpaque(false);
        tarjetasSonidos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 205));
        tarjetasSonidos.setAlignmentX(LEFT_ALIGNMENT);

        tarjetasSonidos.add(crearTarjetaSonido(
                "/FP/imagenes/lluvia.png", "Lluvia", "Sonido continuo de lluvia suave"));
        tarjetasSonidos.add(crearTarjetaSonido(
                "/FP/imagenes/bosque.png", "Bosque", "Ambiente natural y calma profunda"));
        tarjetasSonidos.add(crearTarjetaSonido(
                "/FP/imagenes/olas.png", "Olas", "Ritmo constante de marea relajante"));

        contenido.add(etiqueta);
        contenido.add(Box.createVerticalStrut(13));
        contenido.add(portada);
        contenido.add(Box.createVerticalStrut(23));
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(4));
        contenido.add(apoyo);
        contenido.add(Box.createVerticalStrut(12));
        contenido.add(tarjetasSonidos);

        JScrollPane desplazamiento = new JScrollPane(contenido);
        desplazamiento.setBorder(BorderFactory.createEmptyBorder());
        desplazamiento.getVerticalScrollBar().setUnitIncrement(16);
        desplazamiento.getViewport().setBackground(fondoPrincipal);
        pagina.add(desplazamiento, BorderLayout.CENTER);
        return pagina;
    }

    private JPanel crearTarjetaSonido(String ruta, String nombre, String descripcion) {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(superficieTarjeta);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3B5066), 1),
                BorderFactory.createEmptyBorder(10, 10, 12, 10)));

        JLabel foto = imagen(ruta, 165, 110);
        foto.setAlignmentX(CENTER_ALIGNMENT);
        foto.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titulo = new JLabel(nombre);
        titulo.setForeground(textoPrincipal);
        titulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 16));
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel detalle = new JLabel(descripcion);
        detalle.setForeground(textoSecundario);
        detalle.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 11));
        detalle.setAlignmentX(CENTER_ALIGNMENT);

        tarjeta.add(foto);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(3));
        tarjeta.add(detalle);
        return tarjeta;
    }

    
    private JPanel crearVistaSonidos() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(fondoPrincipal);
        panel.setBorder(BorderFactory.createEmptyBorder(36, 36, 32, 36));

        
        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Sonidos de la naturaleza");
        titulo.setForeground(colorPrimario);
        titulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 28));

        JLabel subtitulo = new JLabel("Prueba cada ambiente sonoro (vista previa de 8 segundos) antes de iniciar sesión.");
        subtitulo.setForeground(textoSecundario);
        subtitulo.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 14));

        cabecera.add(titulo);
        cabecera.add(Box.createVerticalStrut(6));
        cabecera.add(subtitulo);
        panel.add(cabecera, BorderLayout.NORTH);

       
        JPanel filaTarjetas = new JPanel(new GridLayout(1, 3, 18, 0));
        filaTarjetas.setOpaque(false);

        filaTarjetas.add(crearTarjetaPreviewSonido(
                "/FP/imagenes/lluvia.png",
                new Sonido("Lluvia", "/FP/sonidos/lluvia.wav"),
                "Gotas constantes para bloquear distracciones."));

        filaTarjetas.add(crearTarjetaPreviewSonido(
                "/FP/imagenes/bosque.png",
                new Sonido("Bosque", "/FP/sonidos/bosque.wav"),
                "Brisa entre árboles y calma profunda."));

        filaTarjetas.add(crearTarjetaPreviewSonido(
                "/FP/imagenes/olas.png",
                new Sonido("Olas", "/FP/sonidos/olas.wav"),
                "Vaivén suave del mar para mantener el ritmo."));

        panel.add(filaTarjetas, BorderLayout.CENTER);

        
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        pie.setOpaque(false);

        JButton btnDetener = new JButton("⏹ Detener vista previa");
        btnDetener.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 13));
        btnDetener.setBackground(superficieTarjeta);
        btnDetener.setForeground(colorDescanso);
        btnDetener.setOpaque(true);
        btnDetener.setFocusPainted(false);
        btnDetener.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetener.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorDescanso, 1),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)));
        btnDetener.addActionListener(e -> detenerVistaPrevia());

        JButton btnIngresar = new JButton("Iniciar sesión  →");
        btnIngresar.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 13));
        btnIngresar.setBackground(colorPrimario);
        btnIngresar.setForeground(fondoPrincipal);
        btnIngresar.setOpaque(true);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnIngresar.addActionListener(e -> abrirLogin());

        pie.add(btnDetener);
        pie.add(btnIngresar);
        panel.add(pie, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearTarjetaPreviewSonido(String rutaImagen, Sonido sonido, String descripcion) {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(superficieTarjeta);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x3B5066), 1),
                BorderFactory.createEmptyBorder(16, 16, 18, 16)));

        JLabel foto = imagen(rutaImagen, 200, 135);
        foto.setAlignmentX(CENTER_ALIGNMENT);
        foto.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblNombre = new JLabel(sonido.getNombre());
        lblNombre.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 18));
        lblNombre.setForeground(textoPrincipal);
        lblNombre.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel("<html><center>" + descripcion + "</center></html>");
        lblDesc.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 12));
        lblDesc.setForeground(textoSecundario);
        lblDesc.setAlignmentX(CENTER_ALIGNMENT);

        JButton btnEscuchar = new JButton("▶ Escuchar vista previa");
        btnEscuchar.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 13));
        btnEscuchar.setForeground(fondoPrincipal);
        btnEscuchar.setBackground(colorPrimario);
        btnEscuchar.setOpaque(true);
        btnEscuchar.setBorderPainted(false);
        btnEscuchar.setFocusPainted(false);
        btnEscuchar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEscuchar.setAlignmentX(CENTER_ALIGNMENT);
        btnEscuchar.setMaximumSize(new Dimension(210, 38));
        btnEscuchar.setPreferredSize(new Dimension(210, 38));

        btnEscuchar.addActionListener(e -> {
            detenerVistaPrevia();
            sonidoVistaPrevia = sonido;
            sonidoVistaPrevia.setVolumen(75);
            sonidoVistaPrevia.reproducirVistaPrevia();
        });

        tarjeta.add(foto);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(lblNombre);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(lblDesc);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(btnEscuchar);

        return tarjeta;
    }

    private void detenerVistaPrevia() {
        if (sonidoVistaPrevia != null) {
            sonidoVistaPrevia.detener();
            sonidoVistaPrevia = null;
        }
    }

    private JLabel imagen(String ruta, int ancho, int alto) {
        java.net.URL recurso = getClass().getResource(ruta);
        JLabel etiqueta = new JLabel();
        etiqueta.setPreferredSize(new Dimension(ancho, alto));
        etiqueta.setMinimumSize(new Dimension(ancho, alto));
        etiqueta.setMaximumSize(new Dimension(ancho, alto));
        etiqueta.setOpaque(true);
        etiqueta.setBackground(fondoPrincipal);

        if (recurso != null) {
            ImageIcon original = new ImageIcon(recurso);
            Image escalada = original.getImage().getScaledInstance(
                    ancho, alto, Image.SCALE_SMOOTH);
            etiqueta.setIcon(new ImageIcon(escalada));
        } else {
            etiqueta.setText("Focus Player");
            etiqueta.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 13));
            etiqueta.setForeground(colorPrimario);
            etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        }
        return etiqueta;
    }

    private JPanel crearVistaMetodo() {
        return paginaInformativa(
                "Técnica Pomodoro y Estadísticas",
                "Define la duración de tus ciclos de trabajo y descanso. Al completar cada "
                + "sesión, Focus Player registra tus minutos enfocados en tu perfil personal.<br><br>"
                + "Desarrollado por: Samuel Ladino, Fernando Rocha e Isabella Velandia.",
                colorDescanso);
    }

    private JPanel paginaInformativa(String titulo, String descripcion, Color acento) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(fondoPrincipal);
        panel.setBorder(BorderFactory.createEmptyBorder(55, 45, 45, 45));

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JLabel nombre = new JLabel(titulo);
        nombre.setForeground(acento);
        nombre.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 30));

        JLabel descripcionLabel = new JLabel(
                "<html><div style='width:480px; line-height:1.4;'>"
                + descripcion + "</div></html>");
        descripcionLabel.setForeground(textoPrincipal);
        descripcionLabel.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 15));

        JButton ingresar = new JButton("Iniciar sesión");
        ingresar.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 14));
        ingresar.setBackground(colorPrimario);
        ingresar.setForeground(fondoPrincipal);
        ingresar.setOpaque(true);
        ingresar.setBorderPainted(false);
        ingresar.setFocusPainted(false);
        ingresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ingresar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        ingresar.addActionListener(e -> abrirLogin());

        contenido.add(nombre);
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(descripcionLabel);
        contenido.add(Box.createVerticalStrut(28));
        contenido.add(ingresar);

        panel.add(contenido, BorderLayout.NORTH);
        return panel;
    }

    private void abrirLogin() {
        detenerVistaPrevia();
        dispose();
        if (accionLogin != null) {
            accionLogin.run();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PersistenciaDatos repositorio = new RepositorioArchivo();
            AlmacenDatos datos;
            try {
                datos = repositorio.cargar();
                datos.completarDatosIniciales();
            } catch (Exception e) {
                datos = AlmacenDatos.conDatosIniciales();
            }

            final AlmacenDatos almacenFinal = datos;

            BienvenidaFrame ventana = new BienvenidaFrame(() -> {
                LoginFrame login = new LoginFrame(almacenFinal, repositorio);
                login.setAlIngresar(usuario -> {
                    login.dispose();
                    new MenuPrincipalFrame(usuario, almacenFinal, repositorio).setVisible(true);
                });
                login.setVisible(true);
            });

            ventana.setVisible(true);
        });
    }
}