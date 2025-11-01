/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LENOVO
 */

import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.DefaultHighlighter;

public class FormPenghitungKata extends javax.swing.JFrame {

    /**
     * Creates new form FormPenghitungKata
     */
    private final Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(javax.swing.UIManager.getColor("TextArea.selectionBackground"));
    
    public FormPenghitungKata() {
        initComponents();
        attachListeners();
        updateCounts();
    }
    
        private void initComponents() {
        setTitle("Aplikasi Penghitung Kata");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        textAreaInput = new javax.swing.JTextArea();
        textAreaInput.setLineWrap(true);
        textAreaInput.setWrapStyleWord(true);
        textAreaInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        jScrollPane1 = new javax.swing.JScrollPane(textAreaInput);

        btnHitung = new javax.swing.JButton("Hitung");
        btnSimpan = new javax.swing.JButton("Simpan");
        btnCari = new javax.swing.JButton("Cari & Highlight");
        tfCari = new javax.swing.JTextField();
        tfCari.setColumns(15);

        lblWords = new javax.swing.JLabel("Kata: 0");
        lblChars = new javax.swing.JLabel("Karakter (dgn spasi): 0");
        lblCharsNoSpace = new javax.swing.JLabel("Karakter (tanpa spasi): 0");
        lblSentences = new javax.swing.JLabel("Kalimat: 0");
        lblParagraphs = new javax.swing.JLabel("Paragraf: 0");
        lblCariCount = new javax.swing.JLabel("Ditemukan: 0");

        // Layout sederhana: top textarea, bawah panel kontrol
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        // Horizontal group
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblWords)
                    .addComponent(lblChars)
                    .addComponent(lblCharsNoSpace))
                .addGap(40)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lblSentences)
                    .addComponent(lblParagraphs))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(tfCari, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSimpan)
                )
                .addGap(8)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(btnCari)
                    .addComponent(btnHitung)
                    .addComponent(lblCariCount))
                .addContainerGap()
            )
        );

        // Vertical group
        layout.setVerticalGroup(
            layout.createSequentialGroup()
            .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE)
            .addGap(8)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lblWords)
                .addComponent(lblSentences)
                .addComponent(tfCari)
                .addComponent(btnCari)
            )
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lblChars)
                .addComponent(lblParagraphs)
                .addComponent(btnSimpan)
                .addComponent(btnHitung)
            )
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(lblCharsNoSpace)
                .addComponent(lblCariCount)
            )
            .addContainerGap(10, Short.MAX_VALUE)
        );
    }
        
            private void attachListeners() {
        // DocumentListener untuk update real-time
        textAreaInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateCounts(); clearHighlights(); }
            @Override public void removeUpdate(DocumentEvent e) { updateCounts(); clearHighlights(); }
            @Override public void changedUpdate(DocumentEvent e) { updateCounts(); clearHighlights(); }
        });

        // tombol Hitung (redundan karena DocumentListener sudah real-time)
        btnHitung.addActionListener(e -> {
            updateCounts();
            clearHighlights();
        });

        // tombol Cari & Highlight
        btnCari.addActionListener(e -> {
            clearHighlights();
            String pattern = tfCari.getText().trim();
            if (pattern.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan kata/teks yang ingin dicari.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int found = highlightAllOccurrences(pattern, true); // true = case-insensitive
            lblCariCount.setText("Ditemukan: " + found);
            JOptionPane.showMessageDialog(this, "Ditemukan: " + found + " kemunculan.", "Hasil Pencarian", JOptionPane.INFORMATION_MESSAGE);
        });

        // tombol Simpan
        btnSimpan.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan teks dan hasil perhitungan");
            chooser.setSelectedFile(new File("hasil_penghitungan.txt"));
            int userSel = chooser.showSaveDialog(this);
            if (userSel == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                try {
                    saveToFile(f);
                    JOptionPane.showMessageDialog(this, "Berhasil menyimpan ke:\n" + f.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
            
                // Method utama untuk update semua hitungan
    private void updateCounts() {
        String text = textAreaInput.getText();
        if (text == null) text = "";

        int charsWithSpace = text.length();
        int charsNoSpace = text.replaceAll("\\s+", "").length();

        int words = countWords(text);
        int sentences = countSentences(text);
        int paragraphs = countParagraphs(text);

        lblWords.setText("Kata: " + words);
        lblChars.setText("Karakter (dgn spasi): " + charsWithSpace);
        lblCharsNoSpace.setText("Karakter (tanpa spasi): " + charsNoSpace);
        lblSentences.setText("Kalimat: " + sentences);
        lblParagraphs.setText("Paragraf: " + paragraphs);
    }
    
        private int countWords(String text) {
        if (text.trim().isEmpty()) return 0;
        Pattern p = Pattern.compile("\\b[\\p{L}0-9']+\\b", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = p.matcher(text);
        int c = 0;
        while (m.find()) c++;
        return c;
    }
        
            private int countSentences(String text) {
        if (text.trim().isEmpty()) return 0;
        String[] parts = text.split("[.!?]+\\s*");
        int c = 0;
        for (String s : parts) {
            if (s.trim().length() > 0) c++;
        }
        return c;
    }
            
                private int countParagraphs(String text) {
        if (text.trim().isEmpty()) return 0;
        String[] parts = text.split("\\r?\\n\\s*\\r?\\n");
        int c = 0;
        for (String s : parts) {
            if (s.trim().length() > 0) c++;
        }
        return c;
    }
                
                    private int highlightAllOccurrences(String patternText, boolean caseInsensitive) {
        String content = textAreaInput.getText();
        if (content.isEmpty() || patternText.isEmpty()) return 0;

        Pattern pattern;
        if (caseInsensitive) {
            pattern = Pattern.compile(Pattern.quote(patternText), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
        } else {
            pattern = Pattern.compile(Pattern.quote(patternText), Pattern.UNICODE_CHARACTER_CLASS);
        }
        Matcher matcher = pattern.matcher(content);
        Highlighter high = textAreaInput.getHighlighter();
        int found = 0;
        while (matcher.find()) {
            try {
                high.addHighlight(matcher.start(), matcher.end(), highlightPainter);
                found++;
            } catch (BadLocationException ex) {
                // ignore
            }
        }
        return found;
    }
                    
                        private void clearHighlights() {
        textAreaInput.getHighlighter().removeAllHighlights();
        lblCariCount.setText("Ditemukan: 0");
    }
                        
                            private void saveToFile(File f) throws IOException {
        String text = textAreaInput.getText();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Aplikasi Penghitung Kata ===\n\n");
        sb.append("Teks:\n");
        sb.append(text).append("\n\n");
        sb.append("===== Hasil Penghitungan =====\n");
        sb.append(lblWords.getText()).append("\n");
        sb.append(lblChars.getText()).append("\n");
        sb.append(lblCharsNoSpace.getText()).append("\n");
        sb.append(lblSentences.getText()).append("\n");
        sb.append(lblParagraphs.getText()).append("\n");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(sb.toString());
            bw.flush();
        }
    }







            
            



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textAreaInput = new javax.swing.JTextArea();
        btnHitung = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();
        lblWords = new javax.swing.JLabel();
        lblChars = new javax.swing.JLabel();
        lblCharsNoSpace = new javax.swing.JLabel();
        lblSentences = new javax.swing.JLabel();
        lblParagraphs = new javax.swing.JLabel();
        btnCari = new javax.swing.JButton();
        lblCariCount = new javax.swing.JLabel();
        tfCari = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        textAreaInput.setColumns(20);
        textAreaInput.setRows(5);
        jScrollPane1.setViewportView(textAreaInput);

        btnHitung.setText("Hitung");

        btnSimpan.setText("Simpan");

        lblWords.setText("jLabel1");

        lblChars.setText("jLabel2");

        lblCharsNoSpace.setText("jLabel3");

        lblSentences.setText("jLabel4");

        lblParagraphs.setText("jLabel5");

        btnCari.setText("Cari");

        lblCariCount.setText("jLabel6");

        tfCari.setText("jTextField1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(lblCharsNoSpace)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblCariCount, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(btnCari))
                            .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnHitung)
                                .addGap(26, 26, 26)
                                .addComponent(btnSimpan)))
                        .addGap(43, 43, 43))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblWords)
                            .addComponent(lblChars)
                            .addComponent(lblParagraphs)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSentences))
                        .addContainerGap(132, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(lblWords)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblChars)
                    .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblCharsNoSpace)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCari)
                            .addComponent(lblCariCount, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)))
                .addComponent(lblSentences)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnHitung)
                    .addComponent(btnSimpan))
                .addGap(5, 5, 5)
                .addComponent(lblParagraphs)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FormPenghitungKata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormPenghitungKata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormPenghitungKata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormPenghitungKata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormPenghitungKata().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHitung;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCariCount;
    private javax.swing.JLabel lblChars;
    private javax.swing.JLabel lblCharsNoSpace;
    private javax.swing.JLabel lblParagraphs;
    private javax.swing.JLabel lblSentences;
    private javax.swing.JLabel lblWords;
    private javax.swing.JTextArea textAreaInput;
    private javax.swing.JTextField tfCari;
    // End of variables declaration//GEN-END:variables
}
