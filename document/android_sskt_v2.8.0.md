# Android小班课SDK开发指南

版本: 2.7.0
日期: 2018-08-23

[TOC]

## 1. 文档介绍
### 1.1 文档目的
方便客户对接推流SDK，介绍主要API。
### 1.2 术语和缩写解释
无
### 1.3 最低版本要求
Android SDK API level 16 or above<br>
CC提供Demo最低版本支持Android SDK API level 19

## 2. SDK使用介绍
**清单文件**

    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.camera.autofocus" />
    <uses-feature
        android:glEsVersion="0x00020000"
        android:required="true" >
    </uses-feature>
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.WRITE_SETTINGS"/>

### 2.1 关于arm64的支持配置
在app->build.gradle进行配置  
在android->defaultConfig里面添加  

    ndk {
            abiFilters "arm64-v8a", "armeabi-v7a", "x86"
        }

在dependencies里面添加

	dependencies {
		...
		compile('io.socket:socket.io-client:0.8.3') {
	    // excluding org.json which is provided by Android
	    exclude group: 'org.json', module: 'json'
	}
	compile 'com.squareup.okhttp3:okhttp:3.8.1'
	compile 'com.squareup.okhttp3:logging-interceptor:3.8.1'
		...
	}

### 2.2 依赖库
ccclassroom.jar : sdk核心库  
libjingle_peerconnection_so.so : sdk动态库
### 2.3 加载流程
复制libs包的jar到工程libs文件夹，将动态库放在src/mian/jinLibs，您就可以使用cc提供的小班课SDK功能了。
### 2.4 混淆编译
pushsdk.jar已经混淆过，如果需要对应用进行混淆，需要在混淆的配置文件增加如下代码，以防止pushsdk的二次混淆   

	-keep public class com.bokecc.sskt.**{*;}
	-keep public interface com.bokecc.sskt.**{*;}


