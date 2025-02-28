<template>
    <Teleport to="body">
      <transition name="fade">
        <el-dialog
          v-model="dialogVisible"
          width="500px"
          :show-close="false"
          class="custom-dialog"
          modal
        >
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
                    <template #prefix><el-icon><User /></el-icon></template>
                  </el-input>
                </el-form-item>
                <el-form-item prop="password">
                  <el-input
                    v-model="loginForm.password"
                    placeholder="密码"
                    :type="showPassword ? 'text' : 'password'"
                    show-password
                  >
                    <template #prefix><el-icon><Lock /></el-icon></template>
                    <template #suffix>
                      <el-icon @click="showPassword = !showPassword" style="cursor: pointer;">
                        <View v-if="showPassword" /><Hide v-else />
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
                    <template #prefix><el-icon><User /></el-icon></template>
                  </el-input>
                </el-form-item>
                <el-form-item prop="email">
                  <el-input v-model="registerForm.email" placeholder="邮箱" clearable>
                    <template #prefix><el-icon><Message /></el-icon></template>
                  </el-input>
                </el-form-item>
                <el-form-item prop="password">
                  <el-input
                    v-model="registerForm.password"
                    placeholder="密码"
                    :type="showPassword ? 'text' : 'password'"
                    show-password
                  >
                    <template #prefix><el-icon><Lock /></el-icon></template>
                    <template #suffix>
                      <el-icon @click="showPassword = !showPassword" style="cursor: pointer;">
                        <View v-if="showPassword" /><Hide v-else />
                      </el-icon>
                    </template>
                  </el-input>
                </el-form-item>
                <el-button type="success" block @click="handleRegister">注 册</el-button>
              </el-form>
            </el-tab-pane>
  
            <!-- 找回密码 -->
            <el-tab-pane label="找回密码" name="forgot">
              <el-form ref="forgotFormRef" :model="forgotForm" :rules="rules">
                <el-form-item prop="email">
                  <el-input v-model="forgotForm.email" placeholder="请输入注册邮箱" clearable>
                    <template #prefix><el-icon><Message /></el-icon></template>
                  </el-input>
                </el-form-item>
                <el-button type="warning" block @click="handleForgotPassword">发送重置邮件</el-button>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-dialog>
      </transition>
    </Teleport>
  </template>
  
  <script setup>
  import { ref, defineExpose } from "vue";
  import { ElMessage } from "element-plus";
  import { Close, User, Lock, Message, View, Hide } from "@element-plus/icons-vue";
  
  const dialogVisible = ref(true);
  const activeTab = ref("login");
  const showPassword = ref(false);
  const rememberMe = ref(false);
  
  const loginForm = ref({ username: "", password: "" });
  const registerForm = ref({ username: "", email: "", password: "" });
  const forgotForm = ref({ email: "" });
  
  const rules = {
    username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
    email: [{ required: true, message: "请输入邮箱", trigger: "blur" }, { type: "email", message: "邮箱格式错误", trigger: "blur" }],
    password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  };
  
  // 处理登录
  const handleLogin = async () => {
    try {
      const response = await loginApi(loginForm.value);
      if (response.code === 200) {
        localStorage.setItem("user_token", response.data.token);
        ElMessage.success("登录成功");
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
  
  // 允许外部调用
  defineExpose({ openLoginModal });
  </script>
  
  <style scoped>
  /* 弹出层动画 */
  .fade-enter-active, .fade-leave-active {
    transition: opacity 0.3s;
  }
  .fade-enter-from, .fade-leave-to {
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
  