package id.gensurat.service;

import id.gensurat.model.Surat;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PdfGenerator {
    private static final DateTimeFormatter FORMAT_TANGGAL =
            DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.forLanguageTag("id-ID"));

    public void generate(Surat surat, Path tujuan) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDDocumentInformation info = new PDDocumentInformation();
            info.setTitle(surat.getJudul() + " - " + surat.getNomor());
            info.setAuthor(surat.getNamaPenandatangan());
            info.setSubject(surat.getPerihal());
            info.setCreator("Generator Surat Otomatis");
            document.setDocumentInformation(info);

            try (Layout layout = new Layout(document, surat)) {
                layout.tulisDokumen();
            }
            document.save(tujuan.toFile());
        }
    }

    private static final class Layout implements AutoCloseable {
        private static final float MARGIN_KIRI = 62;
        private static final float MARGIN_KANAN = 62;
        private static final float MARGIN_BAWAH = 55;
        private static final float LEBAR = PDRectangle.A4.getWidth() - MARGIN_KIRI - MARGIN_KANAN;
        private static final Color BIRU = new Color(31, 78, 121);
        private static final Color ABU = new Color(90, 103, 116);

        private final PDDocument document;
        private final Surat surat;
        private final PDFont regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        private final PDFont bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        private PDPageContentStream stream;
        private float y;
        private int nomorHalaman;

        private Layout(PDDocument document, Surat surat) throws IOException {
            this.document = document;
            this.surat = surat;
            halamanBaru(false);
        }

        private void tulisDokumen() throws IOException {
            judulTengah(surat.getJudul(), 14);
            teksTengah("Nomor: " + surat.getNomor(), regular, 10.5f);
            jarak(20);

            teksKanan(surat.getTempat() + ", " + surat.getTanggal().format(FORMAT_TANGGAL), regular, 10.5f);
            jarak(18);
            pasangan("Perihal", surat.getPerihal());
            jarak(13);

            if (!surat.getPenerima().isBlank()) {
                paragraf("Yth. " + surat.getPenerima() + "\ndi tempat", regular, 10.5f, 15, 0);
                jarak(10);
            }

            paragraf(surat.getKalimatPembuka(), regular, 10.5f, 16, 0);
            jarak(8);
            pasangan(surat.getJenis().getLabelDetailUtama(), surat.getDetailUtama());
            pasangan(surat.getJenis().getLabelDetailKedua(), surat.getDetailKedua());
            jarak(10);
            paragraf(surat.getIsi(), regular, 10.5f, 16, 24);
            jarak(10);
            paragraf(surat.getKalimatPenutup(), regular, 10.5f, 16, 24);

            tandaTangan();
        }

        private void halamanBaru(boolean lanjutan) throws IOException {
            tutupHalaman();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            nomorHalaman++;
            y = PDRectangle.A4.getHeight() - 52;
            kopSurat(lanjutan);
        }

        private void kopSurat(boolean lanjutan) throws IOException {
            float logoX = MARGIN_KIRI + 27;
            float logoY = y - 27;
            stream.setNonStrokingColor(BIRU);
            stream.addRect(logoX - 25, logoY - 25, 50, 50);
            stream.fill();
            stream.setNonStrokingColor(Color.WHITE);
            tulisPada("SG", bold, 16, logoX - 12, logoY - 6);

            stream.setNonStrokingColor(BIRU);
            teksTengahPada("GENERATOR SURAT OTOMATIS", bold, 15, y - 10);
            stream.setNonStrokingColor(ABU);
            teksTengahPada("Dokumen Administrasi Resmi", regular, 10.5f, y - 28);
            teksTengahPada("Aplikasi desktop offline", regular, 9, y - 43);

            y -= 65;
            stream.setStrokingColor(BIRU);
            stream.setLineWidth(2.2f);
            stream.moveTo(MARGIN_KIRI, y);
            stream.lineTo(PDRectangle.A4.getWidth() - MARGIN_KANAN, y);
            stream.stroke();
            y -= 6;
            stream.setLineWidth(0.6f);
            stream.moveTo(MARGIN_KIRI, y);
            stream.lineTo(PDRectangle.A4.getWidth() - MARGIN_KANAN, y);
            stream.stroke();
            y -= lanjutan ? 23 : 28;
            if (lanjutan) {
                teksTengah("Lanjutan " + surat.getJudul(), bold, 9);
                jarak(8);
            }
        }

        private void pasangan(String label, String nilai) throws IOException {
            List<String> baris = bungkus(nilai, regular, 10.5f, LEBAR - 105);
            pastikanRuang(Math.max(1, baris.size()) * 15 + 2);
            tulisPada(label, bold, 10.5f, MARGIN_KIRI, y);
            tulisPada(":", regular, 10.5f, MARGIN_KIRI + 92, y);
            for (int i = 0; i < baris.size(); i++) {
                tulisPada(baris.get(i), regular, 10.5f, MARGIN_KIRI + 105, y);
                y -= 15;
                if (i < baris.size() - 1) {
                    pastikanRuang(15);
                }
            }
        }

        private void paragraf(String teks, PDFont font, float ukuran, float tinggiBaris, float inden) throws IOException {
            String[] paragraf = teks.replace("\r", "").split("\n", -1);
            for (int p = 0; p < paragraf.length; p++) {
                List<String> baris = bungkus(paragraf[p], font, ukuran, LEBAR - inden);
                if (baris.isEmpty()) baris = List.of("");
                for (String barisTeks : baris) {
                    pastikanRuang(tinggiBaris);
                    tulisPada(barisTeks, font, ukuran, MARGIN_KIRI + inden, y);
                    y -= tinggiBaris;
                }
                if (p < paragraf.length - 1) y -= 3;
            }
        }

        private void tandaTangan() throws IOException {
            pastikanRuang(145);
            y -= 24;
            float x = MARGIN_KIRI + LEBAR * 0.58f;
            tulisPada(surat.getTempat() + ", " + surat.getTanggal().format(FORMAT_TANGGAL), regular, 10.5f, x, y);
            y -= 17;
            tulisPada(surat.getJabatanPenandatangan(), regular, 10.5f, x, y);
            y -= 65;
            tulisPada(surat.getNamaPenandatangan(), bold, 10.5f, x, y);
            float namaWidth = lebarTeks(surat.getNamaPenandatangan(), bold, 10.5f);
            stream.setStrokingColor(Color.BLACK);
            stream.setLineWidth(0.5f);
            stream.moveTo(x, y - 2);
            stream.lineTo(x + Math.min(namaWidth, LEBAR * 0.4f), y - 2);
            stream.stroke();
        }

        private void judulTengah(String teks, float ukuran) throws IOException {
            teksTengah(teks, bold, ukuran);
            float width = lebarTeks(teks, bold, ukuran);
            float x = (PDRectangle.A4.getWidth() - width) / 2;
            stream.moveTo(x, y - 2);
            stream.lineTo(x + width, y - 2);
            stream.stroke();
            y -= 5;
        }

        private void teksTengah(String teks, PDFont font, float ukuran) throws IOException {
            pastikanRuang(ukuran + 4);
            teksTengahPada(teks, font, ukuran, y);
            y -= ukuran + 5;
        }

        private void teksKanan(String teks, PDFont font, float ukuran) throws IOException {
            pastikanRuang(ukuran + 4);
            float x = PDRectangle.A4.getWidth() - MARGIN_KANAN - lebarTeks(teks, font, ukuran);
            tulisPada(teks, font, ukuran, x, y);
            y -= ukuran + 5;
        }

        private void teksTengahPada(String teks, PDFont font, float ukuran, float posisiY) throws IOException {
            float x = (PDRectangle.A4.getWidth() - lebarTeks(teks, font, ukuran)) / 2;
            tulisPada(teks, font, ukuran, x, posisiY);
        }

        private void tulisPada(String teks, PDFont font, float ukuran, float x, float posisiY) throws IOException {
            stream.beginText();
            stream.setFont(font, ukuran);
            stream.setRenderingMode(RenderingMode.FILL);
            stream.newLineAtOffset(x, posisiY);
            stream.showText(aman(teks, font));
            stream.endText();
        }

        private List<String> bungkus(String teks, PDFont font, float ukuran, float lebarMaksimum) throws IOException {
            List<String> hasil = new ArrayList<>();
            String bersih = teks == null ? "" : teks.trim().replaceAll("\\s+", " ");
            if (bersih.isEmpty()) return hasil;
            StringBuilder baris = new StringBuilder();
            for (String kata : bersih.split(" ")) {
                String kandidat = baris.isEmpty() ? kata : baris + " " + kata;
                if (lebarTeks(kandidat, font, ukuran) <= lebarMaksimum) {
                    baris.setLength(0);
                    baris.append(kandidat);
                } else {
                    if (!baris.isEmpty()) hasil.add(baris.toString());
                    baris.setLength(0);
                    if (lebarTeks(kata, font, ukuran) <= lebarMaksimum) {
                        baris.append(kata);
                    } else {
                        for (char karakter : kata.toCharArray()) {
                            String kandidatKarakter = baris.toString() + karakter;
                            if (lebarTeks(kandidatKarakter, font, ukuran) > lebarMaksimum && !baris.isEmpty()) {
                                hasil.add(baris.toString());
                                baris.setLength(0);
                            }
                            baris.append(karakter);
                        }
                    }
                }
            }
            if (!baris.isEmpty()) hasil.add(baris.toString());
            return hasil;
        }

        private float lebarTeks(String teks, PDFont font, float ukuran) throws IOException {
            return font.getStringWidth(aman(teks, font)) / 1000f * ukuran;
        }

        private String aman(String teks, PDFont font) {
            String normal = teks.replace('–', '-').replace('—', '-')
                    .replace('“', '"').replace('”', '"')
                    .replace('’', '\'').replace('•', '-');
            StringBuilder hasil = new StringBuilder();
            for (char karakter : normal.toCharArray()) {
                try {
                    font.encode(String.valueOf(karakter));
                    hasil.append(karakter);
                } catch (Exception tidakDidukung) {
                    hasil.append('?');
                }
            }
            return hasil.toString();
        }

        private void pastikanRuang(float kebutuhan) throws IOException {
            if (y - kebutuhan < MARGIN_BAWAH) halamanBaru(true);
        }

        private void jarak(float nilai) throws IOException {
            pastikanRuang(nilai);
            y -= nilai;
        }

        private void tutupHalaman() throws IOException {
            if (stream == null) return;
            stream.setNonStrokingColor(ABU);
            String footer = "Halaman " + nomorHalaman;
            teksTengahPada(footer, regular, 8, 30);
            stream.close();
            stream = null;
        }

        @Override
        public void close() throws IOException {
            tutupHalaman();
        }
    }
}
