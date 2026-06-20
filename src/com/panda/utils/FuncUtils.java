package com.panda.utils;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.mozilla.universalchardet.UniversalDetector;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_account_xiaofeishuiBean;
import com.panda.bean.t_jct_shenqingBean;
import com.panda.bean.t_xiaofeishui_shengaoBean;
import com.panda.dao.EtaxDao;
import com.panda.dao.m_sequenceDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.servlet.ai.ColumnDefinition;
import com.panda.servlet.ai.TableDefinition;
import com.spire.doc.FileFormat;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;




public class FuncUtils {

	private static Logger logger = Logger.getLogger(FuncUtils.class.toString());
	//	/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）/07手続一覧等/01手続一覧
	//	E:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/t mp3/wtpwebapps/PandaServiceMA/WEB-INF/classes/
    public static String projectPath = FuncUtils.class.getClassLoader().getResource("").getPath().replace("WEB-INF/classes/", "").replace("/E:", "E:")
    		.replace("E:/Users/Administrator/git/PandaServiceMA/PandaServiceMA/target/classes/", "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA/")
    		.replace("//", "/")
    		;

	public static HashMap<String, String> HashMapWareki = new HashMap<String, String>();
	private static HashMap<String, String> HashMapWarekiEnglish = new HashMap<String, String>();
	public static HashMap<String, String> HashMapPinyinKana = new HashMap<String, String>();

	public FuncUtils() {
		super();
		//1明治・2大正・3昭和・4平成・5令和
		HashMapWareki.put("明治", "1");
		HashMapWareki.put("大正", "2");
		HashMapWareki.put("昭和", "3");
		HashMapWareki.put("平成", "4");
		HashMapWareki.put("令和", "5");

		HashMapWarekiEnglish.put("明治", "M");
		HashMapWarekiEnglish.put("大正", "T");
		HashMapWarekiEnglish.put("昭和", "S");
		HashMapWarekiEnglish.put("平成", "H");
		HashMapWarekiEnglish.put("令和", "R");

		HashMapPinyinKana.put("lv", "リー");//手动添加
		//https://fimi.info/pinyin/onsetu/
		//エクセル
		//=IF(A1="","","HashMapPinyinKana.put("""&A1&""", """&B1&""");")
		HashMapPinyinKana.put("a", "アー");
		HashMapPinyinKana.put("ai", "アイ");
		HashMapPinyinKana.put("an", "アン");
		HashMapPinyinKana.put("ang", "アン");
		HashMapPinyinKana.put("ao", "アオ");
		HashMapPinyinKana.put("ba", "バー");
		HashMapPinyinKana.put("bai", "バイ");
		HashMapPinyinKana.put("ban", "バン");
		HashMapPinyinKana.put("bang", "バン");
		HashMapPinyinKana.put("bao", "バオ");
		HashMapPinyinKana.put("bei", "ベイ");
		HashMapPinyinKana.put("ben", "ベン");
		HashMapPinyinKana.put("beng", "ボン");
		HashMapPinyinKana.put("bi", "ビー");
		HashMapPinyinKana.put("bian", "ビエン");
		HashMapPinyinKana.put("biao", "ビャオ");
		HashMapPinyinKana.put("bie", "ビエ");
		HashMapPinyinKana.put("bin", "ビン");
		HashMapPinyinKana.put("bing", "ビン");
		HashMapPinyinKana.put("bo", "ボー");
		HashMapPinyinKana.put("bu", "ブー");
		HashMapPinyinKana.put("ca", "ツァー");
		HashMapPinyinKana.put("cai", "ツァイ");
		HashMapPinyinKana.put("can", "ツァン");
		HashMapPinyinKana.put("cang", "ツァン");
		HashMapPinyinKana.put("cao", "ツァオ");
		HashMapPinyinKana.put("ce", "ツォー,ツァー,ツー");
		HashMapPinyinKana.put("cen", "ツェン");
		HashMapPinyinKana.put("ceng", "ツォン");
		HashMapPinyinKana.put("cha", "チャー");
		HashMapPinyinKana.put("chai", "チャイ");
		HashMapPinyinKana.put("chan", "チャン");
		HashMapPinyinKana.put("chang", "チャン");
		HashMapPinyinKana.put("chao", "チャオ");
		HashMapPinyinKana.put("che", "チョー,チャー");
		HashMapPinyinKana.put("chen", "チェン");
		HashMapPinyinKana.put("cheng", "チョン");
		HashMapPinyinKana.put("chi", "チー");
		HashMapPinyinKana.put("chong", "チョン");
		HashMapPinyinKana.put("chou", "チョウ");
		HashMapPinyinKana.put("chu", "チュー");
		HashMapPinyinKana.put("chua", "チュワー");
		HashMapPinyinKana.put("chuai", "チュワイ");
		HashMapPinyinKana.put("chuan", "チュワン");
		HashMapPinyinKana.put("chuang", "チュワン");
		HashMapPinyinKana.put("chui", "チュイ");
		HashMapPinyinKana.put("chun", "チュン");
		HashMapPinyinKana.put("chuo", "チュオ");
		HashMapPinyinKana.put("ci", "ツー");
		HashMapPinyinKana.put("cong", "ツォン");
		HashMapPinyinKana.put("cou", "ツォウ");
		HashMapPinyinKana.put("cu", "ツー");
		HashMapPinyinKana.put("cuan", "ツワン");
		HashMapPinyinKana.put("cui", "ツイ");
		HashMapPinyinKana.put("cun", "ツン");
		HashMapPinyinKana.put("cuo", "ツオ");
		HashMapPinyinKana.put("da", "ダー");
		HashMapPinyinKana.put("dai", "ダイ");
		HashMapPinyinKana.put("dan", "ダン");
		HashMapPinyinKana.put("dang", "ダン");
		HashMapPinyinKana.put("dao", "ダオ");
		HashMapPinyinKana.put("de", "ダー,ドー");
		HashMapPinyinKana.put("dei", "デイ");
		HashMapPinyinKana.put("den", "デン");
		HashMapPinyinKana.put("deng", "ドン");
		HashMapPinyinKana.put("di", "ディー");
		HashMapPinyinKana.put("dia", "デャー");
		HashMapPinyinKana.put("dian", "ディエン");
		HashMapPinyinKana.put("diao", "デャオ");
		HashMapPinyinKana.put("die", "ディエ");
		HashMapPinyinKana.put("ding", "ディン");
		HashMapPinyinKana.put("diu", "デュー");
		HashMapPinyinKana.put("dong", "ドン");
		HashMapPinyinKana.put("dou", "ドウ");
		HashMapPinyinKana.put("du", "ドゥー");
		HashMapPinyinKana.put("duan", "ドワン");
		HashMapPinyinKana.put("dui", "ドゥイ");
		HashMapPinyinKana.put("dun", "ドゥン");
		HashMapPinyinKana.put("duo", "ドゥオ");
		HashMapPinyinKana.put("e", "オー,ウー");
		HashMapPinyinKana.put("ei", "エイ");
		HashMapPinyinKana.put("en", "エン");
		HashMapPinyinKana.put("eng", "オン");
		HashMapPinyinKana.put("er", "アー,アル");
		HashMapPinyinKana.put("fa", "ファー");
		HashMapPinyinKana.put("fan", "ファン");
		HashMapPinyinKana.put("fang", "ファン");
		HashMapPinyinKana.put("fei", "フェイ");
		HashMapPinyinKana.put("fen", "フェン");
		HashMapPinyinKana.put("feng", "フォン");
		HashMapPinyinKana.put("fo", "フォー");
		HashMapPinyinKana.put("fou", "フォウ");
		HashMapPinyinKana.put("fu", "フー");
		HashMapPinyinKana.put("ga", "ガー");
		HashMapPinyinKana.put("gai", "ガイ");
		HashMapPinyinKana.put("gan", "ガン");
		HashMapPinyinKana.put("gang", "ガン");
		HashMapPinyinKana.put("gao", "ガオ");
		HashMapPinyinKana.put("ge", "ゴー,グー,ガー");
		HashMapPinyinKana.put("gei", "ゲイ");
		HashMapPinyinKana.put("gen", "ゲン");
		HashMapPinyinKana.put("geng", "ゴン");
		HashMapPinyinKana.put("gong", "ゴン");
		HashMapPinyinKana.put("gou", "ゴウ");
		HashMapPinyinKana.put("gu", "グー");
		HashMapPinyinKana.put("gua", "グワー");
		HashMapPinyinKana.put("guai", "グワイ");
		HashMapPinyinKana.put("guan", "グワン");
		HashMapPinyinKana.put("guang", "グワン");
		HashMapPinyinKana.put("gui", "グイ");
		HashMapPinyinKana.put("gun", "グン");
		HashMapPinyinKana.put("guo", "グオ");
		HashMapPinyinKana.put("ha", "ハー");
		HashMapPinyinKana.put("hai", "ハイ");
		HashMapPinyinKana.put("han", "ハン");
		HashMapPinyinKana.put("hang", "ハン");
		HashMapPinyinKana.put("hao", "ハオ");
		HashMapPinyinKana.put("he", "ホー,フー,ハー");
		HashMapPinyinKana.put("hei", "ヘイ");
		HashMapPinyinKana.put("hen", "ヘン");
		HashMapPinyinKana.put("heng", "ホン");
		HashMapPinyinKana.put("hng", "フン");
		HashMapPinyinKana.put("hong", "ホン");
		HashMapPinyinKana.put("hou", "ホウ");
		HashMapPinyinKana.put("hu", "フー");
		HashMapPinyinKana.put("hua", "フワー");
		HashMapPinyinKana.put("huai", "フワイ");
		HashMapPinyinKana.put("huan", "フアン");
		HashMapPinyinKana.put("huang", "フアン");
		HashMapPinyinKana.put("hui", "フイ");
		HashMapPinyinKana.put("hun", "フン");
		HashMapPinyinKana.put("huo", "フオ");
		HashMapPinyinKana.put("ji", "ジー");
		HashMapPinyinKana.put("jia", "ジャー");
		HashMapPinyinKana.put("jian", "ジエン");
		HashMapPinyinKana.put("jiang", "ジャン");
		HashMapPinyinKana.put("jiao", "ジャオ");
		HashMapPinyinKana.put("jie", "ジエ");
		HashMapPinyinKana.put("jin", "ジン");
		HashMapPinyinKana.put("jing", "ジン");
		HashMapPinyinKana.put("jiong", "ジョン");
		HashMapPinyinKana.put("jiu", "ジュー");
		HashMapPinyinKana.put("ju", "ジー");
		HashMapPinyinKana.put("juan", "ジュエン");
		HashMapPinyinKana.put("jue", "ジュエ");
		HashMapPinyinKana.put("jun", "ジン");
		HashMapPinyinKana.put("ka", "カー");
		HashMapPinyinKana.put("kai", "カイ");
		HashMapPinyinKana.put("kan", "カン");
		HashMapPinyinKana.put("kang", "カン");
		HashMapPinyinKana.put("kao", "カオ");
		HashMapPinyinKana.put("ke", "コー,クー,カー");
		HashMapPinyinKana.put("kei", "ケイ");
		HashMapPinyinKana.put("ken", "ケン");
		HashMapPinyinKana.put("keng", "コン");
		HashMapPinyinKana.put("kong", "コン");
		HashMapPinyinKana.put("kou", "コウ");
		HashMapPinyinKana.put("ku", "クー");
		HashMapPinyinKana.put("kua", "クワー");
		HashMapPinyinKana.put("kuai", "クワイ");
		HashMapPinyinKana.put("kuan", "クワン");
		HashMapPinyinKana.put("kuang", "クワン");
		HashMapPinyinKana.put("kui", "クイ");
		HashMapPinyinKana.put("kun", "クン");
		HashMapPinyinKana.put("kuo", "クオ");
		HashMapPinyinKana.put("la", "ラー");
		HashMapPinyinKana.put("lai", "ライ");
		HashMapPinyinKana.put("lan", "ラン");
		HashMapPinyinKana.put("lang", "ラン");
		HashMapPinyinKana.put("lao", "ラオ");
		HashMapPinyinKana.put("le", "ラー");
		HashMapPinyinKana.put("lei", "レイ");
		HashMapPinyinKana.put("leng", "ロン");
		HashMapPinyinKana.put("li", "リー");
		HashMapPinyinKana.put("lia", "リャー");
		HashMapPinyinKana.put("lian", "リエン");
		HashMapPinyinKana.put("liang", "リャン");
		HashMapPinyinKana.put("liao", "リャオ");
		HashMapPinyinKana.put("lie", "リエ");
		HashMapPinyinKana.put("lin", "リン");
		HashMapPinyinKana.put("ling", "リン");
		HashMapPinyinKana.put("liu", "リュー");
		HashMapPinyinKana.put("lo", "ロー");
		HashMapPinyinKana.put("long", "ロン");
		HashMapPinyinKana.put("lou", "ロウ");
		HashMapPinyinKana.put("lü", "リー");
		HashMapPinyinKana.put("lu", "ルー");
		HashMapPinyinKana.put("luan", "ルワン");
		HashMapPinyinKana.put("lüe", "リュエ");
		HashMapPinyinKana.put("lun", "ルン");
		HashMapPinyinKana.put("luo", "ルオ");
		HashMapPinyinKana.put("m", "ン");
		HashMapPinyinKana.put("ma", "マー");
		HashMapPinyinKana.put("mai", "マイ");
		HashMapPinyinKana.put("man", "マン");
		HashMapPinyinKana.put("mang", "マン");
		HashMapPinyinKana.put("mao", "マオ");
		HashMapPinyinKana.put("me", "マー,モー");
		HashMapPinyinKana.put("mei", "メイ");
		HashMapPinyinKana.put("men", "メン");
		HashMapPinyinKana.put("meng", "モン");
		HashMapPinyinKana.put("mi", "ミー");
		HashMapPinyinKana.put("mian", "ミエン");
		HashMapPinyinKana.put("miao", "ミャオ");
		HashMapPinyinKana.put("mie", "ミエ");
		HashMapPinyinKana.put("min", "ミン");
		HashMapPinyinKana.put("ming", "ミン");
		HashMapPinyinKana.put("miu", "ミュー");
		HashMapPinyinKana.put("mo", "モー");
		HashMapPinyinKana.put("mou", "モウ");
		HashMapPinyinKana.put("mu", "ムー");
		HashMapPinyinKana.put("n", "ン");
		HashMapPinyinKana.put("na", "ナー");
		HashMapPinyinKana.put("nai", "ナイ");
		HashMapPinyinKana.put("nan", "ナン");
		HashMapPinyinKana.put("nang", "ナン");
		HashMapPinyinKana.put("nao", "ナオ");
		HashMapPinyinKana.put("ne", "ナー,ノー");
		HashMapPinyinKana.put("nei", "ネイ");
		HashMapPinyinKana.put("nen", "ネン");
		HashMapPinyinKana.put("neng", "ノン");
		HashMapPinyinKana.put("ng", "ン");
		HashMapPinyinKana.put("ni", "ニー");
		HashMapPinyinKana.put("nian", "ニエン");
		HashMapPinyinKana.put("niang", "ニャン");
		HashMapPinyinKana.put("niao", "ニャオ");
		HashMapPinyinKana.put("nie", "ニエ");
		HashMapPinyinKana.put("nin", "ニン");
		HashMapPinyinKana.put("ning", "ニン");
		HashMapPinyinKana.put("niu", "ニュー");
		HashMapPinyinKana.put("nong", "ノン");
		HashMapPinyinKana.put("nou", "ノウ");
		HashMapPinyinKana.put("nü", "ニー");
		HashMapPinyinKana.put("nu", "ヌー");
		HashMapPinyinKana.put("nuan", "ヌワン");
		HashMapPinyinKana.put("nüe", "ニュエ");
		HashMapPinyinKana.put("nun", "ヌン");
		HashMapPinyinKana.put("nuo", "ヌオ");
		HashMapPinyinKana.put("o", "オー");
		HashMapPinyinKana.put("ou", "オウ");
		HashMapPinyinKana.put("pa", "パー");
		HashMapPinyinKana.put("pai", "パイ");
		HashMapPinyinKana.put("pan", "パン");
		HashMapPinyinKana.put("pang", "パン");
		HashMapPinyinKana.put("pao", "パオ");
		HashMapPinyinKana.put("pei", "ペイ");
		HashMapPinyinKana.put("pen", "ペン");
		HashMapPinyinKana.put("peng", "ポン");
		HashMapPinyinKana.put("pi", "ピー");
		HashMapPinyinKana.put("pian", "ピエン");
		HashMapPinyinKana.put("piao", "ピャオ");
		HashMapPinyinKana.put("pie", "ピエ");
		HashMapPinyinKana.put("pin", "ピン");
		HashMapPinyinKana.put("ping", "ピン");
		HashMapPinyinKana.put("po", "ポー");
		HashMapPinyinKana.put("pou", "ポウ");
		HashMapPinyinKana.put("pu", "プー");
		HashMapPinyinKana.put("qi", "チー");
		HashMapPinyinKana.put("qia", "チャー");
		HashMapPinyinKana.put("qian", "チエン");
		HashMapPinyinKana.put("qiang", "チャン");
		HashMapPinyinKana.put("qiao", "チャオ");
		HashMapPinyinKana.put("qie", "チエ");
		HashMapPinyinKana.put("qin", "チン");
		HashMapPinyinKana.put("qing", "チン");
		HashMapPinyinKana.put("qiong", "チョン");
		HashMapPinyinKana.put("qiu", "チュー");
		HashMapPinyinKana.put("qu", "チー");
		HashMapPinyinKana.put("quan", "チュエン");
		HashMapPinyinKana.put("que", "チュエ");
		HashMapPinyinKana.put("qun", "チン");
		HashMapPinyinKana.put("ran", "ラン");
		HashMapPinyinKana.put("rang", "ラン");
		HashMapPinyinKana.put("rao", "ラオ");
		HashMapPinyinKana.put("re", "ロー,ルー,ラー");
		HashMapPinyinKana.put("ren", "レン");
		HashMapPinyinKana.put("reng", "ロン");
		HashMapPinyinKana.put("ri", "リー");
		HashMapPinyinKana.put("rong", "ロン");
		HashMapPinyinKana.put("rou", "ロウ");
		HashMapPinyinKana.put("ru", "ルー");
		HashMapPinyinKana.put("rua", "ルワー");
		HashMapPinyinKana.put("ruan", "ルワン");
		HashMapPinyinKana.put("rui", "ルイ");
		HashMapPinyinKana.put("run", "ルン");
		HashMapPinyinKana.put("ruo", "ルオ");
		HashMapPinyinKana.put("sa", "サー");
		HashMapPinyinKana.put("sai", "サイ");
		HashMapPinyinKana.put("san", "サン");
		HashMapPinyinKana.put("sang", "サン");
		HashMapPinyinKana.put("sao", "サオ");
		HashMapPinyinKana.put("se", "ソー,サー,スー");
		HashMapPinyinKana.put("sen", "セン");
		HashMapPinyinKana.put("seng", "ソン");
		HashMapPinyinKana.put("sha", "シャー");
		HashMapPinyinKana.put("shai", "シャイ");
		HashMapPinyinKana.put("shan", "シャン");
		HashMapPinyinKana.put("shang", "シャン");
		HashMapPinyinKana.put("shao", "シャオ");
		HashMapPinyinKana.put("she", "ショー,シャー");
		HashMapPinyinKana.put("shei", "シェイ");
		HashMapPinyinKana.put("shen", "シェン");
		HashMapPinyinKana.put("sheng", "ション");
		HashMapPinyinKana.put("shi", "シー");
		HashMapPinyinKana.put("shou", "ショウ");
		HashMapPinyinKana.put("shu", "シュー");
		HashMapPinyinKana.put("shua", "シュワー");
		HashMapPinyinKana.put("shuai", "シュワイ");
		HashMapPinyinKana.put("shuan", "シュワン");
		HashMapPinyinKana.put("shuang", "シュワン");
		HashMapPinyinKana.put("shui", "シュイ");
		HashMapPinyinKana.put("shun", "シュン");
		HashMapPinyinKana.put("shuo", "シュオ");
		HashMapPinyinKana.put("si", "スー");
		HashMapPinyinKana.put("song", "ソン");
		HashMapPinyinKana.put("sou", "ソウ");
		HashMapPinyinKana.put("su", "スー");
		HashMapPinyinKana.put("suan", "スワン");
		HashMapPinyinKana.put("sui", "スイ");
		HashMapPinyinKana.put("sun", "スン");
		HashMapPinyinKana.put("suo", "スオ");
		HashMapPinyinKana.put("ta", "ター");
		HashMapPinyinKana.put("tai", "タイ");
		HashMapPinyinKana.put("tan", "タン");
		HashMapPinyinKana.put("tang", "タン");
		HashMapPinyinKana.put("tao", "タオ");
		HashMapPinyinKana.put("te", "ター,トー");
		HashMapPinyinKana.put("teng", "トン");
		HashMapPinyinKana.put("ti", "ティー");
		HashMapPinyinKana.put("tian", "ティエン");
		HashMapPinyinKana.put("tiao", "テャオ");
		HashMapPinyinKana.put("tie", "ティエ");
		HashMapPinyinKana.put("ting", "ティン");
		HashMapPinyinKana.put("tong", "トン");
		HashMapPinyinKana.put("tou", "トウ");
		HashMapPinyinKana.put("tu", "トゥー");
		HashMapPinyinKana.put("tuan", "トワン");
		HashMapPinyinKana.put("tui", "トゥイ");
		HashMapPinyinKana.put("tun", "トゥン");
		HashMapPinyinKana.put("tuo", "トゥオ");
		HashMapPinyinKana.put("wa", "ワー");
		HashMapPinyinKana.put("wai", "ワイ");
		HashMapPinyinKana.put("wan", "ワン");
		HashMapPinyinKana.put("wang", "ワン");
		HashMapPinyinKana.put("wei", "ウェイ");
		HashMapPinyinKana.put("wen", "ウェン");
		HashMapPinyinKana.put("weng", "ウォン");
		HashMapPinyinKana.put("wo", "ウオ");
		HashMapPinyinKana.put("wu", "ウー");
		HashMapPinyinKana.put("xi", "シー");
		HashMapPinyinKana.put("xia", "シャー");
		HashMapPinyinKana.put("xian", "シエン");
		HashMapPinyinKana.put("xiang", "シャン");
		HashMapPinyinKana.put("xiao", "シャオ");
		HashMapPinyinKana.put("xie", "シエ");
		HashMapPinyinKana.put("xin", "シン");
		HashMapPinyinKana.put("xing", "シン");
		HashMapPinyinKana.put("xiong", "ション");
		HashMapPinyinKana.put("xiu", "シュー");
		HashMapPinyinKana.put("xu", "シー");
		HashMapPinyinKana.put("xuan", "シュエン");
		HashMapPinyinKana.put("xue", "シュエ");
		HashMapPinyinKana.put("xun", "シン");
		HashMapPinyinKana.put("ya", "ヤー");
		HashMapPinyinKana.put("yan", "イエン");
		HashMapPinyinKana.put("yang", "ヤン");
		HashMapPinyinKana.put("yao", "ヤオ");
		HashMapPinyinKana.put("ye", "イエ");
		HashMapPinyinKana.put("yi", "イー");
		HashMapPinyinKana.put("yin", "イン");
		HashMapPinyinKana.put("ying", "イン");
		HashMapPinyinKana.put("yo", "ヨー");
		HashMapPinyinKana.put("yong", "ヨン");
		HashMapPinyinKana.put("you", "ヨウ");
		HashMapPinyinKana.put("yu", "イー");
		HashMapPinyinKana.put("yuan", "ユエン");
		HashMapPinyinKana.put("yue", "ユエ");
		HashMapPinyinKana.put("yun", "イン");
		HashMapPinyinKana.put("za", "ザー");
		HashMapPinyinKana.put("zai", "ザイ");
		HashMapPinyinKana.put("zan", "ザン");
		HashMapPinyinKana.put("zang", "ザン");
		HashMapPinyinKana.put("zao", "ザオ");
		HashMapPinyinKana.put("ze", "ゾー,ザー,ズー");
		HashMapPinyinKana.put("zei", "ゼイ");
		HashMapPinyinKana.put("zen", "ゼン");
		HashMapPinyinKana.put("zeng", "ゾン");
		HashMapPinyinKana.put("zha", "ジャー");
		HashMapPinyinKana.put("zhai", "ジャイ");
		HashMapPinyinKana.put("zhan", "ジャン");
		HashMapPinyinKana.put("zhang", "ジャン");
		HashMapPinyinKana.put("zhao", "ジャオ");
		HashMapPinyinKana.put("zhe", "ジョー,ジャー");
		HashMapPinyinKana.put("zhei", "ジェイ");
		HashMapPinyinKana.put("zhen", "ジェン");
		HashMapPinyinKana.put("zheng", "ジョン");
		HashMapPinyinKana.put("zhi", "ジー");
		HashMapPinyinKana.put("zhong", "ジョン");
		HashMapPinyinKana.put("zhou", "ジョウ");
		HashMapPinyinKana.put("zhu", "ジュー");
		HashMapPinyinKana.put("zhua", "ジュワー");
		HashMapPinyinKana.put("zhuai", "ジュワイ");
		HashMapPinyinKana.put("zhuan", "ジュワン");
		HashMapPinyinKana.put("zhuang", "ジュワン");
		HashMapPinyinKana.put("zhui", "ジュイ");
		HashMapPinyinKana.put("zhun", "ジュン");
		HashMapPinyinKana.put("zhuo", "ジュオ");
		HashMapPinyinKana.put("zi", "ズー");
		HashMapPinyinKana.put("zong", "ゾン");
		HashMapPinyinKana.put("zou", "ゾウ");
		HashMapPinyinKana.put("zu", "ズー");
		HashMapPinyinKana.put("zuan", "ズワン");
		HashMapPinyinKana.put("zui", "ズイ");
		HashMapPinyinKana.put("zun", "ズン");
		HashMapPinyinKana.put("zuo", "ズオ");

		//英文字符
		HashMapPinyinKana.put("A", "アー");
		HashMapPinyinKana.put("B", "ビー");
		HashMapPinyinKana.put("C", "シー");
		HashMapPinyinKana.put("D", "ディー");
		HashMapPinyinKana.put("E", "イー");
		HashMapPinyinKana.put("F", "エフ");
		HashMapPinyinKana.put("G", "ジー");
		HashMapPinyinKana.put("H", "エーチ");
		HashMapPinyinKana.put("I", "アイ");
		HashMapPinyinKana.put("J", "ジェー");
		HashMapPinyinKana.put("K", "ケー");
		HashMapPinyinKana.put("L", "エル");
		HashMapPinyinKana.put("M", "エム");
		HashMapPinyinKana.put("N", "エン");
		HashMapPinyinKana.put("O", "オー");
		HashMapPinyinKana.put("P", "ピー");
		HashMapPinyinKana.put("Q", "キュー");
		HashMapPinyinKana.put("R", "アー");
		HashMapPinyinKana.put("S", "エス");
		HashMapPinyinKana.put("T", "ティー");
		HashMapPinyinKana.put("U", "ユー");
		HashMapPinyinKana.put("V", "ビー");
		HashMapPinyinKana.put("W", "ダブリュー");
		HashMapPinyinKana.put("X", "エックス");
		HashMapPinyinKana.put("Y", "ワイー");
		HashMapPinyinKana.put("Z", "ゼッド");

		//https://www.sljfaq.org/cgi/e2k_ja.cgi?word=Crazy

	}

