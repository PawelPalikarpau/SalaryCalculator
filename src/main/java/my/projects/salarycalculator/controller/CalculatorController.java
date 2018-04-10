package my.projects.salarycalculator.controller;

import my.projects.salarycalculator.domain.Currency;
import my.projects.salarycalculator.domain.DataToCount;
import my.projects.salarycalculator.service.Calculator;
import my.projects.salarycalculator.service.FileService;
import my.projects.salarycalculator.util.Support;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@RestController
public class CalculatorController {

    private final FileService fileService;
    private final Support support;

    @Autowired
    public CalculatorController(FileService fileService, Support support) {
        this.fileService = fileService;
        this.support = support;
    }

    @PostConstruct
    public void createFiles() {
        fileService.createAndFillDefaultFiles();
    }

    @RequestMapping(value = "/getCurrencies", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public Set<Currency> getCurrency() throws Exception {
        fileService.setPath(support.getPathToFile(new Currency()));
        return fileService.getDataFromFile();
    }

    @RequestMapping(value = "/getVats", method = RequestMethod.GET)
    @CrossOrigin(origins = "http://localhost:4200")
    public Set<BigDecimal> getVats() throws Exception {
        fileService.setPath(support.getPathToFile(""));
        return fileService.getDataFromFile();
    }

    @RequestMapping(value = "/addCurrency", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public void addCurrency(@RequestBody Currency currency) throws Exception {
        fileService.setPath(support.getPathToFile(currency));
        fileService.addNewItem(currency);
    }

    @RequestMapping(value = "/addVat", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public void addVat(@RequestBody String vat) throws Exception {
        fileService.setPath(support.getPathToFile(vat));
        BigDecimal vatValue = new BigDecimal(vat).setScale(0, RoundingMode.HALF_UP);
        fileService.addNewItem(vatValue);
    }

    @RequestMapping(value = "/countSalary", method = RequestMethod.POST)
    @CrossOrigin(origins = "http://localhost:4200")
    public String addVat(@RequestBody DataToCount dataToCount) {
        return Calculator.countSalary(dataToCount);
    }
}
