package com.panda.batch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Element;

import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsExcel;

public class ExcelToXMLConverter {


	private static Logger logger = Logger.getLogger(ExcelToXMLConverter.class.toString());

    public static void main(String[] args) {


//    	String main_folderPath = "E:\\日本-软件开发\\e-Tax仕様書一覧全仕様書（一括ダウンロード）";
        String main_folderPath = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）";
    	String folderPath = main_folderPath;
    	folderPath = "E:\\日本-e-tax\\e-taxall";

    	File folder = new File(folderPath);



        /*
         *
         */

//		folderPath = main_folderPath + "/11XML構造設計書等【消費税】";
//		folderPath = main_folderPath + "/17XML構造設計書等【その他】";
//
//        String fileExtension = ".ps"; // 需要删除的文件扩展名
//        folder = new File(folderPath);
//        FuncUtils.deleteFilesInFolder(folder, fileExtension);
//		processFolder_XML構造設計書(folder); // 递归处理文件夹下的所有文件



        /*
         *
         */
//        folderPath = main_folderPath + "/07手続一覧等";

        folder = new File(folderPath);
		String fileExtension = ".csv"; // 需要删除的文件扩展名
		FuncUtils.deleteFilesInFolder(folder, fileExtension);
        processFolder(folder); // 递归处理文件夹下的所有文件


        /*
         *
         */




	}

    public static void processFolder_XML構造設計書(File folder) {

        for (File file : folder.listFiles()) {
            if (file.isDirectory() && file.getPath().contains("XML構造設計書")) {
            	processFolder_XML構造設計書(file); // 如果是子文件夹，递归调用

            } else if (file.getName().endsWith(".xlsx") && file.getPath().contains("XML構造設計書")) {
            	if (file.getPath().contains("11XML構造設計書等【消費税】")
            			|| file.getPath().contains("17XML構造設計書等【その他】")
            			) {

            	} else {
            		continue;

            	}

            	if (file.getName().contains("XML構造設計書")) {
//                	processExcelFile_moban(file);

            	} else if (file.getName().contains("帳票フィールド仕様書")) {
            		logger.debug("\n" + "nexcelFileName：" + file.getPath());
                	processExcelFile_ps_master(file);
            	}


            }

        }
    }

