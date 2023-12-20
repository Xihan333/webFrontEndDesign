<template>
    <div class="classmate">
        <div>
            <div class="query">
                <el-input
                    clearable
                    class="search"
                    v-model="inputSearch"
                    placeholder="搜索姓名或学号..."
                    @keyup.enter="searchFn"
                    />
                <el-button type="primary" @click="searchFn" color="#6FB6C1" class="searchBtn">查 询</el-button>
            </div>
        </div>
        <el-table border
          :data="paginatedTableData"
          style="width: 100%"
        >
            <el-table-column prop="num" label="学号" width="180"/>
            <el-table-column prop="name" label="学号" width="180"/>
            <el-table-column prop="gender" label="学号" width="180"/>
            <el-table-column prop="num" label="学号" width="180"/>
            <el-table-column prop="num" label="学号" width="180"/>
            <el-table-column prop="num" label="学号" width="180"/>
            <el-table-column prop="num" label="学号" width="180"/>
            <el-table-column prop="num" label="学号" width="180"/>
        </el-table>
    </div>
</template>