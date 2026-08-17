import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { CalculateComponent } from './calculate.component';

describe('CalculateComponent', () => {
  let component: CalculateComponent;
  let fixture: ComponentFixture<CalculateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CalculateComponent,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CalculateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
