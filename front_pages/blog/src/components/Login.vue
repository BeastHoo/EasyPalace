<template>
  <div class="mainContainer">


    <div style="overflow-y: auto;position: relative;top: 20%;left: 30%;background: rgba(255, 255, 255, .9);border-radius: 20px;height: 62%;width: 40%">
      <br/>
      <br/>
      <h1 >欢 迎</h1>


      <el-form :model="form" :rules="rules" ref="form" label-width="80px">
      <div style="width: 60%; position: relative;left: 15%">
        <el-form-item label="账 号 "prop="name">
          <el-input v-model="form.name"  placeholder="账 号"></el-input>
        </el-form-item>
      </div>


      <div style="width: 60%; position: relative;left: 15%">
        <el-form-item label="密 码" prop="password" >
          <el-input v-model="form.password" show-password  placeholder="密 码" ></el-input>
        </el-form-item>
      </div>
      </el-form>
      <el-row>
        <el-col :span="24"><el-button  type="primary" @click="submitForm('form')">立即登录</el-button>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="24">
          <el-button type="text" @click="certificate()" style="opacity: .54;font-size: 12px;color: black;">忘记密码?</el-button>
        </el-col>
      </el-row>



      <el-row>
        <el-col :span="24"><h4>还没有账号?<el-button type="text" @click="regist()">立即注册</el-button></h4>
        </el-col>
      </el-row>
    </div>


    <div>
      <el-dialog title="注 册" :visible.sync="showRegisterDialog">
        <el-form :model="registForm" ref="registForm" label-width="80px" :rules="rules">
          <div style="width: 60%; position: relative;left: 15%">
            <el-form-item label="账 号 " prop="name_re">
              <el-input v-model="registForm.name_re" autocomplete="off"></el-input>
            </el-form-item>
          </div>

          <div style="width: 60%; position: relative;left: 15%">
          <el-form-item label="密 码 " prop="password_1">
            <el-input v-model="registForm.password_1" show-password autocomplete="off"></el-input>
          </el-form-item>
          </div>

          <div style="width: 60%; position: relative;left: 15%">
          <el-form-item label="确认密码" prop="password_2">
            <el-input v-model="registForm.password_2" show-password autocomplete="off"></el-input>
          </el-form-item>
          </div>

          <div style="width: 60%; position: relative;left: 15%">
          <el-form-item label="电子邮箱" prop="email">
            <el-input v-model="registForm.email" autocomplete="off"></el-input>
          </el-form-item>
          </div>

        </el-form>
        <div slot="footer" style="position: absolute;left: 43%;top: 85%">
          <el-button type="primary" @click="askForCode('registForm')">立即注册</el-button>
        </div>

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
      </el-dialog>
    </div>
  </div>
</template>

<script>

export default {
  name: "Login",
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

<style scoped>
.mainContainer{
  display: flex;
  overflow-y: auto;
  background-image: url("https://easypalace.oss-cn-chengdu.aliyuncs.com/projectfile/bgp.jpg");
  background-size: cover;
  height: 89vh;
}
</style>
