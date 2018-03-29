package my.projects.salacrycalculator.services;

import javassist.bytecode.stackmap.TypeData;
import my.projects.salacrycalculator.domains.Currency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultFileThread implements Runnable {

    private final Path path;
    private final String fileName;
    private final static Logger LOG = Logger.getLogger(TypeData.ClassName.class.getName());
    private List<?> defaultItems;
    private final boolean isCurrency;

    public DefaultFileThread(Path path, String fileName) {
        this.path = path;
        this.fileName = fileName;

        if (fileName.equalsIgnoreCase("currency file")) {
            defaultItems = getCurrencyArray();
            isCurrency = true;
        } else {
            defaultItems = getVatArray();
            isCurrency = false;
        }
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

        if (file.length() == 0) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                final FileWriter finalWriter = writer;
                defaultItems.parallelStream()
                        .forEach((item) -> {
                                    try {
                                        finalWriter.append((isCurrency) ? ((Currency) item).convertToString() : item.toString() + "\r\n");
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

    private List<Currency> getCurrencyArray() {
        List<Currency> defaultItems = new ArrayList<>();
        defaultItems.add(new Currency("Great Britain", "GBP", "4.7993"));
        defaultItems.add(new Currency("Germany", "EUR", "4.2164"));
        defaultItems.add(new Currency("Poland", "PLN", "1"));
        return defaultItems;
    }

    private List<BigDecimal> getVatArray() {
        List<BigDecimal> defaultItems = new ArrayList<>();
        defaultItems.add(new BigDecimal(25).setScale(0, RoundingMode.HALF_UP));
        defaultItems.add(new BigDecimal(20).setScale(0, RoundingMode.HALF_UP));
        defaultItems.add(new BigDecimal(19).setScale(0, RoundingMode.HALF_UP));
        return defaultItems;
    }
}
