<template>
    <div class="blog-detail">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-if="error" class="error">{{ error }}</div>
        <div v-else>
            <div v-if="blog" class="blog-container">
                <!-- 如果有封面图片 -->
                <div v-if="blog.coverImageUid" class="blog-cover">
                    <img :src="blog.coverImageUrl" alt="Blog Cover" />
                </div>

                <h1 class="blog-title">{{ blog.title }}</h1>

                <!-- 博客元数据 -->
                <div class="blog-meta">
                    <span class="author">作者: {{ user.username }}</span>
                    <span class="date">发布日期: {{ formattedDate(blog.createTime) }}</span>
                    <div class="blog-stats">
                        <span class="clicks">阅读量: {{ blog.clicks }}</span>
                        <span class="likes">点赞数: {{ blog.likes }}</span>
                        <span class="favorites">收藏数: {{ blog.favorites }}</span>
                    </div>
                </div>

                <div v-if="blog.introduction" class="blog-introduction">
                    <p>{{ blog.introduction }}</p>
                </div>

                <!-- 博客内容 -->
                <div class="blog-content" v-html="blog.content"></div>

                <div class=blog-footer>
                    <!-- 标签 -->
                    <div v-if="tags.length > 0" class="blog-tags">
                        <span v-for="(tag, index) in tags" :key="tag.uid" class="tag"
                            :style="{ backgroundColor: tagBackgroundColors[index % tagBackgroundColors.length] }">
                            # {{ tag.tagName }}
                        </span>
                    </div>

                    <!-- 分类信息 -->
                    <div v-if="userCategory" class="blog-category">
                        <strong>分类:</strong> {{ userCategory.categoryName }}
                    </div>

                    <!-- 原始链接 -->
                    <div v-if="blog.originalUrl" class="blog-original">
                        <strong class="blog-category">原文链接:</strong>
                        <a :href="blog.originalUrl" target="_blank">查看原文</a>
                    </div>
                </div>
                <!-- 点赞，收藏等 -->
                <div class="blog-other">
                    <div class="blog-like" @click="toggleBlogLike(blog)">
                        <span class="blog-like-count">
                            <svg v-if="blog.isLiked" t="1736235625404" class="icon" viewBox="0 0 1024 1024"
                                version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1090" width="50" height="50">
                                <path
                                    d="M461.6 624.7m-300.58 0a300.58 300.58 0 1 0 601.16 0 300.58 300.58 0 1 0-601.16 0Z"
                                    fill="#E03B5D" p-id="1091"></path>
                                <path
                                    d="M287.5 784.84a26 26 0 0 1-26-25.28L254 495.38a26 26 0 0 1 25.26-26.72h0.74A26 26 0 0 1 306 494l7.48 264.18a26 26 0 0 1-25.26 26.72z"
                                    fill="#231815" p-id="1092"></path>
                                <path
                                    d="M728.68 898.24H264.34c-31.14 0-63.3-13.56-88.26-37.22-25.4-24-40-55.68-40.18-86.76L126 470.6v-0.42a118.58 118.58 0 0 1 118.44-118.44h17.32c38.62 0 48-7.62 85.5-43.48a218.88 218.88 0 0 0 50.4-87.08c3.04-10.16 4.24-14.98 5.02-18.18 2-8 3.02-10.14 7.48-18.5 0.76-1.72 3.1-7.24 4.68-10.98 4.88-11.58 5.76-13.58 6.74-15.38 12.6-23.24 35.36-37.16 64-39.22C524 116.18 567 135.84 590 166.76l1.24 1.68a194.96 194.96 0 0 1 35.14 88.18 18.42 18.42 0 0 1 0.22 4.5h0.14a305.66 305.66 0 0 1-4.74 48.54l-4.2 42h199.7A118.42 118.42 0 0 1 928 512.52l-88.8 309.7-0.48 1.18a117.8 117.8 0 0 1-110.04 74.84zM594 403.54a38.56 38.56 0 0 1-21.44-11.22c-16.54-17.2-11.62-41.78-6-70.24 2.3-11.46 4.78-23.86 6.36-37.02a21.62 21.62 0 0 1-0.18-6.76v-0.72c0.16-2 0.44-4.94 0.82-9.1 0.26-2.7 0.52-5.44 0.7-7.4a142 142 0 0 0-24.98-61.88l-1.02-1.4C536 181.18 510 169.32 489.24 170.8c-15.14 1.08-20 8.44-21.74 11.58-0.84 1.86-3.24 7.52-4.86 11.36-4.68 11.08-5.52 13.02-6.44 14.74-1.1 2-2.24 4.18-2.66 5.06-0.14 0.52-0.28 1.12-0.46 1.88-0.86 3.48-2.3 9.3-5.7 20.68a271.02 271.02 0 0 1-62.92 108.4l-0.86 0.86c-41.14 39.42-63.5 58.38-122 58.38h-17.32a66.52 66.52 0 0 0-66.44 66l10 303.58v0.44c0 35.88 38.58 72.44 76.44 72.44h464.4A66 66 0 0 0 790 805.34l88.76-309.52 0.46-1.18a66.44 66.44 0 0 0-61.76-90.9H613.7l-1.16 0.14M410.34 184a0.4 0.4 0 0 0 0 0.16 0.6 0.6 0 0 1 0-0.16z"
                                    fill="#231815" p-id="1093"></path>
                            </svg>
                            <svg v-if="!blog.isLiked" t="1736235934897" class="icon" viewBox="0 0 1024 1024"
                                version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8127" width="50" height="50">
                                <path
                                    d="M857.28 344.992h-264.832c12.576-44.256 18.944-83.584 18.944-118.208 0-78.56-71.808-153.792-140.544-143.808-60.608 8.8-89.536 59.904-89.536 125.536v59.296c0 76.064-58.208 140.928-132.224 148.064l-117.728-0.192A67.36 67.36 0 0 0 64 483.04V872c0 37.216 30.144 67.36 67.36 67.36h652.192a102.72 102.72 0 0 0 100.928-83.584l73.728-388.96a102.72 102.72 0 0 0-100.928-121.824zM128 872V483.04c0-1.856 1.504-3.36 3.36-3.36H208v395.68H131.36A3.36 3.36 0 0 1 128 872z m767.328-417.088l-73.728 388.96a38.72 38.72 0 0 1-38.048 31.488H272V476.864a213.312 213.312 0 0 0 173.312-209.088V208.512c0-37.568 12.064-58.912 34.72-62.176 27.04-3.936 67.36 38.336 67.36 80.48 0 37.312-9.504 84-28.864 139.712a32 32 0 0 0 30.24 42.496h308.512a38.72 38.72 0 0 1 38.048 45.888z"
                                    p-id="8128"></path>
                            </svg>

                        </span>
                    </div>
                    <div class="blog-collect" @click="toggleCollect(blog)">
                        <span class="blog-collect-count">
                            <svg v-if="isFavorite" t="1736235739807" class="icon" viewBox="0 0 1024 1024" version="1.1"
                                xmlns="http://www.w3.org/2000/svg" p-id="1243" width="50" height="50">
                                <path
                                    d="M624.68 588.62m-300.58 0a300.58 300.58 0 1 0 601.16 0 300.58 300.58 0 1 0-601.16 0Z"
                                    fill="#E03B5D" p-id="1244"></path>
                                <path
                                    d="M772.46 960.64a54.12 54.12 0 0 1-25.68-6.5l-225.84-121.2a2.3 2.3 0 0 0-2 0L291 950.24a54.22 54.22 0 0 1-78.2-57.84l45.48-252.22a2.22 2.22 0 0 0-0.62-2l-182-180.46a54.26 54.26 0 0 1 30.84-92.26l254-34.68a2.22 2.22 0 0 0 1.7-1.2l115.32-228.84A54 54 0 0 1 526 70.92h0.48a54 54 0 0 1 48.32 30.66l111.44 230.78a2.32 2.32 0 0 0 1.68 1.24l253.3 39.02a54.22 54.22 0 0 1 29.28 92.76l-185.06 177.32a2.3 2.3 0 0 0-0.66 2L826 897.64a54.32 54.32 0 0 1-53.48 63z m-252.58-180a54.38 54.38 0 0 1 25.66 6.44l225.82 121.2a2 2 0 0 0 2.36-0.14 2 2 0 0 0 0.9-2.18l-41.16-252.94a54.2 54.2 0 0 1 16-47.86l185.04-177.32a2.24 2.24 0 0 0-1.2-3.82L680 385a54.22 54.22 0 0 1-40.58-30L528 124.18a2 2 0 0 0-2-1.26 2 2 0 0 0-2 1.24L408.56 353a54.18 54.18 0 0 1-41.08 29.32l-254 34.68a2 2 0 0 0-1.82 1.52 2 2 0 0 0 0.56 2.3l182 180.44a54.28 54.28 0 0 1 15.2 48.14L264 901.62a2.24 2.24 0 0 0 3.22 2.4l227.86-117.32a54.16 54.16 0 0 1 24.8-6.02z"
                                    fill="#231815" p-id="1245"></path>
                                <path
                                    d="M310.36 493.3a26 26 0 0 1-3.48-51.78l117.82-16 44.64-88.54a26 26 0 1 1 46.42 23.4l-44.82 88.82a51.2 51.2 0 0 1-38.8 27.7L314 493.04a26.88 26.88 0 0 1-3.64 0.26z"
                                    fill="#231815" p-id="1246"></path>
                            </svg>
                            <svg v-if="!isFavorite" t="1736264135034" class="icon" viewBox="0 0 1024 1024" version="1.1"
                                xmlns="http://www.w3.org/2000/svg" p-id="3202" width="50" height="50">
                                <path
                                    d="M454.613333 96.682667l-110.122666 223.146666-246.272 35.797334a64 64 0 0 0-35.456 109.141333l178.176 173.696-42.069334 245.290667a64 64 0 0 0 6.442667 40.576l3.114667 5.333333a64 64 0 0 0 83.328 21.546667L512 835.413333l220.245333 115.797334a64 64 0 0 0 92.885334-67.456l-42.069334-245.290667 178.176-173.653333a64 64 0 0 0-35.456-109.226667l-246.272-35.754667-110.08-223.146666a64 64 0 0 0-114.816 0zM512 173.226667l91.008 184.362666 3.328 6.144a85.333333 85.333333 0 0 0 60.928 40.533334l203.434667 29.525333-147.2 143.573333-4.821334 5.077334a85.333333 85.333333 0 0 0-19.712 70.442666l34.730667 202.624-201.813333-106.069333-4.778667-2.133333a42.666667 42.666667 0 0 0-34.986667 2.133333L290.304 855.466667l34.773333-202.624 0.896-6.912a85.333333 85.333333 0 0 0-25.429333-68.608l-147.242667-143.573334 203.52-29.525333a85.333333 85.333333 0 0 0 64.213334-46.677333L512 173.226667z"
                                    fill="#000000" p-id="3203"></path>
                            </svg>
                        </span>
                    </div>

                    <!-- 收藏 -->
                    <el-dialog v-model="collectDialogFormVisible" @open="openDialog()" title="收藏夹" width="500">
                        <el-form :model="form">
                            <!-- 新建收藏夹模块 -->
                            <el-form-item>
                                <div class="collect-form-control">
                                    <el-button type="primary" @click="showCollection()">{{ createCollectionVisible ?
                                        '收起' : '创建新收藏夹' }}</el-button>
                                </div>
                            </el-form-item>
                            <el-form-item v-if="createCollectionVisible" label="" label-width="10px">
                                <div class="blog-new-forder">
                                    <el-input v-model="newFolder.categoryName" autocomplete="off"
                                        placeholder="创建新收藏夹名称" />
                                    <el-input type="textarea" :rows="3" v-model="newFolder.description"
                                        autocomplete="off" placeholder="收藏夹描述" />
                                    <el-button type="primary" @click="createNewFolder">创建</el-button>
                                </div>
                            </el-form-item>

                            <!-- 已有收藏夹模块 -->
                            <el-form-item label="" label-width="10px">
                                <div class="folders-container" v-if="folders.length > 0"
                                    style="margin-top: 10px; max-height: 300px; overflow-y: auto;">
                                    <div v-for="folder in folders" :key="folder.uid" class="folder-item">
                                        <span>{{ folder.categoryName }}</span>
                                        <el-button round @click="addToFolder(folder)" width="100px">
                                            {{ folder.isCollected ? '已收藏' : '收藏' }}</el-button>
                                    </div>
                                </div>
                            </el-form-item>
                        </el-form>
                        <el-form>

                        </el-form>
                    </el-dialog>

                </div>

            </div>

            <div class="comments-section">
                <h2>评论区</h2>

                <div v-if="comments" class="comments-list">
                    <div v-for="comment in comments" :key="comment.uid" class="comment-item">
                        <div class="comment-meta">
                            <img :src="comment.userVO.avatarUrl" alt="Avatar" class="comment-avatar" />
                            <strong class="username">{{ comment.userVO.username }}</strong>
                            <div class="comment-meta-detail">
                                <span class="comment-time">{{ formattedDate(comment.createTime) }}</span>

                            </div>
                        </div>
                        <div class="comment-content">{{ comment.content }}</div>

                        <div class="comment-footer">
                            <span class="reply" @click="toggleReplyForm(comment)">
                                <svg t="1736130960867" class="icon" viewBox="0 0 1024 1024" version="1.1"
                                    xmlns="http://www.w3.org/2000/svg" p-id="8094" width="20" height="20">
                                    <path
                                        d="M860.377432 133.214001 163.652244 133.214001c-41.643446 0-75.546603 33.903158-75.546603 75.546603l0 510.917342c0 41.643446 33.903158 75.546603 75.546603 75.546603l252.743328 0 95.618754 95.559403 95.589079-95.559403 252.774027 0c41.643446 0 75.516928-33.903158 75.516928-75.546603L935.89436 208.761628C935.89436 167.117159 902.020878 133.214001 860.377432 133.214001zM877.749078 231.983515l0 464.473567c0 22.381753-18.21178 40.593534-40.593534 40.593534L579.520813 737.050616l-67.506487 67.565838-67.536162-67.565838L186.874131 737.050616c-22.412452 0-40.624233-18.21178-40.624233-40.593534L146.249899 231.983515c0-22.412452 18.21178-40.624233 40.624233-40.624233l650.28039 0C859.537298 191.360306 877.749078 209.571063 877.749078 231.983515z"
                                        p-id="8095"></path>
                                    <path
                                        d="M302.83519 403.749554 741.266637 403.749554c14.851243 0 26.972305-12.481266 26.972305-27.812439 0-15.361873-12.121062-27.872814-26.972305-27.872814L302.83519 348.0643c-14.851243 0-26.942629 12.510942-26.942629 27.872814C275.892561 391.268288 287.983947 403.749554 302.83519 403.749554z"
                                        p-id="8096"></path>
                                    <path
                                        d="M741.266637 496.578776 302.83519 496.578776c-14.851243 0-26.942629 12.510942-26.942629 27.842115 0 15.331173 12.091386 27.842115 26.942629 27.842115L741.266637 552.263006c14.851243 0 26.972305-12.510942 26.972305-27.842115C768.238942 509.089717 756.11788 496.578776 741.266637 496.578776z"
                                        p-id="8097"></path>
                                </svg>
                            </span>
                            <span class="likes" @click="toggleLike(comment)">
                                <svg v-if="!comment.isLiked" t="1736130948618" class="icon" viewBox="0 0 1024 1024"
                                    version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7946" width="20" height="20">
                                    <path
                                        d="M194.792475 478.69343c16.167215 0 29.330002-13.163811 29.330002-29.346375 0-16.092513-13.015432-29.212322-29.801746-29.212322-0.086981 0-0.177032 0-0.264013 0l-60.606333 0.177032c-1.560542-0.206708-3.149736-0.325411-4.76963-0.325411-21.291932 0-38.636972 17.537422-38.636972 39.09439l-0.323365 423.853427c0 21.645996 16.9613 38.578644 38.636972 38.578644l1.677199 0.057305c0.707105 0.029676 1.385557 0.059352 2.032286 0.059352 0.823761 0 1.64957-0.029676 1.826602-0.11768l59.926858 0.086981c0.559748 0 1.001817-0.11768 1.089821-0.11768 0 0-0.029676 0-0.116657 0.057305l3.76986-0.147356 0-0.382717c13.517875-2.236947 23.64656-13.84124 23.64656-27.799136 0-13.987572-10.129708-25.589818-23.64656-27.828812l0-0.766456-49.827849 0 1.413186-385.92356L194.792475 478.69036z"
                                        fill="#231F20" p-id="7947"></path>
                                    <path
                                        d="M918.228001 436.301947c-18.199501-29.66974-45.204551-44.821835-82.22163-46.322002-1.883907-0.25071-3.799536-0.412392-5.77247-0.412392l-195.744149-0.648776c13.398148-40.359196 20.732184-83.750449 20.732184-122.740462 0-27.255761-3.00545-54.686507-8.894577-81.543178l-1.206478-3.196809-0.089028 0.01535c-12.161994-46.837748-53.477982-79.259158-101.626585-79.259158-55.393611 0-97.18032 46.248324-97.18032 107.576087l-0.059352 3.047406c-0.057305 2.222621-0.116657 4.40124 0.059352 6.16849-2.796696 101.715613-83.869153 187.397041-188.559517 199.324698l-3.358491 0.36839-0.76441 274.43128 0 228.491994 13.930267 0.005117 0.587378 0.11154 3.179412-0.027629 0.01535-0.081864 485.653959 0.198521 8.685823-0.236384c19.84907 0 31.038924-5.03569 48.621371-16.344247 16.22452-10.482749 29.359678-25.266453 37.751812-42.316781 2.562359-3.76986 4.418636-7.862062 5.565762-12.251022l75.30101-336.657506c1.089821-4.225231 1.472538-8.643867 1.149173-12.56006C935.660023 478.42737 930.212964 455.900308 918.228001 436.301947zM879.148961 488.807788l-0.912789 3.799536 0.295735 0.074701-80.129992 355.2387-0.148379 0.353041c-2.296299 5.536087-6.15314 10.160407-11.101849 13.340843-3.415796 2.209318-7.185656 3.622504-11.161201 4.239558-0.470721-0.057305-0.941442-0.057305-1.413186-0.057305l-3.03308 0.086981-462.84344-0.530073-0.177032-392.299781c79.540567-35.986609 148.155295-73.959455 183.080735-167.79868l0.295735 0.074701 1.119497-3.328815c3.709485-11.263531 6.596232-22.350031 8.598842-32.909527 5.447059-28.577872 5.212722-56.422034 5.183046-58.041928-1.766226-13.502525 1.206478-24.738427 8.80555-33.394574 8.598842-9.777691 21.673625-14.151301 30.744211-14.151301 22.586414 0.766456 45.526893 30.966269 45.61592 48.163953 0.029676 0.26606 5.38873 26.945699 5.418406 55.186903 0.057305 27.65178-2.651386 45.277206-3.858888 51.828412l-0.500397 0-0.589424 3.062755c-5.595438 29.522384-15.518438 57.794287-29.41903 84.003206l-0.972141 2.856048 0.118704 0.132006c-1.885953 4.696975-2.856048 9.643638-2.856048 14.709003 0 25.032116 27.121707 25.032116 41.697681 25.032116l233.381351 0.264013 7.15598 0.221034c1.266853 0.045025 2.503007 0.089028 3.592828 0.118704l0 0.425695 5.538133-0.248663c10.453073 0 19.937075 5.344728 25.412786 14.296611C880.709503 471.080032 881.829 480.283648 879.148961 488.807788z"
                                        fill="#231F20" p-id="7948"></path>
                                </svg>
                                <svg v-if="comment.isLiked" t="1736220948916" class="icon" viewBox="0 0 1024 1024"
                                    version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="8542" width="20" height="20">
                                    <path
                                        d="M844.231111 391.39555600000006H659.9111109999999c-13.653333-2.275556-18.204444-15.928889-20.48-22.755556V202.52444400000002c0-45.511111-36.408889-81.92-81.92-81.92-40.96 0-75.093333 31.857778-81.92 70.542223-25.031111 157.013333-120.604444 209.351111-182.044444 225.28 2.275556 6.826667 2.275556 11.377778 2.275555 15.928889v441.457777c0 9.102222-2.275556 18.204444-6.826666 27.306666999999997h446.008888c43.235556-9.102222 72.817778-25.031111 93.297778-68.266667l93.297778-327.68c18.204444-59.16444400000001-11.377778-116.053333-77.368889-113.77777699999999zM257.137778 876.0888889999999V432.355556c0-13.653333-18.204444-25.031111-38.684445-25.031112H157.013333c-31.857778 0-59.16444400000001 27.306666999999997-59.16444400000001 59.16444500000001v373.191111c0 34.133333 25.031111 61.44 59.16444400000001 61.44h61.44c20.48 0 38.684444-11.377778 38.684445-25.031111z m0 0"
                                        fill="" p-id="8543"></path>
                                </svg>
                                <i :class="['like-icon', { liked: comment.isLiked }]"></i> {{ comment.likes || 0 }}
                            </span>
                        </div>
                        <!-- 回复表单 -->
                        <div v-if="commentReply.parentUid === comment.uid && ishowReplyForm" class="reply-form">
                            <textarea v-model="commentReply.content" placeholder="请输入回复内容"
                                class="input-reply"></textarea>
                            <button @click="submitReply(commentReply)" class="submit-reply">提交回复</button>
                        </div>

                        <!-- 子评论 -->
                        <comment-item v-if="comments.length > 0 && comments" :comment="comment"
                            :collapseReply="collapseReply" :level="2" @toggle-like="toggleLike"
                            @submit-reply="handleReplySubmission" />


                        <!-- 子评论展开/收起按钮 -->
                        <div v-if="comment.children && comment.children.length > 0" class="toggle-children">
                            <button @click="toggleChildren(comment)">
                                {{ collapseReply ? '收起' : '展开' }}({{ comment.childCount }}条子评论)
                            </button>
                        </div>
                    </div>
                </div>
                <!-- 博客回复 -->
                <div class="comment-affix">
                    <el-affix :offset="20" target=".comments-section">
                        <el-button type="primary" @click="toggleReplyForm()">发表评论</el-button>
                    </el-affix>
                    <!-- 回复表单 -->
                    <div v-if="isShowComment" class="reply-form">
                        <textarea v-model="commentReply.content" placeholder="请输入回复内容" class="input-reply"></textarea>
                        <button @click="submitReply(commentReply)" class="submit-reply">提交回复</button>
                    </div>
                </div>
            </div>
        </div>

        <LoginModal ref="loginModalRef" />
    </div>


