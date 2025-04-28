<template>
    <div class="d-flex flex-column">
        <div class="d-flex align-items-center">
            <Field :name :type="'checkbox'" v-slot="{ field }" :disabled :value="true" :unchecked-value="false">
                <div class="form-check">
                    <input v-bind="field" type="checkbox" class="form-check-input" :id="name" :disabled="disabled" />
                    <label :for="name" class="form-check-label" :class="{ 'disabled': disabled }">
                        {{ label }}
                    </label>
                </div>
            </Field>
        </div>
        <ErrorMessage :name class="text-danger small mb-2" />
    </div>
</template>

<script setup lang="ts">
import { Field, ErrorMessage } from 'vee-validate';

defineProps<{
    name: string;
    label: string;
    disabled?: boolean;
}>();
</script>
