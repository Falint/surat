package id.gensurat.model;

public enum JenisSurat {
    UNDANGAN("Surat Undangan", "Waktu pelaksanaan", "Agenda"),
    TUGAS("Surat Tugas", "Periode tugas", "Rincian tugas"),
    KETERANGAN("Surat Keterangan", "Identitas/subjek", "Status/keterangan");

    private final String nama;
    private final String labelDetailUtama;
    private final String labelDetailKedua;

    JenisSurat(String nama, String labelDetailUtama, String labelDetailKedua) {
        this.nama = nama;
        this.labelDetailUtama = labelDetailUtama;
        this.labelDetailKedua = labelDetailKedua;
    }

    public String getNama() {
        return nama;
    }

    public String getLabelDetailUtama() {
        return labelDetailUtama;
    }

    public String getLabelDetailKedua() {
        return labelDetailKedua;
    }

    @Override
    public String toString() {
        return nama;
    }
}