## 3. 主要API介绍
### 3.1 CCInteractSDK
```
/**
 * 初始化
 * @param context  上下文
 * @param isLogOut 是否输出日志
 */
public static void init(Context context, boolean isLogOut)
```
### 3.2 CCInteractSession
```
/**
 * 登录
 *
 * @param roomId        直播间id
 * @param userId        用户id
 * @param role          用户角色 {@link Role}
 * @param username      用户名
 * @param password      用户密码 学生密码为空(null || "")的时候使用免密码模式登录 教师没有该模式
 * @param loginListener 回调 {@link OnLoginStatusListener}
 */
public void login(String roomId, String userId, @Role int role, String username, String password, final OnLoginStatusListener loginListener)                 
```
```
/**
 * 释放全部资源
 */
public void releaseAll()
```
```
/**
 * 加入房间
 */
public void joinRoom(AtlasCallBack callBack)
```
```
/**
 * 离开房间
 */
public void leaveRoom(AtlasCallBack callBack)                
```
```
/**
 * 关闭视频
 *
 * @param isDoBroadcast 是否广播通知所有人
 */
public void disableVideo(boolean isDoBroadcast)               
```
```
/**
 * 关闭音频
 *
 * @param isDoBroadcast 是否广播通知所有人
 */
public void disableAudio(boolean isDoBroadcast)               
```
```
/**
 * 开启视频
 *
 * @param isDoBroadcast 是否广播通知所有人
 */
public void enableVideo(boolean isDoBroadcast)                
```
```
/**
 * 开启音频
 *
 * @param isDoBroadcast 是否广播通知所有人
 */
public void enableAudio(boolean isDoBroadcast)                   
```
```
/**
 * 暂停指定流的音频数据
 */
public void pauseAudio(SubscribeRemoteStream remoteStream, final AtlasCallBack<Void> callBack)
```
```
/**
 * 恢复指定流的音频数据
 */
public void playAudio(SubscribeRemoteStream remoteStream, final AtlasCallBack<Void> callBack)
```
```
/**
 * 设置默认开启摄像头
 *
 * @param cameraType 摄像头类型 
 */
public void setCameraType(LocalCameraStreamParameters.CameraType cameraType)                 
```
```
/**
 * 切换摄像头
 *
 * @param cameraSurfaceRenderer {@link CCSurfaceRenderer}
 * @param listener {@link AtlasCallBack}
 */
public void switchCamera(@Nullable final CCSurfaceRenderer cameraSurfaceRenderer, final AtlasCallBack<Void> listener)                 
```
```
/**
 * 设置帧率
 */
public void setFps(int fps)
```
```
/**
 * 设置分辨率
 * @param resolution {@link LocalStreamConfig}
 * @see LocalStreamConfig.Resolution_480P
 * @see LocalStreamConfig.Resolution_720P
 */
public void setResolution(@LocalStreamConfig.Resolution int resolution)
```
```
/**
 * 初始化本地流
 */
public void initCameraStream(@MeidaMode int mode) throws StreamException                
```
```
/**
 * 获取相机参数配置
 * @return {@link Parameters}
 * @see #setCameraParameters(Parameters)
 */
public
@Nullable
Parameters getCameraParameters() 
```
```
/**
 * 设置相机参数
 * @param parameters {@link Parameters}
 * @see #getCameraParameters()
 */
public void setCameraParameters(@NonNull Parameters parameters)
```
```
/**
 * 关闭本地流
 */
public void closeLocalCameraStream()                 
```
```
/**
 * 订阅远程流
 */
public synchronized void subscribe(SubscribeRemoteStream remoteStream, AtlasCallBack<SubscribeRemoteStream> callBack) throws StreamException                 
```
```
/**
 * 取消订阅远程流
 */
public synchronized void unsubscribe(SubscribeRemoteStream remoteStream, AtlasCallBack<Void> callBack) throws StreamException                
```
```
/**
 * 发布本地流
 */
public synchronized void publish(final AtlasCallBack<Void> callBack)                 
```
```
/**
 * 添加第三方推流地址
 *
 * @param serverUrl 地址
 * @param callBack  回调
 * @see #getPushUrl()
 * @see #getPushUrl()
 */
public void addExternalOutput(String serverUrl, final AtlasCallBack<Void> callBack)               
```
```
/**
 * 移除第三方推流地址
 *
 * @param serverUrl 地址
 * @param callBack  回调
 * @see #getPushUrl()
 * @see #getPushUrl()
 */
public void removeExternalOutput(String serverUrl, final AtlasCallBack<Void> callBack)              
```
```
/**
 * 停止发布本地流
 */
public void unpublish(final AtlasCallBack callBack)               
```
```
/**
 * 获取本地流id
 */
public String getLocalStreamId()                
```
```
/**
 * 发送停止推流的指令
 */
public void sendStopCommand(final AtlasCallBack<Void> callBack)                
```
```
/**
 * 邀请学生上麦
 */
public void inviteUserLianMai(String userId, final AtlasCallBack<Void> callBack)                
```
```
/**
 * 取消邀请
 */
public void cancleInviteUserLianMai(String userId, AtlasCallBack<Void> callBack)               
```
```
/**
 * 自动连麦==>举手
 *
 * @param flag     <ul><li>true 举手</li><li>false 取消举手</li></ul>
 * @param callBack {@link AtlasCallBack}
 */
public void handup(boolean flag, final AtlasCallBack<Void> callBack)
```
```
/**
 * 同意举手
 */
public void certainHandup(String userId, final AtlasCallBack<Void> callBack)                
```
```
/**
 * 拒绝老师连麦邀请
 */
public void refuseTeacherInvite(AtlasCallBack<Void> callBack)                
```
```
/**
 * 接收老师邀请
 */
public void acceptTeacherInvite(final AtlasCallBack<Void> callBack)                
```
```
/**
 * 请求排麦/举手
 */
public void requestLianMai(final AtlasCallBack<Void> callBack)                
```
```
/**
 * 取消连麦/举手
 */
public void cancleLianMai(final AtlasCallBack<Void> callBack)                 
```
```
/**
 * 踢人下麦
 */
public void kickUserFromLianmai(@NonNull String userId, AtlasCallBack<Void> callBack)                 
```
```
/**
 * 主动下麦
 */
public void stopLianMai(AtlasCallBack<Void> callBack)                
```
```
/**
 * 发布公告
 * @param announcement 公告内容
 */
public void releaseAnnouncement(@NonNull String announcement, final AtlasCallBack<Void> callBack)               
```
```
/**
 * 移除公告
 */
public void removeAnnouncement(final AtlasCallBack<Void> callBack)                  
```
```
/**
 * 获取当前基本信息
 * @return {@link CCInteractBean}
 */
public CCInteractBean getInteractBean()
```
```
/**
 * 获取直播间信息
 *
 * @return {@link Room}
 */
public Room getRoom()                  
```
```
/**
 * 获取直播间第三方推流地址
 */
public String getPushUrl()
```
```
/**
 * 获取当前用户名称
 */
public String getUserName()                  
```
```
/**
 * 获取当前用户id
 */
public String getUserIdInPusher()                 
```
```
/**
 * 获取初始时计时器开始时间
 */
public long getInitStartTime()
```
```
/**
 * 获取初始时计时器剩余时间
 */
public long getInitLastTime()
```
```
/**
 * 获取当前用户音频是否开放
 */
public boolean isAllowAudio()                
```
```
/**
 * 获取当前用户视频是否开放
 */
public boolean isAllowVideo()               
```
```
/**
 * 获取当前用户是否被授权标注
 */
public boolean isAllowDraw()
```
```
/**
 * 获取当前用户是否被锁定
 */
public boolean isLock() 
```
```
/**
 * 当前房间是否禁言
 */
public boolean isRoomGag()                
```
```
/**
 * 当前用户是否被禁言
 */
public boolean isGag()                 
```
```
/**
 * 查看指定用于是否被禁言
 */
public boolean isGag(@NonNull String userId)                
```
```
/**
 * 旁听用户是否被禁言
 *
 * @param auditorId 旁听用户id
 */
public boolean isAuditorGag(String auditorId)
```
```
/**
 * 判断当前直播间是否在直播
 */
public boolean isRoomLive()                
```
```
/**
 * 获取模板
 */
public
@Template
int getTemplate()                 
```
```
/**
 * 获取直播间最大的连麦人数
 */
public int getRoomMaxStreams()                 
```
```
/**
 * 获取房间最大人数
 */
public int getRoomMaxMemberCount()                 
```
```
/**
 * 获取学生码率
 */
public int getTalkerBitrate()                
```
```
/**
 * 获取教师码率
 */
public int getPresenterBitrate()                 
```
```
/**
 * 获取连麦多媒体格式
 */
public
@MediaMode
int getMediaMode()                 
```
```
/**
 * 获取连麦模式
 */
public
@LianmaiMode
int getLianmaiMode()                 
```
```
/**
 * 获取直播间人数
 */
public void getUserCount()                 
```
```
/**
 * 获取直播间用户列表
 */
public
@Nullable
ArrayList<User> getUserList()                  
```
```
/**
 * 获取可以被订阅的流集合
 */
public CopyOnWriteArrayList<SubscribeRemoteStream> getSubscribeRemoteStreams()                 
```
```
/**
 * 文档翻页
 *
 * @param docid     文档id
 * @param fileName  文档名称
 * @param totalPage 文档总页数
 * @param url       当前文档URL
 * @param curPage   当前页码
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限</li></ul>
 */
public boolean docPageChange(String docid, String fileName, int totalPage, String url, int curPage)               
```
```
/**
 * 获取当前跟随用户id
 */
public String teacherFollowUserID()                 
```
```
/**
 * 主视频模式跟随，老师切换视频(userId为空表示关闭)
 */
public void changeMainStreamInSigleTemplate(String userId, final AtlasCallBack<Void> callBack)                  
```
```
/**
 * 修改合流主视频
 *
 * @param streamId 主视频流id
 */
public void setRegion(String streamId)                  
```
```
/**
 * 发送图片
 *
 * @param url 图片地址
 */
public void sendPic(String url)
```
```
/**
 * 发送消息
 * @param msg 消息内容
 */
public void sendMsg(String msg)                
```
```
/**
 * 设置连麦媒体模式
 *
 * @param mediaMode {@link MediaMode}
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限</li></ul>
 */
public boolean setMediaMode(@MediaMode int mediaMode, AtlasCallBack<Void> callBack)              
```
```
/**
 * 设置连麦模式
 *
 * @param lianmaiMode {@link LianmaiMode}
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限</li></ul>
 */
public boolean setLianmaiMode(@LianmaiMode int lianmaiMode, AtlasCallBack<Void> callBack)               
```
```
/**
 * 更新学生码率
 *
 * @param bitrate 码率
 */
public void changeRoomStudentBitrate(int bitrate, AtlasCallBack<Void> callBack)                 
```
```
/**
 * 更新老师的码率
 *
 * @param bitrate 码率
 */
public void changeRoomTeacherBitrate(int bitrate, AtlasCallBack<Void> callBack)                
```
```
/**
 * 设置最大连麦个数
 *
 * @param count 连麦个数
 */
public void changeRoomMaxCount(int count, AtlasCallBack<Void> callBack)                
```
```
/**
 * 设置直播间模板
 *
 * @param template 模板
 */
public void changeRoomTemplateMode(@Template int template, AtlasCallBack<Void> callBack)               
```
```
/**
 * 设置课堂名称
 *
 * @param roomName 名称
 */
public void setRoomName(@NonNull final String roomName, final AtlasCallBack<Void> callBack)                
```
```
/**
 * 设置课堂简介
 *
 * @param roomDesc 简介
 */
public void setRoomDesc(@NonNull final String roomDesc, final AtlasCallBack<Void> callBack)              
```
```
/**
 * 全部禁言
 * @param callBack
 */
public void gagAll(AtlasCallBack<Void> callBack)                 
```
```
/**
 * 取消群体禁言
 * @param callBack
 */
public void cancelGagAll(AtlasCallBack<Void> callBack)               
```
```
/**
 * 开启/关闭指定学生禁言
 *
 * @param flag   <ul><li>true 开启禁言</li><li>false 关闭禁言</li></ul>
 * @param userId 用户id
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限/数据出错</li></ul>
 */
public boolean gagOne(boolean flag, @NonNull String userId)                 
```
```
/**
 * 踢出指定id的学生
 *
 * @param userId 用户id
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限/数据出错</li></ul>
 */
public boolean kickUserFromRoom(@NonNull String userId)                
```
```
/**
 * 开关指定id的学生音频
 *
 * @param flag   <ul><li>true 开启音频</li><li>false 关闭音频</li></ul>
 * @param userId 用户id
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限/数据出错</li></ul>
 */
public boolean toggleAudio(boolean flag, @NonNull String userId)                  
```
```
/**
 * 开关指定id的学生视频
 *
 * @param flag   <ul><li>true 开启视频</li><li>false 关闭视频</li></ul>
 * @param userId 用户id
 * @return <ul><li>true 执行</li><li>false 拒绝-没有权限/数据出错</li></ul>
 */
public boolean toggleVideo(boolean flag, @NonNull String userId)                 
```
```
/**
 * 渲染流数据
 */
public void attachLocalCameraStram(CCSurfaceRenderer surfaceRenderer)                  
```
```
/**
 * 停止渲染流数据
 */
public void detachLocalCameraStram(CCSurfaceRenderer surfaceRenderer)                 
```
```
/**
 * 开始点名
 *
 * @param seconds 点名持续时间
 */
public boolean startNamed(long seconds)
```
```                
/**
 * 学生签到
 */
public boolean studentNamed()
```
```
/**
 * 获取应答学生列表
 */
public ArrayList<String> getStudentNamedList()
```
```
/**
 * 设置文档展示界面
 *
 * @param docView {@link IDocView}
 */
public void setDocView(IDocView docView)
```
```
/**
 * 获取指定文档
 *
 * @param roomid 直播间id
 * @param docid  文档id
 */
public void getRoomDoc(@Nullable String roomid, @NonNull String docid, final AtlasCallBack<DocInfo> callBack)
```
```
/**
 * 获取直播间文档
 */
public void getRoomDocs(@Nullable String roomid, final AtlasCallBack<RoomDocs> callBack)
```
```
/**
 * 删除文档
 *
 * @param docid 文档id
 */
public void delDoc(String roomid, String docid, final AtlasCallBack<Void> callBack)
```
```
/**
 * 获取直播间信息
 *
 * @param roomId 房间id
 */
public void getRoomMsg(String roomId, final AtlasCallBack<String[]> callBack)
```
```
/**
 * 初始化CCSurfaceRenderer
 */
public void initSurfaceContext(CCSurfaceRenderer surfaceRenderer)
```
```
/**
 * 初始化CCSurfaceRenderer
 * @param rendererEvents {@link RendererCommon}
 */
public void initSurfaceContext(CCSurfaceRenderer surfaceRenderer, RendererCommon.RendererEvents rendererEvents)
```
### 3.3 静态常量释义
```
共享桌面流id
    public static final String SHARE_SCREEN_STREAM_ID = "isScreen";
共享桌面流名称
    public static final String SHARE_SCREEN_STREAM_NAME = "共享桌面";
```
```
用户设备web（暂时包括移动端H5）
    public static final int PC = 0;
用户设置移动端
    public static final int MOBILE = 1;
```
```
连麦状态
    public static final int LIANMAI_STATUS_IDLE = 0; // 空闲
    public static final int LIANMAI_STATUS_IN_MAI = 1; // 排麦
    public static final int LIANMAI_STATUS_UP_MAI = 2; // 上麦
    public static final int LIANMAI_STATUS_MAI_ING = 3; // 连麦
    public static final int LIANMAI_STATUS_INVITE_MAI = 4; // 邀请
```
```
用户角色 老师
    public static final int PRESENTER = 0;
用户角色 学生
    public static final int TALKER = 1;
用户角色 旁听
    public static final int AUDITOR = 2;
```
```
多媒体模式 仅音频
    public static final int MEDIA_MODE_AUDIO = 0;
多媒体模式 音视频
    public static final int MEDIA_MODE_BOTH = 1;
```
```
连麦模式
    public static final int LIANMAI_MODE_FREE = 0; // 自由
    public static final int LIANMAI_MODE_NAMED = 1; // 点名
    public static final int LIANMAI_MODE_AUTO = 3; // 自动
```
```
模板
    public static final int TEMPLATE_SPEAK = 1; // 主讲
    public static final int TEMPLATE_SINGLE = 2; // 主视频
    public static final int TEMPLATE_TILE = 4; // 平铺
    public static final int TEMPLATE_DOUBLE_TEACHER = 16; // 双师
```
```
关于disconnect 网络
    public static final int PLATFORM_NETWORK = 1;
关于disconnect atlas服务器断开  
    public static final int PLATFORM_ATLAS = 2;
关于disconnect pusher服务器断开    
    public static final int PLATFORM_PUSHER = 3;
```

