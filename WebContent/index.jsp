<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>CPSC eFiling 数据导入</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/app.css">
</head>
<body>
<div class="header">
    <h1>CPSC eFiling Product Registry 数据导入</h1>
    <p>上传 Excel 数据收集表，后台读取后生成 /import JSON，并写入 MySQL 数据库。</p>
    <div class="nav">
        <a href="<%=request.getContextPath()%>/index.jsp">上传Excel</a>
        <a href="<%=request.getContextPath()%>/products">查看数据库数据</a>
    </div>
</div>

<div class="container">
    <% if (error != null && error.length() > 0) { %>
        <div class="alert error"><%= error %></div>
    <% } %>

    <div class="card">
        <h2>上传数据收集表</h2>
        <form id="uploadForm" action="<%=request.getContextPath()%>/upload" method="post" enctype="multipart/form-data">
            <div class="form-grid">
                <div class="form-item">
                    <label>Certifier ID</label>
                    <input type="text" name="certifierId" id="certifierId" placeholder="example-company" value="example-company">
                    <div class="hint">对应 /import 参数 certifierId。建议正式环境由系统配置，不让业务人员随意改。</div>
                </div>
                <div class="form-item">
                    <label>Collection ID</label>
                    <input type="text" name="collectionId" id="collectionId" placeholder="11111111-2222-3333-4444-999999999999" value="11111111-2222-3333-4444-999999999999">
                    <div class="hint">对应 /import 参数 collectionId。用户 token 必须有该 Collection 权限。</div>
                </div>
                <div class="form-item">
                    <label>Excel 文件</label>
                    <input type="file" name="excelFile" id="excelFile" accept=".xlsx,.xls">
                    <div class="hint">请上传“CPSC_eFiling_Product_Registry_产品证书资料收集表.xlsx”。</div>
                </div>
                <div class="form-item">
                    <label>导入设置</label>
                    <div style="padding-top:12px;">
                        <label style="font-weight:500;"><input type="checkbox" name="doCertify" value="true"> 导入时直接认证 doCertify=true</label>
                    </div>
                    <div class="hint">只有 Collection 中具有 Certifier 权限的用户才可设 true；默认建议不勾选。</div>
                </div>
            </div>
            <div class="actions">
                <button type="submit" class="btn">上传并写入数据库</button>
                <a class="btn light" href="<%=request.getContextPath()%>/products">查看数据库数据</a>
            </div>
        </form>
    </div>

    <div class="card">
        <h2>页面功能说明</h2>
        <div class="details-grid">
            <div class="detail-box">
                <b>1. 后台读取 Excel</b>
                <span class="muted">按第4行 API字段路径读取 Sheet 01~05，并组装产品、制造商、实验室、POC、法规豁免。</span>
            </div>
            <div class="detail-box">
                <b>2. 生成 JSON</b>
                <span class="muted">调用 buildImportJsonFromExcel 逻辑，生成 CPSC /import 的 productList JSON。</span>
            </div>
            <div class="detail-box">
                <b>3. 写入数据库</b>
                <span class="muted">把规范化数据写入产品、制造商、实验室等表，并把完整 JSON 保存到 import_batch.request_json。</span>
            </div>
        </div>
    </div>
</div>

<div class="footer">CPSC eFiling Demo Web · JSP + Servlet + JDBC + jQuery</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>window.jQuery || document.write('<script src="<%=request.getContextPath()%>/assets/jquery-lite.js"><\/script>')</script>
<script>
$(function(){
    $('#uploadForm').on('submit', function(e){
        var certifierId = $('#certifierId').val();
        var collectionId = $('#collectionId').val();
        var file = $('#excelFile').val();
        if (!certifierId || !collectionId || !file) {
            alert('请填写 Certifier ID、Collection ID，并选择 Excel 文件。');
            e.preventDefault();
            return false;
        }
        if (!/\.(xlsx|xls)$/i.test(file)) {
            alert('请上传 .xlsx 或 .xls 文件。');
            e.preventDefault();
            return false;
        }
    });
});
</script>
</body>
</html>
