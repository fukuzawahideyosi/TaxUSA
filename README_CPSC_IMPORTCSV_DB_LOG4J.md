# CPSC importCsv.jsp 同步保存DB + products.jsp明细显示 + Log4j2 修正版

## 本次修改内容

1. `importCsv.jsp` 上传客户Excel后：
   - 生成 CPSC Import CSV；
   - CSV 文件名自动追加 `yyyyMMddHHmmssSSS` 到毫秒；
   - 同时把客户Excel读取出的产品明细保存到本地 MySQL。

2. 保存到数据库的表包括：
   - `CPSC_eFiling_import_batch`
   - `CPSC_eFiling_import_batch_item`
   - `CPSC_eFiling_product_certificate`
   - `CPSC_eFiling_manufacturer`
   - `CPSC_eFiling_lab`
   - `CPSC_eFiling_product_lab`
   - `CPSC_eFiling_product_lab_citation`
   - `CPSC_eFiling_poc`（客户填写POC详细信息时保存）
   - `CPSC_eFiling_product_identifier`

3. `products.jsp` 可以通过以下地址查看刚读入DB的明细：

```text
/products?batchId=批次ID
```

4. CPSC eFiling 相关 Java 类都加入了 Log4j2 Logger。

5. Eclipse 调试时日志会输出到 Console 控制台。配置文件：

```text
src/log4j2.xml
```

## 重点替换文件

```text
pom.xml
src/log4j2.xml
WebContent/importCsv.jsp
WebContent/products.jsp
src/com/cpsc/efiling/service/CpscImportCsvService.java
src/com/cpsc/efiling/service/CpscCsvExcelDbSaveService.java
src/com/cpsc/efiling/service/ProductQueryService.java
src/com/cpsc/efiling/servlet/ImportCsvGenerateServlet.java
src/com/cpsc/efiling/servlet/ProductListServlet.java
src/com/cpsc/efiling/util/DbUtil.java
```

## Eclipse控制台看日志

直接在 Eclipse 中启动 Tomcat，Console 会看到：

```text
开始处理客户Excel
Excel读取完成
CSV生成成功
开始保存CSV和产品明细到DB
产品明细保存完成
products.jsp数据准备完成
```

