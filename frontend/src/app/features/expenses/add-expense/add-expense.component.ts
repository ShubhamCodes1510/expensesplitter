import { Component, OnInit, HostListener, ChangeDetectorRef, ChangeDetectionStrategy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormControl } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ExpenseService } from '../../../core/services/expense.service';
import { UserService } from '../../../core/services/user.service';
import { CategoryService } from '../../../core/services/category.service';
import { UploadService } from '../../../core/services/upload.service';
import { NotificationService } from '../../../core/services/notification.service';
import { User } from '../../../core/models/user.model';
import { Category } from '../../../core/models/category.model';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule, DateAdapter, MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatRadioModule } from '@angular/material/radio';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { Expense } from '../../../core/models/expense.model';

export const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'dd/MM/yyyy',
  },
  display: {
    dateInput: 'dd/MM/yyyy',
    monthYearLabel: 'MMM yyyy',
    dateA11yLabel: 'dd/MM/yyyy',
    monthYearA11yLabel: 'MMMM yyyy',
  },
};

@Component({
  selector: 'app-add-expense',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCheckboxModule,
    MatIconModule,
    MatTooltipModule,
    MatRadioModule,
    MatSlideToggleModule,
    MatDividerModule,
    MatButtonToggleModule,
  ],
  templateUrl: './add-expense.component.html',
  styleUrls: ['./add-expense.component.scss'],
  providers: [
    { provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS },
  ],
})
export class AddExpenseComponent implements OnInit {
  expenseForm: FormGroup;
  loading = false;
  submitted = false;
  users: User[] = [];
  categories: Category[] = [];
  selectedUsers: string[] = [];
  customAmounts: { [userId: string]: string } = {};
  splitType: 'EQUAL' | 'CUSTOM' = 'EQUAL';
  Math = Math;
  receiptFiles: File[] = [];
  receiptUrls: string[] = [];
  uploadingReceipt = false;
  showUploadOptions = false;
  filePreviewUrls: { [key: number]: string } = {};
  showUserSelector = false;
  focusedUserId: string | null = null;
  suggestedCategories: Category[] = [];
  showCategorySuggestions = false;
  scanningReceipt = false;
  ocrResults: any = null;
  showOcrResults = false;

  frequencies = [
    { value: 'DAILY', label: 'Daily' },
    { value: 'WEEKLY', label: 'Weekly' },
    { value: 'MONTHLY', label: 'Monthly' },
    { value: 'YEARLY', label: 'Yearly' },
  ];

  // Keyword mapping for category suggestions
  private categoryKeywords: { [key: string]: string[] } = {
    'Food': ['food', 'restaurant', 'dinner', 'lunch', 'breakfast', 'meal', 'cafe', 'coffee', 'snack', 'groceries', 'pizza', 'burger'],
    'Transport': ['transport', 'taxi', 'uber', 'bus', 'train', 'metro', 'fuel', 'gas', 'parking', 'ticket', 'flight', 'travel'],
    'Shopping': ['shopping', 'clothes', 'shoes', 'electronics', 'gadget', 'store', 'mall', 'amazon', 'online'],
    'Entertainment': ['entertainment', 'movie', 'cinema', 'concert', 'game', 'netflix', 'spotify', 'music', 'theater'],
    'Utilities': ['utilities', 'electricity', 'water', 'internet', 'phone', 'bill', 'rent', 'mortgage', 'maintenance'],
    'Healthcare': ['healthcare', 'doctor', 'medicine', 'pharmacy', 'hospital', 'medical', 'insurance'],
    'Education': ['education', 'book', 'course', 'tuition', 'school', 'college', 'training'],
    'Travel': ['travel', 'hotel', 'vacation', 'holiday', 'flight', 'trip', 'tour'],
    'Gifts': ['gift', 'present', 'donation', 'charity', 'birthday', 'anniversary'],
    'Other': ['other', 'miscellaneous', 'uncategorized']
  };

