<template>
    <div v-if="collapseReply" class="comment-children">
        <div v-for="child in comment.children" :key="child.uid" class="comment-item child-comment">
            <div class="comment-meta">
                <img :src="child.userVO.avatarUrl" alt="Avatar" class="comment-avatar" />
                <strong class="username">{{ child.userVO.username }}</strong>
                <div class="comment-meta-detail">
                    <span class="comment-time">{{ formattedDate(child.createTime) }}</span>
                </div>
            </div>

            <!-- 三级及以上评论，显示 @父评论的内容 -->
            <div class="comment-content">
                <span v-if="isThirdOrMoreLevel(level)">
                    回复 <strong class="user-name">@{{ comment.userVO.username }}：</strong>
                </span>
                {{ child.content }}
            </div>

            <!-- 回复按钮 -->
            <div class="comment-footer">
                <span class="reply" @click="toggleReplyForm(child)">
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
                <span class="likes" @click="toggleLike(child)">
                    <svg v-if="!child.isLiked" t="1736130948618" class="icon" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="7946" width="20" height="20">
                        <path
                            d="M194.792475 478.69343c16.167215 0 29.330002-13.163811 29.330002-29.346375 0-16.092513-13.015432-29.212322-29.801746-29.212322-0.086981 0-0.177032 0-0.264013 0l-60.606333 0.177032c-1.560542-0.206708-3.149736-0.325411-4.76963-0.325411-21.291932 0-38.636972 17.537422-38.636972 39.09439l-0.323365 423.853427c0 21.645996 16.9613 38.578644 38.636972 38.578644l1.677199 0.057305c0.707105 0.029676 1.385557 0.059352 2.032286 0.059352 0.823761 0 1.64957-0.029676 1.826602-0.11768l59.926858 0.086981c0.559748 0 1.001817-0.11768 1.089821-0.11768 0 0-0.029676 0-0.116657 0.057305l3.76986-0.147356 0-0.382717c13.517875-2.236947 23.64656-13.84124 23.64656-27.799136 0-13.987572-10.129708-25.589818-23.64656-27.828812l0-0.766456-49.827849 0 1.413186-385.92356L194.792475 478.69036z"
                            fill="#231F20" p-id="7947"></path>
                        <path
                            d="M918.228001 436.301947c-18.199501-29.66974-45.204551-44.821835-82.22163-46.322002-1.883907-0.25071-3.799536-0.412392-5.77247-0.412392l-195.744149-0.648776c13.398148-40.359196 20.732184-83.750449 20.732184-122.740462 0-27.255761-3.00545-54.686507-8.894577-81.543178l-1.206478-3.196809-0.089028 0.01535c-12.161994-46.837748-53.477982-79.259158-101.626585-79.259158-55.393611 0-97.18032 46.248324-97.18032 107.576087l-0.059352 3.047406c-0.057305 2.222621-0.116657 4.40124 0.059352 6.16849-2.796696 101.715613-83.869153 187.397041-188.559517 199.324698l-3.358491 0.36839-0.76441 274.43128 0 228.491994 13.930267 0.005117 0.587378 0.11154 3.179412-0.027629 0.01535-0.081864 485.653959 0.198521 8.685823-0.236384c19.84907 0 31.038924-5.03569 48.621371-16.344247 16.22452-10.482749 29.359678-25.266453 37.751812-42.316781 2.562359-3.76986 4.418636-7.862062 5.565762-12.251022l75.30101-336.657506c1.089821-4.225231 1.472538-8.643867 1.149173-12.56006C935.660023 478.42737 930.212964 455.900308 918.228001 436.301947zM879.148961 488.807788l-0.912789 3.799536 0.295735 0.074701-80.129992 355.2387-0.148379 0.353041c-2.296299 5.536087-6.15314 10.160407-11.101849 13.340843-3.415796 2.209318-7.185656 3.622504-11.161201 4.239558-0.470721-0.057305-0.941442-0.057305-1.413186-0.057305l-3.03308 0.086981-462.84344-0.530073-0.177032-392.299781c79.540567-35.986609 148.155295-73.959455 183.080735-167.79868l0.295735 0.074701 1.119497-3.328815c3.709485-11.263531 6.596232-22.350031 8.598842-32.909527 5.447059-28.577872 5.212722-56.422034 5.183046-58.041928-1.766226-13.502525 1.206478-24.738427 8.80555-33.394574 8.598842-9.777691 21.673625-14.151301 30.744211-14.151301 22.586414 0.766456 45.526893 30.966269 45.61592 48.163953 0.029676 0.26606 5.38873 26.945699 5.418406 55.186903 0.057305 27.65178-2.651386 45.277206-3.858888 51.828412l-0.500397 0-0.589424 3.062755c-5.595438 29.522384-15.518438 57.794287-29.41903 84.003206l-0.972141 2.856048 0.118704 0.132006c-1.885953 4.696975-2.856048 9.643638-2.856048 14.709003 0 25.032116 27.121707 25.032116 41.697681 25.032116l233.381351 0.264013 7.15598 0.221034c1.266853 0.045025 2.503007 0.089028 3.592828 0.118704l0 0.425695 5.538133-0.248663c10.453073 0 19.937075 5.344728 25.412786 14.296611C880.709503 471.080032 881.829 480.283648 879.148961 488.807788z"
                            fill="#231F20" p-id="7948"></path>
                    </svg>
                    <svg v-if="child.isLiked" t="1736220948916" class="icon" viewBox="0 0 1024 1024" version="1.1"
                        xmlns="http://www.w3.org/2000/svg" p-id="8542" width="20" height="20">
                        <path
                            d="M844.231111 391.39555600000006H659.9111109999999c-13.653333-2.275556-18.204444-15.928889-20.48-22.755556V202.52444400000002c0-45.511111-36.408889-81.92-81.92-81.92-40.96 0-75.093333 31.857778-81.92 70.542223-25.031111 157.013333-120.604444 209.351111-182.044444 225.28 2.275556 6.826667 2.275556 11.377778 2.275555 15.928889v441.457777c0 9.102222-2.275556 18.204444-6.826666 27.306666999999997h446.008888c43.235556-9.102222 72.817778-25.031111 93.297778-68.266667l93.297778-327.68c18.204444-59.16444400000001-11.377778-116.053333-77.368889-113.77777699999999zM257.137778 876.0888889999999V432.355556c0-13.653333-18.204444-25.031111-38.684445-25.031112H157.013333c-31.857778 0-59.16444400000001 27.306666999999997-59.16444400000001 59.16444500000001v373.191111c0 34.133333 25.031111 61.44 59.16444400000001 61.44h61.44c20.48 0 38.684444-11.377778 38.684445-25.031111z m0 0"
                            fill="" p-id="8543"></path>
                    </svg>
                    <i :class="['like-icon', { liked: child.isLiked }]"></i> {{ child.likes || 0 }}
                </span>
            </div>

            <!-- 回复表单 -->
            <div v-if="child.showReplyForm" class="reply-form">
                <textarea v-model="child.replyContent" placeholder="请输入回复内容" class="input-reply"></textarea>
                <button @click="submitReply(child)" class="submit-reply">提交回复</button>
            </div>

            <!-- 子评论 -->
            <div v-if="child.children && child.children.length > 0" class="comment-children">
                <comment-item :comment="child" :collapseReply="collapseReply" :level="level + 1"
                    @toggle-like="toggleLike" @submit-reply="handleReplySubmission" />
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';
import { submitCommentApi,commentToggleLikeApi } from '../../api/comentApi'
const props = defineProps({
    comment: {
        type: Object,
    },
    collapseReply: {
        type: Boolean,
        required: true,
    },
    level: {
        type: Number,
        required: true,
    },
});

