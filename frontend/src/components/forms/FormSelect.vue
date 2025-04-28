<template>
    <div class="my-2">
        <label :for="name" class="form-label text-light">{{ label }}</label>
        <Field :name="name" v-slot="{ field }">
            <select v-bind="field" class="form-select" :class="{ 'disabled': disabled }" :disabled="disabled">
                <option value="" disabled selected>
                    Select {{ label }}
                </option>
                <option v-for="option in options" :key="option.value" :value="option.value">
                    {{ option.label }}
                </option>
            </select>
        </Field>
        <ErrorMessage :name="name" class="text-danger small mt-1" />
    </div>
</template>

<script setup lang="ts">
import { Field, ErrorMessage } from 'vee-validate';

defineProps<{
    name: string;
    label: string;
    options: { value: string | number; label: string }[];
    disabled?: boolean;
}>();
</script>
