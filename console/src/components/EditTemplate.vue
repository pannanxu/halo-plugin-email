<script lang="ts" setup>
import {reactive, ref, watch} from "vue";
import {EmailTemplateExtension, TemplateOption} from "@/types";
import apiClient from "@/utils/api-client";

const props = defineProps<{ selected: string }>();

let loading = ref<boolean>(false);
let data = reactive<EmailTemplateExtension>({
  spec: {
    template: "classpath:template/comment.html",
    enable: true
  },
  apiVersion: "io.mvvm.halo.plugins.email/v1alpha1",
  kind: "EmailTemplateExtension",
  metadata: {
    name: "comment",
    labels: {
      "plugin.halo.run/plugin-name": "halo-plugin-email"
    },
    version: 56,
    creationTimestamp: "2022-10-25T08:31:07.942124Z"
  }
});

const handleSubmit = async () => {
  loading.value = true;
  console.log("handleSubmit...", data)
  // TODO save EmailTemplateExtension
  loading.value = false;
}

const handleFetchTemplateExtension = async (name: string) => {
  // TODO load EmailTemplateExtension by selected name
  console.log("handleFetchTemplateExtension...", name)
  const resp = await apiClient.get<EmailTemplateExtension>(`/apis/io.mvvm.halo.plugins.email/v1alpha1/emailTemplateExtensions/${name}`)
  console.log('data: ', data)
  data = resp.data || {}
}

watch(props, async (newName, oldName) => {
  data.metadata.name = newName.selected;
  await handleFetchTemplateExtension(newName.selected);
})

</script>

<template>
  <FormKit
    id="email-config-template"
    name="email-config-template"
    :actions="false"
    type="form"
    @submit="handleSubmit"
  >
    <FormKit
      name="enable"
      type="checkbox"
      label="开启"
      validation="required"
      value="false"
      v-model="data.spec.enable"
    ></FormKit>
    <FormKit
      name="template"
      help="模板内容(HTML)"
      label="模板内容"
      type="textarea"
      v-model="data.spec.template"
    ></FormKit>

    <VButton
      block
      type="secondary"
      :loading="loading"
      @click="$formkit.submit('email-config-template')"
    >
      保存
    </VButton>
  </FormKit>
</template>

<style scoped>

</style>
