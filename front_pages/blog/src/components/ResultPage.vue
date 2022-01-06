<template>
  <div>
    <div style="height: 100%;text-align: left;">
      <el-row>
        <el-row>
          <el-col :span="18">
            <h3 style="margin-left: 4%">搜索关键字:{{keyword}}</h3>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <span style="margin-left: 4%;opacity: .54;font-size: 12px;color: #2c3e50;">共找到{{resultList.length}}条数据</span>
          </el-col>
        </el-row>

      </el-row>

    </div>
    <div v-if="searchmode==='false'" id="blogBox">     <!-- 搜索结果为blog时进行渲染-->
      <div  style="height: 100%;width: 700px;position: absolute;top:20px;left: 120px;">
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

    <div v-else>     <!-- 搜索结果为文件时进行渲染-->
      <el-dialog title="在线查看" :visible.sync="dialogVisible" show-close @close="change()">
        <div @contextmenu.prevent="menuPlayer()" v-if="ispic === true">
          <img  :src="picsrc" id="hideImg" style="width: 100%" height="100%" alt=""/>
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
                  <el-button v-if="scope.row.iscollected === true" type="text" size="small" @click="cancelCollectFile(scope.row)"
                             icon="el-icon-star-on">已收藏</el-button>
                </el-tooltip>

                <el-button v-if="scope.row.type === true " type="text" size="small" @click="deleteFile(scope.row)"
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
  </div>

</template>

<script>
import OSS from "ali-oss";

export default {

  watch: {
    '$route' (to, from) { //监听路由是否变化
      if(to.query.keyword !== from.query.keyword  ||
      to.query.searchmode !== from.query.searchmode){
        // this.id = to.query.id;
        this.fetchData();//重新加载数据
        this.index.page=1
        this.exactPage(this.index.page)
      }
    }
  },
  created() {
    if(this.$route.query) {
      // this.id = this.$route.query.id;
      this.fetchData();
      this.exactPage(this.index.page)
    }
  },
  name: "ResultPage",
  data(){
    return{
      resultList:[],
      searchmode:true,
      keyword:"",
      curPage:[],
      dialogVisible: false,
      ismusic: false,
      mussrc:'',
      isvideo: false,
      vidsrc:'',
      ispic: false,
      picsrc:'',
      index:{
        all:20,
        page:1
      },
      isDownLoading:[],
      percentage:0,
      client:null,
    }
  },
  methods:{
    fetchData(){
      this.searchmode=this.$route.query.searchmode
      this.resultList=this.$route.query.resultList
      this.keyword=this.$route.query.keyword
      this.index.all=this.resultList.length;
      if (this.searchmode === true ){
        this.client = new OSS({
          accessKeyId: this.$store.state.keyId,  // 查看你自己的阿里云KEY
          accessKeySecret: this.$store.state.keySecret, // 查看自己的阿里云KEYSECRET
          stsToken:this.$store.state.stsToken,
          bucket: 'easypalace', // 你的 OSS bucket 名称
          region: 'oss-cn-chengdu', // bucket 所在地址
        });
        for (let i=0; i<20; i++)
        {
          this.isDownLoading[i]=false
        }
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
    downloadFile(row,index)
    {
      if (this.$store.state.isLogged === false)
      {
        this.$router.push("/login");
        this.$message.error("请先登录");
        return;
      }
      this.$notify({
        title: '警告',
        message: '下载过程中请勿在此网页做其他操作,请耐心等待',
        type: 'warning',
        position: 'top-left'
      });
      row.downloadnum+=1;
      this.isDownLoading[index] = true;
      this.axios.get(row.loc,
        {responseType:'blob', onDownloadProgress: (evt) => {
            // 有时候拿不到total值，这个值总是为0
            //只要设置后台的代码  response.setContentLengthLong(文件长度); 就可以了
            this.percentage = parseInt((evt.loaded / evt.total) * 100);
            if(this.percentage===100){
              this.isDownLoading[index]= false;
              this.$message.success("下载成功!") ;
              console.log(row);
              this.axios.post("https://healthkeeper.top:8080/download",this.$qs.stringify({fid:row.id}),{headers:{'authorization':this.$store.state.token}})
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
    rTime(date) {
      var json_date = new Date(date).toJSON();
      return new Date(+new Date(json_date) + 8 * 3600 * 1000).toISOString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '')
    },
    exactPage(currentpage) {
      this.index.page=currentpage
      this.curPage = this.resultList.slice((currentpage - 1) * 10 , (currentpage * 10) < this.index.all ? (currentpage * 10) : this.index.all);
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
            this.resultList.splice((this.index.page-1)*10+index,1);
            this.exactPage(this.index.page)
            if(this.curPage.length===0)
            {
              this.index.page-=1;
              this.exactPage(this.index.page)
            }
            this.index.all=this.resultList.length;
          }else {
            this.$message.error("取消收藏失败!")
          }
        }).catch((e)=>
      {
        this.$message.error("取消收藏失败! 服务器未响应请求");
      })
    },
    deleteFile(row, index){
      let url = row.loc.replace("http://easypalace.oss-cn-chengdu.aliyuncs.com/","");


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
    menuPlayer(){
      return false;
    },
    formatter(row,index){
      if (row.type === true)
        return "私有"
      else return "公开"
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
#box3{
  width: 690px;
  height: 230px;
  border-radius: 5px;
  border: 1px solid #EDEDED;
}

#blogBox{
  border-radius: 10px;
  border: 1px dashed #409EFF;
  width: 1000px;
  height: 600px;
  overflow-y: auto;
  position: absolute;
  left: 260px;
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
