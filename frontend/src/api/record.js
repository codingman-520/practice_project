let records = [
  { id: 10001, userId: 3, skillName: 'Composition API', content: '今天系统学习了 Vue 3 的 Composition API，深入理解了 ref 与 reactive 的底层 proxy 机制，并比较了它们的区别与使用场景，做了一些基本的 todo demo。', duration: 120, createTime: '2026-07-03 20:00:00' },
  { id: 10002, userId: 4, skillName: 'TypeScript', content: '学习了 TypeScript 的 Interface 接口以及 Type 类型别名的区别，了解了范型 (Generics) 的用法，对于强类型的定义有了一定掌握。', duration: 90, createTime: '2026-07-03 19:30:00' },
  { id: 10003, userId: 3, skillName: 'Spring Boot 3', content: '配置了 Spring Boot 的开发环境，编写了第一个 RESTful API，掌握了 @RestController, @GetMapping 等常用注解。', duration: 150, createTime: '2026-07-02 18:00:00' },
  { id: 10004, userId: 5, skillName: 'MySQL', content: '练习了 SQL 常用的聚合函数如 GROUP BY 以及连接查询 JOIN，对索引的创建和慢查询优化也做了一些深入了解。', duration: 80, createTime: '2026-07-02 15:20:00' },
  { id: 10005, userId: 4, skillName: 'Docker', content: '成功编写了 Dockerfile，并将 Vue 静态项目打包成 Nginx 镜像并进行本地容器化部署。理解了 Volume 挂载和 Port 映射。', duration: 110, createTime: '2026-07-01 10:00:00' },
  { id: 10006, userId: 6, skillName: '简历优化', content: '通过 AI 诊断功能对原有的简历进行了全面体检，重点修正了项目成果描述部分，引入了 STAR 原则进行了定量化表达。', duration: 45, createTime: '2026-06-30 16:30:00' }
];

export const getRecordList = (params = {}) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      let filtered = [...records];
      if (params.userId) {
        filtered = filtered.filter(r => String(r.userId) === String(params.userId));
      }
      if (params.skillName) {
        filtered = filtered.filter(r => r.skillName.toLowerCase().includes(params.skillName.toLowerCase()));
      }
      
      const page = params.page || 1;
      const size = params.size || 5;
      const total = filtered.length;
      const start = (page - 1) * size;
      const end = start + size;
      const list = filtered.slice(start, end);

      resolve({
        code: 200,
        message: 'success',
        data: {
          list,
          total
        }
      });
    }, 400);
  });
};

export const deleteRecord = (id) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const idx = records.findIndex(r => r.id === id);
      if (idx !== -1) {
        records.splice(idx, 1);
      }
      resolve({
        code: 200,
        message: 'success'
      });
    }, 400);
  });
};
