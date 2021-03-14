import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterParse {

    private static HashSet<String> hashtagList = new HashSet();
    private static HashSet<String> mentionList = new HashSet();
    private static int largestTweetLength = 0;
    private static int smallestTweetLength = Integer.MAX_VALUE;
    private static int averageTweetLength = 0;

    public static void loginToTwitter(WebDriver driver , String username , String password) throws InterruptedException {

        driver.get("https://twitter.com/login");

        Thread.sleep(1000);

        WebElement userName = driver.findElement(By.name("session[username_or_email]"));
        WebElement userPassword = driver.findElement(By.name("session[password]"));

        Thread.sleep(1000);
        userName.sendKeys(username);
        Thread.sleep(1000);
        userPassword.sendKeys(password);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[3]")).click();
        Thread.sleep(1000);
    }

    public static void searchTwitter(WebDriver driver , String twitterName) throws InterruptedException {
        WebElement searchBox = driver.findElement(By.xpath("//div/input[@data-testid='SearchBox_Search_Input']"));
        searchBox.clear();
        searchBox.sendKeys(twitterName);
        Thread.sleep(6000);
        searchBox.submit();
        Thread.sleep(2000);

        driver.findElement(By.xpath("//div[@data-testid='UserCell']")).click();
    }

    public static void takeLastTweets(WebDriver driver , int tweetsNumToTake) throws InterruptedException {

        LinkedHashSet<WebElement> tweets = new LinkedHashSet<>();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        int pixelToScroll = 200;
        int previousSize = 0;

        long lastHeight=((Number)js.executeScript("return document.body.scrollHeight")).longValue();
        long newHeight;

//        String datePinnedTweet = driver.findElement(By.xpath(".//time")).getAttribute("datetime");
//        System.out.println(datePinnedTweet);

        while (tweets.size() < tweetsNumToTake) { //או שהגענו הכי למטה

            js.executeScript("window.scrollTo(0,"+pixelToScroll+")" );
            pixelToScroll += 400;
            Thread.sleep(1000);
            tweets.addAll(driver.findElements(By.xpath("//div[@data-testid='tweet']")));

            int index = 0;
            for (WebElement tweet : tweets) {
                if(index >= previousSize  &&  index < tweetsNumToTake){ //כי יכול להיות שנכניס יותר מ100
                    filterHashtagAndMention(tweet);
                }
                index++;
            }
            previousSize = tweets.size();

            //Calculate new scroll height and compare with last scroll height.
            newHeight = ((Number)js.executeScript("return document.body.scrollHeight")).longValue();
            if (newHeight == lastHeight) break;
            lastHeight = newHeight;
        }

        averageTweetLength = averageTweetLength/100;
        Thread.sleep(1000);
        System.out.println("tweets size: " + tweets.size());
    }

    public static void filterHashtagAndMention(WebElement tweet){

        //content of tweet
        String content = tweet.findElement(By.xpath(".//div[2]/div[2]/div[1]")).getText();

        int tweetLength = content.length();
        largestTweetLength = Math.max(largestTweetLength , tweetLength);
        smallestTweetLength = Math.min(smallestTweetLength , tweetLength);
        averageTweetLength += tweetLength;

        Pattern hashtagPattern = Pattern.compile("#\\w+");
        Matcher hashtagMatcher = hashtagPattern.matcher(content);

        while (hashtagMatcher.find())
        {
            hashtagList.add(hashtagMatcher.group());
        }

        Pattern mentionPattern = Pattern.compile("@\\w+");
        Matcher mentionMatcher = mentionPattern.matcher(content);

        while (mentionMatcher.find())
        {
            mentionList.add(mentionMatcher.group());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver" , "C:/Users/יראת פלג/Downloads/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        loginToTwitter(driver , "Yirat6" , "yirat315");

        Thread.sleep(1000);

        searchTwitter(driver , "@amit_segal");

        Thread.sleep(2000);

        takeLastTweets(driver , 10);

        System.out.println(hashtagList);
        System.out.println(mentionList);
        System.out.println("The largest Tweet Length is: " + largestTweetLength);
        System.out.println("The smallest Tweet Length is: " + smallestTweetLength);
        System.out.println("The average of Tweets Length is: " + averageTweetLength);

        Thread.sleep(5000);

        driver.quit();
    }

}
