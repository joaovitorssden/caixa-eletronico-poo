import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI - Interface Grafica do Caixa Eletronico.
 *
 * Design: "Dark Banking Premium"
 * Tema escuro refinado com identidade visual do grupo Java Masters.
 *
 * Diferenciais visuais:
 *  - Relogio em tempo real no header
 *  - Botoes com borda lateral colorida e animacao de hover
 *  - Status bar dinamica com saldo atualizado apos cada operacao
 *  - Separacao clara por modulo (Cliente / Administrador / Ambos)
 *  - Extrato exibido em janela de recibo bancario ao clicar em Sair
 *  - Logo circular "J" dourado no cabecalho
 *
 * CONTRATO: Nenhum metodo de ICaixaEletronico foi alterado.
 * Esta classe apenas chama os metodos definidos na interface.
 *
 * @author Java Masters
 * @version 2.0
 */
public class GUI extends JFrame {

    // ===================================================
    // PALETA — Dark Banking Premium
    // ===================================================
    private static final Color C_BG_DEEP    = new Color(10,  14,  22);
    private static final Color C_BG_CARD    = new Color(16,  22,  36);
    private static final Color C_BG_SECTION = new Color(20,  28,  45);
    private static final Color C_GOLD       = new Color(196, 160, 80);
    private static final Color C_GOLD_LT    = new Color(232, 200, 120);
    private static final Color C_BLUE       = new Color(50,  120, 220);
    private static final Color C_GREEN      = new Color(40,  200, 120);
    private static final Color C_RED        = new Color(220, 60,  60);
    private static final Color C_TEXT       = new Color(220, 228, 245);
    private static final Color C_MUTED      = new Color(110, 130, 170);
    private static final Color C_DIVIDER    = new Color(30,  40,  62);
    private static final Color C_DISPLAY    = new Color(8,   12,  20);

    // ===================================================
    // FONTES
    // ===================================================
    private static final Font F_TITLE   = new Font("Georgia",    Font.BOLD,  18);
    private static final Font F_BRAND   = new Font("Georgia",    Font.PLAIN, 10);
    private static final Font F_BTN     = new Font("Segoe UI",   Font.BOLD,  12);
    private static final Font F_LABEL   = new Font("Segoe UI",   Font.PLAIN, 10);
    private static final Font F_DISPLAY = new Font("Courier New",Font.PLAIN, 12);
    private static final Font F_CLOCK   = new Font("Segoe UI",   Font.PLAIN, 11);
    private static final Font F_STATUS  = new Font("Segoe UI",   Font.PLAIN, 10);

    // ===================================================
    // ESTADO
    // ===================================================
    private ICaixaEletronico caixa;
    private CaixaEletronico  caixaImpl;
    private JTextArea        displayArea;
    private JLabel           lblRelogio;
    private JLabel           lblSaldo;
    private Timer            timerClock;

    // ===================================================
    // CONSTRUTOR
    // ===================================================

