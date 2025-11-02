/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LENOVO
 */


import java.io.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.DefaultHighlighter;
import java.awt.Color;


public class FormPenghitungKata extends javax.swing.JFrame {

    /**
     * Creates new form FormPenghitungKata
     */
    private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 120);
private final Highlighter.HighlightPainter highlightPainter =
    new DefaultHighlighter.DefaultHighlightPainter(HIGHLIGHT_COLOR);

    
    public FormPenghitungKata() {
        initComponents();
        attachListeners();
        updateCounts();
        setLocationRelativeTo(null);
        
        lblCariCount.setVisible(false);
    }
    
       
        
            private void attachListeners() {
        // DocumentListener untuk update real-time
        textAreaInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateCounts(); clearHighlights(); }
            @Override public void removeUpdate(DocumentEvent e) { updateCounts(); clearHighlights(); }
            @Override public void changedUpdate(DocumentEvent e) { updateCounts(); clearHighlights(); }
        });

        // tombol Hitung (redundan karena DocumentListener sudah real-time)
       

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
            lblCariCount.setVisible(true);
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

    // 🔹 Jika textarea kosong → reset semua label termasuk karakter dgn spasi
    if (text.trim().isEmpty()) {
        lblWords.setText("Kata: 0");
        lblCharsNoSpace.setText("Karakter tanpa spasi: 0");
        lblSentences.setText("Kalimat: 0");
        lblParagraphs.setText("Paragraf: 0");
        lblChars.setText("Karakter dgn spasi: 0"); // ✅ reset juga
        return;
    }

    // 🔹 Kalau tidak kosong → lanjut hitung real-time
    int charsNoSpace = text.replaceAll("\\s+", "").length();
    int words = countWords(text);
    int sentences = countSentences(text);
    int paragraphs = countParagraphs(text);

    // Update label real-time
    lblWords.setText("Kata: " + words);
    lblCharsNoSpace.setText("Karakter tanpa spasi: " + charsNoSpace);
    lblSentences.setText("Kalimat: " + sentences);
    lblParagraphs.setText("Paragraf: " + paragraphs);

    // ⚠️ Jangan hitung "Karakter (dgn spasi)" di sini — biar cuma muncul saat klik tombol Hitung
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
        btnReset = new javax.swing.JButton();
        btnKeluar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        textAreaInput.setColumns(20);
        textAreaInput.setRows(5);
        jScrollPane1.setViewportView(textAreaInput);

        btnHitung.setText("Hitung");
        btnHitung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHitungActionPerformed(evt);
            }
        });

        btnSimpan.setText("Simpan");

        lblWords.setText("Kata");

        lblChars.setText("Karakter dgn spasi");

        lblCharsNoSpace.setText("karakter tanpa spasi");

        lblSentences.setText("Kalimat");

        lblParagraphs.setText("Paragraf");

        btnCari.setText("Cari");

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKeluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(430, 430, 430)
                        .addComponent(btnSimpan))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblWords)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblParagraphs)
                                .addGap(92, 92, 92)
                                .addComponent(btnHitung)
                                .addGap(285, 285, 285)
                                .addComponent(btnCari)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 151, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCharsNoSpace)
                                    .addComponent(lblChars)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(lblSentences)))
                                .addGap(180, 180, 180)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCariCount, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(10, 10, 10)
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(btnKeluar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(lblWords)
                        .addGap(134, 134, 134))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCharsNoSpace)
                    .addComponent(tfCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSentences)
                    .addComponent(lblCariCount, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblParagraphs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblChars)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(btnHitung))
                    .addComponent(btnCari))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnKeluar)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSimpan)
                            .addComponent(btnReset)))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHitungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHitungActionPerformed
        // TODO add your handling code here:
         updateCounts();
    clearHighlights();

    String text = textAreaInput.getText();
    if (text == null) text = "";

    // Jika kosong → reset ke 0
    if (text.trim().isEmpty()) {
        lblChars.setText("Karakter dgn spasi: 0");
        return;
        
        
    }

    // Cek apakah ada whitespace (spasi/tab/newline). 
    // Jika tidak ada whitespace sama sekali, kita tidak tampilkan hasilnya (pakai "-" misalnya)
    boolean hasWhitespace = text.matches(".*\\s+.*"); // true kalau ada spasi/tab/newline
    if (!hasWhitespace) {
        lblChars.setText("Karakter dgn spasi: -"); // atau "0" kalau kamu mau 0
    } else {
        lblChars.setText("Karakter dgn spasi: " + text.length());
    }
    
    textAreaInput.requestFocusInWindow();

    }//GEN-LAST:event_btnHitungActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        textAreaInput.setText("");
    tfCari.setText("");
    
    // Hapus semua highlight
    clearHighlights();
    
    // Reset semua label hasil perhitungan
    lblWords.setText("Kata: 0");
    lblChars.setText("Karakter dgn spasi: 0");
    lblCharsNoSpace.setText("Karakter tanpa spasi: 0");
    lblSentences.setText("Kalimat: 0");
    lblParagraphs.setText("Paragraf: 0");
    lblCariCount.setText("Ditemukan: 0");
    
    textAreaInput.requestFocusInWindow();
    
    lblCariCount.setVisible(false);
        
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        // TODO add your handling code here:
        int konfirmasi = JOptionPane.showConfirmDialog(
        this,
        "Apakah Anda yakin ingin keluar?",
        "Konfirmasi Keluar",
        JOptionPane.YES_NO_OPTION
    );
    if (konfirmasi == JOptionPane.YES_OPTION) {
        System.exit(0);
    } else {
        textAreaInput.requestFocusInWindow(); // kembali ke textarea kalau batal keluar
    }
    }//GEN-LAST:event_btnKeluarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> {
            new FormPenghitungKata().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHitung;
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnReset;
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
