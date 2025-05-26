export interface Statistics {
    customerCount: number;
    transactionCount: number;
    totalBankCapital: number;
    transactionTypeChart: {
        labels: string[];
        datasets: {
            label: string;
            data: number[];
            backgroundColor: string[];
        }[];
    };
}
