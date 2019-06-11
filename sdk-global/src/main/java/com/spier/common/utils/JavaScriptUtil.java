package com.spier.common.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * JavaScript工具类
 * @author GHB
 * @version 1.0
 * @date 2019.1.28
 */
public class JavaScriptUtil {

	/**
	 * 本地执行Js脚本，返回结果
	 * @param htmlFilePath 文件路径
	 * @param js js代码段
	 * @return 可能为null
	 */
	public static String executeSpJsObject(String emptyPageUrl, String js) {
		String historyUrl = null;
		  WebClient webClient = new WebClient();
		  webClient.getOptions().setJavaScriptEnabled(true);
		  try {
			HtmlPage page = webClient.getPage(emptyPageUrl);// 这里链接需要换成本地一个html，省资源，能不能省去这一步，目前还不太清楚
			page.executeJavaScript("javascript:" + js);
			History history = webClient.getCurrentWindow().getHistory();
			for(int index = 0; index < history.getLength(); index++){
				URL urlItem = history.getUrl(index);
				historyUrl = urlItem.toString();
				//System.out.println("path:" + historyUrl);
			}
			webClient.close();
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
		return historyUrl;
	}
}
