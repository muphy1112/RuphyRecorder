package me.muphy.servicce;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    public static void main(String[] args) {
        new ZipService().zip("D:/temp");
    }

    /**
     * 压缩
     *
     * @param filepath
     * @return
     */
    public boolean zip(String filepath) {
        return zip(filepath, new File(filepath).getAbsolutePath() + ".zip");
    }

    /**
     * 压缩
     *
     * @param filepath
     * @param zipPath
     * @return
     */
    public boolean zip(String filepath, String zipPath) {
        try {
            File file = new File(filepath);// 要被压缩的文件夹
            File zipFile = new File(zipPath);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File fileSec : files) {
                    if (!fileSec.getAbsolutePath().equals(zipFile.getAbsolutePath()))
                        recursionZip(zipOut, fileSec, file.getName() + File.separator);
                }
            } else {
                recursionZip(zipOut, file, "");
            }
            zipOut.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void recursionZip(ZipOutputStream zipOut, File file, String baseDir) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileSec : files) {
                try {
                    recursionZip(zipOut, fileSec, baseDir + file.getName() + File.separator);
                } catch (Exception  e){
                    throw e;
                }
                recursionZip(zipOut, fileSec, baseDir + file.getName() + File.separator);
            }
        } else {
            byte[] buf = new byte[1024];
            InputStream input = new FileInputStream(file);
            zipOut.putNextEntry(new ZipEntry(baseDir + file.getName()));
            System.out.println(file + "压缩成功！");
            int len;
            while ((len = input.read(buf)) != -1) {
                zipOut.write(buf, 0, len);
            }
            input.close();
        }
    }

    /**
     * 标准 解压
     *
     * @param zippath
     * @return
     */
    public boolean unZip(String zippath) {
        System.out.println("done.");
        return unZip(zippath, new File(zippath).getParent());
    }

    /**
     * 标准 解压
     *
     * @param zippath
     * @param outPath
     * @return
     */
    public boolean unZip(String zippath, String outPath) {
        try {
            ZipInputStream Zin = new ZipInputStream(new FileInputStream(zippath));//输入源zip路径
            BufferedInputStream Bin = new BufferedInputStream(Zin);
            File Fout = null;
            ZipEntry entry;
            try {
                while ((entry = Zin.getNextEntry()) != null && !entry.isDirectory()) {
                    Fout = new File(outPath, entry.getName());
                    if (!Fout.exists()) {
                        (new File(Fout.getParent())).mkdirs();
                    }
                    FileOutputStream out = new FileOutputStream(Fout);
                    BufferedOutputStream Bout = new BufferedOutputStream(out);
                    int b;
                    while ((b = Bin.read()) != -1) {
                        Bout.write(b);
                    }
                    Bout.close();
                    out.close();
                    System.out.println(Fout + "解压成功！");
                }
                Bin.close();
                Zin.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
