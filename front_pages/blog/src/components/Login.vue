<template>
  <div class="mainContainer">
    <div class="container right-panel-active">
      <!-- Sign in -->
      <div class="container__form container--signup">
        <el-form :model="form" :rules="rules" class="formStyle" ref="form"  id="form2">
          <h2 class="form__title">登 录</h2>
          <el-form-item  prop="name" class="input">
            <el-input v-model="form.name"  placeholder="账 号"></el-input>
          </el-form-item>
          <!--          <input placeholder="账 号" class="input" required/>-->
          <el-form-item  prop="password" class="input">
            <el-input v-model="form.password"  show-password  placeholder="密 码" ></el-input>
          </el-form-item>
          <!--          <input type="password" placeholder="密 码" class="input" />-->
          <router-link to="/certificate" class="link">忘记密码?</router-link>
          <el-button class="btn" @click="submitForm('form')">登 录</el-button>
        </el-form>

      </div>

      <!-- Sign up -->
      <div class="container__form container--signin">
        <el-form :model="registForm" class="formStyle" ref="registForm" :rules="rules">
          <h2 class="form__title">注 册</h2>
          <el-form-item  prop="name_re" class="input">
            <el-input v-model="registForm.name_re" autocomplete="off" placeholder="账 号"></el-input>
          </el-form-item>
<!--          <input type="text" placeholder="账 号"  class="input"/>-->
          <el-form-item prop="email" class="input">
            <el-input  v-model="registForm.email" autocomplete="off" placeholder="电子邮箱"></el-input>
          </el-form-item>
<!--          <input type="email"  class="input" />-->

          <el-form-item prop="password_1" class="input">
            <el-input v-model="registForm.password_1" show-password placeholder="密 码" autocomplete="off"></el-input>
          </el-form-item>
<!--          <input type="password" placeholder="密 码" class="input" />-->
          <el-form-item prop="password_2" class="input">
            <el-input v-model="registForm.password_2" show-password autocomplete="off" placeholder="二次密码"></el-input>
          </el-form-item>
<!--          <input type="password" placeholder="二次密码" class="input" />-->
<!--          <button class="btn">注 册</button>-->
          <el-button class="btn" @click="askForCode('registForm')">立即注册</el-button>
        </el-form>
      </div>

      <!-- Overlay -->
      <div class="container__overlay">
        <div class="overlay">
          <div class="overlay__panel overlay--left">
            <button class="btn" id="signIn">注 册</button>
          </div>
          <div class="overlay__panel overlay--right">
            <el-button class="btn" id="signUp">登 录</el-button>
          </div>
        </div>
      </div>
    </div>

<!--    <div style="overflow-y: auto;position: relative;top: 20%;left: 30%;background: rgba(255, 255, 255, .9);border-radius: 20px;height: 62%;width: 40%">-->
<!--      <br/>-->
<!--      <br/>-->
<!--      <h1 >欢 迎</h1>-->


<!--      <el-form :model="form" :rules="rules" ref="form" label-width="80px">-->
<!--      <div style="width: 60%; position: relative;left: 15%">-->
<!--        <el-form-item label="账 号 "prop="name">-->
<!--          <el-input v-model="form.name"  placeholder="账 号"></el-input>-->
<!--        </el-form-item>-->
<!--      </div>-->


<!--      <div style="width: 60%; position: relative;left: 15%">-->
<!--        <el-form-item label="密 码" prop="password" >-->
<!--          <el-input v-model="form.password" show-password  placeholder="密 码" ></el-input>-->
<!--        </el-form-item>-->
<!--      </div>-->
<!--      </el-form>-->
<!--      <el-row>-->
<!--        <el-col :span="24"><el-button  type="primary" @click="submitForm('form')">立即登录</el-button>-->
<!--        </el-col>-->
<!--      </el-row>-->

<!--      <el-row>-->
<!--        <el-col :span="24">-->
<!--          <el-button type="text" @click="certificate()" style="opacity: .54;font-size: 12px;color: black;">忘记密码?</el-button>-->
<!--        </el-col>-->
<!--      </el-row>-->



<!--      <el-row>-->
<!--        <el-col :span="24"><h4>还没有账号?<el-button type="text" @click="regist()">立即注册</el-button></h4>-->
<!--        </el-col>-->
<!--      </el-row>-->
<!--    </div>-->


    <div>
<!--      <el-dialog title="注 册" :visible.sync="showRegisterDialog">-->
<!--        <el-form :model="registForm" ref="registForm" label-width="80px" :rules="rules">-->
<!--          <div style="width: 60%; position: relative;left: 15%">-->
<!--            <el-form-item label="账 号 " prop="name_re">-->
<!--              <el-input v-model="registForm.name_re" autocomplete="off"></el-input>-->
<!--            </el-form-item>-->
<!--          </div>-->