</template>

<script setup>
import { ref, onMounted, watch, inject } from 'vue'
import { fetchCommentsApi, submitCommentApi, commentToggleLikeApi,fetchCommentsAfterLoginApi } from '../../api/comentApi'
import { fetchBlogDetailApi, blogToggleLikeApi, createBlogCollectionCategoryApi, fetchBlogCollectionCategoryApi, toggleBlogCollectionApi, fetchBlogLikeAndCollectionApi } from '../../api/blogApi'
import { validateTokenApi } from '../../api/authApi'
import CommentItem from '../comment/CommentItem.vue'
import { tagBackgroundColors } from '../../../static/tagConfig'
import { ElMessage } from 'element-plus'
import LoginModal from "../login/LoginModal.vue";

// 组件间传参
const author = inject('author')

const blog = ref(null)
const user = ref(null)
const userCategory = ref(null)
const tags = ref([])
const loginModalRef = ref(null);

// 登录状态
let isLogin = ref(false)

let collectDialogFormVisible = ref(false)
let form = ref({
    selectedFolder: null,  // 当前选择的收藏夹ID
});
var newFolder = ref({
    categoryName: '',
    description: ''
});  // 新收藏夹
let folders = ref([]);  // 收藏夹列表
let createCollectionVisible = ref(false);// 显示创建收藏夹表单
let isFavorite = ref(false);// 是否收藏

