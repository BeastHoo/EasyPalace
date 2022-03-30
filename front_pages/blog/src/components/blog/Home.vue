<template>

  <div id="maincontainer">

    <div id="sideBox">
      <Aside/>
    </div>


  <div id="box2">
    <p>EasyPalace</p>
  </div>

    <div id="box_slogan">
      <span ref="slogan">迷失的人迷失了，相逢的人会再相逢</span>
    </div>

  <div id="box">
    <div id="box1">
        <el-carousel  indicator-position="outside">
          <el-carousel-item v-for="item in carolselList" :key="item.blogId">
            <router-link :to="{path: '/blogdetails',query: {bid: item.blogId} }" >
            <img v-if="item.imgUrl" :src="item.imgUrl" style="width: 100%;height: 100%;border-radius: 10px"  />
            <h1 style="z-index: 1">{{item.title}}</h1>
            <p >{{  item.title}}</p>
          </router-link>
          </el-carousel-item>
        </el-carousel>
    </div>
  </div>

    <div id="blog_list_box" >
        <ul id="box3"  v-for="(blog_item,i) in curPage" :key="blog_item.blogId">
          <li @click="look(blog_item.blogId)">
            <el-image v-if="blog_item.imgUrl !== ''" style="position:absolute;text-align: left;width: 230px;
            height: 220px;left: 10px;border-radius: 5px;  margin-top: -12px;"
                       :src="blog_item.imgUrl" alt="封面" lazy>
              <div slot="placeholder" class="image-slot">
                加载中<span class="dot">...</span>
              </div>
            </el-image>
            <img v-else id="ava" src="../../assets/b1.jpg" alt="">
            <div id="title_box" >
              <h2 >{{blog_item.title}}</h2>
            </div>

            <div id="desc_box">
              <p>{{blog_item.description}}</p>
            </div>
            <div id="line_box">
              <hr>
            </div>
            <div id="et_box" >
              <small>{{blog_item.editTime}}&nbsp;{{blog_item.editor}}</small>
            </div>
            <div id="rank_box">
              <img src="../../assets/click.png" />
              <small>{{blog_item.clickRate}}</small>
            </div>
          </li>
        </ul>
    </div>

  <div id="pagee">
    <el-pagination
      background
      layout="prev, pager, next"
      :total="index.all"
      :page-size="index.page_cnt"
      @current-change="exactPage">
    </el-pagination>
  </div>

  <div id="box6">

    <h2>热门文章</h2>
    <div id="emt">

    </div>
    <ul id="wz"  v-for="(blog_item,i) in blogInfos.slice(0,this.blogInfos.length>5?5:this.blogInfos.length)" :key="blog_item.blogId">
      <li @click="look(blog_item.blogId)">
        <el-image v-if="blog_item.imgUrl !== ''" style="position:absolute;text-align: left;margin-top: -10px;width: 60px;
            height: 60px;left: 10px;border-radius: 5px;"  :src="blog_item.imgUrl" alt="封面" lazy >
          <div slot="placeholder" class="image-slot">
            加载中<span class="dot">...</span>
          </div>
        </el-image>
        <img v-else  src="../../assets/b1.jpg" alt="">
        <p>{{blog_item.title}}</p>
        <div id="wz_line" style="">
          <hr>
        </div>
      </li>
    </ul>



  </div>

  <div id="box7">
    <p>博客信息</p>
    <span class="b1">文章数目</span>
    <span class="b2">评论数目</span>
    <span class="b3">运行天数</span>
    <span class="b4">最后活动</span>
    <span class="c1">{{blogInfos.length}}</span>
    <span class="c2">1253</span>
    <span class="c3">一个月</span>
    <span class="c4">3天前</span>

  </div>

  <div id="box8">
    <el-row>
      <el-col :span="20">
        <p style="margin-left: 15%">标签</p>
      </el-col>
      <el-col :span="4">
        <img style="width: 40%;height: 40%;margin-top: 40%" src="../../assets/refresh.png" alt="刷新" :class="[rotate?  'go' : 'aa']" @click="start">
      </el-col>
    </el-row>
    <div v-loading="loading">
      <el-tag
        :key="tag.tagId"
        type="info"
        @click="queryBytags(tag)"
        v-for="tag in tags">
        {{tag.tagName}}
      </el-tag>
    </div>

  </div>
  </div>
</template>
<script>
// @ is an alias to /src

import Aside from "./Aside";

