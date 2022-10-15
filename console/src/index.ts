import { definePlugin, BasicLayout } from "@halo-dev/console-shared";
import DefaultView from "./views/DefaultView.vue";
import { IconGrid } from "@halo-dev/components";
import "./styles/index.css";

export default definePlugin({
  name: "halo-plugin-email",
  components: [],
  routes: [
    {
      path: "/halo-plugin-email-config",
      component: BasicLayout,
      children: [
        {
          path: "",
          name: "Email",
          component: DefaultView,
          meta: {
            // permissions: ["plugin:halo-plugin-email:view"],
          },
        },
      ],
    },
  ],
  menus: [
    {
      name: "From halo-plugin-email",
      items: [
        {
          name: "Email",
          path: "/halo-plugin-email-config",
          icon: IconGrid,
        },
      ],
    },
  ],
  extensionPoints: {},
  activated() {},
  deactivated() {},
});
