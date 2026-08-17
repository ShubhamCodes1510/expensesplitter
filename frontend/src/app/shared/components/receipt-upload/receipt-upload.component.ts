import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-receipt-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './receipt-upload.component.html',
  styleUrls: ['./receipt-upload.component.css'],
})
export class ReceiptUploadComponent {
  @Output() uploadComplete = new EventEmitter<any>();

  onFileSelected(event?: any): void {
    const file = event?.target?.files[0];
    if (file) {
      this.uploadComplete.emit(file);
    }
  }
}
