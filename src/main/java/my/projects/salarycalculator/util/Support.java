package my.projects.salarycalculator.util;

import my.projects.salarycalculator.domain.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Component
public class Support {

    private final static Path currencyPath = Paths.get("src/main/resources/currencyFile");
    private final static Path vatPath = Paths.get("src/main/resources/vatFile");

    public List getDefaultArrayOfItems(String fileName) {
        if (fileName.equalsIgnoreCase("currency file")) {
            List<Currency> defaultItems = new LinkedList<>();
            defaultItems.add(new Currency("Great Britain", "GBP", "4.7993"));
            defaultItems.add(new Currency("Germany", "EUR", "4.2164"));
            defaultItems.add(new Currency("Poland", "PLN", "1"));
            return defaultItems;
        } else {
            List<BigDecimal> defaultItems = new LinkedList<>();
            defaultItems.add(new BigDecimal(25).setScale(0, RoundingMode.HALF_UP));
            defaultItems.add(new BigDecimal(20).setScale(0, RoundingMode.HALF_UP));
            defaultItems.add(new BigDecimal(19).setScale(0, RoundingMode.HALF_UP));
            return defaultItems;
        }
    }

    public Path getPathToFile(Object item) {
        if (item instanceof Currency) {
            return currencyPath;
        } else {
            return vatPath;
        }
    }

    public String convertItemToAppend(Object item) {
        if (item instanceof Currency) {
            return ((Currency) item).convertToString();
        } else {
            return item.toString() + "\r\n";
        }
    }

    public byte[] convertItemToByteArray(Object item) {
        if (item instanceof Currency) {
            return ((Currency) item).convertToString().getBytes();
        } else {
            return (item.toString() + "\r\n").getBytes();
        }
    }

    public String getFileName(Path path) {
        if (path.equals(currencyPath)) {
            return "currency file";
        } else if (path.equals(vatPath)) {
            return "vat file";
        }
        return null;
    }

    public<T> void addToItemSet(Path path, Set<T> fromFileItems, String line) {
        if (path.equals(currencyPath)) {
            ((HashSet<Currency>) fromFileItems).add(Currency.convertFromString(line));
        } else if (path.equals(vatPath)) {
            ((HashSet<BigDecimal>) fromFileItems).add(new BigDecimal(line).setScale(0, RoundingMode.HALF_UP));
        }
    }

    public Path getCurrencyPath() {
        return currencyPath;
    }

    public Path getVatPath() {
        return vatPath;
    }
}
