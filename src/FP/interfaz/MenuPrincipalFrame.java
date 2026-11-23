package FP.interfaz;

import FP.interfaces.PersistenciaDatos;
import FP.modelo.AlmacenDatos;
import FP.modelo.Configuracion;
import FP.modelo.Sesion;
import FP.modelo.Sonido;
import FP.modelo.Usuario;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import java.awt.Font;

import java.awt.FontMetrics;


public class MenuPrincipalFrame extends JFrame {

    private final Usuario usuario;
    private final AlmacenDatos almacen;
    private final PersistenciaDatos persistencia;
    private final JPanel contenido = new JPanel(new CardLayout());


    private static final Color FONDO_PRINCIPAL    = new Color(0x1E2A38); 
    private static final Color PANEL_LATERAL      = new Color(0x17212B); 
    private static final Color SUPERFICIE_TARJETA = new Color(0x2C3E50); 
    private static final Color BORDE_TARJETA      = new Color(0x3B5066);
    private static final Color COLOR_PRIMARIO     = new Color(0x4ECDC4); 
    private static final Color COLOR_DESCANSO     = new Color(0xFFB88C); 
    private static final Color TEXTO_PRINCIPAL    = new Color(0xF5F5F5); 
    private static final Color TEXTO_SECUNDARIO   = new Color(0xA0AEC0);

    
    private static final String FUENTE_TITULOS = "Poppins";
    private static final String FUENTE_TEXTOS  = "Inter";

    
    private final Map<String, JButton> botonesMenu = new HashMap<>();

    
    private Timer timerSwing;
    private int segundosRestantes;
    private String estadoTemporizador = "TRABAJO"; 
    private String estadoPrevioPausa  = "TRABAJO";
    private boolean corriendo = false;
    private int ciclosCompletadosSesion = 0;

    private JLabel lblEstadoCiclo;
    private JLabel lblTiempo;
    private JLabel lblSonidoActivo;
    private JLabel lblCiclosSesion;
    private JButton btnIniciar;
    private JButton btnPausar;
    private JButton btnReiniciar;

    
    private JSlider sliderTrabajo;
    private JSlider sliderDescanso;
    private JSlider sliderVolumenConfig;
    private JLabel lblValorTrabajo;
    private JLabel lblValorDescanso;
    private JLabel lblValorVolumenConfig;
    private BarraVistaPreviaPanel panelVistaPreviaCiclo;

    
    private JLabel lblTotalCiclos;
    private JLabel lblTotalMinutos;
    private JLabel lblTotalSesiones;
    private GraficoSemanalPanel graficoSemanal;

   
    private JSlider sliderVolumenSonidos;
    private JLabel lblValorVolumenSonidos;
    private Sonido sonidoSeleccionadoTemp;
    private Sonido sonidoEnVistaPrevia;
    private final Map<String, JPanel> tarjetasSonido = new HashMap<>();
    private final Map<String, JRadioButton> radiosSonido = new HashMap<>();

