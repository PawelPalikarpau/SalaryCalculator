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

    private BufferedReader reader = null;

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

    public void addCurrency(Currency newCurrency) {
        Set<Currency> currencies = getCurrenciesFromFile();

        int sizeBefore = currencies.size();
        currencies.add(newCurrency);
        int sizeAfter = currencies.size();

        if (sizeBefore < sizeAfter) {
            try {
                Files.write(currencyPath, newCurrency.convertToString().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with appending to currency file", e);
            }
        }
    }

    public void addVat(BigDecimal newVat) {
        Set<BigDecimal> vats = getVatsFromFile();

        int sizeBefore = vats.size();
        vats.add(newVat);
        int sizeAfter = vats.size();

        if (sizeBefore < sizeAfter) {
            try {
                Files.write(vatPath, ("," + newVat.toString()).getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Problem with appending to vat file", e);
            }
        }
    }

    public Set<Currency> getCurrenciesFromFile() {
        File file = new File(currencyPath.toString());
        Set<Currency> currencies = null;

        if (file.length() != 0) {
            try {
                reader = new BufferedReader(new FileReader(file));
                currencies = new HashSet<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        currencies.add(Currency.convertFromString(line.replaceAll("\r\n", "")));
                    } catch (ArrayIndexOutOfBoundsException ioobe) {
                        LOG.log(Level.SEVERE, "Illegal index of array in: " + line, ioobe);
                    }
                    currencies.remove(new Currency("country", "currencyName", "exchangeRate"));
                }
            } catch (FileNotFoundException fnfe) {
                LOG.log(Level.SEVERE, "Currency File does not exists", fnfe);
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "Problem with reading from currency file", ioe);
            } finally {
                closeReader(reader, "currency file");
            }
        } else {
            LOG.log(Level.SEVERE, "Currency File is empty !!!");
        }

        return currencies;
    }

    public Set<BigDecimal> getVatsFromFile() {
        File file = new File(vatPath.toString());
        Set<BigDecimal> vats = null;

        if (file.length() != 0) {
            try {
                reader = new BufferedReader(new FileReader(file));
                vats = new HashSet<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] vatsArray = line.split(",");
                        for (String vat : vatsArray) {
                            vats.add(new BigDecimal(vat).setScale(0, RoundingMode.HALF_UP));
                        }
                    } catch (ArrayIndexOutOfBoundsException ioobe) {
                        LOG.log(Level.SEVERE, "Illegal index of array in: " + line, ioobe);
                    }
                }
            } catch (FileNotFoundException fnfe) {
                LOG.log(Level.SEVERE, "Vat File does not exists", fnfe);
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "Problem with reading from vat file", ioe);
            } finally {
                closeReader(reader, "vat file");
            }
        } else {
            LOG.log(Level.SEVERE, "Vat File is empty !!!");
        }

        return vats;
    }

    public void createAndFillFiles() {
        Thread currencyThread = new Thread(new CurrencyThread(currencyPath));
        Thread vatThread = new Thread(new VatThread(vatPath));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(currencyThread);
        executor.submit(vatThread);

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ie) {
            LOG.log(Level.SEVERE, "Problem to shut down ExecutorService", ie);
        }
    }

    private void closeReader(BufferedReader reader, String type) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "Problem with closing FileReader for " + type, ioe);
            }
        }
    }
}