export default {
  name: 'home',
  components: {
    Aside
  },
  mounted:function () {
    this.queryBlogs();
    this.queryTags();
    this.setPage();
  },
  watch:{
    '$store.state.isLogged'(){
      let isLogged = this.$store.state.isLogged
      if (isLogged === true)
      {
        this.setPage()
      }
    }
  },
  data(){
    return{
      loading:false,
      tags: [],
      rotate:false,
      blogInfos:[],
      carolselList:[],
      curPage:[],
      index:{
        all:10,
        page_cnt:4
      }
    }
  },methods:{
    queryBlogs(){
      this.axios.get("https://healthkeeper.top:8080/blog/hotBlogs").then(res =>{
        this.blogInfos = res.data
        let j,len;
        this.carolselList = res.data.slice(0,4)

        for(j = 0,len=this.carolselList.length; j < len; j++) {
          if (this.carolselList[j].imgUrl)
            this.carolselList[j].imgUrl = this.carolselList[j].imgUrl + "?timestamp=" + Math.floor(Math.random() * (100));
        }

        this.index.all = this.blogInfos.length
        this.curPage=this.blogInfos.slice(0,this.blogInfos.length>4?4:this.blogInfos.length)
      }).catch(e =>{
        this.$message.error("粗错啦~刷新一下44吧!");
      })
    },
    queryTags(){
      this.axios.get("https://healthkeeper.top:8080/tag/getTags").then(res =>{
        this.tags = res.data
      }).catch(e =>{
        this.$message.error("粗错啦~刷新一下44吧!");
      })
    },
    //查看博客的逻辑
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
    exactPage(currentpage) {
      this.index.page=currentpage
      this.curPage = this.blogInfos.slice((currentpage - 1) * 4 , (currentpage * 4) < this.index.all ? (currentpage * 4) : this.index.all);
    },
    queryBytags(tag){
      this.axios.post("https://healthkeeper.top:8080/search/blogbytag",this.$qs.stringify({tagId:tag.tagId}))
      .then(res =>{
        if (res.data.length === 0)
        {
          this.$message.error("未查询到包含该标签的博客")
        }
        else
        {
          this.$router.push(
            {
              path:"/result",
              query:{
                resultList:res.data,
                searchmode:false,
                keyword:tag.tagName
              }
            }
          );
        }

      }).catch(e=>{
        this.$message.error("搜索时发生了错误")
      })
    },
    setPage(){
      if (this.$store.state.token !== " ")
      {
        const slo = this.$store.state.slogan
        if (slo === "" || slo === " ")
        {
          this.$refs.slogan.innerText = "暂未设置座右铭,点击”用户信息“进入设置"
        }else {
          this.$refs.slogan.innerText = slo
        }
      }
    },
    start(){
      this.rotate = !this.rotate;
      // this.rotate = !this.rotate;
      this.loading=true;
      this.queryTags();
      setTimeout(()=>{
        this.loading=false;
        this.rotate =! this.rotate
      },2000)


    }
  }


}
</script>

<style scoped>
.aa {

}

.go {
  transform: rotate(360deg);
  transition: all 2s;
}


#wz p{
  white-space:nowrap;
  text-overflow:ellipsis;
  -o-text-overflow:ellipsis;
}

#box2{
  position: absolute;
  top: 0;
  left: 20em;
}
#box2 p{
  font-family: 华文宋体;
  font-size: 30px;
}

#box_slogan
{
  position: absolute;
  top: 0;
  left: 20em;
  text-align: left;
}

#box_slogan span{
  position: absolute;
  top: 70px;
  width: 600px;
  color: #D4D4D4;
}
#box{
  position: absolute;
  top: 100px;
  left: 20em;
  width: 46em;
  height: 10%;
  /*border: 1px solid #EDEDED;*/
  /*border-radius: 10px;*/
}
#box1 h1{
  font-family: 黑体;
  color: white;
  position: absolute;
  top:160px;
  padding: 10px;
}
#box1 p{
  font-family: 黑体;
  color: white;
  position: absolute;
  top:220px;
  padding: 10px;
}
#box1{
  border-radius: 10px;
  position: absolute;
  width: 100%;
  height: 300px;
  background-image: url("../../assets/a1.jpg");
  background-size: cover;
  border: 1px solid #EDEDED;
}
/*.el-carousel__item h3 {*/
/*  color: #475669;*/
/*  font-size: 18px;*/
/*  opacity: 0.75;*/
/*  line-height: 300px;*/
/*  margin: 0;*/
/*  text-align: center;*/
/*}*/

/*.el-carousel__item:nth-child(n) {*/
/*  position: absolute;*/
/*  width: 700px;*/
/*  height: 280px;*/
/*  border-radius:5px;*/
/*  background-image: url("../../assets/a1.jpg");*/
/*  border: 1px solid #EDEDED;*/
/*  background-size:100% 100%;*/
/*}*/

