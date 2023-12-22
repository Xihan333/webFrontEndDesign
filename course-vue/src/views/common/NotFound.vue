<template>
    <p>该页面不存在！</p>
    <img :src="avatar" @click="select"/>
    <input @change="upload" id="upload" type="file" accept="image/*" style="display:none"/>
</template>
<script setup>
import { ref } from "vue";

function select(){
    document.getElementById('upload').click();
}

const avatar=ref('')
avatar.value=localStorage.getItem('id')

function upload(e){
    let file=e.target.files[0];
    let reader=new FileReader();
    reader.readAsDataURL(file);
    reader.onload=(data)=>{
        avatar.value=data.target.result;
        localStorage.setItem('id',data.target.result) //键值对,键名为用户唯一标识
    }
}
</script>

<style scoped>
p{
    margin: auto;
    padding-top: 20px;
    width: 400px;
    font-size: 20px;
}
</style>