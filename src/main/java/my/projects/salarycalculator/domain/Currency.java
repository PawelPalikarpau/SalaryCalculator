package my.projects.salarycalculator.domain;

public class Currency {

    private String country;
    private String currencyName;
    private String exchangeRate;

    public Currency() {
    }

    public Currency(String country, String currencyName, String exchangeRate) {
        this.country = country;
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        if (country != null ? !country.equals(currency.country) : currency.country != null) return false;
        if (currencyName != null ? !currencyName.equals(currency.currencyName) : currency.currencyName != null)
            return false;
        return exchangeRate != null ? exchangeRate.equals(currency.exchangeRate) : currency.exchangeRate == null;
    }

    @Override
    public int hashCode() {
        int result = country != null ? country.hashCode() : 0;
        result = 31 * result + (currencyName != null ? currencyName.hashCode() : 0);
        result = 31 * result + (exchangeRate != null ? exchangeRate.hashCode() : 0);
        return result;
    }

    public String convertToString() {
        StringBuilder builder = new StringBuilder();
        builder.append(country).append(",")
                .append(currencyName).append(",")
                .append(exchangeRate).append("\r\n");
        return builder.toString();
    }

    public static Currency convertFromString(String str) {
        String[] fields = str.split(",");
        return new Currency(fields[0], fields[1], fields[2]);
    }
}
