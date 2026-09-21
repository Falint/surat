package id.gensurat.model;

import java.time.LocalDate;

public final class SuratFactory {
    private SuratFactory() {
    }

    public static Surat buat(JenisSurat jenis, String nomor, String penerima, String perihal,
                             LocalDate tanggal, String tempat, String isi,
                             String namaPenandatangan, String jabatanPenandatangan,
                             String detailUtama, String detailKedua) {
        return switch (jenis) {
            case UNDANGAN -> new SuratUndangan(nomor, penerima, perihal, tanggal, tempat, isi,
                    namaPenandatangan, jabatanPenandatangan, detailUtama, detailKedua);
            case TUGAS -> new SuratTugas(nomor, penerima, perihal, tanggal, tempat, isi,
                    namaPenandatangan, jabatanPenandatangan, detailUtama, detailKedua);
            case KETERANGAN -> new SuratKeterangan(nomor, penerima, perihal, tanggal, tempat, isi,
                    namaPenandatangan, jabatanPenandatangan, detailUtama, detailKedua);
        };
    }
}
