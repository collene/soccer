# Soccer Tournament

Soccer Tournament is a Java command line application using Spring Boot, Spring Shell, and Spring Data.

## Description

Demonstration project to model a soccer tournament with teams, players, coaches, games, and report for the tournament.  

The project fills the following functional requirements:
* Ability to create a tournament
* Ability to add a team to the tournament
* Ability to add a coach to a team
* Ability to add a player to a team
* Ability to create a game between 2 teams
* Ability to enter the result of a game
* Ability to report results from a tournament

The project also fills the following non-functional requirements:
* Written in Java with Maven
* Command line application (no GUI required)
* Persist any information using any method
* Provide unit tests using JUnit

The project has a command line interpreter (CLI) as an interface following a REPL design (Read, Eval, Print Loop).  While the interpreter loop is running, an in-memory database will persist the data entered by the user.

## Getting Started

### Dependencies

* [JDK 1.8 or later](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Maven 3.2+](https://maven.apache.org/download.cgi)

### Installing

Clone the repo:
```
git clone https://github.com/collene/soccer.git
```

### Executing program
```
mvn spring-boot:run -DskipTests
```
The first time the application executes, it will download any dependencies using Maven, and so may take some time to initialize.  Subsequent executions should be much faster.

### Using the CLI

When the application first starts, you'll see the CLI with a prompt:
```
soccer:>
```
Type help to see a list of the commands that are available:
```
soccer:>help
```
To see details about a specific command, use help and the name of the command:
```
soccer:>help create-tournament
```
Once you have finished using the application, type exit to return back to the command line:
```
soccer:>exit
```
Note that the command line interpreter expects parameters to be separated by spaces.  If one of your parameters contains spaces (such as a name), you'll need to use single or double quotes around the parameter.  For example:
```
soccer:>create-tournament 'My Tournament'
```
You can also use the tab key to auto complete commands and the up/down keys to load a command from your history.

See more about Spring Shell [here](https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/), especially when [dealing with spaces](https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/#quotes-handling)
#### Full List of Commands
Built-In Commands:
* clear: Clear the shell screen.
* exit, quit: Exit the shell.
* help: Display help about available commands.
* history: Display or save the history of previously run commands
* script: Read and execute commands from a file.
* stacktrace: Display the full stacktrace of the last error.

Person Commands:
* create-person: Create new person with specified name.

Team Commands:
* add-coach-to-team: Add coach to team.
* add-player-to-team: Add player to team.
* create-team: Create new team with specified name.

Tournament Commands:
* add-game-to-tournament: Add game between two teams to tournament.
* add-team-to-tournament: Add team to tournament.
* create-tournament: Create new tournament with specified name.
* report-tournament-results: Report game results for tournament.
* score-game-in-tournament: Score game between two teams in tournament.

### Executing tests
```
mvn test
```

## Authors

Collene Hansen 

Visit my blog: [https://uncommentedout.com](https://uncommentedout.com)

## Approach
As much as possible, I tried to stick to the MVP principle (minimal viable product) and have a working (but not fully functional) application at the end of each iteration.  As such, I made several simplifying assumptions but tried to keep the design flexible enough to allow for extension in the future.  In particular, the entities are very "bare bones".  A realistic application would have a more complete model, depending on requirements.  I created the entities with enough information to complete basic requirements, but no more.

I also tried to keep the user interface simple for the user, considering they have to type each of the commands.  In some situations, I allowed the user to bypass steps.  For example, a user just needs to create a game between two teams, and this will create the teams and add them automatically to the tournament if they aren't already added.  If a step can not be missed, an error message will indicate to the user what step needs to be completed first.

## Assumptions
* Multiple tournaments will be possible.  The interface should allow you to create new tournaments or add items to existing ones.
* Teams exist outside of tournaments, and it is possible that teams will participate in multiple tournaments.
* Coaches and players exist outside of tournaments, and it is possible that they will belong to multiple teams in multiple roles.
* While there are different types of tournaments, I assumed that the application would only score a round-robin tournament, where multiple games are played and the winner is determined by a win/loss/tie calculation.
* Sports tournaments have restrictions in place to make sure there is fair game play (such as one player cannot play on more than one team in a tournament).  I assumed that external judges would keep track of such restrictions and the task for this application is simply to calculate scores and the winner.
* The full player list for a team is also the "roster" for a game in the tournament.
* Lazy loading of entities is acceptable for performance for this application.  There won't be a large number of tournaments or teams.
* I assumed that the points awarded for a Win is 3, a Tie is 2, and a Loss is 1.

## Known Issues
* Names are case-sensitive, so names with the same spelling but different capitalization will be considered as two separate items.  For example:  'Jane doe' and 'Jane Doe' represent two different people, and 'Ladybugs' and 'LADYBUGS' represent two different teams.

## Future Work
* Allow the user to specify a default tournament so they don't have to specify the tournament as a parameter every time.  The CLI prompt should indicate that a default tournament has been selected (for example the prompt will change to soccer:>tournamentName:>
* Before allowing a team to be added to a game or a tournament, make sure that it has at least 5 players and 1 coach.
* Extend the scoring to allow for knockout tournaments (with quarter finals, semi finals, etc).
* Allow full CRUD capabilities for entities in the interface.
* Switch to a visual interface instead of the CLI for more flexibility.
* Add checkstyle and code coverage plugins to Maven build.
* Add integration test that loads multiple commands from a file and makes sure the calculations work over many commands.
* Create configuration for multiple environments (dev, test, prod).
* Switch to a different database provider instead of using the in-memory database.
* Extend to create REST web services to have multiple consumers of the services.
* Create a "list" command to show the games, teams, players, etc. in a tournament.  
* Fix the countable nouns (for example 1 point vs 1 point(s)).
* Allow a user to auto generate a game list for a tournament from the current list of teams in the tournament.
* Make the tally configurable by allowing different values for the Win, Loss, Tie calculation.
* ...

## Acknowledgments

Inspiration, code snippets, etc:

* [Spring Shell 2.0](https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/)
* [Writing Assertions with Hamcrest](https://www.petrikainulainen.net/programming/testing/junit-5-tutorial-writing-assertions-with-hamcrest/)
* [Make a readme](https://www.makeareadme.com/)
* [Readme template](https://gist.github.com/DomPizzie/7a5ff55ffa9081f2de27c315f5018afc)
* [Spring Shell TableBuilder examples](https://www.programcreek.com/java-api-examples/?api=org.springframework.shell.table.TableBuilder)
* [Sorting Streams in Java](https://howtodoinjava.com/java8/stream-sorting/)
* [Java Stream Filter with Lambda](https://www.baeldung.com/java-stream-filter-lambda)
* [Persisting Maps with Hibernates](https://www.baeldung.com/hibernate-persisting-map)
* [The best way to map a One to Many relationship with JPA and Hibernate](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/)
* [Spring Initializr](https://start.spring.io/)
* [Markdown-Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)