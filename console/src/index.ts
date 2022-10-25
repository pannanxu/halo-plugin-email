import {definePlugin} from "@halo-dev/console-shared";
import DefaultView from "./views/DefaultView.vue";
import "./styles/index.css";
import {markRaw} from "vue";
import {IconFolder} from "@halo-dev/components";

export default definePlugin({
  name: "halo-plugin-email",
  components: [],
  routes: [
    {
      parentName: "Root",
      route: {
        path: "/email",
        name: "Email",
        component: DefaultView,
        meta: {
          title: "邮件",
          searchable: true,
          permissions: ["plugin:halo-plugin-email:view"],
          menu: {
            name: "邮件",
            // group: "tool",
            icon: markRaw(IconFolder),
            priority: 0,
          },
        },
      },
    },
  ],
  extensionPoints: {},
});