<!--          <div style="width: 60%; position: relative;left: 15%">-->
<!--          <el-form-item label="密 码 " prop="password_1">-->
<!--            <el-input v-model="registForm.password_1" show-password autocomplete="off"></el-input>-->
<!--          </el-form-item>-->
<!--          </div>-->

<!--          <div style="width: 60%; position: relative;left: 15%">-->
<!--          <el-form-item label="确认密码" prop="password_2">-->
<!--            <el-input v-model="registForm.password_2" show-password autocomplete="off"></el-input>-->
<!--          </el-form-item>-->
<!--          </div>-->

<!--          <div style="width: 60%; position: relative;left: 15%">-->
<!--          <el-form-item label="电子邮箱" prop="email">-->
<!--            <el-input v-model="registForm.email" autocomplete="off"></el-input>-->
<!--          </el-form-item>-->
<!--          </div>-->

<!--        </el-form>-->
<!--        <div slot="footer" style="position: absolute;left: 43%;top: 85%">-->
<!--          <el-button type="primary" @click="askForCode('registForm')">立即注册</el-button>-->
<!--        </div>-->

        <el-dialog
          width="30%"
          title="验证"
          :visible.sync="innerVisible"
          append-to-body
        id="verifyDialog">
          <div style="width: 60%;position: relative;left: 5%">
            <el-input v-model="verifyCode" placeholder="验证码"></el-input>
          </div>
          <div style="position: absolute; top: 39.8%;left: 70%">
            <el-button type="primary" v-if="!isCodeEffective" style="width: 12vh" @click="askForCodeAgain">重发</el-button>
            <el-button type="primary" v-if="isCodeEffective" style="width: 12vh" disabled>{{count}}</el-button>
          </div>
          <div style="position: relative;left: 40%">
            <br/>
            <el-button type="primary" @click="checkCode">确&nbsp;&nbsp;&nbsp;&nbsp;定</el-button>
          </div>
        </el-dialog>
<!--      </el-dialog>-->
    </div>
  </div>
</template>

<script>

