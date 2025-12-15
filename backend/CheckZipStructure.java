import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CheckZipStructure {
    public static void main(String[] args) {
        String zipFilePath = "D:\\aiProject\\gitbook-project\\test-export.zip";
        try {
            ZipFile zipFile = new ZipFile(zipFilePath);
            System.out.println("Zip file structure:");
            System.out.println("===================");
            for (ZipEntry entry : zipFile.stream().toArray(ZipEntry[]::new)) {
                System.out.println(entry.getName());
            }
            zipFile.close();
        } catch (IOException e) {
            System.err.println("Error reading zip file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}