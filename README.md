# Generator Surat Otomatis

Aplikasi desktop Java untuk membuat surat resmi, melihat preview, dan menyimpannya sebagai PDF A4. Aplikasi berjalan sepenuhnya offline setelah dependency Maven pertama kali terunduh.

## Fitur

- Tiga template: Surat Undangan, Surat Tugas, dan Surat Keterangan.
- Form tambahan berubah mengikuti jenis surat.
- Validasi field wajib dengan penanda visual dan pesan yang jelas.
- Preview surat di dalam jendela JavaFX.
- Pemilihan nama serta folder tujuan melalui file chooser.
- Konfirmasi sebelum mengganti PDF yang sudah ada.
- Layout PDF A4 dengan kop, nomor, perihal, penerima, isi, informasi tambahan, tanda tangan, wrapping teks, nomor halaman, dan page break otomatis.
- Tampilan JavaFX modern yang dapat mengikuti perubahan ukuran jendela.

## Persyaratan

- JDK 21 LTS
- Apache Maven 3.9 atau lebih baru

Cek instalasi:

```bash
java -version
mvn -version
```

Pastikan output Maven menunjukkan Java 21.

## Menjalankan aplikasi

```bash
mvn clean javafx:run
```

## Build dan test

```bash
mvn clean test
mvn clean package
```

Hasil JAR berada di `target/surat-generator-1.0.0.jar`. Untuk menjalankan aplikasi, cara yang direkomendasikan selama pengembangan tetap `mvn javafx:run` karena Maven otomatis menyiapkan module path JavaFX.

## Membuat aplikasi desktop mandiri dengan jpackage

JDK 21 menyediakan `jpackage`. Build project terlebih dahulu, lalu salin dependency runtime:

```bash
mvn clean package dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/dependency
```

Linux:

```bash
jpackage \
  --type app-image \
  --name SuratGenerator \
  --input target \
  --main-jar surat-generator-1.0.0.jar \
  --main-class id.gensurat.app.Launcher \
  --java-options "--module-path dependency" \
  --java-options "--add-modules javafx.controls,javafx.fxml" \
  --dest target/installer
```

Jalankan perintah `jpackage` pada sistem operasi target. Untuk membuat `.exe`, jalankan di Windows; untuk aplikasi Linux, jalankan di Linux. Packaging native dapat memerlukan tool bawaan OS seperti WiX Toolset pada Windows.

## Struktur utama

```text
src/main/java/id/gensurat/
├── app/          # entry point JavaFX
├── controller/   # validasi, aksi tombol, dan koordinasi UI
├── model/        # model OOP ketiga jenis surat
├── service/      # preview dan generator PDF
└── util/         # helper nama file

src/main/resources/
├── view/main.fxml
└── css/style.css
```

`PdfGenerator` memiliki helper layout internal yang mengatur posisi vertikal, wrapping, margin, pembuatan halaman baru, dan footer. Model surat hanya menyimpan data dan perilaku isi surat; model tidak melakukan rendering PDF.

## Catatan versi pertama

- Kop surat masih menggunakan identitas generik aplikasi. Nama instansi, alamat, dan logo kustom dapat ditambahkan sebagai pengaturan pada versi berikutnya.
- Preview merupakan representasi JavaFX yang isinya sama dengan PDF, bukan viewer PDF penuh.
- Font PDF memakai font standar bawaan PDF agar aplikasi tetap ringan dan offline. Karakter yang tidak didukung font akan diganti dengan `?`.
