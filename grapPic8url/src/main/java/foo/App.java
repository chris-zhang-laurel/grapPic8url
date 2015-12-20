package foo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 * Hello world!
 * 
 */
public class App {

	public static String sendPost(String url, String params) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");

			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			out.print(params);
			out.flush();

			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
			}
		} catch (Exception e) {
			System.out.println("Post error:" + e);
			e.printStackTrace();
		}
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static void main(String[] args) throws IOException,
			InterruptedException, JSONException {

		Properties prop = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(
					args[0]));
			prop.load(in);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.setProperty("phantomjs.binary.path",
				prop.getProperty("PHANTOMJS"));
		String urls = prop.getProperty("URLS");
		System.out.println(urls);
		String[] urlArray = {urls};
		if(urls.contains("\\|"))
			urlArray = urls.split("\\|");

		WebDriver driver = new PhantomJSDriver();
		String name1 = UUID.randomUUID().toString();
		String name2 = UUID.randomUUID().toString();
		System.out.println(name1);
		System.out.println(name2);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		driver.manage().window().setSize(new Dimension(800, 1000));

		for (String pageUrl : urlArray) {
//			driver.get(pageUrl);
			driver.get("http://www.baidu.com");
			Thread.sleep(10000);
			java.io.File screenShotFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenShotFile, new java.io.File("./" + name1
					+ ".png"));
			

			BufferedImage bufferedImage = ImageIO.read(new File("./" + name1
					+ ".png"));

			// create a blank, RGB, same width and height, and a white
			// background
			BufferedImage newBufferedImage = new BufferedImage(
					bufferedImage.getWidth(), bufferedImage.getHeight(),
					BufferedImage.TYPE_INT_RGB);
			newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0,
					Color.WHITE, null);

			// write to jpeg file
			ImageIO.write(newBufferedImage, "jpg", new File("./" + name2
					+ ".jpg"));
			
		}
		File a = new File("./" + name1 + ".png");
//		a.delete();
		a = new File("./" + name2 + ".jpg");
//		a.delete();
		driver.quit();
		System.out.println("done");

	}
}
