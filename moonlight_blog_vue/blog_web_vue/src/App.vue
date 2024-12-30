<script setup>
import { RouterLink, RouterView } from 'vue-router'

import ArticleEditor from './components/blog/ArticleEditor.vue'
</script>

<template>
    <div id = "app1">
     <ArticleEditor/>
    </div>

</template>

<style scoped>

 #app1 {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        width: 100%;
        height: 100%;
    } 

</style>
