<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>CPSC eFiling 导入CSV生成</title>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <style>
        body {
            margin: 0;
            font-family: "Microsoft YaHei", Arial, sans-serif;
            background: #f5f6fb;
            color: #222;
        }

        .topbar {
            height: 58px;
            display: flex;
            align-items: center;
            padding: 0 26px;
            background: #ffffff;
            border-bottom: 1px solid #ddd;
            box-shadow: 0 2px 6px rgba(0,0,0,.08);
        }

        .logo {
            width: 34px;
            height: 34px;
            background: #17265f;
            border-radius: 50%;
            margin-right: 14px;
        }

        .title {
            font-size: 22px;
            font-weight: 600;
            color: #17265f;
        }

        .title span {
            color: #e61f35;
        }

        .container {
            max-width: 1180px;
            margin: 32px auto;
            padding: 0 18px;
        }

        .card {
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 8px 20px rgba(18, 38, 63, .08);
            padding: 28px;
            margin-bottom: 22px;
        }

        .card h2 {
            margin: 0 0 20px;
            color: #17265f;
            font-size: 22px;
        }

        .form-row {
            display: flex;
            flex-wrap: wrap;
            gap: 18px;
            margin-bottom: 18px;
        }

        .form-group {
            flex: 1;
            min-width: 260px;
        }

        label {
            display: block;
            font-weight: 600;
            margin-bottom: 7px;
        }

        input[type="text"],
        input[type="file"] {
            width: 100%;
            box-sizing: border-box;
            padding: 11px 12px;
            border: 1px solid #ccd0dd;
            border-radius: 6px;
            font-size: 14px;
            background: #fff;
        }

        .hint {
            color: #666;
            font-size: 13px;
            line-height: 1.7;
        }

        .btn {
            border: none;
            border-radius: 6px;
            padding: 12px 22px;
            font-size: 15px;
            font-weight: 700;
            cursor: pointer;
            background: #651fff;
            color: white;
            box-shadow: 0 3px 8px rgba(101,31,255,.25);
        }

        .btn:hover {
            background: #5017cc;
        }

        .result {
            display: none;
            border-left: 5px solid #651fff;
        }

        .result.success {
            border-left-color: #2eaf52;
        }

        .result.error {
            border-left-color: #e61f35;
        }

        .msg {
            white-space: pre-wrap;
            line-height: 1.7;
            font-size: 14px;
        }

        .download {
            display: inline-block;
            margin-top: 14px;
            padding: 10px 16px;
            background: #17265f;
            color: #fff;
            border-radius: 5px;
            text-decoration: none;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
            margin-top: 14px;
        }

        th, td {
            border-bottom: 1px solid #e6e8f0;
            padding: 10px;
            text-align: left;
        }

        th {
            background: #f0f2ff;
            color: #17265f;
        }


		.bottom-image-card {
		    text-align: left;
		}

		.bottom-image-title {
		    text-align: left !important;
		    margin-left: 0;
		}

        .bottom-page-image {
            max-width: 100%;
            height: auto;
            border-radius: 8px;
            box-shadow: 0 6px 16px rgba(18, 38, 63, .12);
        }

        .red {
            color: red;
            font-weight: 700;
        }

        .template-download-card {
            text-align: left;
        }

        .template-download-title {
            text-align: left !important;
            margin-left: 0;
        }

    </style>
</head>
<body>

<div class="topbar">
    <div class="logo"></div>
    <div class="title">CPSC <span>eFILING</span> CSV Generator</div>
</div>

