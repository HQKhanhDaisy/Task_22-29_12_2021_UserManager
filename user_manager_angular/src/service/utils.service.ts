import { Injectable } from '@angular/core';
import { HttpServiceService } from './http-service.service';

@Injectable({
  providedIn: 'root',
})
export class UtilsService {
  constructor(private http: HttpServiceService) {}

  public messageError(status: any): string {
    if (status === 400) {
      return 'Email: already existed';
    } else if (status === 411) {
      return 'User not found';
    } else if (status == 421) {
      return 'Username already existed';
    } else return '';
  }
}