    // 递归处理文件夹中的所有文件
    public static void processFolder(File folder) {


        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                processFolder(file); // 如果是子文件夹，递归调用


//            } else if (file.getName().endsWith(".xlsx") && file.getPath().contains("XML構造設計書")) {
////            	if (!file.getPath().contains("11XML構造設計書等【消費税】")) {
////            		continue;
////            	}
//
//
//            	if (file.getName().contains("XML構造設計書")) {
////                	processExcelFile_moban(file);
//
//            	} else if (file.getName().contains("帳票フィールド仕様書")) {
//            		logger.debug("\n" + "nexcelFileName：" + file.getPath());
//                	processExcelFile_ps_master(file);
//            	}


            } else if (file.getName().endsWith(".xlsx") && file.getPath().contains("01手続一覧")) {
                processExcelFile_csv(file, 2);


//            } else if (file.getName().endsWith(".xlsx") && file.getPath().contains("02手続内帳票対応表")) {
//            	processExcelFile_csv(file, 4);

            }

        }
    }

	private static void processExcelFile_csv(File excelFile, int start) {
		FileInputStream fis = null;
		Workbook workbook = null;

		try {
			fis = new FileInputStream(excelFile);
			workbook = WorkbookFactory.create(fis);

			String csvFileName = excelFile.getName().replace(".xlsx", ".csv");
			String csvFilePath = excelFile.getParent() + "\\" + csvFileName;

			//TODO
			if (excelFile.toPath().toString().replace("\\", "/").contains("/01手続一覧/手続一覧（申告）")
					|| excelFile.toPath().toString().replace("\\", "/").contains("/02手続内帳票対応表/手続内帳票対応表(申告）")) {

			} else {
				//return;
			}
			if (excelFile.toPath().toString().replace("\\", "/").contains("手続一覧（申告）Ver232x")) {
				csvFileName = csvFileName;
			}
			if (!excelFile.toPath().toString().replace("\\", "/").contains("手続内帳票対応表(申告）Ver130x")) {
//				return;
			}
			if (!excelFile.toPath().toString().replace("\\", "/").contains("イメージ添付書類手続内帳票対応表（申告）Ver150x")) {
				csvFileName = csvFileName;
			}

			//TODO




			logger.debug("excelFileName：" + excelFile.toPath());

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath, true))) {
				for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
					Sheet sheet = workbook.getSheetAt(i);
					Iterator<Row> rowIterator = sheet.iterator();
					int rowIndex = 0;

					String value0 = "";
					String value8 = "";
					while (rowIterator.hasNext()) {
						Row row = rowIterator.next();
						rowIndex++;
						if (rowIndex <= start) {
							//continue;
						}

						int count = 0;
						StringBuilder csvLine = new StringBuilder();

						for (int colIndex = 0; colIndex < row.getLastCellNum(); colIndex++) {
							++count;

							Cell cell = row.getCell(colIndex);

							String value = "";
							if (cell == null || cell.getCellType() == CellType.BLANK) {
//								// 如果单元格为空并且是合并单元格，则获取合并单元格的值
//								value = FuncUtilsAiEtax.isMergedRegion(sheet, rowIndex - 1, colIndex)
//										? FuncUtilsAiEtax.getMergedCellValue(sheet, rowIndex - 1, colIndex)
//										: "";

								if (colIndex == 0) {
									value = value0;
								}
								if (colIndex == 8) {
									value = value8;
								}

							} else {
								value = FuncUtilsExcel.getCellValueAsString(cell).replaceAll("\r\n|\r|\n", "");
							}




//							//初始化，第一次记录
//							if (colIndex == 0 && value0 == null) {
//								value0 = value;
//							}
//
//							if (colIndex == 8 && value8 == null) {
//								value8 = value;
//							}



							//TODO
							if ("PGE9010".equals(value) ) {
								value = value;

							}

							//值变化的时候，刷新
							if (colIndex == 0 && !Objects.equals(value0, value)) {
								value0 = value;

							}
							if (colIndex == 8 && !Objects.equals(value8, value)) {
								value8 = value;
							}



							if (colIndex == 0 && !value.matches("^[A-Za-z]{3}\\d{4}$")) {
								break;
							}


							if (value.contains(",")) {
								value = value.replace(",", "，");
							}

							//                        idOld = value.split(",")[0];

							if (!StringUtils.isEmpty(value.replace(",", ""))) {
								csvLine.append(value).append(",");
							} else {
								csvLine.append(",");
							}

							//                        logger.info("value["+count+"]" + value);

						}


						if (csvLine.length() > 0) {
							logger.info("csvLine["+i+"]" + csvLine);
							//手続き名称は値有の場合、抽出
							String[] csvLine_list = csvLine.toString().split(",");
							if (csvLine_list.length > 1) {
								if (!StringUtils.isEmpty(csvLine_list[1])) {
									writer.write(csvLine.substring(0, csvLine.length() - 1));
									writer.newLine();

								}
							}

						}
					}
				}
				logger.debug("已生成CSV文件：" + csvFilePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void processExcelFile_ps_master(File excelFile) {
		String filePath = excelFile.getPath().split("Ver")[0] + ".ps";

        FileInputStream fis = null;
        Workbook workbook = null;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            fis = new FileInputStream(excelFile);
            workbook = WorkbookFactory.create(fis);

            String excelFileName = excelFile.getName();
//            logger.debug("excelFileName：" + excelFile.toPath());

            String fileBaseName = "";
            if (excelFileName.indexOf("Ver") > -1) {
                fileBaseName = "_" + excelFileName.substring(excelFileName.indexOf("Ver"), excelFileName.lastIndexOf(".xlsx"));
            }
            String parentDir = excelFile.getParent(); // 获取输入文件的路径

            // 根据文件路径设置开始行数
            int startRow = excelFile.getPath().contains("02受付システムインターフェイス") ? 7 : 4;

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);

                // 获取G2单元格的值
                Row row = sheet.getRow(1);  // G2在索引为1的行
                if (row != null) {
                    Cell cell_G2 = row.getCell(6);  // G列是第7列，索引为6
                    Cell cell_O2 = row.getCell(14);  // G列是第7列，索引为6
                    String cellValue_G2 = FuncUtilsExcel.getCellValueAsString(cell_G2);
                    String cellValue_O2 = FuncUtilsExcel.getCellValueAsString(cell_O2);

                    // 将G2单元格的值写入文件
                    writer.write(cellValue_G2 + "," + cellValue_O2+ "," + excelFile.getName());
                    writer.newLine();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

	}

	public static void processExcelFile_moban(File excelFile) {
        FileInputStream fis = null;
        Workbook workbook = null;
        try {
            fis = new FileInputStream(excelFile);
            workbook = WorkbookFactory.create(fis);

            String excelFileName = excelFile.getName();
            logger.debug("excelFileName：" + excelFile.toPath());

            String fileBaseName = "";
            if (excelFileName.indexOf("Ver") > -1) {
                fileBaseName = "_" + excelFileName.substring(excelFileName.indexOf("Ver"), excelFileName.lastIndexOf(".xlsx"));
            }
            String parentDir = excelFile.getParent(); // 获取输入文件的路径

            // 根据文件路径设置开始行数
            int startRow = excelFile.getPath().contains("02受付システムインターフェイス") ? 7 : 4;

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                String xmlFileName = sheetName + fileBaseName + ".xml";

                // 使用输入文件的路径生成输出路径
                String outputFilePath = parentDir + File.separator + xmlFileName;
                generateXML(sheet, outputFilePath, startRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void generateXML(Sheet sheet, String xmlFileName, int startRow) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            org.w3c.dom.Document xmlDocument = docBuilder.newDocument();

            // 创建根元素
            Element rootElement = xmlDocument.createElement("Root");
            xmlDocument.appendChild(rootElement);

            // 使用栈管理元素层次
            Stack<Element> elementStack = new Stack<>();
            elementStack.push(rootElement);
            int previousIndentLevel = 0;

            // 使用指定的开始行数进行循环
            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                Cell tagNameCell = row.getCell(18); // S列（第19列）作为标签名字
                Cell idRefCell = row.getCell(16); // Q列（第17列）作为IDREF属性
                Cell indentCell = row.getCell(1); // B列（第2列）作为缩进空格个数
                Cell otherAttributesCell = row.getCell(17); // R列（第18列）作为其他属性名

                if (tagNameCell == null || indentCell == null) continue;

                String tagName = tagNameCell.getStringCellValue();
                String idRef = idRefCell != null ? idRefCell.getStringCellValue().trim() : "";
                String otherAttributes = otherAttributesCell != null ? otherAttributesCell.getStringCellValue().trim() : "";
                int indentLevel = 0;

                // 检查缩进单元格是否为数值类型
                if (indentCell.getCellType() == CellType.NUMERIC) {
                    indentLevel = (int) indentCell.getNumericCellValue();
                } else if (indentCell.getCellType() == CellType.STRING) {
                    try {
                        indentLevel = Integer.parseInt(indentCell.getStringCellValue().trim().replace("　", ""));
                    } catch (NumberFormatException e) {
                        logger.debug("缩进单元格的值不是有效的数字：" + indentCell.getStringCellValue());
                    }
                }

                // 创建新元素并设置属性
                Element element = xmlDocument.createElement(tagName);
                // 添加文本节点，确保不会简写标签
                element.appendChild(xmlDocument.createTextNode("#削除用文字#"));

                //TODO
//            	if (rowIndex == startRow) {
//                    element.setAttribute("id", tagName + "-1");
//            	}

                // 将 otherAttributes 内容按行拆分并逐行添加为属性
                if (!otherAttributes.isEmpty()) {
                    String[] attributeLines = otherAttributes.split("\\n");
                    for (int i = 0; i < attributeLines.length; i++) {
                    	String attribute= attributeLines[i];

                    	String value = "";

                        if ("page".equals(attribute)) {
                        	value = "1";

                        } else if ("VR".equals(attribute)) {
                        	value = sheet.getRow(1).getCell(20).toString();

                        } else if ("softNM".equals(attribute)) {
                        } else if ("sakuseiNM".equals(attribute)) {
                        } else if ("sakuseiDay".equals(attribute)) {
                        }

                        element.setAttribute(attribute, value);

                    }
                }

                if (!idRef.isEmpty()) {
                    element.setAttribute("IDREF", idRef);
                }


                // 根据缩进级别调整元素层次结构
                while (indentLevel < previousIndentLevel) {
                    elementStack.pop();
                    previousIndentLevel--;
                }

                if (indentLevel > previousIndentLevel) {
                    elementStack.peek().appendChild(element);
                    elementStack.push(element);
                    previousIndentLevel = indentLevel;
                } else {
                    elementStack.pop();
                    elementStack.peek().appendChild(element);
                    elementStack.push(element);
                }
            }

            // 转换为字符串并格式化输出
            String xmlContent = convertDocumentToString(xmlDocument);
//            logger.debug("格式化后的XML内容:\n" + xmlContent);

            // 保存替换后的内容到文件
            saveXMLContentToFile(xmlContent, xmlFileName);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    // 将 Document 转换为格式化的字符串
    public static String convertDocumentToString(org.w3c.dom.Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // 不输出 XML 声明
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // 设置缩进量为4

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            String xmlContent = writer.toString();

            // 删除特定标记并去除空行
            xmlContent = xmlContent.replace("#削除用文字#", "");
            xmlContent = xmlContent.replace("<Root>", "");
            xmlContent = xmlContent.replace("</Root>", "");
            xmlContent = xmlContent.replaceAll("(?m)^[ \\t]*\\r?\\n", ""); // 删除空行

            return xmlContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // 将字符串内容保存到文件，使用 UTF-8 编码
    public static void saveXMLContentToFile(String xmlContent, String filePath) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            writer.write(xmlContent);
            logger.debug("生成XML文件: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
