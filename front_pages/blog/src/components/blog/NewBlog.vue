<template>
<div >
  <el-dialog title="选择标签" :visible.sync="dialogShow"  @close="destroyDialog()">
    <div id="tag_box" >
    <el-tag
      :key="tag.tagId"
      v-for="tag in dynamicTags"
      closable
      :disable-transitions="false"
      @close="handleClose(tag)">
      {{tag.tagName}}
    </el-tag>
    <el-input
      class="input-new-tag"
      v-if="inputVisible"
      v-model="inputValue"
      ref="saveTagInput"
      size="small"
      @keyup.enter.native="handleInputConfirm"
      @blur="handleInputConfirm"
    >
    </el-input>
    <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Tag</el-button>
    </div>
    <div style="margin-top: 20px">
      <span slot="footer" class="dialog-footer" >
        <el-button type="success" @click="uploadBlog">完成</el-button>
        </span>
    </div>

  </el-dialog>


  <div style="text-align: left;width: 60%;margin-left: 10%;margin-top: 22px;margin-bottom: 10px">
    <el-form>
      <el-row>
        <el-col :span="20">
          <el-row>
            <el-col :span="20">
              <el-form-item :error="this.titleError">
                <el-input
                  ref="titleArea"
                  @blur="onTitleChange()"
                  placeholder="请输入标题"
                  maxlength="30" show-word-limit
                  v-model="blogInfo.title">
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <div style="position: relative;left:10%;top: 19%;color: #CFCFCF;font-size: 16px;padding-top: 10px">博客封面图:</div>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item :error="this.descError">
                <el-input
                  ref="descArea"
                  @change="onDescChange()"
                  type="textarea"
                  style="margin-top: 30px"
                  placeholder="请输入简介"
                  maxlength="130" show-word-limit
                  v-model="blogInfo.description">
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-col>




        <el-col :span="4">
          <div style="margin-left: 10%">
            <el-upload
              class="avatar-uploader"
              :disabled="isNotAllowed"
              action=""
              :http-request="aliOSSUpload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess">
              <img v-if="imageUrl" :src="imageUrl" class="avatar">
              <div v-else>
                <i class="el-icon-plus avatar-uploader-icon"></i>
                <div class="notice">请上传博客封面</div>
              </div>
            </el-upload>
          </div>
        </el-col>
      </el-row>
    </el-form>
  </div>


  <div style="float: left;position: relative;text-align: left;width: 0px;top: -70px;left: 85%">
    <el-button type="primary" @click="showDialog">提交</el-button>
  </div>
  <div style="margin-left: 10%;margin-bottom: 20px;margin-top: 30px;width: 80%">
    <mavon-editor
      :toolbars="toolbars"
      @imgAdd="handleEditorImgAdd"
      @imgDel="handleEditorImgDel"
      style="height:600px"
      v-model="value"
      @change="change"
      ref=md
    />
  </div>
</div>

</template>

