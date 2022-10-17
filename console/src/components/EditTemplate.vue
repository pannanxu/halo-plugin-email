<script lang="ts" setup>
import {reactive, ref, watch} from "vue";
import {EmailTemplateExtension} from "@/types";

const props = defineProps<{ selected: string }>();

let loading = ref<boolean>(false);
let data = reactive<EmailTemplateExtension>({
  apiVersion: "io.mvvm.halo.plugins.email/v1alpha1",
  kind: "EmailTemplateExtension",
  metadata: {
    name: "comment",
    version: 32,
    labels: {
      "plugin.halo.run/plugin-name": "halo-plugin-email"
    },
    creationTimestamp: "2022-10-17T11:22:01.200921Z"
  },
  spec: {
    template: "\u003ch1 th:text\u003d\"${post.spec.title}\"\u003e\u003c/h1\u003e",
    enable: true
  }
});

const handleSubmit = async () => {
  loading.value = true;
  console.log("handleSubmit...", data)
  // TODO save EmailTemplateExtension
  loading.value = false;
}

const handleFetchTemplateExtension = (name: string) => {
  // TODO load EmailTemplateExtension by selected name
  console.log("handleFetchTemplateExtension...", name)
}

watch(props, async (newName, oldName) => {
  data.metadata.name = newName.selected;
  handleFetchTemplateExtension(newName.selected);
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