let comments = ref([])  // 存储评论列表
const newComment = ref({ username: '', content: '', parentUid: null })  // 新评论内容，parentUid 默认空
const loading = ref(true)
let error = ref(null)
let commentReply = ref({
    blogUid: '',
    content: '',
    parentUid: ''
})
let ishowReplyForm = ref(false)
let isShowComment = ref(false)
const collapseReply = ref(false)
const props = defineProps({
    uid: {
        type: String,
        required: true
    }
})


const fetchBlogDetail = async () => {
    try {
        console.log('isLogin', isLogin.value)
        const request = {
            uid: props.uid,
            isDetailRequest: true   
        }
        let response = ref(null)
        if (isLogin.value === false) {
            response = await fetchBlogDetailApi(request)
        }
        else {
            response = await fetchBlogLikeAndCollectionApi(request)
        }

        if (response.code !== 200) {
            throw new Error('Network response was not ok')
        }
        const data = response.data

        blog.value = data.blogVO
        user.value = data.userVO
        userCategory.value = data.userCategory
        tags.value = data.tags || []

        isFavorite.value = blog.value.isFavorite

        author.value = user.value
        // 如果允许评论，加载评论
        if (blog.value.commentsAllowed) {
            await fetchComments()
        }
    } catch (err) {
        error.value = err.message
    } finally {
        loading.value = false
    }
}

