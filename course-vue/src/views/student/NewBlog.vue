<!-- 发布新文章 -->
<template>
    <div class="new-blog">
        <input class="title" v-model="title" 
              maxlength="50"
              placeholder="请输入文章标题(50字以内)"
              show-word-limit  />
        <v-md-editor v-model="markdown" height="700px" 
            placeholder="# 记录创作灵感 & 美好生活 #"></v-md-editor>
        
        <div class="box">
            <p class="tagName">*文章标签</p>
            <el-tag
                v-for="tag in dynamicTags"
                :key="tag"
                class="tags"
                closable
                :disable-transitions="false"
                @close="handleClose(tag)"
            >
                {{ tag }}
            </el-tag>
            <el-input
                v-if="inputVisible"
                ref="InputRef"
                v-model="inputValue"
                class="tagsBtn"
                size="small"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
            />
            <el-button v-else class="tagsBtn" size="small" @click="showInput">
                + New Tag
            </el-button>
            <div>
                <el-button class="submit" type="primary">发布博客</el-button>
                <el-button class="back">返回文章</el-button>
            </div>
            
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref,onMounted,nextTick } from "vue";
import { ElInput } from 'element-plus'

const markdown = ref('')
const title = ref('')

// tag相关 Start
const inputValue = ref('')
const dynamicTags = ref(['Tag 1', 'Tag 2', 'Tag 3'])
const inputVisible = ref(false)
const InputRef = ref<InstanceType<typeof ElInput>>()

const handleClose = (tag:string) => {
  dynamicTags.value.splice(dynamicTags.value.indexOf(tag), 1)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value!.input!.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    dynamicTags.value.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}
// tag相关 end

</script>

<style scoped lang="scss">
.title{
    width: 70%;
    height: 50px;
    border: 0;
    outline: none;  //去除选中时的边框
    margin-left: 10px;
    margin-bottom:20px ;
    font-size: 25px;
    opacity: 0.5;
}
.box{
    width: 50%;
    min-height: 200px;
    background-color: white;
    margin-top: 30px;
    margin-left: 5px;
    margin-bottom: 5px;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);

    .tagName{
        padding-top: 20px;
        margin-left: 20px;
        font-size: 16px;
        color: rgb(93, 93, 93);
        display: inline-block;
        vertical-align:middle;
    }
    .tags{
        display: inline-block;
        vertical-align:middle;
        margin-top: 20px;
        margin-left: 20px;
    }
    .tagsBtn{
        display: inline-block;
        vertical-align:middle;
        margin-top: 20px;
        margin-left: 20px;
    }
    .submit{
        display: block;
        margin-top: 20px;
        margin-left: 20px;
    }
}
</style>

