export function formatIban(iban: string): string {
    if (!iban) return '';
    const formattedIban = iban.replace(/[^\dA-Z]/g, '').replace(/(.{4})/g, '$1 ').trim();
    return formattedIban;
}
