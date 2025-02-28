<template>
  <el-card class="profile-card">
    <template #header>
      <div class="card-header">
        <span>个人信息</span>
        <el-button type="danger" @click="logout" class="logout-btn">退出登录</el-button>
      </div>
    </template>

    <!-- 头像 -->
    <div class="avatar-container">
      <el-upload class="avatar-uploader" :http-request="customUploadRequest" :show-file-list="false"
        :before-upload="beforeAvatarUpload" :on-success="handleAvatarSuccess">
        <div class="avatar-frame">
          <img v-if="user.avatarUid" :src="avatar.url" class="avatar" />
          <el-icon v-else class="avatar">
            <UserFilled />
          </el-icon>
        </div>
      </el-upload>
      <p class="username">{{ user.username }}</p>

      <!-- 个性签名 -->
      <p class="remark" @click="editRemark">
        <span v-if="!isEditingRemark">{{ user.remarks || "点击编辑个性签名..." }}</span>
        <el-input v-else v-model="profileForm.remarks" class="remark-input" maxlength="100" show-word-limit
          ref="remarkInputRef" @blur="saveRemark" />
      </p>
    </div>

    <!-- 当前邮箱和修改按钮 -->
    <el-form ref="profileFormRef" :model="profileForm" :rules="rules" label-width="100px">

      <!-- 其他个人信息表单 -->
      <el-form-item label="用户名" prop="username">
        <el-input v-model="profileForm.username" placeholder="请输入用户名" />
      </el-form-item>


      <el-form-item label="当前邮箱">
        <span>{{ user.email }}</span>
        <el-button @click="openVerifyOldEmailDialog" size="mini">修改</el-button>
      </el-form-item>

      <!-- 验证旧邮箱验证码弹窗 -->
      <el-dialog v-model="verifyOldEmailDialogVisible" title="获取旧邮箱验证码">
        <el-form :model="verifyOldEmailForm" label-width="100px">
          <el-form-item>
            <div class="old-email-code">
              <span>向 {{ user.email }} 邮箱获取验证码</span>
              <div class="new-email-code">
                <el-input v-model="verifyOldEmailForm.code" placeholder="请输入验证码" class />
                <el-button @click="sendOldEmailCode" size="mini">获取验证码</el-button>
              </div>
            </div>


          </el-form-item>
          <div class="dialog-footer">
            <el-button @click="submitOldEmailVerification" type="primary">验证</el-button>
            <el-button @click="closeVerifyOldEmailDialog">取消</el-button>
          </div>

        </el-form>
      </el-dialog>

      <!-- 修改邮箱表单（只有在验证通过后才显示） -->
      <el-dialog v-model="modifyEmailDialogVisible" title="修改邮箱">
        <el-form :model="modifyEmailForm" label-width="100px">
          <el-form-item label="新邮箱">
            <el-input v-model="modifyEmailForm.newEmail" placeholder="请输入新邮箱" />
          </el-form-item>

          <el-form-item>
            <div class="new-email-code">
              <el-input v-model="modifyEmailForm.newEmailCode" placeholder="请输入验证码" />
              <el-button @click="sendNewEmailCode" size="mini">获取验证码</el-button>
            </div>

          </el-form-item>

          <el-button @click="submitModifyEmail" type="primary">确认修改</el-button>
          <el-button @click="closeModifyEmailDialog">取消</el-button>
        </el-form>
      </el-dialog>
      <el-button type="primary" @click="updateProfile" class="update-btn">保存修改</el-button>

    </el-form>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { UserFilled } from "@element-plus/icons-vue";
