package my.projects.salacrycalculator.services;

import javassist.bytecode.stackmap.TypeData;
import my.projects.salacrycalculator.domains.Currency;
import my.projects.salacrycalculator.domains.DataToCount;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class FileService {

    private final Path currencyPath = Paths.get("src/main/resources/currencyFile");
    private final Path vatPath = Paths.get("src/main/resources/vatFile");

    private final static Logger LOG = Logger.getLogger(TypeData.ClassName.class.getName());

    public String countSalary(DataToCount data) {
        String output = "";

        try {
            BigDecimal result = new BigDecimal(data.getValue());
            result = result.multiply(new BigDecimal(22));

            BigDecimal exchangeValue = new BigDecimal(data.getCurrency().getExchangeRate()).setScale(2, RoundingMode.HALF_UP);
            result = result.multiply(exchangeValue).setScale(2, RoundingMode.HALF_UP);

            BigDecimal vat = new BigDecimal(data.getVat()).setScale(0, RoundingMode.HALF_UP);
            vat = vat.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            vat = vat.add(new BigDecimal(1));

            result = result.divide(vat, 2, RoundingMode.HALF_UP);
            output = result.toString();
        } catch (ArithmeticException ae) {
            LOG.log(Level.SEVERE, "Problem with counting net value", ae);
        }

        return output;
    }

    public<T> void addNewItem(T newItem, String fileName) throws FileNotFoundException {
        Set<T> fromFileItems;
        byte[] bytesToWrite;
        Path path;

        if (newItem instanceof Currency) {
            path = currencyPath;
            fromFileItems =  getDataFromFile(fileName);
            bytesToWrite = ((Currency) newItem).convertToString().getBytes();
        } else {
            path = vatPath;
            fromFileItems =  getDataFromFile(fileName);
            bytesToWrite = (newItem.toString() + "\r\n").getBytes();
        }

        int sizeBefore = fromFileItems.size();
        fromFileItems.add(newItem);
        int sizeAfter = fromFileItems.size();

        if (sizeBefore < sizeAfter) {
            try {
                Files.write(path, bytesToWrite, StandardOpenOption.APPEND);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with appending to " + fileName, e);
            }
        }
    }

    public <T> Set<T> getDataFromFile(String fileName) throws FileNotFoundException {
        boolean isCurrency = fileName.equalsIgnoreCase("currency file");

        String path;
        if (fileName.equalsIgnoreCase("currency file")) path = currencyPath.toString();
        else if (fileName.equalsIgnoreCase("vat file")) path = vatPath.toString();
        else path = "";

        File file = new File(path);
        Set<T> fromFileItems = null;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            fromFileItems = new HashSet<>();

            if (file.length() != 0) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        line = line.replaceAll("\r\n", "");
                        if (isCurrency) {
                            ((HashSet<Currency>) fromFileItems).add(Currency.convertFromString(line));
                        } else {
                            ((HashSet<BigDecimal>) fromFileItems).add(new BigDecimal(line).setScale(0, RoundingMode.HALF_UP));
                        }
                    } catch (ArrayIndexOutOfBoundsException ioobe) {
                        LOG.log(Level.SEVERE, "Illegal index of array in: " + line, ioobe);
                    }
                }
            } else {
                LOG.log(Level.SEVERE, fileName + " is empty !!!");
            }
        } catch (FileNotFoundException fnfe) {
            LOG.log(Level.SEVERE, fileName + " does not exists", fnfe);
            throw new FileNotFoundException("File not found");
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "Problem with reading from " + fileName, ioe);
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

    public void createAndFillFiles() {
        Thread currencyThread = new Thread(new DefaultFileThread(currencyPath, "currency file"));
        Thread vatThread = new Thread(new DefaultFileThread(vatPath, "vat file"));

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
}
