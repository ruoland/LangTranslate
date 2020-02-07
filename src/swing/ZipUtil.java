package swing;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {


    public static void compress(Component component) {
        File file = new File("./번역");

        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(new File(MainNew.MC_FOLDER+"/resourcepacks/아이템 번역 리소스팩.zip"));
            zos = new ZipOutputStream(fos);
            searchDirectory(file,file.getPath(), zos);

            zos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(component, "리소스팩 생성 중에 오류가 발생하여 생성하지 못했습니다. : "+e.getLocalizedMessage());
        }

    }

    public static void searchDirectory(File file, String root, ZipOutputStream zos) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                searchDirectory(f, root, zos);
                System.out.println("file = > " + f);

            }
        } else {
            compressZip(file, root, zos);
        }
    }

    public static void compressZip(File file, String root, ZipOutputStream zos) {
        try {
            FileInputStream fis = new FileInputStream(file);
            String zipName = file.getPath().replace(root + "\\", "");

            ZipEntry zipEntry = new ZipEntry(zipName);

            zos.putNextEntry(zipEntry);
            int length = (int) file.length();
            byte[] buffer = new byte[length];

            fis.read(buffer, 0, length);
            zos.write(buffer, 0, length);
            zos.closeEntry();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
