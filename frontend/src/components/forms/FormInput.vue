<template>
    <div class="d-flex flex-column">
        <div class="d-flex flex-column">
            <div class="form-floating my-1">
                <Field :name :type="type ?? 'text'" class="form-control" :class="{
                    disabled: disabled,
                    'opacity-50': disabled,
                }" :placeholder="placeholder ?? label.toLowerCase()" :modelValue="value" :disabled="disabled"
                    :required="required" :min="min" :max="max" :step="step" />
                <label :for="name">{{ label }}</label>
            </div>
        </div>
        <ErrorMessage :name class="text-danger small mb-2" />
    </div>
</template>

<script setup lang="ts">
import { Field, ErrorMessage } from "vee-validate";

defineProps<{
    name: string;
    label: string;
    type?: string;
    placeholder?: string;
    value?: number;
    disabled?: boolean;
    min?: number;
    max?: number;
    step?: number;
    required?: boolean;
}>();
</script>
