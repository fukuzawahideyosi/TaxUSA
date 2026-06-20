<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.panda.servlet.*"%>
<%@ page language="java" import="com.panda.bean.*"%>
<%@ page language="java" import="java.util.ArrayList"%>
<%@ page language="java" import="org.apache.commons.lang3.StringUtils"%>
<%@ page language="java" import="java.util.LinkedHashMap"%>
<%@ page language="java" import="java.text.SimpleDateFormat"%>
<%@ page language="java" import="java.util.Date"%>
<%@ page language="java" import="java.util.*"%>
<%@ page import="java.net.*" %>

<%




LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMapt_etax_account_infoExBean = (LinkedHashMap<String, t_etax_account_infoExBean>)session.getAttribute("LinkedHashMapt_etax_account_infoBean");
LinkedHashMap<String, LinkedHashMap<String, String>> LinkedHashMapTongji = (LinkedHashMap<String, LinkedHashMap<String, String>>)session.getAttribute("LinkedHashMapTongji");
LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanErrCompanyName_Chinese = (LinkedHashMap<String, t_etax_account_resBean>)session.getAttribute("LinkedHashMapEtaxBeanErrCompanyName_Chinese");
LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanErrCompanyName_English = (LinkedHashMap<String, t_etax_account_resBean>)session.getAttribute("LinkedHashMapEtaxBeanErrCompanyName_English");


User_infoBean User_infoBean = (User_infoBean)session.getAttribute("User_infoBean");

LinkedHashMap<String, User_infoBean> HashMapGroup_id_user_id = User_infoBean.getGroup_id_user_id();


SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");

String license = "";
if(session.getAttribute("license") != null){
	license = (String)session.getAttribute("license");
}

String pw = "";
if(session.getAttribute("pw") != null){
	pw = (String)session.getAttribute("pw");
}


String user_id = "";
String maxNo = "66";
if (session.getAttribute("user_id") != null) {
	user_id = (String) session.getAttribute("user_id");
	if ("wangzihao".equals(user_id)) {
		//maxNo = "88888888";
	}
}



int count = 0;
%>



<!DOCTYPE html>
<html lang="zh-CN" data-n-head="%7B%22lang%22:%7B%22ssr%22:%22ja%22%7D%7D">
<head>

<meta charset="utf-8">
<title>熊猫导航</title>
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<meta data-rh="true" name="keywords" content="">
<meta name="description" content="">

<meta data-rh="true" property="og:title" content="">
<meta data-rh="true" property="og:description" content="">



<link rel="preconnect" href="https://fonts.googleapis.com/">
<link rel="preconnect" href="https://fonts.gstatic.com/" crossorigin="">
<link href="static/css/css2.css" rel="stylesheet">

<link href="static/css/all.min.css" rel="stylesheet">
<link href="static/css/bootstrap-icons.css" rel="stylesheet">

<link href="static/css/owl.carousel.min.css" rel="stylesheet">

<link href="static/css/bootstrap.min.css" rel="stylesheet">

<link href="static/css/style.css" rel="stylesheet">
<link rel="shortcut icon" href="img/panda.png"><script type="text/javascript" src="js/Basic.js"></script>

<link type="text/css" href="css/step.css" rel="stylesheet">
<link type="text/css" href="css/element.min.css" rel="stylesheet">


<link href="css/Basic.css" rel="stylesheet">
<!-- CSS -->
<link rel="stylesheet" href="./検索結果一覧｜国税庁法人番号公表サイト_files/reset.css" media="screen, print">
<link rel="stylesheet" href="./検索結果一覧｜国税庁法人番号公表サイト_files/style.css" media="screen, print">
<link rel="stylesheet" href="./検索結果一覧｜国税庁法人番号公表サイト_files/jquery-ui.min.css" media="screen, print">
<link rel="stylesheet" href="./検索結果一覧｜国税庁法人番号公表サイト_files/print.css" media="print">

	<!--
	<script src="./js/Basic.js"></script>
	-->
