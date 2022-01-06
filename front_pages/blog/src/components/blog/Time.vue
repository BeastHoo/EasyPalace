<!--导航里面的日历界面-->
<template>
  <div>
    <Aside/>

  <div id="box">

    <el-calendar>
      <!-- 这里使用的是 2.5 slot 语法，对于新项目请使用 2.6 slot 语法-->
      <template
        slot="dateCell"
        slot-scope="{date, data}">
        <p :class="data.isSelected ? 'is-selected' : ''">
          {{ data.day.split('-').slice(1).join('/') }} {{ data.isSelected ? '✔️' : ''}}
        </p>
      </template>
    </el-calendar>
  </div>
  </div>
</template>

<script>
import Aside from "./Aside";
export default {
  name: "Time",
  components: {Aside}
}


</script>

<style scoped>
#box{
  position: absolute;
  top: 100px;
  left: 200px;
}

.is-selected {
  color: #1989FA;
}
</style>
