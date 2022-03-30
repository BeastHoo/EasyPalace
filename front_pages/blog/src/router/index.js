import Vue from 'vue'
import Router from 'vue-router'
import Info from "../components/Info";
import Home from "../components/blog/Home";
import Login from "../components/Login";
import OurSeelfPrivate from "../components/OurSeelf/OurSeelfPrivate";
import OurSeelfPublic from "../components/OurSeelf/OurSeelfPublic";
import MessagePage from "../components/MessagePage";
import ResultPage from "../components/ResultPage";
import Certificate from "../components/Certificate";
import BlogDetail from "../components/blog/BlogDetail";
import Manage from "../components/blog/Manage";
import NewBlog from "../components/blog/NewBlog";
import Time from "../components/blog/Time";
import Setting from "../components/Setting";
import Book from "../components/blog/Book";
import EditorInfo from "../components/EditorInfo";
// import GlobeChess from "../../static/GlobeChess/index.html"
// import plane from "../components/plane";
Vue.use(Router)


//获取原型对象上的push函数
const originalPush = Router.prototype.push
//修改原型对象中的push方法
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

export default new Router({
  routes: [
    {path: '/info', component: Info},
    {path: '/blog',component: Home},
    {path: '/login',component: Login},
    {path: '/ourseelfpub', component: OurSeelfPublic},
    {path: '/ourseelfpri', component: OurSeelfPrivate},
    {path: '/msg', component: MessagePage},
    {path: '/result',component: ResultPage},
    {path: '/certificate', component:Certificate},
    {path: '/blogdetails', component:BlogDetail},
    {path: '/manage', component:Manage},
    {path: '/news', component:NewBlog},
    {path: '/time', component:Time},
    {path: '/setting', component:Setting},
    {path: '/book', component: Book},
    {path: '/userinfo', component: EditorInfo},
    // {path: '/static/GlobeChess', component: GlobeChess},
    // {path: '/plane', component: plane},
  ]
})