  constructor(
    private fb: FormBuilder,
    private expenseService: ExpenseService,
    private userService: UserService,
    private categoryService: CategoryService,
    private uploadService: UploadService,
    private notificationService: NotificationService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {
    this.expenseForm = this.fb.group({
      description: ['', [Validators.required, Validators.minLength(3)]],
      amount: ['', [Validators.required, Validators.min(0.01)]],
      category: [''],
      paidBy: ['', Validators.required],
      date: [new Date(), Validators.required],
      comments: [''],
      isRecurring: [false],
      frequency: ['MONTHLY'],
    });
  }

  ngOnInit(): void {
    this.loadUsers();
    this.loadCategories();
  }

  loadUsers(): void {
    this.userService.getAll().subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        console.error('Error loading users:', error);
      },
    });
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      },
    });
  }

  getUserName(userId: string): string {
    const user = this.users.find(u => u.id?.toString() === userId);
    return user?.name || 'Unknown';
  }

  getInitial(name: string | undefined): string {
    if (!name) return '?';
    return name.charAt(0).toUpperCase();
  }

  get f() {
    return this.expenseForm.controls;
  }

  isUserSelected(userId: string): boolean {
    return this.selectedUsers.includes(userId);
  }

  toggleUser(userId: string): void {
    if (this.isUserSelected(userId)) {
      this.onUserSelection(userId, false);
    } else {
      this.onUserSelection(userId, true);
    }
  }

  toggleUserSelector(): void {
    this.showUserSelector = !this.showUserSelector;
  }

  onUserSelection(userId: string, selected: boolean): void {
    if (selected) {
      if (!this.selectedUsers.includes(userId)) {
        this.selectedUsers.push(userId);
        if (this.splitType === 'CUSTOM') {
          const equalShare = this.getEqualShare();
          this.customAmounts[userId] = equalShare > 0 ? equalShare.toFixed(2) : '';
        }
      }
    } else {
      this.selectedUsers = this.selectedUsers.filter((id) => id !== userId);
      delete this.customAmounts[userId];
    }
    this.cdr.detectChanges();
  }

  getEqualShare(): number {
    if (this.selectedUsers.length === 0) return 0;
    const amount = parseFloat(this.expenseForm.get('amount')?.value) || 0;
    return amount / this.selectedUsers.length;
  }

  calculateShareAmount(userId: string): number {
    if (this.splitType === 'CUSTOM') {
      return parseFloat(this.customAmounts[userId] || '0') || 0;
    }
    return this.getEqualShare();
  }

  getTotalCustomAmount(): number {
    const total = this.selectedUsers.reduce((sum, userId) => {
      return sum + (parseFloat(this.customAmounts[userId] || '0') || 0);
    }, 0);
    return Math.round(total * 100) / 100;
  }

  getRemainingAmount(): number {
    const total = parseFloat(this.expenseForm.get('amount')?.value) || 0;
    return Math.round((total - this.getTotalCustomAmount()) * 100) / 100;
  }

  onCustomAmountChange(userId: string, value: string): void {
    const total = parseFloat(this.expenseForm.get('amount')?.value) || 0;
    
    this.customAmounts[userId] = value ? value : '0';
    
    const customTotal = Math.round(this.getTotalCustomAmount() * 100) / 100;
    const remaining = Math.round((total - customTotal) * 100) / 100;
    
    const usersWithoutThis = this.selectedUsers.filter(uid => uid !== userId);
    
    if (remaining >= 0 && usersWithoutThis.length > 0) {
      const perUser = Math.round((remaining / usersWithoutThis.length) * 100) / 100;
      usersWithoutThis.forEach(uid => {
        this.customAmounts[uid] = perUser.toString();
      });
    }
    
    this.cdr.detectChanges();
  }


  autoDistribute(): void {
    const total = parseFloat(this.expenseForm.get('amount')?.value) || 0;
    if (this.selectedUsers.length === 0) return;
    
    // Calculate equal share with proper rounding
    const sharePrecision = 2; // 2 decimal places for currency
    const baseShare = total / this.selectedUsers.length;
    const roundedShare = Math.floor(baseShare * Math.pow(10, sharePrecision)) / Math.pow(10, sharePrecision);
    
    // Calculate the difference due to rounding
    const totalRounded = roundedShare * (this.selectedUsers.length - 1);
    const lastShare = Math.round((total - totalRounded) * Math.pow(10, sharePrecision)) / Math.pow(10, sharePrecision);
    
    // Distribute amounts
    this.selectedUsers.forEach((userId, index) => {
      if (index === this.selectedUsers.length - 1) {
        this.customAmounts[userId] = lastShare.toFixed(sharePrecision);
      } else {
        this.customAmounts[userId] = roundedShare.toFixed(sharePrecision);
      }
    });
    
    this.cdr.detectChanges();
    
    // Show notification
    const shareAmount = this.selectedUsers.length > 1 ? roundedShare.toFixed(2) : total.toFixed(2);
    this.notificationService.showSuccess(`Set equal shares of ₹${shareAmount} for each person`);
  }

  onInputFocus(userId: string): void {
    this.focusedUserId = userId;
  }

  onInputBlur(userId: string): void {
    this.focusedUserId = null;
    const total = parseFloat(this.expenseForm.get('amount')?.value) || 0;
    const customTotal = Math.round(this.getTotalCustomAmount() * 100) / 100;
    const remaining = Math.round((total - customTotal) * 100) / 100;
    
    if (Math.abs(remaining) < 0.01) {
      return;
    }
    
    const usersWithoutThis = this.selectedUsers.filter(uid => uid !== userId);
    if (usersWithoutThis.length > 0) {
      const perUser = Math.round((remaining / usersWithoutThis.length) * 100) / 100;
      usersWithoutThis.forEach(uid => {
        this.customAmounts[uid] = Math.max(0, perUser).toString();
      });
    }
    
    this.cdr.detectChanges();
  }

  distributeRemaining(): void {
    const total = parseFloat(this.expenseForm.get('amount')?.value) || 0;
    const customTotal = Math.round(this.getTotalCustomAmount() * 100) / 100;
    const remaining = Math.round((total - customTotal) * 100) / 100;
    
    if (Math.abs(remaining) < 0.01) {
      return;
    }
    
    // Strategy 1: If some users have zero amount, distribute only to them
    const usersWithZeroAmount = this.selectedUsers.filter(userId => {
      const amount = parseFloat(this.customAmounts[userId] || '0') || 0;
      return amount === 0;
    });
    
    if (usersWithZeroAmount.length > 0 && remaining > 0) {
      const perUser = Math.round((remaining / usersWithZeroAmount.length) * 100) / 100;
      usersWithZeroAmount.forEach(userId => {
        this.customAmounts[userId] = perUser.toString();
      });
      this.cdr.detectChanges();
      return;
    }
    
    // Strategy 2: Distribute proportionally based on existing amounts
    const existingAmounts: { [userId: string]: number } = {};
    let totalExisting = 0;
    
    this.selectedUsers.forEach(userId => {
      const amount = parseFloat(this.customAmounts[userId] || '0') || 0;
      existingAmounts[userId] = amount;
      totalExisting += amount;
    });
    
    if (totalExisting > 0 && remaining > 0) {
      // Distribute remaining proportionally to existing amounts
      this.selectedUsers.forEach(userId => {
        const existingShare = existingAmounts[userId];
        const proportion = existingShare / totalExisting;
        const additional = Math.round(remaining * proportion * 100) / 100;
        this.customAmounts[userId] = (existingShare + additional).toString();
      });
      this.cdr.detectChanges();
    } else if (remaining > 0) {
      // Strategy 3: All users have zero or equal distribution
      const perUser = Math.round((remaining / this.selectedUsers.length) * 100) / 100;
      this.selectedUsers.forEach(userId => {
        this.customAmounts[userId] = perUser.toString();
      });
      this.cdr.detectChanges();
    }
    
    // Show notification
    if (remaining > 0) {
      this.notificationService.showSuccess(`Distributed ₹${remaining.toFixed(2)} among ${this.selectedUsers.length} people`);
    }
  }

  getUsersWithCustomAmount(): number {
    return this.selectedUsers.filter(userId => {
      const amount = parseFloat(this.customAmounts[userId] || '0') || 0;
      return amount > 0;
    }).length;
  }

  onAmountChange(): void {
    this.cdr.detectChanges();
  }

  // Category suggestion methods
  onDescriptionChange(): void {
    const description = this.expenseForm.get('description')?.value || '';
    if (description.length > 2) {
      this.suggestCategories(description);
      this.showCategorySuggestions = true;
    } else {
      this.suggestedCategories = [];
      this.showCategorySuggestions = false;
    }
  }

  suggestCategories(description: string): void {
    const desc = description.toLowerCase();
    const matchedCategories: { category: Category, score: number }[] = [];

    // Check each category for keyword matches
    this.categories.forEach(category => {
      let score = 0;
      const categoryName = category.name?.toLowerCase() || '';
      
      // Check if category name is in the keyword mapping
      if (this.categoryKeywords[categoryName]) {
        this.categoryKeywords[categoryName].forEach(keyword => {
          if (desc.includes(keyword.toLowerCase())) {
            score += 3; // Higher score for exact keyword match
          }
        });
      }
      
      // Check if category name appears in description
      if (categoryName && desc.includes(categoryName)) {
        score += 2;
      }
      
      // Check for partial matches
      if (categoryName) {
        const words = desc.split(/\s+/);
        words.forEach(word => {
          if (word.length > 3 && categoryName.includes(word)) {
            score += 1;
          }
        });
      }
      
      if (score > 0) {
        matchedCategories.push({ category, score });
      }
    });
    
    // Sort by score descending and take top 3
    matchedCategories.sort((a, b) => b.score - a.score);
    this.suggestedCategories = matchedCategories.slice(0, 3).map(item => item.category);
    
    // If no matches found, show some default categories
    if (this.suggestedCategories.length === 0 && this.categories.length > 0) {
      this.suggestedCategories = this.categories.slice(0, 3);
    }
  }

  selectSuggestedCategory(category: Category): void {
    this.expenseForm.patchValue({ category: category.id });
    this.showCategorySuggestions = false;
    this.notificationService.showSuccess(`Selected category: ${category.name}`);
  }

  toggleSplitType(type: 'EQUAL' | 'CUSTOM'): void {
    this.splitType = type;
    if (type === 'EQUAL') {
      this.customAmounts = {};
    } else {
      const equalShare = this.getEqualShare();
      this.selectedUsers.forEach(userId => {
        this.customAmounts[userId] = equalShare.toFixed(2);
      });
    }
    this.cdr.detectChanges();
  }

  isCustomAmountValid(): boolean {
    if (this.splitType !== 'CUSTOM') return true;
    if (this.selectedUsers.length === 0) return false;
    
    const total = parseFloat(this.expenseForm.get('amount')?.value) || 0;
    const customTotal = this.getTotalCustomAmount();
    return Math.abs(Math.round((customTotal - total) * 100) / 100) < 0.01;
  }

  getCustomAmountError(): string | null {
    if (this.splitType !== 'CUSTOM') return null;
    if (this.selectedUsers.length === 0) return 'Select at least one person';
    
    const total = parseFloat(this.expenseForm.get('amount')?.value) || 0;
    const customTotal = this.getTotalCustomAmount();
    const remaining = Math.round((total - customTotal) * 100) / 100;
    
    if (remaining > 0.01) {
      return `₹${remaining.toFixed(2)} remaining`;
    } else if (remaining < -0.01) {
      return `Over by ₹${Math.abs(remaining).toFixed(2)}`;
    }
    return null;
  }

  onFileSelected(event: any): void {
    const files = Array.from(event.target.files) as File[];
    if (files.length > 0) {
      files.forEach(file => this.processFile(file));
    }
    this.showUploadOptions = false;
  }

  onCameraCapture(): void {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = 'image/*';
    input.capture = 'environment';
    input.multiple = true;
    input.onchange = (e: any) => {
      const files = Array.from(e.target.files) as File[];
      if (files.length > 0) {
        files.forEach(file => this.processFile(file));
      }
    };
    input.click();
    this.showUploadOptions = false;
  }

  processFile(file: File): void {
    const fileIndex = this.receiptFiles.length;
    this.receiptFiles.push(file);
    
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.filePreviewUrls[fileIndex] = e.target?.result as string;
      };
      reader.readAsDataURL(file);
    }
    this.uploadReceipt(file, fileIndex);
  }

  // OCR Processing (Mock implementation)
  scanReceiptForOCR(file: File): void {
    if (!file.type.startsWith('image/')) {
      this.notificationService.showError('Only image files can be scanned for OCR');
      return;
    }

    this.scanningReceipt = true;
    this.notificationService.showInfo('Scanning receipt for text...');

    // Simulate OCR processing delay
    setTimeout(() => {
      this.scanningReceipt = false;
      
      // Mock OCR results based on file name/size
      const mockResults = {
        extractedText: `RECEIPT\nDate: ${new Date().toLocaleDateString()}\nMerchant: Sample Restaurant\nTotal: ₹${(Math.random() * 1000 + 100).toFixed(2)}\nItems: Food & Beverages\nTax: ₹${(Math.random() * 100).toFixed(2)}`,
        merchant: 'Sample Restaurant',
        totalAmount: (Math.random() * 1000 + 100).toFixed(2),
        date: new Date().toISOString().split('T')[0],
        category: 'Food',
        confidence: 0.85
      };

      this.ocrResults = mockResults;
      this.showOcrResults = true;
      
      // Auto-fill form with OCR results
      this.applyOcrResults(mockResults);
      
      this.notificationService.showSuccess('Receipt scanned successfully! Found amount and category.');
    }, 2000);
  }

  applyOcrResults(results: any): void {
    if (results.totalAmount) {
      this.expenseForm.patchValue({
        amount: parseFloat(results.totalAmount).toFixed(2),
        description: `Dinner at ${results.merchant}`,
        date: new Date(results.date)
      });
    }
    
    if (results.category && this.categories.length > 0) {
      const matchingCategory = this.categories.find(cat =>
        cat.name.toLowerCase().includes(results.category.toLowerCase())
      );
      if (matchingCategory) {
        this.expenseForm.patchValue({ category: matchingCategory.id });
      }
    }
    
    this.cdr.detectChanges();
  }

  useOcrResults(): void {
    if (this.ocrResults) {
      this.notificationService.showSuccess('Form filled with OCR data!');
      this.showOcrResults = false;
    }
  }

  dismissOcrResults(): void {
    this.showOcrResults = false;
    this.ocrResults = null;
  }

  uploadReceipt(file: File, index: number): void {
    this.uploadingReceipt = true;
    this.uploadService.uploadReceipt(file).subscribe({
      next: (response) => {
        this.uploadingReceipt = false;
        this.receiptUrls[index] = response.url;
        this.notificationService.showSuccess(`Receipt ${index + 1} uploaded successfully`);
      },
      error: (error) => {
        this.uploadingReceipt = false;
        console.error('Upload error:', error);
        this.notificationService.showError(`Failed to upload receipt ${index + 1}`);
      },
    });
  }

  removeReceipt(index: number): void {
    this.receiptFiles.splice(index, 1);
    this.receiptUrls.splice(index, 1);
    delete this.filePreviewUrls[index];
    // Reindex filePreviewUrls
    const newFilePreviewUrls: { [key: number]: string } = {};
    Object.entries(this.filePreviewUrls).forEach(([key, value]) => {
      const oldIndex = parseInt(key);
      if (oldIndex > index) {
        newFilePreviewUrls[oldIndex - 1] = value;
      } else if (oldIndex < index) {
        newFilePreviewUrls[oldIndex] = value;
      }
    });
    this.filePreviewUrls = newFilePreviewUrls;
  }

  toggleUploadOptions(): void {
    this.showUploadOptions = !this.showUploadOptions;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    const uploadContainer = document.querySelector('.whatsapp-upload-container');
    if (uploadContainer && !uploadContainer.contains(target) && this.showUploadOptions) {
      this.showUploadOptions = false;
    }
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.expenseForm.invalid || this.selectedUsers.length === 0) {
      this.notificationService.showError('Please fill in all required fields');
      return;
    }

    if (this.splitType === 'CUSTOM' && !this.isCustomAmountValid()) {
      this.notificationService.showError('Custom amounts must equal the total expense amount');
      return;
    }

    this.loading = true;
    const { description, amount, category, paidBy, date, comments, isRecurring, frequency } = this.expenseForm.value;

    const shares = this.selectedUsers.map((userId: string) => ({
      userId: parseInt(userId),
      shareAmount: this.splitType === 'CUSTOM'
        ? parseFloat(this.customAmounts[userId] || '0')
        : this.getEqualShare(),
      settled: false,
    }));

    // Create receipt objects from uploaded URLs
    const receipts = this.receiptUrls.map(url => ({ url }));
    
    const expense: any = {
      description,
      amount: parseFloat(amount),
      paidById: parseInt(paidBy),
      date: new Date(date).toISOString(),
      shares,
      splitType: this.splitType,
      receipts: receipts.length > 0 ? receipts : undefined,
      comments: comments || undefined,
      isRecurring: isRecurring || false,
      frequency: isRecurring ? frequency : undefined,
      category: category ? { id: parseInt(category) } : undefined,
    };

    this.expenseService.create(expense).subscribe({
      next: () => {
        this.loading = false;
        this.notificationService.showSuccess('Expense created successfully!');
        this.router.navigate(['/expenses']);
      },
      error: (error) => {
        this.loading = false;
        console.error('Error creating expense:', error);
        this.notificationService.showError(error?.error?.message || 'Failed to create expense');
      },
    });
  }
}
