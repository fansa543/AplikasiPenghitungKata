# README — FormPenghitungKata

**Nama file:** `FormPenghitungKata.java`

**Deskripsi singkat**
Aplikasi sederhana (Swing) untuk menghitung kata, karakter (dengan dan tanpa spasi), jumlah kalimat, dan paragraf dari teks yang dimasukkan. Fitur tambahan: pencarian teks dengan highlight (penyorotan), menyimpan hasil hitungan ke file, reset, dan konfirmasi keluar.

> ⚠️ Alfadilah nur sahdan albiya 2310010465

---

## Fitur utama

* Hitung jumlah kata, kalimat, paragraf secara real-time.
* Tampilkan jumlah karakter tanpa spasi real-time.
* Tombol **Hitung** untuk menampilkan karakter termasuk spasi (hanya jika ada spasi dalam teks).
* Pencarian kata/teks dengan penyorotan semua kemunculan (case-insensitive).
* Simpan teks dan ringkasan hasil ke file `.txt`.
* Reset form, dan tombol keluar dengan konfirmasi.

---


### 1. Header komentar & author

```java
/* ... license header ... */
/**
 * @author LENOVO
 */
```

**Penjelasan:** Komentar template standar yang dihasilkan IDE. Tidak berpengaruh ke runtime—hanya dokumentasi.

---

### 2. Imports

```java
import java.io.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.DefaultHighlighter;
import java.awt.Color;
```

**Penjelasan:**

* `java.io.*` digunakan untuk operasi file (menyimpan hasil).
* `java.util.regex.*` dipakai untuk pencocokan teks (search & count words split jika perlu).
* `javax.swing.*` dan subpackage untuk UI (komponen, event, highlighter).
* `java.awt.Color` untuk warna highlight.

---

### 3. Deklarasi kelas dan konstanta warna highlight

```java
public class FormPenghitungKata extends javax.swing.JFrame {
    private static final Color HIGHLIGHT_COLOR = new Color(255, 255, 120);
    private final Highlighter.HighlightPainter highlightPainter =
        new DefaultHighlighter.DefaultHighlightPainter(HIGHLIGHT_COLOR);
    ...
}
```

**Penjelasan:**

* Kelas utama turunan `JFrame`.
* `HIGHLIGHT_COLOR` menentukan warna sorotan.
* `highlightPainter` adalah objek untuk menyorot teks di `JTextArea`.

---

### 4. Konstruktor: `public FormPenghitungKata()`

**Potongan:** di dalam konstruktor memanggil `initComponents(); attachListeners(); updateCounts(); setLocationRelativeTo(null); lblCariCount.setVisible(false);`

**Penjelasan:**

* `initComponents()` — method yang di-generate IDE (menginisialisasi GUI). Jangan ubah method ini kecuali paham dampaknya.
* `attachListeners()` — method kustom Anda yang menempelkan listener (DocumentListener & action listeners untuk tombol).
* `updateCounts()` — inisialisasi tampilan hasil (agar label tidak kosong saat start).
* `setLocationRelativeTo(null)` — centering window.
* `lblCariCount.setVisible(false)` — sembunyikan label hasil pencarian sampai digunakan.

---

### 5. Method `attachListeners()`

**Fungsi utama:** Menambahkan listener ke `textAreaInput` agar update hasil menjadi real-time, menambahkan listener untuk tombol `Cari` dan `Simpan`.

**Detil perilaku:**

* `DocumentListener` pada `textAreaInput` memanggil `updateCounts()` dan `clearHighlights()` pada setiap perubahan teks (insert/remove/change). Ini membuat label (kata/kalimat/dll) selalu up-to-date saat mengetik.
* `btnCari` action: ambil teks dari `tfCari`, jika kosong tampil peringatan; jika tidak, panggil `highlightAllOccurrences(pattern, true)` untuk mencari case-insensitive; tampilkan jumlah hasil di `lblCariCount` dan `JOptionPane`.
* `btnSimpan` action: membuka `JFileChooser`, default nama `hasil_penghitungan.txt`, lalu panggil `saveToFile(File)`; tampilkan dialog sukses atau error.

Catatan: `btnHitung` memiliki fungsional di `initComponents()` sebagai action listener yang menunjuk ke `btnHitungActionPerformed` (lihat bagian bawah).

---

### 6. `updateCounts()`

