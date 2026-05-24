import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'role',
})
export class RolePipe implements PipeTransform {
  transform(value: unknown, ...args: unknown[]): unknown {

    if(value === null || value === undefined) {
      return '';
    }
    
    value = (value as string).split('_')[1].toUpperCase();
    
    return value;
  }
}
