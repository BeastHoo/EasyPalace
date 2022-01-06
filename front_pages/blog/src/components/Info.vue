<template>
  <div class="mainContainer">
    <br/>
    <p style="font-size: xx-large;color: violet">基于VUE ELEMENT-UI SpringBoot+Mysql的一个小网站</p>

    <h1 style="color: greenyellow">WeBlog</h1>
    <h3 STYLE="color: darkturquoise">这是一个博客系统, 用户注册登录之后, 可以收藏他人博客, 点赞, 评论等</h3>
    <br/>

    <h1 style="color: cornflowerblue">OurSeelf</h1>
    <h3 STYLE="color: blanchedalmond">这是一个云盘空间, 用户注册登录之后, 可以收藏公共区域的文件, 下载所有文件, 删除自己的文件</h3>
    <h3 STYLE="color: blanchedalmond">上传文件时, 存在文件大小限制, 单个文件提交时不可大于200MB, 多个文件提交时大小不可大于500MB</h3>
    <h3 STYLE="color: blanchedalmond">同时存在权限设置, 可以设置为公共或者私有, 顾名思义, 公共文件可以被所有使用此网站的用户查看, 收藏, 下载</h3>
    <h3 STYLE="color: blanchedalmond">文件的在线查看功能只支持.JPG, .PNG, .ICO, .MP3, .WAV</h3>
    <h3 STYLE="color: blanchedalmond">服务器带宽有限, 大概1-2Mbps左右, 所以建议如无必要的话不要在线查看视频</h3>
    <br/>
    <br/>
    <span style="opacity: .54;font-size: 12px;color: #fff;display: inline-block;">2021&nbsp;©BeastHoo</span>
  </div>
</template>

<script>
export default {
  name: "Info"
}
</script>

<style scoped>
.mainContainer{
  overflow-y: auto;
  background-image: url("https://easypalace.oss-cn-chengdu.aliyuncs.com/projectfile/infoBg.jpg");
  background-size: cover;
  height: 89vh;
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
}
</style>
