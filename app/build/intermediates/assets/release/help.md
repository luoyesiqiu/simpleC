<table>
<tr><th>软件作者</th><th>落叶似秋</th></tr>
<tr><th>作者博客 </th><th><a href="http://blog.csdn.net/e_one">blog.csdn.net/e_one</a></th></tr>
<tr><th>项目地址</th><th><a href="https://github.com/luoyesiqiu/simpleC">github.com/luoyesiqiu/simpleC</a></th></tr>
</table>

> 点击菜单加群，寻找有共同编程爱好的人，也可以输入群号：236121720

**如果觉得软件还不错，可以请我吃包辣条**

###**1.微信**###

<img src="file:///android_asset/img/webchat_pay.jpg"/>

###**2.支付宝**###

<img src="file:///android_asset/img/alipay.jpg"/>

</br><font color="red"><b>二维码使用方法：</b></font></br>
<font color="red">在本页面截下完整的二维码图片，并保存到手机相册，然后打开微信扫一扫功能，从相册选择该截图就可以付款啦~~~</font>

## simpleC已知问题

### 1.不能使用以下程序结构

<pre>
int i=0;
while(i++&lt;10)
{
//...
}
</pre>

代替方式：

<pre>
int i=-1;
while(++i&lt;10)
{
//...
}
</pre>

###2.不支持变长数组

例如：

<pre>
int len=10;
int arr[len];
</pre>

**如果你发现有更多问题，麻烦您反馈给我，谢谢!**

##更新内容

**1.2.16**

* 支持快速注释/取消注释代码

* 新增从第三方文件管理器中打开文件

* 修复从后台重新进入程序时空白的bug和旋转屏幕时空白的bug

* 加入帮助,在线API文档，在线练习等

**1.2.15**

* 文件选择界面新增：新建文件，重命名文件，删除文件等选项

**1.2.14**

* 修复控制台打印输入的问题

* 将编辑框的行号显示为粗体

**1.2.13**

* 修复对宏定义高亮出现的问题

* 支持提示用户输入过的词汇