const fetchComments = async () => {
    try {
        let response = ref(null)
        if (isLogin.value === false) 
        response = await fetchCommentsApi(props.uid)
        else 
        response = await fetchCommentsAfterLoginApi(props.uid)
        if (response.code === 200) {
            comments.value = response.data
            console.log("comments", comments.value)
        }
    } catch (err) {
        error.value = err.message
    }
}

const toggleBlogLike = async (blog) => {
    blog.isLiked = !blog.isLiked
    blog.likes += blog.isLiked ? 1 : -1
    const data = {
        uid: blog.uid,
        likes: blog.likes,
        isLiked: blog.isLiked
    }
    const response = await blogToggleLikeApi(data)
    console.log("blogToggleLikeApi", response)
    if (response.code === 200) {
        console.log("博客点赞成功”")
    }
    else if (response.code === 401) {
        console.log("未登录")
        blog.isLiked = !blog.isLiked
        blog.likes -= blog.isLiked ? 1 : -1
        loginModalRef.value.openLoginModal();
    } else {
        console.log("博客点赞失败")
        blog.isLiked = !blog.isLiked
        blog.likes -= blog.isLiked ? 1 : -1
    }

}
const toggleCollect = (blog) => {
    collectDialogFormVisible.value = !collectDialogFormVisible.value
}
const isProcessing = ref(false) // 防止重复点击

