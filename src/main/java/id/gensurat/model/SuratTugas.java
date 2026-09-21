package id.gensurat.model;

import java.time.LocalDate;

public final class SuratTugas extends Surat {
    public SuratTugas(String nomor, String penerima, String perihal, LocalDate tanggal,
                      String tempat, String isi, String namaPenandatangan,
                      String jabatanPenandatangan, String periode, String rincianTugas) {
        super(nomor, penerima, perihal, tanggal, tempat, isi, namaPenandatangan,
                jabatanPenandatangan, periode, rincianTugas);
    }

    @Override
    public JenisSurat getJenis() { return JenisSurat.TUGAS; }

    @Override
    public String getKalimatPembuka() {
        return "Yang bertanda tangan di bawah ini memberikan tugas kepada:";
    }

    @Override
    public String getKalimatPenutup() {
        return "Surat tugas ini dibuat untuk dilaksanakan dengan penuh tanggung jawab dan dipergunakan sebagaimana mestinya.";
    }
}
