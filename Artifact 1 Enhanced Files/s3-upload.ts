import { Injectable } from '@angular/core';
import { HttpClient, HttpEventType } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class S3UploadService {
  private apiBaseUrl = 'http://localhost:3000/api';

  constructor(private http: HttpClient) {}

  uploadFile(
    file: File,
    progressCallback?: (progress: number) => void
  ): Promise<any> {
    const formData = new FormData();
    formData.append('image', file);

    return new Promise((resolve, reject) => {
      this.http
        .post(`${this.apiBaseUrl}/upload`, formData, {
          reportProgress: true,
          observe: 'events',
        })
        .pipe(
          catchError((error) => {
            reject(error);
            return throwError(() => error);
          })
        )
        .subscribe((event) => {
          if (
            event.type === HttpEventType.UploadProgress &&
            progressCallback &&
            event.total
          ) {
            const progress = Math.round((100 * event.loaded) / event.total);
            progressCallback(progress);
          } else if (event.type === HttpEventType.Response) {
            resolve(event.body);
          }
        });
    });
  }
}