/*.el-carousel__item:nth-child(2n) {*/
/*  position: absolute;*/
/*  width: 700px;*/
/*  height: 280px;*/
/*  border-radius:5px;*/
/*  background-image: url("../../assets/a5.jpg");*/
/*  border: 1px solid #EDEDED;*/
/*  background-size:100% 100%;*/
/*}*/
/*.el-carousel__item:nth-child(2n+1) {*/
/*  position: absolute;*/
/*  width: 700px;*/
/*  height: 280px;*/
/*  border-radius:5px;*/
/*  background-image: url("../../assets/a12.jpg");*/
/*  border: 1px solid #EDEDED;*/
/*  background-size:100% 100%;*/
/*}*/
/*.el-carousel__item:nth-child(3n) {*/
/*  position: absolute;*/
/*  width: 700px;*/
/*  height: 280px;*/
/*  border-radius:5px;*/
/*  background-image: url("../../assets/a1.jpg");*/
/*  border: 1px solid #EDEDED;*/
/*  background-size:100% 100%;*/
/*}*/
#box3{
  width: 100%;
  height: 14.5em;
  border-radius: 5px;
  border: 1px solid #EDEDED;
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
  width: 100%;
  /*top:20px;*/
  font-size: 1.4em;
}
#box3 p{
  word-break: break-all;
  word-wrap: break-word;
  overflow-wrap: break-word;

  text-align: left;
  font-family: 黑体;
  color: #99a9bf;
  line-height:2em
}
#box3 hr{
  width: 100%;
  border: 1px solid #EDEDED;
}
#box3 small{
  color: #99a9bf;
}


#box6{
  position: absolute;
  top: 100px;
  left: 67.8em;
  border: 1px solid #EDEDED;
  width: 19em;
  height: 31.5em;
}
#box6 h2{
  font-size: 20px;
  position: absolute;
  top: 5px;
  left: 15px;
}
#wz{
  position: relative;
  top: 35px;
  border: 1px solid white;
  width: 260px;
  height: 75px;
}


#wz p{
  text-align: left;
  position: relative;
  left: 40px;
  top: 12px;
  width: 200px;
  height: 40px;
  font-size: 15px;
  color: #AAAAAA;
  overflow: hidden;
}

ul{
  list-style-type: none;
}

#wz img{
  position: absolute;
  left: 0px;
  width: 60px;
  height: 60px;
  background-size: 100% 100%;
  top: 10px;
  border-radius: 5px;
  left: 10px;
}


#box7{
  position: absolute;
  top: 610px;
  left: 67.8em;
  border: 1px solid #EDEDED;
  width: 19em;
  height: 19em;
}
#box7 p{
  position: absolute;
  top: 5px;
  left: 30px;
  font-size: 20px;
}
#box7 .b1{
  position: absolute;
  top: 80px;
  left: 20px;
  color: #AAAAAA;
}
#box7 .b2{
  position: absolute;
  top: 130px;
  left: 20px;
  color: #AAAAAA;
}
#box7 .b3{
  position: absolute;
  top: 180px;
  left: 20px;
  color: #AAAAAA;
}
#box7 .b4{
  position: absolute;
  top: 230px;
  left: 20px;
  color: #AAAAAA;
}
#box7 .c1{
  position: absolute;
  top: 80px;
  left: 170px;
  color: #AAAAAA;
}
#box7 .c2{
  position: absolute;
  top: 130px;
  left: 170px;
  color: #AAAAAA;
}
#box7 .c3{
  position: absolute;
  top: 180px;
  left: 170px;
  color: #AAAAAA;
}
#box7 .c4{
  position: absolute;
  top: 230px;
  left: 170px;
  color: #AAAAAA;
}
#box8{
  position: absolute;
  top: 920px;
  left: 67.8em;
  border: 1px solid #EDEDED;
  width: 19em;
  height: 18em;
}

#box8 span{
  margin: 8px;
  height: 30px;
}

#sideBox{
  display: flex;

}

#blog_list_box{
  height: 100%;
  width: 44em;
  position: absolute;
  top:410px;
  left: 19.8em;
}

#coverBox{
  position:absolute;
  text-align: left;
  margin-top: -12px;
  width: 230px;
  height: 220px;
  left: 10px;
  border-radius: 5px;
  z-index: 1;
}


#title_box{
  position: relative;
  left: 220px;
  width: 30em;
  height: 1.2em
}

#et_box{
  position: relative;
  left: 14em;
  width: 26em;
  text-align: left
}
#line_box{
  position: relative;
  left: 14em;
  width: 28em
}
#desc_box{
  position: relative;
  left: 14em;
  width: 29.6em;
  height: 9.4em;
  overflow-y:hidden;
}

#rank_box{
  position: relative;
  left:20em;
  top: -1.2em;
  display: inline-block;
  width: 7.2em;
  height: 2em;
  text-align: left
}

#rank_box img{
  width: 1.2em;
  height: 1.2em;
}

#rank_box small{
  position: relative;
  top: -0.4em
}

#pagee{
  position: absolute;
  top: 1420px;
  left: 520px;
  height: 100px;
}

#emt{
  height: 4px;
}

#wz_line{
  text-align: left;
  position: relative;
  top: -10px;
  left: 40px;
  width: 70%;
}

#wz_line hr{
  width: 100%;
  border: 1px solid #EDEDED;
}

#maincontainer{
  position: absolute;
  width: 99%;
}
</style>

