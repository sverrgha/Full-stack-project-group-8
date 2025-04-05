export async function validatePostalCode(postalCode) {
    if (!/^\d{4}$/.test(postalCode)) {
        return { valid: false };
    }

    try {
        const response = await fetch(`https://ws.geonorge.no/adresser/v1/sok?fuzzy=false&postnummer=${postalCode}&utkoordsys=4258&treffPerSide=1&side=0&asciiKompatibel=true`);

        if (!response.ok) {
            return { valid: false };
        }

        const data = await response.json();
        if (!data || !data.adresser || data.adresser.length === 0) {
            return { valid: false };
        }

        return {
            valid: true,
            city: data.adresser[0].poststed
        };
    } catch (error) {
        console.error('Error validating postal code:', error);
        return { valid: false };
    }
}