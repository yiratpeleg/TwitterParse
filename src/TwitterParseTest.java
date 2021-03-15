import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashSet;

public class TwitterParseTest {

    static WebDriver driver;

    @BeforeClass
    public static void Before(){
        System.setProperty("webdriver.chrome.driver" , "C:/Users/יראת פלג/Downloads/chromedriver.exe");
        driver = new ChromeDriver();
    }

    @Test
    public void loginToTwitter() throws InterruptedException {
        TwitterParse.loginToTwitter(driver, "Yirat6", "yirat315");
        String homeUrl = driver.getCurrentUrl();
        Assert.assertEquals(homeUrl, "https://twitter.com/home");
    }

    @Test
    public void searchTwitter() throws InterruptedException {
        driver.get("https://twitter.com/YouTube");
        TwitterParse.searchTwitter(driver, "@amit_segal");
        String homeUrl = driver.getCurrentUrl();
        Assert.assertEquals(homeUrl, "https://twitter.com/amit_segal");
    }

    @Test
    public void takeTopTweets() throws InterruptedException {
        driver.get("https://twitter.com/Yirat6");
        TwitterParse.takeTopTweets(driver, 2);

        HashSet<String> hashtagList = new HashSet<>();
        hashtagList.add("#secondTweet");
        hashtagList.add("#oneTweet");

        HashSet<String> mentionList = new HashSet<>();
        mentionList.add("@YouTube");

        Assert.assertEquals(TwitterParse.hashtagList, hashtagList);
        Assert.assertEquals(TwitterParse.mentionList, mentionList);

        TwitterParse.hashtagList.clear();
        TwitterParse.mentionList.clear();
    }

    @Test
    public void calculateMinMaxLength() {
        TwitterParse.calculateMinMaxLength(10);
        TwitterParse.calculateMinMaxLength(20);
        TwitterParse.calculateMinMaxLength(30);

        Assert.assertEquals(TwitterParse.largestTweetLength, 30);
        Assert.assertEquals(TwitterParse.smallestTweetLength, 10);
    }

    @Test
    public void filterHashtags() {
        //Prefix of '#' is a hashtag
        String twoHashtags = "This #case should identify #two hashtags";
        HashSet<String> twoHashtagsList = new HashSet<>();
        twoHashtagsList.add("#case");
        twoHashtagsList.add("#two");

        //Suffix '#' is not a hashtag
        String noTwoHashtags = "This case# should not identify two# hashtags";

        TwitterParse.filterHashtags(twoHashtags);
        Assert.assertEquals(TwitterParse.hashtagList, twoHashtagsList);
        TwitterParse.hashtagList.clear();

        TwitterParse.filterHashtags(noTwoHashtags);
        Assert.assertNotEquals(TwitterParse.hashtagList, twoHashtagsList);
    }

    @Test
    public void filterMentions() {
        //Prefix of '@' is a mention
        String twoMentions = "This @case should identify @two mentions";
        HashSet<String> twoMentionsList = new HashSet<>();
        twoMentionsList.add("@case");
        twoMentionsList.add("@two");

        //Suffix '@' is not a mention
        String noTwoMentions = "This case@ should not identify two@ mentions";

        TwitterParse.filterMentions(twoMentions);
        Assert.assertEquals(TwitterParse.mentionList, twoMentionsList);
        TwitterParse.mentionList.clear();

        TwitterParse.filterMentions(noTwoMentions);
        Assert.assertNotEquals(TwitterParse.mentionList, twoMentionsList);
    }

}