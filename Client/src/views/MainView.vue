<template>

  <div class="div1">
    <span class="a1">请输入一个好听的名字</span>
    <input class="input1" v-model="username" >
  </div>
  <div class="div2" >
    <span class="a1">请输入房间号</span>
    <input class="input2" v-model="roomId" >
  </div>

  <div class="div3">
    <button class="button1" @click="createRoom">创建房间</button>
    <button class="button2" @click="joinRoom">加入房间</button>
  </div>


</template>

<script setup lang="ts">
import {useUserStore} from "@/stores/userStore";
import {ref} from "vue";
import {useRouter} from "vue-router";

const user=useUserStore()
const router=useRouter()
const username=ref("")
const roomId=ref(0)
const createRoom = () => {
  if(username.value!='') {
    user.$state.username = username.value
    user.$state.roomId = roomId.value
    router.push('/host')
  }else{
    alert('请输入用户名')
  }
}

const joinRoom=()=>{
  if(username.value) {
    user.$state.username = username.value
    user.$state.roomId = roomId.value
    router.push('/client')
  }else {
    alert('请输入用户名')
  }
}

</script>

<style scoped>
.div1{
  margin-top: 20%;
}
.div2{
  margin-top: 5%;
}
.div3{
  margin-top: 70%;
}
.button1{

  width: 200px;
  height: 100px;
  background-color: #214586;
  border-radius: 5px;
  border-style: none;
  color: #FFFFFF;
  float: left;
}
.button2{
  margin-left: 10%;
  width: 200px;
  height: 100px;
  background-color: #214586;
  border-radius: 5px;
  border-style: none;
  color: #FFFFFF;

}
.a1{
  color: #FFFFFF;
  margin-left: 30%;
  font-size: 20px;
}
.input1{
  margin-left: 31%;
  height: 25px;

}
.input2{
   margin-left: 31%;
  height: 25px;
 }
@media  screen and (min-width: 1200px) {
  .div1{
    margin-top: 20%;
  }
  .div2{
    margin-top: 5%;
  }
  .div3{
    margin-top: 5%;
  }
  .button1{
    margin-left: 30%;
    width: 200px;
    height: 100px;
    background-color: #214586;
    border-radius: 5px;
    border-style: none;
    color: #FFFFFF;
    float: left;
  }
  .button2{
    margin-left: 10%;
    width: 200px;
    height: 100px;
    background-color: #214586;
    border-radius: 5px;
    border-style: none;
    color: #FFFFFF;

  }
  .a1{
    color: #FFFFFF;
    margin-left: 35%;
    float: bottom;
  }
  .input1{
    margin-left: 5%
  }
  .input2{
    margin-left: 10%;
  }
}
</style>
