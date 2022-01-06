<template>
  <div class="mainContainer">

    <div style="position: relative;top: 20%;left: 30%;background: rgba(255, 255, 255, .9);border-radius: 20px;height: 50%;width: 40%;overflow-y: auto">
      <div style="text-align: left; padding-top: 5%;">
        <el-steps :space="200" :active="active" finish-status="success" align-center>
          <el-step title="输入账户"></el-step>
          <el-step title="验证邮箱"></el-step>
          <el-step title="重设密码"></el-step>
          <el-step title="成功"></el-step>
        </el-steps>
      </div>

      <transition name="el-fade-in-linear">
      <div v-if="active === 0 "  v-show="active === 0" style="width: 40%;position: relative;left: 30%;top: 20%">
        <el-input
          placeholder="请输入您的账号"
          v-model="username"
          clearable>
        </el-input>
      </div>
      </transition>

      <transition name="el-fade-in-linear">
        <div v-if="active === 1 && showVer === false"  v-show="active === 1" style="width: 40%;position: relative;left: 30%;top: 20%">
          <el-input
            placeholder="请输入您的邮箱地址"
            v-model="email"
            clearable>
          </el-input>
        </div>
      </transition>

      <transition name="el-fade-in-linear">
        <div v-if="showVer === true"  v-show="showVer === true" style="width: 40%;position: relative;left: 30%;top: 20%;">
          <div style="position: absolute;left: -10%;width: 80%">
            <el-input
              placeholder="请输入验证码"
              v-model="code"
              clearable>
            </el-input>
          </div>

          <div style="position: relative;left: 50%">
            <el-button type="primary" v-if="!isCodeEffective" style="width: 12vh" @click="verEmail">重发</el-button>
            <el-button type="primary" v-if="isCodeEffective" style="width: 12vh" disabled>{{count}}</el-button>
          </div>

        </div>
      </transition>

      <transition name="el-fade-in-linear">
        <div v-if="active === 2"  v-show="active === 2" style="width: 40%;position: relative;left: 30%;top: 12%">
          <div>
            <el-input
              placeholder="请设置您的新密码"
              v-model="password_1"
              show-password>
            </el-input>
          </div>

          <div style="position: absolute;top: 130%;width: 100%">
            <el-input
              placeholder="请再次输入您的新密码"
              v-model="password_2"
              show-password>
            </el-input>
          </div>

        </div>
      </transition>

      <transition name="el-fade-in-linear">
        <div v-if="active === 3"  v-show="active === 3" style="width: 40%;height: 100%;position: relative;left: 30%;top: 20%">
          <h3>密码重设成功！<a href="/#/login" style="text-decoration-line: none">点此登录</a></h3>
        </div>
      </transition>
      <div style="position: relative;top: 35%">
        <el-button type="primary" v-if="active !==3"  @click="actRequest">确  定</el-button>
        <el-button type="default" v-if="active !==3 || active!==2" :disabled="!useButton" @click="next">下一步</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Certificate",
  data() {
    return {
      active: 0,
      useButton: false,
      username: '',
      email:'',
      showVer: false,
      count:'',
      isCodeEffective:true,
      timer:null,
      code:'',
      password_1:'',
      password_2:''
    };
  },

  methods: {
    next() {
      if (this.active++ > 3)
      {
        this.active = 3
      }
      if (this.active === 2)
      {
        this.showVer = false
      }
      this.useButton = false
    },
    verName(){
      if (this.username !== '')
      {
        this.axios.post("https://healthkeeper.top:8080/user/nameRepeat",this.$qs.stringify({username:this.username})).
        then(res => {
          if (res.data.status === 0)
          {
            this.$message.success("验证成功!")
            this.useButton = true
          }
          else
          {
            this.$message.error("您输入的账号不正确，请重新输入")
          }
        }).catch(e =>
        {
          this.$message.error("与服务器失去连接")
        })
      }
    },
    verEmail(){
      if (this.email !== '')
      {
        this.axios.post("https://healthkeeper.top:8080/user/findEmail",this.$qs.stringify({username:this.username,email:this.email})).
        then(res => {
          if (res.data.status === 1)
          {
            const h = this.$createElement;
            this.$notify({
              title: '提示',
              message: h('i', { style: 'color: teal'}, '验证码已发送至您的邮箱('+this.email+')中，请注意查收！')
            });
            this.showVer = true
            const TIME_COUNT = 60;
            if (!this.timer) {
              this.count = TIME_COUNT;
              this.isCodeEffective = true;
              this.timer = setInterval(() => {
                if (this.count > 0 && this.count <= TIME_COUNT) {
                  this.count--;
                } else {
                  this.isCodeEffective = false;
                  clearInterval(this.timer);
                  this.timer = null;
                }
              }, 1000)
            }
          }
          else if (res.data.status === 2)
          {
            this.active = 0
            this.$message.error(res.data.message)
          }
          else
          {
            this.$message.error("您输入的邮箱不正确，请重新输入")
          }
        }).catch(e =>
        {
          this.$message.error("与服务器失去连接")
        })
      }
    },verCode(){
      if (this.code !== '')
      {
        this.axios.post("https://healthkeeper.top:8080/user/verCode",this.$qs.stringify({code:this.code,email:this.email})).
        then(res=>{
          if(res.data.status === 1)
          {
              this.$message.success(res.data.message)
              this.useButton = true
          }
          else if (res.data.status === 2)
          {
            this.active = 0
            this.$message.error(res.data.message)
          }
          else this.$message.error(res.data.message)
        }).
        catch(e=>{
          this.$message.error("与服务器失去连接")
        })
      }
    },setNewPwd()
    {
      this.axios.post("https://healthkeeper.top:8080/user/reset",this.$qs.stringify({name:this.username,password:this.$md5(this.password_2)})).
        then(res =>
      {
        if (res.data.status === 1)
        {
          this.$message.success("设置成功")
          this.next()
        }
        else
        {
          this.active = 0
          this.$message.error(res.data.message)
        }
      }).catch(e =>{
        this.$message.error("与服务器连接出现错误")
      })
    },actRequest(){
      switch (this.active) {
        case 0:
          this.verName()
          break;
        case 1:
          if (this.showVer === true)
          {
            this.verCode()
            break;
          }else {
            this.verEmail()
            break;
          }
        case 2:
          if (this.password_1 !== this.password_2)
          {
            this.$message.error("两次密码不一致!")
            break;
          }else {
            this.setNewPwd()
            break;
          }
        default:
          break;
      }
    }
  }
}
</script>

<style scoped>
.mainContainer{
  background-image: url("https://easypalace.oss-cn-chengdu.aliyuncs.com/projectfile/bgp.jpg");
  background-size: cover;
  height: 89vh;
}
</style>
