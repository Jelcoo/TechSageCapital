import type { GenericObject, SubmissionContext } from 'vee-validate';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function processFormError(error: any, values: GenericObject, actions: SubmissionContext) {
    if (error.response.data.message) {
        const fieldNames = Object.keys(values);
        const lastField = fieldNames[fieldNames.length - 1];
        actions.setErrors({ [lastField]: error.response.data.message });
    } else {
        actions.setErrors(error.response.data);
    }
}