**Fungsi:** Menghitung dan memperbarui label: kata, karakter tanpa spasi, kalimat, paragraf — secara real-time.

**Logika ringkas:**

* Ambil `text` dari `textAreaInput`.
* Jika `text.trim().isEmpty()` → reset semua label menjadi 0 (termasuk `lblChars`).
* Jika ada isi: hitung `charsNoSpace` = hapus semua whitespace (`text.replaceAll("\\s+", "")`), `words` melalui `countWords(text)`, `sentences` via `countSentences(text)`, `paragraphs` via `countParagraphs(text)`.
* Update label: `lblWords`, `lblCharsNoSpace`, `lblSentences`, `lblParagraphs`.

**Catatan penting:** komentar pada kode menjelaskan bahwa **`lblChars` (karakter dgn spasi)** sengaja **tidak** dihitung di sini — hanya muncul ketika pengguna menekan tombol **Hitung**. Itu desain Anda.

---

### 7. `countWords(String text)`

```java
Pattern p = Pattern.compile("\\b[\\p{L}0-9']+\\b", Pattern.UNICODE_CHARACTER_CLASS);
Matcher m = p.matcher(text);
int c = 0; while (m.find()) c++;
```

**Penjelasan:**

* Menghitung kata menggunakan regex yang mendukung karakter Unicode (`\p{L}` untuk huruf) dan angka serta apostrof. Menghitung setiap match yang merupakan "kata".
* Mengembalikan 0 jika string kosong.

---

### 8. `countSentences(String text)`

**Pendekatan:** Splitting dengan regex `"[.!?]+\s*"` lalu hitung bagian yang non-empty.

**Catatan:** Ini metode sederhana; tidak menanggulangi kasus-kasus kompleks seperti "Mr." atau singkatan lain. Namun untuk kebanyakan teks umum, ini memadai.

---

### 9. `countParagraphs(String text)`

**Pendekatan:** Split berdasarkan baris kosong ganda `"\r?\n\s*\r?\n"`. Setiap blok non-empty dihitung sebagai paragraf.

---

### 10. `highlightAllOccurrences(String patternText, boolean caseInsensitive)`

**Fungsi:** Cari semua kemunculan `patternText` di `textAreaInput` dan tambahkan highlight.

**Detail:**

* Jika `caseInsensitive==true`, compile pattern dengan `CASE_INSENSITIVE | UNICODE_CHARACTER_CLASS`.
* Gunakan `Pattern.quote(patternText)` agar karakter khusus di pattern diperlakukan literal.
* Untuk setiap match, `high.addHighlight(matcher.start(), matcher.end(), highlightPainter);` dan hitung total.
* Mengembalikan jumlah kemunculan.

---

### 11. `clearHighlights()`

Menghapus semua highlight pada `textAreaInput` dan reset `lblCariCount` ke "Ditemukan: 0".

---

### 12. `saveToFile(File f)`

**Fungsi:** Menulis teks lengkap dan ringkasan label ke file teks.

**Format file:**

```
=== Aplikasi Penghitung Kata ===

Teks:
<isi teks>

===== Hasil Penghitungan =====
Kata: X
Karakter dgn spasi: Y
Karakter tanpa spasi: Z
Kalimat: A
Paragraf: B
```

**Catatan:** Method membuka `BufferedWriter` dan menulis string hasil.

---

### 13. `initComponents()` (Generated Code)

**Penjelasan:**

* Method yang dihasilkan GUI-builder (kemungkinan NetBeans). Menginisialisasi semua komponen Swing (JPanel, JTextArea, JButton, JLabel, JTextField, layout, font, warna, dsb.).
* Jangan mengedit secara manual kecuali Anda paham bagaimana GUI builder akan bereaksi (IDE biasanya akan menimpa perubahan saat regenerate).

Hal-hal penting yang di-setup di sini:

* `textAreaInput` font `Monospaced` ukuran 24.
* Tombol: `btnHitung`, `btnSimpan`, `btnCari`, `btnReset`, `btnKeluar` — masing-masing dengan font & warna background.
* Layout diatur menggunakan `GroupLayout`.
* `btnHitung`, `btnReset`, `btnKeluar` dihubungkan ke method `btnHitungActionPerformed`, `btnResetActionPerformed`, `btnKeluarActionPerformed`.

---

