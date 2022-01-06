<template>
<div>
  <h3 style="color: aquamarine;background: #B3C0D1">公共文件</h3>
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
            <el-button v-if="scope.row.iscollected !== true" type="text" size="small" @click="collectFile(scope.row)"
                       icon="el-icon-star-off" >收藏</el-button>

            <el-tooltip effect="dark" content="取消收藏" placement="top-start">
              <el-button v-if="scope.row.iscollected === true" type="text" size="small" @click="cancelCollectFile(scope.row)"
                         icon="el-icon-star-on">已收藏</el-button>
            </el-tooltip>
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

export default {
  mounted:function (){
    this.querydatas();
    for (let i=0; i<20; i++)
    {
      this.isDownLoading[i]=false
    }
  },
  name: "OurSeelfPublic",
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
      index:{
        all:10
      },
      percentage:0,
      isDownLoading:[]

    }
  },
  methods:{
    querydatas()
    {
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      this.axios.post("https://healthkeeper.top:8080/pub/files",null,
        {headers: {'authorization': this.$store.state.token}}).then(res=>
      {
        this.tabledata=res.data;
        this.index.all=res.data.length;
        this.curPage=this.tabledata.slice(0,this.tabledata.length>10?10:this.tabledata.length)
        loading.close();
      }).catch((e)=>
      {
         loading.close();
         this.$message.error("未获取到云盘信息，请重试");
      })
    },
    play(row)
    {
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
    collectFile(row)
    {
      this.axios.post("https://healthkeeper.top:8080/pub/collect",this.$qs.stringify({fid: row.id,username:row.username}),
        {headers: {'authorization': this.$store.state.token}})
        .then(res=>{
          if (res.data.status === 1)
          {
            this.$message.success("收藏成功!")
            row.collectnum+=1;
            row.iscollected=!row.iscollected
          }else {
            this.$message.error(res.data.message)
          }
        }).catch((e)=>
        {
          this.$message.error("收藏失败! 服务器未响应请求");
        })
    },
    exactPage(currentpage){
      this.curPage=this.tabledata.slice((currentpage-1)*10,(currentpage*10)<this.index.all?(currentpage*10):this.index.all);
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
    cancelCollectFile(row)
    {
      this.axios.post("https://healthkeeper.top:8080/pub/cancelcollect",this.$qs.stringify({fid:row.id,cid: row.cid}),
        {headers: {'authorization': this.$store.state.token}})
        .then(res=>{
          if (res.data.status === 1)
          {
            this.$message.success("取消收藏!");
            row.collectnum-=1;
            row.iscollected=!row.iscollected
          }else {
            this.$message.error("取消收藏失败!")
          }
        }).catch((e)=>
      {
        this.$message.error("取消收藏失败! 服务器未响应请求");
      })
    },
    menuPlayer(){
      return false;
    }
  }
}
</script>

<style scoped>

</style>
