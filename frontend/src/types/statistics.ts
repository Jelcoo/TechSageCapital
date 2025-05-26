import type { ChartData } from 'chart.js';

export interface Statistics {
    customerCount: number;
    transactionCount: number;
    totalBankCapital: number;
    transactionTypeChart: ChartData<'pie'>;
    transactionTodayChart: ChartData<'line'>;
}