export default {
  name: "Login",
  mounted:function () {
    const signInBtn = document.getElementById("signIn");
    const signUpBtn = document.getElementById("signUp");
    // const fistForm = document.getElementById("form1");
    // const secondForm = document.getElementById("form2");
    const container = document.querySelector(".container");

    signInBtn.addEventListener("click", () => {
      container.classList.remove("right-panel-active");
    });

    signUpBtn.addEventListener("click", () => {
      container.classList.add("right-panel-active");
    });

    // fistForm.addEventListener("submit", (e) => e.preventDefault());
    // secondForm.addEventListener("submit", (e) => e.preventDefault());
  },
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      }
      else if (value.length<8)
      {
        callback(new Error('密码长度不得低于8位'));
      }
      else {
        if (this.registForm.password_2 !== '') {
          this.$refs.registForm.validateField('password_2');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.registForm.password_1) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    var checkRepeat = (rule, value, callback) => {
      let tes = /[0-9a-zA-Z]+$/;
      if (value === ''){
        callback(new Error('账号不可为空'));
      }else{
        if(value.length > 18)
        {
          callback(new Error(('用户名过长')))
        }else if (tes.test(value)===false){
          callback(new Error(('账号仅支持英文和数字')))
        }
        else {
          this.axios.post("https://healthkeeper.top:8080/user/nameRepeat",this.$qs.stringify({username: this.registForm.name_re})).
          then(res =>{
            const status = res.data.status;
            if (status===0)
            {
              callback(new Error('该用户名已存在'))
            }else {
              callback()
              this.$message.success(res.data.message);
            }
          }).
          catch(e => {
            this.$message.error("与服务器失去连接")
          })
        }
      }
    };
    var checkEmail=(rule, value, callback) => {
      if (value === ''){
        callback(new error('请输入邮箱'));
      }else{
        this.axios.post("https://healthkeeper.top:8080/user/checkEmail",this.$qs.stringify({email: this.registForm.email})).
        then(res =>{
          const status = res.data.status;
          if (status===0)
          {
            callback(new Error('该邮箱已经被注册!'))
          }else {
            callback()
            this.$message.success(res.data.message);
          }
        }).
        catch(e => {
          this.$message.error("与服务器失去连接")
        })
      }
    };
    var validateUsername=(rule,value,callback)=>{
      let tes = /[0-9a-zA-Z]+$/;
      if (value === ''){
        callback(new Error('请输入账号'));
      }else{
        if(value.length > 18)
        {
          callback(new Error(('账号过长')))
        }else if (tes.test(value)===false){
          callback(new Error(('账号仅支持英文和数字,请确认您的账号')))
        }
        else {
          callback()
        }
      }
    };
    return {
      form: {
        name: '',
        password: ''
      },
      registForm:{
        name_re: '',
        password_1: '',
        password_2: '',
        email:''
      },
      showRegisterDialog:false,
      innerVisible:false,
      verifyCode:'',
      isCodeEffective:false,
      count:'',
      timer:null,
      rules: {
        name: [
          { validator: validateUsername, trigger: 'blur' },
        ],
        name_re:[
          {validator: checkRepeat, trigger: 'blur'}
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        password_1: [
          { validator: validatePass, trigger: 'blur' }
        ],
        password_2: [
          { validator: validatePass2, trigger: 'blur' }
        ],
        email: [
          { validator: checkEmail, trigger: 'blur' },
        ]
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const loading = this.$loading({
            lock: true,
            text: 'Loading',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
          });
            this.axios.post("https://healthkeeper.top:8080/user/login", this.$qs.stringify({username: this.form.name,pwd: this.$md5(this.form.password)})).then(
              res=>{
                if (res.data.status === 1)
                {

                  this.$store.commit("setToken",res.headers.authorization);
                  this.getTokenFromServer();
                  this.$message.success("欢迎你! "+res.data.message);
                  this.queryUserInfo()
                  this.$router.push("/blog")
                }else
                {
                  this.$message.error(res.data.message);
                }
                loading.close();
              }
            )
          .catch ((e)=> {
            loading.close();
            this.$message.error('与服务器断开连接!');
          })
        } else {
          return false;
        }
      });
    },
    regist(){
      this.showRegisterDialog=!this.showRegisterDialog
    },
    askForCode(formName){
      this.$refs[formName].validate((valid) => {
        if (valid) {
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
        target: document.getElementById("verifyDialog")
      });
      this.innerVisible=true
      //axios后端发送邮件
      this.axios.post("https://healthkeeper.top:8080/user/sendverifycode",this.$qs.stringify({email:this.registForm.email,
        name:this.registForm.name_re,password:this.$md5(this.registForm.password_2)})).then(res=>
      {
        const h = this.$createElement;

          this.$notify({
            title: '提示',
            message: h('i', { style: 'color: teal'}, '验证码已发送至您的邮箱('+this.registForm.email+')中，请注意查收！')
          });
          loading.close()
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

      }).catch(e => {
        loading.close();
        this.$message.error("与服务器失去连接")
      })
        }})
    },
    //后端检查验证码
    checkCode(){
      this.axios.post("https://healthkeeper.top:8080/user/regist",this.$qs.stringify({encryptedCode:this.verifyCode})).
      then(res=>{
        const status = res.data.status
        if (status === 1)
        {
          this.$message.success(res.data.message)
          this.innerVisible = false
          this.showRegisterDialog = false
        }else
        {
          this.$message.error(res.data.message)
        }
      })
    },certificate(){
      this.$router.push("/certificate")
    },askForCodeAgain(){
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)',
        target: document.getElementById("verifyDialog")
      });
      this.innerVisible=true
      //axios后端发送邮件
      this.axios.post("https://healthkeeper.top:8080/user/sendverifycode",this.$qs.stringify({email:this.registForm.email,
        name:this.registForm.name_re,password:this.$md5(this.registForm.password_2)})).then(res=>
      {
        const h = this.$createElement;

        this.$notify({
          title: '提示',
          message: h('i', { style: 'color: teal'}, '验证码已发送至您的邮箱('+this.registForm.email+')中，请注意查收！')
        });
        loading.close()
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

      }).catch(e => {
        loading.close();
        this.$message.error("与服务器失去连接")
      })
    }
    ,queryUserInfo(){
      this.axios.post("https://healthkeeper.top:8080/user/finduserinfo",null,
        {headers:{'authorization':this.$store.state.token}})
        .then(res =>{
          this.$store.commit("setUserAva",res.data.imageUrl)
          this.$store.commit("setUserSig",res.data.signature)
          this.$store.commit("setUserSlo",res.data.slogan)
          this.$store.commit("setUserName",res.data.username)
        } ).catch(e =>{
        this.$message.error("拉取用户信息时发生了错误...!")
      })
    }
    ,async getTokenFromServer(){
      this.axios.post("https://healthkeeper.top:8080/getToken",null,{headers:{'authorization':this.$store.state.token}})
      .then(res =>{
        console.log(res.data);
        this.$store.commit("setKeyId",res.data.accessKeyId);
        this.$store.commit("setKeySecret",res.data.accessKeySecret);
        this.$store.commit("setStsToken",res.data.securityToken);
        this.$store.commit("setExpire",res.data.expiration);
      })
    }

  }
}

