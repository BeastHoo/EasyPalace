<template>
  <div id="container">
    <div id="avatar">
        <viewer>
          <img v-if="userInfo.imageUrl" :src="userInfo.imageUrl">
          <img v-else src="../assets/ico.jpg" alt="" >
        </viewer>
    </div>

    <div id="table">
      <div id="form-wrapper">
        <el-form ref="form" :label-position="formPos" label-width="25%" :model="userInfo">
          <el-form-item label="性别">
            <el-select style="width: 90%" :disabled="isUnEditable" v-model="userInfo.gender" >
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
              style="width: 90%"
              :disabled="isUnEditable"
              v-model="userInfo.birthday"
              type="date"
              format="yyyy 年 MM 月 dd 日"
              value-format="yyyy-MM-dd">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="账号">
            <el-input style="width: 90%" :disabled="true" v-model="userInfo.username"></el-input>
          </el-form-item>
          <el-form-item label="邮箱地址">
            <el-input style="width: 90%" :disabled="true" v-model="userInfo.email"></el-input>
          </el-form-item>
          <el-form-item label="签名">
            <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" maxlength="30" show-word-limit
                      style="width: 90%" :disabled="isUnEditable" v-model="userInfo.signature"></el-input>
          </el-form-item>
          <el-form-item label="座右铭">
            <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" maxlength="30" show-word-limit
                      style="width: 90%"  :disabled="isUnEditable" v-model="userInfo.slogan"></el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>


    <div id="blogBox">     <!-- 搜索结果为blog时进行渲染-->
      <div  style="height: 100%;width: 700px;position: absolute;top:20px;left: 80px;">
        <ul id="box3"  v-for="(blog_item,i) in resultList" :key="blog_item.blogId">
          <li @click="look(blog_item.blogId)">
            <el-image v-if="blog_item.imgUrl !== ''" style="position:absolute;text-align: left;margin-top: -12px;width: 230px;
            height: 220px;left: 10px;border-radius: 5px;" :src="blog_item.imgUrl" alt="封面" lazy >
              <div slot="placeholder" class="image-slot">
                加载中<span class="dot">...</span>
              </div>
            </el-image>
            <img v-else id="ava" src="../assets/b1.jpg" alt="">
            <div style="position: relative;left: 220px;width: 460px;height: 25px">
              <h2>{{blog_item.title}}</h2>
            </div>

            <div style="position: relative;left: 220px;width: 460px;height: 142px;overflow-y:hidden;">
              <p>{{blog_item.description}}</p>
            </div>
            <div style="position: relative;left: 220px;width: 420px">
              <hr>
            </div>
            <div style="position: relative;left: 220px;width: 420px;text-align: left">
              <small>{{blog_item.editTime}}&nbsp;{{blog_item.editor}}</small>
            </div>
            <div style="position: relative;left:260px;top: -20px;display: inline-block;width: 100px;height: 20px;text-align: left">
              <img src="../assets/click.png" style="width: 20px;height: 20px;"/>
              <small style="position: relative;top: -5px">{{blog_item.clickRate}}</small>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: "Setting",
  watch: {
    '$route' (to, from) { //监听路由是否变化
      if(to.query.id !== from.query.id ){
        // this.id = to.query.id;
        this.queryData();//重新加载数据
      }
    }
  },
  created() {
    if(this.$route.query) {
      // this.id = this.$route.query.id;
      this.queryData();
    }
  },
  data() {
    return {
      formPos:"right",
      isNotChangeAvatar:true,
      isUnEditable:true,
      userInfo: {
        imageUrl:"",
      },
      options:[
        "男","女"
      ],
      resultList:[]
    }
  },
  methods:{
    queryData() {
      let username = this.$route.query.id
      this.axios.post("https://healthkeeper.top:8080/user/findEditor",this.$qs.stringify({username:username}))
        .then(res =>{
          this.userInfo = res.data
        } ).catch(e =>{
        this.$message.error("拉取用户信息时发生了错误，请重试!")
      })

      this.axios.post("https://healthkeeper.top:8080/blog/findblogbyuser",this.$qs.stringify({username:username}))
        .then(res =>{
          this.resultList=res.data
        }).catch(e =>{
        this.$message.error("拉取用户信息时发生了错误，请重试!")
      })
    },
    look(blog_id){
      //先this.router.push到/blog去,带上当前选中的blog的blog_id
      this.$router.push(
        {
          path:"/blogdetails",
          query:{
            bid:blog_id
          }
        }
      );
    },
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


#avatar{
  background: rgba(255, 255, 255, .8);
  border-radius: 100%;
  position: relative;
  top: 6%;
  left: 8.5%;
  width: 130px;
  height: 130px;
}

#avatar img{
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
  background: rgba(255, 255, 255, .8);
  border-radius: 20px;
  position: relative;
  left: 2%;
  top: 10%;
  width: 20%;
  height: 64%;
}

#form-wrapper{
  overflow-y: auto;
  height: 92%;
  width: 99%;
  position: relative;
  left: 1%;
  top: 4%;
}



#box3{
  width: 690px;
  height: 230px;
  border-radius: 5px;
  border: 1px solid #EDEDED;
}

#blogBox{
  background: rgba(255, 255, 255, .8);
  border-radius: 10px;
  border: 1px dashed #409EFF;
  width: 72%;
  height: 600px;
  overflow-y: auto;
  position: absolute;
  left: 24%;
  top: 15%;
}

#ava{
  margin-top: -12px;
  width: 230px;
  height: 220px;
  position: absolute;
  left: 10px;
  border-radius: 5px;
  /*top: 5px;*/
}
#box3 h2{
  text-align: left;
  color: darkcyan;
  width: 460px;
  /*top:20px;*/
  font-size: 20px;
}
#box3 p{
  word-break: break-all;
  word-wrap: break-word;
  overflow-wrap: break-word;

  text-align: left;
  font-family: 黑体;
  color: #99a9bf;
  line-height:30px
}
#box3 hr{
  width: 420px;
  border: 1px solid #EDEDED;
}
#box3 small{
  color: #99a9bf;
}

ul{
  list-style-type: none;
}
</style>