	public boolean getLicenseboolean(String pw, String className, User_infoBean user_infoBean) {
		//用户ID确认
		if (StringUtils.isEmpty(user_infoBean.getUser_id())) {
			return false;

		}

		//密码确认
		//		if (StringUtils.isEmpty(pw) == true) {
		//			return false;
		//		}
		//		if (pw.equals(user_infoBean.getPw()) == false) {
		//			return false;
		//		}

		//有效期确认
		String License_yyyymmdd = user_infoBean.getLicense_yyyymmdd();
		if (StringUtils.isEmpty(License_yyyymmdd) == true) {
			return false;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String yyyyMMddNow = simpleDateFormat.format(new Date());

		if (Integer.parseInt(yyyyMMddNow) < Integer.parseInt(License_yyyymmdd)) {
			//何もしない
		} else {
			return false;
		}

		if ("admin".equals(user_infoBean.getUser_id().toLowerCase())) {
			//admin的时候，其他权限确认不要
			return true;

		}
		//		className = className.replaceAll("class com.panda.servlet.", "");
		//		className = className.replaceAll("class com.panda.tools.", "");
		//		className = className.replaceAll("com.panda.servlet.", "");

		String[] classNameList = className.split("\\.");
		className = classNameList[classNameList.length - 1];

		//页面访问权限确认
		if (StringUtils.isEmpty(user_infoBean.getLicense_url())) {
			return false;

		} else if ("ALL".equals(user_infoBean.getLicense_url())) {

		} else if (user_infoBean.getLicense_url().contains(className)) {

		} else {
			return false;

		}

		return true;

	}

	public boolean getLicensebooleanPW(String pw, String className, User_infoBean user_infoBean) {
		//用户ID确认
		if (StringUtils.isEmpty(user_infoBean.getUser_id())) {
			return false;

		}

		//密码确认
//		if (StringUtils.isEmpty(pw) == true) {
//			return false;
//		}
//		if (pw.equals(user_infoBean.getPw()) == false) {
//			return false;
//		}

		//有效期确认
		String License_yyyymmdd = user_infoBean.getLicense_yyyymmdd();
		if (StringUtils.isEmpty(License_yyyymmdd) == true) {
			return false;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String yyyyMMddNow = simpleDateFormat.format(new Date());

		if (Integer.parseInt(yyyyMMddNow) < Integer.parseInt(License_yyyymmdd)) {
			//何もしない
		} else {
			return false;
		}

		if ("admin".equals(user_infoBean.getUser_id().toLowerCase())) {
			//admin的时候，其他权限确认不要
			return true;

		}
		//		className = className.replaceAll("class com.panda.servlet.", "");
		//		className = className.replaceAll("class com.panda.tools.", "");
		//		className = className.replaceAll("com.panda.servlet.", "");

		String[] classNameList = className.split("\\.");
		className = classNameList[classNameList.length - 1];

		//页面访问权限确认
		if (StringUtils.isEmpty(user_infoBean.getLicense_url())) {
			return false;

		} else if ("ALL".equals(user_infoBean.getLicense_url())) {

		} else if (user_infoBean.getLicense_url().contains(className)) {

		} else {
			return false;

		}

		return true;

	}

	/**
	 * 得到文件名称
	 *
	 * @param path 路径
	 * @return {@link List}<{@link String}>
	 */
	public static List<String> getFileNames(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return new ArrayList<String>();
		}
		List<String> fileNames = new ArrayList<String>();
		return getFileNames(file, fileNames);
	}

	/**
	 * 得到文件名称
	 *
	 * @param file      文件
	 * @param fileNames 文件名
	 * @return {@link List}<{@link String}>
	 */
	public static List<String> getFileNames(File file, List<String> fileNames) {
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				//				getFileNames(f, fileNames);
			} else {
				fileNames.add(f.getName());

			}
		}
		return fileNames;
	}

    public static File[] listFilesInFolder(String folderPath) {
        List<File> fileList = new ArrayList<>();
        listFilesRecursive(new File(folderPath), fileList);
        return fileList.toArray(new File[0]);
    }

    private static void listFilesRecursive(File folder, List<File> fileList) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是子文件夹，递归遍历
                    listFilesRecursive(file, fileList);
                } else {
                    // 如果是文件，添加到列表中
                    fileList.add(file);
                }
            }
        }
    }
	public String delHtmlJavascript(String html) {
		if (StringUtils.isEmpty(html) == false) {
			Document doc = Jsoup.parse(html);
			//			doc.getElementsByTag("base").remove();
			//			return doc.html();

			if (doc.getElementById("center") != null) {
				return doc.getElementById("center").html();

			} else {
				return doc.getElementById("document").html();

			}

		}
		return html;
	}

	public static String readTextFromFile(String fileName) throws IOException {
		StringBuffer sb = new StringBuffer();
		//	        BufferedReader br = new BufferedReader(new FileReader(fileName));
		//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "Shift-JIS"));

		String content;
		while ((content = br.readLine()) != null) {
			sb.append(content);
		}
		return sb.toString();
	}

	public static String fn_hanzi(String str) {
		String strNew = "";
		if (StringUtils.isEmpty(str)) {
			return strNew;
		}

		str = str.toUpperCase();
		str = toHalfWidth(str);

		/*
		 * server.xml
		 *     <Connector URIEncoding="UTF-8"/>
		 *
		 *     http://127.0.0.1:8080/PandaServiceMA/KanaLogic?hanzi=SunMoon%E6%A0%AA%E5%BC%8F%E4%BC%9A%E7%A4%BE
		 *     https://www.pandaservicejapan.com/KanaLogic?hanzi=SunMo(on%E6%A0%AA%E5%BC%8F%E4%BC%9A%E7%A4%BE
		 *
		 */
		logger.info(str);
		//					str = new String(str.getBytes("iso-8859-1"), "utf-8");
		//					logger.info(str);
		for (int i = 0; i < str.length(); i++) {
			String value = str.substring(i, i + 1);
			try {
				//				logger.info(value);
				Map<String, String> fydmcPinYinMap = changeChinesePinyin(value);
				//				logger.info(fydmcPinYinMap.get("fullPinyin"));
				//				logger.info(HashMapPinyinKana.get(fydmcPinYinMap.get("fullPinyin")));

				String strTemp = HashMapPinyinKana.get(fydmcPinYinMap.get("fullPinyin"));
				if (StringUtils.isEmpty(strTemp) == true) {
					strTemp = value;
				} else {
					strTemp = strTemp.split(",")[0];
				}
				strNew = strNew + strTemp + " ";
			} catch (Exception e) {
								logger.info(value + " -> skip");
			}
		}
		return strNew;
	}

	public static Map<String, String> changeChinesePinyin(String chinese) throws BadHanyuPinyinOutputFormatCombination {
		Map<String, String> pinyin = new HashMap<String, String>();

		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		StringBuffer fullPinyin = new StringBuffer();
		StringBuffer simplePinyin = new StringBuffer();
		StringBuffer firstPinyin = new StringBuffer();

		char[] chineseChar = chinese.toCharArray();
		for (int i = 0; i < chineseChar.length; i++) {
			String[] str = null;
			try {
				str = PinyinHelper.toHanyuPinyinStringArray(chineseChar[i],
						format);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
			if (str != null) {
				if (str.length == 0) {
					continue;
				}
				fullPinyin = fullPinyin.append(str[0].toString());
				simplePinyin = simplePinyin.append(str[0].charAt(0));

			}
			if (str == null) {
				String regex = "^[0-9]*[a-zA-Z]*+$";
				Pattern pattern = Pattern.compile(regex);
				Matcher m = pattern.matcher(String.valueOf(chineseChar[i]));
				if (m.find()) {
					fullPinyin = fullPinyin.append(chineseChar[i]);
					simplePinyin = simplePinyin.append(chineseChar[i]);
				}
			}
		}
		String[] name = PinyinHelper.toHanyuPinyinStringArray(chineseChar[0], format);
		if (name == null && fullPinyin.substring(0, 1).matches("[a-zA-Z]")) {
			name = new String[] { fullPinyin.substring(0, 1) };
		} else if (name == null) {
			name = new String[] { "#" };
		}
		firstPinyin = firstPinyin.append(name[0].charAt(0));
		pinyin.put("fullPinyin", fullPinyin.toString());
		pinyin.put("simplePinyin", simplePinyin.toString().toUpperCase());
		pinyin.put("groupPinyin", firstPinyin.toString().toUpperCase());
		return pinyin;
	}

	/**
		 * 对字符串处理:将指定位置到指定位置的字符以星号代替
		 *
		 * @param content
		 *            传入的字符串
		 * @param begin
		 *            开始位置
		 * @param end
		 *            结束位置
		 * @return
		 */
	public static String getStarString(String content, int begin, int end) {

		if (begin >= content.length() || begin < 0) {
			return content;
		}
		if (end >= content.length() || end < 0) {
			//			return content;
		}
		if (begin >= end) {
			return content;
		}
		String starStr = "";
		for (int i = begin; i < end; i++) {
			starStr = starStr + "*";
		}
		return content.substring(0, begin) + starStr + content.substring(end, content.length());
	}

	public static BufferedImage createImageFromText(String text, Font font) {
		int padding = 20; // 设置文本与图像边缘的间距

		BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D tempGraphics = tempImage.createGraphics();
		tempGraphics.setFont(font);
		FontMetrics fontMetrics = tempGraphics.getFontMetrics();

		int lineHeight = fontMetrics.getHeight();
		String[] lines = splitTextIntoLines(text, fontMetrics);

		int maxWidth = 0;
		for (String line : lines) {
			int lineWidth = fontMetrics.stringWidth(line);
			maxWidth = Math.max(maxWidth, lineWidth);
		}

		int imageWidth = maxWidth + 2 * padding;
		int imageHeight = lineHeight * lines.length + 2 * padding;

		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = image.createGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, imageWidth, imageHeight);

		graphics.setColor(Color.BLACK);
		graphics.setFont(font);

		int y = padding + fontMetrics.getAscent();

		for (String line : lines) {
			int lineWidth = fontMetrics.stringWidth(line);
			int x = padding; // 左对齐，x坐标从padding开始
			graphics.drawString(line, x, y);
			y += lineHeight;
		}

		graphics.dispose();

		return image;
	}

	private static String[] splitTextIntoLines(String text, FontMetrics fontMetrics) {
		StringBuilder line = new StringBuilder();
		StringBuilder lines = new StringBuilder();

		String[] words = text.split("\\s+");

		for (String word : words) {
			if (fontMetrics.stringWidth(line + " " + word) <= 0) {
				if (line.length() > 0) {
					line.append(" ");
				}
				line.append(word);
			} else {
				lines.append(line).append("\n");
				line = new StringBuilder(word);
			}
		}

		if (line.length() > 0) {
			lines.append(line).append("\n");
		}

		return lines.toString().split("\n");
	}

	/**传入txt路径读取txt文件
	 * @param txtPath
	 * @return 返回读取到的内容
	 */
	public static String readTxt(String txtPath) {
		File file = new File(txtPath);
		if (file.isFile() && file.exists()) {
			try {

				FileInputStream inputStream = new FileInputStream(file);
				byte[] b = new byte[inputStream.available()];
				inputStream.read(b);
				inputStream.close();
				return new String(b, "UTF-8");

				//                FileInputStream fileInputStream = new FileInputStream(file);
				//                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				//
				//                StringBuffer sb = new StringBuffer();
				//                String text = null;
				//                while((text = bufferedReader.readLine()) != null){
				//                    sb.append(text);
				//                }
				//
				//                bufferedReader.close();
				//            	inputStreamReader.close();
				//            	fileInputStream.close();
				//
				//                return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String readTxt(String txtPath, String encoding) {
		File file = new File(txtPath);
		if (file.isFile() && file.exists()) {
			try {

				FileInputStream inputStream = new FileInputStream(file);
				byte[] b = new byte[inputStream.available()];
				inputStream.read(b);
				inputStream.close();
				return new String(b, "UTF-8");

				//                FileInputStream fileInputStream = new FileInputStream(file);
				//                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
				//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				//
				//                StringBuffer sb = new StringBuffer();
				//                String text = null;
				//                while((text = bufferedReader.readLine()) != null){
				//                    sb.append(text);
				//                }
				//
				//                bufferedReader.close();
				//            	inputStreamReader.close();
				//            	fileInputStream.close();
				//
				//                return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**使用FileOutputStream来写入txt文件
	 * @param txtPath txt文件路径
	 * @param content 需要写入的文本
	 */
	public static void writeTxt(String txtPath, String content) {
		FileOutputStream fileOutputStream = null;
		File file = new File(txtPath);
		try {
			if (file.exists()) {
				//判断文件是否存在，如果不存在就新建一个txt
				file.createNewFile();
			}

			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(content.getBytes("UTF-8"));
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeTxt(String txtPath, String content, String encoding) {
		FileOutputStream fileOutputStream = null;
		File file = new File(txtPath);
		try {
			if (file.exists()) {
				//判断文件是否存在，如果不存在就新建一个txt
				file.createNewFile();
			}

			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(content.getBytes(encoding));
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String cut(String text, int size) {
	    if (text == null) return "";
	    return text.length() <= size ? text : text.substring(0, size);
	}

	// 半角から全角への変換
	public static String toFullWidthAndTruncate(String input, int size) {
		if (input == null || input.isEmpty() || size <= 0) {
			return ""; // もし空の文字列または無効なサイズが指定された場合、空の文字列を返す
		}

		String result = toFullWidth(input);
		if (result.length() > size) {
			result = result.substring(0, size);
		}
		return result.toString();
	}

	// 半角から全角への変換
	public static String toFullWidth(String input) {
		if (input == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder(input.length());

		for (char c : input.toCharArray()) {
			if (c >= '!' && c <= '~') {
				// 半角文字の範囲を全角に変換
				sb.append((char) (c - '!' + '！'));
			} else {
				// その他の文字はそのまま追加
				sb.append(c);
			}
		}

		return sb.toString().replaceAll(" ", "　");
	}

	//	    // 半角から全角への変換
	//	    public static String toFullWidthNG(String value) {
	//			String result = "";
	//			if (value != null) {
	//				StringBuilder sb = new StringBuilder(value);
	//				for (int i = 0; i < sb.length(); i++) {
	//					int c = (int) sb.charAt(i);
	//					if ((c >= 0x41 && c <= 0x5A) || (c >= 0x61 && c <= 0x7A)) {
	//						sb.setCharAt(i, (char) (c + 0xFEE0));
	//					}
	//				}
	//				result = sb.toString();
	//			}
	//			return result;
	//		}

	//这个方法有BUG
	//アー＀ビー＀シー＀ディー＀イー＀エフ＀ジー＀エーチ
	//	    public static String toFullWidth(String input) {
	//	        if (input == null) {
	//	            return null;
	//	        }
	//
	//	        StringBuilder sb = new StringBuilder();
	//
	//	        for (int i = 0; i < input.length(); i++) {
	//	            char c = input.charAt(i);
	//
	//	            // 半角文字の範囲内の場合、対応する全角文字に変換
	//	            if (c >= ' ' && c <= '~') {
	//	                sb.append((char) (c + 0xfee0));
	//	            } else {
	//	                // 半角文字でない場合はそのまま追加
	//	                sb.append(c);
	//	            }
	//	        }
	//
	//	        return sb.toString();
	//	    }

	   // 将文件夹及其内容添加到ZIP文件中
	public static void addToZipFile(String rootPath, String sourceFolderPath, ZipOutputStream zipOut)
			throws IOException {
		File sourceFolder = new File(sourceFolderPath);
		File[] files = sourceFolder.listFiles();

		for (File file : files) {
			if (file.isDirectory()) {
				// 如果是文件夹，递归调用addToZipFile
				addToZipFile(rootPath, file.getAbsolutePath(), zipOut);
			} else {
				// 如果是文件，将文件添加到ZIP文件中
				String relativePath = file.getAbsolutePath().substring(rootPath.length() + 1);
				ZipEntry zipEntry = new ZipEntry(relativePath);
				zipOut.putNextEntry(zipEntry);

				FileInputStream fis = new FileInputStream(file);
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = fis.read(buffer)) != -1) {
					zipOut.write(buffer, 0, bytesRead);
				}

				fis.close();
				zipOut.closeEntry();
			}
		}
	}








	 public static void zipDirectory(String sourceFolderPath, String zipFilePath) throws IOException {
	        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
	             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
	            File sourceFolder = new File(sourceFolderPath);
	            addFolderToZip(sourceFolder, sourceFolder.getAbsolutePath(), zipOut);
	        }
	    }

	    private static void addFolderToZip(File folder, String rootFolderPath, ZipOutputStream zipOut) throws IOException {
	        for (File file : folder.listFiles()) {
	            if (file.isDirectory()) {
	                String folderPath = file.getAbsolutePath().substring(rootFolderPath.length() + 1);
	                zipOut.putNextEntry(new ZipEntry(folderPath + "/"));
	                zipOut.closeEntry();
	                addFolderToZip(file, rootFolderPath, zipOut);
	            } else {
	                addFileToZip(file, rootFolderPath, zipOut);
	            }
	        }
	    }

	    private static void addFileToZip(File file, String rootFolderPath, ZipOutputStream zipOut) throws IOException {
	        try (FileInputStream fis = new FileInputStream(file)) {
	            String entryName = file.getAbsolutePath().substring(rootFolderPath.length() + 1);
	            zipOut.putNextEntry(new ZipEntry(entryName));
	            byte[] buffer = new byte[1024];
	            int length;
	            while ((length = fis.read(buffer)) > 0) {
	                zipOut.write(buffer, 0, length);
	            }
	            zipOut.closeEntry();
	        }
	    }



	public static void readeExcel(String file, String newFile, HashMap<String, String> hashMapKeyValueNew) {
		logger.info("readeExcel start");
		try {

			readExcelText(file, newFile, hashMapKeyValueNew);

//			deleteDocxFirstText(newFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("readeExcel end");
	}
	private static void readExcelText(String file, String newFile, HashMap<String, String> hashMapKeyValueNew) {

		 FileInputStream fis = null;
	        FileOutputStream fos = null;
	        Workbook workbook = null;

	        try {
	            fis = new FileInputStream(file);
	            workbook = WorkbookFactory.create(fis);

	            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
	                Sheet sheet = workbook.getSheetAt(sheetIndex);

	                for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	                    Row row = sheet.getRow(rowIndex);
	                    if (row != null) {
	                        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
	                            Cell cell = row.getCell(cellIndex);
	                            if (cell != null && cell.getCellType() == CellType.STRING) {
	                                String cellValue = cell.getStringCellValue();

	                                for (String key : hashMapKeyValueNew.keySet()) {
	                                    if (cellValue.contains(key)) {
	                                        String value = hashMapKeyValueNew.get(key);
		                                	if(value == null) {
		                                		value = "";
		                                	}
	                                        cellValue = cellValue.replace(key, value);
	                                        cell.setCellValue(cellValue);
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }

	            fos = new FileOutputStream(newFile);
	            workbook.write(fos);
	        } catch (Throwable e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (workbook != null) {
//	                    workbook.close();
	                }
	                if (fis != null) {
	                    fis.close();
	                }
	                if (fos != null) {
	                    fos.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }


	}

	public static void readWord(String file, String newFile, HashMap<String, String> hashMapKeyValueNew) {
		logger.info("readWord start");
		try {

			logger.info("file " + file);
			readWordText2(file, newFile, hashMapKeyValueNew);

			deleteDocxFirstText(newFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("readWord end");
	}

	private static void readWordText2(String file, String newFile, HashMap<String, String> hashMapKeyValueNew) {

		//加载文档
		com.spire.doc.Document doc = new com.spire.doc.Document();
		doc.loadFromFile(file);



		for (String key : hashMapKeyValueNew.keySet()) {
			if (StringUtils.isEmpty(key) == false) {
				String value = hashMapKeyValueNew.get(key).replaceAll("\n", " ");

				//要替换第一个出现的指定文本，只需在替换前调用setReplaceFirst方法来指定只替换第一个出现的指定文本
				//doc.setReplaceFirst(true);

				//调用方法用新文本替换原文本内容
				doc.replace(key, value, false, true);
			}

		}



		//TODO

		String path = FuncUtils.projectPath + "ETAX_moban/消费税税号注册信息.csv";
		HashMap<String, String> hashMapKeyValueTemp = readCsvToMap(path);



//		hashMapKeyValueTemp.put("重庆棠睫科技有限公司", "まだ開始していない");
//		hashMapKeyValueTemp.put("能量起源（南京）科技有限公司", "2024/03/23");
//		hashMapKeyValueTemp.put("能量起源（合肥）科技有限公司", "まだ開始していない");
//		hashMapKeyValueTemp.put("深圳市可可莉商贸有限责任公司", "まだ開始していない");
//		hashMapKeyValueTemp.put("深圳市德得龙商贸有限责任公司", "まだ開始していない");
//		hashMapKeyValueTemp.put("惠州市奥派香氛家居有限公司", "まだ開始していない");
//		hashMapKeyValueTemp.put("义乌市界雅贸易有限公司", "まだ開始していない");
//		hashMapKeyValueTemp.put("深圳市华能智加科技有限公司", "2025/04/30");
//		hashMapKeyValueTemp.put("佛山市娅荟办公用品有限公司", "2019/11/14");
//
//		hashMapKeyValueTemp.put("施得樂斯有限公司", "2025/05/31");
//		hashMapKeyValueTemp.put("苏州有山屹诚电子商务有限公司", "2025/04/01");
//		hashMapKeyValueTemp.put("贵州丝路优品电子商务有限公司", "2025/04/07");
//		hashMapKeyValueTemp.put("重庆阿刻提斯生命科学研究有限公司", "まだ開始していません");
//
//		hashMapKeyValueTemp.put("深圳华马世实业有限公司", "2015/03/30");
//		hashMapKeyValueTemp.put("衡阳耀慕商贸有限公司", "まだ開始していません");
//
//		hashMapKeyValueTemp.put("南昌市无疆电子商务有限公司", "2025/04/30");
//		hashMapKeyValueTemp.put("北京大智若愚管理咨询有限公司", "まだ開始していません");
//		hashMapKeyValueTemp.put("杭州萌仔电子商务有限公司", "2025/05/01");
//		hashMapKeyValueTemp.put("丽水隐山网络科技有限公司", "まだ開始していません");
//		hashMapKeyValueTemp.put("宁波竹特网络科技有限公司", "2023/02/13");
//		hashMapKeyValueTemp.put("深圳市骏晨电子商务有限责任公司", "2025/04/30");


		logger.debug("#国内において事業を開始した年月日#");
		String CompanyName_Chinese = hashMapKeyValueNew.get("#会社名自国語全部#");
		if (hashMapKeyValueTemp.containsKey(CompanyName_Chinese)) {
			doc.replace("#国内において事業を開始した年月日#", hashMapKeyValueTemp.get(CompanyName_Chinese), false, true);
			logger.debug(CompanyName_Chinese);
			logger.debug(hashMapKeyValueTemp.get(CompanyName_Chinese));

		} else {
			doc.replace("#国内において事業を開始した年月日#", "", false, true);

		}



		//保存文档
		doc.saveToFile(newFile, FileFormat.Docx_2013);
		doc.dispose();

	}

	   public static HashMap<String, String> readCsvToMap(String filePath) {
	        HashMap<String, String> hashMapKeyValueTemp = new HashMap<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                // 假设 CSV 用逗号分隔
	                String[] parts = line.split(",", -1);
	                if (parts.length >= 2) {
	                    String key = parts[0].trim();
	                    String value = parts[1].trim();
	                    hashMapKeyValueTemp.put(key, value);
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return hashMapKeyValueTemp;
	    }

	//		private static void readWordText3(String file, String newFile, HashMap<String, String> hashMapKeyValueNew) {
	//
	//			//加载文档
	//			com.spire.doc.Document doc = new com.spire.doc.Document();
	//			doc.loadFromFile(file);
	//
	//	        // ドキュメントにテキストを追加
	//	        Section section = doc.addSection();
	//	        Paragraph paragraph = section.addParagraph();
	//	        TextRange textRange = paragraph.appendText("これはフォントを設定するテキストです。");
	//
	//	        // フォント情報を設定
	//	        CharacterFormat charFormat = textRange.getCharacterFormat();
	//	        charFormat.setFontName("Arial"); // フォント名を設定
	//
	//
	//			for (String key : hashMapKeyValueNew.keySet()) {
	//				if (StringUtils.isEmpty(key) == false) {
	//					String value = hashMapKeyValueNew.get(key).replaceAll("\n", " ");
	//
	//					//要替换第一个出现的指定文本，只需在替换前调用setReplaceFirst方法来指定只替换第一个出现的指定文本
	//					//doc.setReplaceFirst(true);
	//
	//					//调用方法用新文本替换原文本内容
	//					doc.replace(key, value, false, true);
	//				}
	//
	//			}
	//
	//			//保存文档
	//			doc.saveToFile(newFile, FileFormat.Docx_2013);
	//			doc.dispose();
	//
	//		}

	/**
	  *	删除Word.docx第一行
	  * @param path 需要删除首行警示语的doc路径
	  */
	public static void deleteDocxFirstText(String path) {
		try {
			FileInputStream inputStream = new FileInputStream(path);
			XWPFDocument document = new XWPFDocument(inputStream);
			inputStream.close();
			XWPFParagraph toDelete = document.getParagraphs().stream()
					.filter(p -> StringUtils.equalsIgnoreCase(
							"Evaluation Warning: The document was created with Spire.Doc for JAVA.",
							p.getParagraphText()))
					.findFirst().orElse(null);
			if (toDelete != null) {
				document.removeBodyElement(document.getPosOfParagraph(toDelete));
				OutputStream fos = new FileOutputStream(path);
				document.write(fos);
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void copyZipFiles(String sourceFolderPath, String targetFolderPath) {
		try {
			// 创建目标文件夹（如果不存在）
			File targetFolder = new File(targetFolderPath);
			if (!targetFolder.exists()) {
				targetFolder.mkdirs();
			}

			// 使用Stream过滤并复制ZIP文件
			Stream<Path> zipFiles = Files.list(Paths.get(sourceFolderPath))
					.filter(path -> path.toString().endsWith(".zip"));

			zipFiles.forEach(zipFile -> {
				try {
					String fileName = zipFile.getFileName().toString();
					Path targetFilePath = Paths.get(targetFolderPath, fileName);
					Files.copy(zipFile, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
					logger.info("复制文件：" + fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			logger.info("ZIP文件复制完成到：" + targetFolderPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteFolder(File folder) {
		if (folder.exists()) {
			File[] files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory()) {
						// 如果是子文件夹，递归删除
						deleteFolder(file);
					} else {
						// 如果是文件，删除文件
						if (file.delete()) {
							logger.info("已删除文件：" + file.getAbsolutePath());
						} else {
							System.err.println("无法删除文件：" + file.getAbsolutePath());
						}
					}
				}
			}

			// 删除空文件夹
			if (folder.delete()) {
				logger.info("已删除文件夹：" + folder.getAbsolutePath());
			} else {
				System.err.println("无法删除文件夹：" + folder.getAbsolutePath());
			}
		}
	}

	public static boolean deleteFile(String filePath) {
		File fileToDelete = new File(filePath);

		// 检查文件是否存在
		if (fileToDelete.exists()) {
			// 尝试删除文件
			boolean isDeleted = fileToDelete.delete();
			return isDeleted;
		} else {
			// 文件不存在
			return false;
		}
	}

	public static String convertToEra(int number) {
		String era = "";
		switch (number) {
		case 1:
			era = "明治";
			break;
		case 2:
			era = "大正";
			break;
		case 3:
			era = "昭和";
			break;
		case 4:
			era = "平成";
			break;
		case 5:
			era = "令和";
			break;
		default:
			era = "未知";
			break;
		}
		return era;
	}

	// 会社の何期目を計算するメソッド（年単位）
	public static int calculateFiscalYear(String establishmentDate, String currentDate) throws ParseException {
		// 設立日から現在日までの経過年数を計算
		int establishmentYear = Integer.parseInt(establishmentDate.substring(0, 4));
		int currentYear = Integer.parseInt(currentDate.substring(0, 4));
		int elapsedYears = currentYear - establishmentYear;

		return elapsedYears + 1; // 経過年数に1を加えて事業年度を計算
	}

	public static void copyFolder(String sourceFolderPath, String destinationFolderPath) throws IOException {

		File sourceFolder = new File(sourceFolderPath); // 替换为实际的源文件夹路径
		File destinationFolder = new File(destinationFolderPath); // 替换为实际的目标文件夹路径

		// 检查源文件夹是否存在
		if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
			logger.info("源文件夹不存在或不是文件夹。");
			return;
		}

		// 如果目标文件夹不存在，创建它
		if (!destinationFolder.exists()) {
			destinationFolder.mkdirs();
		}

		// 获取源文件夹中的所有文件和子文件夹
		File[] files = sourceFolder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					// 如果是子文件夹，递归调用复制文件夹方法
					File newDestinationFolder = new File(destinationFolder, file.getName());
					copyFolder(file.toPath().toString(), newDestinationFolder.toPath().toString());
				} else {
					// 如果是文件，复制文件到目标文件夹
					File destinationFile = new File(destinationFolder, file.getName());
					Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	public static void sendMail_activation_code(t_etax_account_infoBean EtaxAccountInfoBean) {

//		String title = "【" + EtaxAccountInfoBean.getYyyymmdd_count() + "】【"
//				+ EtaxAccountInfoBean.getCompanyName_Chinese() + "】您已成功激活"
//				+ "";
		String title = "【"
				+ EtaxAccountInfoBean.getCompanyName_Chinese() + "】您已成功激活"
				+ "";
		String mailarea = EtaxAccountInfoBean.getEmail();

		String textboxdata = ""
				+ "您好。"
				+ "<br>"
				+ "<br>您已成功激活，敝司将为您申请日本消费税税号。"
				+ "<br>如有其他问题，请咨询客服，并告知管理ID，谢谢您的配合。"
				+ "<br>****************"
				+ "<br>★[4]公司或本人姓名（本国语言） : " + EtaxAccountInfoBean.getCompanyName_Chinese() + ""
				+ "<br>****************"
				+ "";

		SendMail SendMail = new SendMail();
		SendMail.sendMessage("", mailarea, null, title, textboxdata);
//		mailarea = "info@pandaservicejapan.com";
//		SendMail.sendMessage("", mailarea, title, textboxdata);


	}

	public static void sendMail_ai(t_etax_account_infoBean t_etax_account_infoBean, String yyyymmdd_count, String user_id, String status, TableDefinition def) {
		String CompanyName_Chinese = "";
		String yewu_leixing = "";
		StringBuilder sb = new StringBuilder();

		int i = 0;
		for (ColumnDefinition col : def.columns) {
		    sb.append("<br>★[D"+(++i)+"]")
		      .append(col.comment != null ? col.comment : col.name) // 优先用字段说明，没有就用字段名
		      .append(" : ")
		      .append(col.value != null ? col.value : "")
		      ;

		    if (col.comment.contains("【业务类型】")) {
		    	yewu_leixing = col.value;
		    }
		    if (col.comment.contains("【公司名称或个体户本人姓名（所在地区文字）】")) {
		    	CompanyName_Chinese = col.value;
		    }
		}

//		String title = "【" + EtaxAccountInfoBean.getYyyymmdd_count() + "】【"
//				+ EtaxAccountInfoBean.getCompanyName_Chinese() + "】您已成功激活"
//				+ "";
		String title = ""
				+ "【" + CompanyName_Chinese + "】您好，【" + yewu_leixing + "】信息已收到"
				+ "";
		String mailarea = t_etax_account_infoBean.getEmail();

		String textboxdata = "";
		String URL = "https://www.pandaservicejapan.com";
		String URL1 = "https://www.japanetax.com";


		// 获取系统类型属性
		String osName = System.getProperty("os.name");
		// 您可以根据不同的系统类型执行不同的操作
		if (osName.toLowerCase().contains("windows")) {
			logger.info("这是Windows系统");
			// 在Windows系统上执行特定操作
			URL = "http://127.0.0.1:8080/PandaServiceMA";

		} else if (osName.toLowerCase().contains("linux")) {
			logger.info("这是Linux系统");
			// 在Linux系统上执行特定操作
		}

		i = 0;
		if ("jiben_qingbao0".equals(status)) {
			 textboxdata = ""
					+ "您好。"
					+ "<br>"
					+ "<br>您已成功提交基本信息，業務種類<" + def.tableName_comment + ">"
					+ "<br>系统没有匹配到您的基本信息，请填写基本信息"
					+ "<br>" + URL + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=jiben_qingbao"
					+ "<br>" + URL1 + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=jiben_qingbao"
					+ "<br>****************基本信息****************"
					+ "<br>★[J" + (++i) + "]公司或本人姓名（本国语言） : " + t_etax_account_infoBean.getCompanyName_Chinese()
					+ "<br>****************************************"
					+ "";

		} else if ("jiben_qingbao".equals(status)) {
			 textboxdata = ""
						+ "您好。"
						+ "<br>"
						+ "<br>您已成功提交基本信息，業務種類<" + def.tableName_comment + ">"
						+ "<br>请填写业务信息"
						+ "<br>" + URL + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=yewu_qingbao"
						+ "<br>" + URL1 + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=yewu_qingbao"
//						+ "<br>****************基本信息****************"
//						+ "<br>★[J" + (++i) + "]消费税税号 : " + t_etax_account_infoBean.getJCT_NO()
//						+ "<br>★[J" + (++i) + "]用户类型 : " + t_etax_account_infoBean.getUser_type()
//						+ "<br>★[J" + (++i) + "]公司或本人姓名（本国语言） : " + t_etax_account_infoBean.getCompanyName_Chinese()
//						+ "<br>★[J" + (++i) + "]公司或本人姓名（英文） : " + t_etax_account_infoBean.getCompanyName_English()
//						+ "<br>★[J" + (++i) + "]公司或本人姓名（日语片假名） : " + t_etax_account_infoBean.getCompanyName_pianjiaming()
//						+ "<br>★[J" + (++i) + "]公司或本人地址（本国语言） : " + t_etax_account_infoBean.getAddress_Chinese()
//						+ "<br>★[J" + (++i) + "]公司或本人地址（英文） : " + t_etax_account_infoBean.getAddress_English()
//						+ "<br>★[J" + (++i) + "]公司或本人地址（日语片假名）: " + t_etax_account_infoBean.getAddress_pianjiaming()
//						+ "<br>★[J" + (++i) + "]公司代表人或经营场所名称（本国语言） : " + t_etax_account_infoBean.getDaibiaoName_Chinese()
//						+ "<br>★[J" + (++i) + "]公司代表人或经营场所名称（英文） : " + t_etax_account_infoBean.getDaibiaoName_English()
//						+ "<br>★[J" + (++i) + "]公司代表人或经营场所名称（日语片假名） : " + t_etax_account_infoBean.getDaibiaoName_pianjiaming()
//						+ "<br>★[J" + (++i) + "]公司代表人或经营场所地址（本国语言） : " + t_etax_account_infoBean.getDaibiaoName_address_Chinese()
//						+ "<br>★[J" + (++i) + "]公司代表人或经营场所地址（英文） : " + t_etax_account_infoBean.getDaibiaoName_address_English()
//						+ "<br>★[J" + (++i) + "]公司代表人或经营场所地址（日语片假名） : " + t_etax_account_infoBean.getDaibiaoName_address_pianjiaming()
//						+ "<br>★[J" + (++i) + "]电话（国家码 中国86） : " + t_etax_account_infoBean.getTel_country()
//						+ "<br>★[J" + (++i) + "]电话（第一段） : " + t_etax_account_infoBean.getTel_1()
//						+ "<br>★[J" + (++i) + "]电话（第二段） : " + t_etax_account_infoBean.getTel_2()
//						+ "<br>★[J" + (++i) + "]电话（第三段） : " + t_etax_account_infoBean.getTel_3()
//						+ "<br>★[J" + (++i) + "] 注册资本（请换算为日元） : " + t_etax_account_infoBean.getZhice_ziben()
//						+ "<br>★[J" + (++i) + "] 公司成立年 : " + t_etax_account_infoBean.getCompany_YYYY()
//						+ "<br>★[J" + (++i) + "] 公司成立月 : " + t_etax_account_infoBean.getCompany_MM()
//						+ "<br>★[J" + (++i) + "] 公司成立日 : " + t_etax_account_infoBean.getCompany_DD()
//						+ "<br>****************************************"
						+ "";


		} else if ("yewu_qingbao".equals(status)) {
			 textboxdata = ""
						+ "您好。"
						+ "<br>"
						+ "<br>您已成功提交业务信息，業務種類<" + def.tableName_comment + ">"
						+ "<br>后台会尽快确认，并发邮件给您，请耐心等待，谢谢。"
						+ "<br>****************基本信息****************"
						+ "<br>★公司或本人姓名（本国语言） : " + t_etax_account_infoBean.getCompanyName_Chinese() + ""
						+ "<br>****************業務信息****************"
						+ sb.toString()
						+ "<br>****************************************"
						+ "";


		} else if ("houtai_queren_ok".equals(status)) {
			 textboxdata = ""
						+ "您好。"
						+ "<br>"
						+ "<br>您提交的信息，后台审核通过，業務種類<" + def.tableName_comment + ">"
						+ "<br>请上传确认书。"
						+ "<br>" + URL + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=queren_shu"
								+ "<br>" + URL1 + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=queren_shu"

						+ "<br>****************基本信息****************"
						+ "<br>★公司或本人姓名（本国语言） : " + t_etax_account_infoBean.getCompanyName_Chinese() + ""
						+ "<br>****************業務信息****************"
						+ sb.toString()
						+ "<br>****************************************"
						+ "";

		} else if ("houtai_queren_ng".equals(status)) {
			 textboxdata = ""
						+ "您好。"
						+ "<br>"
						+ "<br>您提交的信息，后台审核没有通过，業務種類<" + def.tableName_comment + ">"
						+ "<br>请重新填写业务信息，谢谢您的配合。"
						+ "<br>" + URL + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=yewu_qingbao"
								+ "<br>" + URL1 + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=yewu_qingbao"

						+ "<br>****************基本信息****************"
						+ "<br>★公司或本人姓名（本国语言） : " + t_etax_account_infoBean.getCompanyName_Chinese() + ""
						+ "<br>****************業務信息****************"
						+ sb.toString()
						+ "<br>****************************************"
						+ "";

		} else if ("houtai_queren_zuizhong".equals(status)) {
			 textboxdata = ""
						+ "您好。"
						+ "<br>"
						+ "<br>您提交的信息，后台最终审核通过，業務種類<" + def.tableName_comment + ">"
						+ "<br>****************基本信息****************"
						+ "<br>★[J" + (++i) + "]消费税税号 : " + t_etax_account_infoBean.getJCT_NO()
						+ "<br>★[J" + (++i) + "]用户类型 : " + t_etax_account_infoBean.getUser_type()
						+ "<br>★[J" + (++i) + "]公司或本人姓名（本国语言） : " + t_etax_account_infoBean.getCompanyName_Chinese()
						+ "<br>★[J" + (++i) + "]公司或本人姓名（英文） : " + t_etax_account_infoBean.getCompanyName_English()
						+ "<br>★[J" + (++i) + "]公司或本人姓名（日语片假名） : " + t_etax_account_infoBean.getCompanyName_pianjiaming()
						+ "<br>★[J" + (++i) + "]公司或本人地址（本国语言） : " + t_etax_account_infoBean.getAddress_Chinese()
						+ "<br>★[J" + (++i) + "]公司或本人地址（英文） : " + t_etax_account_infoBean.getAddress_English()
						+ "<br>★[J" + (++i) + "]公司或本人地址（日语片假名）: " + t_etax_account_infoBean.getAddress_pianjiaming()
						+ "<br>★[J" + (++i) + "]公司代表人或经营场所名称（本国语言） : " + t_etax_account_infoBean.getDaibiaoName_Chinese()
						+ "<br>★[J" + (++i) + "]公司代表人或经营场所名称（英文） : " + t_etax_account_infoBean.getDaibiaoName_English()
						+ "<br>★[J" + (++i) + "]公司代表人或经营场所名称（日语片假名） : " + t_etax_account_infoBean.getDaibiaoName_pianjiaming()
						+ "<br>★[J" + (++i) + "]公司代表人或经营场所地址（本国语言） : " + t_etax_account_infoBean.getDaibiaoName_address_Chinese()
						+ "<br>★[J" + (++i) + "]公司代表人或经营场所地址（英文） : " + t_etax_account_infoBean.getDaibiaoName_address_English()
						+ "<br>★[J" + (++i) + "]公司代表人或经营场所地址（日语片假名） : " + t_etax_account_infoBean.getDaibiaoName_address_pianjiaming()
						+ "<br>★[J" + (++i) + "]电话（国家码 中国86） : " + t_etax_account_infoBean.getTel_country()
						+ "<br>★[J" + (++i) + "]电话（第一段） : " + t_etax_account_infoBean.getTel_1()
						+ "<br>★[J" + (++i) + "]电话（第二段） : " + t_etax_account_infoBean.getTel_2()
						+ "<br>★[J" + (++i) + "]电话（第三段） : " + t_etax_account_infoBean.getTel_3()
						+ "<br>★[J" + (++i) + "] 注册资本（请换算为日元） : " + t_etax_account_infoBean.getZhice_ziben()
						+ "<br>★[J" + (++i) + "] 公司成立年 : " + t_etax_account_infoBean.getCompany_YYYY()
						+ "<br>★[J" + (++i) + "] 公司成立月 : " + t_etax_account_infoBean.getCompany_MM()
						+ "<br>★[J" + (++i) + "] 公司成立日 : " + t_etax_account_infoBean.getCompany_DD()
						+ "<br>****************業務信息****************"
						+ sb.toString()
						+ "<br>****************************************"
						+ "";

		}


		String result = "";
		for (ColumnDefinition col : def.columns) {
        	if (col.name != null && col.name.startsWith("col_name")) {

        	} else {
        		continue;

        	}
			if (col.comment.indexOf("#不显示》客户#") >-1 || col.comment.indexOf("#不显示》业务情报登录#") >-1
					|| (!col.comment.contains(yewu_leixing) && !col.comment.contains("#主表段#") && !col.comment.contains("#邮箱段#"))) {
				continue;
			}

		    result = result + "<br>★[C" + col.name.replace("col_name_", "") + "]" + col.comment.split("#,#")[1] + " : " + (col.value != null ? col.value : "");
		}

		 if ("houtai_queren_ok".equals(status)) {
				textboxdata = ""
						+ title
						+ "<br>"
						+ "<br>请您打印该封邮件后签字盖章后通过以下的URL上传扫描文件。"
						+ "<br>" + URL + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=queren_shu"
						+ "<br>" + URL1 + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=queren_shu"
						+ "<br>****************详细信息****************"
						+ result
						+ "<br>****************************************"
						+ "";
		} else {

			textboxdata = ""
					+ title
					+ "<br>"
					+ "<br>我们的工作人员之后会与您联系。"
					+ "<br>" + URL + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=yewu_qingbao"
					+ "<br>" + URL1 + "/AiSetYewuShujuLogic?activation_code=" + t_etax_account_infoBean.getActivation_code() + "&table_name=" + def.tableName + "&status=yewu_qingbao"
					+ "<br>****************详细信息****************"
					+ result
					+ "<br>****************************************"
					+ "";
		}

		textboxdata = textboxdata + ""
				+ "<br>如有其他问题，请咨询客服，并告知业务种类<" + yewu_leixing + ">和咨询ID<" + t_etax_account_infoBean.getActivation_code().substring(0, 6) + ">，谢谢您的配合。"
				+ "";

		SendMail SendMail = new SendMail();
		SendMail.sendMessage("", mailarea, null, title, textboxdata);
//		mailarea = "info@pandaservicejapan.com";
//		SendMail.sendMessage("", mailarea, title, textboxdata);


	}

	public static void sendMail_denglu(t_etax_account_infoBean EtaxAccountInfoBean, String attachmentFolderPath,
			String xiaoshouerYYYY_2_title,
			String xiaoshouerYYYY_1_half_title, String tokutei_kikann_siharai_kyuuyo_title,
			String xiaoshouerYYYY_1_YYYYMMDD_title, String shuoming) {

		String title = "【" + EtaxAccountInfoBean.getYyyymmdd_count() + "】【"
				+ EtaxAccountInfoBean.getCompanyName_Chinese() + "】请确认您的日本消费税税号注册信息"
				+ "";

		String mailarea = EtaxAccountInfoBean.getEmail();
		String ccEmail = "";

		int i = 0;
		String textboxdata = ""
				+ "<br>您好。"
				+ "<br>"
				+ "<br>您填写的关于注册日本消费税税号的信息如下。"
				+ "<br>如果没有问题，请点击下方的【激活链接】，谢谢您的配合。"
				+ "<br>****************"
				//					+ "<br>利用者識別番号（没有或不知道或不知道这是什么请保持空栏） : " + EtaxAccountInfoBean.getBangou() + ""
				//					+ "<br>邀请码 : " + EtaxAccountInfoBean.getYaoqing_no()
				+ "<br>["+ (++i) +"] 用户类型 : " + EtaxAccountInfoBean.getUser_type()
				+ "<br>["+ (++i) +"] 公司名（中文） : " + EtaxAccountInfoBean.getCompanyName_Chinese()
				+ "<br>["+ (++i) +"] 公司名（英文） : " + EtaxAccountInfoBean.getCompanyName_English()
				+ "<br>["+ (++i) +"] 公司地址（中文） : " + EtaxAccountInfoBean.getAddress_Chinese()
				+ "<br>["+ (++i) +"] 公司地址（英文） : " + EtaxAccountInfoBean.getAddress_English()
				+ "<br>["+ (++i) +"] 代表人姓名（中文） : " + EtaxAccountInfoBean.getDaibiaoName_Chinese()
				+ "<br>["+ (++i) +"] 代表人姓名（英文） : " + EtaxAccountInfoBean.getDaibiaoName_English()
				+ "<br>["+ (++i) +"] 电话（国家码 中国86） : " + EtaxAccountInfoBean.getTel_country()
				+ "<br>["+ (++i) +"] 电话（第一段） : " + EtaxAccountInfoBean.getTel_1()
				+ "<br>["+ (++i) +"] 电话（第二段） : " + EtaxAccountInfoBean.getTel_2()
				+ "<br>["+ (++i) +"] 电话（第三段） : " + EtaxAccountInfoBean.getTel_3()
				+ "<br>["+ (++i) +"] 注册资本（请换算为日元） : " + EtaxAccountInfoBean.getZhice_ziben()
				+ "<br>["+ (++i) +"] 公司成立年 : " + EtaxAccountInfoBean.getCompany_YYYY()
				+ "<br>["+ (++i) +"] 公司成立月 : " + EtaxAccountInfoBean.getCompany_MM()
				+ "<br>["+ (++i) +"] 公司成立日 : " + EtaxAccountInfoBean.getCompany_DD()
				+ "<br>" + trimWhitespaceAndTabs(xiaoshouerYYYY_2_title) + " : "
				+ EtaxAccountInfoBean.getXiaoshouerYYYY_2() + ""
				+ "<br>" + trimWhitespaceAndTabs(xiaoshouerYYYY_1_half_title) + " : "
				+ EtaxAccountInfoBean.getXiaoshouerYYYY_1_half() + ""
				+ "<br>" + trimWhitespaceAndTabs(tokutei_kikann_siharai_kyuuyo_title) + " : "
				+ EtaxAccountInfoBean.getTokutei_kikann_siharai_kyuuyo() + ""
				+ "<br>" + trimWhitespaceAndTabs(xiaoshouerYYYY_1_YYYYMMDD_title) + " : "
				+ EtaxAccountInfoBean.getXiaoshouerYYYY_1() + ""
				+ "<br>["+ (++i) +"] 在日本有无常设机构（指分公司、子公司、办事处等机构，如有，请通过该机构申请JCT） : "
				+ EtaxAccountInfoBean.getChangshe_jigou_Select() + ""
				+ "<br>["+ (++i) +"] 作为免税事业者您想从下一个会计年度的首日开始登录吗 : " + EtaxAccountInfoBean.getShouri_kaishi_denglu_xiayige() + ""
				+ "<br>["+ (++i) +"] 作为新设立的公司，您想从本年度的首日开始登录吗 :" + EtaxAccountInfoBean.getShouri_kaishi_denglu_ben() + ""
				+ "<br>" + shuoming.replaceAll("\n", "<br>")
				+ "<br>["+ (++i) +"] 是否申请简易课税 : " + EtaxAccountInfoBean.getJianyi_keshui_Select() + ""
				//					+ "<br>电子邮箱 : " + EtaxAccountInfoBean.getEmail() + ""
				+ "<br>"
				+ "<br>我是公司的代表人或合法的代理人，为申请日本消费税税号一事承担法律责任。我已知晓上述内容并保证填写的信息和上传的文件真实有效。我现授权日本的合规的税理士代理我司申请日本消费税税号。"
				+ "<br>****************"
				+ "<br>激活链接"
				//					+ "<br>https://www.pandaservicejapan.com/SetUserInfoLogic?activation_code=" + EtaxAccountInfoBean.getActivation_code()
				+ "<br>https://www.pandaservicejapan.com/SetUserInfoLogic?activation_code="
				+ EtaxAccountInfoBean.getActivation_code()
				+ "";

		SendMail SendMail = new SendMail();
		//有附件 to 客户
		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata.replaceAll("<br>", "\n"), attachmentFolderPath);
		//无附件
//		mailarea = "info@pandaservicejapan.com";
//		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata);
	}


	public static void sendMail_jct_shengao(t_etax_account_infoBean EtaxAccountInfoBean, t_jct_shenqingBean t_jct_shenqingBean, String attachmentFolderPath,
			String xiaoshouerYYYY_2_title,
			String xiaoshouerYYYY_1_half_title, String tokutei_kikann_siharai_kyuuyo_title,
			String xiaoshouerYYYY_1_YYYYMMDD_title, String shuoming) {

		String title = "【" + EtaxAccountInfoBean.getYyyymmdd_count() + "】【"
				+ EtaxAccountInfoBean.getCompanyName_Chinese() + "】请确认您的日本消费税税号注册信息V2.0"
				+ "";

		String mailarea = EtaxAccountInfoBean.getEmail();
		String ccEmail = "";

		int i = 0+3;
		String textboxdata = ""
				+ "<br>您好。"
				+ "<br>"
				+ "<br>您填写的关于注册日本消费税税号的信息如下。"
				+ "<br>如果没有问题，请点击下方的【激活链接】，谢谢您的配合。"
				+ "<br>****************"
				//					+ "<br>利用者識別番号（没有或不知道或不知道这是什么请保持空栏） : " + EtaxAccountInfoBean.getBangou() + ""
				//					+ "<br>邀请码 : " + EtaxAccountInfoBean.getYaoqing_no()
				+ "<br>★[3]用户类型 : " + EtaxAccountInfoBean.getUser_type()
				+ "<br>★[4]公司或本人姓名（本国语言） : " + EtaxAccountInfoBean.getCompanyName_Chinese()
				+ "<br>★[5]公司或本人姓名（英文） : " + EtaxAccountInfoBean.getCompanyName_English()
				+ "<br>★[6]公司或本人姓名（日语片假名） : " + EtaxAccountInfoBean.getCompanyName_pianjiaming()
				+ "<br>★[7]公司或本人地址（本国语言） : " + EtaxAccountInfoBean.getAddress_Chinese()
				+ "<br>★[8]公司或本人地址（英文） : " + EtaxAccountInfoBean.getAddress_English()
				+ "<br>★[9]公司或本人地址（日语片假名）: " + EtaxAccountInfoBean.getAddress_pianjiaming()
				+ "<br>★[10]公司代表人或经营场所名称（本国语言） : " + EtaxAccountInfoBean.getDaibiaoName_Chinese()
				+ "<br>★[11]公司代表人或经营场所名称（英文） : " + EtaxAccountInfoBean.getDaibiaoName_English()
				+ "<br>★[12]公司代表人或经营场所名称（日语片假名） : " + EtaxAccountInfoBean.getDaibiaoName_pianjiaming()
				+ "<br>★[13]公司代表人或经营场所地址（本国语言） : " + EtaxAccountInfoBean.getDaibiaoName_address_Chinese()
				+ "<br>★[14]公司代表人或经营场所地址（英文） : " + EtaxAccountInfoBean.getDaibiaoName_address_English()
				+ "<br>★[15]公司代表人或经营场所地址（日语片假名） : " + EtaxAccountInfoBean.getDaibiaoName_address_pianjiaming()
				+ "<br>★[16]电话（国家码 中国86） : " + EtaxAccountInfoBean.getTel_country()
				+ "<br>★[17]电话（第一段） : " + EtaxAccountInfoBean.getTel_1()
				+ "<br>★[18]电话（第二段） : " + EtaxAccountInfoBean.getTel_2()
				+ "<br>★[19]电话（第三段） : " + EtaxAccountInfoBean.getTel_3()
				+ "<br>★[20] 注册资本（请换算为日元） : " + EtaxAccountInfoBean.getZhice_ziben()
				+ "<br>★[21] 公司成立年 : " + EtaxAccountInfoBean.getCompany_YYYY()
				+ "<br>★[22] 公司成立月 : " + EtaxAccountInfoBean.getCompany_MM()
				+ "<br>★[23] 公司成立日 : " + EtaxAccountInfoBean.getCompany_DD()
				+ "<br>★[24] 在日本开始事业的年 : " + t_jct_shenqingBean.getRiben_kaishi_shiye_YYYY()
				+ "<br>★[25] 在日本开始事业的月 : " + t_jct_shenqingBean.getRiben_kaishi_shiye_MM()
				+ "<br>★[26] 在日本开始事业的日 : " + t_jct_shenqingBean.getRiben_kaishi_shiye_DD()
				+ "<br>★[27] 基准期间【该主体无基准期间】在日本的课税销售额（含税日元金额） : " + t_jct_shenqingBean.getXiaoshouerYYYY_2()
				+ "<br>★[28] 特定期间【该主体无特定期间】在日本的课税销售额（含税日元金额） : " + t_jct_shenqingBean.getXiaoshouerYYYY_1_half()
				+ "<br>★[29]确认事项"
				+ "<br> A 我确认本申请主体在日本没有常设机构。我已知晓：如果有常设机构要通过常设机构申请消费税税号；常设机构有义务向日本政府申告所得税；没有常设机构的外国主体在申告消费税时不能使用简易课税制度和2割特例。"
				+ "<br> B 我确认本申请主体没有在日本因违反消费税法被处罚金"
				+ "<br> C 我确认本申请主体没有在日本滞纳税金"
				+ "<br> D 我确认本申请主体没有被取消过消费税税号"
				+ "<br> E 我确认本申请主体不是【特定新规设立法人】（https://www.nta.go.jp/taxes/shiraberu/taxanswer/shohi/6503.htm）"
				+ "<br> F 我确认本申请主体没有向日本税务机关主动递交过其他消费税文件"
				+ "<br> G 我确认我是本申请主体的代表人（本人）或合法的代理人，为申请日本消费税税号一事承担法律责任。我保证填写的信息和上传的文件真实有效。我现授权日本的合规的税理士代理我司申请日本消费税税号。"
				+ "<br>★[30] 该主体此刻是课税的还是免税的 : " + t_jct_shenqingBean.getKeshui_or_mianshui()
				+ "<br>★[31] 该主体是第一年在日本开始事业吗: " + t_jct_shenqingBean.getYYYY_1()
				+ "<br>★[32] 该主体应该递交课税事业者文件吗 : " + t_jct_shenqingBean.getKeshui_shiyezhe_wenshu()
				+ "<br>****************"
				+ "<br>激活链接"
				//					+ "<br>https://www.pandaservicejapan.com/SetUserInfoLogic?activation_code=" + EtaxAccountInfoBean.getActivation_code()
				+ "<br>https://www.pandaservicejapan.com/SetUserInfoLogic2?activation_code="
				+ EtaxAccountInfoBean.getActivation_code()
				+ "";

		SendMail SendMail = new SendMail();
		//有附件 to 客户
		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata.replaceAll("<br>", "\n"), attachmentFolderPath);
		//无附件
//		mailarea = "info@pandaservicejapan.com";
//		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata);
	}


	public static void sendMail_shengao(t_etax_account_infoExBean t_etax_account_infoExBean, t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean
			, String attachmentFolderPath, User_infoBean user_infoBean_groupAdmin) {

		String title = ""
				+ "【" + t_etax_account_infoExBean.getCompanyName_Chinese() + "】请确认您的日本消费税申告确认书"
				+ "";

		String mailarea = t_etax_account_infoExBean.getEmail();
		//默认cc groupAdmin
		String ccEmail = user_infoBean_groupAdmin.getEmail();//t_xiaofeishui_shengaoBean.getForm_mailarea();

		int yyyy = Integer.parseInt(t_xiaofeishui_shengaoBean.getShengao_qijian_from().substring(0, 4));
		int yyyy_2 = yyyy-2;
		int yyyy_1 = yyyy-1;

		   NumberFormat numberFormat = NumberFormat.getInstance();


		int i = 0;
		String textboxdata = ""
				+ "<br>您好。"
				+ "<br>"
				+ "<br>您填写的关于日本消费税申告确认书的信息如下。"
				+ "<br>如果没有问题，请点击下方的【激活链接】，谢谢您的配合。"
				+ "<br>****************"
				//					+ "<br>利用者識別番号（没有或不知道或不知道这是什么请保持空栏） : " + EtaxAccountInfoBean.getBangou() + ""
				//					+ "<br>邀请码 : " + EtaxAccountInfoBean.getYaoqing_no()


				+ "<br>["+ (++i) +"] 消费税税号 : " + t_etax_account_infoExBean.getInvoiceBangou()
				+ "<br>["+ (++i) +"] 申告主体类别 : " + t_etax_account_infoExBean.getUser_type()

				+ "<br>["+ (++i) +"] 申告主体名称（英文） : " + t_etax_account_infoExBean.getCompanyName_English()
				+ "<br>["+ (++i) +"] 申告主体名称（主体所在地区文字） : " + t_etax_account_infoExBean.getCompanyName_Chinese()
				+ "<br>["+ (++i) +"] 申告主体地址（英文） : " + t_etax_account_infoExBean.getAddress_English()
				+ "<br>["+ (++i) +"] 申告主体地址（主体所在地区文字） : " + t_etax_account_infoExBean.getAddress_Chinese()
				+ "<br>["+ (++i) +"] 公司代表人姓名（英文） : " + t_etax_account_infoExBean.getDaibiaoName_English()
				+ "<br>["+ (++i) +"] 公司代表人姓名（主体所在地区文字） : " + t_etax_account_infoExBean.getDaibiaoName_Chinese()

				+ "<br>["+ (++i) +"] 公司成立年月日/个人生日 : "
				+ t_etax_account_infoExBean.getCompany_YYYY() + "年"
				+ t_etax_account_infoExBean.getCompany_MM() + "月"
				+ t_etax_account_infoExBean.getCompany_DD() + "日"

				+ "<br>["+ (++i) +"] 会计年度 : " + t_xiaofeishui_shengaoBean.getShengao_qijian_from()
				+ "-" + t_xiaofeishui_shengaoBean.getShengao_qijian_to()
				+ "<br>["+ (++i) +"] 本申告主体在基准期间的日本课税销售额(" + yyyy_2 + "年1月1日～" + yyyy_2 + "年12月31日) : "
				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getJizhun_qijian())) + "日元"
				+ "<br>["+ (++i) +"] 本申告主体在特定期间的日本课税销售额(" + yyyy_1 + "年1月1日～" + yyyy_1 + "年6月30日) : "
				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getTeding_qijian())) + "日元"
				+ "<br>["+ (++i) +"] 本申告主体在上一会计年度的日本课税销售额(" + yyyy_1 + "年1月1日～" + yyyy_1 + "年12月31日) : "
				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getShangyi_niandu())) + "日元"
				+ "<br>["+ (++i) +"] 本申告主体在该会计年度计算消费税时采用 : " + t_xiaofeishui_shengaoBean.getKeshui_type()
				+ "<br>["+ (++i) +"] 去年是否申告过消费税	 : "
				+ t_xiaofeishui_shengaoBean.getQunian_xiaofeishui_shengao() + " "
				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getQunian_xiaofeishui_guoshui())) + "日元"

				+ "<br>["+ (++i) +"] 本申告主体在该会计年度计算消费税时采用 : " + t_xiaofeishui_shengaoBean.getKeshui_type()

				+ "<br>["+ (++i) +"] 含税总销售额	 : "
				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getHanshui_zongxiaoshoue())) + "日元"
				+ "<br>["+ (++i) +"] 适格请求书总支出额	 : "
				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getShige_qingqiushu_zongzhichue())) + "日元"
				+ "<br>["+ (++i) +"] 非适格请求书总支出额	 : "
				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getFei_shige_qingqiushu_zongzhichue())) + "日元"
				+ "<br>["+ (++i) +"] 进口消费税国税部分总额 : "
				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getJinkou_xiaofeishui_guoshui_zonge())) + "日元"

//				+ "<br>["+ (++i) +"] 用户类型 : " + t_etax_account_infoExBean.getUser_type()
//				+ "<br>["+ (++i) +"] 公司名（中文） : " + t_etax_account_infoExBean.getCompanyName_Chinese()
//				+ "<br>["+ (++i) +"] 公司名（英文） : " + t_etax_account_infoExBean.getCompanyName_English()
//				+ "<br>["+ (++i) +"] 公司地址（中文） : " + t_etax_account_infoExBean.getAddress_Chinese()
//				+ "<br>["+ (++i) +"] 公司地址（英文） : " + t_etax_account_infoExBean.getAddress_English()
//				+ "<br>["+ (++i) +"] 代表人姓名（中文） : " + t_etax_account_infoExBean.getDaibiaoName_Chinese()
//				+ "<br>["+ (++i) +"] 代表人姓名（英文） : " + t_etax_account_infoExBean.getDaibiaoName_English()
//				+ "<br>["+ (++i) +"] 电话（国家码 中国86） : " + t_etax_account_infoExBean.getTel_country()
//				+ "<br>["+ (++i) +"] 电话（第一段） : " + t_etax_account_infoExBean.getTel_1()
//				+ "<br>["+ (++i) +"] 电话（第二段） : " + t_etax_account_infoExBean.getTel_2()
//				+ "<br>["+ (++i) +"] 电话（第三段） : " + t_etax_account_infoExBean.getTel_3()
//				+ "<br>["+ (++i) +"] 注册资本（请换算为日元） : " + t_etax_account_infoExBean.getZhice_ziben()
//				+ "<br>["+ (++i) +"] 公司成立年 : " + t_etax_account_infoExBean.getCompany_YYYY()
//				+ "<br>["+ (++i) +"] 公司成立月 : " + t_etax_account_infoExBean.getCompany_MM()
//				+ "<br>["+ (++i) +"] 公司成立日 : " + t_etax_account_infoExBean.getCompany_DD()
//				+ "<br>" + trimWhitespaceAndTabs(xiaoshouerYYYY_2_title) + " : "
//				+ t_etax_account_infoExBean.getXiaoshouerYYYY_2() + ""
//				+ "<br>" + trimWhitespaceAndTabs(xiaoshouerYYYY_1_half_title) + " : "
//				+ t_etax_account_infoExBean.getXiaoshouerYYYY_1_half() + ""
//				+ "<br>" + trimWhitespaceAndTabs(tokutei_kikann_siharai_kyuuyo_title) + " : "
//				+ t_etax_account_infoExBean.getTokutei_kikann_siharai_kyuuyo() + ""
//				+ "<br>" + trimWhitespaceAndTabs(xiaoshouerYYYY_1_YYYYMMDD_title) + " : "
//				+ t_etax_account_infoExBean.getXiaoshouerYYYY_1() + ""
//				+ "<br>["+ (++i) +"] 在日本有无常设机构（指分公司、子公司、办事处等机构，如有，请通过该机构申请JCT） : "
//				+ t_etax_account_infoExBean.getChangshe_jigou_Select() + ""
//				+ "<br>["+ (++i) +"] 作为免税事业者您想从下一个会计年度的首日开始登录吗 : " + t_etax_account_infoExBean.getShouri_kaishi_denglu_xiayige() + ""
//				+ "<br>["+ (++i) +"] 作为新设立的公司，您想从本年度的首日开始登录吗 :" + t_etax_account_infoExBean.getShouri_kaishi_denglu_ben() + ""
//				+ "<br>" + shuoming.replaceAll("\n", "<br>")
//				+ "<br>["+ (++i) +"] 是否申请简易课税 : " + t_etax_account_infoExBean.getJianyi_keshui_Select() + ""




				//					+ "<br>电子邮箱 : " + EtaxAccountInfoBean.getEmail() + ""
				+ "<br>"
				+ "<br>我已经阅读了日本国税厅公布的关于消费税的相关说明并保证遵守相关规定。我的所有凭证真实、有效，无篡改。我根据实际经营情况和凭证记录账本。我将按照规定保存凭证和账本。我委托日本纳税代理人根据我提供的上述材料进行日本消费税申告。如我违反承诺或主观故意提供虚假材料，我愿负相应的法律责任并承担一切后果。"
				+ "<br>****************"
				+ "<br>激活链接"
//				+ "<br>https://www.pandaservicejapan.com/SetXiaofeishuiShengaoCHengnuoshuOpenLogic?activation_code="
				+ "<br>https://www.pandaservicejapan.com/SetXiaofeishuiShengaoCHengnuoshuOpenLogic?activation_code="
				+ t_xiaofeishui_shengaoBean.getActivation_code()
				+ "";

		SendMail SendMail = new SendMail();
		//有附件 to 客户
//		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata.replaceAll("<br>", "\n"), attachmentFolderPath);
		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata.replaceAll("<br>", "\n"));
		//无附件
//		mailarea = "info@pandaservicejapan.com";
//		SendMail.sendMessage("", mailarea, null, title, textboxdata);
	}

	public static void sendMail_shengao_chengren(t_etax_account_infoExBean t_etax_account_infoExBean, t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean
			, String attachmentFolderPath) {

		String title = ""
				+ "【" + t_etax_account_infoExBean.getCompanyName_Chinese() + "】成功激活日本消费税申告"
				+ "";

		String mailarea = t_etax_account_infoExBean.getEmail();
		String ccEmail = "";//t_xiaofeishui_shengaoBean.getForm_mailarea();

		int yyyy = Integer.parseInt(t_xiaofeishui_shengaoBean.getShengao_qijian_from().substring(0, 4));
		int yyyy_2 = yyyy-2;
		int yyyy_1 = yyyy-1;

		NumberFormat numberFormat = NumberFormat.getInstance();


		int i = 0;
		String textboxdata = ""
				+ "<br>您好。"
				+ "<br>"
				+ "<br>您的激活信息如下。"
				+ "<br>****************"
				//					+ "<br>利用者識別番号（没有或不知道或不知道这是什么请保持空栏） : " + EtaxAccountInfoBean.getBangou() + ""
				//					+ "<br>邀请码 : " + EtaxAccountInfoBean.getYaoqing_no()


				+ "<br>["+ (++i) +"] 消费税税号 : " + t_etax_account_infoExBean.getInvoiceBangou()
				+ "<br>["+ (++i) +"] 申告主体类别 : " + t_etax_account_infoExBean.getUser_type()

				+ "<br>["+ (++i) +"] 申告主体名称（英文） : " + t_etax_account_infoExBean.getCompanyName_English()
				+ "<br>["+ (++i) +"] 申告主体名称（主体所在地区文字） : " + t_etax_account_infoExBean.getCompanyName_Chinese()
				+ "<br>["+ (++i) +"] 申告主体地址（英文） : " + t_etax_account_infoExBean.getAddress_English()
				+ "<br>["+ (++i) +"] 申告主体地址（主体所在地区文字） : " + t_etax_account_infoExBean.getAddress_Chinese()
				+ "<br>["+ (++i) +"] 公司代表人姓名（英文） : " + t_etax_account_infoExBean.getDaibiaoName_English()
				+ "<br>["+ (++i) +"] 公司代表人姓名（主体所在地区文字） : " + t_etax_account_infoExBean.getDaibiaoName_Chinese()

//				+ "<br>["+ (++i) +"] 公司成立年月日/个人生日 : "
//				+ t_etax_account_infoExBean.getCompany_YYYY() + "年"
//				+ t_etax_account_infoExBean.getCompany_MM() + "月"
//				+ t_etax_account_infoExBean.getCompany_DD() + "日"


//				+ "<br>["+ (++i) +"] 会计年度 : " + t_xiaofeishui_shengaoBean.getShengao_qijian_from()
//				+ "-" + t_xiaofeishui_shengaoBean.getShengao_qijian_to()
//				+ "<br>["+ (++i) +"] 本申告主体在基准期间的日本课税销售额(" + yyyy_2 + "年1月1日～" + yyyy_2 + "年12月31日) : "
//				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getJizhun_qijian())) + "日元"
//				+ "<br>["+ (++i) +"] 本申告主体在特定期间的日本课税销售额(" + yyyy_1 + "年1月1日～" + yyyy_1 + "年6月30日) : "
//				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getTeding_qijian())) + "日元"
//				+ "<br>["+ (++i) +"] 本申告主体在上一会计年度的日本课税销售额(" + yyyy_1 + "年1月1日～" + yyyy_1 + "年12月31日) : "
//				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getShangyi_niandu())) + "日元"
//				+ "<br>["+ (++i) +"] 本申告主体在该会计年度计算消费税时采用 : " + t_xiaofeishui_shengaoBean.getKeshui_type()
//				+ "<br>["+ (++i) +"] 去年是否申告过消费税	 : "
//				+ t_xiaofeishui_shengaoBean.getQunian_xiaofeishui_shengao() + " "
//				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getQunian_xiaofeishui_guoshui())) + "日元"
//
//				+ "<br>["+ (++i) +"] 本申告主体在该会计年度计算消费税时采用 : " + t_xiaofeishui_shengaoBean.getKeshui_type()
//
//				+ "<br>["+ (++i) +"] 含税总销售额	 : "
//				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getHanshui_zongxiaoshoue())) + "日元"
//				+ "<br>["+ (++i) +"] 适格请求书总支出额	 : "
//				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getShige_qingqiushu_zongzhichue())) + "日元"
//				+ "<br>["+ (++i) +"] 非适格请求书总支出额	 : "
//				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getFei_shige_qingqiushu_zongzhichue())) + "日元"
//				+ "<br>["+ (++i) +"] 进口消费税国税部分总额 : "
//				+ numberFormat.format(Long.parseLong(t_xiaofeishui_shengaoBean.getJinkou_xiaofeishui_guoshui_zonge())) + "日元"



				//					+ "<br>电子邮箱 : " + EtaxAccountInfoBean.getEmail() + ""
				+ "<br>"
				+ "<br>" + t_xiaofeishui_shengaoBean.getDiv_chengren()
				+ "<br>****************"
//				+ "<br>激活链接"
////				+ "<br>https://www.pandaservicejapan.com/SetXiaofeishuiShengaoCHengnuoshuOpenLogic?activation_code="
//				+ "<br>https://www.pandaservicejapan.com/SetXiaofeishuiShengaoCHengnuoshuOpenLogic?activation_code="
//				+ t_xiaofeishui_shengaoBean.getActivation_code()
				+ "";

		SendMail SendMail = new SendMail();
		//有附件 to 客户
		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata.replaceAll("<br>", "\n"), attachmentFolderPath);
		//无附件
//		mailarea = "info@pandaservicejapan.com";
//		SendMail.sendMessage("", mailarea, null, title, textboxdata);
	}

	public static void sendMail_Amazoncsvzhangben(String mailarea, String attachmentFolderPath) {

		String title = "AmazonCsv转换【账本A收入】结果，请确认";

		String ccEmail = "";//t_xiaofeishui_shengaoBean.getForm_mailarea();

		String textboxdata = ""
				+ "<br>您好。"
				+ "<br>"
				+ "<br>您的AmazonCsv转换【账本A收入】结果如下。请您确认附件。"
				+ "";

		SendMail SendMail = new SendMail();
		//有附件 to 客户
		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata.replaceAll("<br>", "\n"), attachmentFolderPath);
		//无附件
//		mailarea = "info@pandaservicejapan.com";
//		SendMail.sendMessage("", mailarea, null, title, textboxdata);
	}

	  // 添加主方法作为程序入口
    public static void main(String[] args) {


		String str = "蒋国儿";
		FuncUtils FuncUtils = new FuncUtils();

//    	logger.info(FuncUtils.fn_hanzi(str.substring(0, 1)));
//    	logger.info(FuncUtils.fn_hanzi(str.substring(1)));



//        if (args.length < 4) {
//        	logger.error("Usage: java com.panda.utils.FuncUtils <mailarea> <ccEmail> <title> <content>");
//            return;
//        }
//
//        String mailarea = args[0];
//        String ccEmail = args[1];
//        String title = args[2];
//        String content = args[3];
//
//        try {
//            sendMail_incident(mailarea, ccEmail, title, content);
//            logger.info("Mail sent successfully.");
//        } catch (Exception e) {
//            logger.error("Error sending mail", e);
//        }




        if (args.length < 1) {
            printUsage();
            return;
        }

        String command = args[0];

        try {
            switch (command) {


                case "sendMailIncident":
                    if (args.length < 5) {
                        System.out.println("参数不足: sendMailIncident");
                        printUsage();
                        return;
                    }
                    sendMail_incident(args[1], args[2], args[3], args[4]);
                    break;

                default:
                    System.out.println("未知命令: " + command);
                    printUsage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }




	public static void sendMail_incident(String mailarea, String ccEmail, String title, String content) {

		title = "【PS异常】" + title;

//		String ccEmail = "";//t_xiaofeishui_shengaoBean.getForm_mailarea();

		String textboxdata = ""
				+ "<br>" + content
				+ "";

		SendMail SendMail = new SendMail();
		//无附件
		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata.replaceAll("<br>", "\n"));
	}


	public static void sendMail_digital_certificate(t_etax_account_infoExBean t_etax_account_infoExBean) {

		String title = "【" + t_etax_account_infoExBean.getYyyymmdd_count() + "】【"
				+ t_etax_account_infoExBean.getCompanyName_Chinese() + "】请更新您的日本电子证明书[有效期限]" + t_etax_account_infoExBean.getDigital_certificate()
				+ "";

		String mailarea = t_etax_account_infoExBean.getEmail();
		String ccEmail = "info@pandaservicejapan.com";

		int i = 0;
		String textboxdata = ""
				+ "<br>您好。"
				+ "<br>"
				+ "<br>您的日本电子证明书即将到期，请您及时更新，以免影响您的使用。"
				+ "<br>您的日本电子证明书的信息如下。"
				+ "<br>****************"
				+ "<br>["+ (++i) +"] 日本电子证明书有效期限 : " + t_etax_account_infoExBean.getDigital_certificate()
				+ "<br>["+ (++i) +"] 用户类型 : " + t_etax_account_infoExBean.getUser_type()
				+ "<br>["+ (++i) +"] 公司名（中文） : " + t_etax_account_infoExBean.getCompanyName_Chinese()
				+ "<br>["+ (++i) +"] 公司名（英文） : " + t_etax_account_infoExBean.getCompanyName_English()
				+ "<br>["+ (++i) +"] 公司地址（中文） : " + t_etax_account_infoExBean.getAddress_Chinese()
				+ "<br>["+ (++i) +"] 公司地址（英文） : " + t_etax_account_infoExBean.getAddress_English()
				+ "<br>["+ (++i) +"] 代表人姓名（中文） : " + t_etax_account_infoExBean.getDaibiaoName_Chinese()
				+ "<br>["+ (++i) +"] 代表人姓名（英文） : " + t_etax_account_infoExBean.getDaibiaoName_English()
				+ "<br>["+ (++i) +"] 电话（国家码 中国86） : " + t_etax_account_infoExBean.getTel_country()
				+ "<br>["+ (++i) +"] 电话（第一段） : " + t_etax_account_infoExBean.getTel_1()
				+ "<br>["+ (++i) +"] 电话（第二段） : " + t_etax_account_infoExBean.getTel_2()
				+ "<br>["+ (++i) +"] 电话（第三段） : " + t_etax_account_infoExBean.getTel_3()
				+ "<br>["+ (++i) +"] 注册资本（请换算为日元） : " + t_etax_account_infoExBean.getZhice_ziben()
				+ "<br>["+ (++i) +"] 公司成立年 : " + t_etax_account_infoExBean.getCompany_YYYY()
				+ "<br>["+ (++i) +"] 公司成立月 : " + t_etax_account_infoExBean.getCompany_MM()
				+ "<br>["+ (++i) +"] 公司成立日 : " + t_etax_account_infoExBean.getCompany_DD()
				+ "<br>****************"
				+ "";

		SendMail SendMail = new SendMail();
//		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata, null);
		SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata);
	}

	public static void sendMail_METI(String to, String subject, String body) throws Exception {



		String title = subject;

		String mailarea = to;
		String bccEmail = "lixiweb@yahoo.co.jp";

		int i = 0;
		String textboxdata = ""
//				+ "<br>您好。"
//				+ "<br>"
//				+ "<br>您的日本电子证明书即将到期，请您及时更新，以免影响您的使用。"
//				+ "<br>您的日本电子证明书的信息如下。"
//				+ "<br>****************"
				+ "";

		textboxdata = body;

		SendMailMETI SendMailMETI = new SendMailMETI();
		SendMailMETI.sendMessage("", mailarea, bccEmail, title, textboxdata);
	}


	public static void sendMail_denglu_xiaofeishui(t_etax_account_infoBean t_etax_account_infoBean,t_etax_account_xiaofeishuiBean t_etax_account_xiaofeishuiBean, String attachmentFolderPath, String xiaoshouerYYYY_2_title,
				String xiaoshouerYYYY_1_half_title, String tokutei_kikann_siharai_kyuuyo_title,
				String xiaoshouerYYYY_1_YYYYMMDD_title, String shuoming) {


			String title = "【"+t_etax_account_infoBean.getYyyymmdd_count()+"】【"+t_etax_account_infoBean.getCompanyName_Chinese()+"】请确认您的日本消费税申告注册信息"
					+ "";

			String mailarea = t_etax_account_infoBean.getEmail();
			String ccEmail = "";

			int i = 0;
			String textboxdata = ""
					+ "<br>您好。"
					+ "<br>"
					+ "<br>您填写的关于注册日本消费税申告的信息如下。"
					+ "<br>如果没有问题，请点击下方的【激活链接】，谢谢您的配合。"
					+ "<br>****************";
//			textboxdata = textboxdata + "<br>["+ (++i) +"] 用户类型 : <p style=\"color: red;\">" + t_etax_account_infoBean.getUser_type() + "</p>";

			textboxdata = textboxdata + "<br>["+ (++i) +"] 用户类型 : " + t_etax_account_infoBean.getUser_type();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 消费税税号 : " + t_etax_account_infoBean.getXiaofeishui_shuihao();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司名（中文） 填写要点：请与营业执照内容完全一致 : " + t_etax_account_infoBean.getCompanyName_Chinese();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司名（英文）填写要点：本项为公示项目，写法由各公司自行决定。日本消费税法不要求公司的英文名称和网店后台一致。 : " + t_etax_account_infoBean.getCompanyName_English();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司名（日语片假名）填写要点：未填写的场合，由AI自动翻译。 : " + t_etax_account_infoBean.getCompanyName_pianjiaming();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司地址（中文）填写要点：请与营业执照内容完全一致 : " + t_etax_account_infoBean.getAddress_Chinese();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司地址（英文）填写要点：本项为公示项目，翻译方法由各公司自行决定，内容请与中文版保持统一，不可缺漏增改。 : " + t_etax_account_infoBean.getAddress_English();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司地址（日语片假名）填写要点：未填写的场合，由AI自动翻译。 : " + t_etax_account_infoBean.getAddress_pianjiaming();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 代表人姓名（中文）填写要点：请与营业执照内容完全一致 : " + t_etax_account_infoBean.getDaibiaoName_Chinese();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 代表人姓名（英文） : " + t_etax_account_infoBean.getDaibiaoName_English();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 代表人姓名（日语片假名）填写要点：未填写的场合，由AI自动翻译。 : " + t_etax_account_infoBean.getDaibiaoName_pianjiaming();

			textboxdata = textboxdata + "<br>电话 ：　["+ (++i) +"] （国家码 中国86），["+ (++i) +"] （第一段），["+ (++i) +"] （第二段），["+ (++i) +"] （第三段）";
			i = i-4;
			textboxdata = textboxdata + "<br>["+ (++i) +"]  : " + t_etax_account_infoBean.getTel_country();
			textboxdata = textboxdata + "<br>["+ (++i) +"]  : " + t_etax_account_infoBean.getTel_1();
			textboxdata = textboxdata + "<br>["+ (++i) +"]  : " + t_etax_account_infoBean.getTel_2();
			textboxdata = textboxdata + "<br>["+ (++i) +"]  : " + t_etax_account_infoBean.getTel_3();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 注册资本（请换算为日元） : " + t_etax_account_infoBean.getZhice_ziben();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司成立年 : " + t_etax_account_infoBean.getCompany_YYYY();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司成立月 : " + t_etax_account_infoBean.getCompany_MM();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 公司成立日 : " + t_etax_account_infoBean.getCompany_DD();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 纳税地地址邮编（第一段） : " + t_etax_account_infoBean.getNashuidi_youbian1();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 纳税地地址邮编（第二段） : " + t_etax_account_infoBean.getNashuidi_youbian2();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 纳税地地址（日语） : " + t_etax_account_infoBean.getKsaTodofuken()+ t_etax_account_infoBean.getNashuidi();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 管辖税务署 : " + t_etax_account_infoBean.getGuanxia_shuiwushu();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 纳税地电话（第一段） : " + t_etax_account_infoBean.getNashuidi_tel1();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 纳税地电话（第二段） : " + t_etax_account_infoBean.getNashuidi_tel2();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 纳税地电话（第三段） : " + t_etax_account_infoBean.getNashuidi_tel3();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 利用者識別番号 : " + t_etax_account_infoBean.getLiyongzhe_shibie_fanhao();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 申告时期首公元年月日（例：20230102） : " + t_etax_account_xiaofeishuiBean.getShengao_shiqishou_YYYYMMDD();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 申告时期末公元年月日（例：20230102） : " + t_etax_account_xiaofeishuiBean.getShengao_shiqimo_YYYYMMDD();
			textboxdata = textboxdata + "<br>["+ (++i) +"] 原则课税还是简易课税 : " + t_etax_account_xiaofeishuiBean.getYuanze_or_jianyi();



			textboxdata = textboxdata + ""
					+ "<br>"
					+ "<br>我是公司的代表人或合法的代理人，为申请日本消费税税号一事承担法律责任。我已知晓上述内容并保证填写的信息和上传的文件真实有效。我现授权日本的合规的税理士代理我司申请日本消费税税号。"
					+ "<br>****************"
					+ "<br>激活链接"
//					+ "<br>https://www.pandaservicejapan.com/SetUserInfoLogic?activation_code=" + EtaxAccountInfoBean.getActivation_code()
					+ "<br>https://www.pandaservicejapan.com/SetUserInfoLogic?activation_code=" + t_etax_account_infoBean.getActivation_code()
					+ "";



			SendMail SendMail = new SendMail();
			SendMail.sendMessage("", mailarea, ccEmail, title, textboxdata.replaceAll("<br>", "\n"), attachmentFolderPath);
//			mailarea = "info@pandaservicejapan.com";
//			SendMail.sendMessage("", mailarea, title, textboxdata);
		}

	public static t_etax_account_infoBean exe_activation(String yyyymmdd_count,
			t_etax_account_infoDao t_etax_account_infoDao,
			t_etax_account_resDao t_etax_account_resDao, EtaxDao EtaxDao, String hojinmeiKana) throws SQLException {

		FuncUtils FuncUtils = new FuncUtils();

		t_etax_account_infoBean EtaxAccountInfoBean;
		EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);
		EtaxAccountInfoBean.setCompanyName_English(
				FuncUtils.toFullWidth(EtaxAccountInfoBean.getCompanyName_English()));

		int count = t_etax_account_resDao.selecCount(yyyymmdd_count);
		if (count == 0) {
			t_etax_account_resDao.INSERT(yyyymmdd_count, EtaxAccountInfoBean);
		}

		HashMap<String, String> HashMapKeyValueHtml = new HashMap<String, String>();
		HashMapKeyValueHtml.put("gHojinmei", EtaxAccountInfoBean.getCompanyName_English());

		String str = "";
		if (StringUtils.isEmpty(hojinmeiKana)) {
			str = FuncUtils.fn_hanzi(EtaxAccountInfoBean.getCompanyName_Chinese());
			str = str.replaceAll(" ", "").replaceAll("　", "");
			if (str.length() > 59) {
				str = str.substring(0, 59);
			}

		} else {
			str = hojinmeiKana;

		}

		//法人名称（フリガナ）
		HashMapKeyValueHtml.put("gHojinmeiKana", str);
		HashMapKeyValueHtml.put("gNChiTodohuken", "東京都");
		HashMapKeyValueHtml.put("gNChiAdd1", "文京区千石");
		HashMapKeyValueHtml.put("gNChiAdd2", "４丁目１４番９号１階");
		HashMapKeyValueHtml.put("gTTodohuken", "東京都");
		HashMapKeyValueHtml.put("gTZeimushomei", "小石川");
		str = EtaxAccountInfoBean.getDaibiaoName_Chinese();
		str = str.replaceAll(" ", "").replaceAll("　", "");
		HashMapKeyValueHtml.put("gDSeiKana", FuncUtils.fn_hanzi(str.substring(0, 1)));
		HashMapKeyValueHtml.put("gDmeiKana", FuncUtils.fn_hanzi(str.substring(1)));
		HashMapKeyValueHtml.put("gDSei", str.substring(0, 1));
		HashMapKeyValueHtml.put("gDmei", str.substring(1));
		HashMapKeyValueHtml.put("gDTodohuken", "東京都");
		HashMapKeyValueHtml.put("gDAdd1", "外国");
		HashMapKeyValueHtml.put("gDAdd2", "外国");
		HashMapKeyValueHtml.put("gPwd", EtaxAccountInfoBean.getEtax_pw());
		HashMapKeyValueHtml.put("gPwd2", EtaxAccountInfoBean.getEtax_pw());
		HashMapKeyValueHtml.put("gNKakuninBango", "123456");
		HashMapKeyValueHtml.put("gShoKugyo", "小売業");




		EtaxDao.DELETE(yyyymmdd_count);
		if (EtaxDao.INSERT(HashMapKeyValueHtml, yyyymmdd_count) == true) {
			logger.debug(yyyymmdd_count + " → etax DB import OK");

		} else {
			logger.debug(yyyymmdd_count + " → etax DB import NG");

		}
		return EtaxAccountInfoBean;
	}

	public static String trimWhitespaceAndTabs(String input) {
		if (input == null) {
			return null;
		}

		// 使用正则表达式替换制表符和首尾空格为空字符串
		input = input.replaceAll("\\s+$|^\\s+|\\t+", "");

		// 使用正则表达式替换首尾全角空格为""
		input = input.replaceAll("^[　]+|[　]+$", "");

		// 使用正则表达式替换首尾半角空格为""
		input = input.replaceAll("^\\s+|\\s+$", "");

		// 使用正则表达式替换换行符为空格
		input = input.replaceAll("\\r|\\n", " ");

		return input;
	}

	public static t_etax_account_infoExBean sendGetInvoiceBangou(String date) {

		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();

		URL url;
		try {
			//https://www.invoice-kohyo.nta.go.jp/web-api/web-api-download.files/k-web-api-kinou.pdf
			String url_s = "https://web-api.invoice-kohyo.nta.go.jp/1/num?id=Kp9FL6TRHTUbj&number=" + date
					+ "&type=01&history=0";
			logger.debug(url_s);
			url = new URL(url_s);
			URLConnection conn;
			conn = url.openConnection();
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			int count = 1;

			String line = "";
			while ((line = br.readLine()) != null) {

				logger.debug(line);
				if (count == 1) {
					//						2023-04-24,1,1,1
					//						line1 = line.split(",");

				} else {
					//						2023-04-24,1,1,1
					//						1,"T3700150114682",01,0,2,2,1,2023-10-01,2023-04-21,,,"ｊｕｎｆａｓａｎｌｕ　１２ｈａｏ　２ｈａｏｌｏｕ　３０１ｓｈｉ　ｄｏｎｇｇｕａｎ　ｇｕａｎｇｄｏｎｇｄｏｎｇｋｅｎｇｚｈｅｎ　ｃｎ",,,"",,,"ドングワンジェンイーダーコージーヨウシエンゴンスー","ｄｏｎｇｇｕａｎｚｈｅｎｙｉｄａｋｅｊｉｙｏｕｘｉａｎｇｏｎｇｓｉ","",,,"",""
					if (count == 3) {
						line = "NG";
						break;
					}
					String[] lineList = line.replaceAll("\"", "").split(",");
					t_etax_account_infoExBean.setInvoiceBangou(lineList[1]);

					//人格区分 1個人 2法人
					t_etax_account_infoExBean.setUser_type(lineList[4]);
					if ("2".equals(lineList[4])) {
						t_etax_account_infoExBean.setAddress_English(toHalfWidth(lineList[11]));

					} else {
						t_etax_account_infoExBean.setAddress_English(toHalfWidth(lineList[14]));
					}
					t_etax_account_infoExBean.setCompanyName_pianjiaming(lineList[17]);
					t_etax_account_infoExBean.setCompanyName_English(toHalfWidth(lineList[18]));
				}

				count++;
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return t_etax_account_infoExBean;

	}

	public static t_etax_account_infoExBean sendGetInvoiceBangou_json(String date) {

		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();

		URL url;
		try {
			//https://www.invoice-kohyo.nta.go.jp/web-api/web-api-download.files/k-web-api-kinou.pdf
			String url_s = "https://web-api.invoice-kohyo.nta.go.jp/1/num?id=Kp9FL6TRHTUbj&number=" + date
					+ "&type=21&history=0";
			logger.debug(url_s);
			url = new URL(url_s);
			URLConnection conn;
			conn = url.openConnection();
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            // 使用 StringBuilder 读取内容
            StringBuilder jsonContent = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                jsonContent.append(inputLine);
            }

            // 关闭流
            br.close();

            String jsonString = jsonContent.toString();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonObject = objectMapper.readTree(jsonString);

            if (!"1".equals(jsonObject.get("count").asText())) {
            	t_etax_account_infoExBean.setInvoiceBangou("err");

            } else {
                // 获取第一个对象
                JsonNode firstObject = jsonObject.get("announcement").get(0);

                t_etax_account_infoExBean.setInvoiceBangou(firstObject.get("registratedNumber").asText());
                //人格区分 1個人 2法人
                t_etax_account_infoExBean.setUser_type(firstObject.get("kind").asText());

                if ("1".equals(t_etax_account_infoExBean.getUser_type())) {
                	t_etax_account_infoExBean.setAddress_English(toHalfWidth(firstObject.get("addressRequest").asText()));
                    t_etax_account_infoExBean.setDaibiaoName_English(toHalfWidth(firstObject.get("name").asText()));

                } else {
                	t_etax_account_infoExBean.setAddress_English(toHalfWidth(firstObject.get("address").asText()));
                }

                t_etax_account_infoExBean.setCompanyName_pianjiaming(firstObject.get("kana").asText());
                t_etax_account_infoExBean.setCompanyName_English(toHalfWidth(firstObject.get("name").asText()));

            }


		} catch (Exception e) {
			logger.warn(e.getMessage());
//			e.printStackTrace();

		}

		return t_etax_account_infoExBean;

	}
	public static t_etax_account_infoExBean sendGetHoujinBangou(String date) throws Exception {

		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
		URL url;
		try {

			String url_s = "https://api.houjin-bangou.nta.go.jp/4/num?id=Kp9FL6TRHTUbj&change=1&type=01&number=" + date;
			logger.debug(url_s);
			url = new URL(url_s);
			URLConnection conn;
			conn = url.openConnection();
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "Shift-JIS"));

			int count = 1;

			String line = "";
			while ((line = br.readLine()) != null) {

				logger.debug(line);
				if (count == 1) {
					//2022-10-13,8742,1,18
					//						line1 = line.split(",");

				} else {
					// 2023-04-05,1,1,1
					//						1,4700150114797,01,0,2023-03-13,2023-03-13,"Ｓｈｅｎｚｈｅｎ　ｙｉｔｉｎｇ　Ｔｒａｄｉｎｇ　Ｃｏ．，Ｌｔｄ．",,401,,,,,,,,"Ｒｏｏｍ　６１０，Ａｎｎｅｘ　Ｂｕｉｌｄｉｎｇ，Ｎｉｕｌａｎｑｉａｎ　Ｂｕｉｌｄｉｎｇ，Ｍｉｎｚｈｉ　Ａｖｅｎｕｅ，Ｘｉｎｎｉｕ　Ｃｏｍｍｕｎｉｔｙ，Ｍｉｎｚｈｉ　Ｓｔｒｅｅｔ，Ｌｏｎｇｈｕａ　Ｄｉｓｔｒｉ　Ｓｈｅｎｚｈｅｎ　Ｇｕａｎｇｄｏｎｇ　５１８０００　ＣＮ",,,,,,2023-03-13,1,,,,,"シェンジェンシーイーティンマオイーヨウシエンゴンスー",0
					if (count == 3) {
						line = "NG";
						break;
					}
					String[] lineList = line.replaceAll("\"", "").split(",");
					t_etax_account_infoExBean.setHoujinBangou(lineList[1]);
					t_etax_account_infoExBean.setCompanyName_English(lineList[6]);
					t_etax_account_infoExBean.setAddress_English(lineList[16]);
					t_etax_account_infoExBean.setCompanyName_pianjiaming(lineList[28]);
				}

				count++;
			}
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return t_etax_account_infoExBean;

	}
	public static t_etax_account_infoExBean sendGetHoujinBangouByHoujinName(String date) throws Exception {

		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
		URL url;
		try {

			logger.debug(date);
			 // 对参数进行转义
            String encodedParam = URLEncoder.encode(date, StandardCharsets.UTF_8.toString());

			String url_s = "https://api.houjin-bangou.nta.go.jp/4/name?id=Kp9FL6TRHTUbj&change=1&type=01&name=" + encodedParam;

			logger.debug(url_s);
			url = new URL(url_s);
            // 打开连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 检查响应代码
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
    			InputStream in = conn.getInputStream();
    			BufferedReader br = new BufferedReader(new InputStreamReader(in, "Shift-JIS"));

    			int count = 1;

    			String line = "";
    			while ((line = br.readLine()) != null) {

    				logger.debug(line);
    				if (count == 1) {
    					//2022-10-13,8742,1,18
    					//						line1 = line.split(",");

    				} else {

//    					2024-12-09,4,1,1
//    					1,5010001136366,01,1,2018-08-13,2015-10-05,"ＦＯＲＥＶＥＲ株式会社",,301,"東京都","千代田区","麹町６丁目２番６号",,13,101,1020083,,,,,,,2015-10-05,0,,,,,"フォーエバー",0
//    					2,5010001136366,12,1,2018-08-13,2018-08-06,"ＦＯＲＥＶＥＲ株式会社",,301,"東京都","千代田区","六番町６番４号",,13,101,1020085,,,,,,,2015-10-05,0,,,,,"フォーエバー",0
//    					3,5010001136366,12,0,2019-06-14,2019-06-04,"ＦＯＲＥＶＥＲ株式会社",,301,"東京都","文京区","大塚５丁目６－１５ワイビル２０２室",00116889,13,105,1120012,,,,,,,2015-10-05,0,,,,,"フォーエバー",0
//    					4,9010901045737,01,0,2020-02-19,2020-02-19,"Ｆｏｒｅｖｅｒ株式会社",,301,"東京都","世田谷区","上北沢１丁目３９番６号",,13,112,1560057,,,,,,,2020-02-19,1,,,,,"フォーエバー",0

//    					if (count == 3) {
//    						line = "NG";
//    						break;
//    					}
    					String[] lineList = line.replaceAll("\"", "").split(",");
    					t_etax_account_infoExBean.setHoujinBangou(lineList[1]);
    					t_etax_account_infoExBean.setCompanyName_English(lineList[6]);
    					t_etax_account_infoExBean.setAddress_English(lineList[16]);
    					t_etax_account_infoExBean.setCompanyName_pianjiaming(lineList[28]);
    				}

    				count++;
    			}
    			br.close();

            } else {
    			logger.error("HTTP error code : " + responseCode);

            }



		} catch (Exception e) {
			e.printStackTrace();
		}

		return t_etax_account_infoExBean;

	}

	/**
	 * @param req
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws UnsupportedEncodingException
	 */
	public static String filesUp(HttpServletRequest req, String path)
			throws IOException, ServletException, UnsupportedEncodingException {
		String yyyymmdd_count;
		String hidden_user_type = req.getParameter("hidden_user_type");
		String form_CompanyName_Chinese = req.getParameter("form_CompanyName_Chinese");
		//去掉字符串里的TAB，首尾半角空格，首尾全角空格
		form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);


		String form_DaibiaoName_Chinese = req.getParameter("form_DaibiaoName_Chinese");
		//去掉字符串里的TAB，首尾半角空格，首尾全角空格
		form_DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_DaibiaoName_Chinese);


		if ("个人".equals(hidden_user_type)) {
			form_CompanyName_Chinese = form_DaibiaoName_Chinese.replace(" ", "");
		}

		m_sequenceDao m_sequenceDao = new m_sequenceDao();
		yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();

		path = path + "/" + yyyymmdd_count + "_" + form_CompanyName_Chinese;

		File directory = new File(path);

		//mkdir
		boolean hasSucceeded = directory.mkdir();
		logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);

		req.getParts();
		req.setCharacterEncoding("utf-8");

		// 拡張for文
		for (int j = 0; j < req.getParts().size(); j++) {
			//name属性がfileのファイルをPartオブジェクトとして取得
			Part part = req.getPart("file[" + j + "]");
			//ファイル名を取得
			//String filename=part.getSubmittedFileName();//ie対応が不要な場合
			String filename = yyyymmdd_count + "_";
			filename = filename + Paths.get(part.getSubmittedFileName()).getFileName().toString();

			//書き込み
			part.write(path + File.separator + filename);

			String fe = FilenameUtils.getExtension(filename);

			if ("pdf".equals(fe)) {
				byte[] cert = Files.readAllBytes(Paths.get(path + File.separator + filename));

			}

		}
		return yyyymmdd_count;
	}



	/**
	 * @param req
	 * @param path
	 * @param file_name2
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws UnsupportedEncodingException
	 */
	public static String filesUp_ai(HttpServletRequest req, String path, String yyyymmdd_count, String file_name)
			throws IOException, ServletException, UnsupportedEncodingException {

		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);

		String form_CompanyName_Chinese = t_etax_account_infoExBean.getCompanyName_Chinese();
		String form_DaibiaoName_Chinese = t_etax_account_infoExBean.getDaibiaoName_Chinese();
		String hidden_user_type = t_etax_account_infoExBean.getUser_type();

		//去掉字符串里的TAB，首尾半角空格，首尾全角空格
		form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);
		//去掉字符串里的TAB，首尾半角空格，首尾全角空格
		form_DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_DaibiaoName_Chinese);

		if ("个人".equals(hidden_user_type)) {
			form_CompanyName_Chinese = form_DaibiaoName_Chinese.replace(" ", "");
		}

		if (StringUtils.isEmpty(file_name)) {
			file_name = yyyymmdd_count + "_" + form_CompanyName_Chinese + "_" + yyyymmddhhmmss;
		}
		path = path + "/" + file_name;

		File directory = new File(path);

		//mkdir
		boolean hasSucceeded = directory.mkdir();
		logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);

		req.getParts();
		req.setCharacterEncoding("utf-8");

		// 拡張for文
		for (int j = 0; j < req.getParts().size(); j++) {
			//name属性がfileのファイルをPartオブジェクトとして取得
			Part part = req.getPart("file[" + j + "]");
			if (part == null) {
				continue;
			}
			//ファイル名を取得
			//String filename=part.getSubmittedFileName();//ie対応が不要な場合
			String filename = yyyymmdd_count + "_";
			filename = filename + Paths.get(part.getSubmittedFileName()).getFileName().toString();

			//書き込み
			part.write(path + File.separator + filename);

			String fe = FilenameUtils.getExtension(filename);

			if ("pdf".equals(fe)) {
				byte[] cert = Files.readAllBytes(Paths.get(path + File.separator + filename));

			}

		}
		return file_name;
	}



	/**
	 * @param req
	 * @param path
	 * @param yyyymmddhhmmss
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws UnsupportedEncodingException
	 */
	public static void filesUp_yyyymmddhhmmss(HttpServletRequest req, String path, String yyyymmddhhmmss)
			throws IOException, ServletException, UnsupportedEncodingException {

		logger.debug("filesUp_yyyymmddhhmmss S");

		path = path + "/" + yyyymmddhhmmss;
		File directory = new File(path);
		directory.mkdir();

		req.getParts();
		req.setCharacterEncoding("utf-8");

		// 拡張for文
		for (int j = 0; j < req.getParts().size(); j++) {
			//name属性がfileのファイルをPartオブジェクトとして取得
			Part part = req.getPart("file[" + j + "]");
			//ファイル名を取得
			//String filename=part.getSubmittedFileName();//ie対応が不要な場合

			String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();

			//書き込み
			part.write(path + File.separator + filename);

//			byte[] cert = Files.readAllBytes(Paths.get(path + File.separator + filename));

		}


		logger.debug("filesUp_yyyymmddhhmmss E");
	}

	public static String getFileExtension(String fileName) {
		int lastDotIndex = fileName.lastIndexOf(".");
		if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
			return fileName.substring(lastDotIndex + 1).toLowerCase();
		}
		return "";
	}


	public static String evaluateFormulaCell(Cell cell, Workbook workbook) {
		if (cell != null) {
			if (cell.getCellType() == CellType.FORMULA) {
				// 如果单元格中包含公式，则计算并输出结果
				FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
				CellValue cellValue = formulaEvaluator.evaluate(cell);

				if (cellValue.getCellType() == CellType.NUMERIC) {
					return String.format("%.0f", cellValue.getNumberValue());
				} else if (cellValue.getCellType() == CellType.STRING) {
					return cellValue.getStringValue();
				} else {
					return null;
				}
			} else {
				// 如果是其他类型的单元格，直接输出值
				return cell.toString();
			}

		}

		return null;

	}

    public static String getFormattedCellValue(Cell cell) {
        if (cell != null) {
            DataFormatter dataFormatter = new DataFormatter();
            return dataFormatter.formatCellValue(cell);
        }
        return null;
    }

	public static String readFileContent(File file) throws IOException {
		String charset = FuncUtils.detectCharset(file.getPath());
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file, Charset.forName(charset)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
		}
		return content.toString();
	}


	public static String[] splitCSV(String csvData) {
		List<String> fields = new ArrayList<>();

		boolean insideQuotes = false;
		StringBuilder fieldBuffer = new StringBuilder();

		for (char c : csvData.toCharArray()) {
			if (c == '\"') {
				insideQuotes = !insideQuotes;
			} else if (c == ',' && !insideQuotes) {
				fields.add(fieldBuffer.toString().trim());
				fieldBuffer.setLength(0);
			} else {
				fieldBuffer.append(c);
			}
		}

		fields.add(fieldBuffer.toString().trim());

		// 将List<String> 转换为 String[]
		return fields.toArray(new String[0]);
	}

