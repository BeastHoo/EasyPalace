<template>
  <div id="container">
    <div id="avatar">
      <el-upload
        :disabled="isUnEditable"
        class="avatar-uploader"
        action=""
        auto-upload="auto-upload"
        :show-file-list="false"
        :http-request="aliOSSUpload"
        :on-success="handleAvatarSuccess">
<!--        :before-upload="beforeAvatarUpload"-->
        <viewer>
        <img v-if="userInfo.imageUrl" :src="userInfo.imageUrl">
        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </viewer>
      </el-upload>



    </div>

    <div id="table">
      <div id="form-wrapper">
      <el-form ref="form" :label-position="formPos" label-width="17%" :model="userInfo">
        <el-form-item label="性别">
          <el-select :disabled="isUnEditable" v-model="userInfo.gender" placeholder="请选择">
            <el-option
              v-for="item in options"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker
            :disabled="isUnEditable"
            v-model="userInfo.birthday"
            type="date"
            format="yyyy 年 MM 月 dd 日"
            value-format="yyyy-MM-dd"
            placeholder="设置生日">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="账号">
          <el-input style="width: 54.5%" :disabled="true" v-model="userInfo.username"></el-input>
        </el-form-item>
        <el-form-item label="邮箱地址">
          <el-input style="width: 70%" :disabled="true" v-model="userInfo.email"></el-input>
        </el-form-item>
        <el-form-item label="签名">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" maxlength="30" show-word-limit
                    style="width: 98%" :disabled="isUnEditable" v-model="userInfo.signature"></el-input>
        </el-form-item>
        <el-form-item label="座右铭">
          <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" maxlength="30" show-word-limit
                    style="width: 98%"  :disabled="isUnEditable" v-model="userInfo.slogan"></el-input>
        </el-form-item>
      </el-form>

    </div>
      <div id="funcButton">
        <el-button v-if="isUnEditable" type="primary" @click="edit">编辑</el-button>
        <div  v-else>
          <el-button  type="success" @click="commit">完成</el-button>
          <el-button  type="warning" @click="cancel">取消</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const OSS = require('ali-oss');


