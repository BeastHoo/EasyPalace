<template>
  <div>
    <el-row>
      <el-col :span="23">
        <h3 style="color: aquamarine;background: #B3C0D1;width: 20%;position: relative;left: 40%">个人文件</h3>
      </el-col>
      <el-col :span="1">
        <el-button type="primary" icon="el-icon-upload2" @click="uploads()" circle style="position: absolute;top: 11%;left: 90%"></el-button>
      </el-col>
    </el-row>


      <el-collapse-transition>
        <div v-show="displayUploads">
          <div class="transition-box">
            <el-upload
              ref="uploads"
              drag
              action=""
              :http-request="uploadFile"
               multiple
              :on-success="uploadSuccess">
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              <div class="el-upload__tip" slot="tip">单个文件不超过200MB，多个文件不超过500MB</div>
            </el-upload>
            <el-button type="text" id="switcher" @click="switchMode">当前为私密上传</el-button>
          </div>
        </div>
      </el-collapse-transition>


    <el-dialog title="在线查看" :visible.sync="dialogVisible" show-close @close="change()">
      <div @contextmenu.prevent="menuPlayer()" v-if="ispic === true">
        <viewer>
          <img  :src="picsrc" id="hideImg" style="width: 100%" height="100%" alt=""/>
        </viewer>
      </div>

      <div @contextmenu.prevent="menuPlayer()" v-if="isvideo === true">
        <video ref="vid" width="100%" height="100%" controls="controls" controlsList="nodownload" autoplay >
          <source :src="vidsrc" type="video/mp4">
          您的浏览器不支持Video标签。
        </video>
      </div>

      <div @contextmenu.prevent="menuPlayer()" v-if="ismusic === true">
        <audio ref="aud"  controls="controls" controlsList="nodownload" autoplay>
          <source :src="mussrc">
          您的浏览器不支持audio标签。
        </audio>
      </div>

    </el-dialog>
    <template>
      <el-table
        :data="curPage"
        border
        style="width: 100%"
        height="580">
        <el-table-column
          prop="username"
          label="用户名"
          width="200">
        </el-table-column>
        <el-table-column
          prop="filename"
          label="文件名">
        </el-table-column>
        <el-table-column
          prop="type"
          :formatter = "formatter"
          label="类型">
        </el-table-column>
        <el-table-column
          prop="msize"
          label="文件大小">
        </el-table-column>
        <el-table-column
          prop="mtime"
          label="提交时间"
          width="180">
        </el-table-column>
        <el-table-column
          prop="downloadnum"
          label="下载量"
          width="120">
        </el-table-column>
        <el-table-column
          prop="collectnum"
          label="收藏量"
          width="120">
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="220">
          <template slot-scope="scope">
            <div v-if="isDownLoading[scope.$index] === true">
              <el-progress :text-inside="true" :stroke-width="20" :percentage="percentage"></el-progress>
            </div>
            <div v-else>
              <el-button v-if="scope.row.isplayable === true" type="text" size="small" icon="el-icon-video-play"
                         @click="play(scope.row)">查看</el-button>
              <el-button type="text" size="small" @click="downloadFile(scope.row,scope.$index)" icon="el-icon-download">下载</el-button>
              <el-tooltip effect="dark" content="取消收藏" placement="top-start">
                <el-button v-if="scope.row.iscollected === true" type="text" size="small" @click="cancelCollectFile(scope.row,scope.$index)"
                           icon="el-icon-star-on">已收藏</el-button>
              </el-tooltip>
              <el-button v-if="scope.row.iscollected !== true" type="text" size="small" @click="openConfirm(scope.row,scope.$index)"
                         icon="el-icon-remove">删除</el-button>
            </div>

          </template>
        </el-table-column>
      </el-table>
    </template>

    <el-pagination
      background
      layout="prev, pager, next"
      :total="index.all"
      @current-change="exactPage">
    </el-pagination>

  </div>
</template>

