# OICQ
程序还存在的一些问题：
1.ScrollView失效，但是网上找到了一种替代的方法先用



碰到的一些问题：
1.app闪退，查看log信息，发现是DrawerLayout的NavigationView出问题。
  刚开始以为是以下两个原因导致的：
	1.NavigationView这个控件对主题的类型有要求
	2.依赖库版本号有问题
  通过更换主题和复制依赖库的方法排除了这两种可能，继续查看log，找关键词caused by发现
  是NavigationView引用了其他的布局文件，布局文件的引用图像出现问题。解决方法：