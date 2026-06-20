<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String productsJson = (String) request.getAttribute("productsJson");
    if (productsJson == null || productsJson.length() == 0) productsJson = "[]";
    String batchesJson = (String) request.getAttribute("batchesJson");
    if (batchesJson == null || batchesJson.length() == 0) batchesJson = "[]";
    String selectedBatchId = (String) request.getAttribute("selectedBatchId");
    if (selectedBatchId == null) selectedBatchId = "";
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>CPSC eFiling 数据查看与校验</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/app.css">
</head>
<body>
<div class="header">
    <h1>CPSC eFiling 数据库数据查看与 API 参数校验</h1>
    <p>页面从 MySQL 读取已导入的数据，并根据 CPSC /import 字段限制进行检测；异常项用红色显示。</p>
    <div class="nav">
        <a href="<%=request.getContextPath()%>/index.jsp">上传Excel</a>
        <a href="<%=request.getContextPath()%>/products">查看全部数据</a>
    </div>
</div>

<div class="container">
    <% if (success != null && success.length() > 0) { %>
        <div class="alert success"><%= success %></div>
    <% } %>
    <% if (error != null && error.length() > 0) { %>
        <div class="alert error"><%= error %></div>
    <% } %>

    <div class="card">
        <h2>导入批次</h2>
        <div id="batchList" class="batch-list"></div>
    </div>

    <div class="summary-grid">
        <div class="summary-card">
            <div class="num" id="totalCount">0</div>
            <div class="label">产品证书记录</div>
        </div>
        <div class="summary-card">
            <div class="num" id="okCount">0</div>
            <div class="label">校验通过</div>
        </div>
        <div class="summary-card">
            <div class="num" id="errorCount">0</div>
            <div class="label">存在红色错误提示</div>
        </div>
        <div class="summary-card">
            <div class="num" id="labCount">0</div>
            <div class="label">实验室/测试报告记录</div>
        </div>
    </div>

    <div class="card">
        <div class="toolbar">
            <div class="left">
                <h2 style="margin:0;">产品证书数据</h2>
                <span class="badge info" id="selectedBatchBadge"></span>
            </div>
            <div class="right">
                <input type="text" id="keyword" placeholder="搜索产品ID、名称、制造商、实验室" style="width:300px;">
                <label class="small"><input type="checkbox" id="onlyError"> 只看错误</label>
                <button class="btn light" id="expandAll" type="button">展开全部</button>
                <button class="btn light" id="collapseAll" type="button">收起全部</button>
            </div>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th style="width:90px;">状态</th>
                    <th>产品ID / 版本</th>
                    <th>产品信息</th>
                    <th>制造商</th>
                    <th>实验室 / 法规代码</th>
                    <th>POC</th>
                    <th>日期与批次</th>
                    <th style="width:100px;">操作</th>
                </tr>
                </thead>
                <tbody id="productTableBody"></tbody>
            </table>
        </div>
    </div>
</div>

<div class="footer">红色提示为本系统根据 API 字段必填、枚举值、日期格式、最大长度等规则预检结果。</div>

