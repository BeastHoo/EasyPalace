<template>

  <div id="app" >

    <div class="line"></div>

    <el-menu
      :default-active="activeIndex"
      class="el-menu-demo"
      mode="horizontal"
      @select="handleSelect"
      background-color="#fff"
      text-color="black"
      width="100%"
      active-text-color="#ffd04b">
      <el-menu-item index="1">博客</el-menu-item>
      <el-submenu index="2">
        <template slot="title">OurSeelf</template>
        <el-menu-item index="2-1">公共空间</el-menu-item>
        <el-menu-item index="2-2">个人区域</el-menu-item>
      </el-submenu>
      <el-menu-item index="3">消息中心</el-menu-item>
      <el-menu-item index="4"><el-link href="#/info"  :underline="false">关于</el-link></el-menu-item>
      <el-submenu index="5" v-if="isLogged">
        <template slot="title"><img :src="imageUrl" style="border-radius: 100%;height: 2.5rem;width:2.5rem" ></template>
        <router-link to="/setting" style="text-decoration-line: none"><el-menu-item index="5-1">我的主页</el-menu-item></router-link>
        <el-menu-item index="5-2" style="color: red">注销账户</el-menu-item>
      </el-submenu>
      <el-menu-item index="6" v-if="isLogged === false"><el-link href="#/login" :underline="false">登录</el-link></el-menu-item>
      <el-menu-item index="7">
        <img src="./assets/search.png" style="height: 1.5rem;width: 1.5rem">
      </el-menu-item>

    </el-menu>
    <router-view/>

    <el-dialog
      title="提示"
      :visible.sync="centerDialogVisible"
      width="30%"
      center>
      <div>
        <el-input
          placeholder="搜索"
          suffix-icon="el-icon-search"
          v-model="inputValue">
        </el-input>
      </div>
<!--        <app_result v-if="1!==1" ref="child"></app_result>-->
        <span slot="footer" class="dialog-footer">
          <el-row>
            <el-col :span="12">
              <el-button type="success" @click="searchBlog">搜博客</el-button>
            </el-col>
            <el-col :span="12">
              <el-button type="primary" @click="searchFiles">搜文件</el-button>
            </el-col>
          </el-row>
        </span>

    </el-dialog>

  </div>


</template>

<script>
import Login from "./components/Login";
export default {
  name: 'App',
  created() {
    // 在页面加载时读取sessionStorage里的状态信息
    if (sessionStorage.getItem('store')) {
      this.$store.replaceState(
        Object.assign(
          {},
          this.$store.state,
          JSON.parse(sessionStorage.getItem('store'))
        )
      )
    }
    // 页面刷新时，将vuex里的信息保存到sessionStorage
    // 在页面刷新时先触发beforeunload事件
    window.addEventListener('beforeunload', () => {
      sessionStorage.setItem('store', JSON.stringify(this.$store.state))
    })
  },
  watch:{
    '$store.state.avatar'(){
      this.isLogged = this.$store.state.isLogged
      if (this.isLogged === true)
      {
        this.imageUrl=this.$store.state.avatar
      }
    }
  },
  mounted:function () {
    if (this.$store.state.token !== undefined && this.$store.state.token !== ' ')
    {
      this.isLogged = true;
      this.imageUrl=this.$store.state.avatar+"?timestamp="+Math.floor(Math.random() * (100))
      // this.$router.push("/blog")
    }
    if(this.activeIndex === '/')
    this.$router.push('/blog')
  },
  data() {
    return {
      activeIndex: this.$route.path,
      isLogged: false,
      centerDialogVisible: false,
      inputValue:"",
      imageUrl:""
      // resultList:[],
      // searchmode:false
    };
  },
  methods:
  {
    handleSelect(key, keyPath)
    {
      switch (key) {
        case '1':
          this.$router.push('/blog');
          break;
        case '2-1':
          this.$router.push('/ourseelfpub');
          break;
        case '2-2':
          this.$router.push("/ourseelfpri");
          break;
        case '3':
          this.$router.push("/msg");
          break;
        case '7':
          this.centerDialogVisible=!this.centerDialogVisible;
          break;
        case '5-2':
          this.$store.commit("logout");
          this.$router.push("/login")
          this.isLogged = false
          break;
      }
    },
    searchBlog(){
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      this.axios.post("https://healthkeeper.top:8080/search/blogbyname",this.$qs.stringify({key:this.inputValue})).then(res=>
      {
        this.centerDialogVisible=!this.centerDialogVisible;
        loading.close();
        var resultList=res.data
        this.$router.push(
          {
            path:"/result",
            query:{
              resultList:resultList,
              searchmode:false,
              keyword:this.inputValue
            }
          }
        );
      }).catch(e=>{
        this.centerDialogVisible=!this.centerDialogVisible;
        loading.close()
        this.$message.error("与服务器连接错误");
      })
    },
    searchFiles(){
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      let _mode;
      _mode=this.isLogged;  // 当未登录时，_mode为false，后台执行public的查询，登录后，_mode为true，后台执行private查询
      this.axios.post("https://healthkeeper.top:8080/search/files",this.$qs.stringify({key:this.inputValue,smode:_mode}),
        {headers:{'authorization':this.$store.state.token}}).then(res=>
      {
        this.centerDialogVisible=!this.centerDialogVisible;
        loading.close();
        const resultList = res.data;

        this.$router.push(
          {
            path:"/result",
            query:{
              resultList:res.data,
              searchmode:true,
              keyword:this.inputValue
            }
          }
        );
      }).catch(e=>{
        this.centerDialogVisible=!this.centerDialogVisible;
        this.$message.error("与服务器连接错误");
      })
      }
  },components: {
    "app_login":Login,
  }
}
</script>

