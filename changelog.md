#changelog

1.5.5
1. 优化sp初始化机制，提升初始化效率
2. 严格区分getService, getFixedService
3. autoservice兼容gradle6.0
4. Android组件初始化增加按优先级顺序初始化
5. 修复一些bug

1.5.0
1. scope增加Soft, 对应Weak
2. 命名规范, SP -> Sp, SPA -> Spa
3. 增加混淆配置
4. 其他优化

1.4.0
1. 版本规范化、名称规范化 
2. scope增加custom类型, 支持开发者自定义scope 
3. 增加disableIntercept属性, 支持禁用service方法调用拦截
4. 扩大scope, priority取值范围

1.3.6
1. 新增sprouter模块, 扩展路由能力
2. 新增sprpc模块, 增加rpc能力
3. 修复已知问题

1.2.1
1. service方法调用过程增加同步拦截
2. 新增getFixedService方法，通过class获取该class的实例，而不会是class的子类实例
3. @Service支持inherited属性, inherited属性为true时,
   他的所有子类都会被当做一个Service
4. 修复@Service注解处理Field字段生成注入对象时机不准确问题
5. 修复@Service会对Field进行重复多次注入问题
6. 其他一些优化
