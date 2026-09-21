package id.gensurat.service;

import id.gensurat.model.JenisSurat;
import id.gensurat.model.Surat;
import id.gensurat.model.SuratFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfGeneratorTest {
    @TempDir
    Path folderSementara;

    private final PdfGenerator generator = new PdfGenerator();

    @Test
    void menghasilkanPdfValidUntukSemuaJenisSurat() throws Exception {
        for (JenisSurat jenis : JenisSurat.values()) {
            Surat surat = buatSurat(jenis, "Isi surat pengujian yang harus muncul pada dokumen PDF.");
            Path hasil = folderSementara.resolve(jenis.name().toLowerCase() + ".pdf");

            generator.generate(surat, hasil);

            assertTrue(Files.exists(hasil));
            assertTrue(Files.size(hasil) > 1_000);
            try (PDDocument document = Loader.loadPDF(hasil.toFile())) {
                String teks = new PDFTextStripper().getText(document);
                assertTrue(document.getNumberOfPages() >= 1);
                assertTrue(teks.contains(surat.getJudul()));
                assertTrue(teks.contains("023/TEST/IX/2026"));
                assertTrue(teks.contains("Isi surat pengujian"));
            }
        }
    }

    @Test
    void isiPanjangOtomatisBerlanjutKeHalamanBerikutnya() throws Exception {
        String isiPanjang = "Paragraf panjang untuk menguji pembungkusan teks dan pergantian halaman otomatis. ".repeat(180);
        Surat surat = buatSurat(JenisSurat.UNDANGAN, isiPanjang);
        Path hasil = folderSementara.resolve("panjang.pdf");

        generator.generate(surat, hasil);

        try (PDDocument document = Loader.loadPDF(hasil.toFile())) {
            assertTrue(document.getNumberOfPages() > 1);
            String teks = new PDFTextStripper().getText(document);
            assertTrue(teks.contains("Halaman 2"));
            assertTrue(teks.contains("Ahmad Penguji"));
        }
    }

    private Surat buatSurat(JenisSurat jenis, String isi) {
        return SuratFactory.buat(jenis, "023/TEST/IX/2026", "Saudara Pengguna",
                "Pengujian aplikasi", LocalDate.of(2026, 9, 21), "Depok", isi,
                "Ahmad Penguji", "Ketua Pelaksana", "21 September 2026",
                "Kegiatan pengujian generator surat");
    }
}
