import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ExpenseService, ExpenseSummary } from '../../core/services/expense.service';
import { NotificationService } from '../../core/services/notification.service';
import { Expense } from '../../core/models/expense.model';
import { BalanceCardComponent } from '../../shared/components/balance-card/balance-card.component';
import { ExpenseCardComponent } from '../../shared/components/expense-card/expense-card.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartEvent, ChartType } from 'chart.js';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, BalanceCardComponent, ExpenseCardComponent, MatProgressSpinnerModule, MatCardModule, MatIconModule, NgChartsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  totalExpense = 0;
  youOwe = 0;
  youAreOwed = 0;
  recentExpenses: Expense[] = [];
  loading = true;
  dropdownOpen = false;

  categoryData: { category: string; amount: number; percentage: number; color: string }[] = [];

  // Chart configurations
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    scales: {
      x: {},
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => '₹' + value
        }
      }
    },
    plugins: {
      legend: {
        display: true,
      },
      tooltip: {
        callbacks: {
          label: (context) => `₹${context.parsed.y}`
        }
      }
    }
  };

  public barChartLabels: string[] = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'];
  public barChartType: ChartType = 'bar';
  public barChartLegend = true;

  public barChartData: ChartData<'bar'> = {
    labels: this.barChartLabels,
    datasets: [
      { data: [6500, 5900, 8000, 8100, 5600, 5500, 4000], label: 'Expenses', backgroundColor: '#36A2EB' },
      { data: [2800, 4800, 4000, 1900, 8600, 2700, 9000], label: 'Settlements', backgroundColor: '#FF6384' }
    ]
  };

  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        display: true,
        position: 'right',
      },
      tooltip: {
        callbacks: {
          label: (context) => {
            const label = context.label || '';
            const value = context.parsed as number;
            const data = context.dataset.data as number[];
            const total = data.reduce((a: number, b: number) => a + b, 0);
            const percentage = total > 0 ? Math.round((value / total) * 100) : 0;
            return `${label}: ₹${value} (${percentage}%)`;
          }
        }
      }
    }
  };

  public pieChartLabels: string[] = ['Food', 'Transport', 'Entertainment', 'Shopping', 'Bills'];
  public pieChartData: ChartData<'pie'> = {
    labels: this.pieChartLabels,
    datasets: [{
      data: [3000, 1500, 1000, 2500, 2000],
      backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF']
    }]
  };
  public pieChartType: ChartType = 'pie';

  public lineChartData: ChartConfiguration['data'] = {
    datasets: [
      {
        data: [65, 59, 80, 81, 56, 55, 40],
        label: 'Monthly Trend',
        backgroundColor: 'rgba(54, 162, 235, 0.2)',
        borderColor: 'rgba(54, 162, 235, 1)',
        pointBackgroundColor: 'rgba(54, 162, 235, 1)',
        pointBorderColor: '#fff',
        pointHoverBackgroundColor: '#fff',
        pointHoverBorderColor: 'rgba(54, 162, 235, 0.8)',
        fill: 'origin',
      }
    ],
    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July']
  };

  public lineChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    scales: {
      x: {},
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => '₹' + value
        }
      }
    }
  };

  public lineChartLegend = true;
  public lineChartType: ChartType = 'line';

  constructor(
    private expenseService: ExpenseService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.dropdown')) {
      this.dropdownOpen = false;
    }
  }

  loadDashboardData(): void {
    this.loading = true;

    this.expenseService.getSummary().subscribe({
      next: (summary: ExpenseSummary) => {
        this.totalExpense = summary.totalExpenses;
        this.youOwe = summary.amountOwed ?? (summary.totalOwed ?? 0);
        this.youAreOwed = summary.amountLent ?? (summary.totalSettled ?? 0);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading summary:', error);
        this.notificationService.showError('Failed to load summary data');
        this.loading = false;
      },
    });

    this.expenseService.getAll().subscribe({
      next: (expenses: Expense[]) => {
        this.recentExpenses = expenses.slice(0, 5);
        this.updateCategoryData(expenses);
      },
      error: (error) => {
        console.error('Error loading expenses:', error);
        this.notificationService.showError('Failed to load recent expenses');
      },
    });
  }

  updateCategoryData(expenses: Expense[]): void {
    const categoryMap = new Map<string, number>();
    const colors = ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'];
    
    expenses.forEach(expense => {
      const category = expense.category?.name || 'Uncategorized';
      const current = categoryMap.get(category) || 0;
      categoryMap.set(category, current + Number(expense.amount));
    });

    const total = Array.from(categoryMap.values()).reduce((a, b) => a + b, 0);
    
    this.categoryData = Array.from(categoryMap.entries()).map(([category, amount], index) => ({
      category,
      amount,
      percentage: total > 0 ? Math.round((amount / total) * 100) : 0,
      color: colors[index % colors.length]
    }));
  }

  getUniqueCategories(): number {
    const categories = new Set(this.recentExpenses.map(e => e.category?.name || 'Uncategorized'));
    return categories.size;
  }

  getAverageExpense(): number {
    if (this.recentExpenses.length === 0) return 0;
    const total = this.recentExpenses.reduce((sum, e) => sum + Number(e.amount), 0);
    return total / this.recentExpenses.length;
  }
}
