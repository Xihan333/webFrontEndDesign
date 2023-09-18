// Utilities
import { PiniaPluginContext, defineStore } from "pinia";
import { userLoginReq } from "../services/userServ";
import { type MenuInfo, type UserInfo, SystemConfig } from "../models/general";
import { getMenuList } from "../services/mainServ";

const defaultNaviList: MenuInfo[] = [];

export const useAppStore = defineStore("app", {
  state: () => ({
    usernameSave: "",
    passwordSave: "",
    remember: false,
    userInfo: {
      loggedIn: false,
      username: "",
      perName: "",
      jwtToken: "",
      id: 0,
      roles: "",
      password: "",
    } as UserInfo,

    systemConfig: {
      naviList: defaultNaviList,
      showLeftMeun: false,
      leftList: defaultNaviList,
      id: null,
    } as SystemConfig,
  }),
  actions: {
    async login(username: string, password: string): Promise<void> {
      const res = await userLoginReq(username, password);
      this.userInfo = {
        loggedIn: true,
        username: res.username,
        perName: res.perName,
        jwtToken: res.accessToken,
        id: res.id,
        roles: res.roles,
        password: password,
      };
    },
    logout() {
      this.userInfo = {
        loggedIn: false,
        username: "",
        perName: "",
        jwtToken: "",
        roles: "",
        id: 0,
        password: "",
      };
      this.systemConfig.naviList = [];
    },
    async setNavi() {
      this.systemConfig.naviList = await getMenuList();
      this.systemConfig.leftList = this.systemConfig.naviList[0].sList;
      this.systemConfig.showLeftMeun = true;
      this.systemConfig.id = this.systemConfig.naviList[0].id;
    },
    saveAccount(username: string, password: string) {
      this.usernameSave = username;
      this.passwordSave = password;
      this.remember = true;
    },
    removeAccount() {
      this.usernameSave = "";
      this.passwordSave = "";
      this.remember = false;
    },
    //选中模块菜单，设置左侧菜单数据
    setLeftList(id: number) {
      let i: number;
      for (i = 0; i < this.systemConfig.naviList.length; i++) {
        if (this.systemConfig.naviList[i].id === id) {
          this.systemConfig.leftList = this.systemConfig.naviList[i].sList;
          this.systemConfig.id = id;
          break;
        }
      }
    },
  },
  persist: {
    storage: localStorage,
    // debug: true,
    // afterRestore: (context: PiniaPluginContext) => {
    //   console.log(context);
    // },
  },
});
