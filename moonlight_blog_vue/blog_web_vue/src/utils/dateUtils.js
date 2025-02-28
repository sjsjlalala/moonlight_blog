const formatDate = (dateStr) => {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN', options); // 显示格式: 2025年2月1日
}

export {
    formatDate
}