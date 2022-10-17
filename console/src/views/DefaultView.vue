<script lang="ts" setup>
import {reactive, ref, onMounted} from "vue";
import apiClient from "@/utils/api-client";
import type {Option, TemplateOption} from "@/types";
import EditTemplate from "@/components/EditTemplate.vue";

let selected = ref<string>('');
let options = reactive<TemplateOption[]>([]);

const handleFetchConfig = async () => {
  const {data} = await apiClient.get<Array<TemplateOption>>('/api/api.plugin.halo.run/v1alpha1/plugins/halo-plugin-email/io.mvvm.halo.plugins.email/templateOptions')
  data.forEach(option => {
    options.push({...option, value: option.name})
  })
}

onMounted(() => {
  handleFetchConfig()
})

</script>
<template>
  <div class="bg-white p-4 sm:px-6">
    <div class="w-1/3">

      <FormKit
        v-model="selected"
        label="选择模板"
        name="visible"
        type="select"
      >
        <option v-for="option in options" :value="option.value">{{ option.label }}</option>
      </FormKit>

      <EditTemplate :selected="selected"></EditTemplate>
    </div>
  </div>
</template>
<style scoped>

</style>
