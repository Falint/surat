package id.gensurat.model;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Surat {
    private final String nomor;
    private final String penerima;
    private final String perihal;
    private final LocalDate tanggal;
    private final String tempat;
    private final String isi;
    private final String namaPenandatangan;
    private final String jabatanPenandatangan;
    private final String detailUtama;
    private final String detailKedua;

    protected Surat(String nomor, String penerima, String perihal, LocalDate tanggal,
                    String tempat, String isi, String namaPenandatangan,
                    String jabatanPenandatangan, String detailUtama, String detailKedua) {
        this.nomor = Objects.requireNonNull(nomor);
        this.penerima = Objects.requireNonNull(penerima);
        this.perihal = Objects.requireNonNull(perihal);
        this.tanggal = Objects.requireNonNull(tanggal);
        this.tempat = Objects.requireNonNull(tempat);
        this.isi = Objects.requireNonNull(isi);
        this.namaPenandatangan = Objects.requireNonNull(namaPenandatangan);
        this.jabatanPenandatangan = Objects.requireNonNull(jabatanPenandatangan);
        this.detailUtama = Objects.requireNonNull(detailUtama);
        this.detailKedua = Objects.requireNonNull(detailKedua);
    }

    public abstract JenisSurat getJenis();

    public abstract String getKalimatPembuka();

    public abstract String getKalimatPenutup();

    public String getJudul() {
        return getJenis().getNama().toUpperCase();
    }

    public String getNomor() { return nomor; }
    public String getPenerima() { return penerima; }
    public String getPerihal() { return perihal; }
    public LocalDate getTanggal() { return tanggal; }
    public String getTempat() { return tempat; }
    public String getIsi() { return isi; }
    public String getNamaPenandatangan() { return namaPenandatangan; }
    public String getJabatanPenandatangan() { return jabatanPenandatangan; }
    public String getDetailUtama() { return detailUtama; }
    public String getDetailKedua() { return detailKedua; }
}