import { updateUserApi, updateEmailApi } from "@/api/userApi";
import { sendEmailCodeApi, validateEmailCodeApi } from '@/api/authApi'
import axios from 'axios';
// 获取用户信息
const user = ref(JSON.parse(localStorage.getItem("user")) || {});
let avatar = ref({
  uid: '',
  url: user.value.avatarUid || "",
  href: ''
});
// 表单数据
const profileForm = ref({
  username: user.value.username || "",
  email: user.value.email || "",
  remarks: user.value.remarks || "",
  newPassword: "",
  confirmPassword: "",
  avatarUid:  ""
});
// 控制验证旧邮箱弹窗显示
const verifyOldEmailDialogVisible = ref(false);
const verifyOldEmailForm = ref({
  code: ""
});

// 控制修改邮箱弹窗显示
const modifyEmailDialogVisible = ref(false);
const modifyEmailForm = ref({
  newEmail: "",
  newEmailCode: ""
});
// 头像地址
const avatarUrl = computed(() =>
  user.value.avatarUid ? `https://your-avatar-url.com/${user.value.avatarUid}.jpg` : "path/to/default-avatar.svg"
);

// 处理个性签名
const isEditingRemark = ref(false);
const remarkInputRef = ref(null);

const editRemark = () => {
  isEditingRemark.value = true;
  nextTick(() => {
    remarkInputRef.value.focus();
  });
};

const saveRemark = () => {
  isEditingRemark.value = false;
  user.value.remark = profileForm.value.remark;
  localStorage.setItem("user", JSON.stringify(user.value));
};

// 表单验证规则
const rules = {
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "邮箱格式错误", trigger: "blur" },
  ],
  newPassword: [
    { min: 6, message: "密码至少6位", trigger: "blur" },
  ],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (value !== profileForm.value.newPassword) {
          callback(new Error("两次密码输入不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

// 更新个人信息
const updateProfile = async () => {
  try {
    if (avatar.value.uid)
      profileForm.value.avatarUid = avatar.value.uid;
    const response = await updateUserApi(profileForm.value);
    if (response.code === 200) {
      user.value.avatarUid = avatar.value.uid;
      user.value.username = profileForm.value.username;
      user.value.email = profileForm.value.email;
      user.value.remarks = profileForm.value.remarks;
      user.value.avatarUrl = avatar.value.url;
      window.location.reload();
      localStorage.setItem("user", JSON.stringify(user.value));
      ElMessage.success("信息更新成功！");
    } else {
      ElMessage.error(response.message || "更新失败");
    }
  } catch (error) {
    ElMessage.error("更新出错");
  }
};

// 上传头像前检查
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === "image/jpeg" || file.type === "image/png";
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isJPG) {
    ElMessage.error("头像必须是 JPG 或 PNG 格式！");
  }
  if (!isLt2M) {
    ElMessage.error("头像大小不能超过 2MB！");
  }
  return isJPG && isLt2M;
};

// 上传头像成功
const handleAvatarSuccess = async (response) => {
  if (response.errno === 0) {
    user.value.avatarUid = response.data.uid;
    avatar.value.uid = response.data.uid;
    avatar.value.url = response.data.url;
    avatar.value.href = response.data.href;
    localStorage.setItem("user", JSON.stringify(user.value));
    ElMessage.success("头像上传成功！");
  } else {
    ElMessage.error("头像上传失败！");
  }
};


// 上传头像api
const customUploadRequest = (options) => {
  const { file, onSuccess, onError } = options;
  const formData = new FormData();
  formData.append('file', file);

  axios.post('/api/blog-file/upload/uploadImage', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': 'Bearer ' + localStorage.getItem('token') // 自定义请求头
    }
  })
    .then(response => {
      handleAvatarSuccess(response.data); // 上传成功后的回调
    })
    .catch(error => {
      onError(error); // 上传失败后的回调
    });
}





// 打开验证旧邮箱的弹窗
const openVerifyOldEmailDialog = () => {
  verifyOldEmailDialogVisible.value = true;
};

// 关闭验证旧邮箱弹窗
const closeVerifyOldEmailDialog = () => {
  verifyOldEmailDialogVisible.value = false;
};

