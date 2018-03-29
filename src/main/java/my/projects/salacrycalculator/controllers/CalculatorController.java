package my.projects.salacrycalculator.controllers;

import my.projects.salacrycalculator.domains.Currency;
import my.projects.salacrycalculator.domains.DataToCount;
import my.projects.salacrycalculator.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@RestController
public class CalculatorController {

    private final FileService fileService;

    @Autowired
    public CalculatorController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = "/createFiles", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public void createFiles() {
        fileService.createAndFillFiles();
    }

    @RequestMapping(value = "/getCurrencies", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public Set<Currency> getCurrency() throws FileNotFoundException {
        return fileService.getDataFromFile("currency file");
    }

    @RequestMapping(value = "/getVats", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public Set<BigDecimal> getVats() throws FileNotFoundException {
        return fileService.getDataFromFile("vat file");
    }

    @RequestMapping(value = "/addCurrency", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public void addCurrency(@RequestBody Currency currency) throws FileNotFoundException {
        fileService.addNewItem(currency, "currency file");
    }

    @RequestMapping(value = "/addVat", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public void addVat(@RequestBody String vat) throws FileNotFoundException {
        BigDecimal vatValue = new BigDecimal(vat).setScale(0, RoundingMode.HALF_UP);
        fileService.addNewItem(vatValue, "vat file");
    }

    @RequestMapping(value = "/countSalary", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public String addVat(@RequestBody DataToCount dataToCount) {
        return fileService.countSalary(dataToCount);
    }
}
