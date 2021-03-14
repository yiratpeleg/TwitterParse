import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *The purpose of this class is to analyze the following:
 * Login to a specific Twitter account.
 * Login to a particular user's Twitter page.
 * Retrieving a certain amount of recent tweets.
 * Bringing conclusions about the tweets extracted:
 * 1. A list of all the hashtags.
 * 2. List of all mentions.
 * 3. The largest tweet length.
 * 4. The smallest tweet length.
 * 5. Average length of tweet.
 */

public class TwitterParse {

    //Storage list of all hashtags
    private static HashSet<String> hashtagList = new HashSet();
    //Storage list of all mentions
    private static HashSet<String> mentionList = new HashSet();
    //Largest tweet length
    private static int largestTweetLength = 0;
    //Smallest tweet length
    private static int smallestTweetLength = Integer.MAX_VALUE;
    //Average tweet length
    private static int averageTweetLength = 0;

    private static final int MAXIMUM_TWEET_PIXELS = 200;

    /**
     * This function log in to a specific Twitter account
     * @param driver The initialized driver with which to open the browser
     * @param username The username of the account to which you want to log in
     * @param password The password of the account to which you want to log in
     * @throws InterruptedException For the thread
     */
    public static void loginToTwitter(WebDriver driver , String username , String password) throws InterruptedException {

        //Open the login twitter page
        driver.get("https://twitter.com/login");

        Thread.sleep(1000);

        //Get the username and password elements.
        WebElement userName = driver.findElement(By.name("session[username_or_email]"));
        WebElement userPassword = driver.findElement(By.name("session[password]"));
        Thread.sleep(1000);

        //Enter the username and password
        userName.sendKeys(username);
        userPassword.sendKeys(password);
        Thread.sleep(1000);

        //Click the login button
        driver.findElement(By.xpath("//*[@id=\"react-root\"]/div/div/div[2]/main/div/div/div[2]/form/div/div[3]")).click();
        Thread.sleep(1000);
    }

    /**
     * This function searches for and enters the page of a specific user
     * @param driver The initialized driver with which to open the browser
     * @param handleTwitter The handle twitter you want to search (must be handle for a unique result)
     * @throws InterruptedException For the thread
     */
    public static void searchTwitter(WebDriver driver , String handleTwitter) throws InterruptedException {

        //Get the search box element and enter the handle twitter in it
        WebElement searchBox = driver.findElement(By.xpath("//div/input[@data-testid='SearchBox_Search_Input']"));
        searchBox.clear();
        searchBox.sendKeys(handleTwitter);
        Thread.sleep(6000);
        searchBox.submit();
        Thread.sleep(2000);

        //Enter the page of user with the handle twitter
        driver.findElement(By.xpath("//div[@data-testid='UserCell']")).click();
    }

    /**
     * This function takes the top tweets and filters features from them into the global variables
     * @param driver The initialized driver with which to open the browser
     * @param tweetsNumToTake The amount of top tweets to take
     * @throws InterruptedException For the thread
     */
    public static void takeTopTweets(WebDriver driver , int tweetsNumToTake) throws InterruptedException {

        //Storage list of top tweets
        LinkedHashSet<WebElement> tweets = new LinkedHashSet<>();
        //The last size of tweets list
        int previousSize = 0;
        //Java script executor for the page scroll
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //The amount of pixels to scroll down the page
        int pixelToScroll = MAXIMUM_TWEET_PIXELS;

        //The amount of pixels of the page to check if we have reached the bottom of the page
        long lastHeight=((Number)js.executeScript("return document.body.scrollHeight")).longValue();
        long newHeight;

        //Make a move over all the top tweets
        while (tweets.size() < tweetsNumToTake) {

            //Running a script for scrolling the page
            js.executeScript("window.scrollTo(0,"+pixelToScroll+")" );
            pixelToScroll += 2*MAXIMUM_TWEET_PIXELS;
            Thread.sleep(1000);

            //Add to the tweet list all the tweets that appear on the page
            tweets.addAll(driver.findElements(By.xpath("//div[@data-testid='tweet']")));

            int index = 0;
            for (WebElement tweet : tweets) {
                //Filter hashtags and mentions just from the new tweets we have inserted to the list
                if(index >= previousSize  &&  index < tweetsNumToTake){

                    String tweetContent = tweet.findElement(By.xpath(".//div[2]/div[2]/div[1]")).getText();
                    filterHashtags(tweetContent);
                    filterMentions(tweetContent);
                    calculateMinMaxLength(tweetContent.length());
                }
                index++;
            }
            previousSize = tweets.size();

            //Calculate new scroll height and compare with last scroll height.
//            newHeight = ((Number)js.executeScript("return document.body.scrollHeight")).longValue();
//            if (newHeight == lastHeight) break;
//            lastHeight = newHeight;
        }

        //Calculate the average tweet length
        averageTweetLength = averageTweetLength/tweetsNumToTake;
        Thread.sleep(1000);
    }

    /**
     * This function calculate the current largest tweet length and smallest tweet length
     * @param tweetLength The length of the tweet (amount of characters)
     */
    public static void calculateMinMaxLength(int tweetLength){
        largestTweetLength = Math.max(largestTweetLength , tweetLength);
        smallestTweetLength = Math.min(smallestTweetLength , tweetLength);
        averageTweetLength += tweetLength;
    }

    /**
     * This function filter the hashtags from the content of the tweet and add them to the hashtag list.
     * @param tweetContent The content of the tweet.
     */
    public static void filterHashtags(String tweetContent){

        //Filter all words which start with '#'
        Pattern hashtagPattern = Pattern.compile("#\\w+");
        Matcher hashtagMatcher = hashtagPattern.matcher(tweetContent);

        while (hashtagMatcher.find())
        {
            hashtagList.add(hashtagMatcher.group());
        }
    }

    /**
     * This function filter the mentions from the content of the tweet and add them to the mention list.
     * @param tweetContent
     */
    public static void filterMentions(String tweetContent){

        //Filter all words which start with '@'
        Pattern mentionPattern = Pattern.compile("@\\w+");
        Matcher mentionMatcher = mentionPattern.matcher(tweetContent);

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

        takeTopTweets(driver , 100);

        System.out.println("hashtag List: " + hashtagList);
        System.out.println("mention List: " + mentionList);
        System.out.println("The largest Tweet Length is: " + largestTweetLength);
        System.out.println("The smallest Tweet Length is: " + smallestTweetLength);
        System.out.println("The average of Tweets Length is: " + averageTweetLength);

        Thread.sleep(5000);

        driver.quit();
    }

}