export default {
  name: "Setting",
  mounted: function () {
    if (this.$store.state.isLogged === false)
    {
      this.$message.error("请先登录!")
      this.$router.push("/login")
    }
    else {
      this.client = new OSS({
        accessKeyId: this.$store.state.keyId,  // 查看你自己的阿里云KEY
        accessKeySecret: this.$store.state.keySecret, // 查看自己的阿里云KEYSECRET
        stsToken:this.$store.state.stsToken,
        bucket: 'easypalace', // 你的 OSS bucket 名称
        region: 'oss-cn-chengdu', // bucket 所在地址
      });
      this.queryData();
    }
  },
  data() {
    return {
      formPos:"right",
      isUnEditable:true,
      headerObj: {
        authorization: this.$store.state.token
      },
      userInfo: {
        imageUrl:"",
      },
      userInfoBackUp: {},
      options:[
        "男","女"
      ],
      client:null
    }
  },
  methods:{
    queryData() {
      this.axios.post("https://healthkeeper.top:8080/user/finduserinfo",null,
        {headers:{'authorization':this.$store.state.token}})
      .then(res =>{
        this.userInfo = res.data
      } ).catch(e =>{
        this.$message.error("拉取用户信息时发生了错误，请重试!")
      })
    },
    setUploadParam() {
      // 判断本地是否有已存在oss
      return new Promise((resolve, reject) => {
        if (this.rTime(this.$store.state.expire)  > new Date().format("yyyy-MM-dd hh:mm:ss")) {
          resolve(200);
        } else {
          // 如果本地oss key已过期，再向后端获取
          this.axios.post("https://healthkeeper.top:8080/getToken",null,{headers:{'authorization':this.$store.state.token}})
            .then(res =>{
              this.$store.commit("setKeyId",res.data.accessKeyId);
              this.$store.commit("setKeySecret",res.data.accessKeySecret);
              this.$store.commit("setStsToken",res.data.securityToken)
              this.$store.commit("setExpire",res.data.expiration);
              this.client = new OSS({
                accessKeyId: this.$store.state.keyId,  // 查看你自己的阿里云KEY
                accessKeySecret: this.$store.state.keySecret, // 查看自己的阿里云KEYSECRET
                stsToken:this.$store.state.stsToken,
                bucket: 'easypalace', // 你的 OSS bucket 名称
                region: 'oss-cn-chengdu', // bucket 所在地址
              });
              resolve(200);
            }),
            () => {
              reject(400);
            }
        }
      });
    },
    rTime(date) {
      var json_date = new Date(date).toJSON();
      return new Date(+new Date(json_date) + 8 * 3600 * 1000).toISOString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '')
    },
    aliOSSUpload(options) {
      const isJPG = options.file.type === 'image/jpeg';
      const isPNG = options.file.type === 'image/png';
      const isLt3M = options.file.size / 1024 / 1024 < 3;

      if (!isJPG && !isPNG) {
        this.$message.error('上传头像只能是 JPG 或 PNG 格式!');
        return;
      }
      if (!isLt3M) {
        this.$message.error('上传头像大小不能超过 3MB!');
        return;
      }
      try {
        let file = options.file; // 拿到 file
        let fileName = file.name.substr(0,file.name.lastIndexOf('.'))
        let date = new Date().getTime()
        let fileNames = `${date}_${fileName}` // 拼接文件名，保证唯一，这里使用时间戳+原文件名
        // 上传文件,这里是上传到OSS的 uploads文件夹下
        this.setUploadParam().then(async status=>{
          if (status === 200){
            this.client.put("file/"+this.$store.state.username+"/"+fileNames, file).then(res=>{
              if (res.res.statusCode === 200) {
                options.onSuccess(res)
              }else {
                options.onError("上传失败")
              }
            })
          }
        })

      }catch (e) {
        options.onError("上传失败")
      }
    },
    handleAvatarSuccess(res) {
      this.userInfo.imageUrl = res.url;
      this.$store.commit("setUserAva",this.userInfo.imageUrl)
    },edit(){
      this.userInfoBackUp = this.userInfo
      this.isUnEditable=false;
      this.isNotChangeAvatar=false;
      this.userInfo.imageUrl=""
    },
    cancel(){
      this.userInfo = this.userInfoBackUp
      this.isUnEditable=true;
      this.isNotChangeAvatar=true;
      this.userInfo.imageUrl=this.$store.state.avatar
    },
    commit(){
      this.isNotChangeAvatar=false;
      this.isUnEditable=false;
      this.axios.post("https://healthkeeper.top:8080/user/updateUser",this.$qs.stringify({username:this.userInfo.username,
      signature:this.userInfo.signature,slogan:this.userInfo.slogan,gender:this.userInfo.gender,birthday: this.userInfo.birthday,imageUrl: this.userInfo.imageUrl})).
      then(res =>{
        if (res.data.status === 1)
        {
          this.$message.success("更新用户信息成功")
          this.userInfo.imageUrl=this.$store.state.avatar
          this.$store.commit("setUserSig",this.userInfo.signature)
          this.$store.commit("setUserSlo",this.userInfo.slogan)
          this.$store.commit("setUpdateStatus")
          this.$store.commit("setUserName",this.userInfo.username)
          this.isUnEditable = true
        }else {
          this.$message.error("更新用户信息失败")
        }
      })
    }
  }
}
</script>

<style scoped>
#container{
  background-image: url("https://easypalace.oss-cn-chengdu.aliyuncs.com/projectfile/settingBgp.jpg") ;
  background-size: cover;
  height: 89vh;
  overflow: auto;
}

.avatar-uploader{
  border: 1px dashed #d9d9d9;
  border-radius: 100%;
  height: 129px;
  width: 129px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 130px;
  height: 130px;
  line-height: 130px;
  text-align: center;
}



#avatar{
  background: rgba(255, 255, 255, .8);
  border-radius: 100%;
  position: relative;
  top: 6%;
  left: 45%;
  width: 130px;
  height: 130px;
}

.avatar-uploader img{
  border-radius: 100%;
  width: 130px;
  height: 130px;
  /*height: 0;*/
}

#table{
  overflow-y: auto;
  font-weight: normal;
  color: #2c3e50;
  text-align: left;
  background: rgba(255, 255, 255, .8);border-radius: 20px;
  position: relative;
  left: 27.5%;
  top: 10%;
  width: 45%;
  height: 64%;
}

#form-wrapper{
  overflow-y: auto;
  height: 72%;
  width: 72%;
  position: relative;
  left: 12%;
  top: 6%;
}

#funcButton{
  position: relative;
  /*top: 45px;*/
  /*left: 310px;*/
  /*width: 0;*/
  text-align: center;
  top: 10%;
  /*left: 45%;*/
}
</style>
