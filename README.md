# OICQ

技巧：
1.beginning of crash 崩溃的开始
2.OutOfMemoryError  内存溢出
3.logout真不愧是万能的，几乎所有问题都能从其中看出端倪


程序还存在的一些问题：
1.ScrollView失效，但是网上找到了一种替代的方法先用
2.因为socket似乎只能用ip不能用域名，所以暂时无法找到外网访问的方法，可能可以用租用阿里的学生服务器来完成这步。



碰到的一些问题：
1.app闪退，查看log信息，发现是DrawerLayout的NavigationView出问题。
  刚开始以为是以下两个原因导致的：
	1.NavigationView这个控件对主题的类型有要求
	2.依赖库版本号有问题
  通过更换主题和复制依赖库的方法排除了这两种可能，继续查看log，找关键词caused by发现
  是NavigationView引用了其他的布局文件，布局文件的引用图像出现问题。查看log最后发现是
  内存溢出。
  解决方法：将图片的大小变小。
  
2.点击主页头像无法响应事件。查看logout发现与未给列表控件添加适配器有关，其导致该layout
  无法正确加载。
  解决方法：将滚动控件先注释掉。