    /**
     * Instancia a GUI recebendo a classe que implementa ICaixaEletronico via reflection.
     *
     * @param classeImpl Class que implementa ICaixaEletronico (ex: CaixaEletronico.class)
     */
    public GUI(Class<?> classeImpl) {
        try {
            Constructor<?> ctor = classeImpl.getDeclaredConstructor();
            ctor.setAccessible(true);
            Object obj = ctor.newInstance();
            if (!(obj instanceof ICaixaEletronico))
                throw new IllegalArgumentException("Classe nao implementa ICaixaEletronico");
            caixa = (ICaixaEletronico) obj;
            if (obj instanceof CaixaEletronico) caixaImpl = (CaixaEletronico) obj;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao inicializar:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        montarUI();
        iniciarRelogio();
    }

    // ===================================================
    // MONTAGEM DA UI
    // ===================================================

    private void montarUI() {
        setTitle("Caixa Eletronico — Java Masters Bank");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(760, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(C_BG_DEEP);
        setLayout(new BorderLayout(0, 0));

        add(montarHeader(),  BorderLayout.NORTH);
        add(montarCentro(),  BorderLayout.CENTER);
        add(montarStatus(),  BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { aoSair(); }
        });
    }

    // ---------------------------------------------------
    // HEADER
    // ---------------------------------------------------
    private JComponent montarHeader() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,new Color(18,25,42),getWidth(),0,new Color(12,18,32)));
                g2.fillRect(0,0,getWidth(),getHeight());
                // linha dourada base
                g2.setColor(C_GOLD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14,20,14,20));

        // --- Esquerda: logo + nome ---
        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        esq.setOpaque(false);

        // Circulo "J" dourado
        JLabel logo = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,C_GOLD,36,36,new Color(130,90,20)));
                g2.fillOval(2,2,36,36);
                g2.setColor(C_GOLD_LT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(2,2,36,36);
                g2.setFont(new Font("Georgia",Font.BOLD,20));
                g2.setColor(C_BG_DEEP);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("J", 20-fm.stringWidth("J")/2, 27);
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(42,42); }
        };

        JPanel nomes = new JPanel(new GridLayout(2,1,0,2));
        nomes.setOpaque(false);
        JLabel n1 = new JLabel("JAVA MASTERS BANK");
        n1.setFont(F_TITLE); n1.setForeground(C_GOLD);
        JLabel n2 = new JLabel("Sistema de Autoatendimento  ·  Grupo Java Masters");
        n2.setFont(F_BRAND); n2.setForeground(C_MUTED);
        nomes.add(n1); nomes.add(n2);

        esq.add(logo); esq.add(nomes);

        // --- Direita: relogio + data ---
        JPanel dir = new JPanel(new GridLayout(2,1,0,2));
        dir.setOpaque(false);
        lblRelogio = new JLabel("--:--:--", SwingConstants.RIGHT);
        lblRelogio.setFont(F_CLOCK); lblRelogio.setForeground(C_TEXT);
        JLabel data = new JLabel(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            SwingConstants.RIGHT);
        data.setFont(F_CLOCK); data.setForeground(C_MUTED);
        dir.add(lblRelogio); dir.add(data);

        p.add(esq, BorderLayout.WEST);
        p.add(dir, BorderLayout.EAST);
        return p;
    }

    // ---------------------------------------------------
    // CENTRO: display + botoes
    // ---------------------------------------------------
    private JPanel montarCentro() {
        JPanel p = new JPanel(new BorderLayout(14,0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12,16,8,16));
        p.add(montarDisplay(), BorderLayout.CENTER);
        p.add(montarBotoes(),  BorderLayout.EAST);
        return p;
    }

    private JScrollPane montarDisplay() {
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(F_DISPLAY);
        displayArea.setBackground(C_DISPLAY);
        displayArea.setForeground(C_GREEN);
        displayArea.setBorder(new EmptyBorder(14,16,14,16));
        displayArea.setLineWrap(true);
        displayArea.setWrapStyleWord(true);
        displayArea.setText(
            "  ╔══════════════════════════════════╗\n" +
            "  ║     JAVA MASTERS BANK            ║\n" +
            "  ║     Bem-vindo!                   ║\n" +
            "  ╚══════════════════════════════════╝\n\n" +
            "  Selecione uma operacao no menu ao lado.\n");

        JScrollPane sc = new JScrollPane(displayArea);
        sc.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(C_GOLD, 1),
            BorderFactory.createLineBorder(C_BG_SECTION, 3)));
        return sc;
    }

    private JPanel montarBotoes() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(210, 0));

        // Modulo Cliente
        p.add(secaoLabel("MODULO DO CLIENTE"));
        p.add(Box.createVerticalStrut(5));
        JButton btnSacar   = botaoPremium("Efetuar Saque",          C_GREEN, "↑");
        p.add(btnSacar);
        p.add(Box.createVerticalStrut(10));

        // Modulo Administrador
        p.add(secaoLabel("MODULO DO ADMINISTRADOR"));
        p.add(Box.createVerticalStrut(5));
        JButton btnRel     = botaoPremium("Relatorio de Cedulas",    C_BLUE,  "≡");
        JButton btnTotal   = botaoPremium("Valor Total Disponivel",  C_BLUE,  "◈");
        JButton btnRepor   = botaoPremium("Reposicao de Cedulas",    C_GOLD,  "+");
        JButton btnCota    = botaoPremium("Cota Minima",             C_GOLD,  "⚑");
        p.add(btnRel);   p.add(Box.createVerticalStrut(6));
        p.add(btnTotal); p.add(Box.createVerticalStrut(6));
        p.add(btnRepor); p.add(Box.createVerticalStrut(6));
        p.add(btnCota);  p.add(Box.createVerticalStrut(10));

        // Modulo Ambos
        p.add(secaoLabel("MODULO DE AMBOS"));
        p.add(Box.createVerticalStrut(5));
        JButton btnSair    = botaoPremium("Sair",                    C_RED,   "×");
        p.add(btnSair);

        // ----- Acoes (chamam ICaixaEletronico) -----
        btnSacar.addActionListener(e -> {
            String v = inputDialog("Efetuar Saque", "Valor do saque (R$):");
            if (v == null) return;
            try {
                mostrar(caixa.sacar(Integer.parseInt(v.trim())));   // << contrato
                refreshSaldo();
            } catch (NumberFormatException ex) { mostrar("Valor invalido."); }
        });

        btnRel.addActionListener(e ->
            mostrar(caixa.pegaRelatorioCedulas()));                  // << contrato

        btnTotal.addActionListener(e -> {
            mostrar(caixa.pegaValorTotalDisponivel());               // << contrato
            refreshSaldo();
        });

        btnRepor.addActionListener(e -> {
            String[] ops = {"100","50","20","10","5","2"};
            String ced = (String) JOptionPane.showInputDialog(this,
                "Selecione a cedula:", "Reposicao de Cedulas",
                JOptionPane.PLAIN_MESSAGE, null, ops, ops[0]);
            if (ced == null) return;
            String qtd = inputDialog("Reposicao", "Quantidade de R$ "+ced+",00:");
            if (qtd == null) return;
            try {
                mostrar(caixa.reposicaoCedulas(             // << contrato
                    Integer.parseInt(ced),
                    Integer.parseInt(qtd.trim())));
                refreshSaldo();
            } catch (NumberFormatException ex) { mostrar("Valor invalido."); }
        });

        btnCota.addActionListener(e -> {
            String v = inputDialog("Cota Minima", "Valor da cota minima (R$):");
            if (v == null) return;
            try {
                mostrar(caixa.armazenaCotaMinima(Integer.parseInt(v.trim()))); // << contrato
            } catch (NumberFormatException ex) { mostrar("Valor invalido."); }
        });

        btnSair.addActionListener(e -> aoSair());

        return p;
    }

    // ---------------------------------------------------
    // STATUS BAR
    // ---------------------------------------------------
    private JComponent montarStatus() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(C_BG_SECTION);
                g.fillRect(0,0,getWidth(),getHeight());
                g.setColor(C_GOLD);
                g.drawLine(0,0,getWidth(),0);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(6,16,6,16));

        lblSaldo = new JLabel("Saldo: carregando...");
        lblSaldo.setFont(F_STATUS); lblSaldo.setForeground(C_GREEN);
        refreshSaldo();

        JLabel grupo = new JLabel("Java Masters  ·  POO 2026  ·  v2.0", SwingConstants.RIGHT);
        grupo.setFont(F_STATUS); grupo.setForeground(C_MUTED);

        p.add(lblSaldo, BorderLayout.WEST);
        p.add(grupo,    BorderLayout.EAST);
        return p;
    }

    // ===================================================
    // JANELA DE EXTRATO
    // ===================================================

    /**
     * Exibe o extrato completo da sessao em uma janela estilo recibo bancario.
     * Chamado ao clicar em Sair ou fechar a janela.
     */
    private void aoSair() {
        String txt = (caixaImpl != null) ? caixaImpl.gerarExtrato() : "Extrato indisponivel.";
        mostrar(txt);

        JDialog dlg = new JDialog(this, "Extrato — Java Masters Bank", true);
        dlg.setSize(490, 500);
        dlg.setLocationRelativeTo(this);
        dlg.getContentPane().setBackground(C_BG_DEEP);
        dlg.setLayout(new BorderLayout(0,0));

        // Header recibo
        JPanel hdr = new JPanel(new GridLayout(3,1,0,3)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,new Color(18,25,42),getWidth(),0,C_BG_DEEP));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(C_GOLD);
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
            }
        };
        hdr.setOpaque(false);
        hdr.setBorder(new EmptyBorder(14,0,12,0));

        JLabel t1 = new JLabel("JAVA MASTERS BANK", SwingConstants.CENTER);
        t1.setFont(new Font("Georgia",Font.BOLD,15)); t1.setForeground(C_GOLD);
        JLabel t2 = new JLabel("Extrato da Sessao", SwingConstants.CENTER);
        t2.setFont(new Font("Segoe UI",Font.PLAIN,11)); t2.setForeground(C_MUTED);
        JLabel t3 = new JLabel(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm:ss")),
            SwingConstants.CENTER);
        t3.setFont(new Font("Courier New",Font.PLAIN,10)); t3.setForeground(C_MUTED);
        hdr.add(t1); hdr.add(t2); hdr.add(t3);
        dlg.add(hdr, BorderLayout.NORTH);

        // Corpo
        JTextArea area = new JTextArea(txt);
        area.setEditable(false);
        area.setFont(new Font("Courier New",Font.PLAIN,11));
        area.setBackground(C_DISPLAY); area.setForeground(C_GREEN);
        area.setBorder(new EmptyBorder(12,16,12,16));
        JScrollPane sc = new JScrollPane(area);
        sc.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(8,14,4,14),
            BorderFactory.createLineBorder(C_GOLD,1)));
        dlg.add(sc, BorderLayout.CENTER);

        // Botoes
        JPanel rod = new JPanel(new FlowLayout(FlowLayout.CENTER,12,10));
        rod.setBackground(C_BG_SECTION);
        rod.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_DIVIDER));

        JButton bVoltar = botaoSimples("Voltar",          C_BLUE);
        JButton bSair   = botaoSimples("Encerrar Sessao", C_RED);
        bVoltar.addActionListener(ev -> dlg.dispose());
        bSair.addActionListener(ev -> { dlg.dispose(); System.exit(0); });
        rod.add(bVoltar); rod.add(bSair);
        dlg.add(rod, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    // ===================================================
    // COMPONENTES AUXILIARES
    // ===================================================

    /** Label de separacao de modulos. */
    private JPanel secaoLabel(String texto) {
        JPanel p = new JPanel(new BorderLayout(6,0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(210,20));
        JLabel l = new JLabel(texto);
        l.setFont(F_LABEL); l.setForeground(C_MUTED);
        JSeparator sep = new JSeparator();
        sep.setForeground(C_DIVIDER); sep.setBackground(C_DIVIDER);
        p.add(l, BorderLayout.WEST);
        p.add(sep, BorderLayout.CENTER);
        return p;
    }

    /**
     * Botao com estilo premium: fundo escuro, borda lateral colorida, hover suave.
     *
     * @param texto  rotulo do botao
     * @param cor    cor de acento (borda + icone)
     * @param icon   caractere de icone
     */
    private JButton botaoPremium(String texto, Color cor, String icon) {
        JButton btn = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                boolean hover = getModel().isRollover();
                boolean press = getModel().isPressed();

                // Fundo
                Color bg = press ? blend(C_BG_CARD, cor, 0.30f)
                                 : hover ? blend(C_BG_CARD, cor, 0.14f)
                                         : C_BG_CARD;
                g2.setPaint(new GradientPaint(0,0,bg.brighter(),0,h,bg));
                g2.fill(new RoundRectangle2D.Float(0,0,w,h,8,8));

                // Borda lateral
                g2.setColor(hover || press ? cor.brighter() : cor);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(2, 6, 2, h-6);

                // Borda exterior
                g2.setColor(hover ? cor : C_DIVIDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0,0,w,h,8,8));

                // Icone
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(hover ? cor.brighter() : cor);
                g2.drawString(icon, 11, h/2+5);

                // Texto
                g2.setFont(F_BTN);
                g2.setColor(hover ? C_TEXT : new Color(175,188,215));
                g2.drawString(getText(), 28, h/2+5);

                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(210,38); }
            @Override public Dimension getMaximumSize()   { return getPreferredSize(); }
            @Override public Dimension getMinimumSize()   { return getPreferredSize(); }
        };
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Botao simples para dialogos. */
    private JButton botaoSimples(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setFont(F_BTN);
        b.setBackground(cor); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setBorder(new EmptyBorder(8,18,8,18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ===================================================
    // UTILITARIOS
    // ===================================================

    /** Exibe texto no display principal. */
    private void mostrar(String txt) {
        displayArea.setText(txt);
        displayArea.setCaretPosition(0);
    }

    /** Atualiza o saldo na status bar. */
    private void refreshSaldo() {
        if (caixaImpl == null) return;
        int s = caixaImpl.calcularSaldoTotal();
        lblSaldo.setText("Saldo disponivel: R$ " + s + ",00");
        lblSaldo.setForeground(s > 0 ? C_GREEN : C_RED);
    }

    /** Inicia timer do relogio (atualiza a cada segundo). */
    private void iniciarRelogio() {
        timerClock = new Timer(1000, e ->
            lblRelogio.setText(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        timerClock.start();
        lblRelogio.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    /** InputDialog com tema escuro. */
    private String inputDialog(String titulo, String msg) {
        UIManager.put("OptionPane.background",        C_BG_CARD);
        UIManager.put("Panel.background",             C_BG_CARD);
        UIManager.put("OptionPane.messageForeground", C_TEXT);
        UIManager.put("TextField.background",         C_DISPLAY);
        UIManager.put("TextField.foreground",         C_TEXT);
        UIManager.put("TextField.caretForeground",    C_GREEN);
        return JOptionPane.showInputDialog(this, msg, titulo, JOptionPane.PLAIN_MESSAGE);
    }

    /** Mistura duas cores com fator t em [0,1]. */
    private Color blend(Color a, Color b, float t) {
        return new Color(
            clamp((int)(a.getRed()   + (b.getRed()   - a.getRed())   * t)),
            clamp((int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t)),
            clamp((int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)));
    }

    /** Limita valor ao intervalo [0, 255]. */
    private int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
