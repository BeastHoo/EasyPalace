<template>
  <div>


  <div id="box">

  <div id="box1">
    <img v-if="isLogged" :src="ava"  alt="">
    <img v-else src="../../assets/ico.jpg" alt="" @click="toLog">
  </div>
  <div id="box2" style="text-align: center">
    <h3 >{{uname}}</h3>
  </div>
  <div id="box3">
    <p>{{signature}}</p>
  </div>
  <div id="box4">
    <small>导航</small>
  </div>
  <div id="box5">
    <router-link to="/blog" style="text-decoration: none"><p>博客</p></router-link>
    <router-link to="/msg" style="text-decoration: none"><p>消息</p></router-link>
    <router-link to="/time" style="text-decoration: none"><p>日历</p></router-link>
    <router-link to="/ourseelfpri" style="text-decoration: none"><p>个人空间</p></router-link>

  </div>
  <div id="box6">
    <small>管理</small>
  </div>
  <div id="box7">
    <router-link to="/book" style="text-decoration: none"><p style="color: red">热门推荐</p></router-link>

    <router-link to="/setting" style="text-decoration: none"><p>用户信息</p></router-link>
    <router-link to="/manage" style="text-decoration: none"><p>博客管理</p></router-link>
    <router-link to="/news" style="text-decoration: none"><p>撰写博客</p></router-link>

  </div>
  </div>

  </div>
</template>

<script>
export default {
  name: "Aside",
  mounted: function (){
    this.refreshPage()
  },
  watch:{
    '$store.state.isLogged'(){
      this.refreshPage()
    }
  },
  data(){
    return{
      uname : "请先登录",
      signature:" ",
      ava:"",
      isLogged:false
    }
  },methods:{
    refreshPage()
    {
      if (this.$store.state.token === " " || this.$store.state.token === undefined)
      {
        this.uname = "请先登录"
        this.signature = "登录以展示签名"
        this.isLogged = false
      }
      else {
        this.uname = this.$store.state.username;
        this.ava = this.$store.state.avatar+"?timestamp="+Math.floor(Math.random() * (100))
        const sig = this.$store.state.signature
        if (sig === " " || sig === "")
          this.signature = "暂未设置签名,进入”用户信息“进行设置"
        else
          this.signature=sig
        this.isLogged = true
      }
    },
    toLog(){
      this.$router.push("/login")
    }
  }

}
</script>

<style scoped>
#box{
  position: absolute;
  border: 1px solid #EDEDED;
  top: 38px;
  left: 5%;
  width: 14em;
  height: 600px;
}
#box1{
  position: absolute;
  top: 4%;
  left: 30%;
  width: 100px;
  height: 100px;
  /*background-image: url("../../assets/cat.jpg");*/
  background-size: 100% 100%;
  border-radius: 100px;
}

#box1 img{
  width: 100px;
  height: 100px;
  border-radius: 100%;
}
#box2{
  position: absolute;
  top: 20%;
  left: 35%;
}
#box3{
  position: absolute;
  top: 26%;
  width: 187px;
  left: 10%;
  color: #AAAAAA;
  font-size: 15px;
}
#box4{
  position: absolute;
  top: 35%;
  left: 20%;
  color: #969696;
}
#box5{
  position: absolute;
  top: 37%;
  left: 35%;
  color:  #969696;
  font-size: 16px;
}
#box6{
  position: absolute;
  top: 66%;
  left: 20%;
  color: #969696;
}
#box7{
  position: absolute;
  top: 68%;
  left: 35%;
  color:  #969696;
}
</style>
