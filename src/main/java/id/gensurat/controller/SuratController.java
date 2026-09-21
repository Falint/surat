package id.gensurat.controller;

import id.gensurat.model.JenisSurat;
import id.gensurat.model.Surat;
import id.gensurat.model.SuratFactory;
import id.gensurat.service.PdfGenerator;
import id.gensurat.service.PreviewService;
import id.gensurat.util.FileUtil;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SuratController {
    @FXML private ComboBox<JenisSurat> jenisSuratCombo;
    @FXML private TextField nomorField;
    @FXML private TextField penerimaField;
    @FXML private TextField perihalField;
    @FXML private DatePicker tanggalPicker;
    @FXML private TextField tempatField;
    @FXML private TextArea isiArea;
    @FXML private TextField namaPenandatanganField;
    @FXML private TextField jabatanPenandatanganField;
    @FXML private Label detailUtamaLabel;
    @FXML private TextField detailUtamaField;
    @FXML private Label detailKeduaLabel;
    @FXML private TextField detailKeduaField;
    @FXML private Label statusLabel;

    private final PdfGenerator pdfGenerator = new PdfGenerator();
    private final PreviewService previewService = new PreviewService();
    private final List<javafx.scene.control.Control> kontrolWajib = new ArrayList<>();

    @FXML
    private void initialize() {
        jenisSuratCombo.setItems(FXCollections.observableArrayList(JenisSurat.values()));
        jenisSuratCombo.getSelectionModel().select(JenisSurat.UNDANGAN);
        tanggalPicker.setValue(LocalDate.now());
        isiArea.setText("Tuliskan maksud dan keterangan utama surat di sini.");
        kontrolWajib.addAll(List.of(jenisSuratCombo, nomorField, penerimaField, perihalField,
                tanggalPicker, tempatField, isiArea, namaPenandatanganField,
                jabatanPenandatanganField, detailUtamaField, detailKeduaField));

        jenisSuratCombo.valueProperty().addListener((obs, lama, baru) -> perbaruiFormDinamis(baru));
        perbaruiFormDinamis(jenisSuratCombo.getValue());

        ChangeListener<Object> hapusError = (obs, lama, baru) -> statusLabel.setText("");
        jenisSuratCombo.valueProperty().addListener(hapusError);
        tanggalPicker.valueProperty().addListener(hapusError);
        for (javafx.scene.control.Control kontrol : kontrolWajib) {
            if (kontrol instanceof TextField field) field.textProperty().addListener(hapusError);
            if (kontrol instanceof TextArea area) area.textProperty().addListener(hapusError);
        }
    }

    @FXML
    private void preview() {
        Optional<Surat> surat = validasiDanBuatSurat();
        surat.ifPresent(nilai -> previewService.tampilkan(nilai, statusLabel.getScene().getWindow()));
    }

    @FXML
    private void generatePdf() {
        Optional<Surat> hasil = validasiDanBuatSurat();
        if (hasil.isEmpty()) return;
        Surat surat = hasil.get();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Simpan PDF Surat");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Dokumen PDF", "*.pdf"));
        chooser.setInitialFileName(FileUtil.namaFileAman(
                surat.getJenis().getNama() + "_" + surat.getNomor()) + ".pdf");
        File file = chooser.showSaveDialog(statusLabel.getScene().getWindow());
        if (file == null) return;
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }

        if (file.exists()) {
            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION,
                    "File sudah ada. Apakah Anda ingin menggantinya?", ButtonType.YES, ButtonType.NO);
            konfirmasi.setHeaderText("Konfirmasi penggantian file");
            if (konfirmasi.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;
        }

        try {
            pdfGenerator.generate(surat, file.toPath());
            statusLabel.setText("PDF berhasil disimpan: " + file.getAbsolutePath());
            statusLabel.getStyleClass().removeAll("status-error");
            statusLabel.getStyleClass().add("status-success");
            Alert sukses = new Alert(Alert.AlertType.INFORMATION);
            sukses.setTitle("PDF Berhasil Dibuat");
            sukses.setHeaderText("Surat berhasil disimpan");
            sukses.setContentText(file.getAbsolutePath());
            sukses.showAndWait();
        } catch (IOException exception) {
            tampilkanError("PDF gagal dibuat", "Pastikan folder dapat ditulis dan file tidak sedang dibuka.\n\n" + exception.getMessage());
        }
    }

    @FXML
    private void resetForm() {
        jenisSuratCombo.getSelectionModel().select(JenisSurat.UNDANGAN);
        nomorField.clear();
        penerimaField.clear();
        perihalField.clear();
        tanggalPicker.setValue(LocalDate.now());
        tempatField.clear();
        isiArea.clear();
        namaPenandatanganField.clear();
        jabatanPenandatanganField.clear();
        detailUtamaField.clear();
        detailKeduaField.clear();
        statusLabel.setText("");
        kontrolWajib.forEach(kontrol -> kontrol.getStyleClass().remove("input-error"));
    }

    private void perbaruiFormDinamis(JenisSurat jenis) {
        if (jenis == null) return;
        detailUtamaLabel.setText(jenis.getLabelDetailUtama());
        detailKeduaLabel.setText(jenis.getLabelDetailKedua());
        detailUtamaField.setPromptText(contohDetailUtama(jenis));
        detailKeduaField.setPromptText(contohDetailKedua(jenis));
    }

    private Optional<Surat> validasiDanBuatSurat() {
        kontrolWajib.forEach(kontrol -> kontrol.getStyleClass().remove("input-error"));
        List<String> kosong = new ArrayList<>();
        wajib(jenisSuratCombo, jenisSuratCombo.getValue(), "Jenis surat", kosong);
        wajib(nomorField, nomorField.getText(), "Nomor surat", kosong);
        wajib(penerimaField, penerimaField.getText(), "Tujuan/penerima", kosong);
        wajib(perihalField, perihalField.getText(), "Perihal", kosong);
        wajib(tanggalPicker, tanggalPicker.getValue(), "Tanggal", kosong);
        wajib(tempatField, tempatField.getText(), "Tempat", kosong);
        wajib(isiArea, isiArea.getText(), "Isi/keterangan", kosong);
        wajib(namaPenandatanganField, namaPenandatanganField.getText(), "Nama penandatangan", kosong);
        wajib(jabatanPenandatanganField, jabatanPenandatanganField.getText(), "Jabatan penandatangan", kosong);
        wajib(detailUtamaField, detailUtamaField.getText(), detailUtamaLabel.getText(), kosong);
        wajib(detailKeduaField, detailKeduaField.getText(), detailKeduaLabel.getText(), kosong);

        if (!kosong.isEmpty()) {
            String pesan = "Lengkapi field wajib: " + String.join(", ", kosong) + ".";
            statusLabel.setText(pesan);
            statusLabel.getStyleClass().removeAll("status-success");
            statusLabel.getStyleClass().add("status-error");
            tampilkanError("Data belum lengkap", pesan);
            return Optional.empty();
        }

        return Optional.of(SuratFactory.buat(jenisSuratCombo.getValue(), bersih(nomorField),
                bersih(penerimaField), bersih(perihalField), tanggalPicker.getValue(),
                bersih(tempatField), isiArea.getText().trim(), bersih(namaPenandatanganField),
                bersih(jabatanPenandatanganField), bersih(detailUtamaField), bersih(detailKeduaField)));
    }

    private void wajib(javafx.scene.control.Control kontrol, Object nilai, String nama, List<String> kosong) {
        boolean tidakAda = nilai == null || nilai.toString().isBlank();
        if (tidakAda) {
            kosong.add(nama);
            if (!kontrol.getStyleClass().contains("input-error")) kontrol.getStyleClass().add("input-error");
        }
    }

    private String bersih(TextField field) {
        return field.getText().trim();
    }

    private String contohDetailUtama(JenisSurat jenis) {
        return switch (jenis) {
            case UNDANGAN -> "Senin, 21 September 2026 pukul 09.00 WIB";
            case TUGAS -> "21-23 September 2026";
            case KETERANGAN -> "Nama, NIM, atau identitas subjek";
        };
    }

    private String contohDetailKedua(JenisSurat jenis) {
        return switch (jenis) {
            case UNDANGAN -> "Rapat koordinasi kegiatan";
            case TUGAS -> "Mengikuti dan menyusun laporan kegiatan";
            case KETERANGAN -> "Status atau hal yang diterangkan";
        };
    }

    private void tampilkanError(String judul, String pesan) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(judul);
        alert.setHeaderText(judul);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
