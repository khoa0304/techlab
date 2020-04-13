package lab.parsing;

import java.io.IOException;

import org.apache.http.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class TestParsing {

	@Test
	public void testParsingHTML1() throws IOException {

		String url = "https://towardsdatascience.com/top-10-great-sites-with-free-data-sets-581ac8f6334";

		Document doc = Jsoup.connect(url).get();
		String title = doc.title();
		String body = doc.body().text();

		System.out.printf("Title: %s%n", title);
		System.out.printf("Body: %s", body);
	}

//	@Test
//	public void testParsingJavascript() throws ParseException, IOException {
//
//		String url = "https://onix-systems.com/blog/top-10-java-machine-learning-tools-and-libraries";
//		
//		//String url = "https://towardsdatascience.com/teslas-deep-learning-at-scale-7eed85b235d3";
//
//		DesiredCapabilities caps = new DesiredCapabilities();
//		caps.setJavascriptEnabled(true);                
//		//caps.setCapability("takesScreenshot", true);  
//		
//		//System.setProperty("phantomjs.binary.path", "C:\\mvnrepos2\\com\\codeborne\\phantomjsdriver\\1.4.1\\phantomjsdriver-1.4.1.jar");
//	      
//		WebDriver driver = new PhantomJSDriver(caps);
//        // And now use this to visit Google
//        driver.get("http://www.google.com");
//        // Alternatively the same thing can be done like this
//        // driver.navigate().to("http://www.google.com");
//
//        // Find the text input element by its name
//        WebElement element = driver.findElement(By.name("q"));
//
//        // Enter something to search for
//        element.sendKeys("Cheese!");
//
//        // Now submit the form. WebDriver will find the form for us from the element
//        element.submit();
//
//        // Check the title of the page
//        System.out.println("Page title is: " + driver.getTitle());
//
//        // Google's search is rendered dynamically with JavaScript.
//        // Wait for the page to load, timeout after 10 seconds
//        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
//            public Boolean apply(WebDriver d) {
//                return d.getTitle().toLowerCase().startsWith("cheese!");
//            }
//
//			
//		});
//
//        // Should see: "cheese! - Google Search"
//        System.out.println("Page title is: " + driver.getTitle());
//
//        //Close the browser
//        driver.quit();
//	}
}
