package id.gensurat.service;

import id.gensurat.model.Surat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class PreviewService {
    private static final DateTimeFormatter FORMAT_TANGGAL =
            DateTimeFormatter.ofPattern("d MMMM uuuu", Locale.forLanguageTag("id-ID"));

    public void tampilkan(Surat surat, Window pemilik) {
        VBox halaman = new VBox(10);
        halaman.getStyleClass().add("preview-page");
        halaman.setPadding(new Insets(42, 55, 55, 55));
        halaman.setPrefWidth(720);
        halaman.setMinHeight(960);

        HBox kop = buatKop();
        Region garis = new Region();
        garis.getStyleClass().add("letter-divider");
        Label judul = label(surat.getJudul(), "letter-title");
        judul.setAlignment(Pos.CENTER);
        judul.setMaxWidth(Double.MAX_VALUE);
        Label nomor = label("Nomor: " + surat.getNomor(), "letter-center");
        Label tanggal = label(surat.getTempat() + ", " + surat.getTanggal().format(FORMAT_TANGGAL), "letter-right");

        halaman.getChildren().addAll(kop, garis, judul, nomor, tanggal,
                baris("Perihal", surat.getPerihal()),
                label("Yth. " + surat.getPenerima() + "\ndi tempat", "letter-body"),
                label(surat.getKalimatPembuka(), "letter-body"),
                baris(surat.getJenis().getLabelDetailUtama(), surat.getDetailUtama()),
                baris(surat.getJenis().getLabelDetailKedua(), surat.getDetailKedua()),
                label(surat.getIsi(), "letter-body-indent"),
                label(surat.getKalimatPenutup(), "letter-body-indent"),
                buatTandaTangan(surat));

        ScrollPane scroll = new ScrollPane(halaman);
        scroll.setFitToWidth(false);
        scroll.getStyleClass().add("preview-scroll");

        Stage stage = new Stage();
        stage.initOwner(pemilik);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Preview - " + surat.getJenis().getNama());
        Scene scene = new Scene(scroll, 860, 720);
        scene.getStylesheets().add(PreviewService.class.getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private HBox buatKop() {
        Rectangle logo = new Rectangle(58, 58);
        logo.setArcWidth(12);
        logo.setArcHeight(12);
        logo.getStyleClass().add("preview-logo");
        Label huruf = label("SG", "preview-logo-text");
        javafx.scene.layout.StackPane logoPane = new javafx.scene.layout.StackPane(logo, huruf);

        VBox teks = new VBox(3,
                label("GENERATOR SURAT OTOMATIS", "letter-heading"),
                label("Dokumen Administrasi Resmi", "letter-subheading"),
                label("Aplikasi desktop offline", "letter-caption"));
        teks.setAlignment(Pos.CENTER);
        HBox kop = new HBox(22, logoPane, teks);
        kop.setAlignment(Pos.CENTER_LEFT);
        teks.setPrefWidth(500);
        return kop;
    }

    private HBox baris(String kunci, String nilai) {
        Label labelKunci = label(kunci, "letter-key");
        labelKunci.setPrefWidth(135);
        Label titikDua = label(":", "letter-body");
        Label labelNilai = label(nilai, "letter-body");
        labelNilai.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelNilai, javafx.scene.layout.Priority.ALWAYS);
        return new HBox(8, labelKunci, titikDua, labelNilai);
    }

    private VBox buatTandaTangan(Surat surat) {
        VBox tandaTangan = new VBox(3);
        tandaTangan.getStyleClass().add("signature-box");
        tandaTangan.setMaxWidth(270);
        tandaTangan.setAlignment(Pos.TOP_LEFT);
        Region ruang = new Region();
        ruang.setPrefHeight(58);
        Label nama = label(surat.getNamaPenandatangan(), "signature-name");
        tandaTangan.getChildren().addAll(
                label(surat.getTempat() + ", " + surat.getTanggal().format(FORMAT_TANGGAL), "letter-body"),
                label(surat.getJabatanPenandatangan(), "letter-body"), ruang, nama);
        VBox pembungkus = new VBox(tandaTangan);
        pembungkus.setAlignment(Pos.TOP_RIGHT);
        pembungkus.setPadding(new Insets(15, 15, 0, 0));
        return pembungkus;
    }

    private Label label(String teks, String kelas) {
        Label label = new Label(teks);
        label.getStyleClass().add(kelas);
        label.setWrapText(true);
        return label;
    }
}
