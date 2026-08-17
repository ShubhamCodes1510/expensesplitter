import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  private loadingCount = 0;
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  show(): void {
    this.loadingCount++;
    if (!this.loadingSubject.value) {
      this.loadingSubject.next(true);
    }
  }

  hide(): void {
    if (this.loadingCount > 0) {
      this.loadingCount--;
    }
    if (this.loadingCount === 0) {
      this.loadingSubject.next(false);
    }
  }

  setLoading(loading: boolean): void {
    if (loading) {
      this.show();
    } else {
      this.hide();
    }
  }

  get isLoading(): boolean {
    return this.loadingSubject.value;
  }
}
