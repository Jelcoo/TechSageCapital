export function formatMoney(value: number | string): string {
    if (typeof value === 'string') {
        value = parseFloat(value);
    }
    return new Intl.NumberFormat('nl-NL', {
        style: 'currency',
        currency: 'EUR',
    }).format(value);
}
