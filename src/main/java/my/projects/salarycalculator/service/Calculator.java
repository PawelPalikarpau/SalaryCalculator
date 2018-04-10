package my.projects.salarycalculator.service;

import javassist.bytecode.stackmap.TypeData;
import my.projects.salarycalculator.domain.DataToCount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Calculator {

    private final static Logger LOG = Logger.getLogger(TypeData.ClassName.class.getName());

    public static String countSalary(DataToCount data) {
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
}
