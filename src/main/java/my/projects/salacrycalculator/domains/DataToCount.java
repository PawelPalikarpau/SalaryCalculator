package my.projects.salacrycalculator.domains;

public class DataToCount {
    private String value;
    private Currency currency;
    private String vat;

    public DataToCount() {
    }

    public DataToCount(String value, Currency currency, String vat) {
        this.value = value;
        this.currency = currency;
        this.vat = vat;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getVat() {
        return vat;
    }

    public void setVat(String vat) {
        this.vat = vat;
    }
}
