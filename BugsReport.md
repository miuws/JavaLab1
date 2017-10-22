# BUGS REPORT

### BUG 0000

描述：窗口退出后主线程未退出

位置：MainPaga.java, initMainPage()

解决方式：添加代码`setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);`

