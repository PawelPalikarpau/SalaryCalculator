package my.projects.salacrycalculator.services;

import javassist.bytecode.stackmap.TypeData;
import my.projects.salacrycalculator.domains.Currency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CurrencyThread implements Runnable {

    private final Path currencyPath;
    private final static Logger LOG = Logger.getLogger(TypeData.ClassName.class.getName());

    public CurrencyThread(Path currencyPath) {
        this.currencyPath = currencyPath;
    }

    @Override
    public void run() {
        File currencyFile = new File(currencyPath.toString());

        if (!currencyFile.exists() && !currencyFile.isDirectory()) {
            try {
                Files.createFile(currencyPath);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with creating a currency file", e);
            }
        }

        if (currencyFile.length() == 0) {
            List<Currency> currencies = new ArrayList<>();
            currencies.add(new Currency("Great Britain", "GBP", "4.7993"));
            currencies.add(new Currency("Germany", "EUR", "4.2164"));
            currencies.add(new Currency("Poland", "PLN", "1"));

            FileWriter writer = null;
            try {
                writer = new FileWriter(currencyFile);
                final FileWriter finalWriter = writer;
                finalWriter.append(new Currency("country", "currencyName", "exchangeRate").convertToString());
                currencies.parallelStream()
                        .forEach((item) -> {
                                    try {
                                        finalWriter.append(item.convertToString());
                                    } catch (IOException e) {
                                        LOG.log(Level.SEVERE, "Problem with writing to a currency file", e);
                                    }
                                }
                        );
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with creating FileWriter to currency file", e);
            } finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException ioe) {
                        LOG.log(Level.SEVERE, "Problem with closing FileWriter for currency file", ioe);
                    }
                }
            }
        }
    }
}
