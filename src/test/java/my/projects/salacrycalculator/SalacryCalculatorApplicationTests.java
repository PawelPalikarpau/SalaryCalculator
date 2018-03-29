package my.projects.salacrycalculator;

import my.projects.salacrycalculator.domains.Currency;
import my.projects.salacrycalculator.domains.DataToCount;
import my.projects.salacrycalculator.services.FileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SalacryCalculatorApplicationTests {

	private FileService fileService;
	private FileService fileServiceMock = mock(FileService.class);

	@Before
	public void setUp() {
		fileService = new FileService();
	}

	@Test
	public void countSalaryTest() {
		String firstResult = fileService.countSalary(getDataToCountForTest("20", "100", "Germany"));
		String secondResult = fileService.countSalary(getDataToCountForTest("19", "50", "Great Britain"));
		String thirdResult = fileService.countSalary(getDataToCountForTest("25", "180", "Poland"));

		assertEquals(firstResult, "7736.67");
		assertEquals(secondResult, "4436.97");
		assertEquals(thirdResult, "3168.00");
	}

	@Test
	public void fileNotFoundExceptionTest() {
		try {
			Set<Currency> set = fileService.getDataFromFile("catch exception");
			fail("Expected FileNotFoundException to be bound");
		} catch (FileNotFoundException e) {
			assertEquals(e.getMessage(), "File not found");
		}
	}

	@Test
	public void mockFileServiceTest() throws FileNotFoundException {
		when(fileServiceMock.getDataFromFile("currency file")).thenReturn(getSetForTest());
		assertEquals(1, fileServiceMock.getDataFromFile("currency file").size());
		assertEquals(
				getCurrencyForTest("Poland", "PLN", "1"),
				fileServiceMock.getDataFromFile("currency file").iterator().next()
		);
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

	private<T> Set<T> getSetForTest() {
		Set<T> returnSet = new HashSet<>();
		((HashSet<Currency>) returnSet).add(getCurrencyForTest("Poland", "PLN", "1"));
		return returnSet;
	}
}