<script>
const path = require("path");
const OSS = require('ali-oss');
export default {
  name: "OurSeelfPrivate",
  mounted:function (){
    this.client = new OSS({
      accessKeyId: this.$store.state.keyId,  // 查看你自己的阿里云KEY
      accessKeySecret: this.$store.state.keySecret, // 查看自己的阿里云KEYSECRET
      stsToken:this.$store.state.stsToken,
      bucket: 'easypalace', // 你的 OSS bucket 名称
      region: 'oss-cn-chengdu', // bucket 所在地址
    });
    this.querypridatas();
    for (let i=0; i<20; i++)
    {
      this.isDownLoading[i]=false
    }
  },
  data() {
    return {
      tabledata:[],
      curPage:[],
      dialogVisible: false,
      ismusic: false,
      mussrc:'',
      isvideo: false,
      vidsrc:'',
      ispic: false,
      picsrc:'',
      displayUploads:false,
      priMode:true,
      extraValue:{
        mode:"pri"
      },
      index:{
        all:20,
        page:1
      },
      isDownLoading:[],
      percentage:0,
      fname:"",
      fsize:" ",
      client:null,
    }
  },
  methods: {
    querypridatas() {
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      if (this.$store.state.isLogged === false)
      {
        this.$message.error("请先登录!");
        this.$router.push("/login");
        loading.close()
        return;
      }

      this.axios.post("https://healthkeeper.top:8080/pri/file",null,{headers:{'authorization':this.$store.state.token}}).then(res => {
        this.tabledata=res.data;
        this.index.all=res.data.length;
        this.curPage=this.tabledata.slice(0,this.tabledata.length>10?10:this.tabledata.length)
        loading.close();
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
    uploadFile(options){
      options.status = 'uploading';   //本人是直接向这个对象里面添加status、percentage两个对象
      options.percentage = 0;
      try {
        let file = options.file; // 拿到 file
        // console.log(options);
        this.fname = options.file.name;
        this.fsize = options.file.size;
        let fileName = file.name
        let date = new Date().getTime()
        let fileNames = `${date}_${fileName}` // 拼接文件名，保证唯一，这里使用时间戳+原文件名
        // 上传文件,这里是上传到OSS的 uploads文件夹下
        this.setUploadParam().then(
          async status =>{
            if (status === 200)
            {
              this.client.multipartUpload("file/"+this.$store.state.username+"/pan/"+fileNames, file,{
                progress:function (p) { //获取进度条的值
                  // console.log(p);
                  options.onProgress({percent:p*100});
                }
              }).then(res=>{
                if (res.res.statusCode === 200) {
                  options.onSuccess(res)
                }else {
                  options.onError("上传失败")
                }
              })
            }
          }
        )
      }catch (e) {
        console.log(e)
        options.onError("上传失败")
      }
    },
    play(row) {
      if (row.isvideo)
      {
        this.vidsrc=row.loc
        this.isvideo=true
        this.ispic=false
        this.ismusic=false
        this.dialogVisible=true;
      }else if (row.ismusic)
      {
        this.mussrc=row.loc
        this.ismusic=true
        this.isvideo=false
        this.ispic=false
        this.dialogVisible=true;
      }else
      {
        this.picsrc=row.loc
        this.ispic=true
        this.isvideo=false
        this.ismusic=false
        this.dialogVisible=true;
      }

    },
    downloadFile(row,index) {
      this.$notify({
        title: '警告',
        message: '下载过程中请勿在此网页做其他操作,请耐心等待',
        type: 'warning',
        position: 'top-left'
      });
      this.isDownLoading[index] = true;
      this.axios.get(row.loc,
        {responseType:'blob', onDownloadProgress: (evt) => {
            // 有时候拿不到total值，这个值总是为0
            //只要设置后台的代码  response.setContentLengthLong(文件长度); 就可以了
            this.percentage = parseInt((evt.loaded / evt.total) * 100);
            if(this.percentage===100){
              this.isDownLoading[index]= false;
              this.$message.success("下载成功!");
              this.axios.post("https://healthkeeper.top:8080/download",this.$qs.stringify({fid:row.id}),{headers:{'authorization':this.$store.state.token}});
            }}})
          .then(res =>{
            const filename = row.filename;
            const blob = new Blob([res.data]);
            var downloadElement = document.createElement("a");
            var href = window.URL.createObjectURL(blob);
            downloadElement.href = href;
            downloadElement.download = decodeURIComponent(filename);
            document.body.appendChild(downloadElement);
            downloadElement.click();
            document.body.removeChild(downloadElement);
            window.URL.revokeObjectURL(href);
      })
    },
    exactPage(currentpage) {
      this.index.page=currentpage
      this.curPage = this.tabledata.slice((currentpage - 1) * 10 , (currentpage * 10) < this.index.all ? (currentpage * 10) : this.index.all);
    },
    change()
    {
      this.dialogVisible=false;
      if (this.isvideo === true)
      {
        this.isvideo=false;
        this.$refs.vid.pause()
      }
      else if(this.ismusic === true)
      {
        this.ismusic=false;
        this.$refs.aud.pause()
      }
      else
        this.ispic=false;
    },
    cancelCollectFile(row,index) {
      this.axios.post("https://healthkeeper.top:8080/pub/cancelcollect",this.$qs.stringify({fid:row.id,cid: row.cid}),
        {headers: {'authorization': this.$store.state.token}})
        .then(res=>{
          if (res.data.status === 1)
          {
            this.$message.success("取消收藏!");
            this.curPage.splice(index, 1);
            this.tabledata.splice((this.index.page-1)*10+index,1);
            this.exactPage(this.index.page)
            if(this.curPage.length===0)
            {
              this.index.page-=1;
              this.exactPage(this.index.page)
            }
            this.index.all=this.tabledata.length;
          }else {
            this.$message.error("取消收藏失败!")
          }
        }).catch((e)=>
      {
        this.$message.error("取消收藏失败! 服务器未响应请求");
      })
    },
    deleteFile(row, index){
      let url = row.loc.replace("https://easypalace.oss-cn-chengdu.aliyuncs.com/","");


      this.axios.post("https://healthkeeper.top:8080/pri/del", this.$qs.stringify({
        fid:row.id,
        type:row.type,
        filename:row.filename
      }),{headers:{'authorization':this.$store.state.token}})
        .then(res => {
          if (res.data.status === 1) {
            this.client.delete(url);

            this.curPage.splice(index, 1);
            this.tabledata.splice((this.index.page-1)*10+index,1);
            this.exactPage(this.index.page)
            if(this.curPage.length===0)
            {
              this.index.page-=1;
              this.exactPage(this.index.page)
            }
            this.index.all=this.tabledata.length;
            this.$message.success(res.data.message)

          }
          else {
            this.$message.error(res.data.message)
          }
        }).catch(e => {
        this.$message.error("删除失败! 服务器未响应请求");
      })
    },
    uploads()
    {
      this.displayUploads=!this.displayUploads
    },
    switchMode()
    {
      this.priMode=!this.priMode;
      if (this.priMode===false)
      {
        document.getElementById("switcher").innerText="当前为公开上传"
        this.extraValue.mode="pub"
      }
      else
      {
        document.getElementById("switcher").innerText="当前为私密上传"
        this.extraValue.mode="pri"
      }
    },
    uploadSuccess(res){
      this.prog=0
      if (res.name !== "" || res.name !== undefined)
      {
        let url = "https://easypalace.oss-cn-chengdu.aliyuncs.com/"+res.name
        this.axios.post("https://healthkeeper.top:8080/uploads",this.$qs.stringify({mode:this.extraValue.mode,file:this.fname,url:url,size:this.fsize}),
          {headers:{'authorization':this.$store.state.token}})
        .then(response =>{
          if(response.data.status===1){
            this.$message.success("上传成功！");
            this.querypridatas();
            // this.$router.go(0);
            this.displayUploads=!this.displayUploads
          }else if(response.data.status===0){
            this.$message.error(response.data.message);
          }else{
            this.$message.error('上传失败，请重新上传');
          }
        })
      }

    },
    uploadFail(){
      this.$refs.uploads.clearFiles()
      this.$message.error("发生未知错误，上传失败！")
    },
    menuPlayer(){
      return false;
    },
    formatter(row,index){
      if (row.type === true)
        return "私有"
      else return "公开"
    },
    openConfirm(row,index){
      this.$confirm('此操作将永久删除该文件, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.deleteFile(row,index)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    },
    rTime(date) {
      var json_date = new Date(date).toJSON();
      return new Date(+new Date(json_date) + 8 * 3600 * 1000).toISOString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '')
    },
    setUploadParam() {
      // 判断本地是否有已存在oss
      return new Promise((resolve, reject) => {
        if (this.rTime(this.$store.state.expire) > new Date().format("yyyy-MM-dd hh:mm:ss")) {
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

  }
}
</script>

<style scoped>
.transition-box {
  position: relative;
  left: 30%;
  margin-bottom: 10px;
  width: 40%;
  height: 100%;
  border-radius: 4px;
  background-color: #B3C0D1;
  text-align: center;
  color: #fff;
  padding: 40px 20px;
  box-sizing: border-box;
  margin-right: 20px;
}

</style>
