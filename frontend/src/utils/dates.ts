export function formatDate(date: Date | string): string {
    if (typeof date === 'string') {
        date = new Date(date);
    }
    return new Intl.DateTimeFormat('nl-NL', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
    }).format(date);
}