const toggleLike = async (comment) => {
    if (isProcessing.value) return // 防止重复提交
    isProcessing.value = true

    // 先乐观更新 UI
    const originalIsLiked = comment.isLiked
    const originalLikes = comment.likes
    comment.isLiked = !comment.isLiked
    comment.likes += comment.isLiked ? 1 : -1

    // 调用评论点赞接口
    const data = {
        uid: comment.uid,
        likes: comment.likes,
        isLiked: comment.isLiked
    }

    try {
        const response = await commentToggleLikeApi(data);
        if (response.code === 200) {
            console.log("点赞成功")
        } else if (response.code === 401) {
            console.log("未登录")
            comment.isLiked = originalIsLiked
            comment.likes = originalLikes
            loginModalRef.value.openLoginModal();
        }
        else {
            throw new Error('点赞失败') // 触发 catch 逻辑
        }
    } catch (error) {
        console.error(error.message)
        console.log("回滚点赞状态")
        // 回滚状态
        comment.isLiked = originalIsLiked
        comment.likes = originalLikes
    } finally {
        isProcessing.value = false // 释放锁
    }
}

const toggleReplyForm = (comment) => {

    commentReply.value.content = ''
    if (comment) {
        if (ishowReplyForm.value) {
            ishowReplyForm.value = false;
        } else {
            ishowReplyForm.value = true;
        }

        commentReply.value.parentUid = comment.uid // 设置 parentUid 为当前评论的 uid
    }
    else {

        isShowComment.value = !isShowComment.value
        commentReply.value.parentUid = 'isRoot'
    }


    commentReply.value.blogUid = props.uid
}

