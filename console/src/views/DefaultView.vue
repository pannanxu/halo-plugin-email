<script lang="ts" setup>
import {reactive, ref} from "vue";

import {VButton} from "@halo-dev/components";

const loading = ref(false);
const data = reactive({
  isEnable: false,
  formName: "",
  host: "smtp.qq.com",
  port: 465,
  username: "",
  password: "",
  protocol: "smtps",
  startTls: true,
});

const handleSubmit = async () => {
  // TODO request api
  console.log("handleSubmit...", data)
}

</script>
<template>
  <div class="bg-white p-4 sm:px-6">
    <div class="w-1/3">
      <FormKit
        id="email-config-form"
        name="email-config-form"
        :actions="false"
        type="form"
        v-model="data"
        @submit="handleSubmit"
        @keyup.enter="$formkit.submit('email-config-form')"
      >
        <FormKit
          name="isEnable"
          type="checkbox"
          label="启用邮件通知"
          placeholder="启用邮件通知"
          validation="required"
          value="false"
        ></FormKit>
        <FormKit
          name="formName"
          type="text"
          label="formName"
          placeholder="formName"
          validation="required"
        ></FormKit>
        <FormKit
          name="host"
          type="text"
          label="host"
          placeholder="host"
          validation="required"
        ></FormKit>
        <FormKit
          name="port"
          type="text"
          label="port"
          placeholder="port"
          validation="required"
        ></FormKit>
        <FormKit
          name="username"
          type="text"
          label="username"
          placeholder="username"
          validation="required"
        ></FormKit>
        <FormKit
          name="password"
          type="text"
          label="password"
          placeholder="password"
          validation="required"
        ></FormKit>
        <FormKit
          name="protocol"
          type="text"
          label="protocol"
          placeholder="protocol"
          validation="required"
        ></FormKit>
        <FormKit
          name="startTls"
          type="checkbox"
          label="startTls"
          placeholder="startTls"
          validation="required"
        ></FormKit>
      </FormKit>
      <VButton
        block
        type="secondary"
        :loading="loading"
        @click="$formkit.submit('email-config-form')"
      >
        保存
      </VButton>
    </div>
  </div>
</template>
<style scoped>

</style>
