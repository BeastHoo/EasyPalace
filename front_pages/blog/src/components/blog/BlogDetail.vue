<template>
  <div id="maincontainer">
    <div id="sideBox">
       <Aside/>
    </div>


  <div id="all" >
    <div id="blogBox">
      <p id="btitle">{{blog.title}}</p>
      <div id="box4" >
        <p>{{blog.editTime}}&nbsp;<router-link :to="{path: '/userinfo',query: {id: blog.editor} }">{{blog.editor}}</router-link></p>
      </div><viewer>
      <div id="contentBox">

          <article class="markdown-body"  v-html="blog.content" v-highlight></article>


      </div></viewer>
    </div>

    <div id="box6">
      <el-rate v-model="value" allow-half />
    </div>
    <div id="vcomments">

    </div>
  </div>

  </div>


</template>

<script>

import Aside from "./Aside";
import hljs from 'highlight.js'
//highlight.js 的渲染风格
import "highlight.js/styles/stackoverflow-light.css";
import mavonEditor from "mavon-editor";
// hljs.highlightCode = function () {
//   //自定义highlightCode方法，将只执行一次的逻辑去掉
//   let blocks = document.querySelectorAll('pre code');
//   [].forEach.call(blocks, hljs.highlightBlock);
// };
export default {

  name: "BlogDetails",
  components: {Aside},
  watch: {
    '$route'(to, from) { //监听路由是否变化
      if (to.query.bid !== from.query.bid) {
        this.fetchData();//重新加载数据
        this.createValine()
      }
      hljs.highlightAll()
      // hljs.highlightCode()
    }
  },
  mounted: function () {
    if (this.$route.query) {
      // this.id = this.$route.query.id;
      this.fetchData();
      this.createValine();
    }
    hljs.highlightAll()
    // hljs.highlightCode()
  },
  data() {
    return {
      blog: {},
      textarea1: "",
      textarea2: "",
      value: 5,
      bid: 0
    }
  }, methods: {
    fetchData() {
      this.bid = this.$route.query.bid;

      this.axios.post("https://healthkeeper.top:8080/blog/getblog", this.$qs.stringify({bid: this.bid})).then(res => {
        this.blog = res.data
        let md =  mavonEditor.mavonEditor.getMarkdownIt()
        let result = md.render(this.blog.content);

        this.blog.content =result

        this.blog.content = this.blog.content.replace(/<img/g, "<img style='max-width:60%;height:auto;'");

      }).catch(e => {
        this.$message.error("出错了喔~记得刷新试一下~")
      })
    }, refreshPage() {

    },
    createValine() {
      const Valine = require('valine');
      window.AV = require('leancloud-storage')
      const valine = new Valine({
        el: '#vcomments',
        appId: 'AroJ6CVzyRrMCYXhEMokJw3M-gzGzoHsz',
        appKey: 'kBDiwrk0iOWIxUycIiYqgOGC',
        notify: false,
        verify: false,
        avatar: "retro",
        path: "blogId:"+this.$route.query.bid,
        placeholder: '欢迎留言分享您的想法...',
      });

    }
  },

}
</script>

<style scoped>



#btitle{
  font-size: 25px;
  color: #409EFF;
  font-weight: bold;
}


#vcomments {
  width: 100%;
  background: white;
  position: relative;
  left: 0%;
  top: 0px;
}

#box4{
  font-size: 13px;
  position: relative;
  top: -15px;
  height: 0px;
  color: #99a9bf;
  text-align: center;
}

#box6{
  text-align: left;
  position: relative;
  top: 30px;
  left: 30px;
}
#all{
  position: absolute;
  top: 40px;
  left: 300px;
  border: 1px solid #CFCFCF;
  width: 1000px;
  height: 900px;
  background: rgba(187 ,255 ,255, .1);
}

#sideBox{
  display: flex
}

#blogBox{
  height: 100%;overflow: auto
}

#contentBox{
  margin-top: 5px;text-align:left;padding-left: 10px
}

#maincontainer{
  position: absolute;
  width: 100vh;
  top: 0px;
}
</style>
