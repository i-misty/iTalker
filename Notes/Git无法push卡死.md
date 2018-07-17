### 解决git无法push的问题
* 添加sendpack.sideband属性并置为false就解决了。

* 全局的：git config --global sendpack.sideband false

* 仓库的：git config --local sendpack.sideband false

* 通过git config --local -l 查看仓库级配置，可以看到有sendpack.sideband这一项并且是置为false的。

* 再次push提交到远程仓库已经ok了。





-----------------------------------

> 如果做完以上操作还是不可以

> git help gc 执行gc操作，然后在执行git pull 获得git push 擦走还是出现上述卡死的现象，继续google，新的方法执行 git fsck，在执行 git gc --prune=now，还是出现上述的问题，

> 最终解决办法，关闭所有占用要提交的项目资源，如果ecplise或是idea，重新执行 git gc，在执行，pull或push,一起ok


> 本人最终解决，设置sendpack.sideband false 之后执行git gc 操作然后依然不可以push，关掉所有占用git的进程，sourcetree 或者
> githubDesktop 然后重启ide（可以尝试重新导入项目）