const submitReply = async (commentReply) => {
    if (!commentReply.content) {
        alert('回复内容不能为空！')
        return
    }

    try {
        const response = await submitCommentApi(commentReply)
        if (response.code === 200) {
            commentReply.replyContent = ''
            ishowReplyForm.value = false
            isShowComment.value = true
            await fetchComments()
        } else if (response.code === 401) {
            loginModalRef.value.openLoginModal();
        }
    } catch (err) {
        error.value = err.message
    }
}

const toggleChildren = (comment) => {
    collapseReply.value = !collapseReply.value
}
const validateToken = async () => {
    const token = localStorage.getItem("token");
    if (!token) return;

    try {
        const res = await validateTokenApi(token);
        if (res.code === 200) {
            isLogin.value = true;
        } else {
            handleLogout(); // 处理退出逻辑
        }
    } catch (error) {
        console.error("Token 验证失败", error);
        handleLogout();
    }
};

const handleLogout = () => {
    localStorage.removeItem("token");
    isLogin.value = false;
    ElMessage.warning("登录已失效，请重新登录");
};

onMounted(async () => {
    await validateToken(); // 确保 token 校验优先执行
    fetchBlogDetail();
    fetchComments();
});

const formattedDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    })
}
const handleReplySubmission = async (reply) => {
    console.log('收到回复：', reply);
    try {
        const response = await submitCommentApi(reply)
        if (response.code === 200) {
            await fetchComments()
        }
    } catch (err) {
        error.value = err.message
    }

};
// 添加博客到指定收藏夹
const addToFolder = async (folder) => {
    const data = {
        blogUid: props.uid,
        categoryUid: folder.uid,
        isCollected: !folder.isCollected
    }
    const response = await toggleBlogCollectionApi(data)
    if (response.code === 200) {
        loadFolders()
        if (data.isCollected) {
            blog.value.favorites += 1;
        } else {
            blog.value.favorites -= 1;
        }
    }
};
// 创建新收藏夹
const createNewFolder = async () => {
    try {
        if (newFolder.value.categoryName.trim() === "") {
            throw new Error("请输入收藏夹名称");
        }
        if (newFolder.value.description.trim() === "") {
            throw new Error("请输入收藏夹描述");
        }
    }
    catch (err) {
        ElMessage({
            showClose: true,
            message: err.message,
            type: 'error',
        });
        return;
    }
    const folder = {
        categoryName: newFolder.value.categoryName,
        description: newFolder.value.description,
        // 其他属性
    };
    const response = await createBlogCollectionCategoryApi(folder);
    if (response.code == 200) {
        console.log("收藏夹创建成功", response);
        loadFolders();
        createCollectionVisible.value = false;
        newFolder.value = {};

    }
    else {
        ElMessage({
            showClose: true,
            message: '收藏夹创建失败',
            type: 'error',
        });
    }

};
// 加载已有收藏夹数据（这里模拟数据）
const loadFolders = async () => {
    const response = await fetchBlogCollectionCategoryApi(props.uid);
    if (response.code === 200) {
        folders.value = response.data;
        // 遍历收藏夹列表，更新对应收藏夹的isCollected状态
        isFavorite.value = false
        folders.value.forEach(folder => {
            if (folder.isCollected) {
                isFavorite.value = true
                return;
            }
        })


    } else if (response.code === 401) {

        loginModalRef.value.openLoginModal();
        collectDialogFormVisible.value = !collectDialogFormVisible.value
    }

};
// 打开对话框时加载已有收藏夹
const openDialog = () => {
    loadFolders();
};

