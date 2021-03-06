#Android SDK开发指南

版本: 2.8.0
日期: 2018-09-28

[toc]

##简介

提供云课堂基础功能，包括推流，拉流，聊天等。为用户提供快速，简便的方法开展自己的实时互动课堂。

##功能特性

| |  |  |
| --- | --- | --- |
| 功能特性 | 描述 | 备注 |
| 推拉流 | 支持多人音视频互动 |  |
| 文档 | 支持文档的展示 |  |
| 白板画笔| 支持展示白板及画笔操作 |  |
| 聊天 | 支持图片、文字形式聊天 |  |
| 前后摄像头切换 | 支持手机前后摄像头切换 |  |
| 横竖屏 | 支持手机横屏或竖屏模式布局 |  |
| 点名 | 支持点名功能 |  |
| 答题卡 | 支持答题卡功能 |  |
| 计时器 | 支持计时器功能 |  |
| 布局切换 | 支持实时同步切换课堂布局 |  |
| 连麦模式 | 支持举手、自由、自动三种连麦模式 |  |
| 码率设置 | 支持码率变更 |  |
| 后台播放 | 支持直播退到后台只播放音频 |  |
| 支持https协议 | 支持接口https请求 |  |

##SDK下载

SDK下载地址：
http://liveclass.csslcloud.net/SDK/Class-android-2.8.0.zip

##版本更新记录
| |  |  |
| --- | --- | --- |
| 更新日期 | 版本号 | 更新内容 |
| 2017-10-24 | 1.3.0 | 1、增加授权标注功能<br>2、优化视频框UI<br>3、横屏版讲课模式增加隐藏视频功能 |
| 2017-11-17 | 2.0.0 | 1、基础sdk重构<br>2、增加自动连麦模式<br>|
| 2017-11-30 | 2.1.0 | 1、增加插播媒体功能<br>2、优化桌面共享功能<br>3、合流主视频模版优化<br>|
| 2017-12-14 | 2.1.1 | 1、修复了登录bug<br>|
| 2018-01-05 | 2.1.2 | 1、修复了跨终端回放文档不展示的问题<br>2、优化相机资源释放的逻辑<br>3、优化画笔操作和翻页时间戳机制<br>|
| 2018-01-11 | 2.1.3 | 1、优化部分机型使用SDK拍照功能时出现黑屏的问题<br>|
| 2018-03-01 | 2.1.4 | 1、新增老师版本横屏版，以及画笔功能<br />2、推流上报接口增加码率<br />3、session缺陷的修复<br />4、迁移socket.io到基础版<br />5、在基础版本退出房间添加break接口<br />6、修复若干bug<br />|
| 2018-03-16 | 2.1.5 | 1、修复若干bug<br>2、新增蓝牙耳机的监听，以及打电话通话屏蔽对方直播声音<br>3、优化界面<br>|
| 2018-04-08 | 2.1.6 | 1、修复若干bug<br />2、适配硬解码格式，适配手机机型<br />3、处理黑屏问题<br />4、网络切换断网等问题<br />|
| 2018-05-03 | 2.2.0 | 1、修复若干bug<br />2、屏蔽下麦按钮<br /> 3、双击视频放到文档区<br />|
| 2018-05-18 | 2.3.0 | 1、手机适配优化，及修复历史BUG<br /> 2、ppt动画功能<br />3、设为讲师功能<br />4、华为输入法适配<br /> 5、耳机适配<br />6、优化节点切换功能<br />|
| 2018-07-03 | 2.4.0 | 1、兼容手机蓝牙和电话接听监听的bug修复<br />2、修复横屏模式中，摄像头放大后，按系统的退回建退出摄像头全屏后，白板变为了竖屏模式<br /> 3、修复反复的退出课程，然后再进入。会出现，进入白板后，摄像头画面无法加载<br />4、初次使用用户，被老师邀请上麦时未开启麦克风权限，没有声音<br />5、竖屏状态下，被老师设置为助理讲师，放大白板切换到新的PPT页面后。翻页的控件会消失<br />6、竖屏状态下，被老师设置为助理讲师，选择PPT页面更换后。左下角按钮会消失<br />7、设置一个用户为讲师后。所有的竖屏安卓互动端，都会获得PPT的切换翻页切换权限<br />8、踢出房间后，安卓端学生不会自动退出课程。学生再点击上麦或发言按钮，APP会发生崩溃<br />9、横屏状态下，在聊天栏中连续的发送图片。摄像头和工具栏会不停的向下移动位置<br />10、互动用户A被全体下麦后。用户A再次去连麦，会一直显示“排麦中”。无法正常上麦<br />11、教师端点击下方的视频放大，APP端放大视频时会发现放大的视频和点击的视频不是同一个<br />12.添加隐身者身份，以及修复历史BUG<br />|
| 2018-08-01 | 2.5.0 | 1、增加暖场视频，优化原来老师端界面<br />|
| 2018-08-16 | 2.6.0 | 1、头脑风暴、投票以及上报系统<br />|
| 2018-08-23 | 2.7.0 | 1、添加鲜花奖杯功能<br />|
| 2018-09-29 | 2.8.0 | 1、添加极速文档功能<br />2、修复手机兼容性问题<br />|