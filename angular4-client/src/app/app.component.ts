import {Component, OnInit} from '@angular/core';
import {AppService} from "./app.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  currencies: Currency[];
  vats: number[];

  selectedCurrency: Currency = new Currency;
  selectedVat: number;

  newCurrency: Currency = new Currency;
  newVatValue: string;

  valueGross: string;
  result: any = null;

  error: any = null;

  constructor(private appService: AppService) {}

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll() {
    this.appService.createFiles()
      .then(
        output => {
          this.appService.getCurrencies()
            .then (
              output => {
                this.currencies = output;
              }
            );
          this.appService.getVats()
            .then(
              output => {
                this.vats = output;
              }
            )
        }
      );
  }

  addCurrency() {
    this.error = null;
    if (this.newCurrency.country == null || this.newCurrency.country.length < 1 ||
      this.newCurrency.currencyName == null || this.newCurrency.currencyName.length < 1 ||
      this.newCurrency.exchangeRate == null || this.newCurrency.exchangeRate.length < 1
    ) {
      this.error = 'You must fill country, currency and exchange rate fields to add new Currency';
      return;
    }
    this.appService.addCurrency(this.newCurrency)
      .then (
        output => {
          this.appService.getCurrencies()
            .then(
              output => {
                this.currencies = output;
                this.newCurrency = new Currency();
              }
            )
        }
      );
  }

  addVat() {
    this.error = null;
    if (this.newVatValue == null || this.newVatValue.length < 1) {
      this.error = 'You must fill vat value to add new Vat';
      return;
    }
    let tempVat = Number(this.newVatValue);
    if (isNaN(tempVat) || (tempVat % 1 != 0)) {
      this.error = 'New vat value must be a whole number';
      return;
    }
    this.appService.addVat(tempVat)
      .then (
        outout => {
          this.appService.getVats()
            .then (
              output => {
                this.vats = output;
                this.newVatValue = null;
              }
            )
        }
      );
  }

  countSalary() {
    this.error = null;
    if (this.selectedCurrency == null || this.selectedVat == null) {
      this.error = 'You must choose currency and vat';
      return;
    }
    let value = Number(this.valueGross);
    if (isNaN(value) || (value < 0)) {
      this.error = 'Gross value must be a positive number';
      return;
    }
    this.appService.countSalary(this.valueGross, this.selectedCurrency, this.selectedVat)
      .then (
        output => {
          this.result = output;
        }
      )
  }
}

export class Currency {
  country: string;
  currencyName: string;
  exchangeRate: string;
}

export class DataToCount {
  value: string;
  currency: Currency;
  vat: string;
}