// 收起展开新收藏夹表单
const showCollection = () => {
    createCollectionVisible.value = !createCollectionVisible.value;
}

</script>

<style scoped>
.blog-detail {
    max-width: 960px;
    margin: 0 auto;
    padding: 40px;
    background-color: #ffffff;
    box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
    border-radius: 8px;
    transition: all 0.3s ease-in-out;
}

.blog-detail:hover {
    box-shadow: 0 0 25px rgba(0, 0, 0, 0.2);
}

.blog-container {
    padding: 20px 0;
}

.blog-title {
    font-size: 2.5em;
    font-weight: 700;
    color: #333;
    margin: 20px 0;
    line-height: 1.3;
    text-align: center;
    /* 将文本内容水平居中 */
}

.blog-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    font-size: 1em;
    color: #999;
    line-height: 1.6;
}

.blog-meta .author {
    font-weight: 600;
    color: #555;
}

.blog-meta .date {
    font-style: italic;
}

.blog-stats {
    display: flex;
    gap: 15px;
    font-size: 1em;
    color: #777;
}

.blog-stats span {
    padding: 5px 10px;
    background-color: #f6f6f6;
    border-radius: 20px;
    border: 1px solid #ddd;
    transition: background-color 0.3s ease, color 0.3s ease;
}

.blog-stats span:hover {
    background-color: #007bff;
    color: #fff;
    cursor: pointer;
}

.blog-cover {
    width: 100%;
    height: 450px;
    overflow: hidden;
    border-radius: 12px;
    margin-bottom: 30px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.blog-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 12px;
    transition: transform 0.3s ease-in-out;
}

.blog-cover img:hover {
    transform: scale(1.05);
}

.blog-introduction {
    font-size: 1.15em;
    color: #444;
    line-height: 1.7;
    margin-bottom: 30px;
    font-style: italic;
}

.blog-content {
    font-size: 1.1em;
    line-height: 1.8;
    color: #333;
    margin-bottom: 30px;
    word-wrap: break-word;
}

.blog-tags {
    display: flex;
    flex-wrap: wrap;
}

.tag {
    padding: 4px 8px;
    margin: 4px;
    border-radius: 4px;
    color: white;
    font-size: 14px;
    font-weight: bold;
}


