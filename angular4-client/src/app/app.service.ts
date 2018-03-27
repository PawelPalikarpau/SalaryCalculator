import {Injectable} from '@angular/core';
import {Http} from "@angular/http";
import 'rxjs/add/operator/map';
import {Currency, DataToCount} from "./app.component";

@Injectable()
export class AppService {

  private baseUrl = 'http://localhost:8080';

  constructor(private http: Http) {}

  createFiles(): Promise<any> {
    return this.http.get(this.baseUrl + '/createFiles')
      .toPromise()
      .then(response => {
        return response as any;
      })
      .catch(this.handleError)
  }

  getCurrencies(): Promise<Currency[]> {
    return this.http.get(this.baseUrl + '/getCurrencies')
      .toPromise()
      .then(response => {
        return response.json() as Currency[]
      })
      .catch(this.handleError)
  }

  getVats(): Promise<number[]> {
    return this.http.get(this.baseUrl + '/getVats')
      .toPromise()
      .then(response => {
        return response.json() as number[]
      })
      .catch(this.handleError)
  }

  addCurrency(currency): Promise<any> {
    return this.http.post(this.baseUrl + '/addCurrency', currency)
      .toPromise()
      .then(response => {
        return response as any;
      })
      .catch(this.handleError);
  }

  addVat(vat): Promise<any> {
    return this.http.post(this.baseUrl + '/addVat', vat.toString())
      .toPromise()
      .then(response => {
        return response as any;
      })
      .catch(this.handleError);
  }

  countSalary(value, currency, vat): Promise<string> {
    let dataToCount: DataToCount = {
      value: value,
      currency: currency,
      vat: vat
    };

    return this.http.post(this.baseUrl + '/countSalary', dataToCount)
      .toPromise()
      .then(response => {
        return response.json() as string;
      })
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('Some error occured', error);
    return Promise.reject(error.message || error);
  }
}
