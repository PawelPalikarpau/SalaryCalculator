package my.projects.salarycalculator.util;

import javassist.bytecode.stackmap.TypeData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultFileCreatorThread implements Runnable {

    private final Path path;
    private final String fileName;
    private final static Logger LOG = Logger.getLogger(TypeData.ClassName.class.getName());
    private List<?> defaultItems;

    private Support support = new Support();

    public DefaultFileCreatorThread(Path path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        defaultItems = support.getDefaultArrayOfItems(fileName);
    }

    @Override
    public void run() {
        File file = new File(path.toString());

        if (!file.exists() && !file.isDirectory()) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with creating a " + fileName, e);
            }
        }

        if (file.exists() && file.length() == 0) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                final FileWriter finalWriter = writer;
                defaultItems.parallelStream()
                        .forEach((item) -> {
                                    try {
                                        finalWriter.append(support.convertItemToAppend(item));
                                    } catch (IOException e) {
                                        LOG.log(Level.SEVERE, "Problem with writing to a " + fileName, e);
                                    }
                                }
                        );
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with creating FileWriter for " + fileName, e);
            } finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException ioe) {
                        LOG.log(Level.SEVERE, "Problem with closing FileWriter for " + fileName, ioe);
                    }
                }
            }
        }
    }
}