.blog-tags .tag:hover {
    background-color: #007bff;
    color: white;
    cursor: pointer;
}

.blog-category {
    font-size: 1.1em;
    margin-top: 20px;
    font-weight: 600;
}

.blog-original a {
    font-size: 1.1em;
    color: #007bff;
    text-decoration: none;
    float: right;
}

.blog-original a:hover {
    text-decoration: underline;
}

.comments-section {
    margin-top: 50px;
}

.comments-list {
    margin-bottom: 30px;
}

.comment-item {
    display: flex;
    flex-direction: column;
    padding: 15px 0;
    border-bottom: 1px solid #f1f1f1;
}

.comment-meta {
    display: flex;
    align-items: center;
    gap: 15px;
    font-size: 1em;
    color: #444;
    margin-bottom: 10px;
}

.comment-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #ddd;
    transition: transform 0.2s ease;
}

.comment-avatar:hover {
    transform: scale(1.1);
}

.comment-info {
    display: flex;
    flex-direction: column;
}

.comment-footer {
    margin-top: 20px;
    display: flex;
    gap: 15px;
    align-items: center;
    flex-direction: row;
    margin-bottom: 15px;
    /* padding-left: 10px;  给子评论增加左边距 */
    border-bottom: 1px solid #ddd;
    /* 在子评论底部添加下划线 */
}

.comment-meta-detail {
    display: flex;
    gap: 15px;
    font-size: 0.9em;
    color: #212020;
}

.comment-content {
    font-size: 1em;
    color: #212020;
    line-height: 1.6;
    margin-top: 10px;
}

.comment-children {
    margin-top: 20px;
    padding-left: 20px;
    border-left: 3px solid #f1f1f1;
}

.child-comment {
    background-color: #f9f9f9;
    padding: 12px;
    border-radius: 8px;
    margin-top: 10px;
}

.comment-form {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.input-username,
.input-comment {
    padding: 12px;
    font-size: 1em;
    border: 1px solid #ddd;
    border-radius: 5px;
}

.input-username {
    width: 100%;
}

.input-comment {
    width: 100%;
    min-height: 120px;
    resize: vertical;
}

.submit-comment {
    padding: 12px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.submit-comment:hover {
    background-color: #0056b3;
}

.comment-affix {
    display: flex;
    align-items: center;
    flex-direction: column;
}

.comment-item {
    padding: 15px;
    border-bottom: 1px solid #f1f1f1;
    display: flex;
    flex-direction: column;
}

.comment-meta {
    display: flex;
    gap: 12px;
    font-size: 1em;
    align-items: center;
}

.comment-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid #ddd;
}

.comment-info {
    display: flex;
    flex-direction: column;
}

.comment-meta-detail {
    display: flex;
    gap: 15px;
    font-size: 0.9em;
    color: #777;
}

.comment-content {
    margin-top: 10px;
    font-size: 1em;
    color: #666;
}

.likes {
    cursor: pointer;
}

.reply {
    cursor: pointer;
    color: #007bff;
    font-weight: bold;
}

.blog-other {
    display: flex;
    justify-content: center;
    /* align-items: center; 如果需要垂直居中 */
    gap: 50px;
}

.reply-form {
    width: 100%;
    margin-top: 15px;
    padding: 10px;
    background: #f9f9f9;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;

}

.input-reply {
    width: 100%;
    min-height: 100px;
    padding: 10px;
    font-size: 1em;
    border: 1px solid #ddd;
    border-radius: 5px;
}

.submit-reply {
    margin-top: 10px;
    padding: 8px 20px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.submit-reply:hover {
    background-color: #0056b3;
}

.comment-children {
    margin-top: 20px;
    padding-left: 20px;
    border-left: 2px solid #ddd;
}

.toggle-children button {
    margin-top: 10px;
    background: none;
    border: none;
    color: #007bff;
    cursor: pointer;
}

.blog-footer {
    border-top: 1px solid #ddd;
    border-bottom: 1px solid #ddd;
    /* padding: 30px 0; */
    margin: 50px 20px 30px;
    /* 上 10px，左右 20px，下 30px */
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.blog-new-forder {
    display: flex;
    flex-direction: column;
    width: 100%;
    gap: 10px;
}

.collect-form-control {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    /* 可选：设置容器高度 */

}

.collect-form-control {
    text-align: right;
}

.blog-new-forder {
    display: flex;
    flex-direction: column;
}

.folder-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
    border-bottom: 1px solid #ebeef5;
    width: 100%;
}

.folder-item:last-child {
    border-bottom: none;
}

.folder-item:hover {
    background-color: #f5f7fa;
    cursor: pointer;
}

.folder-item span {
    font-size: 14px;
    color: #303133;
}

.folder-item .el-button {
    padding: 0;
    font-size: 14px;
}

.folders-container {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    padding: 10px;
    background-color: #fff;
    width: 100%;
}
</style>