<style>
#app {
  overflow: hidden;
  /*display: flex;*/
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

.el-menu-demo{
  overflow: no-content;
}

body *::-webkit-scrollbar {
  width: 5px;
  height: 10px;
}
body *::-webkit-scrollbar-track {
  background: #fff;
  border-radius: 2px;
}
body *::-webkit-scrollbar-thumb {
  background: rgb(205, 206, 206);
  border-radius: 10px;
}
body *::-webkit-scrollbar-thumb:hover {
  background: #333;
}
body *::-webkit-scrollbar-corner {
  background:  #fff;
}
.el-menu-demo {
  background: none;
}
.el-menu--horizontal > .el-submenu .el-submenu__title,
.el-menu--horizontal > .el-submenu.is-active .el-submenu__title,
.el-submenu__title i {
  color: #fff;
}
.el-menu--horizontal > .el-submenu.is-active .el-submenu__title {
  border-bottom: 2px solid #ff8923;
  color: #fff;
}
.el-menu-item:hover {
  outline: 0 !important;
  color: #ff8923 !important;
  background: none !important;
}
.el-menu-item.is-active{
  color: #ff8923 !important;
}

.el-submenu__title:focus,
.el-submenu__title:hover {
  outline: 0 !important;
  color: #ff8923 !important;
  background: none !important;
}
.el-menu--horizontal:focus,
.el-menu--horizontal:hover {
  outline: 0 !important;
  color: #fff !important;
  background: none !important;
}
.nav-menu-son .el-menu--horizontal:hover{
  background: pink!important;
}
.el-menu {
  background: none !important;
}
.el-menu-item a:hover {
  background: none;
}
.el-menu--popup-bottom-start {
  margin-top: -2px;
}
.el-menu--popup {
  background: none !important;
  padding: 0 0;
}
.el-menu--horizontal .el-menu {
  background: none;
}
.el-menu--horizontal .el-menu .el-menu-item,
.el-menu--horizontal .el-menu .el-submenu__title {
  background-color: rgba(255, 255, 255, 0.6);
  float: none;
  height: 36px;
  line-height: 36px;
  padding: 0 22px;
  color: #2d84ee!important;
  z-index: 100;
  font-weight: 500;
}

.el-menu--popup-bottom-start .el-menu-item:hover{
  background:  rgba(255, 137, 36, 1)!important;
  color: #fff!important;
}
</style>
