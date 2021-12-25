import java.io.*;
import java.util.*;
import java.util.zip.*;

public class Main {
    private static StringBuilder log = new StringBuilder();
    private static Date date = new Date();

    public static void main(String[] args) throws IOException {
        makeDir("C://Games/src");
        makeDir("C://Games/res");
        makeDir("C://Games/savegames");
        makeDir("C://Games/temp");
        makeDir("C://Games/src/main");
        makeDir("C://Games/src/test");
        makeFile("C://Games/src/main/Main.java");
        makeFile("C://Games/src/main/Utils.java");
        makeDir("C://Games/res/drawables");
        makeDir("C://Games/res/vectors");
        makeDir("C://Games/res/icons");
        makeFile("C://Games/temp/temp.txt");

        GameProgress prog1 = new GameProgress(98, 4, 5, 100.5);
        GameProgress prog2 = new GameProgress(55, 2, 3, 55.6);
        GameProgress prog3 = new GameProgress(10, 1, 1, 15.3);
        saveGame("C://Games/savegames/save1.dat", prog1);
        saveGame("C://Games/savegames/save2.dat", prog2);
        saveGame("C://Games/savegames/save3.dat", prog3);

        ArrayList<String> paths = new ArrayList();
        paths.add("C://Games/savegames/save1.dat");
        paths.add("C://Games/savegames/save2.dat");
        paths.add("C://Games/savegames/save3.dat");
        zipFiles("C://Games/savegames/savefiles.zip", paths);

        openZip("C://Games/savegames/savefiles.zip", "C://Games/savegames/");

        System.out.println(openProgress("C://Games/savegames/packed_save2.dat"));

        try (FileWriter writer = new FileWriter("C://Games/temp/temp.txt", true)) {
            writer.write(log.toString());
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void makeDir(String path) {
        File dir = new File(path);
        if (dir.mkdir()) {
            System.out.println("Каталог создан");
            log.append("Каталог " + path + " создан " + date + "\n");
        }
    }

    public static void makeFile(String path) {
        File myFile = new File(path);
        try {
            if (myFile.createNewFile())
                System.out.println("Файл был создан");
            log.append("Файл " + path + " создан " + date + "\n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void saveGame(String path, GameProgress progress) {
        try (FileOutputStream fos = new FileOutputStream(path);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(progress);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(String zipPath, List<String> paths) throws IOException {
        ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath));
        for (int i = 0; i < paths.size(); i++) {
            try (FileInputStream fis = new FileInputStream(paths.get(i))) {
                ZipEntry entry = new ZipEntry("packed_save" + (i + 1) + ".dat");
                zout.putNextEntry(entry);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                zout.write(buffer);
                zout.closeEntry();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        zout.close();
        for (int j = 0; j < paths.size(); j++) {
            File myFile = new File(paths.get(j));
            if (myFile.delete())
                System.out.println("Файл сохранения удален");
            log.append("Файл сохранения" + paths.get(j) + " удален " + date + "\n");
        }
    }

    public static void openZip(String path, String unzipPath) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(path))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(unzipPath + name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static GameProgress openProgress (String path) {
        GameProgress gameProgress = null;
        try (FileInputStream  fis = new FileInputStream(path);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }
}
