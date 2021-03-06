# jmessage-react-plugin （iOS 尚未支持）
##Android

####这是一个使用JMessage-sdk的混合的React Native应用，目前实现了收发文字消息。

#### 安装
```
npm install jmessage-react-plugin --save
react-native link
```
安装完毕后，在 MainApplication 中加上 JMessagePackage 即可。
```
        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new JMessageReactPackage(),
            );
        }
```

#### example 使用
```
npm install react-native-jchat-demo
react-native run-android
```
 
关于jmessage-sdk的相关接口说明可以参考：
#####[极光IM Android SDK概述](http://docs.jpush.io/client/im_sdk_android/)

#####[IM Android SDK Java docs](http://docs.jpush.io/client/im_android_api_docs/)

####jmessage-react-plugin Android 的项目结构说明
#####JS 部分
除了入口 index.android.js 之外，都放在 react-native-android 文件夹下
 
#####Native部分
- entity 根据需求抽象出的一些实体类，主要是为了使用 Gson 转换为 json 字符串传到 JS（如果是纯 React Native 应用，则不需要如此，直接请求服务端即可）
- tools 主要是一些工具类
- 其他 包括 Native 入口类以及 NativeModule 等

####接口调用

#####在 JS 中调用 jmessage-sdk 的接口详情
```
const JMessageModule = NativeModules.JMessageModule;
```
－ 加载 conversations：
```
JMessageModule.getConvList().then((list) => {
            _convList = JSON.parse(list);
            this.setState({
                dataSource: _ds.cloneWithRows(_convList),
                fetching: false
            });
        }).catch((e) => {
            console.log(e);
            this.setState({
                fetching: false
            });
        });
```
- 接收消息
```
const RECEIVE_MSG_EVENT = "receiveMsgEvent";

componentDidMount() {
        DeviceEventEmitter.addListener(RECEIVE_MSG_EVENT, (map) => {
            console.log("收到消息： " + map.message);
            let conversation = JSON.parse(map.conversation);
            for (let conv in _convList) {
                if (conv.username === conversation.username || conv.groupId === conversation.groupId) {
                    conv = conversation;
                }
            }
            let newData = JSON.parse(JSON.stringify(_convList));
            newData.sort(this.by("date"));
            this.setState({
                dataSource: _ds.cloneWithRows(newData)
            });
            _convList = newData;
        });
    }
```
- 发送消息
```
const {username, appKey, groupId} = this.props;
		JMessageModule.sendTxtMsg(username, appKey, groupId, this.state.inputContent)
			.then((msg) => {
				console.log("Sending text message: " + msg);
				this.msgArr.push(JSON.parse(msg));
				console.log("msgArr: " + this.msgArr);
				this.setState({
					dataSource: this.state.ds.cloneWithRows(this.msgArr),
					inputContent: ""
				});
				if (this.listView !== null) {
					this.listView.scrollToEnd();
				}
			}).catch((e) => {
				console.log(e);
				this.setState({
					inputContent: ""
				});
			});
```
发送消息后还需要监听发送状态：

```
const SEND_MSG_RESULT = "sendMsgResult";
DeviceEventEmitter.addListener(SEND_MSG_RESULT, this.sendMsgResult);
sendMsgResult = (msg) => {
		var message = JSON.parse(msg);
		for (var i = this.msgArr.length - 1; i >= 0; i--) {
			if (this.msgArr[i].msgId === message.msgId) {
				this.msgArr[i].sendState = message.sendState;
			}
		}
		let newData = JSON.parse(JSON.stringify(this.msgArr));
		this.setState({
			dataSource: this.state.ds.cloneWithRows(newData)
		});
		this.msgArr = newData;
	};
```

- 分页加载消息

```
JMessageModule.getMessageFromNewest(username, appKey, groupId, this.START, this.PAGE_MSG_COUNT)
			.then((result) => {
				if ("" === result) {
					console.log("No last page");
					this.setState({
						fetching: false
					});
					return;
				}
				let msgData = JSON.parse(result);
				msgData.reverse();
				this.msgArr = this.msgArr.concat(msgData);
				this.MSG_OFFSET = this.START + msgData.length;
				this.START = this.MSG_OFFSET;
				this.setState({
					fetching: false,
					dataSource: this.state.ds.cloneWithRows(this.msgArr)
				});
			}).catch((e) => {
				console.log(e);
				this.setState({
					fetching: false
				});
			});
```

- 进入会话（进入会话后，通知栏不会展示此会话中收到的消息）

```
JMessageModule.enterConversation(username, appKey, groupId);
```
- 添加好友（无好友模式）

```
JMessageModule.addFriend(inputTxt).then((result) => {
            var newDs = JSON.parse(result);
            _convList = [newDs, ..._convList];
            this.setState({
                dataSource: _ds.cloneWithRows(_convList)
            });
        }).catch((e) => {
            console.log(e);
        });
```
##关于升级React Native
**进入当前项目的目录**
- 在命令行中使用：

> react-native --version

就可以查看当前使用的版本

- 在命令行中输入：

> npm info react-native

就可以查看React Native的历史和最新版本

- React Native可以直接更新到某个版本：

> npm install --save react-native@0.23.0

就可以更新到0.23.0版本

如果升级后出现类似于
```
react-native@0.23.0 requires a peer of react@^0.14.5 but none was installed.
```

执行:
> npm install --save react

或者：
> npm install --save react@0.14.5

即可。

如果更新后执行react-native run-android不能正确运行，而是出现类似：
```
 Could not find com.facebook.react:react-native:0.23.0.
```

错误，或者在Android Studio中直接运行app时报错：
```
Android Studio failed to resolve com.facebook.react:react-native:0.23.0
```

那么可以按照下列命令修复，首先在命令行中执行：
> npm i

执行完毕且不报错后，执行下面的命令，**注意，在执行命令之后，某些文件可能会产生冲突，请确保你的本地文件记录可以恢复**（在Android Studio中可以查看历史记录来恢复文件）
> react-native upgrade

执行上面的命令可能会提示你是否覆盖文件。在解决冲突之后重新运行App即可。
