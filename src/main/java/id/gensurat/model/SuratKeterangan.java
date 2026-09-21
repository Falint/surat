package id.gensurat.model;

import java.time.LocalDate;

public final class SuratKeterangan extends Surat {
    public SuratKeterangan(String nomor, String penerima, String perihal, LocalDate tanggal,
                           String tempat, String isi, String namaPenandatangan,
                           String jabatanPenandatangan, String identitas, String status) {
        super(nomor, penerima, perihal, tanggal, tempat, isi, namaPenandatangan,
                jabatanPenandatangan, identitas, status);
    }

    @Override
    public JenisSurat getJenis() { return JenisSurat.KETERANGAN; }

    @Override
    public String getKalimatPembuka() {
        return "Yang bertanda tangan di bawah ini menerangkan bahwa:";
    }

    @Override
    public String getKalimatPenutup() {
        return "Demikian surat keterangan ini dibuat dengan sebenar-benarnya agar dapat dipergunakan sebagaimana mestinya.";
    }
}
