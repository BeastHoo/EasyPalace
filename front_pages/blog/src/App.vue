<template>
  <div id="app" >
<!--    <plane/>-->
    <div id="el-menu-demo">
      <div class="text typing" v-if="!isShow" data-text="EasyPalace">EasyPalace</div>
    <el-menu
      :default-active="activeIndex"
      style="border: none;z-index: 1"
      mode="horizontal"
      @select="handleSelect"
      text-color="white"
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
    </div>
    <el-collapse-transition>
      <div id="planeWrapper" v-show="isShow">
        <plane ></plane>
      </div>
    </el-collapse-transition>

    <transition :name="transitionName">
      <router-view style="margin-top: 4.5em"></router-view>
    </transition>
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
import Plane from "./components/plane";

//鼠标点击出现爱心特效
(function(window,document,undefined){
  var hearts = [];
  window.requestAnimationFrame = (function(){
    return window.requestAnimationFrame ||
      window.webkitRequestAnimationFrame ||
      window.mozRequestAnimationFrame ||
      window.oRequestAnimationFrame ||
      window.msRequestAnimationFrame ||
      function (callback){
        setTimeout(callback,1000/60);
      }
  })();
  init();
  function init(){
    css(".heart{width: 10px;height: 10px;position: fixed;background: #f00;transform: rotate(45deg);-webkit-transform: rotate(45deg);-moz-transform: rotate(45deg);}.heart:after,.heart:before{content: '';width: inherit;height: inherit;background: inherit;border-radius: 50%;-webkit-border-radius: 50%;-moz-border-radius: 50%;position: absolute;}.heart:after{top: -5px;}.heart:before{left: -5px;}");
    attachEvent();
    gameloop();
  }
  function gameloop(){
    for(var i=0;i<hearts.length;i++){
      if(hearts[i].alpha <=0){
        document.body.removeChild(hearts[i].el);
        hearts.splice(i,1);
        continue;
      }
      hearts[i].y--;
      hearts[i].scale += 0.004;
      hearts[i].alpha -= 0.013;
      hearts[i].el.style.cssText = "left:"+hearts[i].x+"px;top:"+hearts[i].y+"px;opacity:"+hearts[i].alpha+";transform:scale("+hearts[i].scale+","+hearts[i].scale+") rotate(45deg);background:"+hearts[i].color;
    }
    requestAnimationFrame(gameloop);
  }
  function attachEvent(){
    var old = typeof window.onclick==="function" && window.onclick;
    window.onclick = function(event){
      old && old();
      createHeart(event);
    }
  }
  function createHeart(event){
    var d = document.createElement("div");
    d.className = "heart";
    hearts.push({
      el : d,
      x : event.clientX - 5,
      y : event.clientY - 5,
      scale : 1,
      alpha : 1,
      color : randomColor()
    });
    document.body.appendChild(d);
  }
  function css(css){
    var style = document.createElement("style");
    style.type="text/css";
    try{
      style.appendChild(document.createTextNode(css));
    }catch(ex){
      style.styleSheet.cssText = css;
    }
    document.getElementsByTagName('head')[0].appendChild(style);
  }
  function randomColor(){
    return "rgb("+(~~(Math.random()*255))+","+(~~(Math.random()*255))+","+(~~(Math.random()*255))+")";
  }
})(window,document);

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
    });
  },
  watch:{
    '$store.state.avatar'(){
      this.isLogged = this.$store.state.isLogged
      if (this.isLogged === true)
      {
        this.imageUrl=this.$store.state.avatar
      }
    },
    $route(to, from) {
      // 如果to索引大于from索引,判断为前进状态,反之则为后退状态
      //   document.getElementById("").style.marginTop=
      if(from.path==="/blog")
      {
        this.isShow=false;
        document.getElementById("el-menu-demo").style.background="linear-gradient(120deg, #3498db, #8e44ad, #3498db)";
      }

    }
  },
  mounted:function () {
    window.addEventListener("scroll", this.scrolling);
    this.targetTop = document.querySelector('#el-menu-demo').offsetTop
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
      imageUrl:"",
      transitionName:"slide-forward",
      targetTop:1000,
      isShow:true,
      timer:null
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
      },scrolling() {
      // 获取当前滚动条向下滚动的距离
      let scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
      if (scrollTop>0 )
      {
        document.getElementById("el-menu-demo").style.background="linear-gradient(120deg, #02a0ff,red)"
        this.isShow=false
      }else if (scrollTop===0){
        document.getElementById("planeWrapper").style.height="16vh"
        let path = this.$route.path;
        if (path==="/blog")
        {
          this.isShow=true
          document.getElementById("el-menu-demo").style.background="none"
          // document.getElementById("text-wrapper").style.marginTop="14vh"
        }else {
          document.getElementById("el-menu-demo").style.background="linear-gradient(120deg, #3498db, #8e44ad, #3498db)"
          // document.getElementById("text-wrapper").style.marginTop="4vh"
        }
      }
    }
  },components: {
    Plane,
    "app_login":Login,
  }
}
</script>

