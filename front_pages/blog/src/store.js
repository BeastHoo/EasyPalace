import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate"
Vue.use(Vuex);
export default new Vuex.Store({
  state: {
    token: " ",
    isLogged: false,
    avatar:" ",
    username:" ",
    signature: " ",
    slogan: " ",
    keyId:" ",
    keySecret:" ",
    stsToken:" ",
    expire:" "
  },

  // 同步  第一个形参 代表state
  //name形参代表的是  你此时传递过来的参数
  mutations: {
    setToken: (state, token) => {
      state.token = token; //赋值
    },
    logout: (state)=> {
      state.token = " ";
      state.isLogged = false;
      state.avatar = " ";
      state.slogan = " ";
      state.signature = " ";
      state.username = " ";
      state.keyId = " ";
      state.keySecret =" ";
      state.expire = " "
    },
    setUserSig: (state, sign)=>{
      state.signature = sign;
    },
    setUserSlo: (state, slo)=>{
      state.slogan = slo;
    },
    setUserName: (state, uname) =>{
      state.username = uname;
      state.isLogged = true;
    },
    setUpdateStatus:(state)=>{
      state.isLogged = false;
    },
    setUserAva: (state, ava) =>{
      state.avatar = ava;
    },
    setKeyId:(state,keyId) =>{
      state.keyId = keyId;
    },
    setKeySecret:(state,secret) =>{
      state.keySecret = secret;
    },
    setStsToken:(state,stsToken)=>{
      state.stsToken = stsToken
    },
    setExpire:(state,expire)=>{
      state.expire=expire
    }
  },
  actions: {},
  modules: {},
});