//    public static String[] splitCSV(String csvData) throws Exception {
//        CSVParser parser = new CSVParserBuilder()
//                .withSeparator(',')  // 指定逗号为分隔符
//                .withQuoteChar('\"') // 处理带引号的字段
//                .build();
//
//        return parser.parseLine(csvData);
//    }


	public static boolean isLastYear(String startDateStr, String endDateStr, String dateTimeString) {
		try {

			dateTimeString = dateTimeString.split(" ")[0].replace("/", "");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sdf.setTimeZone(TimeZone.getTimeZone("JST"));
			int startDateInt = Integer.parseInt(sdf.format(sdf.parse(startDateStr)));
			int endDateInt = Integer.parseInt(sdf.format(sdf.parse(endDateStr)));
			int currentDateInt = Integer.parseInt(sdf.format(sdf.parse(dateTimeString)));

			// 判断开始日期 <= 现在日期 <= 结束日期
			return !(currentDateInt < startDateInt || currentDateInt > endDateInt);

		} catch (ParseException e) {
//			logger.info("解析失败，可能是日期格式不匹配 " + dateTimeString);
			return false; // 解析失败，可能是日期格式不匹配
		}
	}

    public static void copyFile(String sourceFilePath, String destinationFilePath) throws IOException {
        // 创建源文件和目标文件对象
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);

        // 使用文件通道进行文件复制
        try (FileInputStream sourceStream = new FileInputStream(sourceFile);
             FileOutputStream destinationStream = new FileOutputStream(destinationFile);
             FileChannel sourceChannel = sourceStream.getChannel();
             FileChannel destinationChannel = destinationStream.getChannel()) {

            // 将源文件通道内容复制到目标文件通道
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        }
    }

    public static String detectCharset(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            UniversalDetector detector = new UniversalDetector(null);

            byte[] buf = new byte[4096];
            int nread;
            while ((nread = bis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();

            String charset = detector.getDetectedCharset();
            detector.reset();

//            return charset != null ? charset : "Unknown";

            // 默认使用Shift-JIS，如果检测不到字符集则返回Unknown
            return charset != null ? charset : "Shift-JIS";
//            return "ISO-2022-JP";
        }
    }

    //全角英字から半角英字への変換
    public static String toHalfWidth(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(input.length());

        for (char c : input.toCharArray()) {
            if (c >= '！' && c <= '～') {
                // 全角文字の範囲を半角に変換
                sb.append((char) (c - '！' + '!'));
            } else {
                // その他の文字はそのまま追加
                sb.append(c);
            }
        }

        return sb.toString().replaceAll("　", " ");
    }
    //全角英字から半角英字への変換
	public static String toHalfWidthAndTruncate(String input, int size) {
		if (input == null || input.isEmpty() || size <= 0) {
			return ""; // もし空の文字列または無効なサイズが指定された場合、空の文字列を返す
		}

		String result = toHalfWidth(input);
		if (result.length() > size) {
			result = result.substring(0, size);
		}
		return result.toString();
	}

	public static LinkedHashMap<String, Map<String, String>> get_excelDataHashMap(String path, int rowS, int rowE, int columnS, int columnE, int column_excelDataHashMapKey) throws IOException {
		LinkedHashMap<String, String> excelData = new LinkedHashMap<>();
		LinkedHashMap<String, Map<String, String>> excelDataHashMap = new LinkedHashMap<>();
		File datalFile = new File(path);
		if (datalFile.length() == 0) {
			logger.debug(path + " → NG:excel File data model ");
			return null;
		}

		FileInputStream fis = new FileInputStream(datalFile);
		Workbook workbook = WorkbookFactory.create(fis);
		Sheet sheet = workbook.getSheetAt(0);
		if (rowE == -1) {
			rowE = sheet.getLastRowNum() + 1;
		}

		// 遍历每一行
		int count = 0;
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			// 跳过第N行（假设第N行为标题）
			//test
			//				if (194-1 <= row.getRowNum() && row.getRowNum() <= 198-1) {
			//all
			if (rowS - 1 <= row.getRowNum() && row.getRowNum() <= rowE - 1) {
				//143	327		146	163		194		198

			} else {
				continue;

			}

			++count;

			String key = "";
			for (int i = 0; i <= columnE - 1; i++) {
				if (i + 1 < columnS) {
					continue;
				}

				// 获取 N 列的数据
				Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
				String value = "";

//				if (cell != null) {
//					if (cell.getCellType() == CellType.FORMULA) {
//						// 如果单元格中包含公式，则计算并输出结果
//						value = FuncUtils.evaluateFormulaCell(cell, workbook);
//					} else {
//						// 如果是其他类型的单元格，直接输出值
//						value = cell.toString();
//					}
//
//				}
//				value = dataFormatter.formatCellValue(cell);


				if (cell != null) {
					if (cell.getCellType() == CellType.FORMULA) {
						// 如果单元格中包含公式，则计算并输出结果
						value = FuncUtils.evaluateFormulaCell(cell, workbook);
					} else if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						// 如果是日期类型的单元格，格式化日期输出
						Date date = cell.getDateCellValue();
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
						value = dateFormat.format(date);
				    } else if (cell.getCellType() == CellType.NUMERIC) {
				        // 如果是数值类型的单元格，转换为字符串避免科学计数法
				        DecimalFormat df = new DecimalFormat("0");
				        value = df.format(cell.getNumericCellValue());
					} else {
						// 如果是其他类型的单元格，直接输出值
						value = cell.toString();
					}
				}


				if (value.replaceAll(" ", "").toLowerCase().contains(("« NULL »".replaceAll(" ", "").toLowerCase()))
						|| value.toLowerCase().equals("null")) {
					value = "";
				}

				if (column_excelDataHashMapKey == -1) {
					key = String.valueOf(count);

				} else if (i == column_excelDataHashMapKey - 1) {
					key = value;
				}

				excelData.put("" + i, value);

			}
			if (StringUtils.isEmpty(excelData.get("1"))) {
				continue;
			}

			excelDataHashMap.put(key, excelData);
			excelData = new LinkedHashMap<>();

		}
		return excelDataHashMap;
	}

	    public static String joinMapValues(Map<String, String> map) {
	        return map.values().stream().collect(Collectors.joining("	"));
	    }

		public HashMap<String, String> getHashMapWarekiEnglish() {
			return HashMapWarekiEnglish;
		}

		public static void setHashMapWarekiEnglish(HashMap<String, String> hashMapWarekiEnglish) {
			HashMapWarekiEnglish = hashMapWarekiEnglish;
		}

	    public static String generateRandomNumber(int digits) {
	        if (digits <= 0) {
	            throw new IllegalArgumentException("Digits must be greater than 0");
	        }

	        Random random = new Random();
	        StringBuilder sb = new StringBuilder();

	        // 第一个数字确保不为0
	        sb.append(random.nextInt(9) + 1);

	        // 生成剩余位数的数字
	        for (int i = 1; i < digits; i++) {
	            sb.append(random.nextInt(10));
	        }

	        return sb.toString();
	    }

	    public static boolean isNumeric(String str) {
	        if (str == null || str.isEmpty()) {
	            return false;
	        }
	        String regex = "-?\\d+(\\.\\d+)?";
	        return str.matches(regex);
	    }



	    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
	        try (ZipFile zipFile = new ZipFile(new File(zipFilePath), StandardCharsets.UTF_8.name())) {
	            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
	            while (entries.hasMoreElements()) {
	                ZipArchiveEntry entry = entries.nextElement();
	                String filePath = destDirectory + File.separator + entry.getName();
	                if (entry.isDirectory()) {
	                    File dir = new File(filePath);
	                    dir.mkdirs();
	                } else {
	                    extractFile(zipFile.getInputStream(entry), filePath);
	                }
	            }
	        }
	    }

	    private static void extractFile(InputStream inputStream, String filePath) throws IOException {
	        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
	            IOUtils.copy(inputStream, bos);
	        }
	    }

	    public static void unzipAllZipsInFolder(String folderPath, String destDirectory) throws IOException {

			logger.debug("unzipAllZipsInFolder S");

	        File folder = new File(folderPath);
	        if (!folder.exists() || !folder.isDirectory()) {
	            throw new IllegalArgumentException("The folder path is not valid: " + folderPath);
	        }

	        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
	        if (files != null) {
	            for (File file : files) {
	                String zipFilePath = file.getAbsolutePath();





	                try (java.util.zip.ZipFile zip = new java.util.zip.ZipFile(zipFilePath)) {
	                    Enumeration<? extends ZipEntry> entries = zip.entries();

	                    while (entries.hasMoreElements()) {
	                        ZipEntry entry = entries.nextElement();
	                        String name = entry.getName();
	                        int compressionMethod = entry.getMethod(); // 获取压缩方法

	                        String compressionMethodName;
	                        switch (compressionMethod) {
	                            case ZipEntry.STORED:
	                                compressionMethodName = "Stored (no compression)";
	                                break;
	                            case ZipEntry.DEFLATED:
	                                compressionMethodName = "Deflated (compressed)";
	                                break;
	                            default:
	                                compressionMethodName = "Unknown";
	                                break;
	                        }

	                        logger.info("File: " + name + ", Compression Method: " + compressionMethodName);
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }













	                try {
	                    unzip(zipFilePath, destDirectory);
	                } catch (IOException e) {
	                    System.err.println("Error unzipping file: " + zipFilePath);
	                    e.printStackTrace();
	                }
	            }
	        }


			logger.debug("unzipAllZipsInFolder E");
	    }

	    /**
	     * 获取指定属性名称的值。
	     *
	     * @param propertyName 要获取的属性名称
	     * @return 属性值，如果找不到属性则返回 null
	     */
	    public static String getBeanValue(Object obj, String propertyName) {

	    	if (propertyName.contains("INSQ")) {
	    		propertyName= propertyName;
	    	}

	        try {
	            // 构造getter方法名，例如属性名为"keshuibiao_zhun_e" 则方法名为 "getKeshuibiao_zhun_e"
	            String methodName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
	            Method method = obj.getClass().getMethod(methodName);
	            String value = (String) method.invoke(obj);
	            if ("CompanyName_English".equals(propertyName)
	            		|| "address_English".equals(propertyName)) {
	            	value = value.replace("&", "&amp;");
	            }
	            return value; // 调用getter方法获取属性值
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    /**
	     * 获取指定属性名称的值。
	     *
	     * @param propertyName 要获取的属性名称
	     * @return 属性值，如果找不到属性则返回 null
	     */
	    public static String getBeanValue_AI1(TableDefinition obj, String propertyName) {

	    	if (propertyName.contains("INSQ")) {
	    		propertyName= propertyName;
	    	}

	        try {
	            // 构造getter方法名，例如属性名为"keshuibiao_zhun_e" 则方法名为 "getKeshuibiao_zhun_e"
	            String methodName = "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
	            Method method = obj.getClass().getMethod(methodName);
	            String value = (String) method.invoke(obj);
	            if ("CompanyName_English".equals(propertyName)
	            		|| "address_English".equals(propertyName)) {
	            	value = value.replace("&", "&amp;");
	            }
	            return value; // 调用getter方法获取属性值
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public static String getBeanValue_AI(TableDefinition tableDefinition, String propertyName) {
	        if (tableDefinition == null || tableDefinition.columns == null || propertyName == null) {
	            return "";
	        }

	        for (ColumnDefinition column : tableDefinition.columns) {
	            if (column.comment.contains(propertyName)) {
	                return column.value;
	            }
	        }

	        // 如果没有找到对应的列，返回 null
	        return "";
	    }

	    public static void deleteFilesInFolder(File folder, String fileExtension) {
	        if (folder.exists() && folder.isDirectory()) {
	            for (File file : folder.listFiles()) {
	                if (file.isDirectory()) {
	                    deleteFilesInFolder(file, fileExtension); // 递归处理子文件夹
	                } else if (file.isFile() && file.getName().endsWith(fileExtension)) {
	                    file.delete(); // 删除指定扩展名的文件
	                    logger.info("Deleted: " + file.getAbsolutePath());
	                }
	            }
	        }
	    }



	    /**
	     * 处理金额字符串，去掉逗号和日元符号
	     */
	    public static String cleanCurrency(String currency) {
	        return currency.replaceAll("[,円\\s]", ""); // 去掉逗号、"円" 和空格
	    }



	    // 日本年号对照表
	    private static final Map<String, Integer> ERA_START_YEAR = new HashMap<>();

	    static {
	        ERA_START_YEAR.put("明治", 1867);  // 明治元年 = 1868年
	        ERA_START_YEAR.put("大正", 1911);  // 大正元年 = 1912年
	        ERA_START_YEAR.put("昭和", 1925);  // 昭和元年 = 1926年
	        ERA_START_YEAR.put("平成", 1988);  // 平成元年 = 1989年
	        ERA_START_YEAR.put("令和", 2018);  // 令和元年 = 2019年
	    }

	    /**
	     * 将"日语年号"转换为"yyyy-MM-dd"国际标准格式
	     */
	    public static String convertJapaneseEraDate(String japaneseDate) {

	    	japaneseDate = japaneseDate.replace("自", "").replace(" ", "");

	        // 匹配格式：年号 + 数字 + 年 + 数字 + 月 + 数字 + 日
	        Pattern pattern = Pattern.compile("(明治|大正|昭和|平成|令和)(\\d{1,2})年(\\d{1,2})月(\\d{1,2})日");
	        Matcher matcher = pattern.matcher(japaneseDate);

	        if (matcher.find()) {
	            String era = matcher.group(1);  // 获取年号
	            int eraYear = Integer.parseInt(matcher.group(2)); // 该年号下的年份
	            int month = Integer.parseInt(matcher.group(3)); // 月份
	            int day = Integer.parseInt(matcher.group(4)); // 日期

	            if (ERA_START_YEAR.containsKey(era)) {
	                int year = ERA_START_YEAR.get(era) + eraYear; // 计算公历年份
	                LocalDate date = LocalDate.of(year, month, day);
	                return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	            }
	        }
	        return "";
	    }




		public static TableDefinition buildTableDefinitionByName(
		        String targetTableName,
		        LinkedHashMap<String, LinkedHashMap<String, String>> aiTables,
		        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns, TableDefinition def) {

		    // 從 aiTables 中獲取目標表
		    LinkedHashMap<String, String> tableInfo = aiTables.get(targetTableName);
		    if (tableInfo == null) {
		        return null; // 沒有這個表
		    }

		    TableDefinition tableDef = new TableDefinition();
		    tableDef.tableName = targetTableName.replaceFirst("^AI_T_", "");
		    tableDef.tableName_comment = tableInfo.getOrDefault("comment", "");
		    tableDef.columns = new ArrayList<>();



		    // 查該表的字段
		    LinkedHashMap<String, LinkedHashMap<String, String>> cols = tableColumns.get(targetTableName);
		    if (cols != null) {
		        for (LinkedHashMap<String, String> colInfo : cols.values()) {
		            String colName = colInfo.getOrDefault("name", "");
		            String colType = colInfo.getOrDefault("type", "");
		            boolean nullable = "YES".equalsIgnoreCase(colInfo.getOrDefault("nullable", "NO"));
		            boolean primaryKey = false; // TODO: 之後可以補充查主鍵
		            String comment = colInfo.getOrDefault("comment", "");

		            // 查找 def.columns 是否已有此列的 value
		            String value = null;
		            if (def != null && def.columns != null) {
		                for (ColumnDefinition oldCol : def.columns) {
		                    if (colName.equals(oldCol.name)) {
		                        value = oldCol.value;
		                        break;
		                    }
		                }
		            }

		            ColumnDefinition colDef = new ColumnDefinition(
		                    colName,
		                    colType,
		                    nullable,
		                    primaryKey,
		                    comment,
		                    value // 这里把 def.columns 的 value 传进来
		            );

		            tableDef.columns.add(colDef);
		        }
		    }

		    return tableDef;
		}

		public static File getLatestFile(String folderPath, String extension) {
			// 如果 extension 不是点开头，就补上
			if (!extension.startsWith(".")) {
				extension = "." + extension;
			}

			final String finalExt = extension.toLowerCase();

			File folder = new File(folderPath);
			if (!folder.exists() || !folder.isDirectory()) {
				return null;
			}

			// 只筛选指定扩展名文件
			File[] files = folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(finalExt);
				}
			});

			if (files == null || files.length == 0) {
				return null;
			}

			Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
			return files[0];
		}


	    // ===== 使用说明 =====
	    private static void printUsage() {
	        System.out.println("用法:");
	        System.out.println("  sendMail <to> <cc> <title> <content>");
	        System.out.println("  sendMailIncident <to> <cc> <title> <content>");
	    }

}