<div class="container">

    <div class="card template-download-card">
        <h2 class="template-download-title">客户收集数据 Excel 模板下载地址</h2>
        <p class="hint">
            请先下载客户收集数据模板，填写完成后再在本页面上传，系统会自动生成 CPSC Import 用 CSV。
        </p>
        <a class="download"
           href="<%=request.getContextPath()%>/template/CPSC_eFiling_信息收集表csv用.xlsx"
           download>
            下载客户收集数据 Excel 模板
        </a>
    </div>

    <div class="card">
        <h2>客户收集数据 Excel → CPSC Import CSV</h2>

        <form id="uploadForm" method="post" enctype="multipart/form-data"
              action="<%=request.getContextPath()%>/import-csv-generate">

            <div class="form-row" style="display:none;">
                <div class="form-group" >
                    <label>Certifier ID（认证主体ID）</label>
                    <input type="text" name="certifierId" placeholder="例如 ForeverCo.,Ltd." required value="ForeverCo.,Ltd.">
                </div>

                <div class="form-group">
                    <label>Collection ID / Collection Name（产品集合ID或名称）</label>
                    <input type="text" name="collectionId" placeholder="例如 TEST" required value="TEST">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>上传客户收集数据 Excel（.xlsx）</label>
                    <input type="file" name="excelFile" accept=".xlsx,.xls" required>
                    <div class="hint">
                        支持我之前生成的《CPSC_eFiling_客户收集数据表_中英文注释版.xlsx》。<br>
                        系统会自动读取官方英文字段行，并生成 CPSC Import 用 CSV。
                    </div>
                </div>
            </div>

            <button type="submit" class="btn">读取 Excel 并生成 Import CSV</button>
        </form>
    </div>

    <div id="resultBox" class="card result">
        <h2>处理结果</h2>
        <div id="resultMsg" class="msg"></div>
        <div id="downloadArea"></div>
    </div>

    <div class="card">
        <h2>生成规则说明</h2>
        <table>
            <tr>
                <th>项目</th>
                <th>说明</th>
            </tr>
            <tr>
                <td>CSV 文件名</td>
                <td>文件名末尾自动追加 yyyyMMddHHmmssSSS，精确到毫秒</td>
            </tr>
            <tr>
                <td>字段来源</td>
                <td>以 Excel 中官方英文字段为准，例如 Product Update、New Version ID、Primary Product ID</td>
            </tr>
            <tr>
                <td>数据库</td>
                <td>生成记录会写入 CPSC_eFiling_import_batch 表，状态为 CSV_CREATED</td>
            </tr>
            <tr>
                <td>红色提示</td>
                <td class="red">如果字段缺失、日期格式不对、必填项为空，后台会返回错误信息</td>
            </tr>
        </table>
    </div>

    <div class="card bottom-image-card">
        <h2>CPSC eFILING CSV导入方法</h2>
        <img class="bottom-page-image"
             src="<%=request.getContextPath()%>/img/6a2c4877-f461-49cb-a6ba-f8c22aaab6ba.png"
             alt="页面底部图片">
    </div>
</div>

<script>
    $("#uploadForm").on("submit", function (e) {
        e.preventDefault();

        let formData = new FormData(this);
        $("#resultBox").removeClass("success error").show();
        $("#resultMsg").html("正在读取 Excel，并生成 CSV，请稍等...");
        $("#downloadArea").html("");

        $.ajax({
            url: $(this).attr("action"),
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            dataType: "json",
            success: function (res) {
                if (res.success) {
                    $("#resultBox").addClass("success");
                    $("#resultMsg").html(
                        "处理成功！\n\n" +
                        "生成文件：" + res.fileName + "\n" +
                        "数据库批次ID：" + res.batchId + "\n" +
                        "数据行数：" + res.rowCount
                    );

                    $("#downloadArea").html(
                        '<a class="download" href="' + res.downloadUrl + '">下载 Import CSV</a>'
                    );
                } else {
                    $("#resultBox").addClass("error");
                    $("#resultMsg").html('<span class="red">' + escapeHtml(res.message) + '</span>');
                }
            },
            error: function (xhr) {
                $("#resultBox").addClass("error");
                $("#resultMsg").html('<span class="red">请求失败：' + escapeHtml(xhr.responseText) + '</span>');
            }
        });
    });

    function escapeHtml(text) {
        if (!text) return "";
        return text.replace(/[&<>"']/g, function (m) {
            return ({
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                '"': '&quot;',
                "'": '&#39;'
            })[m];
        });
    }
</script>

</body>
</html>
