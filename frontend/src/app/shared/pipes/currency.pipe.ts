import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'currency',
  standalone: true,
})
export class CurrencyPipe implements PipeTransform {
  transform(value: any, currencySymbol: string = '₹'): string {
    if (value === null || value === undefined || isNaN(value)) {
      return `${currencySymbol}0.00`;
    }
    const num = typeof value === 'number' ? value : parseFloat(value);
    if (isNaN(num)) {
      return `${currencySymbol}0.00`;
    }
    return `${currencySymbol}${num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  }
}
