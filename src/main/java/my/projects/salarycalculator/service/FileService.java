package my.projects.salarycalculator.service;

import javassist.bytecode.stackmap.TypeData;
import my.projects.salarycalculator.util.DefaultFileCreatorThread;
import my.projects.salarycalculator.util.Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FileService {

    private Path path;
    private String fileName;

    private final static Logger LOG = Logger.getLogger(TypeData.ClassName.class.getName());
    private Support support;

    @Autowired
    public FileService(Support support) {
        this.support = support;
    }

    public<T> void addNewItem(T newItem) throws Exception {
        Set<T> fromFileItems = getDataFromFile();

        if (fromFileItems != null) {
            int sizeBefore = fromFileItems.size();
            fromFileItems.add(newItem);
            int sizeAfter = fromFileItems.size();

            if (sizeBefore < sizeAfter) {
                try {
                    byte[] bytesToWrite = support.convertItemToByteArray(newItem);
                    if (bytesToWrite != null)
                        Files.write(path, bytesToWrite, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Problem with appending to " + fileName, e);
                    throw new IOException(e);
                }
            }
        }
    }

    public <T> Set<T> getDataFromFile() throws Exception {
        synchronized (this) {
            fileName = support.getFileName(path);

            File file = new File(path.toString());
            Set<T> fromFileItems;

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                fromFileItems = new HashSet<>();

                if (file.length() != 0) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.replaceAll("\r\n", "");
                        support.addToItemSet(path, fromFileItems, line);
                    }
                } else {
                    LOG.log(Level.SEVERE, fileName + " is empty !!!");
                }
            } catch (FileNotFoundException fnfe) {
                LOG.log(Level.SEVERE, fileName + " does not exists", fnfe);
                throw new FileNotFoundException("File not found");
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "Problem with reading from " + fileName, ioe);
                throw new IOException(ioe);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe) {
                        LOG.log(Level.SEVERE, "Problem with closing FileReader for " + fileName, ioe);
                    }
                }
            }

            return fromFileItems;
        }
    }

    public void createAndFillDefaultFiles() {
        Thread currencyThread = new Thread(new DefaultFileCreatorThread(support.getCurrencyPath(), "currency file"));
        Thread vatThread = new Thread(new DefaultFileCreatorThread(support.getVatPath(), "vat file"));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(currencyThread);
        executor.submit(vatThread);

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException ie) {
            LOG.log(Level.SEVERE, "Problem to shut down executor", ie);
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