// 发送旧邮箱验证码
const sendOldEmailCode = () => {
  const email = user.value.email;
  if (!email) {
    ElMessage.error("当前邮箱为空");
    return;
  }
  const data = new FormData();
  data.append("email", email);
  sendEmailCodeApi(data)
    .then(response => {
      console.log(response);
      if (response.code === 200) {
        ElMessage.success("旧邮箱验证码已发送");
      } else {
        ElMessage.error(response.data.message);
      }
    })
    .catch(() => {
      ElMessage.error("发送旧邮箱验证码失败");
    });
};

// 提交验证旧邮箱验证码
const submitOldEmailVerification = () => {
  const { code } = verifyOldEmailForm.value;
  if (!code) {
    ElMessage.error("请输入旧邮箱验证码");
    return;
  }
  const data = new FormData();
  data.append("code", code);
  data.append('email', user.value.email)
  validateEmailCodeApi(data)
    .then(response => {
      if (response.code === 200) {
        // 验证成功后，打开修改邮箱表单
        verifyOldEmailDialogVisible.value = false;
        modifyEmailDialogVisible.value = true;
      } else {
        ElMessage.error(response.data.message || "验证失败");
      }
    })
    .catch(() => {
      ElMessage.error("验证旧邮箱验证码失败");
    });
};

// 发送新邮箱验证码
const sendNewEmailCode = () => {
  const email = modifyEmailForm.value.newEmail;
  if (!email) {
    ElMessage.error("请输入新邮箱");
    return;
  }
  const data = new FormData();
  data.append("email", email);
  sendEmailCodeApi(data)
    .then(response => {
      if (response.code === 200) {
        ElMessage.success("新邮箱验证码已发送");
      } else {
        ElMessage.error(response.data.message);
      }
    })
    .catch(() => {
      ElMessage.error("发送新邮箱验证码失败");
    });
};

// 提交修改邮箱请求
const submitModifyEmail = () => {
  const { newEmail, newEmailCode } = modifyEmailForm.value;
  if (!newEmail || !newEmailCode) {
    ElMessage.error("所有字段都必须填写");
    return;
  }

  const data = new FormData();
  data.append("code", modifyEmailForm.value.newEmailCode);
  data.append('email', modifyEmailForm.value.newEmail)
  updateEmailApi(data)
    .then(response => {
      if (response.data.success) {
        user.value.email = newEmail;
        localStorage.setItem("user", JSON.stringify(user.value));
        ElMessage.success("邮箱修改成功！");
        modifyEmailDialogVisible.value = false;
      } else {
        ElMessage.error(response.data.message || "邮箱修改失败");
      }
    })
    .catch(() => {
      ElMessage.error("邮箱修改请求失败");
    });
};

// 退出登录
const logout = () => {
  ElMessageBox.confirm("确定要退出登录吗？", "提示", {
    confirmButtonText: "退出",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    ElMessage.success("已退出");
    setTimeout(() => {
      location.reload();
    }, 1000);
  });
};

// 初始化
onMounted(() => {
  if (!user.value.username) {
    ElMessage.warning("请先登录！");
  }
  if (user.value.avatarUid) {
    avatar.value.url = user.value.avatarUrl;
  }
});
</script>

<style scoped>
.profile-card {
  width: 100%;
  margin: auto;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatar-container {
  text-align: center;
  margin-bottom: 10px;
}

.avatar-frame {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  display: inline-block;
  border: 3px solid #409eff;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.username {
  font-size: 20px;
  font-weight: bold;
  margin-top: 10px;
}

.remark {
  font-size: 14px;
  color: #666;
  margin-top: 10px;
  margin-bottom: 20px;
  text-decoration: underline;
  cursor: pointer;
}

.remark-input {
  width: 80%;
  margin: 0 auto;
}

.new-email-code {
  display: flex;
  justify-content: space-between;
}

.dialog-footer {
  text-align: center;
}
.old-email-code {
  display: flex;
  flex-direction: column;
}
.update-btn {
  margin: auto;
  margin-top: 20px;
  display: block;
  width: 35%;
}
</style>