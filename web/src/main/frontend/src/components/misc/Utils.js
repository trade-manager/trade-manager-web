export const utils = {
    toISOStringWithTimezone
}

function toISOStringWithTimezone(dateOrStr, timeZone) {
    const date = new Date(dateOrStr);

    // Format parts according to the target timezone
    const formatter = new Intl.DateTimeFormat('en-US', {
        timeZone,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
    });

    // Convert the array of parts into a clean key-value object
    const parts = formatter.formatToParts(date).reduce((acc, part) => {
        acc[part.type] = part.value;
        return acc;
    }, {});

    // Handle case where hour12: false outputs '24' instead of '00'
    const hour = parts.hour === '24' ? '00' : parts.hour;

    // Re-assemble into ISO 8601 format: YYYY-MM-DDTHH:mm:ss
    return `${parts.year}-${parts.month}-${parts.day}T${hour}:${parts.minute}:${parts.second}`;
}