<style type="text/css">
.bg-primary1 {
	background-color: #357ae8 !important
}
.bg-primary2 {
	background-color: #7f52f3 !important
}
.bg-primary3 {
	background-color: #198754 !important
}

.DnDBox {
    padding-top:18px;
    height: 81px;
    text-align : center;
    font-size: 1.5em;
    border: 4px dashed black;
    background-color: White;
}


        .image-container {
            width: 100px;
            height: 100px;
            overflow: hidden; /* 用于隐藏溢出的部分 */
        }
        .image-container img {
            width: 100%; /* 图像占满整个div */
            height: auto; /* 根据图像比例自动调整高度 */
        }
    </style>


</head>
<body>



<div>

<!-- 头开始 -->

<%
if(!StringUtils.isEmpty(user_id) ) {
%>


<div class="container-fluid bg-secondary ps-5 pe-0 d-none d-lg-block">
<div class="row gx-0">
<div class="col-md-6 text-center text-lg-start mb-2 mb-lg-0">
<div class="d-inline-flex align-items-center">

</div>
</div>
<div class="col-md-6 text-center text-lg-end">
<div class="position-relative d-inline-flex align-items-center bg-primary text-white top-shape px-5">
<div class="me-3 pe-3 border-end py-2">
<p class="m-0">
<i class="fa fa-envelope-open me-2"></i>info@pandaservicejapan.com
</p>
</div>


</div>
</div>
</div>
</div>



<nav class="navbar navbar-expand-lg bg-white navbar-light shadow-sm px-6 py-3 py-lg-0">
		<div style="display: flex;">
		<a href="index.html" class="navbar-brand p-0">
		<h1 class="m-0 text-uppercase text-primary"><img width="40%" src="img/panda.jpg" alt="">熊猫导航</h1>
		</a>
		</div>

<table >
<tr>
	<td>

  <div class="image-container">
        <img src="img/微信公众号.jpg" alt="Your Image">
    </div>


	</td>
</tr>
<tr>
	<td style="text-align: center;">
		<font size="2" >微信公众号</font>
	</td>
</tr>
</table>



<table >
<tr>
	<td>
<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarCollapse">
<span class="navbar-toggler-icon"></span>
</button>
	</td>
</tr>
</table>


<div class="collapse navbar-collapse" id="navbarCollapse">

<div class="navbar-nav ms-auto py-0 me-n0">
<a href="index.html" class="nav-item nav-link ">首页</a>
<a href="SearchLogic" class="nav-item nav-link ">案件检索</a><a href="news_JCT.html" class="nav-item nav-link ">JCT税号</a>
<a href="SearchJCTOverseasLogic" class="nav-item nav-link active">JCT税号检索</a><a href="jisuanqi.jsp" class="nav-item nav-link ">计算器</a>
<a href="./SendMailLogic" class="nav-item nav-link">联系我们</a>
</div>
</div>
</nav>

<%
}
%>
<!-- 头结束 -->


