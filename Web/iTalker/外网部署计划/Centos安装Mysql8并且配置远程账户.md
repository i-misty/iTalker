### MySql 8 安装

#### 注意版本
* mysql8和原来的版本有点不一样，8的安全级别更高，所以在创建远程连接用户的时候，不能用原来的命令（同时创建用户和赋权）:
### 用户登陆
1. 登陆mysql `mysql -u root -p`
2. 选择MySQL数据库 `use mysql;`
3.在 mysql 数据库的 user 表中查看当前 root 用户的相关信息 `select host, user, authentication_string, plugin from user;`

*  Mysql5.7 可以允许创建用户的同时赋予权限; localhost 代表允许本地访问 `%`代表允许远程访问  `mysql> GRANT ALL PRIVILEGES ON *.* TO 'uahello'@'%' IDENTIFIED BY 'MyNewPass4!' WITH GRANT OPTION;`

#### Mysql8 之后的创建远程连接账户的方式
1. 必须先创建用户（密码规则：mysql8.0以上密码策略限制必须要大小写加数字特殊符号）：

`mysql>create user username@'%' identified  by 'password';`密码必须数字+大小写字母+字符

* 更新允许远程连接 ,如果第一步有远程连接的host可以不用更新`UPDATE USER SET host = '%' WHERE user = 'username';`

2. 再进行权限赋值：

 赋予用户所有权限`mysql>grant all privileges on *.* to chenadmin@'%' with grant option;`

3.最后刷新一下：

`mysql>flush privileges;`

*  当你进行远程连接是，会出现这样的错误:(实际操作过程中的过程中没有出现)

`Unable to load authentication plugin 'caching_sha2_password'.`

* 是因为mysql8使用的是caching_sha2_password加密规则，最简单的方法是修改远程连接用户的加密规则：

`mysql>ALTER USER 'username'@'%' IDENTIFIED WITH mysql_native_password BY 'password';`

### 开放mysql 防火墙端口并且添加阿里云服务器的安全组规则
* `sudo firewall-cmd --zone=public --add-port=3306/tcp --permanent`
* `sudo firewall-cmd --reload`

此时就可以通过外网访问mysql服务了;