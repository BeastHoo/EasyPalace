<!--管理里面的热门推荐界面 推荐书籍-->
<template>

  <div id="box">


    <div style="margin: 20px 0">
      <!--  功能区域  -->
      <div style="margin: 10px 0">
        <el-button type="primary" @click="add">新增</el-button>
        <el-button type="primary" @click="load">查询</el-button>
      </div>
      <!--  搜索区域  -->
      <div>
        <el-input v-model="search" placeholder="Please input" style="width: 20%;" clearable/>
      </div>
      <el-table :data="tableData" border style="width: 100%" stripe>
        <el-table-column prop="name" label="名称" width="280" />
        <el-table-column prop="author" label="作者" width="280" />
        <el-table-column prop="createTime" label="出版时间" width="400"/>
        <el-table-column prop="right" label="Operations" width="120">
          <template #default="scope">
            <el-popconfirm
                confirm-button-text="Yes"
                cancel-button-text="No, Thanks"
                :icon="InfoFilled"
                icon-color="red"
                title="确认删除吗?"
                @confirm="handleDelete(scope.row.id)"
            >
              <template #reference>
                <el-button type="text" size="small">删除</el-button>
              </template>
            </el-popconfirm>

            <el-button type="text" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin: 20px 0">
        <el-pagination small layout="prev, pager, next" :total="50"> </el-pagination>

        <el-button type="text" @click="dialogVisible = true"
        ></el-button
        >
        <el-dialog
            v-model="dialogVisible"
            title="Tips"
            width="30%">
          <el-form :model="form" label-width="120px">
            <el-form-item label="名称">
              <el-input v-model="form.name" style="width: 80%;"></el-input>
            </el-form-item>
            <el-form-item label="作者">
              <el-input v-model="form.author" style="width: 80%;"></el-input>
            </el-form-item>
            <el-form-item label="出版时间">
              <el-input v-model="form.createTime" value-format="YYYY-MM-DD" type="date" style="width: 80%;" clearable></el-input>
            </el-form-item>


          </el-form>
          <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">确定</el-button
        >
      </span>
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: "Book",
  data() {
    return {
      search:'',
      form:{},
      dialogVisible:false,
      tableData: [

      ],
    }
  },
  created() {
    this.load()
  },
  methods:{
    load(){
      request.get("/api/book", {
        params:{
          pageNum:this.currentPage,
          pageSize:this.pageSize,
          search:this.search
        }
      }).then(res =>{
        console.log(res)
        this.tableData=res.data.records
      })
    },
    add(){
      this.dialogVisible=true
      this.form={}
    },
    save() {
      if(this.form.id){  //更新
        request.put("/api/book",this.form).then(res =>{
          console.log(res)
          if(res.code==='0'){
            this.$message({
              type:"success",
              message:"更新成功"
            })
          }else {
            this.$message({
              type:"error",
              message:res.msg
            })
          }
          this.load()
          this.dialogVisible=false
        })

      }else{  //新增
        request.post("/api/book",this.form).then(res => {
          console.log(res)
          if(res.code==='0'){
            this.$message({
              type:"success",
              message:"新增成功"
            })
          }else {
            this.$message({
              type:"error",
              message:res.msg
            })
          }
          this.load()
          this.dialogVisible=false
        })
      }

    },
    handleEdit(row) {
      this.form=JSON.parse(JSON.stringify(row))
      this.dialogVisible=true
    },
    handleDelete(id){
      console.log(id)
      request.delete("/api/book/"+id).then(res =>{
        if(res.code==='0'){
          this.$message({
            type: "success",
            message: "删除成功"
          })
        }else {
          this.$message({
            type: "error",
            message: res.msg
          })
        }
        this.load()  //删除之后重新加载表格的数据
      })
    },
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