<script>
import { uuid } from 'vue-uuid';
const OSS = require('ali-oss');
export default {
  name: "NewBlog",
  watch: {
    '$route'(to, from) { //监听路由是否变化
      if (to.query.blog_id !== from.query.blog_id) {
        this.queryBlog()//重新加载数据
      }
    }
  },
  mounted:function () {
    if (this.$store.state.isLogged === false)
    {
      this.$message.error("请先登录!");
      this.$router.push("/login");
    }
    else {
      this.client = new OSS({
        accessKeyId: this.$store.state.keyId,  // 查看你自己的阿里云KEY
        accessKeySecret: this.$store.state.keySecret, // 查看自己的阿里云KEYSECRET
        stsToken:this.$store.state.stsToken,
        bucket: 'easypalace', // 你的 OSS bucket 名称
        region: 'oss-cn-chengdu', // bucket 所在地址
      });
      if (this.$route.query.blog_id)
      {
        this.queryBlog()
      }
      else this.generateBlogId();
    }
  },
  data() {
    return {
      isNotAllowed:false,
      imageUrl: '',
      titleError:'',
      descError:'',
      isTitleInput:false,
      isDescInput:false,
      blogInfo: {
        blogId:"",
        title: "",
        description: ""
      },

      dialogShow: false,
      value: "",
      dynamicTags: [],
      inputVisible: false,
      inputValue: '',
      toolbars: {
        bold: true, // 粗体
        italic: true, // 斜体
        header: true, // 标题
        underline: true, // 下划线
        strikethrough: true, // 中划线
        mark: true, // 标记
        superscript: true, // 上角标
        subscript: true, // 下角标
        quote: true, // 引用
        ol: true, // 有序列表
        ul: true, // 无序列表
        link: true, // 链接
        imagelink: true, // 图片链接
        code: false, // code
        table: true, // 表格
        fullscreen: true, // 全屏编辑
        readmodel: true, // 沉浸式阅读
        htmlcode: true, // 展示html源码
        help: true, // 帮助
        /* 1.3.5 */
        undo: true, // 上一步
        redo: true, // 下一步
        trash: true, // 清空
        save: true, // 保存（触发events中的save事件）
        /* 1.4.2 */
        navigation: true, // 导航目录
        /* 2.1.8 */
        alignleft: true, // 左对齐
        aligncenter: true, // 居中
        alignright: true, // 右对齐
        /* 2.2.1 */
        subfield: true, // 单双栏模式
        preview: true, // 预览
      },
      client:null
    }
  },
  methods: {
    queryBlog(){
      let blog_id = this.$route.query.blog_id
      this.isNotAllowed = true
      this.axios.post("https://healthkeeper.top:8080/blog/getblog", this.$qs.stringify({bid: blog_id})).then(res => {
        if (res.data !== "" || res.data !== undefined)
        {
          this.blogInfo.blogId = res.data.blogId
          this.blogInfo.title = res.data.title
          this.blogInfo.blogMdContent = res.data.content
          this.value = res.data.content
        }else {
          this.$message.error("出错了喔~记得刷新试一下~")
        }
      }).catch(e => {
        this.$message.error("出错了喔~记得刷新试一下~")
      });
      this.axios.post("https://healthkeeper.top:8080/blog/getDetails",this.$qs.stringify({bid:blog_id})).then(res => {
        if (res.data !== "" || res.data !== undefined)
        {
          this.blogInfo.description = res.data.description;
          this.imageUrl = res.data.imgUrl;
        }else {
          this.$message.error("出错了喔~记得刷新试一下~")
        }
      })
      this.$alert('再次编辑的博客不可以上传封面', '提示', {
        confirmButtonText: '确定'
      });
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
          if (status === 200)
          {
            this.client.put("file/"+this.$store.state.username+"/blog/"+this.blogInfo.blogId+"/"+fileNames, file).then(res=>{
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
    generateBlogId(){
      this.blogInfo.blogId = uuid.v1().slice(0,8);
      this.axios.post("https://healthkeeper.top:8080/myblog/writebegin",this.$qs.stringify({blogId:this.blogInfo.blogId}),
        {headers:{'authorization':this.$store.state.token}});
    },
    change(value, render) {
      this.blogInfo.blogContent = render;
      this.blogInfo.blogMdContent = value;
      this.value = value
    },
    //上传图片接口pos 表示第几个图片
    handleEditorImgAdd(pos, $file) {
      try {
        let file = $file; // 拿到 file
        let fileName = file.name.substr(0,file.name.lastIndexOf('.'))
        let date = new Date().getTime()
        let fileNames = `${date}_${fileName}` // 拼接文件名，保证唯一，这里使用时间戳+原文件名
        // 上传文件,这里是上传到OSS的 uploads文件夹下

        this.client.put("file/"+this.$store.state.username+"/blog/"+this.blogInfo.blogId+"/"
          +fileNames, file).then(res=>{
          if (res.res.statusCode === 200) {
            this.$refs.md.$img2Url(pos, res.url);  //这里就是引用ref = md 然后调用$img2Url方法即可替换地址
          }else {
            console.log("上传失败")
          }
        })
      }catch (e) {
        options.onError("上传失败")
      }
      /**
       * 已弃用
       */
      // var formdata = new FormData();
      // formdata.append('file', $file);
      // formdata.append('blogId', this.blogInfo.blogId)
      // this.axios
      //   .post("https://healthkeeper.top:8080/myblog/uploadImg", formdata,
      //     {headers: {'authorization': this.$store.state.token}})
      //   .then(res => {
      //     var url = res.data.message;
      //     this.$refs.md.$img2Url(pos, url);  //这里就是引用ref = md 然后调用$img2Url方法即可替换地址
      //
      //   });
    },
    handleEditorImgDel(pos) {
      let url = pos[0].replace("http://easypalace.oss-cn-chengdu.aliyuncs.com/","");
      console.log(url);
      this.client.delete(url);
      /**
       * 已弃用
       */
      // this.axios.post("https://healthkeeper.top:8080/myblog/delImg", this.$qs.stringify({imgUrl: pos[0]}),
      //   {headers: {"authorization": this.$store.state.token}})
      //   .then(res => {
      //     if (res.data.status === 1) {
      //       this.$message.success(res.data.message)
      //     } else {
      //       this.$message.error(res.data.message)
      //     }
      //   })

    },
    showDialog() {
      if (this.isTitleInput !== true || this.isDescInput !== true) {
        if (this.blogInfo.title === "" || this.blogInfo.description === "")
        {
          this.$message.error("请检查是否输入标题与简介")
          return;
        }
      }
      this.dialogShow = true
      this.axios.post("https://healthkeeper.top:8080/tag/getTags", null, {headers: {'authorization': this.$store.state.token}})
        .then(res => {
          this.dynamicTags = res.data
        }).catch(e => {
        if (e.response.status === 401) {
          this.$message.error(e.response.data.message)
          this.$router.push("/login")
        } else
          this.$message.error("出错了哦，刷新一下试试吧")
      })
    },
    destroyDialog() {
      this.dialogShow = false
    },
    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
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
    rTime(date) {
      var json_date = new Date(date).toJSON();
      return new Date(+new Date(json_date) + 8 * 3600 * 1000).toISOString().replace(/T/g, ' ').replace(/\.[\d]{3}Z/, '')
    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },
    handleInputConfirm() {
      let inputValue = this.inputValue;

      if (inputValue) {
        let temp = {
        tagId:uuid.v1().slice(0,6),
        tagName:inputValue
        }
        this.dynamicTags.push(temp);
      }
      this.inputVisible = false;
      this.inputValue = '';
    },
    uploadBlog() {
      if (this.dynamicTags.length>5)
      {
        this.$message.error("最多只能添加五个标签!");
        return;
      }
      else if (this.blogInfo.blogMdContent === "" || this.blogInfo.blogMdContent === undefined)
      {
        this.$message.error("博客内容不能为空");
        return;
      }
      this.axios.post("https://healthkeeper.top:8080/myblog/uploadBlog", this.$qs.stringify({
          content: this.blogInfo.blogMdContent,
          title: this.blogInfo.title,
          tags: this.dynamicTags,
          imgUrl: this.imageUrl,
          description: this.blogInfo.description,
          blogId: this.blogInfo.blogId,
        },{arrayFormat: 'repeat'}),
        {headers: {'authorization': this.$store.state.token}}).then(res => {
        if (res.data.status === 1) {
          this.$message.success(res.data.message)
          this.dialogShow = false
        } else {
          this.$message.error(res.data.message)
        }
      }).catch(e => {
        if (e.response.status === 401) {
          this.$message.error(e.response.data.message)
          this.$router.push("/login")
        } else
          this.$message.error("出错了哦，刷新一下试试吧")
      })
    },
    handleAvatarSuccess(res) {
      this.imageUrl = res.url;
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isPNG = file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 5;

      if (!isJPG && !isPNG) {
        this.$message.error('上传封面图片只能是 JPG 或 PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传封面图片大小不能超过 5MB!');
      }
      return isJPG && isLt2M;
    },
    onTitleChange() {
      if (this.blogInfo.title === "") {
        this.titleError = "请输入标题"
        if (this.isTitleInput === true)
          this.isTitleInput = false
      } else {
        this.titleError = ""
        if (this.isTitleInput === false)
          this.isTitleInput = true
      }
    },
    onDescChange() {
      if (this.blogInfo.description === "") {
        this.descError = "请输入标题"
        if (this.isDescInput === true)
          this.isDescInput = false
      } else {
        this.descError = ""
        if (this.isDescInput === false)
          this.isDescInput = true
      }
    }
  }
}
</script>

<style scoped>
#tag_box{
  width: 80%;
  margin-left: 10%;
}

#tag_box span{
  margin: 8px;
  height: 30px;
}

.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  width: 140px;
  height: 140px;
  overflow: hidden;
}
.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 140px;
  height: 100px;
  line-height: 120px;
  text-align: center;
}

.notice {
  font-size: 12px;
  color: #8c939d;
  width: 140px;
  line-height: 0px;
  text-align: center;
}

.avatar {
  width: 140px;
  height: 140px;
  display: block;
}
</style>