<%
if(!StringUtils.isEmpty(user_id) ) {
%>



<div class="container-fluid bg-dark p-5">
<div class="row">
<div class="col-12 text-center">


<!-- 微信 AI -->



<h1 class="display-4 text-white">熊猫导航</h1>

</div>
</div>
</div>




<div class="container-fluid py-3 px-3">
	<div class="text-center mx-auto mb-5" style="max-width: 600px;">
	<h1 class="display-5 mb-0">链接一览</h1>
	<hr class="w-25 mx-auto bg-primary">
	</div>


<!-- 翻页功能开始 -->


<!-- 翻页功能结束 -->




        <div id="contents" style="width: 95%;">
            <main role="main">
                <div class="box01 print01">

					<h1 class="head">链接一覧（：）



					</h1>

                    <div class="inBox09">

                            <div class="inBox10">
                                <dl class="srhCondition">
                                    <dt><strong>排序条件：</strong></dt>
                                    <dd>更新日期（降序）</dd>
                                </dl>
                            </div>




                                <div class="tbl01" style="width:100%;height:100%;overflow-x:auto;overflow-y:auto;scrollTop:100">
                                    <table class="fixed normal">
                                        <thead>
                                           <tr>
                                                <th scope="col" style="width:3%;">No</th>
                                                <th scope="col" style="width:6%;">种类</th>
                                                <th scope="col" style="width:30%;">画面名称</th>
                                                <th scope="col" style="">URL</th>

                                            </tr>
                                        </thead>
                                       <tbody>

<%


String currentUrl = request.getRequestURL().toString();
URL url = new URL(currentUrl);
String domain = url.getHost();
String ProjectName ="";

if (domain.contains("127.0.0.1")) {
	ProjectName ="/PandaServiceMA";
}

String URL = "";


int i = 0;
%>

<%
++i;
URL = ProjectName + "/index.jsp";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
	<td><font style="color: red;" size="1"><%= i %><br><i class="fa fa-flag" aria-hidden="true"></i></font></td>
	<td>员工用</td>
	<td>CPSC_eFiling客户信息收集表web v1.0</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>




<%
++i;
URL = ProjectName + "/products.jsp";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>CPSC_eFiling客户信息管理</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<%
++i;
URL = ProjectName + "/SearchUserInfoLogic?license=" + user_id + "&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>日本消费税税号客户信息管理【管理ID（降序）】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<%
++i;
URL = ProjectName + "/LoginServletLogic";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
	<td><%= i %></td>
	<td>员工用</td>
	<td>登录画面</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/AiSetYewuLogic?license=" + user_id;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
	<td><%= i %></td>
	<td>管理用</td>
	<td>Ai数据登录設定管理</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/AiSetYewuShujuLogic";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
	<td><%= i %></td>
	<td>客户用</td>
	<td>Ai数据登录</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/AiSetYewuShujuLogic?yaoqing_no=NoAvtC";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
	<td><%= i %></td>
	<td>代理用</td>
	<td>Ai数据登录</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<%
++i;
URL = ProjectName + "/AiSetYewuShujuLogic?license=" + user_id;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
	<td><%= i %></td>
	<td>客户用 数据免填写</td>
	<td>Ai数据登录</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/AiSetYewuJinduLogic?license=" + user_id + "&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
	<td><%= i %></td>
	<td>管理用</td>
	<td>Ai数据登录进度</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<%
++i;
URL = ProjectName + "/AiEtaxLogic?license="+user_id+"";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>AiEtax仕様設定管理</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>





<!-- 客户用 -->
<%
++i;
URL = ProjectName + "/SetUserInfoLogic2?yaoqing_no=NoAvtC";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td>日本消费税税号客户信息收集表web v2.0</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<%
++i;
URL = ProjectName + "/swagger/index.html";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td>API</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<%
++i;
URL = ProjectName + "/SetDianziZhifuLogic?license=" + user_id + "&filter=&sort=update_date&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>电子支付</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SetAccountAmountLogic?yaoqing_no=NoAvtC";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客戶用</td>
	<td>提交转账信息 【wangzihao】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<%
++i;
URL = ProjectName + "/SearchUserInfoLogic?license=" + user_id + "&filter=没有签字pdf&sort=update_date&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>日本消费税税号客户信息管理【没有签字pdf+更新日期（降序）】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SearchUserInfoLogic?license=" + user_id + "&filter=激活完了pdf&sort=update_date&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>日本消费税税号客户信息管理【签名PDF+更新日期（降序）】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<%
++i;
URL = ProjectName + "/SearchUserInfoLogic?license=" + user_id + "&filter=申告结果&sort=update_date&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>日本消费税税号客户信息管理【申告结果+更新日期（降序）】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<%
++i;
URL = ProjectName + "/SearchUserInfoLogic?license=" + user_id + "&sort=update_date&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>日本消费税税号客户信息管理【更新日期（降序）】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>




<%
++i;
URL = ProjectName + "/Amazoncsvzhangben.jsp?license=" + user_id + "";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用 提交申告數據 免邮件</td>
	<td>AmazonCsv转换【账本A收入】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>




<%
++i;
URL = ProjectName + "/SetXiaofeishuiShengaoCHengnuoshuOpenLogic?yaoqing_no=NoAvtC";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客戶用 提交申告數據</td>
	<td>日本消费税申告确认书Open 【wangzihao】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<%
++i;
URL = ProjectName + "/Amazoncsvzhangben.jsp";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客戶用 提交申告數據</td>
	<td>AmazonCsv转换【账本A收入】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<%
++i;
URL = ProjectName + "/SetXiaofeishuiShengaoCHengnuoshuOpenLogic?yaoqing_no=WL0U88";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客戶用 提交申告數據</td>
	<td>日本消费税申告确认书Open 【xiaolei】</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SetXiaofeishuiShengaoCHengnuoshuOpenLogic?activation_code=";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客戶用 上傳確認書</td>
	<td>日本消费税申告确认书Open</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>




<%
++i;
URL = ProjectName + "/SetXiaofeishuiShengaoCHengnuoshuOpenLogic?license="+user_id+"&PDSK=";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>日本消费税申告确认书Open</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>




<%
++i;
URL = ProjectName + "/SearchEtaxSendJieguoLogic?license="+user_id+"&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>ETAX送信結果一览</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SearchKuaijiLogic?license="+user_id+"&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>会计信息一览</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SetXiaofeishuiShengaoCHengnuoshuLogic?license="+user_id+"";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>日本消费税申告确认书(ncc作成)</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>





<%
++i;
URL = ProjectName + "/SearchEtaxLogic?license="+user_id+"&filter=SPEED&web=KakuninAuto";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>ETAX账号一览（自動你拿号，生成文件）</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SearchJCTToukeiLogic?license="+user_id+"&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>JCT税号登记状况统计</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SearchAmazonRankingLogic?license="+user_id+"&maxNo=" + maxNo;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>AmazonRanking</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SetUserInfoLogic?activation_code=20240250980001";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>管理用</td>
	<td>消费税税号 申请激活</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<!-- 工具 -->
<!-- 工具 -->
<!-- 工具 -->
<%
++i;
URL = ProjectName + "/jisuanqiGeiyuKonchu.jsp";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>工具</td>
	<td>年末調整等のための給与所得控除後の給与等の金額</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/toolsReplace.jsp";
URL = ProjectName + "/ToolsReplaceLogic?license="+user_id;
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>工具</td>
	<td>替换工具</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<!-- API -->

<%
++i;
URL = ProjectName + "/EtaxLogic?jietuo_by_bangou=html&bangou=2030022121930098";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>API</td>
	<td>受信通知</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<%
++i;
URL = ProjectName + "/EtaxLogic?jietuo_by_bangou=html_qr&bangou=2030022121930098";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>API</td>
	<td>スマホアプリ納付用ＱＲコード表示</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>

<!-- 客户用 -->
<%
++i;
URL = ProjectName + "/SetUserInfoLogic?yaoqing_no=NoAvtC";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td>日本消费税税号客户信息收集表web v1.0</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/setXiaofeishuiShengaoCHengnuoshu.jsp";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td>日本消费税申告确认书（印刷モード）</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SetXiaofeishuiShengaoLogic?yaoqing_no=NoAvtC";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td>日本【消费税申告】客户信息收集表</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>


<%
++i;
URL = ProjectName + "/SearchJCTOverseasLogic";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td>JCT税号一览（海外）</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<%
++i;
URL = ProjectName + "";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td></td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<%
++i;
URL = "https://www.graviness.com/app/pwg/";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>外网工具</td>
	<td>密码生成器</td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>















<%
++i;
URL = ProjectName + "";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td></td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>



<%
++i;
URL = ProjectName + "";
%>
<tr id="trRow<%= i %>" style="font-size:12px">
    <td><%= i %></td>
	<td>客户用</td>
	<td></td>
	<td><a href="<%= URL %>" target="_blank"></a><%= URL %></td>
</tr>








                                        </tbody>
                                    </table>
                                </div>



                    </div>
                </div>

            </main>
        </div>





<%
//bug list
if("admin".equals(User_infoBean.getPermissions())) {
%>

<div id="contents" style="width: 95%;text-align: left;">
</div>


<%
}
%>






<!-- 翻页功能开始 -->

<!-- 翻页功能结束 -->







<input id="hidden_license" name="license" type="hidden" value=<%= license %>>

<input id="hidden_user_id" name="user_id" type="hidden" value=<%= user_id %>>

</div>



<!-- 尾开始 -->




<div class="container-fluid bg-dark text-secondary text-center border-top py-4 px-5" style="border-color: rgba(256, 256, 256, .1) !important;">
<p class="m-0">Copyright (C) 2022 All Right Reserverd 广州日东尚禾商贸有限公司 版权所有
<br>無断引用・転載禁止
<br><a href="https://beian.miit.gov.cn/" target="_blank" style="color: ;">粤ICP备2022085609号-1</a>
</p>
		 	<div style="width:300px;margin:0 auto; padding:20px 0;">
		 		<a target="_blank" href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=44010302001019" style="display:inline-block;text-decoration:none;height:20px;line-height:20px;"><img src="img/备案图标.png" style="float:left;"/><p style="float:left;height:20px;line-height:20px;margin: 0px 0px 0px 5px; color:#939393;">粤公网安备 44010302001019号</p></a>
		 	</div>
<table >
<tr>
	<td>
<img width="22%" src="img/微信公众号.jpg" alt="Image">
	</td>
</tr>
<tr>
	<td>
		<font size="1">微信公众号</font>
	</td>
</tr>
</table>
</div>

<a href="#" class="btn btn-lg btn-primary btn-lg-square rounded-circle back-to-top"><i class="bi bi-arrow-up"></i></a>


<%
}
%>