    public MenuPrincipalFrame(Usuario usuario, AlmacenDatos almacen, PersistenciaDatos persistencia) {
        this.usuario = usuario;
        this.almacen = almacen;
        this.persistencia = persistencia;

        Configuracion cfg = usuario.getConfiguracion();
        Sonido sonidoInicial = cfg.getSonidoActual();
        if (sonidoInicial == null) {
            sonidoInicial = new Sonido("Lluvia", "/FP/sonidos/lluvia.wav");
            cfg.setSonido(sonidoInicial);
        } else {
            sonidoInicial.setRutaArchivo("/FP/sonidos/" + sonidoInicial.getNombre().toLowerCase() + ".wav");
        }
        this.segundosRestantes = cfg.getMinutosTrabajo() * 60;
        this.sonidoSeleccionadoTemp = sonidoInicial;

        construirVentana();
        inicializarTimer();

       
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                detenerTodosLosSonidos();
                guardarEnDiscoSilencioso();
            }
        });
    }

    private void construirVentana() {
        setTitle("Focus Player | " + usuario.getNombre());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 740);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO_PRINCIPAL);
        getContentPane().setLayout(new BorderLayout());

        // --- Menú lateral izquierdo (según prototipos de las págs. 2 y 3) ---
        JPanel menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(PANEL_LATERAL);
        menu.setBorder(BorderFactory.createEmptyBorder(26, 16, 22, 16));
        menu.setPreferredSize(new Dimension(215, 0));

        JLabel logo = new JLabel("Focus Player");
        logo.setAlignmentX(LEFT_ALIGNMENT);
        logo.setForeground(TEXTO_PRINCIPAL);
        logo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 21));
        menu.add(logo);
        menu.add(Box.createVerticalStrut(32));

        agregarModulo("Inicio", "INICIO", crearPanelInicio(), menu);
        agregarModulo("Configuración", "CONFIGURACION", crearPanelConfiguracion(), menu);
        agregarModulo("Estadísticas", "ESTADISTICAS", crearPanelEstadisticas(), menu);
        agregarModulo("Sonidos", "SONIDOS", crearPanelSonidos(), menu);

        menu.add(Box.createVerticalGlue());

        
        JLabel lblPerfilTitulo = new JLabel("Perfil de usuario");
        lblPerfilTitulo.setAlignmentX(LEFT_ALIGNMENT);
        lblPerfilTitulo.setForeground(TEXTO_SECUNDARIO);
        lblPerfilTitulo.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 11));

        JLabel lblNombreUsuario = new JLabel(usuario.getNombre());
        lblNombreUsuario.setAlignmentX(LEFT_ALIGNMENT);
        lblNombreUsuario.setForeground(COLOR_PRIMARIO);
        lblNombreUsuario.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 14));

        menu.add(lblPerfilTitulo);
        menu.add(Box.createVerticalStrut(2));
        menu.add(lblNombreUsuario);
        menu.add(Box.createVerticalStrut(14));

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.setAlignmentX(LEFT_ALIGNMENT);
        btnSalir.setMaximumSize(new Dimension(183, 38));
        btnSalir.setPreferredSize(new Dimension(183, 38));
        btnSalir.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 12));
        btnSalir.setForeground(COLOR_DESCANSO);
        btnSalir.setBackground(SUPERFICIE_TARJETA);
        btnSalir.setOpaque(true);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createLineBorder(COLOR_DESCANSO, 1));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> cerrarSesion());
        menu.add(btnSalir);

        contenido.setBackground(FONDO_PRINCIPAL);
        getContentPane().add(menu, BorderLayout.WEST);
        getContentPane().add(contenido, BorderLayout.CENTER);

        mostrarModulo("INICIO");
    }

    private void agregarModulo(String texto, String clave, JPanel panel, JPanel menu) {
        panel.setBackground(FONDO_PRINCIPAL);
        contenido.add(panel, clave);

        JButton boton = new JButton(texto);
        boton.setAlignmentX(LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(183, 42));
        boton.setPreferredSize(new Dimension(183, 42));
        boton.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 14));
        boton.setForeground(TEXTO_SECUNDARIO);
        boton.setBackground(PANEL_LATERAL);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        boton.addActionListener(e -> mostrarModulo(clave));
        botonesMenu.put(clave, boton);

        menu.add(boton);
        menu.add(Box.createVerticalStrut(8));
    }

    private void mostrarModulo(String clave) {
        // Si sale de Sonidos sin confirmar, detiene la vista previa
        if (!"SONIDOS".equals(clave) && sonidoEnVistaPrevia != null) {
            sonidoEnVistaPrevia.detener();
            sonidoEnVistaPrevia = null;
        }

        if ("CONFIGURACION".equals(clave)) {
            cargarValoresConfiguracion();
        } else if ("ESTADISTICAS".equals(clave)) {
            actualizarEstadisticas();
        } else if ("SONIDOS".equals(clave)) {
            cargarValoresSonidos();
        }

        
        for (Map.Entry<String, JButton> entry : botonesMenu.entrySet()) {
            boolean activo = entry.getKey().equals(clave);
            JButton btn = entry.getValue();
            btn.setBackground(activo ? SUPERFICIE_TARJETA : PANEL_LATERAL);
            btn.setForeground(activo ? COLOR_PRIMARIO : TEXTO_SECUNDARIO);
            btn.setFont(new Font(FUENTE_TEXTOS, activo ? Font.BOLD : Font.PLAIN, 14));
        }

        ((CardLayout) contenido.getLayout()).show(contenido, clave);
    }

    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        JLabel titulo = new JLabel("Inicio");
        titulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 28));
        titulo.setForeground(TEXTO_PRINCIPAL);
        panel.add(titulo, BorderLayout.NORTH);

        
        JPanel tarjetaCentral = new JPanel();
        tarjetaCentral.setBackground(SUPERFICIE_TARJETA);
        tarjetaCentral.setLayout(new BoxLayout(tarjetaCentral, BoxLayout.Y_AXIS));
        tarjetaCentral.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_TARJETA, 1),
                BorderFactory.createEmptyBorder(36, 40, 36, 40)));

        lblEstadoCiclo = new JLabel("CICLO DE TRABAJO");
        lblEstadoCiclo.setAlignmentX(CENTER_ALIGNMENT);
        lblEstadoCiclo.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 14));
        lblEstadoCiclo.setForeground(COLOR_PRIMARIO);

        lblTiempo = new JLabel(formatearTiempo(segundosRestantes));
        lblTiempo.setAlignmentX(CENTER_ALIGNMENT);
        lblTiempo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 84));
        lblTiempo.setForeground(COLOR_PRIMARIO);

        Sonido sActual = usuario.getConfiguracion().getSonidoActual();
        String nombreSonido = (sActual != null) ? sActual.getNombre() : "Sin sonido";
        lblSonidoActivo = new JLabel("Sonido ambiental: " + nombreSonido
                + " (" + usuario.getConfiguracion().getVolumen() + "%)");
        lblSonidoActivo.setAlignmentX(CENTER_ALIGNMENT);
        lblSonidoActivo.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 14));
        lblSonidoActivo.setForeground(TEXTO_SECUNDARIO);

        
        JPanel filaBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        filaBotones.setOpaque(false);

        btnIniciar = crearBotonAccion("Iniciar", COLOR_PRIMARIO, FONDO_PRINCIPAL, 130, 44);
        btnPausar = crearBotonAccion("Pausar", COLOR_DESCANSO, FONDO_PRINCIPAL, 130, 44);
        btnReiniciar = crearBotonAccion("Reiniciar", FONDO_PRINCIPAL, TEXTO_PRINCIPAL, 130, 44);
        btnReiniciar.setBorder(BorderFactory.createLineBorder(BORDE_TARJETA, 1));

        btnPausar.setEnabled(false);

        btnIniciar.addActionListener(e -> iniciarTemporizador());
        btnPausar.addActionListener(e -> pausarTemporizador());
        btnReiniciar.addActionListener(e -> reiniciarTemporizador());

        filaBotones.add(btnIniciar);
        filaBotones.add(btnPausar);
        filaBotones.add(btnReiniciar);

        lblCiclosSesion = new JLabel("Ciclos completados en esta sesión: 0");
        lblCiclosSesion.setAlignmentX(CENTER_ALIGNMENT);
        lblCiclosSesion.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 13));
        lblCiclosSesion.setForeground(TEXTO_PRINCIPAL);

        tarjetaCentral.add(Box.createVerticalGlue());
        tarjetaCentral.add(lblEstadoCiclo);
        tarjetaCentral.add(Box.createVerticalStrut(10));
        tarjetaCentral.add(lblTiempo);
        tarjetaCentral.add(Box.createVerticalStrut(12));
        tarjetaCentral.add(lblSonidoActivo);
        tarjetaCentral.add(Box.createVerticalStrut(32));
        tarjetaCentral.add(filaBotones);
        tarjetaCentral.add(Box.createVerticalStrut(28));
        tarjetaCentral.add(lblCiclosSesion);
        tarjetaCentral.add(Box.createVerticalGlue());

        panel.add(tarjetaCentral, BorderLayout.CENTER);
        return panel;
    }

    private void inicializarTimer() {
        timerSwing = new Timer(1000, e -> actualizarTemporizador());
    }

    private void iniciarTemporizador() {
        if (corriendo) return;

        if ("PAUSA".equals(estadoTemporizador)) {
            estadoTemporizador = estadoPrevioPausa;
        }
        corriendo = true;
        timerSwing.start();

        btnIniciar.setEnabled(false);
        btnPausar.setEnabled(true);
        actualizarEstilosEstadoTemporizador();

        
        Sonido sonido = usuario.getConfiguracion().getSonidoActual();
        if (sonido == null) {
            sonido = new Sonido("Lluvia", "/FP/sonidos/lluvia.wav");
            usuario.getConfiguracion().setSonido(sonido);
        }
        sonido.setRutaArchivo("/FP/sonidos/" + sonido.getNombre().toLowerCase() + ".wav");
        sonido.setVolumen(usuario.getConfiguracion().getVolumen());
        sonido.reproducir();
        
    }

    private void pausarTemporizador() {
        if (!corriendo) return;

        corriendo = false;
        timerSwing.stop();
        estadoPrevioPausa = estadoTemporizador;
        estadoTemporizador = "PAUSA";

        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
        actualizarEstilosEstadoTemporizador();

        Sonido sonido = usuario.getConfiguracion().getSonidoActual();
        if (sonido != null) {
            sonido.detener();
        }
    }

    private void reiniciarTemporizador() {
        corriendo = false;
        timerSwing.stop();
        estadoTemporizador = "TRABAJO";
        estadoPrevioPausa = "TRABAJO";
        segundosRestantes = usuario.getConfiguracion().getMinutosTrabajo() * 60;

        lblTiempo.setText(formatearTiempo(segundosRestantes));
        btnIniciar.setEnabled(true);
        btnPausar.setEnabled(false);
        actualizarEstilosEstadoTemporizador();

        Sonido sonido = usuario.getConfiguracion().getSonidoActual();
        if (sonido != null) {
            sonido.detener();
        }
    }

    private void actualizarTemporizador() {
        if (segundosRestantes > 0) {
            segundosRestantes--;
            lblTiempo.setText(formatearTiempo(segundosRestantes));
        } else {
            cambiarEstadoCiclo();
        }
    }

    private void cambiarEstadoCiclo() {
        Configuracion cfg = usuario.getConfiguracion();

        if ("TRABAJO".equals(estadoTemporizador)) {
            // Registrar ciclo completado en el historial del usuario
            ciclosCompletadosSesion++;
            lblCiclosSesion.setText("Ciclos completados en esta sesión: " + ciclosCompletadosSesion);

            Sesion nuevaSesion = new Sesion(
                    LocalDate.now().toString(),
                    cfg.getMinutosTrabajo(),
                    1);
            usuario.agregarSesion(nuevaSesion);
            guardarEnDiscoSilencioso();

            // Cambiar automáticamente a Descanso
            estadoTemporizador = "DESCANSO";
            segundosRestantes = cfg.getMinutosDescanso() * 60;
            actualizarEstilosEstadoTemporizador();
            lblTiempo.setText(formatearTiempo(segundosRestantes));
        } else {
            // Terminó el descanso: volver a Trabajo
            estadoTemporizador = "TRABAJO";
            segundosRestantes = cfg.getMinutosTrabajo() * 60;
            actualizarEstilosEstadoTemporizador();
            lblTiempo.setText(formatearTiempo(segundosRestantes));
        }
    }

    private void actualizarEstilosEstadoTemporizador() {
        if ("TRABAJO".equals(estadoTemporizador)) {
            lblEstadoCiclo.setText("CICLO DE TRABAJO");
            lblEstadoCiclo.setForeground(COLOR_PRIMARIO);
            lblTiempo.setForeground(COLOR_PRIMARIO);
        } else if ("DESCANSO".equals(estadoTemporizador)) {
            lblEstadoCiclo.setText("TIEMPO DE DESCANSO");
            lblEstadoCiclo.setForeground(COLOR_DESCANSO);
            lblTiempo.setForeground(COLOR_DESCANSO);
        } else {
            lblEstadoCiclo.setText("TEMPORIZADOR EN PAUSA");
            lblEstadoCiclo.setForeground(TEXTO_SECUNDARIO);
            lblTiempo.setForeground(TEXTO_SECUNDARIO);
        }
    }

    private String formatearTiempo(int totalSegundos) {
        int min = totalSegundos / 60;
        int seg = totalSegundos % 60;
        return String.format("%02d:%02d", min, seg);
    }

    
    private JPanel crearPanelConfiguracion() {
        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        JLabel titulo = new JLabel("Configuración");
        titulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 28));
        titulo.setForeground(TEXTO_PRINCIPAL);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 24, 0));
        cuerpo.setOpaque(false);

        // Columna izquierda: 3 tarjetas con sliders
        JPanel colIzquierda = new JPanel(new GridLayout(3, 1, 0, 18));
        colIzquierda.setOpaque(false);

        sliderTrabajo = crearSliderEstilizado(1, 60, usuario.getConfiguracion().getMinutosTrabajo());
        lblValorTrabajo = new JLabel(sliderTrabajo.getValue() + " min");
        colIzquierda.add(crearTarjetaSlider("Tiempo de trabajo", lblValorTrabajo, sliderTrabajo, " min"));

        sliderDescanso = crearSliderEstilizado(1, 30, usuario.getConfiguracion().getMinutosDescanso());
        lblValorDescanso = new JLabel(sliderDescanso.getValue() + " min");
        colIzquierda.add(crearTarjetaSlider("Tiempo de descanso", lblValorDescanso, sliderDescanso, " min"));

        sliderVolumenConfig = crearSliderEstilizado(0, 100, usuario.getConfiguracion().getVolumen());
        lblValorVolumenConfig = new JLabel(sliderVolumenConfig.getValue() + " %");
        colIzquierda.add(crearTarjetaSlider("Volumen del sonido", lblValorVolumenConfig, sliderVolumenConfig, " %"));

        // Actualizar barra de vista previa en vivo al mover sliders
        sliderTrabajo.addChangeListener(e -> panelVistaPreviaCiclo.repaint());
        sliderDescanso.addChangeListener(e -> panelVistaPreviaCiclo.repaint());

        // Columna derecha: Vista previa del ciclo
        JPanel tarjetaVistaPrevia = new JPanel();
        tarjetaVistaPrevia.setBackground(SUPERFICIE_TARJETA);
        tarjetaVistaPrevia.setLayout(new BoxLayout(tarjetaVistaPrevia, BoxLayout.Y_AXIS));
        tarjetaVistaPrevia.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_TARJETA, 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)));

        JLabel lblVistaTitulo = new JLabel("Vista previa del ciclo");
        lblVistaTitulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 16));
        lblVistaTitulo.setForeground(TEXTO_PRINCIPAL);
        lblVistaTitulo.setAlignmentX(LEFT_ALIGNMENT);

        panelVistaPreviaCiclo = new BarraVistaPreviaPanel();
        panelVistaPreviaCiclo.setAlignmentX(LEFT_ALIGNMENT);
        panelVistaPreviaCiclo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        panelVistaPreviaCiclo.setPreferredSize(new Dimension(320, 44));

        JLabel lblNota = new JLabel("Los cambios se guardan en el perfil del usuario.");
        lblNota.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 13));
        lblNota.setForeground(TEXTO_SECUNDARIO);
        lblNota.setAlignmentX(LEFT_ALIGNMENT);

        tarjetaVistaPrevia.add(lblVistaTitulo);
        tarjetaVistaPrevia.add(Box.createVerticalStrut(18));
        tarjetaVistaPrevia.add(panelVistaPreviaCiclo);
        tarjetaVistaPrevia.add(Box.createVerticalStrut(16));
        tarjetaVistaPrevia.add(lblNota);
        tarjetaVistaPrevia.add(Box.createVerticalGlue());

        cuerpo.add(colIzquierda);
        cuerpo.add(tarjetaVistaPrevia);
        panel.add(cuerpo, BorderLayout.CENTER);

        // Barra inferior derecha: Volver y Guardar
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pie.setOpaque(false);

        JButton btnVolver = crearBotonAccion("Volver", SUPERFICIE_TARJETA, TEXTO_PRINCIPAL, 110, 40);
        btnVolver.addActionListener(e -> mostrarModulo("INICIO"));

        JButton btnGuardar = crearBotonAccion("Guardar", COLOR_PRIMARIO, FONDO_PRINCIPAL, 120, 40);
        btnGuardar.addActionListener(e -> guardarConfiguracion());

        pie.add(btnVolver);
        pie.add(btnGuardar);
        panel.add(pie, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarValoresConfiguracion() {
        Configuracion cfg = usuario.getConfiguracion();
        sliderTrabajo.setValue(cfg.getMinutosTrabajo());
        sliderDescanso.setValue(cfg.getMinutosDescanso());
        sliderVolumenConfig.setValue(cfg.getVolumen());
        panelVistaPreviaCiclo.repaint();
    }

    private void guardarConfiguracion() {
        Configuracion cfg = usuario.getConfiguracion();
        cfg.setMinutosTrabajo(sliderTrabajo.getValue());
        cfg.setMinutosDescanso(sliderDescanso.getValue());
        cfg.setVolumen(sliderVolumenConfig.getValue());

        
        if (!corriendo && "TRABAJO".equals(estadoTemporizador)) {
            segundosRestantes = cfg.getMinutosTrabajo() * 60;
            lblTiempo.setText(formatearTiempo(segundosRestantes));
        }

        actualizarEtiquetaSonidoInicio();

        try {
            persistencia.guardar(almacen);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar en disco: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        JLabel titulo = new JLabel("Estadísticas");
        titulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 28));
        titulo.setForeground(TEXTO_PRINCIPAL);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 20));
        centro.setOpaque(false);

        JPanel filaMetricas = new JPanel(new GridLayout(1, 3, 18, 0));
        filaMetricas.setOpaque(false);
        filaMetricas.setPreferredSize(new Dimension(0, 125));

        lblTotalCiclos = new JLabel("00");
        lblTotalMinutos = new JLabel("000");
        lblTotalSesiones = new JLabel("00");

        filaMetricas.add(crearTarjetaMetrica("Ciclos completados", lblTotalCiclos, COLOR_PRIMARIO));
        filaMetricas.add(crearTarjetaMetrica("Minutos de enfoque", lblTotalMinutos, COLOR_PRIMARIO));
        filaMetricas.add(crearTarjetaMetrica("Sesiones registradas", lblTotalSesiones, COLOR_DESCANSO));

        JPanel tarjetaGrafico = new JPanel(new BorderLayout(0, 10));
        tarjetaGrafico.setBackground(SUPERFICIE_TARJETA);
        tarjetaGrafico.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_TARJETA, 1),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        JLabel lblGraficoTitulo = new JLabel("Minutos por día");
        lblGraficoTitulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 16));
        lblGraficoTitulo.setForeground(TEXTO_PRINCIPAL);

        graficoSemanal = new GraficoSemanalPanel();

        JLabel lblSemana = new JLabel("Semana", SwingConstants.CENTER);
        lblSemana.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 12));
        lblSemana.setForeground(TEXTO_SECUNDARIO);

        tarjetaGrafico.add(lblGraficoTitulo, BorderLayout.NORTH);
        tarjetaGrafico.add(graficoSemanal, BorderLayout.CENTER);
        tarjetaGrafico.add(lblSemana, BorderLayout.SOUTH);

        centro.add(filaMetricas, BorderLayout.NORTH);
        centro.add(tarjetaGrafico, BorderLayout.CENTER);
        panel.add(centro, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pie.setOpaque(false);
        JButton btnVolver = crearBotonAccion("Volver", SUPERFICIE_TARJETA, TEXTO_PRINCIPAL, 110, 40);
        btnVolver.addActionListener(e -> mostrarModulo("INICIO"));
        pie.add(btnVolver);
        panel.add(pie, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearTarjetaMetrica(String titulo, JLabel lblValor, Color colorNumero) {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(SUPERFICIE_TARJETA);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_TARJETA, 1),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 14));
        lblTitulo.setForeground(TEXTO_SECUNDARIO);
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);

        lblValor.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 34));
        lblValor.setForeground(colorNumero);
        lblValor.setAlignmentX(LEFT_ALIGNMENT);

        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblValor);
        return tarjeta;
    }

    private void actualizarEstadisticas() {
        int totalCiclos = 0;
        int totalMinutos = 0;
        int totalSesiones = usuario.getHistorial().size();

        int[] minutosSemana = new int[7]; 
        LocalDate hoy = LocalDate.now();
        LocalDate inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate finSemana = inicioSemana.plusDays(6);

        for (Sesion s : usuario.getHistorial()) {
            totalCiclos += s.getCiclos();
            totalMinutos += s.getMinutosTrabajados();

            try {
                LocalDate fechaSesion = LocalDate.parse(s.getFecha());
                if (!fechaSesion.isBefore(inicioSemana) && !fechaSesion.isAfter(finSemana)) {
                    int idxDia = fechaSesion.getDayOfWeek().getValue() - 1;
                    minutosSemana[idxDia] += s.getMinutosTrabajados();
                }
            } catch (Exception ignored) {
            }
        }

        lblTotalCiclos.setText(String.format("%02d", totalCiclos));
        lblTotalMinutos.setText(String.format("%03d", totalMinutos));
        lblTotalSesiones.setText(String.format("%02d", totalSesiones));
        graficoSemanal.setDatos(minutosSemana);
    }

    private JPanel crearPanelSonidos() {
        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBorder(BorderFactory.createEmptyBorder(32, 36, 32, 36));

        JLabel titulo = new JLabel("Sonidos");
        titulo.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 28));
        titulo.setForeground(TEXTO_PRINCIPAL);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 20));
        centro.setOpaque(false);

        JPanel filaTarjetas = new JPanel(new GridLayout(1, 3, 20, 0));
        filaTarjetas.setOpaque(false);

        ButtonGroup grupoRadios = new ButtonGroup();
        tarjetasSonido.clear();
        radiosSonido.clear();

        String[] nombresBase = {"Lluvia", "Bosque", "Olas"};
        for (String nombre : nombresBase) {
            Sonido sonido = almacen.buscarSonidoPorNombre(nombre);
            if (sonido == null) {
                sonido = new Sonido(nombre, "/FP/sonidos/" + nombre.toLowerCase() + ".wav");
            }
            JPanel tarjeta = crearTarjetaSeleccionSonido(sonido, grupoRadios);
            tarjetasSonido.put(nombre, tarjeta);
            filaTarjetas.add(tarjeta);
        }

        // Tarjeta inferior de Volumen
        sliderVolumenSonidos = crearSliderEstilizado(0, 100, usuario.getConfiguracion().getVolumen());
        lblValorVolumenSonidos = new JLabel(sliderVolumenSonidos.getValue() + " %");
        JPanel tarjetaVolumen = crearTarjetaSlider("Volumen", lblValorVolumenSonidos, sliderVolumenSonidos, " %");
        tarjetaVolumen.setPreferredSize(new Dimension(0, 105));

        sliderVolumenSonidos.addChangeListener(e -> {
            if (sonidoEnVistaPrevia != null) {
                sonidoEnVistaPrevia.setVolumen(sliderVolumenSonidos.getValue());
            }
        });

        centro.add(filaTarjetas, BorderLayout.CENTER);
        centro.add(tarjetaVolumen, BorderLayout.SOUTH);
        panel.add(centro, BorderLayout.CENTER);

        // Botones inferiores: Volver y Confirmar
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        pie.setOpaque(false);

        JButton btnVolver = crearBotonAccion("Volver", SUPERFICIE_TARJETA, TEXTO_PRINCIPAL, 110, 40);
        btnVolver.addActionListener(e -> mostrarModulo("INICIO"));

        JButton btnConfirmar = crearBotonAccion("Confirmar", COLOR_PRIMARIO, FONDO_PRINCIPAL, 125, 40);
        btnConfirmar.addActionListener(e -> confirmarSonido());

        pie.add(btnVolver);
        pie.add(btnConfirmar);
        panel.add(pie, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearTarjetaSeleccionSonido(Sonido sonido, ButtonGroup grupo) {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(SUPERFICIE_TARJETA);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_TARJETA, 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        // Recuadro visual superior
        JPanel recuadroImagen = new JPanel(new BorderLayout());
        recuadroImagen.setBackground(FONDO_PRINCIPAL);
        recuadroImagen.setBorder(BorderFactory.createLineBorder(BORDE_TARJETA, 1));
        recuadroImagen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        recuadroImagen.setPreferredSize(new Dimension(200, 150));
        recuadroImagen.setAlignmentX(LEFT_ALIGNMENT);

        String simbolo = "Lluvia".equalsIgnoreCase(sonido.getNombre()) ? "🌧"
                       : "Bosque".equalsIgnoreCase(sonido.getNombre()) ? "🌲" : "🌊";
        JLabel lblIcono = new JLabel(simbolo, SwingConstants.CENTER);
        lblIcono.setFont(new Font("SansSerif", Font.PLAIN, 44));
        lblIcono.setForeground(COLOR_PRIMARIO);
        recuadroImagen.add(lblIcono, BorderLayout.CENTER);

        JLabel lblNombre = new JLabel(sonido.getNombre());
        lblNombre.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 18));
        lblNombre.setForeground(TEXTO_PRINCIPAL);
        lblNombre.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnPreview = new JButton("Escuchar vista previa");
        btnPreview.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 13));
        btnPreview.setForeground(TEXTO_PRINCIPAL);
        btnPreview.setContentAreaFilled(false);
        btnPreview.setBorderPainted(false);
        btnPreview.setFocusPainted(false);
        btnPreview.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPreview.setAlignmentX(CENTER_ALIGNMENT);
        btnPreview.addActionListener(e -> {
            if (sonidoEnVistaPrevia != null) {
                sonidoEnVistaPrevia.detener();
            }
            sonidoEnVistaPrevia = sonido;
            sonidoEnVistaPrevia.setVolumen(sliderVolumenSonidos.getValue());
            sonidoEnVistaPrevia.reproducirVistaPrevia();
        });

        JRadioButton radio = new JRadioButton("Usar este sonido");
        radio.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 13));
        radio.setForeground(TEXTO_PRINCIPAL);
        radio.setOpaque(false);
        radio.setFocusPainted(false);
        radio.setAlignmentX(LEFT_ALIGNMENT);
        radio.addActionListener(e -> {
            sonidoSeleccionadoTemp = sonido;
            actualizarBordesTarjetasSonido(sonido.getNombre());
        });

        grupo.add(radio);
        radiosSonido.put(sonido.getNombre(), radio);

        tarjeta.add(recuadroImagen);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(lblNombre);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(btnPreview);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(radio);

        return tarjeta;
    }

    private void cargarValoresSonidos() {
        Configuracion cfg = usuario.getConfiguracion();
        sliderVolumenSonidos.setValue(cfg.getVolumen());

        Sonido actual = cfg.getSonidoActual();
        String nombreActual = (actual != null) ? actual.getNombre() : "Lluvia";
        sonidoSeleccionadoTemp = actual;

        JRadioButton radio = radiosSonido.get(nombreActual);
        if (radio != null) {
            radio.setSelected(true);
        }
        actualizarBordesTarjetasSonido(nombreActual);
    }

    private void actualizarBordesTarjetasSonido(String nombreSeleccionado) {
        for (Map.Entry<String, JPanel> entry : tarjetasSonido.entrySet()) {
            boolean esElegido = entry.getKey().equalsIgnoreCase(nombreSeleccionado);
            entry.getValue().setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(esElegido ? COLOR_PRIMARIO : BORDE_TARJETA, esElegido ? 2 : 1),
                    BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        }
    }

    private void confirmarSonido() {
        if (sonidoEnVistaPrevia != null) {
            sonidoEnVistaPrevia.detener();
            sonidoEnVistaPrevia = null;
        }

        Sonido anterior = usuario.getConfiguracion().getSonidoActual();
        if (anterior != null && anterior != sonidoSeleccionadoTemp) {
            anterior.detener();
        }

        Configuracion cfg = usuario.getConfiguracion();
        cfg.setSonido(sonidoSeleccionadoTemp);
        cfg.setVolumen(sliderVolumenSonidos.getValue());

        if (corriendo && sonidoSeleccionadoTemp != null) {
            sonidoSeleccionadoTemp.reproducir();
        }

        actualizarEtiquetaSonidoInicio();

        try {
            persistencia.guardar(almacen);
            JOptionPane.showMessageDialog(this,
                    "Sonido '" + (sonidoSeleccionadoTemp != null ? sonidoSeleccionadoTemp.getNombre() : "")
                    + "' guardado en tu perfil.",
                    "Focus Player", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el sonido: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEtiquetaSonidoInicio() {
        Configuracion cfg = usuario.getConfiguracion();
        String nombre = (cfg.getSonidoActual() != null) ? cfg.getSonidoActual().getNombre() : "Sin sonido";
        lblSonidoActivo.setText("Sonido ambiental: " + nombre + " (" + cfg.getVolumen() + "%)");
    }

    private JPanel crearTarjetaSlider(String titulo, JLabel lblValor, JSlider slider, String sufijo) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(SUPERFICIE_TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_TARJETA, 1),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(FUENTE_TEXTOS, Font.PLAIN, 14));
        lblTitulo.setForeground(TEXTO_PRINCIPAL);

        lblValor.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 14));
        lblValor.setForeground(TEXTO_PRINCIPAL);

        slider.addChangeListener(e -> lblValor.setText(slider.getValue() + sufijo));

        cabecera.add(lblTitulo, BorderLayout.WEST);
        cabecera.add(lblValor, BorderLayout.EAST);

        tarjeta.add(cabecera, BorderLayout.NORTH);
        tarjeta.add(slider, BorderLayout.CENTER);
        return tarjeta;
    }

    private JSlider crearSliderEstilizado(int min, int max, int valor) {
        JSlider slider = new JSlider(min, max, valor);
        slider.setOpaque(false);
        slider.setForeground(COLOR_PRIMARIO);
        return slider;
    }

    private JButton crearBotonAccion(String texto, Color fondo, Color textoColor, int ancho, int alto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(FUENTE_TITULOS, Font.BOLD, 13));
        boton.setBackground(fondo);
        boton.setForeground(textoColor);
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(ancho, alto));
        return boton;
    }

    private void detenerTodosLosSonidos() {
        if (timerSwing != null && timerSwing.isRunning()) {
            timerSwing.stop();
        }
        if (sonidoEnVistaPrevia != null) {
            sonidoEnVistaPrevia.detener();
        }
        Sonido actual = usuario.getConfiguracion().getSonidoActual();
        if (actual != null) {
            actual.detener();
        }
    }

    private void guardarEnDiscoSilencioso() {
        try {
            persistencia.guardar(almacen);
        } catch (IOException ignored) {
        }
    }

    private void cerrarSesion() {
        detenerTodosLosSonidos();
        try {
            persistencia.guardar(almacen);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron guardar los cambios: " + ex.getMessage(),
                    "Focus Player", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LoginFrame login = new LoginFrame(almacen, persistencia);
        login.setAlIngresar(u -> {
            login.dispose();
            new MenuPrincipalFrame(u, almacen, persistencia).setVisible(true);
        });
        login.setVisible(true);
        dispose();
    }

    private class BarraVistaPreviaPanel extends JPanel {
        public BarraVistaPreviaPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int trabajo = sliderTrabajo != null ? sliderTrabajo.getValue() : 25;
            int descanso = sliderDescanso != null ? sliderDescanso.getValue() : 5;
            int total = Math.max(1, trabajo + descanso);

            int anchoTrabajo = (int) ((w * (double) trabajo) / total);
            int anchoDescanso = w - anchoTrabajo;

            g2.setColor(COLOR_PRIMARIO);
            g2.fillRoundRect(0, 0, anchoTrabajo + 6, h, 12, 12);

            g2.setColor(COLOR_DESCANSO);
            g2.fillRoundRect(anchoTrabajo, 0, anchoDescanso, h, 12, 12);
            g2.fillRect(anchoTrabajo, 0, Math.min(6, anchoDescanso), h);

            g2.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 12));
            g2.setColor(FONDO_PRINCIPAL);
            FontMetrics fm = g2.getFontMetrics();

            if (anchoTrabajo > 60) {
                String txtT = "Trabajo";
                g2.drawString(txtT, (anchoTrabajo - fm.stringWidth(txtT)) / 2, (h + fm.getAscent()) / 2 - 2);
            }
            if (anchoDescanso > 55) {
                String txtD = "Descanso";
                g2.drawString(txtD, anchoTrabajo + (anchoDescanso - fm.stringWidth(txtD)) / 2, (h + fm.getAscent()) / 2 - 2);
            }
            g2.dispose();
        }
    }

    private class GraficoSemanalPanel extends JPanel {
        private int[] datos = new int[7];
        private final String[] dias = {"L", "M", "X", "J", "V", "S", "D"};

        public GraficoSemanalPanel() {
            setOpaque(false);
        }

        public void setDatos(int[] datos) {
            this.datos = datos;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int margenInf = 28;
            int margenSup = 22;
            int areaAlto = Math.max(20, h - margenInf - margenSup);

            int maxVal = 60;
            for (int v : datos) {
                if (v > maxVal) maxVal = v;
            }

            g2.setColor(BORDE_TARJETA);
            g2.drawLine(20, h - margenInf, w - 20, h - margenInf);

            int anchoColumna = (w - 40) / 7;
            int anchoBarra = Math.min(44, anchoColumna - 18);

            g2.setFont(new Font(FUENTE_TEXTOS, Font.BOLD, 12));
            FontMetrics fm = g2.getFontMetrics();

            for (int i = 0; i < 7; i++) {
                int centroX = 20 + i * anchoColumna + anchoColumna / 2;
                int altoBarra = (int) ((datos[i] / (double) maxVal) * areaAlto);
                int x = centroX - anchoBarra / 2;
                int y = (h - margenInf) - altoBarra;

                
                if (altoBarra > 0) {
                    g2.setColor(COLOR_PRIMARIO);
                    g2.fillRoundRect(x, y, anchoBarra, altoBarra, 8, 8);

                    g2.setColor(TEXTO_PRINCIPAL);
                    String valTxt = String.valueOf(datos[i]);
                    g2.drawString(valTxt, centroX - fm.stringWidth(valTxt) / 2, y - 5);
                }

                
                g2.setColor(TEXTO_SECUNDARIO);
                g2.drawString(dias[i], centroX - fm.stringWidth(dias[i]) / 2, h - 8);
            }
            g2.dispose();
        }
    }
}