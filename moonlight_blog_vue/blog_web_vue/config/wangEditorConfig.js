// editorConfig.js
import { ref, shallowRef } from 'vue';

// 编辑器实例
const editorRef = shallowRef(null);
// 收集上传的图片uid
const imgUidList = ref([]);
// 工具栏配置
const toolbarConfig = {
  excludeKeys: [
    "group-video"
  ]
};

// 编辑器配置
const editorConfig = {
  placeholder: '请输入内容...',
  scroll: true, // 允许滚动
  onchange (editor) {
    const html = editor.getHtml()
    console.log('editor content', html)
  },

  MENU_CONF: {}
  
};

// 插入网络图片
editorConfig.MENU_CONF['insertImage'] = {
  onInsertedImage(imageNode) {   
    if (imageNode == null) return
    const { src, alt, url, href } = imageNode
    console.log('inserted image', src, alt, url, href)
  },
  checkImage: customCheckImageFn, // 也支持 async 函数
  parseImageSrc: customParseImageSrc, // 也支持 async 函数

}
editorConfig.MENU_CONF['editImage'] = {
  onUpdatedImage(imageNode) {
    if (imageNode == null) return;
    const { src, alt, url } = imageNode;
    console.log('updated image', src, alt, url);
  },
  checkImage: customCheckImageFn, // 也支持 async 函数
  parseImageSrc: customParseImageSrc, // 也支持 async 函数
}
editorConfig.MENU_CONF['lineHeight'] = {
  lineHeightList: ['1', '1.5', '2', '2.5'],
}

// 自定义校验图片
function customCheckImageFn(src, alt, url) {
  if (!src) {
    console.log('啥都没返回')
    return;
  }
  if (src.indexOf('http') !== 0) {
    console.log('图片网址必须以 http/https 开头')
    return '图片网址必须以 http/https 开头';
  }
  return true;
}

// 转换图片链接
function customParseImageSrc(src) {
  if (src.indexOf('http') !== 0) {
    console.log('转换图片链接')
    return `http://${src}`;
  }
  return src;
}

// 上传本地图片
editorConfig.MENU_CONF['uploadImage'] = {
  // 上传图片的配置
  server:  '/api/blog-file/upload/uploadImage',
  // form-data fieldName ，默认值 'wangeditor-uploaded-image'
  fieldName: 'file',

  // 单个文件的最大体积限制，默认为 2M
  maxFileSize: 10 * 1024 * 1024, // 1M

  // 最多可上传几个文件，默认为 100
  maxNumberOfFiles: 100,

  // 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
  allowedFileTypes: ['image/*'],

  // 自定义上传参数，例如传递验证的 token 等。参数会被添加到 formData 中，一起上传到服务端。
  meta: {
    // token: 'xxx',
    // otherKey: 'yyy',
  },

  // 将 meta 拼接到 url 参数中，默认 false
  metaWithUrl: false,

  // 自定义增加 http  header
  headers: {
    // Accept: 'multipart/form-data',
    Authorization: 'Bearer ' + localStorage.getItem('token'),
  },

  // 跨域是否传递 cookie ，默认为 false
  withCredentials: false,

  // 超时时间，默认为 10 秒
  timeout: 5 * 1000, // 5 秒

  // 上传之前触发
  onBeforeUpload(file) { 
    // 可以 return
    // 1. return file 或者 new 一个 file ，接下来将上传
    // 2. return false ，不上传这个 file
    return file
  },
   // 上传进度的回调函数
   onProgress(progress) {
    // progress 是 0-100 的数字
    console.log('progress', progress)
  },
  // 单个文件上传成功之后
  onSuccess(file, res) {
    imgUidList.value.push(res.data.uid)
    console.log(`${file.name} 上传成功`, res)
    console.log("已经上传的图片uid", imgUidList)
  },

  // 单个文件上传失败
  onFailed(file, res) {
    console.log(`${file.name} 上传失败`, res)
  },

  // 上传错误，或者触发 timeout 超时
  onError(file, err, res) {
    console.log(`${file.name} 上传出错`, err, res)
  },
  // 自定义插入图片
  customInsert(res, insertFn) {
    console.log('custom insert image', res)
    insertFn(res.data.url, res.data.alt, res.data.href)
  }
}

// 模式
const mode = ref('default');

// 处理编辑器创建的函数
const handleCreated = (editor) => {
  console.log('Editor instance:', editor); // 添加日志
  editorRef.value = editor; // 记录 editor 实例，重要！
};

// 导出配置和实例管理
export {
  editorRef,
  toolbarConfig,
  editorConfig,
  mode,
  handleCreated,
  imgUidList
};
