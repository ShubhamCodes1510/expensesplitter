import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ExpenseDetailComponent } from './expense-detail.component';

describe('ExpenseDetailComponent', () => {
  let component: ExpenseDetailComponent;
  let fixture: ComponentFixture<ExpenseDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ExpenseDetailComponent,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ExpenseDetailComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
