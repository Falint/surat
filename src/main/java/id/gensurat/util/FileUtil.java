package id.gensurat.util;

public final class FileUtil {
    private FileUtil() {
    }

    public static String namaFileAman(String nilai) {
        String bersih = nilai == null ? "surat" : nilai.trim()
                .replaceAll("[^a-zA-Z0-9._-]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return bersih.isBlank() ? "surat" : bersih;
    }
}
