<img width="465"  src="https://user-images.githubusercontent.com/57325378/111080506-7179b900-8507-11eb-813c-39d8c3813b75.png">

## Project description:

This project automatically retrieves information from Twitter tweets using Selenium WebDriver.

You can decide what the username and password of the account to which the program will log in,
Decide which page to access and how many tweets to parse.
The program will return the following information to the console:
1. Hashtag list
2. Mention list
3. Word statistics (by the length of the content of tweets): 
   * The largest tweet length
   * The smallest tweet length
   * The average of tweet length

<img width="800"  src="https://user-images.githubusercontent.com/57325378/111115500-5cd00c00-856d-11eb-8c14-ea608086210e.gif">


## Running project:

1. Have java environment setup
2. Clone the repository and go to the cloned directory
3. Install selenium WebDriver and import the libraries to your project
4. Edit the following variables in the [main]((https://github.com/yiratpeleg/TwitterParse/blob/main/src/TwitterParse.java)) function:
   * path - The path on your computer where the chrome driver executable file is located (ChromeDriver 88.0.4324.96)
   * username - Your username to twitter
   * password - Your password to twitter
   * handleTwitter - The handle twitter you want to search
   * tweetNumToTake - The amount of top tweets to retrieve
5. Run the main