### 3.4 关于回调
请参考demo **InteractSessionManager**

## 4. 注意事项
### 4.1 关于音量调节
SDK内部音频数据输出模式是通话模式
```
AudioManager mAlramMAnager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
```
调小
```
mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
```
调大
```
mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager. ADJUST_RAISE,AudioManager.FLAG_SHOW_UI);
```
具体API请参考Android官方文档
通话模式不能调接音量为0实现静音，我们提供了一个解决方案<br>
可以通过本地维护一个音量变量<br>
如果当前变量值从1-0<br>
则遍历当前订阅的流列表，对每一路流进行pauseAudio操作<br>
如果变量值从0-1<br>
则遍历当前订阅的流列表，对每一路流进行playAudio操作<br>

### 4.2 关于用户下线
用户下线可能不会立马生效，但是会在10秒内下线的，所以下线以后立马再次调用登录操作可能会得到一个错误的提示 **“用户已登录”**

### 4.3 关于本地摄像头
调用 **initCameraStream** 和 **closeLocalCameraStream** 是成对出现的。<br>
否则没有及时释放相机资源，可能会出现的 **“Another stream is using the same videocapturer, switch video capturer failed”** 错误提示。

### 4.4 关于进入后台相机冲突问题
当应用进入后台再次开启本地相机或者是其他相机类APP会有相机冲突问题
解决方法参考 **CCApplication**

### 4.5 关于资源释放的操作
SDK提供了一个 **releaseAll** 的操作，可以在activity全部退出的时候进行一次调用。<br>
具体可以参考demo -> CCApplication 里面的实现。

### 4.6 关于bug
demo仅提供一种实现的思路和方式，具体可以自己根据demo优化，也可以自己实现。<br>
对接出现问题，请先测试demo是否有同样的问题，避免是因为不巧当的调用引起的。



**（已经到底部）**

​   