const emit = defineEmits(['toggle-like', 'submit-reply']);

const toggleLike = (comment) => {
    comment.isLiked = !comment.isLiked
    comment.likes += comment.isLiked ? 1 : -1
    // 调用评论点赞接口
    const data = {
        uid: comment.uid,
        likes: comment.likes,
        isLiked: comment.isLiked
    }
    const response = commentToggleLikeApi(data);
    if (response.code === 200) {
        console.log("点赞成功")
    } else {
        console.log("点赞失败")
    }
}
// 用来判断是否是三级或更深的评论
const isThirdOrMoreLevel = (level) => {
    return level > 2;
};

const formattedDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    });
};

// 用来显示或隐藏回复表单
const toggleReplyForm = (child) => {
    child.showReplyForm = !child.showReplyForm;
    child.replyContent = '';  // 清空回复内容
};

// 提交回复的函数
const submitReply = (child) => {
    if (!child.replyContent.trim()) {
        alert('回复内容不能为空');
        return;
    }

    // 创建回复对象
    const reply = {
        content: child.replyContent,
        parentUid: child.uid,
        blogUid: child.blogUid,
    };
    console.log("reply", reply)

    // 在这里，发送提交回复请求的代码（假设存在 submit-reply 事件）
    emit('submit-reply', reply);

    // 隐藏回复表单并清空内容
    child.showReplyForm = false;
    child.replyContent = '';
};

const handleReplySubmission = async (reply) => {
    console.log('收到回复：', reply);
    try {
        const response = await submitCommentApi(reply)
        if (response.code === 200) {
            await fetchComments()
        }
    } catch (err) {

    }

};
</script>

<style scoped>
.comment-children {
    margin-top: 15px;


}

.child-comment {

    /* padding-bottom: 10px; 增加底部内边距 */
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

.comment-meta-detail {
    display: flex;
    gap: 15px;
    font-size: 0.9em;
    color: #777;
}

.comment-content {
    margin-top: 10px;
    font-size: 1em;
    color: #212020;
    line-height: 1.6;
}

.user-name {
    color: #007bff;
    /* 鲜艳的蓝色 */
    font-weight: bold;
    /* 加粗 */
    font-size: 1.1em;
    /* 稍微增大字体 */
    transition: color 0.3s ease;
    /* 添加过渡效果 */
}

.user-name:hover {
    color: #0056b3;
    /* 鼠标悬停时改变为更深的蓝色 */
}

.likes {
    cursor: pointer;
}

.username {
    font-weight: bold;
}

.like-icon {
    margin-right: 5px;
}

.comment-footer {
    margin-top: 20px;
    display: flex;
    gap: 15px;
    align-items: center;
    margin-bottom: 15px;
    /* padding-left: 10px;  给子评论增加左边距 */
    border-bottom: 1px solid #ddd;
    /* 在子评论底部添加下划线 */
}

.comment-footer .reply {
    cursor: pointer;
    color: #007bff;
    font-weight: bold;
}

.comment-footer .reply:hover {
    text-decoration: underline;
}

.reply-form {
    margin-top: 10px;
    padding: 10px;
    border: 1px solid #ddd;
    background-color: #f9f9f9;
}

.input-reply {
    width: 100%;
    height: 80px;
    padding: 10px;
    border-radius: 4px;
    border: 1px solid #ddd;
    font-size: 1em;
    resize: none;
}

.submit-reply {
    margin-top: 10px;
    padding: 8px 16px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.submit-reply:hover {
    background-color: #0056b3;
}
</style>