import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { BalancesComponent } from './balances.component';

describe('BalancesComponent', () => {
  let component: BalancesComponent;
  let fixture: ComponentFixture<BalancesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        BalancesComponent,
        HttpClientTestingModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BalancesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