### 14. `btnHitungActionPerformed(...)`

**Fungsi:** Saat tombol Hitung diklik, method ini dipanggil.

**Logika:**

* Panggil `updateCounts()` dan `clearHighlights()`.
* Ambil teks; jika kosong → set `lblChars` ke "Karakter dgn spasi: 0" dan fokus ke textarea.
* Periksa apakah terdapat *spasi* (cek `text.indexOf(' ') >= 0`). Jika tidak ada spasi sama sekali (teks satu kata tanpa whitespace), `lblChars` diisi `-` (garis strip) sesuai desain. Jika ada spasi → `lblChars` diisi panjang string `text.length()` (termasuk spasi).

**Catatan:** Desain ini mencegah menampilkan jumlah karakter dgn spasi untuk teks yang tidak mengandung spasi (mungkin untuk kejelasan UX).

---

### 15. `btnResetActionPerformed(...)`

**Fungsi:** Reset seluruh form.

**Yang dilakukan:**

* Kosongkan `textAreaInput` dan `tfCari`.
* Hapus semua highlight (`clearHighlights()`).
* Reset semua label hasil perhitungan ke 0 / default.
* Fokus kembali ke `textAreaInput`.
* Sembunyikan `lblCariCount`.

---

### 16. `btnKeluarActionPerformed(...)`

Menampilkan `JOptionPane.showConfirmDialog` konfirmasi keluar. Jika `YES` → `System.exit(0)`; jika `NO` → fokus kembali ke textarea.

---

### 17. `main(String[] args)`

**Fungsi:** Bootstrapping aplikasi. Mencoba mengaktifkan Nimbus Look & Feel (jika tersedia), lalu menjalankan UI di Event Dispatch Thread dengan `java.awt.EventQueue.invokeLater(() -> new FormPenghitungKata().setVisible(true));`.

---

### 18. Variabel GUI (declaration)

Di bagian bawah terdapat deklarasi semua komponen (buttons, labels, textarea, dsb.) di dalam komentar `// Variables declaration - do not modify`. Ini standar generate GUI. Jangan ubah kecuali Anda paham.

---


## Contoh alur singkat pemakaian

1. Jalankan aplikasi → tampil window.
2. Ketik atau paste teks ke textarea. Label kata/kalimat/paragraf/karakter tanpa spasi akan update otomatis.
3. Tekan `Hitung` untuk menampilkan jumlah karakter termasuk spasi (jika ada spasi).
4. Untuk mencari kata: masukkan kata di kotak pencarian, klik `Cari` → semua kemunculan disorot dan jumlah ditampilkan.
5. Tekan `Simpan` untuk menyimpan teks + ringkasan ke file.
6. `Reset` untuk kosongkan; `Keluar` untuk keluar aplikasi (ada dialog konfirmasi).

---

## ketika di masukan text

<img width="908" height="785" alt="image" src="https://github.com/user-attachments/assets/0f985d77-399a-4c35-acb4-ecaef1ddc461" />

---

## ketika di klik hitung

<img width="937" height="787" alt="image" src="https://github.com/user-attachments/assets/822ef655-ae51-4cf9-94b1-225064fd66de" />

<img width="866" height="767" alt="image" src="https://github.com/user-attachments/assets/ffe674e8-0098-4858-b51b-94799af90b20" />

---

## Ketika di klik cari

<img width="986" height="801" alt="image" src="https://github.com/user-attachments/assets/1f35d95e-b78a-45b8-a356-8d2c3eb6eedb" />

---

## Ketika di reset

ketika di reset mereset semuanya, dan kembali ke bagian text

<img width="940" height="760" alt="image" src="https://github.com/user-attachments/assets/2df9caca-8f85-45f6-8884-89e054246f6d" />

---

## ketika di simpan

<img width="1019" height="747" alt="image" src="https://github.com/user-attachments/assets/b0f0e77d-0fb7-469e-a2c9-58db3df922fd" />

<img width="1017" height="778" alt="image" src="https://github.com/user-attachments/assets/6fd98624-c2c6-436e-934e-5c2634df2a43" />
---

## ketika keluar

kalau di klik no dia bakal balik ke text
<img width="933" height="777" alt="image" src="https://github.com/user-attachments/assets/f206c1c6-f6fb-4363-943a-3507878b1ac6" />

---










*Selesai.*
