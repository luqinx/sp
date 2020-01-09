组件化开发过程中，随着组件越来越多，组件的之前的交互就会变得非常的复杂，此时组件间通信变得尤其的重要，ServicePool就是为组件化而生，用最简单的方式进行组件间通信。使用依赖注入，按需灵活注入组件，同时支持组件热插拔效果。可配置组件生命周期，做到组件及回收，充分利用懒加载的思想，解决组件初始化耗时导致的app启动速度问题。


### ServicePool基础能力

<!--![avatar](images/simple_struct.png)-->
<img src="images/simple_struct.png" width="500" height="382"/>