</script>
<style src="../../static/login-root.css"></style>
<style scoped>
/*.mainContainer{*/
/*  display: flex;*/
/*  overflow-y: auto;*/
/*  background-image: url("https://easypalace.oss-cn-chengdu.aliyuncs.com/projectfile/bgp.jpg");*/
/*  background-size: cover;*/
/*  height: 89vh;*/
/*}*/

.mainContainer {
  align-items: center;
  background: var(--white) url("https://easypalace.oss-cn-chengdu.aliyuncs.com/projectfile/bgp.jpg") no-repeat fixed center;
  background-size: cover;
  display: grid;
  height: 100vh;
  place-items: center;
}

.form__title {
  font-weight: 300;
  margin: 0 0 1.25rem;
}

.link {
  color: var(--gray);
  font-size: 0.9rem;
  margin: 1.5rem 0;
  text-decoration: none;
}

.container {
  background-color: var(--white);
  border-radius: var(--button-radius);
  box-shadow: 0 0.9rem 1.7rem rgba(0, 0, 0, 0.25),
  0 0.7rem 0.7rem rgba(0, 0, 0, 0.22);
  height: var(--max-height);
  max-width: var(--max-width);
  overflow: hidden;
  position: relative;
  top: 10%;
  width: 100%;
}

.container__form {
  height: 100%;
  position: absolute;
  top: 0;
  transition: all 0.6s ease-in-out;
}

.container--signin {
  left: 0;
  width: 50%;
  z-index: 2;
}

.container.right-panel-active .container--signin {
  transform: translateX(100%);
}

.container--signup {
  left: 0;
  opacity: 0;
  width: 50%;
  z-index: 1;
}

.container.right-panel-active .container--signup {
  -webkit-animation: show 0.6s;
  animation: show 0.6s;
  opacity: 1;
  transform: translateX(100%);
  z-index: 5;
}

.container__overlay {
  height: 100%;
  left: 50%;
  overflow: hidden;
  position: absolute;
  top: 0;
  transition: transform 0.6s ease-in-out;
  width: 50%;
  z-index: 100;
}

.container.right-panel-active .container__overlay {
  transform: translateX(-100%);
}

.overlay {
  background: var(--lightblue) url("https://easypalace.oss-cn-chengdu.aliyuncs.com/projectfile/bgp.jpg") no-repeat fixed center;
  background-size: cover;
  height: 100%;
  left: -100%;
  position: relative;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
  width: 200%;
}

.container.right-panel-active .overlay {
  transform: translateX(50%);
}

.overlay__panel {
  align-items: center;
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: center;
  position: absolute;
  text-align: center;
  top: 0;
  transform: translateX(0);
  transition: transform 0.6s ease-in-out;
  width: 50%;
}

.overlay--left {
  transform: translateX(-20%);
}

.container.right-panel-active .overlay--left {
  transform: translateX(0);
}

.overlay--right {
  right: 0;
  transform: translateX(0);
}

.container.right-panel-active .overlay--right {
  transform: translateX(20%);
}

.btn {
  background-color: var(--blue);
  background-image: linear-gradient(90deg, var(--blue) 0%, var(--lightblue) 74%);
  border-radius: 20px;
  border: 1px solid var(--blue);
  color: var(--white);
  cursor: pointer;
  font-size: 0.8rem;
  font-weight: bold;
  letter-spacing: 0.1rem;
  padding: 0.9rem 4rem;
  text-transform: uppercase;
  transition: transform 80ms ease-in;
}

.form > .btn {
  margin-top: 1.5rem;
}

.btn:active {
  transform: scale(0.95);
}

.btn:focus {
  outline: none;
}

.btn:hover{
  background: linear-gradient(120deg, #3498db, #8e44ad, #3498db);
}

.formStyle {
  background-color: var(--white);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 3rem;
  height: 100%;
  text-align: center;
}

.input {
  /*background-color: #fff;*/
  border: none;
  padding: 0.2rem 0.9rem;
  margin: 0.5rem 0;
  width: 100%;
}

@-webkit-keyframes show {
  0%,
  49.99% {
    opacity: 0;
    z-index: 1;
  }

  50%,
  100% {
    opacity: 1;
    z-index: 5;
  }
}

@keyframes show {
  0%,
  49.99% {
    opacity: 0;
    z-index: 1;
  }

  50%,
  100% {
    opacity: 1;
    z-index: 5;
  }
}



</style>