<script>
var productData = <%=productsJson%>;
var batchData = <%=batchesJson%>;
var selectedBatchId = '<%=selectedBatchId%>';
</script>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>window.jQuery || document.write('<script src="<%=request.getContextPath()%>/assets/jquery-lite.js"><\/script>')</script>
<script>
function text(v){ return (v === null || v === undefined || v === '') ? '-' : String(v); }
function esc(v){
    v = text(v);
    return String(v).replace(/[&<>'\"]/g, function(c){
        return {'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','\"':'&quot;'}[c];
    });
}
function hasErrors(p){ return p.errors && p.errors.length > 0; }
function labText(p){
    if (!p.labs || p.labs.length === 0) return '-';
    return p.labs.map(function(l){
        return text(l.labAlternateId) + ' / ' + text(l.labType) + ' / ' + text(l.citationCodes);
    }).join(' | ');
}
function productSearchText(p){
    return [p.primaryProductId, p.versionId, p.name, p.tradeBrandName, p.manufacturerAlternateId, p.manufacturerName, labText(p), p.pocCode, p.pocType].join(' ').toLowerCase();
}
function renderBatches(){
    var box = $('#batchList');
    box.empty();
    if (!batchData || batchData.length === 0) {
        box.append('<div class="muted">数据库中还没有导入批次。请先上传 Excel。</div>');
        return;
    }
    batchData.forEach(function(b){
        var active = String(b.id) === String(selectedBatchId) ? ' active' : '';
        var html = '<div class="batch-item' + active + '">' +
            '<a href="<%=request.getContextPath()%>/products?batchId=' + encodeURIComponent(b.id) + '">' +
            '<div class="batch-id">Batch #' + esc(b.id) + '</div>' +
            '<div class="small muted">' + esc(b.createdAt) + '</div>' +
            '<div style="margin-top:8px;"><span class="badge info">' + esc(b.importStatus) + '</span> <span class="badge ok">' + esc(b.productCount) + ' 个产品</span></div>' +
            '<div class="small muted" style="margin-top:8px;">Certifier: ' + esc(b.certifierId) + '</div>' +
            '<div class="small muted">Collection: ' + esc(b.collectionId) + '</div>' +
            '</a></div>';
        box.append(html);
    });
}
function renderSummary(rows){
    var total = rows.length;
    var errorRows = rows.filter(hasErrors).length;
    var labs = 0;
    rows.forEach(function(p){ labs += p.labs ? p.labs.length : 0; });
    $('#totalCount').text(total);
    $('#okCount').text(total - errorRows);
    $('#errorCount').text(errorRows);
    $('#labCount').text(labs);
    $('#selectedBatchBadge').text(selectedBatchId ? ('当前批次 #' + selectedBatchId) : '全部数据，最多显示500条');
}
function renderProducts(){
    var keyword = ($('#keyword').val() || '').toLowerCase();
    var onlyError = $('#onlyError').is(':checked');
    var rows = productData.filter(function(p){
        if (onlyError && !hasErrors(p)) return false;
        if (keyword && productSearchText(p).indexOf(keyword) < 0) return false;
        return true;
    });
    renderSummary(rows);

    var tbody = $('#productTableBody');
    tbody.empty();
    if (rows.length === 0) {
        tbody.append('<tr><td colspan="8" class="muted">没有符合条件的数据。</td></tr>');
        return;
    }
    rows.forEach(function(p){
        var err = hasErrors(p);
        var status = err ? '<span class="badge err">错误 ' + p.errors.length + '</span>' : '<span class="badge ok">OK</span>';
        var labHtml = '-';
        if (p.labs && p.labs.length > 0) {
            labHtml = p.labs.map(function(l){
                return '<div><b>' + esc(l.labAlternateId) + '</b> <span class="badge info">' + esc(l.labType) + '</span><br>' +
                    '<span class="small muted">法规代码：' + esc(l.citationCodes) + '</span><br>' +
                    '<span class="small muted">报告号：' + esc(l.testReportId) + '</span></div>';
            }).join('<hr>');
        }
        var exHtml = p.exemptions && p.exemptions.length > 0 ? '<div class="small muted">豁免：' + esc(p.exemptions.join(', ')) + '</div>' : '';
        var errorsHtml = '';
        if (err) {
            errorsHtml = '<ul class="error-list">' + p.errors.map(function(e){
                return '<li><span class="error-field">' + esc(e.field) + '</span>：' + esc(e.message) + '</li>';
            }).join('') + '</ul>';
        }
        var details = '<div class="details">' +
            '<div class="details-grid">' +
            '<div class="detail-box"><b>产品 API 字段</b>' +
            '<div class="small">versionId: <span class="code">' + esc(p.versionId) + '</span></div>' +
            '<div class="small">primaryProductIdType: <span class="code">' + esc(p.primaryProductIdType) + '</span></div>' +
            '<div class="small">certificateType: <span class="code">' + esc(p.certificateType) + '</span></div>' +
            '<div class="small">productUpdate: <span class="code">' + esc(p.productUpdate) + '</span></div></div>' +
            '<div class="detail-box"><b>制造商</b>' +
            '<div class="small">alternateId: <span class="code">' + esc(p.manufacturerAlternateId) + '</span></div>' +
            '<div class="small">name: ' + esc(p.manufacturerName) + '</div>' +
            '<div class="small">country: ' + esc(p.manufacturerCountry) + '</div>' +
            '<div class="small">isNew: <span class="code">' + esc(p.manufacturerIsNew) + '</span></div></div>' +
            '<div class="detail-box"><b>POC</b>' +
            '<div class="small">code: <span class="code">' + esc(p.pocCode) + '</span></div>' +
            '<div class="small">type: <span class="code">' + esc(p.pocType) + '</span></div>' +
            '<div class="small">email: ' + esc(p.pocEmail) + '</div>' +
            '<div class="small">isNew: <span class="code">' + esc(p.pocIsNew) + '</span></div></div>' +
            '</div>' + errorsHtml + '</div>';

        var tr = '<tr class="' + (err ? 'has-error' : '') + '" data-id="' + esc(p.id) + '">' +
            '<td>' + status + '</td>' +
            '<td><b>' + esc(p.primaryProductId) + '</b><br><span class="small muted">version: ' + esc(p.versionId) + '</span></td>' +
            '<td><b>' + esc(p.name) + '</b><br><span class="small muted">品牌：' + esc(p.tradeBrandName) + '</span><br><span class="small muted">' + esc(p.description) + '</span></td>' +
            '<td><b>' + esc(p.manufacturerAlternateId) + '</b><br><span class="small muted">' + esc(p.manufacturerName) + '</span><br><span class="small muted">' + esc(p.manufacturerCountry) + '</span></td>' +
            '<td>' + labHtml + exHtml + '</td>' +
            '<td><b>' + esc(p.pocType) + '</b><br><span class="small muted">' + esc(p.pocCode) + '</span><br><span class="small muted">' + esc(p.pocEmail) + '</span></td>' +
            '<td><span class="small muted">生产：' + esc(p.manufactureDate) + '</span><br><span class="small muted">测试：' + esc(p.lastTestDate) + '</span><br><span class="small muted">批次：' + esc(p.lotNumber) + '</span></td>' +
            '<td><button type="button" class="btn light toggleDetail">详情</button></td>' +
            '</tr><tr class="detail-row"><td colspan="8">' + details + '</td></tr>';
        tbody.append(tr);
    });
}
$(function(){
    renderBatches();
    renderProducts();
    $('#keyword').on('input', renderProducts);
    $('#onlyError').on('change', renderProducts);
    $('#productTableBody').on('click', '.toggleDetail', function(){
        $(this).closest('tr').next('.detail-row').find('.details').toggle();
    });
    $('#expandAll').on('click', function(){ $('.details').show(); });
    $('#collapseAll').on('click', function(){ $('.details').hide(); });
});
</script>
</body>
</html>