<script data-cfasync="false" src="static/js/email-decode.min.js"></script><script src="static/js/jquery-3.4.1.min.js" ></script>
<script src="static/js/bootstrap.bundle.min.js" type="f2503e2418ef4c796e64bf0b-text/javascript"></script>
<script src="static/js/easing.min.js" type="f2503e2418ef4c796e64bf0b-text/javascript"></script>
<script src="static/js/waypoints.min.js" type="f2503e2418ef4c796e64bf0b-text/javascript"></script>
<script src="static/js/owl.carousel.min.js" type="f2503e2418ef4c796e64bf0b-text/javascript"></script>

<script src="static/js/main.js" type="f2503e2418ef4c796e64bf0b-text/javascript"></script>
<script src="static/js/rocket-loader.min.js" data-cf-settings="f2503e2418ef4c796e64bf0b-|49" defer=""></script>
<!-- 尾结束 -->




<style type="text/css">
.DnDBox {
    padding-top:18px;
    height: 81px;
    text-align : center;
    font-size: 1.5em;
    border: 4px dashed black;
    background-color: White;
}



    /* 链接的样式，悬停时将箭头变成手指形状 */
    tr {
      cursor: pointer;
    }

</style>


		<script>
		// 使用 jQuery 添加悬停效果
		  $('tr').hover(
		    function() {
		      // 鼠标悬停时的处理，将内部背景色变为浅绿
		      $(this).find('td').css('background-color', '#e0ffe0');
		    },
		    function() {
		      // 鼠标移开时的处理，根据 tr 的 ID 判断奇偶数，设置不同的背景色
		      var trId = $(this).attr('id');
		      if (trId && parseInt(trId.replace('trRow', ''), 10) % 2 === 0) {
		        // ID 包含偶数，背景色变为 #F5F5F5
		        $(this).find('td').css('background-color', '#F5F5F5');
		      } else {
		        // ID 不包含偶数，背景色变为白色
		        $(this).find('td').css('background-color', 'white');
		      }
		    }
		  );

		  // 使用 jQuery 添加点击事件
		  $('tr').on('click', function(event) {
			  $(this).find('a')[0].click()
		  });


		</script>



</div>
<body>
</html>