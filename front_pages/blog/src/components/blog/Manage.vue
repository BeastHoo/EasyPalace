<template>
  <div id="box">


    <div style="margin: 20px 0">
      <!--  功能区域  -->

      <!--  搜索区域  -->
      <div>
        <el-autocomplete
          class="inline-input"
          v-model="search"
          value-key="title"
          :fetch-suggestions="querySearch"
          placeholder="请输入内容"
          :trigger-on-focus="false"
          @select="handleSelect"
          @change="refresh"
          clearable
        ></el-autocomplete>
      </div>
      <el-table :data="curPage" stripe style="width: 100%">
        <el-table-column prop="editTime" label="Date" width="380" />
        <el-table-column prop="title" label="Name" width="480" />
        <el-table-column prop="right" label="Operations" width="200px">
          <template slot-scope="scope" >
            <el-button type="text" size="small" @click="openConfirm(scope.row,scope.$index)">删除</el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 20px 0">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="index.all"
          :page-size="index.page_cnt"
          @current-change="exactPage">
        </el-pagination>      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Manage",
  mounted:function () {

    if (this.$store.state.isLogged === false)
    {
      this.$message.error("请先登录!");
      this.$router.push("/login");

    }
    else
    {
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      this.queryData();
      loading.close()
    }

  },
  data() {
    return {
      tableData: [],
      curPage:[],
      search:"",
      index:{
        all:5,
        page_cnt:5,
        page:1
      }
    }
  },
  methods: {
    queryData() {
      this.axios.post("https://healthkeeper.top:8080/myblog/getmyblogs", null, {headers: {'authorization': this.$store.state.token}})
        .then(res => {
          this.tableData = res.data;
          this.index.all = this.tableData.length
          this.curPage = this.tableData.slice(0, this.index.all > this.index.page_cnt ? this.index.page_cnt : this.index.all)

        }).catch(e => {
        if (e.response.status === 401) {
          this.$message.error(e.response.data.message)
          this.$router.push("/login")
        } else
          this.$message.error("出错了哦，刷新一下试试吧")
      })
    },
    exactPage(currentpage) {
      this.index.page = currentpage
      this.curPage = this.tableData.slice((currentpage - 1) * this.index.page_cnt, (currentpage * this.index.page_cnt) < this.index.all ? (currentpage * this.index.page_cnt) : this.index.all);
    }
    , handleClick(row, index) {
      this.axios.post("https://healthkeeper.top:8080/myblog/delBlog", this.$qs.stringify({blog_id: row.blogId})
        , {headers: {"authorization": this.$store.state.token}})
        .then(res => {
          this.$message.success("删除成功")
          if (res.data.status === 1) {
            this.curPage.splice(index, 1);
            this.tableData.splice((this.index.page - 1) * this.index.page_cnt + index, 1);
            this.exactPage(this.index.page)
            if (this.curPage.length === 0) {
              this.index.page -= 1;
              this.exactPage(this.index.page)
            }
            this.index.all = this.tableData.length;
          } else {
            this.$message.error(res.data.message)
          }
        })
    },
    querySearch(queryString, cb) {
      var restaurants = this.tableData;
      var results = queryString ? restaurants.filter(this.createFilter(queryString)) : restaurants;
      // 调用 callback 返回建议列表的数据
      cb(results);
    },
    createFilter(queryString) {
      return (restaurant) => {
        return (restaurant.title.toLowerCase().indexOf(queryString.toLowerCase()) === 0);      };
    },
    handleSelect(item){
      this.index.all=1
      this.curPage.splice(0,this.curPage.length)
      this.curPage.push(item)
    },
    refresh(){
      if (this.search === "")
      {
        this.index.all = this.tableData.length
        this.curPage = this.tableData.slice(0, this.index.all > this.index.page_cnt ? this.index.page_cnt : this.index.all)
      }
    },
    handleEdit(row){
      this.$router.push(
        {
          path:"/news",
          query:{
            blog_id:row.blogId
          }
        });
    },
    openConfirm(row, index) {
      this.$confirm('此操作将永久删除该博客, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.handleClick(row,index)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });
    }
  }

}
</script>

<style scoped>
#box{
  position: absolute;
  top: 100px;
  left: 220px;
}
</style>
