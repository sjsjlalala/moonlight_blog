<template>
    <Teleport to="body">
        <transition name="fade">
            <el-dialog v-model="dialogVisible" width="500px" :show-close="false" class="custom-dialog" modal>
                <template #header>
                    <div class="dialog-header">
                        <el-icon class="close-icon" @click="dialogVisible = false">
                            <Close />
                        </el-icon>
                        <span class="dialog-title">
                            {{ activeTab === 'login' ? '用户登录' : activeTab === 'register' ? '用户注册' : '找回密码' }}
                        </span>
                    </div>
                </template>

                <el-tabs v-model="activeTab" stretch>
                    <!-- 登录 -->
                    <el-tab-pane label="登录" name="login">
                        <el-form ref="loginFormRef" :model="loginForm" :rules="rules">
                            <el-form-item prop="username">
                                <el-input v-model="loginForm.username" placeholder="用户名/邮箱" clearable>
                                    <template #prefix><el-icon>
                                            <User />
                                        </el-icon></template>
                                </el-input>
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input v-model="loginForm.password" placeholder="密码"
                                    :type="showPassword ? 'text' : 'password'">
                                    <template #prefix><el-icon>
                                            <Lock />
                                        </el-icon></template>
                                    <template #suffix>
                                        <el-icon @click="showPassword = !showPassword" style="cursor: pointer;">
                                            <View v-if="showPassword" />
                                            <Hide v-else />
                                        </el-icon>
                                    </template>
                                </el-input>
                            </el-form-item>
                            <el-row justify="space-between">
                                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                                <el-button text @click="activeTab = 'forgot'">忘记密码？</el-button>
                            </el-row>
                            <el-button type="primary" block @click="handleLogin">登 录</el-button>
                        </el-form>
                    </el-tab-pane>
                    <!-- 注册 -->
                    <el-tab-pane label="注册" name="register">
                        <el-form ref="registerFormRef" :model="registerForm" :rules="rules">
                            <el-form-item prop="username">
                                <el-input v-model="registerForm.username" placeholder="用户名" clearable>
                                </el-input>
                            </el-form-item>
                            <el-form-item prop="email">
                                <el-input v-model="registerForm.email" placeholder="邮箱" clearable>
                                    <template #prefix><el-icon>
                                            <Message />
                                        </el-icon></template>
                                </el-input>
                            </el-form-item>
                            <el-form-item prop="code">
                                <el-input v-model="registerForm.code" placeholder="验证码" clearable>
                                    <template #suffix>
                                        <el-button @click="sendVerificationCode('register')" :disabled="countdown > 0">
                                            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                                        </el-button>
                                    </template>
                                </el-input>
                            </el-form-item>
                            <el-form-item prop="password">
                                <el-input v-model="registerForm.password" placeholder="密码" type="password"
                                    show-password></el-input>
                            </el-form-item>
                            <el-button type="success" block @click="handleRegister">注 册</el-button>
                        </el-form>
                    </el-tab-pane>

                    <!-- 找回密码 -->
                    <el-tab-pane label="找回密码" name="forgot">
                        <el-form ref="forgotFormRef" :model="forgotForm" :rules="rules">
                            <el-form-item prop="email">
                                <el-input v-model="forgotForm.email" placeholder="请输入注册邮箱" clearable>
                                    <template #prefix><el-icon>
                                            <Message />
                                        </el-icon></template>
                                </el-input>
                            </el-form-item>
                            <el-form-item prop="code">
                                <el-input v-model="forgotForm.code" placeholder="验证码" clearable>
                                    <template #suffix>
                                        <el-button @click="sendVerificationCode('forgot')" :disabled="countdown > 0">
                                            {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                                        </el-button>
                                    </template>
                                </el-input>
                            </el-form-item>
                            <el-form-item prop="newPassword">
                                <el-input v-model="forgotForm.newPassword" placeholder="新密码" type="password" clearable
                                    show-password>
                                </el-input>
                            </el-form-item>
                            <el-form-item prop="confirmPassword">
                                <el-input v-model="forgotForm.confirmPassword" placeholder="确认新密码" type="password"
                                    clearable show-password>
                                </el-input>
                            </el-form-item>
                            <el-button type="warning" block @click="handleForgotPassword">重置密码</el-button>
                        </el-form>
                    </el-tab-pane>
                </el-tabs>
            </el-dialog>
        </transition>
    </Teleport>
</template>

<script setup>
import { ref, defineExpose, watch } from "vue";
import { ElMessage } from "element-plus";
import { Close, User, Lock, Message, View, Hide } from "@element-plus/icons-vue";
import { loginApi, sendEmailCodeApi, registerApi, resetPasswordApi } from "@/api/authApi";
import { ElNotification } from 'element-plus'
const dialogVisible = ref(false);
const activeTab = ref("login");
const showPassword = ref(false);
const rememberMe = ref(false);

const loginForm = ref({ username: "", password: "" });
const countdown = ref(0);
let timer = null;

const registerForm = ref({ username: "", email: "", code: "", password: "" });
const forgotForm = ref({
    email: "",
    code: "",
    newPassword: "",
    confirmPassword: ""
});

const rules = {
    email: [
        { required: true, message: "请输入邮箱", trigger: "blur" },
        { type: "email", message: "邮箱格式错误", trigger: "blur" }
    ],
    code: [{ required: true, message: "请输入验证码", trigger: "blur" }],
    newPassword: [{ required: true, message: "请输入新密码", trigger: "blur" }],
    confirmPassword: [
        { required: true, message: "请确认新密码", trigger: "blur" },
        {
            validator: (rule, value, callback) => {
                if (value !== forgotForm.value.newPassword) {
                    callback(new Error("两次输入的密码不一致"));
                } else {
                    callback();
                }
            },
            trigger: "blur"
        }
    ]
};


// 校验邮箱格式
const validateEmail = (email) => {
    const regex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return regex.test(email);
};

// 处理登录
const handleLogin = async () => {
    try {
        const response = await loginApi(loginForm.value);
        if (response.code === 200) {
            const { token, user } = response.data;  // 获取 token 和 user 数据
            localStorage.setItem("token", token);  // 保存 token
            localStorage.setItem("user", JSON.stringify(user));  // 保存整个 user 对象
            ElMessage.success("登录成功");
            window.location.reload();
            dialogVisible.value = false;
        } else {
            ElMessage.error(response.message || "登录失败");
        }
    } catch (error) {
        ElMessage.error("登录出错");
    }
};

// 打开弹窗方法
const openLoginModal = (tab = "login") => {
    activeTab.value = tab;
    dialogVisible.value = true;
};
watch(() => {
    if (dialogVisible.value === true) {
        ElNotification({
            title: '警告',
            message: '登录后才能执行该操作~',
            type: 'error',
        })


    }
})

// 发送验证码
const sendVerificationCode = async (type) => {
    const email = type === "register" ? registerForm.value.email : forgotForm.value.email;
    if (!email) {
        ElMessage.warning("请输入邮箱");
        return;
    }

    // 邮箱格式校验
    if (!validateEmail(email)) {
        ElMessage.warning("邮箱格式不正确");
        return;
    }

    try {
        const data = new FormData();
        data.append("email", email);
        const response = await sendEmailCodeApi(data);
        if (response.code === 200) {
            ElMessage.success("验证码已发送，有效期5分钟");
            startCountdown();
        } else {
            ElMessage.error(response.message || "验证码发送失败");
        }
    } catch (error) {
        ElMessage.error("验证码发送出错");
    }
};

// 倒计时
const startCountdown = () => {
    countdown.value = 300;
    timer = setInterval(() => {
        if (countdown.value > 0) {
            countdown.value--;
        } else {
            clearInterval(timer);
        }
    }, 1000);
};

// 注册处理
const handleRegister = async () => {
    try {
        const response = await registerApi(registerForm.value);
        if (response.code === 200) {
            ElMessage.success("注册成功");
             
            activeTab.value = "login";
        } else {
            ElMessage.error(response.message || "注册失败");
        }
    } catch (error) {
        ElMessage.error("注册出错");
    }
};

const handleForgotPassword = async () => {
    if (forgotForm.value.newPassword !== forgotForm.value.confirmPassword) {
        ElMessage.error("两次输入的密码不一致");
        return;
    }

    try {
        const data = new FormData();
        data.append("email", forgotForm.value.email);
        data.append("code", forgotForm.value.code);
        data.append("password", forgotForm.value.newPassword);
        const response = await resetPasswordApi(data);

        if (response.code === 200) {
            ElMessage.success("密码重置成功，请重新登录");
            activeTab.value = "login";
        } else {
            ElMessage.error(response.message || "密码重置失败");
        }
    } catch (error) {
        ElMessage.error("重置密码出错");
    }
};


// 允许外部调用
defineExpose({ openLoginModal });
</script>

<style scoped>
/* 弹出层动画 */
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
}

.custom-dialog {
    border-radius: 12px;
    overflow: hidden;
}

.dialog-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 16px;
    background: linear-gradient(135deg, #6e7f96, #c1d78d);
    color: white;
}

.dialog-title {
    font-size: 18px;
    font-weight: bold;
}

.close-icon {
    cursor: pointer;
    font-size: 20px;
}

.el-input {
    width: 100%;
}
</style>
