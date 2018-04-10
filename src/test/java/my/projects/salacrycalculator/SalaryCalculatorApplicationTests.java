package my.projects.salacrycalculator;

import my.projects.salarycalculator.SalaryCalculatorApplication;
import my.projects.salarycalculator.domain.Currency;
import my.projects.salarycalculator.domain.DataToCount;
import my.projects.salarycalculator.service.FileService;
import my.projects.salarycalculator.service.Calculator;
import my.projects.salarycalculator.util.Support;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SalaryCalculatorApplication.class)
public class SalaryCalculatorApplicationTests {

	private FileService fileService;
	private FileService fileServiceSpy;
	private Support supportMock;
	private final static Path errorPath = Paths.get("src/main/resources/errorFile");

	@Before
	public void beforeTest() {
		supportMock = mock(Support.class);
		fileService = new FileService(supportMock);
		fileServiceSpy = spy(fileService);

		when(supportMock.getCurrencyPath()).thenCallRealMethod();
		when(supportMock.getVatPath()).thenCallRealMethod();
		fileService.createAndFillDefaultFiles();
	}

	@Test
	public void countSalaryTest() {
		String firstResult = Calculator.countSalary(getDataToCountForTest("20", "100", "Germany"));
		String secondResult = Calculator.countSalary(getDataToCountForTest("19", "50", "Great Britain"));
		String thirdResult = Calculator.countSalary(getDataToCountForTest("25", "180", "Poland"));

		assertEquals(firstResult, "7736.67");
		assertEquals(secondResult, "4436.97");
		assertEquals(thirdResult, "3168.00");
	}

	@Test(expected = NullPointerException.class)
	public void expectedException_WhenPathToFile_IsNull() throws Exception {
		fileService.getDataFromFile();
	}

	@Test(expected = FileNotFoundException.class)
	public void expectedException_WhenPathToFile_IsIncorrect() throws Exception {
		fileService.setPath(errorPath);
		when(supportMock.getFileName(fileService.getPath())).thenReturn("error file");
		fileService.getDataFromFile();
	}

	@Test(expected = IOException.class)
	public void expectedException_TryingAppendToFile_WhenPathToFile_IsIncorrect() throws Exception {
		fileServiceSpy.setPath(errorPath);
		doReturn(new HashSet<>()).when(fileServiceSpy).getDataFromFile();
		doCallRealMethod().when(supportMock).convertItemToByteArray(any());
		fileServiceSpy.addNewItem("888");
	}

	@Test
	public void convertItemToAppendTest() throws Exception {
		fileService.setPath(Paths.get("src/main/resources/vatFile"));
		when(supportMock.getFileName(any())).thenCallRealMethod();
		doCallRealMethod().when(supportMock).addToItemSet(any(), anySet(), anyString());

		Set beforeSet = fileService.getDataFromFile();

		when(supportMock.convertItemToAppend(new Object())).thenReturn(null);
		fileService.addNewItem("888");

		Set afterSet = fileService.getDataFromFile();
		assertEquals(beforeSet, afterSet);
	}

	private DataToCount getDataToCountForTest(String vat, String value, String country) {
		DataToCount dataToCount = new DataToCount();

		if (country.equalsIgnoreCase("Germany")) {
			dataToCount.setCurrency(getCurrencyForTest("Germany", "EUR", "4.2164"));
		} else if (country.equalsIgnoreCase("Great Britain")) {
			dataToCount.setCurrency(getCurrencyForTest("Great Britain", "GBP", "4.7993"));
		} else if (country.equalsIgnoreCase("Poland")) {
			dataToCount.setCurrency(getCurrencyForTest("Poland", "PLN", "1"));
		}

		dataToCount.setVat(vat);
		dataToCount.setValue(value);

		return dataToCount;
	}

	private Currency getCurrencyForTest(String country, String currency, String exchangeRate) {
		return new Currency(country, currency, exchangeRate);
	}
}