<style>
@import url(https://fonts.googleapis.com/css?family=Lato);

#app {
  overflow: hidden;
  /*display: flex;*/
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#el-menu-demo{
  overflow: no-content;
  position: fixed;
  top: 0;
  width:100%;
  z-index: 999;
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

#text-wrapper {
  display: flex;
  margin-top: 14vh;
  height: 12vh;
  justify-content: center;
  align-items: center;
  text-align: center;
  background: linear-gradient(120deg, #3498db, #8e44ad, #3498db);
}

.typing {
  width: 6em;
  height: 1.2em;
  border-right: 1px solid transparent;
  animation: typing 2.5s steps(10, end), blink-caret .75s step-end infinite;
  font-family: Consolas, Monaco;
  word-break: break-all;
  overflow: hidden;
}

/* 打印效果 */
@keyframes typing {
  from {
    width: 0;
  }

  to {
    width: 6em;
  }
}

/* 光标 */
@keyframes blink-caret {

  from,
  to {
    border-color: transparent;
  }

  50% {
    border-color: currentColor;
  }
}



.text{
  display: inline-block;
  font-size: 3.2em;
  font-weight: 600;
  padding: 0 4px;
  color: white;
  position: fixed;
  left: 23em;
  top: -0.4%;
}
.text::before{
  /* 获取文本 */
  content: attr(data-text);
  position: absolute;
  /* 向左侧移2px */
  left: -2px;
  width: 100%;
  /* 将背景色设为与主背景同样的颜色，用于遮挡我们的标签元素 */
  background: none;
  /* 给before伪元素的文本添加左侧2px大小的红色文字阴影 */
  text-shadow:2px 0 red;
  /* 应用蒙版垂直变化动画，并一直循环 */
  animation: animation-before 3s infinite linear alternate-reverse;
}
.text::after{
  /* 获取文本 */
  content: attr(data-text);
  position: absolute;
  /* 向左侧移2px */
  left: 2px;
  width: 100%;
  /* 将背景色设为与主背景同样的颜色，用于遮挡我们的标签元素 */
  background: none;
  /* 给before伪元素的文本添加右侧2px大小的蓝色文字阴影 */
  text-shadow: -2px 0 blue;
  /* 应用蒙版垂直变化动画，并一直循环 */
  animation: animation-after 3s infinite linear alternate-reverse;
}

@keyframes animation-before{
  0% {
    clip-path: inset(0 0 0 0);
  }

  5% {
    clip-path: inset(.8em 0 .4em 0);
  }

  10% {
    clip-path: inset(.4em 0 .8em 0);
  }

  15% {
    clip-path: inset(.1em 0 1em 0);
  }

  20% {
    clip-path: inset(.3em 0 .6em 0);
  }

  25% {
    clip-path: inset(.6em 0 .3em 0);
  }

  30% {
    clip-path: inset(.8em 0 .5em 0);
  }

  35% {
    clip-path: inset(1em 0 .1em 0);
  }

  40% {
    clip-path: inset(.7em 0 .35em 0);
  }

  45% {
    clip-path: inset(.5em 0 .2em 0);
  }

  50% {
    clip-path: inset(.2em 0 .5em 0);
  }

  55% {
    clip-path: inset(.35em 0 .7em 0);
  }

  60% {
    clip-path: inset(.1em 0 .9em 0);
  }

  65% {
    clip-path: inset(.8em 0 .46em 0);
  }

  70% {
    clip-path: inset(.66em 0 .33em 0);
  }

  75% {
    clip-path: inset(.48em 0 .23em 0);
  }

  80% {
    clip-path: inset(.23em 0 .48em 0);
  }

  85% {
    clip-path: inset(.39em 0 .79em 0);
  }

  90% {
    clip-path: inset(.33em 0 .66em 0);
  }

  95% {
    clip-path: inset(1em 0 .3em 0);
  }

  100% {
    clip-path: inset(.62em 0 .29em 0);
  }
}
@keyframes animation-after{
  0% {
    clip-path: inset(0 0 0 0);
  }

  5% {
    clip-path: inset(.4em 0 .8em 0);
  }

  10% {
    clip-path: inset(.8em 0 .4em 0);
  }

  15% {
    clip-path: inset(1em 0 .1em 0);
  }

  20% {
    clip-path: inset(.6em 0 .3em 0);
  }

  25% {
    clip-path: inset(.3em 0 .6em 0);
  }

  30% {
    clip-path: inset(.5em 0 .8em 0);
  }

  35% {
    clip-path: inset(.1em 0 1em 0);
  }

  40% {
    clip-path: inset(.35em 0 .7em 0);
  }

  45% {
    clip-path: inset(.2em 0 .5em 0);
  }

  50% {
    clip-path: inset(.5em 0 .2em 0);
  }

  55% {
    clip-path: inset(.7em 0 .35em 0);
  }

  60% {
    clip-path: inset(.9em 0 .1em 0);
  }

  65% {
    clip-path: inset(.46em 0 .8em 0);
  }

  70% {
    clip-path: inset(.3em 0 .66em 0);
  }

  75% {
    clip-path: inset(.23em 0 .48em 0);
  }

  80% {
    clip-path: inset(.48em 0 .23em 0);
  }

  85% {
    clip-path: inset(.79em 0 .39em 0);
  }

  90% {
    clip-path: inset(.66em 0 .33em 0);
  }

  95% {
    clip-path: inset(.3em 0 1em 0);
  }

  100% {
    clip-path: inset(.29em 0 .62em 0);
  }
}

.slide-forward-enter {
  transform: translate(100%);
}
.slide-forward-enter-active {
  transition: all .5s ease-in-out;
}
.slide-forward-leave-active {
  transform: translate(-100%);
  transition: all .5s ease-in-out;
}
.slide-back-enter {
  transform: translate(-100%);
}
.slide-back-enter-active {
  transition: all .5s ease-in-out;
}
.slide-back-leave-active {
  transform: translate(100%);
  transition: all .5s ease-in-out;
}

#planeWrapper{
  width: 100%;
}
</style>
