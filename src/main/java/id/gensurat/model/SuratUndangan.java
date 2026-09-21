package id.gensurat.model;

import java.time.LocalDate;

public final class SuratUndangan extends Surat {
    public SuratUndangan(String nomor, String penerima, String perihal, LocalDate tanggal,
                         String tempat, String isi, String namaPenandatangan,
                         String jabatanPenandatangan, String waktu, String agenda) {
        super(nomor, penerima, perihal, tanggal, tempat, isi, namaPenandatangan,
                jabatanPenandatangan, waktu, agenda);
    }

    @Override
    public JenisSurat getJenis() { return JenisSurat.UNDANGAN; }

    @Override
    public String getKalimatPembuka() {
        return "Dengan hormat, kami mengundang Saudara/i untuk menghadiri kegiatan berikut:";
    }

    @Override
    public String getKalimatPenutup() {
        return "Demikian undangan ini kami sampaikan. Atas perhatian dan kehadirannya, kami ucapkan terima kasih.";
    }
}
