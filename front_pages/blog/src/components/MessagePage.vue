<template>
<div style="height: 100%">
  <el-container style="height: 89vh; border: 1px solid #eee">
    <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
      <el-menu  v-for="(item,i) in msg" :key="i">
        <el-menu-item @click="show(i)">
          {{item.title}}
          <el-badge style="margin-top: -10%" :is-dot="item.status===0"></el-badge>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-main>
        <div class="textPart">
          <div id="msg">
            <br/>
            <p style="color: steelblue;font-size: x-large" ref="tiTle">点击左侧查看消息</p>
            <span ref="from" style="opacity: .54;font-size: 12px;color: black;display: inline-block;"></span>
            <div ref="content"></div>
          </div>
        </div>
      </el-main>
    </el-container>
  </el-container>

</div>
</template>

<script>
export default {
  mounted:function () {
    if (this.$store.state.isLogged === false)
    {
      this.$message.error("请先登录!");
      this.$router.push("/login");
      return;
    }
    const loading = this.$loading({
      lock: true,
      text: 'Loading',
      spinner: 'el-icon-loading',
      background: 'rgba(0, 0, 0, 0.7)'
    });
    this.axios.get("https://healthkeeper.top:8080/msg",{headers: {
      'authorization': this.$store.state.token}}).then(res=>
    {
      this.msg=res.data
      loading.close()

    }).catch(e=>
    {
      loading.close()
      if (e.response.status === 401)
      {
        this.$message.error(e.response.data.message)
        this.$router.push("/login")
      }
      else this.$message.error("与服务器失去连接...")

    })
  },
  name: "Message",
  data(){
    return{
      msg:[
        {
          title:"欢迎",
          content:"<p>尊敬的用户大大: </p><p>欢迎您使用我的小破站<p>",
          userfrom:"Admin",
          mtime:"2021/10/6"
        }
      ]
    }
  },methods:{
    show(id)
    {
      let _id = this.msg.at(id).id;
      this.$refs.tiTle.innerHTML=this.msg.at(id).title
      this.$refs.from.innerText="来自: "+this.msg.at(id).userfrom
      this.$refs.content.innerHTML="<p>&nbsp;&nbsp;"+this.msg.at(id).content+"</p>"+"<p style='position: absolute;left: 70%'><b>"+this.msg.at(id).mtime+"</b></p>"

      if (this.msg.at(id).status === 0)
      this.axios.post("https://healthkeeper.top:8080/updateStatus",
        this.$qs.stringify({id: _id}),
        {headers: {'authorization': this.$store.state.token}}).then(res=>
      {
        if (res.data.status === 1)
        {
          this.msg.at(id).status = 1
        }
      })


    }
  }
}
</script>

<style scoped>
.textPart{
  background-image: url("https://easypalace.oss-cn-chengdu.aliyuncs.com/projectfile/textPart.jpg");
  background-size: cover;
  height: 100%;
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
}


#msg{
  position: relative;
  top: 1%;
  left: 20%;
  background: rgba(255, 255, 255, .9);
  border-radius: 20px;
  height: 98%;width: 60%;
  overflow-x: auto;
  overflow-y: auto;
}
</style>
