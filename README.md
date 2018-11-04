# About
Create a series of automated tests that cover the CRUD
operations plus some edge cases based on Github Gists using the github API.
## Tech Stack

* Rest Assured
* Json Schema Validator
* TestNG
* slf4j-simple

# How to run
    $ git clone https://github.com/nicolaedanu/gist-rest.git
    $ cd gist-rest
    $ mvn clean test -Dtoken=useTestPayconiqOauth2TestTokens
    # To open report go to test-reports/test-report.html or
    $ open test-reports/test-report.html

## Notes:
* More details on Github API can be found [here](https://developer.github.com/v3/gists/)
* How to create a Github Oauth2 token information can be found [here](https://blog.github.com/2013-05-16-personal-api-tokens/)