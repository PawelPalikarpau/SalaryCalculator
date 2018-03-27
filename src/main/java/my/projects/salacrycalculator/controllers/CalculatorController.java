package my.projects.salacrycalculator.controllers;

import my.projects.salacrycalculator.domains.Currency;
import my.projects.salacrycalculator.domains.DataToCount;
import my.projects.salacrycalculator.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Set<Currency> getCurrency() {
        return fileService.getCurrenciesFromFile();
    }

    @RequestMapping(value = "/getVats", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public Set<BigDecimal> getVats() {
        return fileService.getVatsFromFile();
    }

    @RequestMapping(value = "/addCurrency", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public void addCurrency(@RequestBody Currency currency) {
        fileService.addCurrency(currency);
    }

    @RequestMapping(value = "/addVat", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public void addVat(@RequestBody String vat) {
        fileService.addVat(new BigDecimal(vat).setScale(0, RoundingMode.HALF_UP));
    }

    @RequestMapping(value = "/countSalary", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public String addVat(@RequestBody DataToCount dataToCount) {
        return fileService.countSalary(dataToCount);
    }
}
