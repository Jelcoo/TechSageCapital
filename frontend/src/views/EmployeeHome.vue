<template>
    <main>
        <div class="container py-5">
            <h1>Employee Dashboard</h1>
            <p> {{ currentTime }}</p>
            <p>Welcome, {{ userStore.firstName }} {{ userStore.lastName }}</p>
            <p>Let's make that money</p>
            <div class="employee-art mb-4 row w-100">
                <div class="col">
                    <img src="/images/TechSageCapital-employee-incentive.png" alt="Employee Dashboard" />
                    <p class="caption">Wizard Jelcius</p>
                </div>
            </div>
            <RouterLink to="/employee/customers-overview" class="btn btn-primary">Customer Overview</RouterLink>
        </div>
        <div class="container">
            <h2>Statistics</h2>
            <div v-if="statistics">
                <div class="row">
                    <div class="col-sm-4 mb-3 mb-sm-0">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Total Customers</h5>
                                <p class="card-text fw-bold h2">{{ statistics.customerCount }}</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-4">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Total Transactions</h5>
                                <p class="card-text fw-bold h2">{{ statistics.transactionCount }}</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-4">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Total Captial</h5>
                                <p class="card-text fw-bold h2">{{ formatMoney(statistics.totalBankCapital) }}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row mt-4">
                    <div class="col-sm-6">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Transaction value per type</h5>
                                <Pie :data="statistics.transactionTypeChart" :options="pieConfig" />
                            </div>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Transactions per hour</h5>
                                <Line :data="statistics.transactionTodayChart" :options="lineConfig" />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div v-else>
                <p>Loading statistics...</p>
            </div>
        </div>
    </main>
</template>

<script setup lang="ts">
import axiosClient from '@/axios';
import { type Statistics } from '@/types';
import { formatMoney } from '@/utils';
import { onMounted, ref } from 'vue';
import { RouterLink } from 'vue-router';
import { Line, Pie } from 'vue-chartjs'
import { Chart as ChartJS, ArcElement, Tooltip, Legend, type ChartOptions, Colors, CategoryScale, LinearScale, PointElement, LineElement } from 'chart.js'
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
const currentTime = ref(new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }));

ChartJS.register(ArcElement, Tooltip, Legend, Colors, CategoryScale, LinearScale, PointElement, LineElement);

const statistics = ref<Statistics | null>(null);

const pieConfig: ChartOptions<"pie"> = {
    responsive: true,
    plugins: {
        legend: {
            position: 'bottom',
        },
        tooltip: {
            callbacks: {
                label: (context) => {
                    const label = context.label || '';
                    const value = context.raw as number;
                    return `${label}: ${formatMoney(value)}`;
                }
            }
        }
    }
};

const lineConfig: ChartOptions<"line"> = {
    responsive: true,
    plugins: {
        legend: {
            position: 'bottom',
        },
        tooltip: {
            callbacks: {
                label: (context) => {
                    const label = context.dataset.label || '';
                    const value = context.raw as number;
                    const axisId = context.dataset.yAxisID;

                    if (axisId === 'y') {
                        return `${label}: ${formatMoney(value)}`;
                    } else {
                        return `${label}: ${value}`;
                    }
                }
            }
        },
    },
    scales: {
        y: {
            type: 'linear',
            display: true,
            position: 'left',
            title: {
                display: true,
                text: 'Amount (€)'
            }
        },
        y1: {
            type: 'linear',
            display: true,
            position: 'right',
            grid: {
                drawOnChartArea: false,
            },
            title: {
                display: true,
                text: 'Count'
            }
        },
    }
};


onMounted(() => {
    axiosClient.get<Statistics>('/statistics')
        .then(response => {
            statistics.value = response.data;
        });
});
</script>
